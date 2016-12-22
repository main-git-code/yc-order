package com.ai.yc.order.service.elasticjob;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ai.opt.base.vo.BaseResponse;
import com.ai.opt.sdk.dubbo.util.DubboConsumerFactory;
import com.ai.yc.order.api.orderdeal.interfaces.IAutoDealOrderSV;
import com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;

/**
 * @Description: 自动审核订单定时任务
 * @author hougang@asiainfo.com
 * @date 2016年11月22日 下午4:16:25 
 * @version V1.0
 */
public class AutoReviewOrderJob implements SimpleJob{

private static final Logger LOG = LogManager.getLogger(AutoReviewOrderJob.class);
	
	@Override
	public void execute(ShardingContext shardingContext) {
		LOG.info("自动审核订单定时任务start.....................");
		IAutoDealOrderSV iAutoDealOrderSV = DubboConsumerFactory.getService(IAutoDealOrderSV.class);
    	BaseResponse resp = iAutoDealOrderSV.autoReviewOrder();
    	LOG.info("自动审核订单定时任务消息："+JSON.toJSONString(resp));
    	LOG.info("自动审核订单定时任务end.....................");
	}

}
