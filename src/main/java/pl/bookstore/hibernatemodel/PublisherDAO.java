package pl.bookstore.hibernatemodel;

import java.math.BigInteger;
// Generated 2018-04-16 11:57:15 by Hibernate Tools 5.2.8.Final
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Example;

import pl.bookstore.utils.HibernateUtil;

/**
 * Home object for domain model class Publisher.
 * @see pl.bookstore.hibernatemodel.Publisher
 * @author Hibernate Tools
 */
@SuppressWarnings({ "rawtypes", "deprecation" })
public class PublisherDAO extends DAO {

	private static final Log log = LogFactory.getLog(PublisherDAO.class);

	private static final Session session = HibernateUtil.getSession();


	public void persist(Object transientInstance) {
		log.debug("persisting Publisher instance");
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			session.persist((Publisher) transientInstance);
			transaction.commit();
			log.debug("persist successful");
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Object instance) {
		log.debug("attaching dirty Publisher instance");
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			session.saveOrUpdate((Publisher) instance);
			transaction.commit();
			log.debug("attach successful");
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("attach failed", re);
			throw re;
		}
	}


	public void delete(Object persistentInstance) {
		log.debug("deleting Publisher instance");
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			session.delete((Publisher) persistentInstance);
			transaction.commit();
			log.debug("delete successful");
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("delete failed", re);
			throw re;
		}
	}


	public Publisher findById(Integer id) {
		log.debug("getting Publisher instance with id: " + id);
		if (id == null) return null;
		Transaction transaction = session.getTransaction();		
		try {
			transaction.begin();
			Publisher instance = (Publisher) session.get("pl.bookstore.hibernatemodel.Publisher", id);
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

	public List findByExampleWithPagination(Publisher instance, Integer pageNo, Integer pageSize) {
		log.debug("finding Publisher instances by example");
		if (instance == null) return null;
		Transaction transaction = session.getTransaction();

		try {
			int pSize = 20;
			if (pageSize != null)	pSize = pageSize;

			int pNo = 1;
			if (pageNo != null)		pNo = pageNo;

			transaction.begin();
			Criteria criteria = session.createCriteria(Publisher.class);
			criteria.add(Example.create(instance));
			criteria.setFirstResult(pSize*(pNo));
			criteria.setMaxResults(pSize);
			List results = criteria.list();
			transaction.commit();
			log.debug("find by example successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("find by example failed", re);
			throw re;
		}
	}

	public long getSize() {
		long result = 0;
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			Query query = session.createQuery("SELECT count(*) FROM pl.bookstore.hibernatemodel.Publisher");
			result = (long) query.uniqueResult();
			transaction.commit();
			return result;
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("getSize failed", re);
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
		log.debug("fetch all Publishers");
		Transaction transaction = session.getTransaction();
		try {
			int pSize = 20;
			if (pageSize != null)	pSize = pageSize;

			int pNo = 1;
			if (pageNo != null)		pNo = pageNo;

			transaction.begin();
			Query query = session.createQuery("FROM pl.bookstore.hibernatemodel.Publisher");
			query.setMaxResults(pSize);
			query.setFirstResult(pSize*(pNo-1));
			List results = query.list();
			transaction.commit();
			log.debug("fetch all Publishers successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("fetch all Publishers failed", re);
			throw re;
		}
	}

	public List getAvailableBooksWPagination(
			Publisher instance,
			Integer pageNo,
			Integer pageSize,
			String sortBy,
			String sortType,
			String filters,
			String search) {
		log.debug("fetching Books by Publisher with Pagination");
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
					"WHERE book.publisher = :instance and book.available='1' " +
					"ORDER BY book." + sBy + " " + sType;

			transaction.begin();
			Query query = session.createQuery(hql);
			query.setParameter("instance", instance);
			query.setMaxResults(pSize);
			query.setFirstResult(pSize*(pNo-1));
			List results = query.list();
			transaction.commit();
			log.debug("fetching Books by Publisher with Pagination, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("fetching Books by Publisher with Pagination failed", re);
			throw re;
		}
	}

	public long getBookCount(Publisher instance) {
		if (instance == null) return -1;
		long result = -1;
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			// TODO
			// convert to hql
			/*String hql =	"SELECT count(*) FROM pl.bookstore.hibernatemodel.Book AS book " +
							"WHERE book.available='1' AND book.publisher = :publisher";*/
			String sql = "SELECT COUNT(book.id) "
					+ "FROM book "
					+ "WHERE book.available='1' AND book.publisherID = " + instance.getId();
			SQLQuery query = session.createSQLQuery(sql);
			result = ((BigInteger) query.uniqueResult()).longValue();
			transaction.commit();
			return result;
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("getBookCount failed", re);
			throw re;
		}
	}
}
