package de.topobyte.forceterm.cli;

import java.util.List;

import org.apache.commons.cli.Options;

import de.topobyte.forceterm.ForceTermLauncher;
import de.topobyte.forceterm.init.InitAtDir;
import de.topobyte.forceterm.init.InitAuto;
import de.topobyte.forceterm.init.InitMode;
import de.topobyte.shared.preferences.SharedPreferences;
import de.topobyte.swing.util.SwingUtils;
import de.topobyte.utilities.apache.commons.cli.CliTool;
import de.topobyte.utilities.apache.commons.cli.commands.args.CommonsCliArguments;
import de.topobyte.utilities.apache.commons.cli.commands.options.CommonsCliExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptionsFactory;

public class Terminal {

    public static ExeOptionsFactory OPTIONS_FACTORY = new ExeOptionsFactory() {

        @Override
        public ExeOptions createOptions() {
            Options options = new Options();
            return new CommonsCliExeOptions(options, "[options] [<directory>]");
        }

    };

    public static void main(String name, CommonsCliArguments arguments) {
        CliTool tool = arguments.getOptions().tool(name);
        List<String> args = arguments.getLine().getArgList();

        InitMode init = null;
        if (args.size() == 0) {
            init = new InitAuto();
        } else if (args.size() == 1) {
            init = new InitAtDir(args.get(0));
        } else {
            tool.printMessageAndHelpAndExit("Please specify at most one additional parameter.");
        }

        if (SharedPreferences.isUIScalePresent()) {
            SwingUtils.setUiScale(SharedPreferences.getUIScale());
        }

        ForceTermLauncher.launchSingle(init);
    }

}
