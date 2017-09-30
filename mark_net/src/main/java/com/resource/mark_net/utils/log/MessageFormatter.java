/**
 * Copyright (C) 2006-2014 Tuniu All rights reserved
 */
package com.resource.mark_net.utils.log;

import java.util.HashMap;
import java.util.Map;

/**
 * Formats messages according to very simple substitution rules. Substitutions
 * can be made 1, 2 or more arguments.
 * <p/>
 * <p/>
 * For example,
 * <p/>
 * <pre>
 * MessageFormatter.format(&quot;Hi {}.&quot;, &quot;there&quot;)
 * </pre>
 * <p/>
 * will return the string "Hi there.".
 * <p/>
 * The {} pair is called the <em>formatting anchor</em>. It serves to designate
 * the location where arguments need to be substituted within the message
 * pattern.
 * <p/>
 * In case your message contains the '{' or the '}' character, you do not have
 * to do anything special unless the '}' character immediately follows '{'. For
 * example,
 * <p/>
 * <pre>
 * MessageFormatter.format(&quot;Set {1,2,3} is not equal to {}.&quot;, &quot;1,2&quot;);
 * </pre>
 * <p/>
 * will return the string "Set {1,2,3} is not equal to 1,2.".
 * <p/>
 * <p/>
 * If for whatever reason you need to place the string "{}" in the message
 * without its <em>formatting anchor</em> meaning, then you need to escape the
 * '{' character with '\', that is the backslash character. Only the '{'
 * character should be escaped. There is no need to escape the '}' character.
 * For example,
 * <p/>
 * <pre>
 * MessageFormatter.format(&quot;Set \\{} is not equal to {}.&quot;, &quot;1,2&quot;);
 * </pre>
 * <p/>
 * will return the string "Set {} is not equal to 1,2.".
 * <p/>
 * <p/>
 * The escaping behavior just described can be overridden by escaping the escape
 * character '\'. Calling
 * <p/>
 * <pre>
 * MessageFormatter.format(&quot;File name is C:\\\\{}.&quot;, &quot;file.zip&quot;);
 * </pre>
 * <p/>
 * will return the string "File name is C:\file.zip".
 * <p/>
 * <p/>
 * The formatting conventions are different than those of {@link java.text.MessageFormat}
 * which ships with the Java platform. This is justified by the fact that
 * SLF4J's implementation is 10 times faster than that of {@link java.text.MessageFormat}.
 * This local performance difference is both measurable and significant in the
 * larger context of the complete logging processing chain.
 * <p/>
 * <p/>
 * See also
 * {@link #format(String, Object[])} methods for more details.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @author Joern Huxhorn
 * @author inspire from slf4j
 */
final public class MessageFormatter {

    static final char DELIM_START = '{';
    //static final char DELIM_STOP = '}';
    static final String DELIM_STR = "{}";
    private static final char ESCAPE_CHAR = '\\';

    static Throwable getThrowableCandidate(Object[] argArray) {
        if (argArray == null || argArray.length == 0) {
            return null;
        }

        final Object lastEntry = argArray[argArray.length - 1];
        if (lastEntry instanceof Throwable) {
            return (Throwable) lastEntry;
        }
        return null;
    }

    /**
     * Performs several arguments substitution for the 'messagePattern' passed as
     * parameter.
     * <p/>
     * For example,
     * <p/>
     * <pre>
     * MessageFormatter.format(&quot;Hi {}. My name is {}.&quot;, &quot;Alice&quot;, &quot;Bob&quot;);
     * </pre>
     * <p/>
     * will return the string "Hi Alice. My name is Bob.".
     *
     * @param messagePattern The message pattern which will be parsed and formatted
     * @param argArray       An array of arguments to be substituted in place of formatting
     *                       anchors
     * @return The formatted message
     */
    public static FormattingTuple format(final String messagePattern, final Object... argArray) {

        Throwable throwableCandidate = getThrowableCandidate(argArray);

        if (messagePattern == null) {
            return new FormattingTuple(null, throwableCandidate);
        }

        if (argArray == null) {
            return new FormattingTuple(messagePattern);
        }

        int i = 0;
        int j;
        StringBuffer sbuf = new StringBuffer(messagePattern.length() + 50);

        int L;
        for (L = 0; L < argArray.length; L++) {

            j = messagePattern.indexOf(DELIM_STR, i);

            if (j == -1) {
                // no more variables
                if (i == 0) { // this is a simple string
                    return new FormattingTuple(messagePattern, throwableCandidate);
                } else { // add the tail string which contains no variables and return
                    // the result.
                    sbuf.append(messagePattern.substring(i, messagePattern.length()));
                    return new FormattingTuple(sbuf.toString(), throwableCandidate);
                }
            } else {
                if (isEscapedDelimeter(messagePattern, j)) {
                    if (!isDoubleEscaped(messagePattern, j)) {
                        L--; // DELIM_START was escaped, thus should not be incremented
                        sbuf.append(messagePattern.substring(i, j - 1));
                        sbuf.append(DELIM_START);
                        i = j + 1;
                    } else {
                        // The escape character preceding the delimiter start is
                        // itself escaped: "abc x:\\{}"
                        // we have to consume one backward slash
                        sbuf.append(messagePattern.substring(i, j - 1));
                        deeplyAppendParameter(sbuf, argArray[L], new HashMap());
                        i = j + 2;
                    }
                } else {
                    // normal case
                    sbuf.append(messagePattern.substring(i, j));
                    deeplyAppendParameter(sbuf, argArray[L], new HashMap());
                    i = j + 2;
                }
            }
        }
        // append the characters following the last {} pair.
        sbuf.append(messagePattern.substring(i, messagePattern.length()));
        if (L < argArray.length - 1) {
            return new FormattingTuple(sbuf.toString(), throwableCandidate);
        } else {
            return new FormattingTuple(sbuf.toString(), null);
        }
    }

    static boolean isEscapedDelimeter(String messagePattern, int delimeterStartIndex) {

        if (delimeterStartIndex == 0) {
            return false;
        }
        char potentialEscape = messagePattern.charAt(delimeterStartIndex - 1);
        return potentialEscape == ESCAPE_CHAR;
    }

    static boolean isDoubleEscaped(String messagePattern, int delimeterStartIndex) {
        return delimeterStartIndex >= 2
                && messagePattern.charAt(delimeterStartIndex - 2) == ESCAPE_CHAR;
    }

    private static void deeplyAppendParameter(StringBuffer buffer, Object o, Map seenMap) {
        if (o == null) {
            buffer.append("null");
            return;
        }
        if (!o.getClass().isArray()) {
            safeObjectAppend(buffer, o);
        } else {
            // check for primitive array types because they
            // unfortunately cannot be cast to Object[]
            if (o instanceof boolean[]) {
                booleanArrayAppend(buffer, (boolean[]) o);
            } else if (o instanceof byte[]) {
                byteArrayAppend(buffer, (byte[]) o);
            } else if (o instanceof char[]) {
                charArrayAppend(buffer, (char[]) o);
            } else if (o instanceof short[]) {
                shortArrayAppend(buffer, (short[]) o);
            } else if (o instanceof int[]) {
                intArrayAppend(buffer, (int[]) o);
            } else if (o instanceof long[]) {
                longArrayAppend(buffer, (long[]) o);
            } else if (o instanceof float[]) {
                floatArrayAppend(buffer, (float[]) o);
            } else if (o instanceof double[]) {
                doubleArrayAppend(buffer, (double[]) o);
            } else {
                objectArrayAppend(buffer, (Object[]) o, seenMap);
            }
        }
    }

    private static void safeObjectAppend(StringBuffer sbuf, Object o) {
        try {
            String oAsString = o.toString();
            sbuf.append(oAsString);
        } catch (Throwable t) {
            System.err
                    .println("SLF4J: Failed toString() invocation on an object of type ["
                            + o.getClass().getName() + "]");
            t.printStackTrace();
            sbuf.append("[FAILED toString()]");
        }

    }

    @SuppressWarnings(value = "unchecked")
    private static void objectArrayAppend(StringBuffer sbuf, Object[] a, Map seenMap) {
        sbuf.append('[');
        if (!seenMap.containsKey(a)) {
            seenMap.put(a, null);
            final int len = a.length;
            for (int i = 0; i < len; i++) {
                deeplyAppendParameter(sbuf, a[i], seenMap);
                if (i != len - 1)
                    sbuf.append(", ");
            }
            // allow repeats in siblings
            seenMap.remove(a);
        } else {
            sbuf.append("...");
        }
        sbuf.append(']');
    }

    private static void booleanArrayAppend(StringBuffer sbuf, boolean[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1)
                sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void byteArrayAppend(StringBuffer sbuf, byte[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1)
                sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void charArrayAppend(StringBuffer sbuf, char[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1)
                sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void shortArrayAppend(StringBuffer sbuf, short[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1)
                sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void intArrayAppend(StringBuffer sbuf, int[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1)
                sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void longArrayAppend(StringBuffer sbuf, long[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1)
                sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void floatArrayAppend(StringBuffer sbuf, float[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1)
                sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void doubleArrayAppend(StringBuffer sbuf, double[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1)
                sbuf.append(", ");
        }
        sbuf.append(']');
    }
}