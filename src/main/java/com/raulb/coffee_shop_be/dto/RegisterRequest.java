package com.raulb.coffee_shop_be.dto;


public record RegisterRequest(String firstName, String lastName, String username, String password) {
}
