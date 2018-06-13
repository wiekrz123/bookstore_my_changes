package pl.bookstore.utils;
/*
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Driver;
import java.util.Enumeration;
import java.util.Set;
 */

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hibernate.stat.*;

import pl.bookstore.utils.HibernateUtil;

public class HibernateListener implements ServletContextListener {
	private static final Log log = LogFactory.getLog(HibernateListener.class);

	@Override
	public void contextInitialized(ServletContextEvent event) {
		log.debug("Initializing context");
		HibernateUtil.getSessionFactory(); // Just call the static initializer of that class

	}  

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		///////

		Statistics stats = HibernateUtil.getSessionFactory().getStatistics();
		log.debug("Hibernate statistics");
		log.debug(stats);

		///////
		log.debug("Closing DB session");
		HibernateUtil.getSession().close(); // Free all resources


		////////////



		////////////////////////
		// from https://stackoverflow.com/questions/3320400/to-prevent-a-memory-leak-the-jdbc-driver-has-been-forcibly-unregistered/23912257#23912257
		////////////////////////
		/*
	    ClassLoader cl = Thread.currentThread().getContextClassLoader();
	    // Loop through all drivers
	    Enumeration<Driver> drivers = DriverManager.getDrivers();
	    while (drivers.hasMoreElements()) {
	        Driver driver = drivers.nextElement();
	        if (driver.getClass().getClassLoader() == cl) {
	            // This driver was registered by the webapp's ClassLoader, so deregister it:
	            try {
	                log.info("Deregistering JDBC driver {} " + driver.toString());
	                DriverManager.deregisterDriver(driver);
	            } catch (SQLException ex) {
	                log.error("Error deregistering JDBC driver {} " + driver.toString(), ex);
	            }
	        } else {
	            // driver was not registered by the webapp's ClassLoader and may be in use elsewhere
	            log.trace("Not deregistering JDBC driver {} as it does not belong to this webapp's ClassLoader "+ driver.toString());
	        }
	    }
		 */


		////////////////////////
		// from https://stackoverflow.com/questions/11872316/tomcat-guice-jdbc-memory-leak
		////////////////////////
		/*
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        Driver d = null;
        while(drivers.hasMoreElements()) {
            try {
                d = drivers.nextElement();
                DriverManager.deregisterDriver(d);
                log.warn(String.format("Driver %s deregistered", d));
            } catch (SQLException ex) {
            	log.warn(String.format("Error deregistering driver %s", d), ex);
            }
        }
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
        for(Thread t:threadArray) {
            if(t.getName().contains("Abandoned connection cleanup thread")
            		|| t.getName().contains("pool-1-thread-1")) {
            	log.warn(t.getName());
                synchronized(t) {
                    t.stop(); //don't complain, it works
                }
            }
        }
		 */
		////////////////////////
	}
} 
