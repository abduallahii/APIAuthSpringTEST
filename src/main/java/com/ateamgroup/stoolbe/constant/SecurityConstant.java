package com.ateamgroup.stoolbe.constant;

public class SecurityConstant {

    public static final long EXPIRATION_TIME = 432_000_000 ; // 5 days in millie seconds
    public static final String TOKEN_PREFIX = "Bearer " ;
    public static final String JWT_TOKEN_HEADER = "Jwt-Token" ;
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token Cannot be verified" ;
    public static final String ETISALAT_CO_LLC ="Etisalat EG, LLC" ;
    public static final String ETISALAT_CO_ADMINISTRATION = "User Management Portal" ;
    public static final String AUTHORITIES = "Authorities" ;
    public static final String FORBIDDEN_MESSAGE = "You need to log in to access this page" ;
    public static final String ACCESS_DENIED_MESSAGE = "You do not have permission to access this page";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS" ;
    public static final String[] PUBLIC_URLS = { "/user/login" , "/user/register" , "/user/resetPassword/**" , "/user/home"} ;

}
