package com.vo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


/**
 * rpc server
 *
 * @author zhangzhen
 * @data Aug 13, 2020
 *
 */
@EnableAsync
@SpringBootApplication
public class SbZrpc20200813Application {

	@SuppressWarnings("resource")
	public static void main(final String[] args) {
		SpringApplication.run(SbZrpc20200813Application.class, args);
	}

}
