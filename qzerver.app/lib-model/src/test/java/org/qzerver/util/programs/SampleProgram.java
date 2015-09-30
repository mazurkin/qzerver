package org.qzerver.util.programs;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang.SystemUtils;

import java.util.Map;

public class SampleProgram {

    private static final String OPTION_ERROR = "e";

    private static final String OPTION_SLEEP = "s";

    private static final String OPTION_LINES = "l";

    private static final String OPTION_EXITCODE = "x";

    public static void main(String[] arguments) throws Exception {
        CommandLine commandLine = parseCommandLine(arguments);

        System.out.printf("START#\n");

        System.out.printf("WORKFOLDER#%s\n", SystemUtils.getUserDir());

        for (int i=0, size=arguments.length; i < size; i++) {
            System.out.printf("ARGUMENT_%d#%s\n", i+1, arguments[i]);
        }

        Map<String, String> environment = System.getenv();
        for (Map.Entry<String, String> entry : environment.entrySet()) {
            System.out.printf("ENVIRONMENT#%s=%s\n", entry.getKey(), entry.getValue());
        }

        if (commandLine.hasOption(OPTION_ERROR)) {
            String error = commandLine.getOptionValue(OPTION_ERROR);
            if (error != null) {
                System.out.printf("ERROR#\n");
                System.err.printf("%s\n", error);
            }
        }

        if (commandLine.hasOption(OPTION_SLEEP)) {
            String sleep = commandLine.getOptionValue(OPTION_SLEEP);
            if (sleep != null) {
                System.out.printf("SLEEPING#\n");
                long sleepValueSec = Long.parseLong(sleep);
                Thread.sleep(sleepValueSec * 1000);
            }
        }

        if (commandLine.hasOption(OPTION_LINES)) {
            String lines = commandLine.getOptionValue(OPTION_LINES);
            if (lines != null) {
                System.out.printf("DUMPING#\n");
                int linesValue = Integer.parseInt(lines);
                for (int i=0; i < linesValue; i++) {
                    System.out.printf("01234567890123456789012345678901234567890123456789");
                }
            }
        }

        System.out.printf("FINISH#\n");

        if (commandLine.hasOption(OPTION_EXITCODE)) {
            String exitCode = commandLine.getOptionValue(OPTION_EXITCODE);
            if (exitCode != null) {
                int exitCodeValue = Integer.parseInt(exitCode);
                System.exit(exitCodeValue);
            }
        }
    }

    private static CommandLine parseCommandLine(String[] arguments) throws ParseException {
        Options options = new Options();
        options.addOption(OPTION_ERROR, true, "Display error text in stderr");
        options.addOption(OPTION_SLEEP, true, "Sleep specified amount of seconds");
        options.addOption(OPTION_LINES, true, "Pring the specified amount of lines");
        options.addOption(OPTION_EXITCODE, true, "Specify exit code");

        CommandLineParser parser = new PosixParser();
        return parser.parse(options, arguments);
    }
}
