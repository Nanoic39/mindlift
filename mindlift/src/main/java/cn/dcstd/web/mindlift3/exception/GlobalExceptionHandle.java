package cn.dcstd.web.mindlift3.exception;

//GlobalExceptionHandle.java
import cn.dcstd.web.mindlift3.common.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandle {

    @ExceptionHandler(value = {Exception.class})
    @ResponseBody
    public <T> Result handle(Exception e) {
        if(e instanceof CustomException){
            CustomException globalCustomException = (CustomException) e;
            return Result.fail(globalCustomException.getMsg());
        }
        return Result.fail(500, "服务器内部异常："+e.getMessage(), "服务器内部异常");
    }
}
