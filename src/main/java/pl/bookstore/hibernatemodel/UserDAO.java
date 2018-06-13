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
 * Home object for domain model class User.
 * @see pl.bookstore.hibernatemodel.User
 * @author Hibernate Tools
 */
@SuppressWarnings({ "rawtypes", "deprecation" })
public class UserDAO extends DAO {

	private static final Log log = LogFactory.getLog(UserDAO.class);

	private static final Session session = HibernateUtil.getSession();

	public void persist(Object transientInstance) {
		log.debug("persisting User instance");
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			session.persist((User) transientInstance);
			transaction.commit();
			log.debug("persist successful");
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Object instance) {
		log.debug("attaching dirty User instance");
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			session.saveOrUpdate((User) instance);
			transaction.commit();
			log.debug("attach successful");
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Object persistentInstance) {
		log.debug("deleting User instance");
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			session.delete((User) persistentInstance);
			transaction.commit();
			log.debug("delete successful");
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("delete failed", re);
			throw re;
		}
	}

	public User findById(Integer id) {
		log.debug("getting User instance with id: " + id);
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			User instance = (User) session.get("pl.bookstore.hibernatemodel.User", id);
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

	public long getOrderCount(User instance, String filters,	String search) {
		//
		// TODO
		// add support for filters
		// add support for search
		long result = 0;
		String hql =	"SELECT count(*) FROM pl.bookstore.hibernatemodel.Orderinfo AS orderinfo " +
				// "JOIN FETCH Orderinfo.user " +
				"WHERE orderinfo.user = :instance";
		
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			Query query = session.createQuery(hql);
			query.setParameter("instance", instance);
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
		//
		// TODO
		// add support for filters
		// add support for search
		log.debug("fetch all User");
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

			String hql =	"FROM pl.bookstore.hibernatemodel.User AS user " +
					"ORDER BY user." + sBy + " " + sType;
			Query query = session.createQuery(hql);
			query.setMaxResults(pSize);
			query.setFirstResult(pSize*(pNo-1));
			List results = query.list();
			transaction.commit();
			log.debug("fetch all User successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("fetch all User failed", re);
			throw re;
		}
	}

	public long getUserCount(
			String filters,
			String search) {
		//
		// TODO
		// add support for filters
		// add support for search

		Transaction transaction = session.getTransaction();
		try {
		transaction.begin();
		String hql = "SELECT count(*) FROM pl.bookstore.hibernatemodel.User ";
		Query query = session.createQuery(hql);
		long result = (long) query.uniqueResult();
		transaction.commit();
		return result;
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("getUserCount failed", re);
			throw re;
		}
	}

	public List getOrdersWPagination(
			User instance,
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
		log.debug("fetching Orders by User with Pagination");
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

			String hql =	"FROM pl.bookstore.hibernatemodel.Orderinfo AS orderinfo " +
					"JOIN FETCH orderinfo.deliverer deliverer " +
					"JOIN FETCH orderinfo.statusinfo statusinfo " +
					//"JOIN FETCH orderinfo.user user " + 
					"WHERE orderinfo.user = :instance " +
					"ORDER BY orderinfo." + sBy + " " + sType;
			Query query = session.createQuery(hql);
			query.setParameter("instance", instance);
			query.setMaxResults(pSize);
			query.setFirstResult(pSize*(pNo-1));
			List results = query.list();
			transaction.commit();
			log.debug("fetching Orders by User with Pagination, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("fetching Orders by User with Pagination failed", re);
			throw re;
		}
	}
}
