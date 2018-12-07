package com.taoyr.app.utility;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {
    private static Matcher matcher = null;
    private static Pattern pattern = null;

    public static String[] getText(String paramString1, String paramString2, String paramString3) {
        if (("".equals(paramString1)) || ("".equals(paramString2)) || ("".equals(paramString3))) {
            return new String[0];
        }
        return rulePattern(paramString1, "(?<=\\Q" + paramString2 + "\\E).*?(?=\\Q" + paramString3 + "\\E)");
    }

    public static String getText2(String paramString1, String paramString2, String paramString3) {
        String[] res = getText(paramString1, paramString2, paramString3);
        if (res.length > 0) {
            return res[0];
        }
        return "";
    }

    public static String[] rulePattern(String paramString1, String paramString2) {
        Pattern pattern = Pattern.compile(paramString2, 40);
        Matcher matcher = pattern.matcher(paramString1);
        ArrayList list = new ArrayList();
        while (matcher.find()) {
            list.add(matcher.group());
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    public static String[] splitText(String paramString1, String paramString2) {
        if (("".equals(paramString2)) || ("".equals(paramString1))) {
            return new String[0];
        }
        String str = paramString1;
        if (paramString2.equals("\n")) {
            str = subTextReplace(paramString1, "\r", "");
        }
        if (getTextRight(str, getTextLength(paramString2)).equals(paramString2)) {
            return getText(paramString2 + str, paramString2, paramString2);
        }
        return getText(paramString2 + str + paramString2, paramString2, paramString2);
    }

    public static String subTextReplace(String paramString1, String paramString2, String paramString3) {
        if (("".equals(paramString2)) || ("".equals(paramString1))) {
            return "";
        }
        return paramString1.replaceAll("\\Q" + paramString2 + "\\E", paramString3);
    }

    public static String getTextCenter(String paramString, int paramInt1, int paramInt2) {
        if (("".equals(paramString)) || (paramInt1 < 0) || (paramInt2 <= 0) || (paramInt1 > paramString.length())) {
            return "";
        }
        int i = paramInt1 + paramInt2;
        paramInt2 = i;
        if (i > paramString.length()) {
            paramInt2 = paramString.length();
        }
        return paramString.substring(paramInt1, paramInt2);
    }

    public static String getTextRight(String paramString, int paramInt) {
        if (("".equals(paramString)) || (paramInt <= 0)) {
            return "";
        }
        return paramString.substring(paramString.length() - paramInt, paramString.length());
    }

    public static String getTextLeft(String paramString, int paramInt) {
        String str;
        if (("".equals(paramString)) || (paramInt <= 0)) {
            return "";
        }
        return paramString.substring(0, paramInt);
    }

    public static int getTextLength(String paramString) {
        return paramString.length();
    }

    public static int getTextLength2(String paramString) {
        return paramString.getBytes().length;
    }
}
