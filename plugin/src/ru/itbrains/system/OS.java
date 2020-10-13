package ru.itbrains.system;

public class OS
{
    public static boolean isWindows()
    {
        String os = System.getProperty("os.name").toLowerCase();

        return (os.indexOf("win") >= 0);
    }

    public static boolean isMac()
    {

        String os = System.getProperty("os.name").toLowerCase();

        return (os.indexOf("mac") >= 0);

    }

    public static boolean isUnix()
    {

        String os = System.getProperty("os.name").toLowerCase();

        return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);

    }

    public static boolean isFreeBSD()
    {

        String os = System.getProperty("os.name").toLowerCase();

        return (os.indexOf("FreeBSD") >= 0);

    }

    public static boolean isSolaris()
    {
        String os = System.getProperty("os.name").toLowerCase();

        return (os.indexOf("sunos") >= 0);
    }
}
