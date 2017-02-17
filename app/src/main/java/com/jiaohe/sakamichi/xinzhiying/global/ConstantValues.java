package com.jiaohe.sakamichi.xinzhiying.global;

/**
 * Created by sakamichi on 16/9/14.
 */
public class ConstantValues {

    /**
     * sp文件名称的常量
     */
    public static final String CONFIG = "config";

    /**
     * 是否完成新手指引的key
     */
    //public static final String IS_GUIDE_OVER = "is_guide_over";

    /**
     * 服务器地址常量
     */
    //public static final String SERVER_URL = "https://www.xinzhiying.net/rest/app";
    public static final String SERVER_URL = "http://zss1993.vicp.io/rest/app";

    /**
     * 用户登录地址
     */
    public static final String LOGIN_URL = SERVER_URL + "/userLogin";

    /**
     * 用户注册地址
     */
    public static final String REG_URL = SERVER_URL + "/userReg";

    /**
     * 找回密码地址
     */
    public static final String FIND_PW_URL = SERVER_URL + "/forgetPass";

    /**
     * 获取短信验证码地址
     */
    public static final String CERT_URL = SERVER_URL + "/getSmsCode";

    /**
     * 校验验证码是否正确地址
     */
    public static final String CHECK_CERT_URL = SERVER_URL + "/isSmsCode";

    /**
     * 校验token是否有效地址
     */
    public static final String CHECK_TOKEN_URL = SERVER_URL + "/isUserState";
    
    /**
     * 获取sts凭证的url
     */
    public static final String GET_STS_URL = SERVER_URL + "/getOssSts";

}
