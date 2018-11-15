package com.lh.bot.util;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 * Date: 13-11-5
 * Time: 下午12:25
 */
public class StringUtil {

    /**
     * CJK中文汉字正则表达式
     */
    public static final String CHINESE_CJK_REG = "[\\u4E00-\\u9FBF]+";

    private final static int[] li_SecPosValue = {1601, 1637, 1833, 2078, 2274,
            2302, 2433, 2594, 2787, 3106, 3212, 3472, 3635, 3722, 3730, 3858,
            4027, 4086, 4390, 4558, 4684, 4925, 5249, 5590};
    private final static String[] lc_FirstLetter = {"a", "b", "c", "d", "e",
            "f", "g", "h", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "w", "x", "y", "z"};
    private final static String[] special_character = {"\\", " ", "！", "@", "#", "￥", "%", "…", "[", "]", "$",  ".", "|",
            "&", "*", "（", "）", "(", ")", };

    /**
     * 将指定字符串从源编码转到新编码
     *
     * @param text           字符串
     * @param charsetName    原编码
     * @param newCharsetName 新编码
     * @return 新字符串
     */
    public static String newString(String text, String charsetName, String newCharsetName) {
        if (text == null) {
            return null;
        }
        try {
            return new String(text.getBytes(charsetName), newCharsetName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 检测是否有emoji字符
     *
     * @param source 字符串
     * @return 一旦含有就抛出
     */
    public static boolean containsEmoji(String source) {
        if (StringUtils.isBlank(source)) {
            return false;
        }
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (isEmojiCharacter(codePoint)) { //do nothing，判断到了这里表明，确认有表情字符
                return true;
            }
        }
        return false;
    }

    /**
     * 检测字符是不是Emoji
     *
     * @param codePoint 字符
     * @return 是否
     */
    public static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) ||
                (codePoint == 0x9) ||
                (codePoint == 0xA) ||
                (codePoint == 0xD) ||
                ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
                ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }

    /**
     * 过滤emoji 或者 其他非文字类型的字符
     *
     * @param source 字符串
     * @return 过滤后的字符串
     */
    public static String filterEmoji(String source) {
        if (!containsEmoji(source)) {
            return source;//如果不包含，直接返回
        }
        //到这里铁定包含
        StringBuilder buf = null;
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (isEmojiCharacter(codePoint)) {
                if (buf == null) {
                    buf = new StringBuilder(source.length());
                }

                buf.append(codePoint);
            }
        }
        if (buf == null) {
            return source;//如果没有找到 emoji表情，则返回源字符串
        } else {
            if (buf.length() == len) {//这里的意义在于尽可能少的toString，因为会重新生成字符串
                return source;
            } else {
                return buf.toString();
            }
        }
    }

    /**
     * 截取字符串
     *
     * @param content  要截取的内容
     * @param offset   起始index
     * @param maxWidth 截取的长度
     * @return 截取后的内容
     */
    public static String abbreviate(String content, int offset, int maxWidth) {
        return StringUtils.abbreviate(content, offset, maxWidth);
    }

    /**
     * 截取字符串
     *
     * @param content  要截取的内容
     * @param maxWidth 截取的长度
     * @return 截取后的内容
     */
    public static String abbreviate(String content, int maxWidth) {
        return StringUtils.abbreviate(content, maxWidth);
    }

    /**
     * 字符串是否为空
     * 默认情况下使用这个
     * 当需要空格也过滤掉时，调用方法isEmpty("字符串", true);
     *
     * @param str 字符串
     * @return 是否为空
     */
    public static boolean isEmpty(CharSequence str) {
        return isEmpty(str, false);
    }

    /**
     * 字符串是否为空
     *
     * @param str              字符串
     * @param ignoreWhitespace 是否认为空格不算字符。比如, "   "也算空串
     * @return 是否空串
     */
    public static boolean isEmpty(CharSequence str, boolean ignoreWhitespace) {
        return ignoreWhitespace ? !org.springframework.util.StringUtils.hasText(str)
                : StringUtils.isEmpty(str);
    }

    /**
     * 字符串是否不为空
     * 默认情况下使用这个
     * 当需要空格也过滤掉时，调用方法isEmpty("字符串", true);
     *
     * @return
     */
    public static boolean notEmpty(CharSequence str) {
        return !isEmpty(str, false);
    }

    /**
     * 过滤正则特殊符号<br />
     * TODO: 目前仅支持()[]{}，后期可考虑过滤更多的特殊字符
     *
     * @param content
     * @return
     */
    public static String filterRegex(String content) {
        if (isEmpty(content)) {
            return "";
        }
        return content.replaceAll("\\(", "(")
                .replaceAll("\\)", ")")
                .replaceAll("\\?", "?")
                .replaceAll("\\*", "[*]")
                .replaceAll("\\+", "+");
    }

    /**
     * 特殊字符转换 "\\"首先替换
     *
     * @param str
     */
    public static String convertRegex(String str) {
        if(!isEmpty(str)){
            for(String s : special_character) {
                if (str.contains(s)) {
                    str = str.replace(s, "\\" + s);
                }
            }
        }
        return str;
    }

    /**
     * 校验字符是否为中文字符<br />
     * 1.支持检验：中文汉字<br />
     * 2.支持检验：中文符号
     *
     * @param c 字符
     * @return turn - 是中文字符
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION;
    }

    /**
     * 校验字符串是否为中文字符串<br />
     * 1.支持检验：中文汉字<br />
     * 2.支持检验：中文符号
     *
     * @param str 字符串
     * @return TRUE - 该字符串包含中文汉字及符号
     */
    public static boolean isChinese(String str) {
        char[] ch = str.toCharArray();
        for (char c : ch) {
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取字符串中的中文字符
     *
     * @param str
     * @return
     */
    public static String getStringChinese(String str) {
        StringBuffer sb = new StringBuffer();
        char[] ch = str.toCharArray();
        for (char c : ch) {
            if (isChinese(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 校验字符串是否为中文字符串<br />
     * 1.校验部分CJK字符<br />
     * 2.cjk代号意思是：汉语（Chinese）、日语（Japanese）、韩语（Korean）<br />
     * 3.支持校验：中文汉字<br />
     * 4.不支持校验：中文符号
     *
     * @param str 字符串
     * @return
     */
    public static boolean isChineseByREG(String str) {
        if (str == null) {
            return false;
        }
        Pattern pattern = Pattern.compile(CHINESE_CJK_REG);
        return pattern.matcher(str.trim()).find();
    }


    /**
     * 加密手机号<br/>
     * 注：只加密手机号中间4位
     *
     * @param mobile 手机号
     * @param c      指定字符
     * @return
     */
    public static String encryptMobile(String mobile, char c) {
        if (ValidateUtil.validMobile(mobile)) {
            String str = "";
            for (int i = 0; i < 4; i++) {
                str = str + c;
            }
            return mobile.substring(0, 3) + str + mobile.substring(7, mobile.length());
        } else {
            return "";
        }
    }

    /**
     * 对医生端字体加色
     *
     * @param content
     * @return
     */
    public static String htmlFontStyleColor(String content, String fontSize, String color) {
        if (notEmpty(content)) {
            StringBuffer buffer = new StringBuffer("<font ");
            if (notEmpty(fontSize)) {
                buffer.append(" size=" + fontSize);
            }
            if (notEmpty(color)) {
                buffer.append(" color='" + color + "'");
            }
            buffer.append(">" + content + "</font>");
            return buffer.toString();
        }
        return content;
    }

    /**
     * 对用户端字体加色
     *
     * @param content
     * @return
     */
    public static String htmlFontStyleColorForUser(String content, String fontSize, String color) {
        if (notEmpty(content)) {
            StringBuffer buffer = new StringBuffer("<font> <u");
            if (notEmpty(fontSize)) {
                buffer.append(" size=" + fontSize);
            }
            if (notEmpty(color)) {
                buffer.append(" color='" + color + "'");
            }
            buffer.append(">" + content + "</u></font>");
            return buffer.toString();
        }
        return content;
    }

    /**
     * 将内容格式化为html标签
     *
     * @param content     内容
     * @param label       标签名-->html标签名
     * @param propertyMap 标签属性键值对
     * @return
     */
    public static String formatHtmlLabel(String content, String label, LinkedHashMap<String, String> propertyMap) {
        if (notEmpty(content)) {
            StringBuffer buffer = new StringBuffer("<" + label + " ");
            Set<String> propertyNames = propertyMap.keySet();
            for (String propertyName : propertyNames) {
                buffer.append(propertyName + "='" + propertyMap.get(propertyName) + "' ");
            }
            buffer.append(">" + content + "</" + label + ">");
            return buffer.toString();
        }
        return content;
    }

    /**
     * 对小时格式化
     *
     * @param hour
     * @return
     */
    public static String formatHour(Integer hour) {
        if (hour < 0 || hour > 23) {
            return "";
        }
        StringBuffer timeStr = new StringBuffer(hour + ":00--");
        if (23 == hour) {
            timeStr.append("00:00");
        } else {
            timeStr.append((++hour) + ":00");
        }
        return timeStr.toString();
    }

    /**
     * 类似String.valueOf, 但当参数为null时, 返回空白字符串, 而不是null字符串
     * 多在excel数据导出时使用
     */
    public static String valueOf(Object obj) {
        return (null == obj) ? "" : obj.toString();
    }

    /**
     * 随机生成多少位字符
     *
     * @param _length
     * @return
     */
    public static String randomStr(int _length) {
        if (_length <= 0) {
            return "";
        }
        final Random r = new Random();
        final char[] arr = new char[62];
        for (int q = 0; q < 26; q++) {
            arr[q] = (char) ('A' + q);
        }
        for (int q = 26; q < 52; q++) {
            arr[q] = (char) ('a' + q - 26);
        }
        for (int q = 52; q < arr.length; q++) {
            arr[q] = (char) ('0' + q - 52);
        }
        String str = "";
        for (int i = 0; i < _length; i++) {
            int index;
            do {
                final int rnd = r.nextInt();
                index = (rnd < 0 ? -rnd : rnd) % arr.length;
                //
                // 如果碰到'\0'证明取到了上次相同的元素，应当重新取，直到不重复
            } while (arr[index] == '\0');
            str += arr[index];
            //
            // 使用过一次设定的'\0'，下次就不能用了
            arr[index] = '\0';
        }
        return str;
    }

    /**
     * 获取字节长度
     *
     * @param str
     * @return
     */
    public static int getByteLength(String str) {
        if (isEmpty(str)) {
            return 0;
        }

        int len = 0;
        for (int i = 0; i < str.length(); i++) {
            int ascii = Character.codePointAt(str, i);
            if (ascii >= 0 && ascii <= 255) {
                len++;
            } else {
                len += 2;
            }
        }
        return len;
    }

    /**
     * 取得给定汉字串的首字母串,即声母串
     *
     * @param str 给定汉字串
     * @return 声母串
     */
    public static String getAllFirstLetter(String str) {
        if (str == null || str.trim().length() == 0) {
            return "";
        }

        String _str = "";
        for (int i = 0; i < str.length(); i++) {
            _str = _str + getFirstLetter(str.substring(i, i + 1));
        }

        return _str;
    }

    /**
     * 取得给定汉字的首字母,即声母
     *
     * @param chinese 给定的汉字
     * @return 给定汉字的声母
     */
    public static String getFirstLetter(String chinese) {
        if (chinese == null || chinese.trim().length() == 0) {
            return "";
        }
        chinese = conversionStr(chinese, "GB2312", "ISO8859-1");

        if (chinese.length() > 1) // 判断是不是汉字
        {
            int li_SectorCode = (int) chinese.charAt(0); // 汉字区码
            int li_PositionCode = (int) chinese.charAt(1); // 汉字位码
            li_SectorCode = li_SectorCode - 160;
            li_PositionCode = li_PositionCode - 160;
            int li_SecPosCode = li_SectorCode * 100 + li_PositionCode; // 汉字区位码
            if (li_SecPosCode > 1600 && li_SecPosCode < 5590) {
                for (int i = 0; i < 23; i++) {
                    if (li_SecPosCode >= li_SecPosValue[i]
                            && li_SecPosCode < li_SecPosValue[i + 1]) {
                        chinese = lc_FirstLetter[i];
                        break;
                    }
                }
            } else // 非汉字字符,如图形符号或ASCII码
            {
                chinese = conversionStr(chinese, "ISO8859-1", "GB2312");
                chinese = chinese.substring(0, 1);
            }
        }

        return chinese;
    }

    /**
     * 字符串编码转换
     *
     * @param str           要转换编码的字符串
     * @param charsetName   原来的编码
     * @param toCharsetName 转换后的编码
     * @return 经过编码转换后的字符串
     */
    private static String conversionStr(String str, String charsetName, String toCharsetName) {
        try {
            str = new String(str.getBytes(charsetName), toCharsetName);
        } catch (UnsupportedEncodingException ex) {
            System.out.println("字符串编码转换异常：" + ex.getMessage());
        }
        return str;
    }

    public static String format(String content, Map<String, Object> dataJson) {
        if (dataJson == null || dataJson.size() == 0) {
            return content;
        }
        String resultContent = new String(content);
        for (String key : dataJson.keySet()) {
            if (!resultContent.contains("{" + key + "}")) {
                continue;
            }
            resultContent = resultContent.replaceAll("\\{" + key + "\\}", String.valueOf(dataJson.get(key)));
        }
        return resultContent;
    }

    public static String formatSmsContent(String content, Map<String, Object> dataJson) {
        if (dataJson == null || dataJson.size() == 0) {
            return content;
        }
        String resultContent = new String(content);
        for (String key : dataJson.keySet()) {
            if (!resultContent.contains("${" + key + "}")) {
                continue;
            }
            resultContent = resultContent.replaceAll("\\$\\{" + key + "\\}", String.valueOf(dataJson.get(key)));
        }
        return resultContent;
    }

    /**
     * 产生随机字符串，长度为16
     *
     * @return 字符串
     */
    public static String random() {
        return randomStr(16);
    }

    /**
     * 产生随机IP
     * 目前该方法生成的IP不够严谨，业务上尽量避免使用。主要用于单元测试。
     *
     * @return IP
     */
    public static String randomIP() {
        String ip = "";
        for (int i = 0; i < 4; i++) {
            if (i > 0) {
                ip += ".";
            }
            ip += MathUtil.random(256);
        }
        return ip;
    }

    /**
     * 产生随机UA
     * 目前该方法生成的UA不够严谨，业务上尽量避免使用。主要用于单元测试。
     *
     * @return UA
     */
    public static String randomUA() {
        return "";
    }

    /**
     * 判断字符串中是否全是数字
     *
     * @param str
     * @return
     */
    public static boolean isAllNUmber(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 截取ip ，取第一段
     * @param ip 格式xx.xx.xx.xx,xx.xx.xx.xx
     * @return
     */
    public static String subStringIp(String ip) {
        if (ip == null) {
            return ip;
        }
        if (ip.indexOf(",") == -1) {
            return ip;
        }
        ip = ip.substring(0, ip.indexOf(","));
        return ip;
    }


    public static String chineseSpaceReplaceEnglishSpace(String content) {
        if (isEmpty(content)) {
            return content;
        }
        return content.replaceAll(" "/*\u2005*/, " "/*\u0020*/);
    }

    /**
     * 截取后lastNum位字符串
     * @param mobile 截取字符串
     * @param lastNum 截取位数
     * @return
     */
    public static String subMobileLast(String mobile, Integer lastNum) {
        if (StringUtil.notEmpty(mobile)) {
            int length = mobile.length();
            if (length > lastNum) {
                return mobile.substring(length - lastNum, length);
            }
            return mobile;
        }
        return "";
    }
}
