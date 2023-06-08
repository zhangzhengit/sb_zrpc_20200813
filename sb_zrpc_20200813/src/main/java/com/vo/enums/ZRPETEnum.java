package com.vo.enums;

import com.google.common.collect.Maps;
import java.util.HashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 *
 * @author zhangzhen
 * @data Aug 13, 2020
 *
 */
@Getter
@AllArgsConstructor
@SuppressWarnings("boxing")
public enum ZRPETEnum {

	INIT(1, "INIT"),

	INIT_SUCCESS(11, "INIT_SUCCESS"),

	INVOEK(2, "INVOKE"),

	INVOEK_EXCEPTION(21, "INVOKE_EXCEPTION"),

	RESULT(3, "RESULT"),

	SHUTDOWN(4, "SHUTDOWN"),

	PRODUCER_NOT_FOUND(5, "PRODUCER_NOT_FOUND"),

	PRODUCER_CTX_CLOSED(6, "PRODUCER_CTX_CLOSED"),

	COMMIT(7, "COMMIT"),

	ROLLBACK(8, "ROLLBACK"),

	;

	public static ZRPETEnum valueOfType(final Integer type) {
		return ZRPETEnum.map.get(type);
	}

	static HashMap<Integer, ZRPETEnum> map = Maps.newHashMap();
	static {
		final ZRPETEnum[] vssss = values();
		for (final ZRPETEnum zrpete : vssss) {
			ZRPETEnum.map.put(zrpete.getType(), zrpete);
		}
	}

	private Integer type;
	private String name;

}
