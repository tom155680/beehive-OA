package com.wyj.beehive.common.exception;


/**
 * @author yongjianWang
 * @date 2023年03月26日 12:42
 */
public class BeehiveException extends RuntimeException{

    private Integer code;
    private String message;

    public BeehiveException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return "BeehiveException{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
