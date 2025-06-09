package de.topobyte.forceterm;

import com.formdev.flatlaf.FlatLightLaf;
import com.jediterm.terminal.CursorShape;
import com.jediterm.terminal.TtyConnector;
import com.jediterm.terminal.ui.JediTermWidget;
import com.jediterm.terminal.ui.settings.DefaultSettingsProvider;
import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
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
import java.util.function.IntConsumer;

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
            if (isWindows()) {
                Path bash = Paths.get("C:\\Program Files\\Git\\bin\\bash.exe");
                if (Files.exists(bash)) {
                    command = new String[]{bash.toString()};
                } else {
                    command = new String[]{"cmd.exe"};
                }
            } else {
                command = new String[]{"/bin/bash", "--login"};
                envs = new HashMap<>(System.getenv());
                envs.put("TERM", "xterm-256color");
            }

            PtyProcess process = new PtyProcessBuilder().setCommand(command).setEnvironment(envs).start();
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

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            System.out.println("Cannot set look and feel");
        }

        tabbed = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        frame.setContentPane(tabbed);

        tabbed.putClientProperty("JTabbedPane.tabClosable", true);
        tabbed.putClientProperty("JTabbedPane.tabCloseCallback",
                (IntConsumer) index -> {
                    tabbed.remove(index);
                });

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

    private void addTerminalWidget(boolean focus) {
        JediTermWidget widget = createTerminalWidget();
        widgets.add(widget);
        widget.getTerminal().addApplicationTitleListener(title -> {
            int index = tabbed.indexOfComponent(widget);
            tabbed.setTitleAt(index, title);
        });
        tabbed.addTab("Terminal", widget);
        widget.addListener(terminalWidget -> {
            widget.close(); // terminate the current process and dispose all allocated resources
            SwingUtilities.invokeLater(() -> tabbed.remove(widget));
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

}
