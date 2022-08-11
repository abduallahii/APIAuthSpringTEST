package com.ateamgroup.stoolbe.constant;

public class Authority {

    public static final String[] USER_AUTHORITIES = { "REPORT:READ"  };
    public static final String[] BUSNIESS_AUTHORITIES = { "REPORT:READ" ,  "VIOLATION:READ"  };
    public static final String[] ADMIN_AUTHORITIES = {"USER:READ" ,
            "USER:UPDATE" ,
            "REPORT:READ" ,
            "REPORT:WRITE",
            "VIOLATION:WRITE",
            "VIOLATION:READ",
    } ;




}
