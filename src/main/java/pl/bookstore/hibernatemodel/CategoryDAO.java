package pl.bookstore.hibernatemodel;
// Generated 2018-04-16 11:57:15 by Hibernate Tools 5.2.8.Final

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import pl.bookstore.utils.HibernateUtil;

/**
 * Home object for domain model class Category.
 * @see pl.bookstore.hibernatemodel.Category
 * @author Hibernate Tools
 */
@SuppressWarnings({ "rawtypes", "deprecation" })
public class CategoryDAO extends DAO {

	private static final Log log = LogFactory.getLog(CategoryDAO.class);

	private static final Session session = HibernateUtil.getSession();

	public void persist(Object transientInstance) {
		log.debug("persisting Category instance");
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			session.persist((Category) transientInstance);
			transaction.commit();
			log.debug("persist successful");
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Object instance) {
		log.debug("attaching dirty Category instance");
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			session.saveOrUpdate((Category) instance);
			transaction.commit();
			log.debug("attach successful");
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("attach failed", re);
			throw re;
		}
	}


	public void delete(Object persistentInstance) {
		log.debug("deleting Category instance");
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			session.delete((Category) persistentInstance);
			transaction.commit();
			log.debug("delete successful");
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("delete failed", re);
			throw re;
		}
	}


	public Category findById(Integer id) {
		log.debug("getting Category instance with id: " + id);
		if (id == null) return null;
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			Category instance = (Category) session.get("pl.bookstore.hibernatemodel.Category",
					id);
			transaction.commit();
			if (instance == null) {
				log.debug("get successful, no instance found");
			} else {
				log.debug("get successful, instance found");
			}
			return instance;
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("get failed", re);
			throw re;
		}
	}

	public long getCategoryCount(Boolean showInvisible) {
		//
		// TODO
		// add support for filters
		// add support for search

		long result = 0;
		boolean sInvisible = false;
		if (showInvisible != null) sInvisible = showInvisible;

		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			Query query;
			if (sInvisible) {
				query = session.createQuery("SELECT count(*) FROM pl.bookstore.hibernatemodel.Category");
			} else {
				query = session.createQuery("SELECT count(*) FROM pl.bookstore.hibernatemodel.Category WHERE visible='1'");
			}
			result = (long) query.uniqueResult();
			transaction.commit();
			return result;
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("get failed", re);
			throw re;
		}
	}

	public List getWithPagination(
			Boolean showInvisible,
			Integer pageNo,
			Integer pageSize,
			String sortBy,
			String sortType) {
		log.debug("fetch all Categories");
		Transaction transaction = session.getTransaction();
		try {
			int pSize = 20;
			if (pageSize != null)	pSize = pageSize;

			int pNo = 1;
			if (pageNo != null)		pNo = pageNo;

			boolean sInvisible = false;
			if (showInvisible != null)		sInvisible = showInvisible;

			String sBy = "id";
			if (sortBy != null)		sBy = sortBy;

			String sType = "ASC";
			if (sortType != null)		sType = sortType;

			transaction.begin();
			String hql = "FROM pl.bookstore.hibernatemodel.Category as category ";
			if (!sInvisible) hql += "WHERE visible='1' ";
			hql += "ORDER BY category." + sBy + " " + sType;
			Query query = session.createQuery(hql);
			query.setMaxResults(pSize);
			query.setFirstResult(pSize*(pNo-1));
			List results = query.list();
			transaction.commit();
			log.debug("fetch all Categories successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("fetch all Categories failed", re);
			throw re;
		}
	}

	public List getAvailableBooksWPagination(
			Category instance,
			Integer pageNo,
			Integer pageSize,
			String sortBy,
			String sortType,
			String filters,
			String search) {
		//
		// TODO
		// add support for filters
		// add support for search
		log.debug("fetching Books by Category with Pagination");
		if (instance == null) return null;

		Transaction transaction = session.getTransaction();

		try {
			int pSize = 20;
			if (pageSize != null)	pSize = pageSize;

			int pNo = 1;
			if (pageNo != null)		pNo = pageNo;

			String sBy = "id";
			if (sortBy != null)		sBy = sortBy;

			String sType = "ASC";
			if (sortType != null)		sType = sortType;

			transaction.begin();
			String hql =	"FROM pl.bookstore.hibernatemodel.Book AS book " +
					"JOIN FETCH book.categories category " +
					"WHERE category = :instance and book.available='1' " +
					"ORDER BY book." + sBy + " " + sType;
			Query query = session.createQuery(hql);
			query.setParameter("instance", instance);
			query.setMaxResults(pSize);
			query.setFirstResult(pSize*(pNo-1));
			List results = query.list();
			transaction.commit();
			log.debug("fetching Books by Category with Pagination, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("fetching Books by Category with Pagination failed", re);
			throw re;
		}
	}

	public long getBookCount(Category instance, String filters, String search) {
		//
		// TODO
		// add support for filters
		// add support for search
		if (instance == null) return -1;
		long result = -1;
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			// TODO
			// convert to hql
			String sql = "SELECT COUNT(book.id) "
					+ "FROM book join book_category on book.id = book_category.bookID "
					+ "join category on category.id = book_category.categoryID "
					+ "WHERE book.available='1' AND category.id = " + instance.getId();
			SQLQuery query = session.createSQLQuery(sql);
			result = Long.parseLong(query.uniqueResult().toString());
			transaction.commit();
			return result;
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("getBookCount faled", re);
			throw re;
		}
	}

	@Override
	public List getWithPagination(Integer pageNo, Integer pageSize, String sortBy, String sortType, String filters,
			String search) {
		// do not use
		return null;
	}
}
