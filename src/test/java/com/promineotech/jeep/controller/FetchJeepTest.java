package com.promineotech.jeep.controller;

import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import com.promineotech.jeep.entity.Jeep;
import com.promineotech.jeep.entity.JeepModel;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)                    // Generates a random port number for testing
@ActiveProfiles("test")                                                         // Sets testing profile to "test"
@Sql(scripts = {"classpath:flyway/migrations/V1.0__Jeep_Schema.sql",            // Values to create/populate tables for testing
    "classpath:flyway/migrations/V1.1__Jeep_Data.sql"}, config = @SqlConfig(encoding = "utf-8"))
class FetchJeepTest {

  @Autowired
  private TestRestTemplate restTemplate;
  
  @LocalServerPort
  private int serverPort;
  
  @Test
  void testThatJeepsAreReturnedWhenAValidModelAndTrimAreSupplied() {
    // Given: A valid model and trim
    JeepModel model = JeepModel.WRANGLER;                       // Set Jeep model for testing
    String trim = "Sport";                                      // Set Jeep trim for testing
    String uri = String.format("http://localhost:%d/jeeps?model=%s&trim=%s", serverPort, model, trim);
    
    // When: A connection is made
    // Calls DB at Uri (localhost database), makes a GET request, (null), returns response code
    ResponseEntity<List<Jeep>> response = restTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

    // Then: A list of Jeeps is returned (requires 200 Status code is returned)
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    
    // And: the actual list is the same as the expected list
    List<Jeep> actual = response.getBody();
    List<Jeep> expected = buildExpected();                      // Calls method buildExpected to create list of correct values

    assertThat(actual).isEqualTo(expected);                     // Tests response against expected list of values
    
  }

  
  // Class to create a list of expected Jeep values for testing
  protected List<Jeep> buildExpected() {
    
    List<Jeep> list = new LinkedList<>();
    
    // @formatter:off
    list.add(Jeep.builder()
        .modelId(JeepModel.WRANGLER)
        .trimLevel("Sport")
        .numDoors(4)
        .wheelSize(17)
        .basePrice(new BigDecimal("31975.00"))
        .build());

    list.add(Jeep.builder()
        .modelId(JeepModel.WRANGLER)
        .trimLevel("Sport")
        .numDoors(2)
        .wheelSize(17)
        .basePrice(new BigDecimal("28475.00"))
        .build());
    // @formatter:on

    Collections.sort(list);                                     // Sort expected list to properly compare against actual list
    
    return list;
  }
  
  
  @Test
  void testThatErrorMessageIsReturnedWhenUnknownTrimIsSupplied() {
    // Given: A valid model and trim
    JeepModel model = JeepModel.WRANGLER;                       // Set Jeep model for testing
    String trim = "Unknown Value";                              // Set value for trim that is not in the schema
    String uri = String.format("http://localhost:%d/jeeps?model=%s&trim=%s", serverPort, model, trim);
    
    // When: A connection is made
    // Calls DB at uri (localhost database), makes a GET request, (null), returns response code
    ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

    // Then: A not found error is returned (requires 404 Status code returned)
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    
    // And: Error message is returned
    Map<String, Object> error = response.getBody();
    
    assertErrorMessageValid(error, HttpStatus.NOT_FOUND);

  }

  
  protected void assertErrorMessageValid(Map<String, Object> error, HttpStatus status) {
    // @formatter:off
    assertThat(error)
        .containsKey("message")
        .containsEntry("status code", status.value())
        .containsEntry("uri", "/jeeps")
        .containsKey("timestamp")
        .containsEntry("reason", status.getReasonPhrase());
    // @formatter:on
  }

  
  /*
  @ParameterizedTest
  void testThatErrorMessageIsReturnedWhenInvalidValueIsSupplied(String model, String trim, String reason) {
    // Given: A valid model and trim
    String uri = String.format("http://localhost:%d/jeeps?model=%s&trim=%s", serverPort, model, trim);
    
    // When: A connection is made
    // Calls DB at uri (localhost database), makes a GET request, (null), returns response code
    ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

    // Then: A not found error is returned (requires 404 Status code returned)
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    
    // And: Error message is returned
    Map<String, Object> error = response.getBody();
    
    assertErrorMessageValid(error, HttpStatus.BAD_REQUEST);
  }

  */

  
}
