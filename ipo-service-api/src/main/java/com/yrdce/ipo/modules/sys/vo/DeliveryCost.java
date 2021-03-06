package com.yrdce.ipo.modules.sys.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Bob
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DeliveryCost implements Serializable {
	private String deliveryId;

	private String deliveryMethod;

	private Date applyDate;

	private BigDecimal insurance;

	private BigDecimal trusteeFee;

	private BigDecimal warehousingFee;

	private BigDecimal deliveryFee;

	private Date deliveryDate;

	public BigDecimal registrationFee;// 注册费

	public BigDecimal cancellationFee;// 注销费

	public BigDecimal getRegistrationFee() {
		return registrationFee;
	}

	public void setRegistrationFee(BigDecimal registrationFee) {
		this.registrationFee = registrationFee;
	}

	public BigDecimal getCancellationFee() {
		return cancellationFee;
	}

	public void setCancellationFee(BigDecimal cancellationFee) {
		this.cancellationFee = cancellationFee;
	}

	public String getDeliveryId() {
		return deliveryId;
	}

	public void setDeliveryId(String deliveryId) {
		this.deliveryId = deliveryId;
	}

	public String getDeliveryMethod() {
		return deliveryMethod;
	}

	public void setDeliveryMethod(String deliveryMethod) {
		this.deliveryMethod = deliveryMethod == null ? null : deliveryMethod
				.trim();
	}

	public Date getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}

	public BigDecimal getInsurance() {
		return insurance;
	}

	public void setInsurance(BigDecimal insurance) {
		this.insurance = insurance;
	}

	public BigDecimal getTrusteeFee() {
		return trusteeFee;
	}

	public void setTrusteeFee(BigDecimal trusteeFee) {
		this.trusteeFee = trusteeFee;
	}

	public BigDecimal getWarehousingFee() {
		return warehousingFee;
	}

	public void setWarehousingFee(BigDecimal warehousingFee) {
		this.warehousingFee = warehousingFee;
	}

	public BigDecimal getDeliveryFee() {
		return deliveryFee;
	}

	public void setDeliveryFee(BigDecimal deliveryFee) {
		this.deliveryFee = deliveryFee;
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
}