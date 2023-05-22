package com.vo.conf;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vo.client.balance.ZProcuderBalanceEnum;

/**
 *
 *
 * @author zhangzhen
 * @date 2021-12-24 7:34:19
 *
 */
@Component
public class BalanceStrategyComponent {

	@Autowired
	private ZrpcServerConfiguration serverConfiguration;

	private  static final AtomicReference<ZProcuderBalanceEnum> balanceReference = new AtomicReference<>();

	public ZProcuderBalanceEnum getBalanceStrategyEnum() {
		final ZProcuderBalanceEnum e = BalanceStrategyComponent.balanceReference.get();
		if (Objects.nonNull(e)) {
			return e;
		}

		final ZProcuderBalanceEnum[] es = ZProcuderBalanceEnum.values();
		for (final ZProcuderBalanceEnum e2 : es) {
			if (Objects.equals(e2.name(), this.serverConfiguration.getBalanceStrategy())) {
				BalanceStrategyComponent.balanceReference.set(e2);
				return e2;
			}
		}

		return null;
	}

}
