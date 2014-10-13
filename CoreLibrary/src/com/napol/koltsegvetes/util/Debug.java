package com.napol.koltsegvetes.util;

/**
 * @author Polcz Péter <ppolcz@gmail.com>
 * 
 * Created on Oct 13, 2014 7:28:15 PM
 */
public class Debug
{
    private static final String TAG = "pcz> ";
    
    public static void debug (String format) {
        System.out.println(TAG + getCodeLocation(4) + " - " + format);
    }

    public static void debug (String format, Throwable ex) {
        System.out.println(TAG + getCodeLocation(4) + " - " + format + "\n\tMessage: " + ex.getMessage());
    }

    public static void debug (Throwable ex) {
        System.out.println(TAG + getCodeLocation(4) + " - " + ex.getMessage());
    }

    public static void debug (String format, Object ... args) {
        System.out.println(TAG + getCodeLocation(4) + " - " + String.format(format, args));
    }

    public static void debug () {
        System.out.println(TAG + getCodeLocation(4));
    }

    public static String getCodeLocation (int depth) {
        StackTraceElement trace = Thread.currentThread().getStackTrace()[depth];
        return "in " + trace.getClassName()+ "#" + trace.getMethodName() + ":" + trace.getLineNumber();
    }
}
