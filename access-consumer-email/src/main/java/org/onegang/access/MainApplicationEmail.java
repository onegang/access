package org.onegang.access;


import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

@SpringBootApplication
@EnableEncryptableProperties
public class MainApplicationEmail {
	
	public static void main(String[] args) throws IOException {
		SpringApplication.run(MainApplicationEmail.class, args);
	}
}
