package de.topobyte.forceterm.init;

public class InitAtDir implements InitMode {

    private String directory;

    public InitAtDir(String directory) {
        this.directory = directory;
    }

    public String getDirectory() {
        return directory;
    }

}
