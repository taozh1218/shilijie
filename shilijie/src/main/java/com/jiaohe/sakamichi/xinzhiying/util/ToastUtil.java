package com.jiaohe.sakamichi.xinzhiying.util;

import okhttp3.MediaType;

/**
 * 常量
 * <p>
 * Created by taozh on 2017/2/14.
 */

public class ToastUtil {

    public static String BASE_URL = "https://www.xinzhiying.net/rest/app";
    public static String SHARED_PREFERENCE = "xinzhiying";
    public static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");

    public static final String GET_VERIFICATION_CODE = "102";


    /**
     * 获取短信验证时，验证发送信息是否合法的返回结果
     *
     * @param resultCode
     * @return result详细信息
     */
    public static String getSendResultByCode(String resultCode) {
        String result;
        switch (resultCode) {
            case "RC100":
                result = "发送成功";
                break;
            case "RC200":
                result = "发送失败";
                break;
            case "RC201":
                result = "参数不完整";
                break;
            case "RC202":
                result = "参数不合法";
                break;
            case "RC212":
                result = "重复请求";
                break;
            case "RC300":
                result = "操作异常";
                break;
            case "RC403":
                result = "服务不可用";
                break;
            default:
                result = null;
        }
        return result;
    }

    /**
     * 短信验证时，验证码是否正确的返回结果
     *
     * @param resultCode
     * @return result详细信息
     */
    public static String getVerificationResultByResultCode(String resultCode) {
        String result;
        switch (resultCode) {
            case "RC100":
                result = "验证成功";
                break;
            case "RC200":
                result = "验证失败";
                break;
            case "RC201":
                result = "参数不完整";
                break;
            case "RC300":
                result = "操作异常";
                break;
            case "RC403":
                result = "服务不可用";
                break;
            case "RC404":
                result = "验证码失效";
                break;
            default:
                result = null;
        }
        return result;
    }

    /**
     * 注册结果
     * <p>
     * {“result”:RC100}注册成功
     * {“result”:RC200}注册失败
     * {“result”:RC201}参数不完整
     * {“result”:RC211}该用户已经存在
     * {“result”:RC213}验证码不正确
     * {“result”:RC300}操作异常
     * {“result”:RC403}服务不可用
     * {“result”:RC404}短信验证码失效
     *
     * @param resultCode
     * @return
     */
    public static String getRegisterResult(String resultCode) {
        String result;
        switch (resultCode) {
            case "RC100":
                result = "注册成功";
                break;
            case "RC200":
                result = "注册失败";
                break;
            case "RC201":
                result = "参数不完整";
                break;
            case "RC211":
                result = "该用户已经存在";
                break;
            case "RC213":
                result = "验证码不正确";
                break;
            case "RC300":
                result = "操作异常";
                break;
            case "RC403":
                result = "服务不可用";
                break;
            case "RC404":
                result = "短信验证码失效";
                break;
            default:
                result = null;
        }
        return result;

    }

    /**
     * {“result”:RC100}登录成功——>登录成功返回token令牌（第一次登陆返回，其他操作均使用缓存的令牌）
     * {“token”:XXXXXXXXXXXXXXXXXX}用于登陆后操作的合法验证
     * {“result”:RC200}登录失败（用户名或密码错误）
     * {“result”:RC201}参数不完整
     * {“result”:RC210}用户被冻结
     * {“result”:RC300}操作异常
     * {“result”:RC403}服务不可用
     *
     * @return
     */
    public static String getLoginResult(String resultCode) {
        String result;
        switch (resultCode) {
            case "RC100":
                result = "登录成功";
                break;
            case "RC200":
                result = "账号或密码错误";
                break;
            case "RC201":
                result = "参数不完整";
                break;
            case "RC210":
                result = "用户被冻结";
                break;
            case "RC300":
                result = "操作异常";
                break;
            case "RC403":
                result = "服务不可用";
                break;
            default:
                result = null;
        }
        return result;
    }


    /**
     * {“result”:RC100}修改成功
     * {“result”:RC200}修改失败
     * {“result”:RC201}参数不完整
     * {“result”:RC213}验证码不正确
     * {“result”:RC300}操作异常
     * {“result”:RC404}短信失效
     * {“result”:RC403}服务不可用
     *
     * @return
     */
    public static String getChangePwdResult(String resultCode) {
        String result;
        switch (resultCode) {
            case "RC100":
                result = "修改成功";
                break;
            case "RC200":
                result = "修改失败";
                break;
            case "RC201":
                result = "参数不完整";
                break;
            case "RC213":
                result = "验证码不正确";
                break;
            case "RC300":
                result = "操作异常";
                break;
            case "RC403":
                result = "服务不可用";
                break;
            case "RC404":
                result = "短信失效";
                break;
            default:
                result = null;
        }
        return result;
    }

    /**
     * {“result”:RC100}编辑成功
     * {“result”:RC200}更新失败
     * {“result”:RC401}登陆失效
     * {“result”:RC201}参数不完整
     * {“result”:RC300}操作异常
     * {“result”:RC403}服务不可用
     *
     * @return
     */
    public static String getChangeUserInfoResult(String resultCode) {
        String result;
        switch (resultCode) {
            case "RC100":
                result = "编辑成功";
                break;
            case "RC200":
                result = "更新失败";
                break;
            case "RC201":
                result = "参数不完整";
                break;

            case "RC300":
                result = "操作异常";
                break;
            case "RC403":
                result = "服务不可用";
                break;
            case "RC401":
                result = "登陆失效";
                break;
            default:
                result = null;
        }
        return result;
    }

}
