package org.qzerver.util;

import org.apache.commons.lang.SystemUtils;

public final class TestUtils {

    private TestUtils() {
    }

    public static String findJavaCommand() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return System.getProperty("java.home") + "/bin/java.exe";
        } else {
            return System.getProperty("java.home") + "/bin/java";
        }
    }

    public static String findLineByPrefix(String[] lines, String prefix) {
        for (String line : lines) {
            if (line.startsWith(prefix)) {
                return line;
            }
        }
        return null;
    }

    public static String findLineEqualWithCase(String[] lines, String sample) {
        for (String line : lines) {
            if (line.equals(sample)) {
                return line;
            }
        }
        return null;
    }

    public static String findLineEqualCaseless(String[] lines, String sample) {
        for (String line : lines) {
            if (line.equalsIgnoreCase(sample)) {
                return line;
            }
        }
        return null;
    }

}
