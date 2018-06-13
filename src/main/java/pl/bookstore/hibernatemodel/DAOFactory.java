package pl.bookstore.hibernatemodel;

public class DAOFactory {
	public static DAO getDAO(String name) {
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	public static DAO getDAO(Class c) {
		if(c.equals( Author.class)) {
			return new AuthorDAO();
		} else if (c.equals(Book.class)) {
			return new BookDAO();
		} else if (c.equals(Category.class)) {
			return new CategoryDAO();
		} else if (c.equals(Deliverer.class)) {
			return new DelivererDAO();
		} else if (c.equals(Orderinfo.class)) {
			return new OrderinfoDAO();
		} else if (c.equals(Orderitem.class)) {
			return new OrderitemDAO();
		} else if (c.equals(Publisher.class)) {
			return new PublisherDAO();
		} else if (c.equals(Statusinfo.class)) {
			return new StatusinfoDAO();
		} else if (c.equals(User.class)) {
			return new UserDAO();
		} else {
			return null;
		}
	}
}
