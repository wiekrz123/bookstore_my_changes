package pl.bookstore.hibernatemodel;
// Generated 2018-04-16 11:57:08 by Hibernate Tools 5.2.8.Final

import java.util.HashSet;
import java.util.Set;

/**
 * Author generated by hbm2java
 */
@SuppressWarnings("rawtypes")
public class Author implements java.io.Serializable {

	private static final long serialVersionUID = 0;
	private Integer id;
	private String name;
	private String surname;
	private String nickname;
	private Set books = new HashSet(0);

	public Author() {
	}

	public Author(String name, String surname) {
		this.name = name;
		this.surname = surname;
	}

	public Author(String name, String surname, String nickname, Set books) {
		this.name = name;
		this.surname = surname;
		this.nickname = nickname;
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

	public String getSurname() {
		return this.surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getNickname() {
		return this.nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Set getBooks() {
		return this.books;
	}

	public void setBooks(Set books) {
		this.books = books;
	}

}
