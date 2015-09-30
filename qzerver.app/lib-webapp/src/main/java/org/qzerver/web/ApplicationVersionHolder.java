package org.qzerver.web;

/**
 * Application version holder loads version from JAR/WAR manifest
 */
public final class ApplicationVersionHolder {

    public static final String UNKNOWN_VERSION = "0.0-dev";

    public static final String VERSION = loadVersion();

    private ApplicationVersionHolder() {
    }

    private static String loadVersion() {
        Package classPackage = ApplicationVersionHolder.class.getPackage();

        if (classPackage == null) {
            return UNKNOWN_VERSION;
        }

        String version = classPackage.getImplementationVersion();

        if (version == null) {
            return UNKNOWN_VERSION;
        }

        return version;
    }

}
