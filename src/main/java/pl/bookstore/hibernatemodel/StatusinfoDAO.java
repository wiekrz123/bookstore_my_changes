package pl.bookstore.hibernatemodel;
// Generated 2018-04-16 11:57:15 by Hibernate Tools 5.2.8.Final

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Example;

import pl.bookstore.utils.HibernateUtil;


/**
 * Home object for domain model class Statusinfo.
 * @see pl.bookstore.hibernatemodel.Statusinfo
 * @author Hibernate Tools
 */
@SuppressWarnings({ "rawtypes", "deprecation" })
public class StatusinfoDAO extends DAO {

	private static final Log log = LogFactory.getLog(StatusinfoDAO.class);

	private static final Session session = HibernateUtil.getSession();

	public void persist(Object transientInstance) {
		log.debug("persisting Statusinfo instance");
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			session.persist((Statusinfo) transientInstance);
			transaction.commit();
			log.debug("persist successful");
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Object instance) {
		log.debug("attaching dirty Statusinfo instance");
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			session.saveOrUpdate((Statusinfo) instance);
			transaction.commit();
			log.debug("attach successful");
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("attach failed", re);
			throw re;
		}
	}


	public void delete(Object persistentInstance) {
		log.debug("deleting Statusinfo instance");
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			session.delete((Statusinfo) persistentInstance);
			transaction.commit();
			log.debug("delete successful");
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("delete failed", re);
			throw re;
		}
	}


	public Statusinfo findById(Integer id) {
		log.debug("getting Statusinfo instance with id: " + id);
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			Statusinfo instance = (Statusinfo) session.get("pl.bookstore.hibernatemodel.Statusinfo", id);
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
	
	public Statusinfo findByName(String name) {
		log.debug("getting Statusinfo instance with name: " + name);
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			String hql =	"FROM pl.bookstore.hibernatemodel.Statusinfo AS statusinfo " + 
							"WHERE statusinfo.name = :name";
			Query query = session.createQuery(hql);
			query.setParameter("name", name);
			Statusinfo result = (Statusinfo) query.uniqueResult();
			transaction.commit();
			return result;
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("getOrderCount failed", re);
			throw re;
		}
	}

	public List findByExample(Statusinfo instance) {
		log.debug("finding Statusinfo instance by example");
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			List results = session.createCriteria("pl.bookstore.hibernatemodel.Statusinfo")
					.add(Example.create(instance)).list();
			transaction.commit();
			log.debug("find by example successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("find by example failed", re);
			throw re;
		}
	}

	public long getStatusinfoCount() {
		long result = 0;

		String hql = "SELECT count(*) FROM pl.bookstore.hibernatemodel.Statusinfo ";
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			Query query = session.createQuery(hql);
			result = (long) query.uniqueResult();
			transaction.commit();
			return result;
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("getStatusinfoCount failed", re);
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
		log.debug("fetch all Statuses");
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

			String hql =	"FROM pl.bookstore.hibernatemodel.Statusinfo AS statusinfo " +
					"ORDER BY statusinfo." + sBy + " " + sType;

			transaction.begin();
			Query query = session.createQuery(hql);
			query.setMaxResults(pSize);
			query.setFirstResult(pSize*(pNo-1));
			List results = query.list();
			transaction.commit();
			log.debug("fetch all Statuses successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("fetch all Statuses failed", re);
			throw re;
		}
	}

	public List getOrdersWPagination(
			Statusinfo instance,
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

		log.debug("fetching Orders by Statusinfo with Pagination");
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
					"where statusinfo = :instance " +
					"ORDER BY orderinfo." + sBy + " " + sType;

			transaction.begin();
			Query query = session.createQuery(hql);
			query.setParameter("instance", instance);
			query.setMaxResults(pSize);
			query.setFirstResult(pSize*(pNo-1));
			List results = query.list();
			transaction.commit();
			log.debug("fetching Orders by Statusinfo with Pagination, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("fetching Orders by Statusinfo with Pagination failed", re);
			throw re;
		}
	}

	public long getOrderCount(Statusinfo instance, String filters, String search) {
		//
		// TODO
		// add support for filters
		// add support for search

		long result = -1;
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			String hql =	"SELECT count(*) FROM pl.bookstore.hibernatemodel.Orderinfo AS orderinfo " + 
					"WHERE orderinfo.statusinfo = :instance";
			Query query = session.createQuery(hql);
			query.setParameter("instance", instance);
			result = (long) query.uniqueResult();
			transaction.commit();
			return result;
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("getOrderCount failed", re);
			throw re;
		}
	}
}
