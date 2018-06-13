package pl.bookstore.hibernatemodel;
// Generated 2018-04-16 11:57:15 by Hibernate Tools 5.2.8.Final

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import pl.bookstore.utils.HibernateUtil;

/**
 * Home object for domain model class Book.
 * @see pl.bookstore.hibernatemodel.Book
 * @author Hibernate Tools
 */
@SuppressWarnings({ "rawtypes", "deprecation" })
public class BookDAO extends DAO {

	private static final Log log = LogFactory.getLog(BookDAO.class);

	private static final Session session = HibernateUtil.getSession();

	public void persist(Object transientInstance) {
		log.debug("persisting Book instance");
		//Session newSession = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			session.persist((Book) transientInstance);
			log.debug("generated id: " + ((Book) transientInstance).getId());
			transaction.commit();
			log.debug("persist successful");
		} catch (RuntimeException re) {
			transaction.rollback();
			//newSession.close();
			log.error("persist failed", re);
			throw re;
		}
		//newSession.close();
	}

	public void attachDirty(Object instance) {
		log.debug("attaching dirty Book instance");
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			session.saveOrUpdate((Book) instance);
			transaction.commit();
			log.debug("attach successful");
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Object persistentInstance) {
		log.debug("deleting Book instance");
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			session.delete((Book) persistentInstance);
			transaction.commit();
			log.debug("delete successful");
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("delete failed", re);
			throw re;
		}
	}

	public Book findById(java.lang.Integer id) {
		log.debug("getting Book instance with id: " + id);
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			Book instance = (Book) session.get("pl.bookstore.hibernatemodel.Book", id);
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

	public Book findByIdWDetails(Integer id) {
		log.debug("getting Book instance with id: " + id);
		Transaction transaction = session.getTransaction();
		try {
			String hql =	"FROM pl.bookstore.hibernatemodel.Book as book " +
					"JOIN FETCH book.publisher " +
					"JOIN FETCH book.authors " +
					"JOIN FETCH book.categories " +
					"WHERE book.id = :bookID";
			transaction.begin();
			Query query = session.createQuery(hql);
			query.setParameter("bookID", id);
			Book instance = (Book) query.uniqueResult();
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

	public long getSize() {
		long result = 0;
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			Query query = session.createQuery("SELECT count(*) FROM pl.bookstore.hibernatemodel.Book");
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
			Integer pageNo,
			Integer pageSize,
			String sortBy,
			String sortType,
			String filters,
			String search) {
		log.debug("fetch all Books");
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
					"JOIN FETCH book.publisher " +
					"JOIN FETCH book.authors " +
					"JOIN FETCH book.categories " +
					"ORDER BY book." + sBy + " " + sType;

			transaction.begin();
			Query query = session.createQuery(hql);
			query.setMaxResults(pSize);
			query.setFirstResult(pSize*(pNo-1));
			List results = query.list();
			transaction.commit();
			log.debug("fetch all Books successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("fetch all Books failed", re);
			throw re;
		}
	}

	public long getBookCount(
			String filters,
			String search) {
		//
		// TODO
		// add support for filters
		// add support for search

		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			String hql = "SELECT count(*) FROM pl.bookstore.hibernatemodel.Book ";
			Query query = session.createQuery(hql);
			long result = (long) query.uniqueResult();
			transaction.commit();
			return result;
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("get failed", re);
			throw re;
		}
	}
}
