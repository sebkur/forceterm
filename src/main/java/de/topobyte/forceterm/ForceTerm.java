package de.topobyte.forceterm;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntConsumer;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jetbrains.annotations.NotNull;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.jediterm.core.Color;
import com.jediterm.terminal.CursorShape;
import com.jediterm.terminal.ProcessTtyConnector;
import com.jediterm.terminal.TtyConnector;
import com.jediterm.terminal.ui.JediTermWidget;
import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;

import de.topobyte.forceterm.preferences.ForceTermPreferences;
import de.topobyte.forceterm.preferences.Theme;
import de.topobyte.forceterm.settings.ConfigurationAction;
import de.topobyte.swing.util.JMenus;
import de.topobyte.swing.util.action.SimpleAction;

public class ForceTerm {

    private Theme theme;

    private Terminal createTerminalWidget() {
        Terminal terminal = new Terminal(theme);
        JediTermWidget widget = terminal.getWidget();

        widget.getTerminalPanel().setDefaultCursorShape(ForceTermPreferences.getCursorShape());
        widget.setTtyConnector(createTtyConnector());
        widget.start();

        return terminal;
    }

    private static @NotNull TtyConnector createTtyConnector() {
        try {
            Map<String, String> envs = System.getenv();
            String[] command = null;
            boolean setDirToHome = false;
            OperatingSystem os = PlatformUtil.getOS();

            if (ForceTermPreferences.isUseCustomShell()) {
                String customShellCommand = ForceTermPreferences.getCustomShellCommand();
                if (customShellCommand != null && !customShellCommand.isBlank()) {
                    command = customShellCommand.split(" ");
                }
            }

            if (os == OperatingSystem.WINDOWS) {
                if (command == null) {
                    Path bash = Paths.get("C:\\Program Files\\Git\\bin\\bash.exe");
                    if (Files.exists(bash)) {
                        command = new String[] { bash.toString() };
                    } else {
                        command = new String[] { "cmd.exe" };
                    }
                }
                // We currently don't have a reliable way to determine if this
                // was launched from the start menu or from any kind of shell
                // (powershell, cmd, git bash etc). So for now, always set the
                // dir to home.
                setDirToHome = true;
            } else if (os == OperatingSystem.MACOS) {
                if (command == null) {
                    command = new String[] { "/bin/zsh", "--login" };
                }
                envs = new HashMap<>(System.getenv());
                envs.put("TERM", "xterm-256color");
                envs.put("LANG", "en_US.UTF-8");
                envs.put("LC_ALL", "en_US.UTF-8");

                setDirToHome = System.getenv().get("TERM") == null;
            } else if (os == OperatingSystem.LINUX) {
                if (command == null) {
                    command = new String[] { "/bin/bash", "--login" };
                }
                envs = new HashMap<>(System.getenv());
                envs.put("TERM", "xterm-256color");
                // Workaround to make it possible to launch other apps built
                // using jpackage
                // from the shell.
                envs.remove("_JPACKAGE_LAUNCHER");
            } else {
                throw new RuntimeException("Invalid Operating System");
            }

            PtyProcessBuilder ptyProcessBuilder = new PtyProcessBuilder().setCommand(command);
            if (setDirToHome) {
                ptyProcessBuilder.setDirectory(System.getProperty("user.home"));
            } else {
                ptyProcessBuilder.setDirectory("");
            }
            ptyProcessBuilder.setEnvironment(envs);
            PtyProcess process = ptyProcessBuilder.start();
            return new PtyProcessTtyConnector(process, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private JFrame frame;
    private JTabbedPane tabbed;
    private final List<Terminal> terminals = new ArrayList<>();

    public void createAndShowGUI() {
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        theme = ForceTermPreferences.getTheme();
        setLookAndFeel(false);

        String version = Version.getVersion();
        frame = new JFrame("ForceTerm " + version);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("forceterm.png")));
        frame.setIconImage(icon.getImage());

        createMenu();

        tabbed = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        frame.setContentPane(tabbed);

        tabbed.putClientProperty("JTabbedPane.tabClosable", true);
        tabbed.putClientProperty("JTabbedPane.tabCloseCallback", (IntConsumer) index -> tabbed.remove(index));

        tabbed.addChangeListener(e -> {
            Component selected = tabbed.getSelectedComponent();
            if (tabbed.getTabCount() == 0) {
                System.exit(0);
            }
            if (selected != null) {
                SwingUtilities.invokeLater(selected::requestFocusInWindow);
            }
        });

        addTerminalWidget(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.setVisible(false);
                for (Terminal terminal : terminals) {
                    // terminate the current process
                    terminal.getWidget().getTtyConnector().close();
                }
            }
        });

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            KeyStroke ks = KeyStroke.getKeyStrokeForEvent(e);
            Object actionKey = frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).get(ks);
            if (actionKey != null) {
                Action action = frame.getRootPane().getActionMap().get(actionKey);
                if (action != null) {
                    action.actionPerformed(new ActionEvent(frame, ActionEvent.ACTION_PERFORMED, (String) actionKey));
                    e.consume();
                    return true;
                }
            }
            return false;
        });

        frame.pack();
        frame.setVisible(true);
    }

    private void setLookAndFeel(boolean updateComponentTree) {
        try {
            if (theme == null || theme == Theme.LIGHT) {
                UIManager.setLookAndFeel(new FlatLightLaf());
            } else if (theme == Theme.DARK) {
                UIManager.setLookAndFeel(new FlatDarculaLaf());
            }
            if (updateComponentTree) {
                SwingUtilities.updateComponentTreeUI(frame);
            }
        } catch (UnsupportedLookAndFeelException e) {
            if (theme == Theme.LIGHT) {
                System.err.println("Unable to set light LAF");
            } else {
                System.err.println("Unable to set dark LAF");
            }
        }
    }

    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuFile = new JMenu("File");
        JMenu menuView = new JMenu("View");

        Action actionOpenTab = new SimpleAction("Open Tab", "Open a new terminal tab") {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                addTerminalWidget(true);
            }

        };

        Action actionCloseTab = new SimpleAction("Close Tab", "Close currently open terminal tab") {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                closeCurrentTab();
            }

        };

        Action actionPreviousTab = new SimpleAction("Previous tab", "Select the previous tab") {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int i = tabbed.getSelectedIndex();
                if (i > 0) {
                    tabbed.setSelectedIndex(i - 1);
                }
            }

        };

        Action actionNextTab = new SimpleAction("Previous tab", "Select the previous tab") {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int i = tabbed.getSelectedIndex();
                if (i < tabbed.getTabCount() - 1) {
                    tabbed.setSelectedIndex(i + 1);
                }
            }

        };

        ConfigurationAction actionSettings = new ConfigurationAction(this);

        Action actionLightMode = new SimpleAction("Light Mode", "Set colors to a light theme") {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setTheme(Theme.LIGHT);
                ForceTermPreferences.setTheme(theme);
            }

        };

        Action actionDarkMode = new SimpleAction("Dark Mode", "Set colors to a dark theme") {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setTheme(Theme.DARK);
                ForceTermPreferences.setTheme(theme);
            }

        };

        define(menuFile, actionOpenTab, KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK), "ctrl-t");
        define(menuFile, actionCloseTab, KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK), "ctrl-w");
        define(menuFile, actionPreviousTab, KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, InputEvent.CTRL_DOWN_MASK), "ctrl-page-up");
        define(menuFile, actionNextTab, KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, InputEvent.CTRL_DOWN_MASK), "ctrl-page-down");
        JMenus.addItem(menuFile, actionSettings);

        JMenu menuTheme = new JMenu("Theme");
        menuView.add(menuTheme);

        JMenus.addItem(menuTheme, actionLightMode);
        JMenus.addItem(menuTheme, actionDarkMode);

        JMenu menuCursorStyle = new JMenu("Cursor style");
        menuView.add(menuCursorStyle);

        for (CursorShape shape : CursorShape.values()) {
            String name = EnumUtil.toCamelCaseWithSpaces(shape.name());
            Action action = new SimpleAction(name, "Set cursor shape to " + name) {

                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    setCursorShape(shape);
                    ForceTermPreferences.setCursorShape(shape);
                }

            };

            JMenus.addItem(menuCursorStyle, action);
        }

        menuBar.add(menuFile);
        menuBar.add(menuView);
        frame.setJMenuBar(menuBar);
    }

    private void define(JMenu menu, Action action, KeyStroke keyStroke, String mapKey) {
        InputMap inputMap = frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = frame.getRootPane().getActionMap();

        JMenus.addItem(menu, action, keyStroke);
        inputMap.put(keyStroke, mapKey);
        actionMap.put(mapKey, action);
    }

    private void addTerminalWidget(boolean focus) {
        try {
            Terminal terminal = createTerminalWidget();
            terminals.add(terminal);
            JediTermWidget widget = terminal.getWidget();
            widget.getTerminal().addApplicationTitleListener(title -> {
                int index = tabbed.indexOfComponent(widget);
                tabbed.setTitleAt(index, title);
            });
            tabbed.addTab("Terminal", widget);
            widget.addListener(terminalWidget -> {
                ProcessTtyConnector ttyConnector = (ProcessTtyConnector) widget.getTtyConnector();
                // Only close the tab if the shell process exited gracefully.
                // This catches the case where the user supplied a non-working
                // command as a custom shell command
                int exitValue = ttyConnector.getProcess().exitValue();
                if (exitValue == 0) {
                    closeTab(widget);
                }
            });
            if (focus) {
                SwingUtilities.invokeLater(() -> {
                    tabbed.setSelectedComponent(widget);
                    widget.getTerminalPanel().requestFocus();
                });
            }
        } catch (IllegalStateException e) {
            displayError(e);
            return;
        }
    }

    private void displayError(Throwable e) {
        JPanel panel = new JPanel(new BorderLayout());

        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        textArea.setEditable(false);
        textArea.setLineWrap(false);

        StringWriter stringWriter = new StringWriter();
        PrintWriter pw = new PrintWriter(stringWriter);
        printThrowable(e, pw, "");
        pw.close();

        textArea.setText(stringWriter.toString());

        JScrollPane jsp = new JScrollPane(textArea);
        jsp.setPreferredSize(new Dimension(600, 500));
        panel.add(jsp);

        tabbed.add("Error", panel);

        SwingUtilities.invokeLater(() -> textArea.setCaretPosition(0));
    }

    private static void printThrowable(Throwable t, PrintWriter pw, String caption) {
        pw.println(caption + t);
        for (StackTraceElement ste : t.getStackTrace()) {
            pw.println("\tat " + ste);
        }
        for (Throwable sup : t.getSuppressed()) {
            printThrowable(sup, pw, "Suppressed: ");
        }
        Throwable cause = t.getCause();
        if (cause != null) {
            printThrowable(cause, pw, "Caused by: ");
        }
    }

    private void closeCurrentTab() {
        JediTermWidget widget = (JediTermWidget) tabbed.getSelectedComponent();
        closeTab(widget);
    }

    private void closeTab(JediTermWidget widget) {
        widget.close(); // terminate the current process and dispose all
                        // allocated resources
        SwingUtilities.invokeLater(() -> tabbed.remove(widget));
    }

    public JFrame getFrame() {
        return frame;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
        if (theme == Theme.LIGHT) {
            setPalette(ColorPalettes.XTERM_COLORS);
        } else {
            setPalette(ColorPalettes.XTERM_COLORS_DARK);
        }
        setLookAndFeel(true);
    }

    private void setPalette(Color[] palette) {
        for (Terminal terminal : terminals) {
            terminal.getSettingsProvider().setTerminalColorPalette(new ColorPaletteImpl(palette));
        }
    }

    public void setCursorShape(CursorShape shape) {
        for (Terminal terminal : terminals) {
            terminal.getWidget().getTerminalPanel().setCursorShape(shape);
        }
    }

}
