package com.jiaohe.sakamichi.xinzhiying.util;

import java.util.regex.Pattern;

/**
 * Created by sakamichi on 2017/2/9.
 */

public class RegexUtils {
    /**
     * @param password 用户填写的密码
     * @return 格式正确返回true
     * 校验用户输入的密码格式是否合法
     */
    public static boolean checkPW(String password) {
        String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[a-zA-Z0-9]{6,18}";
        return Pattern.matches(regex, password);
    }


    /**
     * @param phoneNum 用户输入的手机号码
     * @return 格式正确返回true
     * <p>
     * 校验用户输入的手机号码格式是否合法
     */
    public static boolean checkNum(String phoneNum) {
        String regex = "^[1][3,4,5,7,8][0-9]{9}$";
        return Pattern.matches(regex, phoneNum);
    }

}
