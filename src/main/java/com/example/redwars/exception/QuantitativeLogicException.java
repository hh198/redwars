package com.example.redwars.exception;

import lombok.Data;

/**
 * 这是一个用来处理数量逻辑异常的类
 */
@Data
public class QuantitativeLogicException extends RuntimeException {
    private Integer code;

    private String msg;

    public QuantitativeLogicException(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public QuantitativeLogicException(String msg) {
        this.msg = msg;
    }

}
