package com.skyblue.exceptions.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.SQLIntegrityConstraintViolationException;

@Slf4j
@ControllerAdvice
public class ExampleExceptionHandler {

    private static final int FIRST_FIELD = 0;

    @Value("${delivery.debug}")
    private boolean showStackTrace;

    @ExceptionHandler(
            value = {
                    SQLIntegrityConstraintViolationException.class,
                    MethodArgumentTypeMismatchException.class,
                    MissingServletRequestParameterException.class,
                    InvalidFormatException.class
            })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ResponseEntity<ErrorDto> handleBadRequestException(Exception exception) {
        if (showStackTrace) {
            exception.printStackTrace();
        }

        ErrorDto errorDto = buildErrorDtoFromException(exception, HttpStatus.BAD_REQUEST);
        logErrorMessage(errorDto);
        return ResponseEntity.badRequest().body(errorDto);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ResponseEntity<ErrorDto> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception) {
        if (showStackTrace) {
            exception.printStackTrace();
        }
        ObjectError error = exception.getBindingResult().getAllErrors().get(FIRST_FIELD);
        ErrorDto errorDto =
                new ErrorDto(
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        error.toString());
        logErrorMessage(errorDto);
        return ResponseEntity.badRequest().body(errorDto);
    }

    @ExceptionHandler(value = HttpServerErrorException.class)
    @ResponseBody
    ResponseEntity<ErrorDto> handleHttpServerErrorException(HttpServerErrorException exception) {
        if (showStackTrace) {
            exception.printStackTrace();
        }
        ErrorDto errorDto = buildErrorDtoFromException( exception, (HttpStatus) exception.getStatusCode());
        logErrorMessage(errorDto);
        return ResponseEntity.status(exception.getStatusCode()).body(errorDto);
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    ResponseEntity<ErrorDto> genericException(Exception exception) {
        if (showStackTrace) {
            exception.printStackTrace();
        }
        ErrorDto errorDto = buildErrorDtoFromException(exception, HttpStatus.INTERNAL_SERVER_ERROR);
        logErrorMessage(errorDto);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDto);
    }

    private void logErrorMessage(ErrorDto errorDto) {
        log.error("logErrorMessage", errorDto.message());
    }
    private ErrorDto buildErrorDtoFromException(Exception exception, HttpStatus httpStatus) {
        return new ErrorDto(httpStatus.value(), httpStatus.getReasonPhrase(), exception.getMessage());
    }
}
