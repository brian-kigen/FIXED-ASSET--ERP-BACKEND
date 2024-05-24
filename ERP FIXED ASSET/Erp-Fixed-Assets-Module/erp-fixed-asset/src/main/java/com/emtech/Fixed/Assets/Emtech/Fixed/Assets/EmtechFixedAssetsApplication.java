package com.emtech.Fixed.Assets.Emtech.Fixed.Assets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EmtechFixedAssetsApplication {

	public static void main(String[] args) {

		SpringApplication.run(EmtechFixedAssetsApplication.class, args);
	}

}
