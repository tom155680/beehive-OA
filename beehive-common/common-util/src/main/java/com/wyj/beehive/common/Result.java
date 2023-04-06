package com.wyj.beehive.common;

import lombok.Data;

/**
 * @author yongjianWang
 * @date 2023年03月25日 22:19
 * 统一结果返回类
 */
@Data
public class Result<T> {

    private Integer code; //状态码

    private String message; //返回信息

    private T data;  //返回数据

    //构造私有化
    private Result(){
        //返回成功

    }

    public static<T> Result<T> build(T body,ResultCodeEnum resultCodeEnum){

        Result<T> result = new Result<>();

        if (body!=null){
            result.setData(body);
        }
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());
        return result;
    }

    public static<T> Result<T> build(T body,ResultCodeEnum resultCodeEnum,String ex){

        Result<T> result = new Result<>();

        if (body!=null){
            result.setData(body);
        }
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());
        System.out.println(ex);
        return result;
    }

    public static<T> Result<T> ok(){
        return build(null,ResultCodeEnum.SUCCESS);
    }

    public static<T> Result<T> ok(T data){
        return build(data,ResultCodeEnum.SUCCESS);
    }

    public static<T> Result<T> fail(){
        return build(null,ResultCodeEnum.FAIL);
    }

    public static<T> Result<T> fail(T data){
        return build(data,ResultCodeEnum.FAIL);
    }

    public Result<T> message(String msg){
        this.setMessage(msg);
        return this;
    }

    public Result<T> code(Integer code){
        this.setCode(code);
        return this;
    }
}
