package pl.bookstore.filter;

import pl.bookstore.hibernatemodel.*;

public class FilterFactory {

	public static Filter getFilter(String name) {
		if (name.equals("Author")) {
			return new AuthorFilter();
		} else if (name.equals("Book")) {
			return new BookFilter();
		} else if (name.equals("Category")) {
			return new CategoryFilter();
		} else if (name.equals("Deliverer")) {
			return new DelivererFilter();
		} else if (name.equals("Orderinfo")) {
			return new OrderinfoFilter();
		} else if (name.equals("Publisher")) {
			return new PublisherFilter();
		} else if (name.equals("Statusinfo")) {
			return new StatusinfoFilter();
		} else if (name.equals("User")) {
			return new UserFilter();
		} else {
			return null;
		}
	}
	

	@SuppressWarnings("rawtypes")
	public static Filter getFilter(Class c) {
		if (c == Author.class) {
			return new AuthorFilter();
		} else if (c == Book.class) {
			return new BookFilter();
		} else if (c == Category.class) {
			return new CategoryFilter();
		} else if (c == Deliverer.class) {
			return new DelivererFilter();
		} else if (c == Orderinfo.class) {
			return new OrderinfoFilter();
		} else if (c == Publisher.class) {
			return new PublisherFilter();
		} else if (c == Statusinfo.class) {
			return new StatusinfoFilter();
		} else if (c == User.class) {
			return new UserFilter();
		} else {
			return null;
		}
	}
}
