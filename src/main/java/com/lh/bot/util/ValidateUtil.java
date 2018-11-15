package com.lh.bot.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * 验证工具类
 * Date: 13-9-26
 * Time: 下午8:28
 */
public class ValidateUtil {

    /**
     * 手机号的正则模式，座机拨打外地的手机需在号码前加0(144号段为刘天王增加)
     * TODO 目前会存在多了一部分，暂时无视！
     */
    private static final Pattern REGEX_MOBILE = Pattern.compile("^0?1(?:3[0-9]|4[579]|5[0-35-9]|7[01235678]|8[012345-9])\\d{8}$");
    /**
     * 验证代理IP的正则模式
     */
    private static final Pattern REGEX_PROXY_IP = Pattern.compile("^\\d+\\.\\d+\\.\\d+\\.\\d+:\\d+$");
    /**
     * 验证银行卡号的正则模式
     */
    private static final Pattern REGEX_BANK_ACCOUNT = Pattern.compile("^\\d*$");
    /**
     * 验证邮箱的正则模式
     */
    private static final Pattern REGEX_EMAIL = Pattern.compile("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+$");

    /**
     * 验证字符是否全是数字的正则模式
     */
    private static final Pattern WHOLE_ARE_NUMBER = Pattern.compile("[0-9]*");

    // 允许上传的格式
    private static final String[] IMAGE_TYPE = new String[] { ".bmp", ".jpg", ".jpeg", ".gif", ".png", ".pcx", ".tiff",
            ".BMP", ".JPEG", ".GIF", ".PNG", ".PSD", ".PCX", ".DXF", ".CDR", ".ICO", ".TIFF", ".SWF", ".SVG", ".EMF", ".LIC",
            ".EPS", ".TGA"};

    /**
     * 验证手机格式是否正确。非空+长度11+前缀验证
     *
     * @param mobile 手机
     * @return 格式是否正确
     */
    public static boolean validMobile(String mobile) {
        return !StringUtils.isEmpty(mobile)
                && mobile.length() == 11
                && REGEX_MOBILE.matcher(mobile).matches();
    }

    /**
     * 验证代理IP格式是否正确。非空+符合xxx.xx.xxx.xx:xx
     *
     * @param ip 代理IP
     * @return 格式是否正确
     */
    public static boolean validProxyIp(String ip) {
        return org.springframework.util.StringUtils.hasText(ip)
                && REGEX_PROXY_IP.matcher(ip).matches();
    }

    /**
     * 验证银行卡号格式是否正确。非空+长度19+格式验证
     *
     * @param account 银行卡号
     * @return 格式是否正确
     */
    public static boolean validBankAccount(String account) {
        return !StringUtils.isEmpty(account) && REGEX_BANK_ACCOUNT.matcher(account).matches();
    }

    /**
     * 验证邮箱账号格式是否正确。非空+格式验证
     *
     * @param email 银行卡号
     * @return 格式是否正确
     */
    public static boolean validEmail(String email) {
        return !StringUtils.isEmpty(email) && REGEX_EMAIL.matcher(email).matches();
    }


    /**
     * 判断字符串是否全是数字
     * @param str
     * @return
     */
    public static boolean validNumber(String str) {
        return WHOLE_ARE_NUMBER.matcher(str).matches();
    }
}
