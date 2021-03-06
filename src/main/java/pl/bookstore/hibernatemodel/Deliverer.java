package pl.bookstore.hibernatemodel;
// Generated 2018-04-16 11:57:08 by Hibernate Tools 5.2.8.Final

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Deliverer generated by hbm2java
 */
@SuppressWarnings("rawtypes")
public class Deliverer implements java.io.Serializable {

	private static final long serialVersionUID = 0;
	private Integer id;
	private String name;
	private String deliveryName;
	private String address;
	private BigDecimal pricePerPackage;
	private boolean available;
	private Set orderinfos = new HashSet(0);

	public Deliverer() {
	}

	public Deliverer(String name, String deliveryName, String address, BigDecimal pricePerPackage, boolean available) {
		this.name = name;
		this.deliveryName = deliveryName;
		this.address = address;
		this.pricePerPackage = pricePerPackage;
		this.available = available;
	}

	public Deliverer(String name, String deliveryName, String address, BigDecimal pricePerPackage, boolean available,
			Set orderinfos) {
		this.name = name;
		this.deliveryName = deliveryName;
		this.address = address;
		this.pricePerPackage = pricePerPackage;
		this.available = available;
		this.orderinfos = orderinfos;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDeliveryName() {
		return this.deliveryName;
	}

	public void setDeliveryName(String deliveryName) {
		this.deliveryName = deliveryName;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public BigDecimal getPricePerPackage() {
		return this.pricePerPackage;
	}

	public void setPricePerPackage(BigDecimal pricePerPackage) {
		this.pricePerPackage = pricePerPackage;
	}

	public boolean isAvailable() {
		return this.available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public Set getOrderinfos() {
		return this.orderinfos;
	}

	public void setOrderinfos(Set orderinfos) {
		this.orderinfos = orderinfos;
	}

}
