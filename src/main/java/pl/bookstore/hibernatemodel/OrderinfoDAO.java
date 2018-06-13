package pl.bookstore.hibernatemodel;
// Generated 2018-04-16 11:57:15 by Hibernate Tools 5.2.8.Final

import java.math.BigInteger;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import pl.bookstore.utils.HibernateUtil;

/**
 * Home object for domain model class Orderinfo.
 * @see pl.bookstore.hibernatemodel.Orderinfo
 * @author Hibernate Tools
 */
@SuppressWarnings({ "rawtypes", "deprecation" })
public class OrderinfoDAO extends DAO {

	private static final Log log = LogFactory.getLog(OrderinfoDAO.class);

	private static final Session session = HibernateUtil.getSession();

	public void persist(Object transientInstance) {
		log.debug("persisting Orderinfo instance");
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			session.persist((Orderinfo) transientInstance);
			transaction.commit();
			log.debug("persist successful");
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("persist failed", re);
			throw re;
		} finally {
			log.debug("closing persistSession");
		}
	}

	public void attachDirty(Object instance) {
		log.debug("attaching dirty Orderinfo instance");
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			session.saveOrUpdate((Orderinfo) instance);
			transaction.commit();
			log.debug("attach successful");
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Object persistentInstance) {
		log.debug("deleting Orderinfo instance");
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			session.delete((Orderinfo) persistentInstance);
			transaction.commit();
			log.debug("delete successful");
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("delete failed", re);
			throw re;
		}
	}

	public Orderinfo findById(Integer id) {
		log.debug("getting Orderinfo instance with id: " + id);
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			Orderinfo instance = (Orderinfo) session
					.get("pl.bookstore.hibernatemodel.Orderinfo", id);
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

	public long getOrderCount(String filters, String search) {
		// TODO
		// add support for filters and search
		long result = 0;
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			Query query = session.createQuery("SELECT count(*) FROM pl.bookstore.hibernatemodel.Orderinfo ");
			result = (long) query.uniqueResult();
			transaction.commit();
			return result;
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("getOrderCount failed", re);
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
		log.debug("fetch all Orders");

		int pSize = 20;
		if (pageSize != null)	pSize = pageSize;

		int pNo = 1;
		if (pageNo != null)		pNo = pageNo;

		String sBy = "id";
		if (sortBy != null)		sBy = sortBy;

		String sType = "ASC";
		if (sortType != null)		sType = sortType;

		String hql =	"FROM pl.bookstore.hibernatemodel.Orderinfo AS orderinfo " +
				"JOIN FETCH orderinfo.deliverer " +
				"JOIN FETCH orderinfo.statusinfo " +
				"JOIN FETCH orderinfo.user " +
				"ORDER BY orderinfo." + sBy + " " + sType;

		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			Query query = session.createQuery(hql);
			query.setMaxResults(pSize);
			query.setFirstResult(pSize*(pNo-1));
			List results = query.list();
			transaction.commit();
			log.debug("fetch all Orders successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("fetch all Orders failed", re);
			throw re;
		}
	}

	public Orderinfo findByIdWDetails(Integer id) {
		log.debug("getting Orderinfo with details, instance with id: " + id);
		
		String sql = "SELECT COUNT(orderitem.bookId) FROM orderitem WHERE orderitem.orderId = " + id;
		Integer orderitemCount;
		
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			SQLQuery query = session.createSQLQuery(sql);
			orderitemCount = ((BigInteger) query.uniqueResult()).intValue();
			transaction.commit();
			log.debug("Order: " + id + " has: " + orderitemCount + " orderitems");
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("get failed", re);
			throw re;
		}
		
		

		
		
		String hql =	"FROM pl.bookstore.hibernatemodel.Orderinfo AS orderinfo " +
						"JOIN FETCH orderinfo.deliverer " +
						"JOIN FETCH orderinfo.statusinfo " +
						"JOIN FETCH orderinfo.user ";
		if (orderitemCount > 0) {
			hql += 		"JOIN FETCH orderinfo.orderitems AS orderitems " +
						"JOIN FETCH orderitems.book " +
						"JOIN FETCH orderitems.id ";
		}
		hql += 			"WHERE orderinfo.id = :orderinfoID";

		transaction = session.getTransaction();
		try {
			transaction.begin();
			Query query = session.createQuery(hql);
			query.setParameter("orderinfoID", id);
			Orderinfo result = (Orderinfo) query.uniqueResult();
			transaction.commit();
			if (result == null) {
				log.debug("get successful, no instance found");
			} else {
				log.debug("get successful, instance found");
			}
			return result;
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("get failed", re);
			throw re;
		}
	}

}
