package com.promineotech.jeep.errorhandler;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice                                                              // Annotation to allow consolidation of error handling 
@Slf4j
public class GlobalErrorHandler {
  
  private enum LogStatus {STACK_TRACE, MESSAGE_ONLY}
  
  
  @ExceptionHandler(ConstraintViolationException.class)                             // Annotation to specify handling for Constraint Violation Exceptions
  @ResponseStatus(code = HttpStatus.BAD_REQUEST)                                    // Annotation to specify handling for 400 error (Bad Request)
  public Map<String, Object> handleConstraintViolationException(ConstraintViolationException e, WebRequest webRequest) {
    
    return createExceptionMessage(e, HttpStatus.BAD_REQUEST, webRequest, LogStatus.MESSAGE_ONLY);
    
  }
  
  @ExceptionHandler(NoSuchElementException.class)                                   // Annotation to specify handling for NoSuchElementExcetpions
  @ResponseStatus(code = HttpStatus.NOT_FOUND)                                      // Annotation to specify handling for 404 error
  public Map<String, Object> handleNoSuchElementException(NoSuchElementException e, WebRequest webRequest) {
    
    return createExceptionMessage(e, HttpStatus.NOT_FOUND, webRequest, LogStatus.MESSAGE_ONLY);
    
  }

  
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)                      // Annotation to specify handling for values not in enum list
  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, WebRequest webRequest) {
    
    return createExceptionMessage(e, HttpStatus.BAD_REQUEST, webRequest, LogStatus.MESSAGE_ONLY);
    
  }
  
  @ExceptionHandler(Exception.class)                                                // Annotation to specify handling for generic/unplanned errors
  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  public Map<String, Object> handleException(Exception e, WebRequest webRequest) {
    
    return createExceptionMessage(e, HttpStatus.INTERNAL_SERVER_ERROR, webRequest, LogStatus.STACK_TRACE);
    
  }
  
  
  // Method to customize capturing details for error messages
  private Map<String, Object> createExceptionMessage(Exception e, HttpStatus status, WebRequest webRequest, LogStatus logStatus) {
    
    Map<String, Object> error = new HashMap<>();
    String timestamp = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);        // Capture current date/time for timestamp
    
    if(webRequest instanceof ServletWebRequest) {                                   // uri for web request
      error.put("uri", ((ServletWebRequest)webRequest).getRequest().getRequestURI());
    }
    
    error.put("message", e.toString());                                             // Exception message
    error.put("status code", status.value());                                       // Error message status (404)
    error.put("timestamp", timestamp);                                              // Timestamp .now()
    error.put("reason", status.getReasonPhrase());                                  // Phrase from HttpStatus error message
    
    if(logStatus == LogStatus.MESSAGE_ONLY) {
      
      log.error("Exception: {}", e.toString());
    } else {
      log.error("Exception: ", e);
    }
    
    return error;

  }

}
