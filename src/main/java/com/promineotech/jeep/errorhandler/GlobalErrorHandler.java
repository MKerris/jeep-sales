package com.promineotech.jeep.errorhandler;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice                                                               // Annotation to allow consolidation of error handling 
public class GlobalErrorHandler {
  
  @ExceptionHandler(NoSuchElementException.class)                                   // Annotation to specify handling for NoSuchElementExcetpions
  @ResponseStatus(code = HttpStatus.NOT_FOUND)                                      // Annotation to specify handling for 404 error
  public Map<String, Object> handleNoSuchElementException(NoSuchElementException e, WebRequest webRequest) {
    
    return createExceptionMessage(e, HttpStatus.NOT_FOUND, webRequest);             // Customize error message below
    
  }

  private Map<String, Object> createExceptionMessage(NoSuchElementException e, HttpStatus status, WebRequest webRequest) {
    
    Map<String, Object> error = new HashMap<>();
    String timestamp = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);        // Capture current date/time for timestamp
    
    if(webRequest instanceof ServletWebRequest) {                                   // uri for web request
      error.put("uri", ((ServletWebRequest)webRequest).getRequest().getRequestURI());
    }
    
    error.put("message", e.toString());                                             // Exception message
    error.put("status code", status.value());                                       // Error message status (404)
    error.put("timestamp", timestamp);                                              // Timestamp .now()
    error.put("reason", status.getReasonPhrase());                                  // Phrase from HttpStatus error message
    
    return error;

  }

}
