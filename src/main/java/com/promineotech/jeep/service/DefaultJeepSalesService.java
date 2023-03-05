package com.promineotech.jeep.service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.promineotech.jeep.dao.JeepSalesDao;
import com.promineotech.jeep.entity.Jeep;
import com.promineotech.jeep.entity.JeepModel;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DefaultJeepSalesService implements JeepSalesService {

  @Autowired
  private JeepSalesDao jeepSalesDao;
  
  @Transactional(readOnly = true)                                           // Manages creation, commit, and rollback of the transaction 
  @Override
  public List<Jeep> fetchJeeps(JeepModel model, String trim) {
    
    log.info("The fetchJeeps method was callled with model={} and trim={}", model, trim);
    
    List<Jeep> jeeps = jeepSalesDao.fetchJeeps(model, trim);
    
    if(jeeps.isEmpty()) {                                                   // Checks to see if jeeps is an empty list (not null)
      String msg = String.format("No jeeps found with model=%s and trim=%s", model, trim);
      throw new NoSuchElementException(msg);
    }
    
    Collections.sort(jeeps);                                                // Sort actual list of jeeps for comparison against expected for testing
    
    return jeeps;
  }

}
