package com.ai.yc.order.api.orderquery.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ai.opt.base.exception.BusinessException;
import com.ai.opt.base.exception.SystemException;
import com.ai.opt.base.vo.PageInfo;
import com.ai.opt.base.vo.ResponseHeader;
import com.ai.opt.sdk.constants.ExceptCodeConstants;
import com.ai.opt.sdk.util.CollectionUtil;
import com.ai.opt.sdk.util.DateUtil;
import com.ai.paas.ipaas.search.vo.Result;
import com.ai.paas.ipaas.search.vo.SearchCriteria;
import com.ai.paas.ipaas.search.vo.SearchOption;
import com.ai.paas.ipaas.search.vo.SearchOption.SearchLogic;
import com.ai.paas.ipaas.search.vo.SearchOption.SearchType;
import com.ai.paas.ipaas.search.vo.Sort;
import com.ai.paas.ipaas.search.vo.Sort.SortOrder;
import com.ai.paas.ipaas.util.StringUtil;
import com.ai.yc.order.api.orderquery.interfaces.IOrderQuerySV;
import com.ai.yc.order.api.orderquery.param.OrdOrderCountRequest;
import com.ai.yc.order.api.orderquery.param.OrdOrderCountResponse;
import com.ai.yc.order.api.orderquery.param.OrdOrderVo;
import com.ai.yc.order.api.orderquery.param.OrdProdExtendVo;
import com.ai.yc.order.api.orderquery.param.OrdProdLevelVo;
import com.ai.yc.order.api.orderquery.param.QueryOrdCountRequest;
import com.ai.yc.order.api.orderquery.param.QueryOrdCountResponse;
import com.ai.yc.order.api.orderquery.param.QueryOrderRequest;
import com.ai.yc.order.api.orderquery.param.QueryOrderRsponse;
import com.ai.yc.order.constants.OrdersConstants;
import com.ai.yc.order.constants.SearchFieldConfConstants;
import com.ai.yc.order.search.bo.OrdProdExtend;
import com.ai.yc.order.search.bo.OrdProdLevel;
import com.ai.yc.order.search.bo.OrderInfo;
import com.ai.yc.order.service.business.impl.search.OrderSearchImpl;
import com.ai.yc.order.service.business.interfaces.IOrdOrderBusiSV;
import com.ai.yc.order.service.business.interfaces.search.IOrderSearch;
import com.ai.yc.order.util.ValidateUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

@Service
@Component
public class OrderQuerySVImpl implements IOrderQuerySV {
	@Autowired
	IOrdOrderBusiSV iOrdOrderBusiSV;

	@Override
	public QueryOrderRsponse queryOrder(QueryOrderRequest request) throws BusinessException, SystemException {
		request.setTenantId(OrdersConstants.TENANT_ID);
		// 有效性校验
		ValidateUtils.validateQueryOrder(request);
		// 调用搜索引擎进行查询
		int startSize = 1;
		int maxSize = 1;
		// 最大条数设置
		int pageNo = request.getPageNo();
		int size = request.getPageSize();
		if (request.getPageNo() == 1) {
			startSize = 0;
		} else {
			startSize = (pageNo - 1) * size;
		}
		maxSize = size;
		QueryOrderRsponse response = new QueryOrderRsponse();
		PageInfo<OrdOrderVo> pageinfo = new PageInfo<OrdOrderVo>();
		List<OrdOrderVo> results = new ArrayList<OrdOrderVo>();
		IOrderSearch orderSearch = new OrderSearchImpl();
		List<SearchCriteria> orderSearchCriteria = commonConditions(request);
		List<Sort> sortList = new ArrayList<Sort>();
		if (!StringUtil.isBlank(request.getOrderByFlag())) {
			if ("1".equals(request.getOrderByFlag())) {
				Sort sort = new Sort(SearchFieldConfConstants.TAKE_DAY, SortOrder.DESC);
				sortList.add(sort);
			} else if ("2".equals(request.getOrderByFlag())) {
				Sort sort = new Sort(SearchFieldConfConstants.TAKE_TIME, SortOrder.DESC);
				sortList.add(sort);
			} else if ("3".equals(request.getOrderByFlag())) {
				Sort sort = new Sort(SearchFieldConfConstants.TOTAL_FEE, SortOrder.DESC);
				sortList.add(sort);
			}
		} else {
			Sort sort = new Sort(SearchFieldConfConstants.ORDER_TIME, SortOrder.DESC);
			sortList.add(sort);
		}
		Result<OrderInfo> result = orderSearch.search(orderSearchCriteria, startSize, maxSize, sortList);
		List<OrderInfo> ordList = result.getContents();
		if (!CollectionUtil.isEmpty(ordList)) {
			for (OrderInfo ord : ordList) {
				OrdOrderVo order = new OrdOrderVo();
				List<OrdProdExtendVo> ordProdExtendList = new ArrayList<OrdProdExtendVo>();
				List<OrdProdLevelVo> ordProdLevelList = new ArrayList<OrdProdLevelVo>();
				// 订单id
				order.setOrderId(Long.valueOf(ord.getOrderid()));
				order.setBusiType(ord.getBusitype());
				order.setChlId(ord.getChlid());
				order.setDisplayFlag(ord.getDisplayflag());
				order.setInterperName(ord.getInterpername());
				order.setFlag(ord.getFlag());
				order.setInterperId(ord.getInterperid());
				order.setLspName(ord.getLspname());
				order.setLspId(ord.getLspid());
				order.setOrderLevel(ord.getOrderlevel());
				order.setOrderType(ord.getOrdertype());
				order.setPayStyle(ord.getPaystyle());
				order.setUserType(ord.getUsertype());
				order.setUserId(ord.getUserid());
				order.setUserName(ord.getUsername());
				order.setState(ord.getState());
				order.setPayStyle(ord.getPaystyle());
				order.setTotalFee(ord.getTotalfee());
				order.setCurrencyUnit(ord.getCurrencyunit());
				order.setOperId(ord.getOperid());
				order.setTranslateName(ord.getTranslatename());
				order.setTranslateType(ord.getTranslatetype());
				order.setUpdateOperName(ord.getUpdatename());
				order.setDiscountFee(ord.getDiscountfee());
				order.setPlatFee(ord.getPlatfee());
				order.setInterperFee(ord.getInterperfee());
				order.setPaidFee(ord.getPaidfee());
				// 评论信息
				order.setEvaluateContent(ord.getEvaluatecontent());
				order.setEvaluateSun(ord.getEvaluatesun());
				order.setServeManner(ord.getServemanner());
				order.setServeQuality(ord.getServequality());
				order.setServeSpeed(ord.getServespeed());
				order.setEvaluateState(ord.getEvaluatestate());
				order.setInterLevel(ord.getInterperlevel());
				order.setParentOrderId(ord.getParentorderid());
				if (ord.getEvaluatetime() != null) {
					order.setEvaluateTime(new Timestamp(ord.getEvaluatetime().getTime()));
				}
				if (ord.getStarttime() != null) {
					order.setStartTime(new Timestamp(ord.getStarttime().getTime()));
				}
				if (ord.getEndtime() != null) {
					order.setEndTime(new Timestamp(ord.getEndtime().getTime()));
				}
				if (ord.getProdupdatetime() != null) {
					order.setProdUpdateTime(new Timestamp(ord.getProdupdatetime().getTime()));
				}
				if (ord.getStatechgtime() != null) {
					order.setStateChgTime(new Timestamp(ord.getStatechgtime().getTime()));
				}
				if (ord.getFinishtime() != null) {
					order.setFinishTime(new Timestamp(ord.getFinishtime().getTime()));
				}
				if (ord.getLocktime() != null) {
					order.setLockTime(new Timestamp(ord.getLocktime().getTime()));
				}
				if (ord.getOrdertime() != null) {
					order.setOrderTime(new Timestamp(ord.getOrdertime().getTime()));
				}
				if (ord.getPaytime() != null) {
					order.setPayTime(new Timestamp(ord.getPaytime().getTime()));
				}
				if (ord.getUpdatetime() != null) {
					order.setUpdateTime(new Timestamp(ord.getUpdatetime().getTime()));
				}
				if (ord.getEndtime() != null) {
					long time = ord.getEndtime().getTime() - DateUtil.getCurrentTimeMillis();
					Timestamp scurrtest = new Timestamp(time);
					order.setRemainingTime(scurrtest);
				}
				if (ord.getEndchgtime() != null) {
					order.setEndChgTime(new Timestamp(ord.getEndchgtime().getTime()));
				}
				// 获取语言对名称
				String extendInfos = JSON.toJSONString(ord.getOrdextendes());
				List<OrdProdExtend> extendList = JSON.parseObject(extendInfos,
						new TypeReference<List<OrdProdExtend>>() {
						});
				if (!CollectionUtil.isEmpty(extendList)) {
					for (OrdProdExtend extend : extendList) {
						OrdProdExtendVo prodExtend = new OrdProdExtendVo();
						prodExtend.setLangungePair(extend.getLangungeid());
						prodExtend.setLangungePairChName(extend.getLangungechname());
						prodExtend.setLangungePairEnName(extend.getLangungeenname());
						ordProdExtendList.add(prodExtend);
					}
					order.setOrdProdExtendList(ordProdExtendList);
				}
				// 获取翻译级别
				String ordLevelInfos = JSON.toJSONString(ord.getOrdprodleveles());
				List<OrdProdLevel> levelList = JSON.parseObject(ordLevelInfos, new TypeReference<List<OrdProdLevel>>() {
				});
				if (!CollectionUtil.isEmpty(levelList)) {
					for (OrdProdLevel level : levelList) {
						OrdProdLevelVo levelVo = new OrdProdLevelVo();
						levelVo.setTranslateLevel(level.getTranslatelevel());
						ordProdLevelList.add(levelVo);
					}
					order.setOrdProdLevelList(ordProdLevelList);
				}

				results.add(order);
			}
		}
		pageinfo.setResult(results);
		pageinfo.setPageSize(request.getPageSize());
		pageinfo.setPageNo(request.getPageNo());
		pageinfo.setCount(Long.valueOf(result.getCount()).intValue());
		response.setPageInfo(pageinfo);
		ResponseHeader responseHeader = new ResponseHeader(true, ExceptCodeConstants.Special.SUCCESS, "订单查询成功");
		response.setResponseHeader(responseHeader);
		return response;

	}

	// 搜索引擎数据公共查询条件
	public List<SearchCriteria> commonConditions(QueryOrderRequest request) {
		List<SearchCriteria> searchfieldVos = new ArrayList<SearchCriteria>();
		// 如果业务标识不为空
		if (!StringUtil.isBlank(request.getFlag())) {
			searchfieldVos.add(new SearchCriteria(SearchFieldConfConstants.FLAG, request.getFlag(),
					new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.querystring)));

		}
		// 如果订单id不为空
		if (request.getOrderId() != null) {
			searchfieldVos.add(new SearchCriteria(SearchFieldConfConstants.ORDER_ID, request.getOrderId().toString(),
					new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.querystring)));
		}
		// 如果用户类型不为空
		if (!StringUtil.isBlank(request.getUserType())) {
			searchfieldVos.add(new SearchCriteria(SearchFieldConfConstants.USER_TYPE, request.getUserType(),
					new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.querystring)));
		}
		// 如果用户id不为空
		if (!StringUtil.isBlank(request.getUserId()) && StringUtil.isBlank(request.getCorporaId())) {
			searchfieldVos.add(new SearchCriteria(SearchFieldConfConstants.USER_ID, request.getUserId(),
					new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.querystring)));
		}
		// 如果企业id不为空
				if (!StringUtil.isBlank(request.getCorporaId()) && StringUtil.isBlank(request.getUserId())) {
					searchfieldVos.add(new SearchCriteria(SearchFieldConfConstants.CORPORA_ID, request.getCorporaId(),
							new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.querystring)));
				}
		//如果企业id和用户id同时不为空
		if(!StringUtil.isBlank(request.getUserId()) && !StringUtil.isBlank(request.getCorporaId())){
			SearchCriteria vo = new SearchCriteria();
            vo.addSubCriteria(new SearchCriteria(SearchFieldConfConstants.CORPORA_ID, request.getCorporaId(), new SearchOption(SearchLogic.should, SearchType.querystring)));
            vo.addSubCriteria(new SearchCriteria(SearchFieldConfConstants.USER_ID, request.getUserId(), new SearchOption(SearchLogic.should, SearchType.querystring)));
            searchfieldVos.add(vo);
		}
		// 如果报价标识不为空
		if (!StringUtil.isBlank(request.getSubFlag())) {
			searchfieldVos.add(new SearchCriteria(SearchFieldConfConstants.SUB_FLAG, request.getSubFlag(),
					new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.querystring)));
		}
		// 如果订单来源不为空
		if (!StringUtil.isBlank(request.getChlId())) {
			searchfieldVos.add(new SearchCriteria(SearchFieldConfConstants.CHL_ID, request.getChlId(),
					new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.querystring)));
		}
		// 如果订单类型不为空
		if (!StringUtil.isBlank(request.getOrderType())) {
			searchfieldVos.add(new SearchCriteria(SearchFieldConfConstants.ORDER_TYPE, request.getOrderType(),
					new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.querystring)));
		}
		// 如果订单级别不为空
		if (!StringUtil.isBlank(request.getOrderLevel())) {
			searchfieldVos.add(new SearchCriteria(SearchFieldConfConstants.ORDER_LEVEL, request.getOrderLevel(),
					new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.querystring)));
		}
		// 如果翻译类型不为空
		if (!StringUtil.isBlank(request.getTranslateType())) {
			searchfieldVos.add(new SearchCriteria(SearchFieldConfConstants.TRANSLATE_TYPE, request.getTranslateType(),
					new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.querystring)));
		}

		// 如果业务类型不为空
		if (!StringUtil.isBlank(request.getBusiType())) {
			searchfieldVos.add(new SearchCriteria(SearchFieldConfConstants.BUSI_TYPE, request.getBusiType(),
					new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.querystring)));
		}

		// 如果翻译主题不为空
		if (!StringUtil.isBlank(request.getTranslateName())) {
			searchfieldVos.add(new SearchCriteria(SearchFieldConfConstants.TRANSLATE_NAME, request.getTranslateName(),
					new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.match,
							SearchOption.TermOperator.AND)));
		}
		// 如果state不为空
		if (!StringUtil.isBlank(request.getState())) {
			searchfieldVos.add(new SearchCriteria(SearchFieldConfConstants.STATE, request.getState(),
					new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.querystring)));
		}
		// 如果客户端显示状态不为空
		if (!StringUtil.isBlank(request.getDisplayFlag())) {
			searchfieldVos.add(new SearchCriteria(SearchFieldConfConstants.DISPLAY_FLAG, request.getDisplayFlag(),
					new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.querystring)));
		}
		// 如果译员类型不为空
		if (!StringUtil.isBlank(request.getInterperType())) {
			searchfieldVos.add(new SearchCriteria(SearchFieldConfConstants.INTERPER_TYPE, request.getInterperType(),
					new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.querystring)));
		}
		// 如果操作员id不为空
		if (!StringUtil.isBlank(request.getOperId())) {
			searchfieldVos.add(new SearchCriteria(SearchFieldConfConstants.OPER_ID, request.getOperId(),
					new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.querystring)));
		}
		// 如果lspid不为空
		if (!StringUtil.isBlank(request.getLspId())) {
			searchfieldVos.add(new SearchCriteria(SearchFieldConfConstants.LSP_ID, request.getLspId(),
					new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.querystring)));
		}
		// 如果译员id不为空
		if (!StringUtil.isBlank(request.getInterperId()) && StringUtil.isBlank(request.getLspId())) {
			searchfieldVos.add(new SearchCriteria(SearchFieldConfConstants.INTERPER_ID, request.getInterperId(),
					new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.querystring)));
		}
		// 如果关键词不为空
		if (!StringUtil.isBlank(request.getKeyWords())) {
			searchfieldVos.add(new SearchCriteria(SearchFieldConfConstants.KEYWORDS, request.getKeyWords(),
					new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.querystring)));
		}
		// 如果返工标识不为空
		if (!StringUtil.isBlank(request.getUpdateFlag())) {
			searchfieldVos.add(new SearchCriteria(SearchFieldConfConstants.UPDATE_FLAG, request.getUpdateFlag(),
					new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.querystring)));
		}
		// 如果语言对id不为空
		if (!StringUtil.isBlank(request.getLangungePaire())) {
			searchfieldVos.add(new SearchCriteria(SearchFieldConfConstants.LANGUNGE_ID, request.getLangungePaire(),
					new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.querystring)));
		}
		// 如果用途id不为空
		if (!StringUtil.isBlank(request.getUseCode())) {
			searchfieldVos.add(new SearchCriteria(SearchFieldConfConstants.USE_CODE, request.getUseCode(),
					new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.querystring)));
		}
		// 如果领域id不为空
		if (!StringUtil.isBlank(request.getFieldCode())) {
			searchfieldVos.add(new SearchCriteria(SearchFieldConfConstants.FIELD_CODE, request.getFieldCode(),
					new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.querystring)));
		}
		// 如果用户昵称不为空
		if (!StringUtil.isBlank(request.getUserName())) {
			searchfieldVos.add(new SearchCriteria(SearchFieldConfConstants.USER_NAME, request.getUserName(),
					new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.match,
							SearchOption.TermOperator.AND)));
		}
		// 如果译员名称不为空
		if (!StringUtil.isBlank(request.getInterperName())) {
			searchfieldVos.add(new SearchCriteria(SearchFieldConfConstants.INTERPER_NAME, request.getInterperName(),
					new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.match,
							SearchOption.TermOperator.AND)));
		}
		// 如果lsp名称不为空
		if (!StringUtil.isBlank(request.getLspName())) {
			searchfieldVos.add(new SearchCriteria(SearchFieldConfConstants.LSP_NAME, request.getLspName(),
					new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.match,
							SearchOption.TermOperator.AND)));
		}
		// 如果支付方式不为空
		if (!StringUtil.isBlank(request.getPayStyle())) {
			searchfieldVos.add(new SearchCriteria(SearchFieldConfConstants.PAY_STYLE, request.getPayStyle(),
					new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.querystring)));
		}
		// 如果报价人不为空
		if (!StringUtil.isBlank(request.getUpdateOperName())) {
			searchfieldVos.add(new SearchCriteria(SearchFieldConfConstants.UPDATE_OPER_NAME,
					request.getUpdateOperName(), new SearchOption(SearchOption.SearchLogic.must,
							SearchOption.SearchType.match, SearchOption.TermOperator.AND)));
		}

		// 如果状态变化开始、结束时间不为空
		if (request.getStateChgTimeStart() != null && request.getStateChgTimeEnd() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");
			String start = sdf.format(request.getStateChgTimeStart());
			String end = sdf.format(request.getStateChgTimeEnd());
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setOption(new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.range));
			searchCriteria.setField(SearchFieldConfConstants.STATE_CHG_TIME);
			searchCriteria.addFieldValue(start);
			searchCriteria.addFieldValue(end);
			searchfieldVos.add(searchCriteria);
		}
		// 如果状态变化开始时间不为空
		if (request.getStateChgTimeStart() != null && request.getStateChgTimeEnd() == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");
			String start = sdf.format(request.getStateChgTimeStart());
			String end = sdf.format(new Date());
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setOption(new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.range));
			searchCriteria.setField(SearchFieldConfConstants.STATE_CHG_TIME);
			searchCriteria.addFieldValue(start);
			searchCriteria.addFieldValue(end);
			searchfieldVos.add(searchCriteria);
		}
		// 如果状态变化结束时间不为空
		if (request.getStateChgTimeStart() == null && request.getStateChgTimeEnd() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");
			String end = sdf.format(request.getStateChgTimeEnd());
			Timestamp sTime = Timestamp.valueOf(OrdersConstants.START_TIME);
			String start = sdf.format(sTime);
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setOption(new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.range));
			searchCriteria.setField(SearchFieldConfConstants.STATE_CHG_TIME);
			searchCriteria.addFieldValue(start);
			searchCriteria.addFieldValue(end);
			searchfieldVos.add(searchCriteria);
		}
		// 下单开始、结束时间不为空
		if (request.getOrderTimeStart() != null && request.getOrderTimeEnd() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");
			String start = sdf.format(request.getOrderTimeStart());
			String end = sdf.format(request.getOrderTimeEnd());
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setOption(new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.range));
			searchCriteria.setField(SearchFieldConfConstants.ORDER_TIME);
			searchCriteria.addFieldValue(start);
			searchCriteria.addFieldValue(end);
			searchfieldVos.add(searchCriteria);
		}
		// 下单开始时间不为空
		if (request.getOrderTimeStart() != null && request.getOrderTimeEnd() == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");
			String start = sdf.format(request.getOrderTimeStart());
			String end = sdf.format(new Date());
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setOption(new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.range));
			searchCriteria.setField(SearchFieldConfConstants.ORDER_TIME);
			searchCriteria.addFieldValue(start);
			searchCriteria.addFieldValue(end);
			searchfieldVos.add(searchCriteria);
		}
		// 下单结束时间不为空
		if (request.getOrderTimeStart() == null && request.getOrderTimeEnd() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");
			String end = sdf.format(request.getOrderTimeEnd());
			Timestamp sTime = Timestamp.valueOf(OrdersConstants.START_TIME);
			String start = sdf.format(sTime);
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setOption(new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.range));
			searchCriteria.setField(SearchFieldConfConstants.ORDER_TIME);
			searchCriteria.addFieldValue(start);
			searchCriteria.addFieldValue(end);
			searchfieldVos.add(searchCriteria);
		}

		// 如果领取开始、结束时间不为空
		if (request.getLockTimeStart() != null && request.getLockTimeEnd() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");
			String start = sdf.format(request.getLockTimeStart());
			String end = sdf.format(request.getLockTimeEnd());
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setOption(new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.range));
			searchCriteria.setField(SearchFieldConfConstants.LOCK_TIME);
			searchCriteria.addFieldValue(start);
			searchCriteria.addFieldValue(end);
			searchfieldVos.add(searchCriteria);
		}
		// 如果领取开始时间不为空
		if (request.getLockTimeStart() != null && request.getLockTimeEnd() == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");
			String start = sdf.format(request.getLockTimeStart());
			String end = sdf.format(new Date());
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setOption(new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.range));
			searchCriteria.setField(SearchFieldConfConstants.LOCK_TIME);
			searchCriteria.addFieldValue(start);
			searchCriteria.addFieldValue(end);
			searchfieldVos.add(searchCriteria);
		}
		// 如果领取结束时间不为空
		if (request.getLockTimeStart() == null && request.getLockTimeEnd() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");
			String end = sdf.format(request.getLockTimeEnd());
			Timestamp sTime = Timestamp.valueOf(OrdersConstants.START_TIME);
			String start = sdf.format(sTime);
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setOption(new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.range));
			searchCriteria.setField(SearchFieldConfConstants.LOCK_TIME);
			searchCriteria.addFieldValue(start);
			searchCriteria.addFieldValue(end);
			searchfieldVos.add(searchCriteria);
		}
		// 如果支付开始、结束时间不为空
		if (request.getPayTimeStart() != null && request.getPayTimeEnd() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");
			String start = sdf.format(request.getPayTimeStart());
			String end = sdf.format(request.getPayTimeEnd());
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setOption(new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.range));
			searchCriteria.setField(SearchFieldConfConstants.PAY_TIME);
			searchCriteria.addFieldValue(start);
			searchCriteria.addFieldValue(end);
			searchfieldVos.add(searchCriteria);
		}
		// 如果支付开始时间不为空
		if (request.getPayTimeStart() != null && request.getPayTimeEnd() == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");
			String start = sdf.format(request.getPayTimeStart());
			String end = sdf.format(new Date());
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setOption(new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.range));
			searchCriteria.setField(SearchFieldConfConstants.PAY_TIME);
			searchCriteria.addFieldValue(start);
			searchCriteria.addFieldValue(end);
			searchfieldVos.add(searchCriteria);
		}
		// 如果支付结束时间不为空
		if (request.getPayTimeStart() == null && request.getPayTimeEnd() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");
			String end = sdf.format(request.getPayTimeEnd());
			Timestamp sTime = Timestamp.valueOf(OrdersConstants.START_TIME);
			String start = sdf.format(sTime);
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setOption(new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.range));
			searchCriteria.setField(SearchFieldConfConstants.PAY_TIME);
			searchCriteria.addFieldValue(start);
			searchCriteria.addFieldValue(end);
			searchfieldVos.add(searchCriteria);
		}
		// 如果报价开始、结束时间不为空
		if (request.getUpdateTimeStart() != null && request.getUpdateTimeEnd() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");
			String start = sdf.format(request.getUpdateTimeStart());
			String end = sdf.format(request.getUpdateTimeEnd());
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setOption(new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.range));
			searchCriteria.setField(SearchFieldConfConstants.UPDATE_TIME);
			searchCriteria.addFieldValue(start);
			searchCriteria.addFieldValue(end);
			searchfieldVos.add(searchCriteria);
		}
		// 如果报价开始时间不为空
		if (request.getUpdateTimeStart() != null && request.getUpdateTimeEnd() == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");
			String start = sdf.format(request.getUpdateTimeStart());
			String end = sdf.format(new Date());
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setOption(new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.range));
			searchCriteria.setField(SearchFieldConfConstants.UPDATE_TIME);
			searchCriteria.addFieldValue(start);
			searchCriteria.addFieldValue(end);
			searchfieldVos.add(searchCriteria);
		}
		// 如果报价结束时间不为空
		if (request.getUpdateTimeStart() == null && request.getUpdateTimeEnd() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");
			String end = sdf.format(request.getUpdateTimeEnd());
			Timestamp sTime = Timestamp.valueOf(OrdersConstants.START_TIME);
			String start = sdf.format(sTime);
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setOption(new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.range));
			searchCriteria.setField(SearchFieldConfConstants.UPDATE_TIME);
			searchCriteria.addFieldValue(start);
			searchCriteria.addFieldValue(end);
			searchfieldVos.add(searchCriteria);
		}
		// 如果状态开始、结束时间不为空
		if (request.getStateTimeStart() != null && request.getStateTimeEnd() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");
			String start = sdf.format(request.getStateTimeStart());
			String end = sdf.format(request.getStateTimeEnd());
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setOption(new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.range));
			searchCriteria.setField(SearchFieldConfConstants.STATE_TIME);
			searchCriteria.addFieldValue(start);
			searchCriteria.addFieldValue(end);
			searchfieldVos.add(searchCriteria);
		}
		// 如果状态开始时间不为空
		if (request.getStateTimeStart() != null && request.getStateTimeEnd() == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");
			String start = sdf.format(request.getStateTimeStart());
			String end = sdf.format(new Date());
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setOption(new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.range));
			searchCriteria.setField(SearchFieldConfConstants.STATE_TIME);
			searchCriteria.addFieldValue(start);
			searchCriteria.addFieldValue(end);
			searchfieldVos.add(searchCriteria);
		}
		// 如果状态结束时间不为空
		if (request.getStateTimeStart() == null && request.getStateTimeEnd() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");
			String end = sdf.format(request.getStateTimeEnd());
			Timestamp sTime = Timestamp.valueOf(OrdersConstants.START_TIME);
			String start = sdf.format(sTime);
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setOption(new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.range));
			searchCriteria.setField(SearchFieldConfConstants.STATE_TIME);
			searchCriteria.addFieldValue(start);
			searchCriteria.addFieldValue(end);
			searchfieldVos.add(searchCriteria);
		}
		// 状态集合不为空
		if (!CollectionUtil.isEmpty(request.getStateList())) {
			SearchCriteria searchCriteria = new SearchCriteria();
			SearchOption option = new SearchOption();
			option.setSearchLogic(SearchOption.SearchLogic.must);
			option.setSearchType(SearchOption.SearchType.term);
			searchCriteria.setFieldValue(request.getStateList());
			searchCriteria.setField(SearchFieldConfConstants.STATE);
			searchCriteria.setOption(option);
			searchfieldVos.add(searchCriteria);
		}

		// 如果评价开始、结束时间不为空
		if (request.getEvaluateTimeStart() != null && request.getEvaluateTimeEnd() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");
			String start = sdf.format(request.getEvaluateTimeStart());
			String end = sdf.format(request.getEvaluateTimeEnd());
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setOption(new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.range));
			searchCriteria.setField(SearchFieldConfConstants.EVALUATE_TIME);
			searchCriteria.addFieldValue(start);
			searchCriteria.addFieldValue(end);
			searchfieldVos.add(searchCriteria);
		}
		// 如果评价开始时间不为空
		if (request.getEvaluateTimeStart() != null && request.getEvaluateTimeEnd() == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");
			String start = sdf.format(request.getEvaluateTimeStart());
			String end = sdf.format(new Date());
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setOption(new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.range));
			searchCriteria.setField(SearchFieldConfConstants.EVALUATE_TIME);
			searchCriteria.addFieldValue(start);
			searchCriteria.addFieldValue(end);
			searchfieldVos.add(searchCriteria);
		}
		// 如果评价结束时间不为空
		if (request.getEvaluateTimeStart() == null && request.getEvaluateTimeEnd() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");
			String end = sdf.format(request.getEvaluateTimeEnd());
			Timestamp sTime = Timestamp.valueOf(OrdersConstants.START_TIME);
			String start = sdf.format(sTime);
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setOption(new SearchOption(SearchOption.SearchLogic.must, SearchOption.SearchType.range));
			searchCriteria.setField(SearchFieldConfConstants.EVALUATE_TIME);
			searchCriteria.addFieldValue(start);
			searchCriteria.addFieldValue(end);
			searchfieldVos.add(searchCriteria);
		}
		//如果lspid和interperid都不为空
		if(!StringUtil.isBlank(request.getInterperId()) && !StringUtil.isBlank(request.getLspId())){
			SearchCriteria vo = new SearchCriteria();
            vo.addSubCriteria(new SearchCriteria(SearchFieldConfConstants.INTERPER_ID, request.getCorporaId(), new SearchOption(SearchLogic.should, SearchType.querystring)));
            vo.addSubCriteria(new SearchCriteria(SearchFieldConfConstants.OPER_INTERPER_ID, request.getUserId(), new SearchOption(SearchLogic.should, SearchType.querystring)));
            searchfieldVos.add(vo);
		}
		return searchfieldVos;
	}

	@Override
	public QueryOrdCountResponse queryOrderCount(QueryOrdCountRequest request)
			throws BusinessException, SystemException {
		request.setTenantId(OrdersConstants.TENANT_ID);
		// 有效性校验
		ValidateUtils.validateQueryOrdCount(request);
		QueryOrdCountResponse response = new QueryOrdCountResponse();
		response.setCountMap(iOrdOrderBusiSV.findOrderCount(request));
		ResponseHeader responseHeader = new ResponseHeader(true, ExceptCodeConstants.Special.SUCCESS, "查询订单数成功");
		response.setResponseHeader(responseHeader);
		return response;
	}

	@Override
	public QueryOrdCountResponse queryOrderCount4TaskCenter(QueryOrdCountRequest request)
			throws BusinessException, SystemException {
		request.setTenantId(OrdersConstants.TENANT_ID);
		// 有效性校验
		ValidateUtils.validateQueryOrdCount(request);
		QueryOrdCountResponse response = new QueryOrdCountResponse();
		response.setCountMap(iOrdOrderBusiSV.findOrderCount4TaskCenter(request));
		ResponseHeader responseHeader = new ResponseHeader(true, ExceptCodeConstants.Special.SUCCESS, "查询订单数成功");
		response.setResponseHeader(responseHeader);
		return response;
	}

	@Override
	public OrdOrderCountResponse queryOrderCountInfo(OrdOrderCountRequest request)
			throws BusinessException, SystemException {
		return iOrdOrderBusiSV.countInfo(request);
	}

}
