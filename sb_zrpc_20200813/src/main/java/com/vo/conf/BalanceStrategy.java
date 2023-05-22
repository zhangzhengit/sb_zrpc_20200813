package com.vo.conf;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * 负载均衡策略校验注解
 * 
 * @author zhangzhen
 * @date 2021-12-22 4:18:25
 * 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Constraint(validatedBy = BalanceStrategyClass.class)
public @interface BalanceStrategy {

	String message();

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
	
}
