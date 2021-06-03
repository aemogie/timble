package io.github.aemogie.timble.util;

import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class Debug {
    public static void prependStackTrace(@NotNull StackTraceElement[] stackTrace, StringBuilder output) {
        StackTraceElement element = stackTrace[2];
        StringBuilder stacktrace = new StringBuilder();
        stacktrace.append("\t" + "From class: " + element.getClassName() + "\n");
        stacktrace.append("\t" + "From method: " + element.getMethodName() + "\n");
        stacktrace.append("\t" + "At line: " + element.getLineNumber() + "\n");
        output.insert(0, stacktrace);
    }

}
