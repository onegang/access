package org.onegang.access.implementor;


import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

@SpringBootApplication
@EnableEncryptableProperties
public class MainApplicationImplementor {
	
	public static void main(String[] args) throws IOException {
		SpringApplication.run(MainApplicationImplementor.class, args);
	}
}
