package com.promineotech.jeep.controller;

import java.util.List;
import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.promineotech.jeep.Constants;
import com.promineotech.jeep.entity.Jeep;
import com.promineotech.jeep.entity.JeepModel;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;

@Validated                                                              // ?
@RequestMapping("/jeeps")                                               // Any request coming to /jeeps will be mapped to this class
@OpenAPIDefinition(info = @Info(title = "Jeep Sales Service"), 
  servers = {@Server(url = "http://localhost:8080", description = "Local server")})
public interface JeepSalesController {

  // @formatter:off
  @Operation(
      summary = "Returns a list of Jeeps",
      
      description = "Returns a list of Jeeps given an optional model and/or trim",
      
      responses = {
          @ApiResponse(
              responseCode = "200",                                     // 200 = OK
              description = "A list of Jeeps is returned.",
              content = @Content(
                  mediaType = "application/json", 
                  schema = @Schema(implementation = Jeep.class))), 
          @ApiResponse(
              responseCode = "400",                                     // 400 = Bad input/request
              description = "Invalid request parameters.",
              content = @Content(mediaType = "application/json")),
          @ApiResponse(
              responseCode = "404",                                     // 404 = Not found
              description = "No Jeeps were found with input criteria.",
              content = @Content(mediaType = "application/json")),
          @ApiResponse(
              responseCode = "500",                                     // 500 = Unplanned exception
              description = "An unplanned error occurred.",
              content = @Content(mediaType = "application/json"))
      },

      parameters = {
          @Parameter(
              name = "model", 
              allowEmptyValue = false, 
              required = false, 
              description = "The model name (i.e. 'WRANGLER')"),
          @Parameter(
              name = "trim", 
              allowEmptyValue = false, 
              required = false, 
              description = "The trim level (i.e. 'Sport')")
      }
      
  )

  @GetMapping                                           // Spring will map GET requests at /jeeps to the fetchJeeps method
  @ResponseStatus(code = HttpStatus.OK)
    List<Jeep> fetchJeeps(
      @RequestParam(required = false) JeepModel model, 

      @Length(max = Constants.TRIM_MAX_LENGTH)          // Perform length validation first (efficiency, prevent REDOS attacks)
      @Pattern(regexp = "[\\w\\s]*")                    // Allow words and spaces
      @RequestParam(required = false) String trim);

  // @formatter:on
}
