package de.topobyte.forceterm;

import com.formdev.flatlaf.FlatLightLaf;
import com.jediterm.terminal.CursorShape;
import com.jediterm.terminal.TtyConnector;
import com.jediterm.terminal.ui.JediTermWidget;
import com.jediterm.terminal.ui.settings.DefaultSettingsProvider;
import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import de.topobyte.swing.util.JMenus;
import de.topobyte.swing.util.action.SimpleAction;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.function.IntConsumer;

import static de.topobyte.forceterm.PlatformUtil.isMacOS;
import static de.topobyte.forceterm.PlatformUtil.isWindows;

public class ForceTerm {

    private static @NotNull JediTermWidget createTerminalWidget() {
        JediTermWidget widget = new JediTermWidget(80, 24, new DefaultSettingsProvider());
        widget.getTerminalPanel().setDefaultCursorShape(CursorShape.BLINK_UNDERLINE);
        widget.setTtyConnector(createTtyConnector());
        widget.start();
        return widget;
    }

    private static @NotNull TtyConnector createTtyConnector() {
        try {
            Map<String, String> envs = System.getenv();
            String[] command;
            boolean setDirToHome = false;
            if (isWindows()) {
                Path bash = Paths.get("C:\\Program Files\\Git\\bin\\bash.exe");
                if (Files.exists(bash)) {
                    command = new String[]{bash.toString()};
                } else {
                    command = new String[]{"cmd.exe"};
                }
            } else if (isMacOS()) {
                command = new String[]{"/bin/zsh", "--login"};
                envs = new HashMap<>(System.getenv());
                envs.put("TERM", "xterm-256color");
                envs.put("LANG", "en_US.UTF-8");
                envs.put("LC_ALL", "en_US.UTF-8");

                setDirToHome = System.getenv().get("TERM") == null;
            } else {
                command = new String[]{"/bin/bash", "--login"};
                envs = new HashMap<>(System.getenv());
                envs.put("TERM", "xterm-256color");
            }

            PtyProcessBuilder ptyProcessBuilder = new PtyProcessBuilder().setCommand(command).setDirectory("");
            if (setDirToHome) {
                ptyProcessBuilder.setDirectory(System.getProperty("user.home"));
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
    private final List<JediTermWidget> widgets = new ArrayList<>();

    public void createAndShowGUI() {
        frame = new JFrame("ForceTerm");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(
                Thread.currentThread().getContextClassLoader().getResource("forceterm.png")));
        frame.setIconImage(icon.getImage());

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            System.out.println("Cannot set look and feel");
        }

        createMenu();

        tabbed = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        frame.setContentPane(tabbed);

        tabbed.putClientProperty("JTabbedPane.tabClosable", true);
        tabbed.putClientProperty("JTabbedPane.tabCloseCallback",
                (IntConsumer) index -> tabbed.remove(index));

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
                for (JediTermWidget widget : widgets) {
                    widget.getTtyConnector().close(); // terminate the current process
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

    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuFile = new JMenu("File");

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

        define(menuFile, actionOpenTab, KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK), "ctrl-t");
        define(menuFile, actionCloseTab, KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK), "ctrl-w");
        define(menuFile, actionPreviousTab, KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, InputEvent.CTRL_DOWN_MASK), "ctrl-page-up");
        define(menuFile, actionNextTab, KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, InputEvent.CTRL_DOWN_MASK), "ctrl-page-down");

        menuBar.add(menuFile);
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
        JediTermWidget widget = createTerminalWidget();
        widgets.add(widget);
        widget.getTerminal().addApplicationTitleListener(title -> {
            int index = tabbed.indexOfComponent(widget);
            tabbed.setTitleAt(index, title);
        });
        tabbed.addTab("Terminal", widget);
        widget.addListener(terminalWidget -> {
            closeTab(widget);
        });

        if (focus) {
            SwingUtilities.invokeLater(() -> {
                tabbed.setSelectedComponent(widget);
                widget.getTerminalPanel().requestFocus();
            });
        }
    }

    private void closeCurrentTab() {
        JediTermWidget widget = (JediTermWidget) tabbed.getSelectedComponent();
        closeTab(widget);
    }

    private void closeTab(JediTermWidget widget) {
        widget.close(); // terminate the current process and dispose all allocated resources
        SwingUtilities.invokeLater(() -> tabbed.remove(widget));
    }

}
