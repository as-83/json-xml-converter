package util;

import org.hyperskill.hstest.exception.outcomes.WrongAnswer;

import java.util.ResourceBundle;
import java.util.regex.Pattern;

import static java.text.MessageFormat.format;

public class Assert {
    private static final ResourceBundle messages = ResourceBundle.getBundle("Messages");

    public static void assertEquals(
            final Object expected,
            final Object actual,
            final String error,
            final Object... args) {

        if (!expected.equals(actual)) {
            final var feedback = format(messages.getString(error), args);
            throw new WrongAnswer(feedback);
        }
    }

    public static void assertMatches(
            final String pattern,
            final String actual,
            final String error,
            final Object... args) {

        if (!actual.matches(pattern)) {
            final var feedback = format(messages.getString(error), args);
            throw new WrongAnswer(feedback);
        }
    }

    public static void assertMatches(
            final Pattern pattern,
            final String actual,
            final String error,
            final Object... args) {

        if (!pattern.matcher(actual).matches()) {
            final var feedback = format(messages.getString(error), args);
            throw new WrongAnswer(feedback);
        }
    }

    public static void assertTrue(
            final boolean condition,
            final String error,
            final Object... args) {
        if (!condition) {
            final var feedback = format(messages.getString(error), args);
            throw new WrongAnswer(feedback);
        }
    }

    public static void assertFalse(
            final boolean condition,
            final String error,
            final Object... args) {
        if (condition) {
            final var feedback = format(messages.getString(error), args);
            throw new WrongAnswer(feedback);
        }
    }
}