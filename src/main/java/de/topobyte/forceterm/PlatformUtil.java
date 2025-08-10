package de.topobyte.forceterm;

public class PlatformUtil {

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().startsWith("windows");
    }

    public static boolean isMacOS() {
        return System.getProperty("os.name").toLowerCase().startsWith("mac");
    }

    // Determine the OS once so that we don't need multiple string comparisons whenever
    // we want to branch depending on the current operating system.
    private static OperatingSystem OS;

    static {
        if (isWindows()) {
            OS = OperatingSystem.WINDOWS;
        } else if (isMacOS()) {
            OS = OperatingSystem.MACOS;
        } else {
            OS = OperatingSystem.LINUX;
        }
    }

    public static OperatingSystem getOS() {
        return OS;
    }

}
