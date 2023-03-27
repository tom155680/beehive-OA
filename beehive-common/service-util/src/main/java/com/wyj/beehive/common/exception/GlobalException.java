package com.wyj.beehive.common.exception;

import com.wyj.beehive.common.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author yongjianWang
 * @date 2023年03月26日 12:35
 * 全局异常处理方法
 */
@ControllerAdvice
public class GlobalException {

    /**
     * 全局异常处理
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e){
        e.printStackTrace();
        return Result.fail().message("全局方法执行异常");
    }

    /**
     * 特定异常处理
     */
    @ExceptionHandler(ArithmeticException.class)
    @ResponseBody
    public Result error(ArithmeticException e){
        e.printStackTrace();
        return Result.fail().message("ArithmeticException执行异常");
    }

    /**
     * 自定义异常处理
     */
    @ExceptionHandler(BeehiveException.class)
    @ResponseBody
    public Result error(BeehiveException e){
        e.printStackTrace();
        return Result.fail().message("BeehiveException执行异常");
    }

}
