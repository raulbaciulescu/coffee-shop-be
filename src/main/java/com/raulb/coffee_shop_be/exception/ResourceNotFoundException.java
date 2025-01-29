package com.raulb.coffee_shop_be.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String s) {
        super(s);
    }
}
