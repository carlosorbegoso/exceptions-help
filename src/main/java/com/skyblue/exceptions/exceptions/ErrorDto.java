package com.skyblue.exceptions.exceptions;

public record ErrorDto (
        int code,
        String reason,
        String message){

}
