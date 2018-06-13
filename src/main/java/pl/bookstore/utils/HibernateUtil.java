package pl.bookstore.utils;

import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.stat.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HibernateUtil {
    private static final SessionFactory sessionFactory;
    private static final Session session;
	private static final Log log = LogFactory.getLog(HibernateUtil.class);

    static {  
        try {  
            // Create the SessionFactory from hibernate.cfg.xml  
        	log.debug("Building HibernateUtil SessionFactory");
            sessionFactory = new Configuration().configure().buildSessionFactory();
            Statistics stats = sessionFactory.getStatistics();
        	log.debug("Enabling statictics");
            stats.setStatisticsEnabled(true);

        } catch (Throwable ex) {  
            // Make sure you log the exception, as it might be swallowed
        	//sessionFactory = null;
        	log.error("Initial SessionFactory creation failed.", ex);  
            throw new ExceptionInInitializerError(ex);  
        }

        try {
        	log.debug("Opening Hibernate Session");
        	session = sessionFactory.openSession();
        } catch (Throwable ex) {  
            // Make sure you log the exception, as it might be swallowed
        	log.error("Session creation failed.", ex);  
            throw new ExceptionInInitializerError(ex);  
        }
    }  
	public static SessionFactory getSessionFactory() {  
		return sessionFactory;  
	}  

	public static Session getSession() {  
		return session;  
	}  

}