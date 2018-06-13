package pl.bookstore.hibernatemodel;
// Generated 2018-04-16 11:57:08 by Hibernate Tools 5.2.8.Final

import java.util.HashSet;
import java.util.Set;

/**
 * Publisher generated by hbm2java
 */
@SuppressWarnings("rawtypes")
public class Publisher implements java.io.Serializable {

	private static final long serialVersionUID = 0;
	private Integer id;
	private String name;
	private String address;
	private String email;
	private Set books = new HashSet(0);

	public Publisher() {
	}

	public Publisher(String name, String address) {
		this.name = name;
		this.address = address;
	}

	public Publisher(String name, String address, String email, Set books) {
		this.name = name;
		this.address = address;
		this.email = email;
		this.books = books;
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

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Set getBooks() {
		return this.books;
	}

	public void setBooks(Set books) {
		this.books = books;
	}

}