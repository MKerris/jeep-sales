package com.promineotech.jeep.entity;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data                                           // creates getters, setters, equals, toString, no argument constructor
@Builder                                        // all args constructor
@NoArgsConstructor
@AllArgsConstructor
public class Jeep {

  private Long modelpk;
  private JeepModel modelid;
  private String trimlevel;
  private int numdoors;
  private int wheelsize;
  private BigDecimal baseprice;
  
}
