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
 * Home object for domain model class Author.
 * @see pl.bookstore.hibernatemodel.Author
 * @author Hibernate Tools
 */
@SuppressWarnings({ "rawtypes", "deprecation" })
public class AuthorDAO extends DAO {

	private static final Log log = LogFactory.getLog(AuthorDAO.class);

	private static final Session session = HibernateUtil.getSession();

	public void persist(Object transientInstance) {
		log.debug("persisting Author instance");
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			session.persist((Author) transientInstance);
			transaction.commit();
			log.debug("persist successful");
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Object instance) {
		log.debug("attaching dirty Author instance");
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			session.saveOrUpdate((Author) instance);
			transaction.commit();
			log.debug("attach successful");
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("attach failed", re);
			throw re;
		}
	}

	@Override
	public void delete(Object persistentInstance) {
		log.debug("deleting Author instance");
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			session.delete((Author) persistentInstance);
			transaction.commit();
			log.debug("delete successful");
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("delete failed", re);
			throw re;
		}
	}

	public Author findById(Integer id) {
		log.debug("getting Author instance with id: " + id);
		if (id == null) return null;
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			Author instance = (Author) session.get("pl.bookstore.hibernatemodel.Author", id);
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

	public long getAuthorCount(
			String filters,
			String search) {
		//
		// TODO
		// add support for filters
		// add support for search

		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			String hql = "SELECT count(*) FROM pl.bookstore.hibernatemodel.Author ";
			Query query = session.createQuery(hql);
			long result = (long) query.uniqueResult();
			transaction.commit();
			return result;
		}  catch (RuntimeException re) {
			transaction.rollback();
			log.error("get failed", re);
			throw re;
		}
	}

	public List getWithPagination(
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

		log.debug("getWithPagination("+pageNo+", "+pageSize+")");
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
			String hql = "FROM pl.bookstore.hibernatemodel.Author AS author ORDER BY author." + sBy + " " + sType;
			Query query = session.createQuery(hql);
			query.setMaxResults(pSize);
			query.setFirstResult(pSize*(pNo-1));
			List results = query.list();
			transaction.commit();
			log.debug("fetch all Authors successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("fetch all Authors failed", re);
			throw re;
		}
	}

	public List getAvailableBooksWPagination(
			Author instance,
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
		log.debug("fetching Books by Author with Pagination");
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

			String hql =	"FROM pl.bookstore.hibernatemodel.Book AS book " +
					"JOIN FETCH book.authors author " +
					"WHERE book.available='1' AND " +
					"author = :instance " +
					"ORDER BY book." + sBy + " " + sType;


			transaction.begin();
			Query query = session.createQuery(hql);
			query.setParameter("instance", instance);
			query.setMaxResults(pSize);
			query.setFirstResult(pSize*(pNo-1));
			List results = query.list();
			transaction.commit();
			log.debug("fetching Books by Author with Pagination, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("fetching Books by Author with Pagination failed", re);
			throw re;
		}
	}

	public long getBookCount(
			Author instance,
			String filters,
			String search) {
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
					+ "FROM book join book_author on book.id = book_author.bookID "
					+ "join author on author.id = book_author.authorID "
					+ "WHERE book.available = '1' AND author.id = " + instance.getId();
			SQLQuery query = session.createSQLQuery(sql);
			result = Long.parseLong(query.uniqueResult().toString());
			transaction.commit();
			return result;
		}  catch (RuntimeException re) {
			transaction.rollback();
			log.error("getBookCount failed", re);
			throw re;
		}
	}
}
