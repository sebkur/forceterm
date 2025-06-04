package de.topobyte.forceterm;

public class PlatformUtil {

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().startsWith("windows");
    }

    public static boolean isMacOS() {
        return System.getProperty("os.name").toLowerCase().startsWith("mac");
    }

}
