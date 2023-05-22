package com.vo.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.vo.common.ServiceDetail;
import com.vo.common.ZContextService;
import com.vo.common.ZRPCProtocol;

import freemarker.core.ArithmeticEngine.ConservativeEngine;

/**
 *
 *
 * @author zhangzhen
 * @date 2022年1月5日
 *
 */
@Controller
public class IC {

	@Autowired
	private ZContextService contextService;

	@GetMapping(value = "/serviceName/{serviceName}")
	public String detail(final Model model,@PathVariable final String serviceName) {
		System.out.println(
				java.time.LocalDateTime.now() + "\t" + Thread.currentThread().getName() + "\t" + "IC.detail()");
		System.out.println("detail-serviceName = " + serviceName);
// FIXME 2022年1月5日 上午12:44:25 zhanghen: 写这里

		final ServiceDetail serviceDetail = this.contextService.getByServiceName(serviceName);
//		final List<ZRPCProtocol> protocolList = this.contextService.getByServiceName(serviceName);
//		System.out.println("plist.size = " + protocolList.size());
//		for (final ZRPCProtocol zrpcProtocol : protocolList) {
//			System.out.println(zrpcProtocol);
//
//		}
		model.addAttribute("serviceDetail", serviceDetail);
		model.addAttribute("serviceName", serviceName);

		return "detail";
	}

	@GetMapping
	public String index(final Model model) {
		final List<String> serviceNameList = this.contextService.getAllServiceName();
		model.addAttribute("serviceNameList", serviceNameList);
		return "index";
	}

}
