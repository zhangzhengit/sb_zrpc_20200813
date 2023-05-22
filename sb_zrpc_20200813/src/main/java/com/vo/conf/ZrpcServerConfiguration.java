package com.vo.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 *
 * @author zhangzhen
 * @data Aug 13, 2020
 * 
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "zrpc")
@Validated
public class ZrpcServerConfiguration {

	private int port = 9995;

	@BalanceStrategy(message = "负载均衡策略必须配置 ZProcuderBalanceEnum 枚举里的一种")
	private String balanceStrategy;
	
}
