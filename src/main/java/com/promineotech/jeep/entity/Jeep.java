package com.promineotech.jeep.entity;

import java.math.BigDecimal;
import java.util.Comparator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data                                           // creates getters, setters, equals, toString, no argument constructor
@Builder                                        // all args constructor
@NoArgsConstructor
@AllArgsConstructor
public class Jeep implements Comparable<Jeep> {

  private Long modelPK;
  private JeepModel modelId;
  private String trimLevel;
  private int numDoors;
  private int wheelSize;
  private BigDecimal basePrice;
  
  @JsonIgnore                                   // Ignores modelpk value when generating the JSON for the results
  public Long getModelpk() {
    return modelPK;
  }

  @Override
  public int compareTo(Jeep that) {
    // @formatter:off
    return Comparator                           // Comparator orders values in a collection to allow for sorting
        .comparing(Jeep::getModelId)
        .thenComparing(Jeep::getTrimLevel)
        .thenComparing(Jeep::getNumDoors)
        .compare(this, that);
    // @formatter:on
  }
  
}
