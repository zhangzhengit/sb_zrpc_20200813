package com.vo.client.balance;

import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  	的配置
 * 
 * @author zhangzhen
 * @date 2021-12-14 21:29:10
 * 
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "zrpc")
public class CC {

	/**
	 *	 服务提供者的URL
	 */
	private Set<String> procuderUrl;
	
	/**
	 * 	对于服务提供者的负载均衡模式
	 */
	private ZProcuderBalanceEnum procuderBalance;

}
