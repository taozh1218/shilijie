package com.jiaohe.sakamichi.xinzhiying.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by sakam on 2016/6/28.
 */
public class Md5Utils {

    /**
     * @param pw 需要加密的原始密码字符串
     */
    public static String encode(String pw) {
        try {
            //0.给原始密码加盐
            //pw = pw + "sakamichi9158@gmail.com";
            //1.指定加密算法类型
            MessageDigest digest = MessageDigest.getInstance("MD5");
            //2.将字符串转换为byte数组并随机hash
            byte[] bs = digest.digest(pw.getBytes());
            //3.遍历数组并生成32位长度字符串(固定写法)
            //4.字符串拼接
            StringBuffer stringBuffer = new StringBuffer();
            for (byte b : bs) {
                int i = b & 0xff;
                //int类型的i转换为十六进制字符串
                String hexString = Integer.toHexString(i);
                //小于2位的位数前面补0
                if (hexString.length() < 2) {
                    hexString = "0" + hexString;
                }
                stringBuffer.append(hexString);
            }
            return stringBuffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return pw;
    }
}
