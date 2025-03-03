package cn.dcstd.web.mindlift3.common;

import lombok.Data;

/**
 * @author NaNo1c
 */
@Data
public class Result<T> {
    //统一返回值
    private int code;
    private String msg;
    private String tips;
    private T data = null;

    public Result(int code, String msg) {
        this.code = code;
        this.msg = "[" + System.currentTimeMillis() + "] -> " + msg;
        this.tips = null;
        this.data = null;
    }

    public Result(int code, String msg, String tips) {
        this.code = code;
        this.msg = "[" + System.currentTimeMillis() + "] -> " + msg;
        this.tips = tips;
        this.data = null;
    }

    public Result(int code, String msg, T data) {
        this.code = code;
        this.msg = "[" + System.currentTimeMillis() + "] -> " + msg;
        this.tips = null;
        this.data = data;
    }

    public Result(int code, String msg, String tips, T data) {
        this.code = code;
        this.msg = "[" + System.currentTimeMillis() + "] -> " + msg;
        this.tips = tips;
        this.data = data;
    }


    public static Result success() {
        Result result = new Result(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), null);
        return result;
    }

    public static Result success(String tips) {
        return new Result(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), tips);
    }

    public static <T> Result<T> success(String msg, T data) {
        return new Result(ResultEnum.SUCCESS.getCode(), msg, null, data);
    }

    public static Result success(String msg, String tips) {
        return new Result(ResultEnum.SUCCESS.getCode(), msg, tips);
    }

    public static <T> Result<T> success(String msg, String tips, T data) {
        return new Result(ResultEnum.SUCCESS.getCode(), msg, tips, data);
    }

    public static <T> Result<T> success(T data) {
        return new Result(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), data);
    }

    public static Result fail() {
        return new Result(ResultEnum.ERROR.getCode(), ResultEnum.ERROR.getMsg());
    }

    public static Result fail(String msg) {
        return new Result(ResultEnum.ERROR.getCode(), msg);
    }

    public static Result fail(int code, String msg) {
        return new Result(code, msg);
    }

    public static Result fail(String msg, String tips) {
        return new Result(ResultEnum.ERROR.getCode(), msg, tips);
    }

    public static Result fail(int code, String msg, String tips) {
        return new Result(code, msg, tips);
    }

    public static <T> Result<T> pause(T data) {
        return new Result(114514, "[正常中间状态]", data);
    }

}
