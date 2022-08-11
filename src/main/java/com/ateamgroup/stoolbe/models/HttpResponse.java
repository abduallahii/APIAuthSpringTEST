package com.ateamgroup.stoolbe.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import java.util.Date;


@NoArgsConstructor
@Getter
@Setter
public class HttpResponse {

    @JsonFormat(shape =  JsonFormat.Shape.STRING , pattern = "MM-dd-yyyy hh:mm:ss" ,timezone = "Egypt/Cairo")
    private Date timeStamp ;
    private int httpStatusCode;
    private HttpStatus httpStatus;
    private String Reason ;
    private String message;

    public HttpResponse(int httpStatusCode, HttpStatus httpStatus, String reason, String message) {
        this.timeStamp = new Date() ;
        this.httpStatusCode = httpStatusCode;
        this.httpStatus = httpStatus;
        Reason = reason;
        this.message = message;
    }
}
