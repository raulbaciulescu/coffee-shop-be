package com.raulb.coffee_shop_be.exception;

public class EntityAlreadyExistsException extends RuntimeException {
    public EntityAlreadyExistsException(String msg) {
        super(msg);
    }
}
