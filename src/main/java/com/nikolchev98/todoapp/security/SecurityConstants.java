package com.nikolchev98.todoapp.security;

public enum SecurityConstants {
    ;
    public static final long JWT_EXPIRATION = 70000;
    public static final String JWT_SECRET = "secret";
    public static byte[] SECRET_KEY_BYTES = JWT_SECRET.getBytes();
}
