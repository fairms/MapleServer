package im.cave.ms.tools;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.StringTokenizer;

/**
 * Provides a suite of utilities for manipulating strings.
 *
 * @author Frz
 * @version 1.0
 * @since Revision 336
 */
public class StringUtil {

    public static String getLeftPaddedStr(String in, char padchar, int length) { //左
        return String.valueOf(padchar).repeat(Math.max(0, length - in.getBytes().length)) +
                in;
    }

    public static String getRightPaddedStr(int in, char padchar, int length) { //右
        return getRightPaddedStr(String.valueOf(in), padchar, length);
    }

    public static String getRightPaddedStr(long in, char padchar, int length) { //右
        return getRightPaddedStr(String.valueOf(in), padchar, length);
    }

    public static String getRightPaddedStr(String in, char padchar, int length) { //右
        return in + String.valueOf(padchar).repeat(Math.max(0, length - in.getBytes().length));
    }


    public static String joinStringFrom(String[] arr, int start) {
        return joinStringFrom(arr, start, " ");
    }


    public static String joinStringFrom(String[] arr, int start, String sep) {
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < arr.length; i++) {
            builder.append(arr[i]);
            if (i != arr.length - 1) {
                builder.append(sep);
            }
        }
        return builder.toString();
    }


    public static String makeEnumHumanReadable(String enumName) {
        StringBuilder builder = new StringBuilder(enumName.length() + 1);
        for (String word : enumName.split("_")) {
            if (word.length() <= 2) {
                builder.append(word); // assume that it's an abbrevation
            } else {
                builder.append(word.charAt(0));
                builder.append(word.substring(1).toLowerCase());
            }
            builder.append(' ');
        }
        return builder.substring(0, enumName.length());
    }


    public static int countCharacters(String str, char chr) {
        int ret = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == chr) {
                ret++;
            }
        }
        return ret;
    }


    public static int[] StringToInt(final String str, final String separator) {
        final StringTokenizer strTokens = new StringTokenizer(str, separator);
        int[] strArray = new int[strTokens.countTokens()];
        int i = 0;
        while (strTokens.hasMoreTokens()) {
            strArray[i] = Integer.parseInt(strTokens.nextToken().trim());
            i++;
        }
        return strArray;
    }

    public static boolean[] StringToBoolean(final String str, final String separator) {
        final StringTokenizer strTokens = new StringTokenizer(str, separator);
        boolean[] strArray = new boolean[strTokens.countTokens()];
        int i = 0;
        while (strTokens.hasMoreTokens()) {
            strArray[i] = Boolean.parseBoolean(strTokens.nextToken().trim());
            i++;
        }
        return strArray;
    }

    public static boolean isNumber(final String str) {
        return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
    }


    public static String codeString(String fileName) throws Exception {
        BufferedInputStream bin = new BufferedInputStream(
                new FileInputStream(fileName));
        int p = (bin.read() << 8) + bin.read();
        String code;
        switch (p) {
            case 0xefbb:
                code = "UTF-8";
                break;
            case 0xfffe:
                code = "Unicode";
                break;
            case 0xfeff:
                code = "UTF-16BE";
                break;
            case 0x5c75:
                code = "ANSI|ASCII";
                break;
            default:
                code = "GBK";
        }

        return code;
    }
}
