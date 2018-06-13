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
 * Home object for domain model class Orderitem.
 * @see pl.bookstore.hibernatemodel.Orderitem
 * @author Hibernate Tools
 */

@SuppressWarnings("deprecation")
public class OrderitemDAO extends DAO {

	private static final Log log = LogFactory.getLog(OrderitemDAO.class);

	private static final Session session = HibernateUtil.getSession();

	public void persist(Object transientInstance) {
		log.debug("persisting Orderitem instance");
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			session.persist((Orderitem) transientInstance);
			transaction.commit();
			log.debug("persist successful");
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Object instance) {
		log.debug("attaching dirty Orderitem instance");
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			session.saveOrUpdate((Orderitem) instance);
			transaction.commit();
			log.debug("attach successful");
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Object persistentInstance) {
		log.debug("deleting Orderitem instance");
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			session.delete((Orderitem) persistentInstance);
			transaction.commit();
			log.debug("delete successful");
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("delete failed", re);
			throw re;
		}
	}

	public Orderitem findById(pl.bookstore.hibernatemodel.OrderitemId id) {
		log.debug("getting Orderitem instance with id: " + id);
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			Orderitem instance = (Orderitem) session
					.get("pl.bookstore.hibernatemodel.Orderitem", id);
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
	
	@SuppressWarnings("rawtypes")
	public List findOrderitemsByorderIdWPagination(
			Orderinfo order,
			Integer pageNo,
			Integer pageSize,
			String sortBy,
			String sortType,
			String filters,
			String search) {
		log.debug("getting Orderitems for order with id: " + order);

		int pSize = 20;
		if (pageSize != null)	pSize = pageSize;

		int pNo = 1;
		if (pageNo != null)		pNo = pageNo;

		String sBy = "book";
		if (sortBy != null)		sBy = sortBy;

		String sType = "ASC";
		if (sortType != null)		sType = sortType;

		String hql =	"FROM pl.bookstore.hibernatemodel.Orderitem AS orderitem " +
						"JOIN FETCH orderitem.book " +
						"JOIN FETCH orderitem.id " +
						"JOIN FETCH orderitem.orderinfo " +
						"WHERE orderitem.orderinfo = :order " +
						"ORDER BY orderitem." + sBy + " " + sType;
						
		/*String sql = 	"SELECT oi.orderID, oi.bookID, oi.price, oi.amount, " + 
						"b.id, b.isbn, b.title " +
						"FROM orderitem oi join book b on oi.bookID = b.id " +
						"WHERE oi.orderID = " + orderID + " " +
						"ORDER BY oi." + sBy + " " + sType + " " +
						"LIMIT "+ pSize + " OFFSET " + pSize*(pNo-1);*/

		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			Query query = session.createQuery(hql);
			query.setParameter("order", order);
			query.setMaxResults(pSize);
			query.setFirstResult(pSize*(pNo-1));
			List results = query.list();
			transaction.commit();
			log.debug("fetch Orderitems successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			transaction.rollback();
			log.error("fetch Orderitems failed", re);
			throw re;
		}
	}

	@Override
	public Object findById(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getWithPagination(Integer pageNo, Integer pageSize, String sortBy, String sortType, String filters,
			String search) {
		// do not use
		return null;
	}
}
