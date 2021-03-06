package pl.bookstore.hibernatemodel;
// Generated 2018-04-16 11:57:08 by Hibernate Tools 5.2.8.Final

import java.util.HashSet;
import java.util.Set;

/**
 * User generated by hbm2java
 */
@SuppressWarnings("rawtypes")
public class User implements java.io.Serializable {

	private static final long serialVersionUID = 0;
	private Integer id;
	private String name;
	private String surname;
	private String email;
	private String password;
	private boolean employee;
	private boolean active;
	private Set orderinfos = new HashSet(0);

	public User() {
	}

	public User(String name, String surname, String email, String password, boolean employee, boolean active) {
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.password = password;
		this.employee = employee;
		this.active = active;
	}

	public User(String name, String surname, String email, String password, boolean employee, boolean active,
			Set orderinfos) {
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.password = password;
		this.employee = employee;
		this.active = active;
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

	public String getSurname() {
		return this.surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isEmployee() {
		return this.employee;
	}

	public void setEmployee(boolean employee) {
		this.employee = employee;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Set getOrderinfos() {
		return this.orderinfos;
	}

	public void setOrderinfos(Set orderinfos) {
		this.orderinfos = orderinfos;
	}

}
