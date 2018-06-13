package pl.bookstore.hibernatemodel;
// Generated 2018-04-16 11:57:08 by Hibernate Tools 5.2.8.Final

import java.util.HashSet;
import java.util.Set;

/**
 * Statusinfo generated by hbm2java
 */
@SuppressWarnings("rawtypes")
public class Statusinfo implements java.io.Serializable {

	private static final long serialVersionUID = 0;
	private Integer id;
	private boolean available;
	private String name;
	private Set orderinfos = new HashSet(0);

	public Statusinfo() {
	}

	public Statusinfo(boolean available, String name) {
		this.available = available;
		this.name = name;
	}

	public Statusinfo(boolean available, String name, Set orderinfos) {
		this.available = available;
		this.name = name;
		this.orderinfos = orderinfos;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public boolean isAvailable() {
		return this.available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set getOrderinfos() {
		return this.orderinfos;
	}

	public void setOrderinfos(Set orderinfos) {
		this.orderinfos = orderinfos;
	}

}