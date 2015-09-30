package org.qzerver.model.agent.action.providers;

import java.util.regex.Pattern;

public final class ActionPlaceholders {

    public static final String NODE_VARIABLE = "node";

    public static final Pattern NODE_PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{node\\}");

    public static final String EXECUTION_VARIABLE = "execution";

    public static final Pattern EXECUTION_PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{execution\\}");

    private ActionPlaceholders() {
    }

    public static String substituteNode(String value, String node) {
        return NODE_PLACEHOLDER_PATTERN.matcher(value).replaceAll(node);
    }

    public static String substituteExecution(String value, long scheduleExecutionId) {
        String execution = Long.toString(scheduleExecutionId);
        return EXECUTION_PLACEHOLDER_PATTERN.matcher(value).replaceAll(execution);
    }
}
