package de.topobyte.forceterm.cli;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.Options;

import de.topobyte.forceterm.ForceTermLauncher;
import de.topobyte.forceterm.init.InitAtDir;
import de.topobyte.forceterm.init.InitMode;
import de.topobyte.shared.preferences.SharedPreferences;
import de.topobyte.swing.util.SwingUtils;
import de.topobyte.utilities.apache.commons.cli.CliTool;
import de.topobyte.utilities.apache.commons.cli.commands.args.CommonsCliArguments;
import de.topobyte.utilities.apache.commons.cli.commands.options.CommonsCliExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptionsFactory;

public class Workspace {

    public static ExeOptionsFactory OPTIONS_FACTORY = new ExeOptionsFactory() {

        @Override
        public ExeOptions createOptions() {
            Options options = new Options();
            return new CommonsCliExeOptions(options, "[options] <directory> [<directory>...]");
        }

    };

    public static void main(String name, CommonsCliArguments arguments) {
        List<String> args = arguments.getLine().getArgList();

        if (args.size() < 1) {
            CliTool tool = arguments.getOptions().tool(name);
            tool.printMessageAndHelpAndExit("Please specify at least one directory");
        }

        if (SharedPreferences.isUIScalePresent()) {
            SwingUtils.setUiScale(SharedPreferences.getUIScale());
        }

        List<InitMode> inits = new ArrayList<>();
        for (String arg : args) {
            inits.add(new InitAtDir(arg));
        }

        ForceTermLauncher.launchMultiple(inits);
    }

}
