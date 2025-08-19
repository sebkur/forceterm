package de.topobyte.forceterm;

import de.topobyte.forceterm.cli.Terminal;
import de.topobyte.forceterm.cli.Workspace;
import de.topobyte.utilities.apache.commons.cli.CliTool;
import de.topobyte.utilities.apache.commons.cli.commands.ArgumentParser;
import de.topobyte.utilities.apache.commons.cli.commands.ExeRunner;
import de.topobyte.utilities.apache.commons.cli.commands.ExecutionData;
import de.topobyte.utilities.apache.commons.cli.commands.RunnerException;
import de.topobyte.utilities.apache.commons.cli.commands.delegate.Delegate;
import de.topobyte.utilities.apache.commons.cli.commands.options.DelegateExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptionsFactory;
import de.topobyte.utilities.apache.commons.cli.parsing.ArgumentParseException;

public class RunForceTerm {

    public static ExeOptionsFactory OPTIONS_FACTORY = new ExeOptionsFactory() {

        @Override
        public ExeOptions createOptions() {
            DelegateExeOptions options = new DelegateExeOptions();
            options.addCommand("terminal", Terminal.OPTIONS_FACTORY, Terminal.class);
            options.addCommand("workspace", Workspace.OPTIONS_FACTORY, Workspace.class);

            return options;
        }

    };

    public static void main(String[] args) throws RunnerException {
        String name = "forceterm";

        ExeOptions options = OPTIONS_FACTORY.createOptions();
        ArgumentParser parser = new ArgumentParser(name, options);

        ExecutionData data;
        try {
            data = parser.parse(name, args, null);
            if (data != null) {
                ExeRunner.run(data);
            }
        } catch (ArgumentParseException e) {
            if (e.getMessage().startsWith("Invalid command")) {
                CliTool tool = options.tool(name);
                tool.printMessageAndHelpAndExit("Invalid command");
            } else if (e.getMessage().equals("Missing command name")) {
                // Run as if 'terminal' subtask had been specified
                runAsTerminalTask(name, (DelegateExeOptions) options, args);
            }
        }
    }

    private static void runAsTerminalTask(String name, DelegateExeOptions options, String[] args) throws RunnerException {
        ExeOptions subOptions = Terminal.OPTIONS_FACTORY.createOptions();
        Delegate nextDelegate = options.getDelegate("terminal");
        ArgumentParser subParser = new ArgumentParser(name, subOptions);
        try {
            ExecutionData data = subParser.parse(name, args, nextDelegate);
            ExeRunner.run(data);
        } catch (ArgumentParseException e) {
            CliTool tool = options.tool(name);
            tool.printMessageAndHelpAndExit("Unable to parse command line arguments");
        }
    }

}
