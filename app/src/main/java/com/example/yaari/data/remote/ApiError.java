package com.example.yaari.data.remote;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

import retrofit2.HttpException;

public class ApiError {
    public static class ErrorMessage{
        public String message;
        public int status;

        public ErrorMessage(String message, int status) {
            this.message = message;
            this.status = status;
        }
    }
    public static ErrorMessage getErrorFromException(Exception e){
        return new ErrorMessage(e.getMessage(),e.hashCode());
    }
    public static ErrorMessage getErrorFromThrowable(Throwable t){
        if(t instanceof HttpException){
            return new ErrorMessage(t.getMessage(),((HttpException) t).code());
        }else if(t instanceof SocketTimeoutException){
            return new ErrorMessage("Timeout",0);
        }else if(t instanceof IOException){
            if(t instanceof MalformedURLException){
                return new ErrorMessage("Malformed Exception",0);

            }else if(t instanceof ConnectException){
                return  new ErrorMessage(t.getMessage()+"your xamp is not running/you have different IP address",0);
            }
            else{
                return new ErrorMessage("No Internet CONNECTION",0);
            }

        }else{
            return new ErrorMessage("UNKNOWN ERROR",0);
        }
    }
}
