package com.ai.yc.order.api.orderallocation.param;

import java.io.Serializable;
import java.sql.Timestamp;

public class OrdAllocationPersonInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 人员信息id，用于修改时使用
	 */
	private Long personId;
	/**
	 * 译员id
	 */
	private String interperId;
	/**
	 * 议员名称
	 */
	private String interperName;
	/**
	 * 联系方式
	 */
	private String tel;
	/**
	 * 译员佣金
	 */
	private Long interperFee;
	/**
	 * 期望完成时间
	 */
	private Timestamp expectEndTime;
	/**
	 * 备注
	 */
	private String reamrk;

	public String getInterperId() {
		return interperId;
	}

	public void setInterperId(String interperId) {
		this.interperId = interperId;
	}

	public String getInterperName() {
		return interperName;
	}

	public void setInterperName(String interperName) {
		this.interperName = interperName;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public Long getInterperFee() {
		return interperFee;
	}

	public void setInterperFee(Long interperFee) {
		this.interperFee = interperFee;
	}

	public Timestamp getExpectEndTime() {
		return expectEndTime;
	}

	public void setExpectEndTime(Timestamp expectEndTime) {
		this.expectEndTime = expectEndTime;
	}

	public String getReamrk() {
		return reamrk;
	}

	public void setReamrk(String reamrk) {
		this.reamrk = reamrk;
	}

	public Long getPersonId() {
		return personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}

}
