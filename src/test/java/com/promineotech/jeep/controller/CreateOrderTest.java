package com.promineotech.jeep.controller;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.jdbc.JdbcTestUtils;
import com.promineotech.jeep.entity.JeepModel;
import com.promineotech.jeep.entity.Order;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)                    // Generates a random port number for testing
@ActiveProfiles("test")                                                         // Sets testing profile to "test"
@Sql(scripts = {"classpath:flyway/migrations/V1.0__Jeep_Schema.sql",            // Values to create/populate tables for testing
    "classpath:flyway/migrations/V1.1__Jeep_Data.sql"}, config = @SqlConfig(encoding = "utf-8"))  

class CreateOrderTest {

  @Autowired
  private TestRestTemplate restTemplate;
  
  @LocalServerPort
  private int serverPort;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  
  @Test
  void testCreateOrderReturnsSuccess201() {
    
    // Given: An order as JSON
    String body = createOrderBody();                                            // Generates JSON for options provided in method createOrderBody for testing
    String uri = String.format("http://localhost:%d/orders", serverPort);       // Creates uri with random port for /orders

    int numRowsOrders = JdbcTestUtils.countRowsInTable(jdbcTemplate, "orders");
    int numRowsOptions = JdbcTestUtils.countRowsInTable(jdbcTemplate, "order_options");
        
    HttpHeaders headers = new HttpHeaders();                                    // TODO: Need to research, not explained in video
    headers.setContentType(MediaType.APPLICATION_JSON);                         // Tells controller that the media type coming in is JSON

    HttpEntity<String> bodyEntity = new HttpEntity<>(body, headers);            // TODO: Need to research, not explained in video

    // When: The order is sent
    ResponseEntity<Order> response = restTemplate.exchange(uri, HttpMethod.POST, bodyEntity, Order.class);
    
    // Then: A 201 status is returned
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);         // Confirm 201 is received

    // And: The returned order is correct
    assertThat(response.getBody()).isNotNull();

    Order order = response.getBody();                                           // Test actual values against expected values set up in createOrderBody
    assertThat(order.getCustomer().getCustomerId()).isEqualTo("ROTH_GARTH");
    assertThat(order.getModel().getModelId()).isEqualTo(JeepModel.GLADIATOR);
    assertThat(order.getModel().getTrimLevel()).isEqualTo("Sport S");
    assertThat(order.getModel().getNumDoors()).isEqualTo(4);
    assertThat(order.getColor().getColorId()).isEqualTo("EXT_SLATE_BLUE");
    assertThat(order.getEngine().getEngineId()).isEqualTo("6_4_GAS");
    assertThat(order.getTire().getTireId()).isEqualTo("295_YOKOHAMA");
    assertThat(order.getOptions()).hasSize(6);
    
    // Testing to prove that the orders table increases by one record and order_options increases by six records
    assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "orders")).isEqualTo(numRowsOrders + 1);
    assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "order_options")).isEqualTo(numRowsOptions + 6);
  }

  
  // Create JSON with details for testCreateOrderRetursSuccess201
  protected String createOrderBody() {
    
    // @formatter:off
    return "{\n"
        + "  \"customer\":\"ROTH_GARTH\",\n"
        + "  \"model\":\"GLADIATOR\",\n"
        + "  \"trim\":\"Sport S\",\n"
        + "  \"doors\":4,\n"
        + "  \"color\":\"EXT_SLATE_BLUE\",\n"
        + "  \"engine\":\"6_4_GAS\",\n"
        + "  \"tire\":\"295_YOKOHAMA\",\n"
        + "  \"options\":[\n"
        + "    \"DOOR_QUAD_4\",\n"
        + "    \"EXT_AEV_LIFT\",\n"
        + "    \"EXT_WARN_WINCH\",\n"
        + "    \"EXT_WARN_BUMPER_FRONT\",\n"
        + "    \"EXT_WARN_BUMPER_REAR\",\n"
        + "    \"EXT_ARB_COMPRESSOR\"\n"
        + "  ]\n"
        + "}";
    // @formatter:on
    
  }
  
}
