package com.ai.slp.order.api.paystatus;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ai.opt.sdk.dubbo.util.DubboConsumerFactory;
import com.ai.slp.order.api.BaseTest;
import com.ai.yc.common.api.sysdomain.interfaces.IQuerySysDomainSV;
import com.ai.yc.common.api.sysdomain.param.QuerySysDomainDetailsRes;
import com.ai.yc.common.api.sysduad.interfaces.IQuerySysDuadSV;
import com.ai.yc.common.api.sysduad.param.QuerySysDuadListReq;
import com.ai.yc.common.api.sysduad.param.QuerySysDuadListRes;
import com.ai.yc.order.api.paystatus.interfaces.IUpdatePayStatusSV;
import com.ai.yc.order.api.paystatus.param.OrderFeeVo;
import com.ai.yc.order.api.paystatus.param.UpdatePayStatusRequest;
import com.ai.yc.order.api.paystatus.param.UpdatePayStatusResponse;
import com.ai.yc.order.service.atom.interfaces.ISysConfigAtomSV;
import com.google.gson.Gson;

public class UpdatePayStatusTest extends BaseTest {

	@Autowired
	private IUpdatePayStatusSV iUpdatePayStatusSV;

	@Test
	public void testUpdatePayStatus() {
		UpdatePayStatusRequest req = new UpdatePayStatusRequest();
		req.setOrderId(2000000001984671l);
		req.setOperId("10000");
		req.setOperName("jack");
		OrderFeeVo orderFee = new OrderFeeVo();
		orderFee.setCurrencyUnit("1");
		orderFee.setTotalFee(999l);
		orderFee.setDiscountFee(0l);
		orderFee.setOperDiscountFee(0l);
		orderFee.setAdjustFee(999l);
		orderFee.setPaidFee(999l);
		orderFee.setPayFee(0l);
		orderFee.setRemark("用户银联转账");
		orderFee.setPayStyle("11");
		req.setOrderFee(orderFee);
		UpdatePayStatusResponse resp = iUpdatePayStatusSV.updatePayStatus(req);
		Gson gson = new Gson();
		System.out.println(gson.toJson(resp));
	}

	@Autowired
	private ISysConfigAtomSV iSysConfigAtomSV;

	@Test
	public void test() {
		Gson gson = new Gson();
		QuerySysDuadListReq req = new QuerySysDuadListReq();
		req.setLanguage("zh_CN");
		req.setOrderType("1");
	    QuerySysDuadListRes list = DubboConsumerFactory
				.getService(IQuerySysDuadSV.class).querySysDuadList(req);
		System.out.println(gson.toJson(list));
		
	}



}
