package cn.dcstd.web.mindlift3.common;

import lombok.Getter;

/**
 * @author NaNo1c
 */

@Getter
public enum ResultEnum {
    SUCCESS(200, "成功"),
    ERROR(500, "失败");

    private int code;
    private String msg;

    ResultEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
