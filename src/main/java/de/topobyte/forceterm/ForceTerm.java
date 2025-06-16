package de.topobyte.forceterm;

import com.formdev.flatlaf.FlatLightLaf;
import com.jediterm.terminal.CursorShape;
import com.jediterm.terminal.TtyConnector;
import com.jediterm.terminal.ui.JediTermWidget;
import com.jediterm.terminal.ui.settings.DefaultSettingsProvider;
import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN && e.isControlDown()) {
                    int i = tabbed.getSelectedIndex();
                    if (i < tabbed.getTabCount() - 1) {
                        tabbed.setSelectedIndex(i + 1);
                    }
                    return true;
                } else if (e.getKeyCode() == KeyEvent.VK_PAGE_UP && e.isControlDown()) {
                    int i = tabbed.getSelectedIndex();
                    if (i > 0) {
                        tabbed.setSelectedIndex(i - 1);
                    }
                    return true;
                }
            }

            return false;
        });

        frame.pack();
        frame.setVisible(true);

        InputMap inputMap = tabbed.getInputMap();
        ActionMap actionMap = tabbed.getActionMap();
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK), "ctrl-t");
        actionMap.put("ctrl-t", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                addTerminalWidget(true);
            }
        });
    }

    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuFile = new JMenu("File");
        JMenuItem itemOpenTab = new JMenuItem("Open Tab");
        JMenuItem itemCloseTab = new JMenuItem("Close Tab");
        itemOpenTab.addActionListener(e -> addTerminalWidget(true));
        itemCloseTab.addActionListener(e -> closeCurrentTab());
        menuFile.add(itemOpenTab);
        menuFile.add(itemCloseTab);

        menuBar.add(menuFile);

        frame.setJMenuBar(menuBar);
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

        widget.getTerminalPanel().addCustomKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_T && e.isControlDown()) {
                    addTerminalWidget(true);
                }
            }
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
