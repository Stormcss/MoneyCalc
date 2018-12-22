package ru.strcss.projects.moneycalc.moneycalcserver.configuration.security;

public class SecurityConstants {
    public static final String SECRET = "extremelySecretKeyToGenJWTs";
    public static final long EXPIRATION_TIME = 864_000_000; // 10 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/api/registration/register";
}