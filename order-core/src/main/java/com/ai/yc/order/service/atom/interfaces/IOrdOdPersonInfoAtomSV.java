package com.ai.yc.order.service.atom.interfaces;

import java.util.List;

import com.ai.yc.order.dao.mapper.bo.OrdOdPersonInfo;

public interface IOrdOdPersonInfoAtomSV {
	public void insertSelective(OrdOdPersonInfo ordOdPersonInfo);

	public void updateSelective(OrdOdPersonInfo ordOdPersonInfo);

	List<OrdOdPersonInfo> findPersonInfo(OrdOdPersonInfo ordOdPersonInfo);
}
