package com.promineotech.jeep.dao;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.promineotech.jeep.entity.Jeep;
import com.promineotech.jeep.entity.JeepModel;
import lombok.extern.slf4j.Slf4j;

@Service
@Component
@Slf4j
public class DefaultJeepSalesDao implements JeepSalesDao {
  
  @Autowired
  private NamedParameterJdbcTemplate jdbcTemplate;                          // allows use of named parameters rather than '?' placeholders

  @Override
  public List<Jeep> fetchJeeps(JeepModel model, String trim) {              // Model and trim sent from service layer
    
    log.debug("DAO: model={}, trim={}", model, trim);                       // Console logging for DAO layer showing values sent from Service layer
    
    String sql = ""                                                         // Set up SQL to return values from schema based on model/trim
        + "SELECT * "
        + "FROM models "
        + "WHERE model_id = :model_id AND trim_level = :trim_level";
    
    Map<String, Object> params = new HashMap<>();                           // Created HashMap to help prevent SQL Injection attacks
    params.put("model_id", model.toString());                               // Values get placed in a Map so the map can be referenced
    params.put("trim_level", trim);                                         // instead of concatenating values, opening up SQL injection attack
       
    return jdbcTemplate.query(sql, params, new RowMapper<>() {              // Returns a list of Jeep objects from SQL query back to the service layer

      @Override
      public Jeep mapRow(ResultSet rs, int rowNum) throws SQLException {    // Builds a Jeep object for each row returned by the SQL query
        // @formatter:off
        return Jeep.builder()
            .basePrice(new BigDecimal(rs.getString("base_price")))
            .modelId(JeepModel.valueOf(rs.getString("model_id")))
            .modelPK(rs.getLong("model_pk"))
            .numDoors(rs.getInt("num_doors"))
            .trimLevel(rs.getString("trim_level"))
            .wheelSize(rs.getInt("wheel_size"))
            .build();
        // @formatter:on
      }});
  }

}
