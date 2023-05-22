package com.vo.conf;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.vo.client.balance.ZProcuderBalanceEnum;

import cn.hutool.core.util.StrUtil;

/**
 * 
 * 
 * @author zhangzhen
 * @date 2021-12-22 4:18:41
 * 
 */
public class BalanceStrategyClass implements ConstraintValidator<BalanceStrategy, Object> {

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		System.out.println("BalanceStrategyClass.isValid()" + "\t" + LocalDateTime.now() + "\t"
				+ Thread.currentThread().getName());
		if (Objects.isNull(value)) {
			return false;
		}
		
		final String s = String.valueOf(value);
		if (StrUtil.isBlank(s)) {
			return false;
		}

		final ZProcuderBalanceEnum[] es = ZProcuderBalanceEnum.values();
		for (final ZProcuderBalanceEnum ee : es) {
			if (Objects.equals(ee.name(), s)) {
				return true;
			}
		}

		return false;
	}

}
