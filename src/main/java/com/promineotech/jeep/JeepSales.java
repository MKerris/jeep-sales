package com.promineotech.jeep;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.promineotech.ComponentScanMarker;

@SpringBootApplication(scanBasePackageClasses = {ComponentScanMarker.class})    // tells Spring to scan (sub)classes in package from ComponentScanMarker.java
public class JeepSales {

	public static void main(String[] args) {
		SpringApplication.run(JeepSales.class, args);
	}
	
}
