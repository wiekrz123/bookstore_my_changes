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
 * Home object for domain model class Deliverer.
 * @see pl.bookstore.hibernatemodel.Deliverer
 * @author Hibernate Tools
 */
@SuppressWarnings({ "rawtypes", "deprecation" })
public class DelivererDAO extends DAO {

	private static final Log log = LogFactory.getLog(DelivererDAO.class);

	private static final Session session = HibernateUtil.getSession();

	public void persist(Object transientInstance) {
		log.debug("persisting Deliverer instance");
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			session.persist((Deliverer) transientInstance);
			transaction.commit();
			log.debug("persist successful");
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Object instance) {
		log.debug("attaching dirty Deliverer instance");
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			session.saveOrUpdate((Deliverer) instance);
			transaction.commit();
			log.debug("attach successful");
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Object persistentInstance) {
		log.debug("deleting Deliverer instance");
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			session.delete((Deliverer) persistentInstance);
			transaction.commit();
			log.debug("delete successful");
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("delete failed", re);
			throw re;
		}
	}


	public Deliverer findById(Integer id) {
		log.debug("getting Deliverer instance with id: " + id);
		if (id == null) return null;
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			Deliverer instance = (Deliverer) session.get("pl.bookstore.hibernatemodel.Deliverer", id);
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

	public long getSize(Boolean showUnavailable) {
		long result = 0;
		boolean sUnavailable = false;
		if (showUnavailable != null) sUnavailable = showUnavailable;

		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			Query query;
			if (sUnavailable) {
				query = session.createQuery("SELECT count(*) FROM pl.bookstore.hibernatemodel.Deliverer");
			} else {
				query = session.createQuery("SELECT count(*) FROM pl.bookstore.hibernatemodel.Deliverer WHERE available='1'");
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
			Boolean showUnavailable,
			Integer pageNo,
			Integer pageSize,
			String sortBy,
			String sortType) {
		log.debug("fetch all Deliverers");
		Transaction transaction = session.getTransaction();
		try {
			boolean sUnavailable = false;
			if (showUnavailable != null)		sUnavailable = showUnavailable;

			int pSize = 20;
			if (pageSize != null)	pSize = pageSize;

			int pNo = 1;
			if (pageNo != null)		pNo = pageNo;

			String sBy = "id";
			if (sortBy != null)		sBy = sortBy;

			String sType = "ASC";
			if (sortType != null)		sType = sortType;

			String hql = "FROM pl.bookstore.hibernatemodel.Deliverer AS deliverer ";
			if (!sUnavailable) {
				hql += "WHERE deliverer.available='1' ";
			}
			hql += "ORDER BY deliverer." + sBy + " " + sType;

			transaction.begin();
			Query query = session.createQuery(hql);
			query.setMaxResults(pSize);
			query.setFirstResult(pSize*(pNo-1));
			List results = query.list();
			transaction.commit();
			log.debug("fetch all Deliverers successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("fetch all Deliverers failed", re);
			throw re;
		}
	}

	public long getOrderCount(Deliverer instance, String filters, String search) {
		//
		// TODO
		// add support for filters
		// add support for search

		long result = -1;
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			String hql =	"SELECT count(*) FROM pl.bookstore.hibernatemodel.Orderinfo AS orderinfo " + 
					"WHERE orderinfo.deliverer = :instance";
			Query query = session.createQuery(hql);
			query.setParameter("instance", instance);
			result = (long) query.uniqueResult();
			transaction.commit();
			return result;
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("fetch all Deliverers failed", re);
			throw re;
		}
	}

	public List getOrdersWPagination(
			Deliverer instance,
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

		log.debug("fetching Orders by Deliverer with Pagination");
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

			String hql = "FROM pl.bookstore.hibernatemodel.Orderinfo AS orderinfo " +
					"JOIN FETCH orderinfo.statusinfo AS statusinfo " +
					"JOIN FETCH orderinfo.user AS user " +
					"JOIN FETCH orderinfo.deliverer AS deliverer " +
					"where deliverer = :instance " +
					"ORDER BY orderinfo." + sBy + " " + sType;

			transaction.begin();
			Query query = session.createQuery(hql);
			query.setParameter("instance", instance);
			query.setMaxResults(pSize);
			query.setFirstResult(pSize*(pNo-1));
			List results = query.list();
			transaction.commit();
			log.debug("fetching Orders by Deliverer with Pagination, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("fetching Orders by Deliverer with Pagination failed", re);
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
