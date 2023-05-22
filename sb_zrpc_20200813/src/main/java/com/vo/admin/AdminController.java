package com.vo.admin;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vo.common.ZRPCContext;
import com.vo.core.ZLog2;

/**
 *
 * 	管理后台api
 *
 * @author zhangzhen
 * @date 2021-12-23 20:47:10
 *
 */
@RestController
//@Controller
@RequestMapping(value = "/admin")
public class AdminController {

	public static final ZLog2 LOG = ZLog2.getInstance();

	@Autowired
	private ZRPCContext zrpcContext;

	@GetMapping(value = "/context/all")
	public Object allContext() {
		System.out.println(
				"AdminController.allContext()" + "\t" + LocalDateTime.now() + "\t" + Thread.currentThread().getName());

		return this.zrpcContext.getAllCTXByMethodNameList1("producerService","producerService@hello");

//		final List<String> methodList = this.zrpcContext.getAllMethodByServiceName("producerService");
//		return methodList;

//		final List<String> allServiceName = this.zrpcContext.getAllServiceName();
//		return allServiceName;

//		final List<Entry<String, List<ZRPCCTX>>> ctxList = this.zrpcContext.all();
//		return ctxList;
	}

}
