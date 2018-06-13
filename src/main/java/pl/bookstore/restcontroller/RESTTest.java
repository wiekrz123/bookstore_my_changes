package pl.bookstore.restcontroller;


import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.simple.JSONObject;

import pl.bookstore.utils.HibernateUtil;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

@SuppressWarnings("deprecation")
@Path("/")
public class RESTTest extends REST {
	private static final Log log = LogFactory.getLog(RESTTest.class);

	@GET @Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response get() {
		log.debug("GET /");
		JSONObject responseJson = new JSONObject();
		responseJson.put("status", "OK");
		responseJson.put("date", LocalDateTime.now().toString());
		return Response.ok(responseJson.toString(), MediaType.APPLICATION_JSON).build();
	}
	
	@GET @Path("/info")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Response getInfo() {
		log.debug("GET /info");
		JSONObject responseJson = new JSONObject();
		//JSONObject connectionDetails = new JSONObject();
		JSONObject connectionOptions = new JSONObject();
		String driver			= null;
		String dialect			= null;
		String host				= null;
		String shemaName 		= null;
		String hostStatus 		= null;
		String selectStatus 	= null;
		String connectionStatus	= "OK";
		String url = HibernateUtil.getSessionFactory().getProperties().
				get("hibernate.connection.url").toString();
		String tmp = url;
		Integer port = null;

		
		if (url != null) {
			// jdbc:mysql://localhost:3306/bookstore?useUnicode=true&amp;useJDBCCompliantTimezoneShift=true&amp;useLegacyDatetimeCode=false&amp;serverTimezone=UTC
			driver = tmp.substring(0, tmp.indexOf(":"));
			tmp = tmp.substring(tmp.indexOf(":")+1);
			
			dialect =  tmp.substring(0, tmp.indexOf(":"));
			tmp = tmp.substring(tmp.indexOf(":")+3);
			
			host = tmp.substring(0, tmp.indexOf(":"));
			tmp = tmp.substring(tmp.indexOf(":")+1);
			
			port = Integer.parseInt(tmp.substring(0, tmp.indexOf("/")));
			tmp = tmp.substring(tmp.indexOf("/")+1);
			
			shemaName = tmp.substring(0, tmp.indexOf("?"));;
			tmp = tmp.substring(tmp.indexOf("?")+1);



			
			// tmp = useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC
			while (tmp.length() > 0) {
				String key = tmp.substring(0, tmp.indexOf("="));
				tmp = tmp.substring(tmp.indexOf("=")+1);
				int next = tmp.indexOf("&");
				if (next == -1) {
					next = tmp.length()-1;
				}
				String value = tmp.substring(0, next);
				tmp = tmp.substring(next+1);
				connectionOptions.put(key, value);
			}

			
			// address
			try {
				InetAddress inet = InetAddress.getByName(host);
				if (inet.isReachable(5000)) {
					hostStatus = "Reachable";
					Session session = HibernateUtil.getSession();
					Transaction transaction = session.getTransaction();
					try {
						transaction.begin();
						SQLQuery query = session.createSQLQuery("SELECT 1 FROM DUAL");
						Integer  result = ((BigInteger ) query.uniqueResult()).intValue();
						if (result.equals(1)) {
							selectStatus = "OK";
						} else {
							connectionStatus = "NOT OK";
							selectStatus = "NOT OK";
						}
						transaction.commit();
					} catch (RuntimeException re) {
						transaction.rollback();
						connectionStatus = "NOT OK"; 
						log.error("SELECT 1 FROM DUAL failed", re);
						throw re;
					}
				} else {
					connectionStatus = "NOT OK";
					hostStatus = "NOT reachable";
				}
			} catch (UnknownHostException e) {
				connectionStatus = "NOT OK";
				hostStatus = "Unknown Host Exception";
			} catch (IOException ex) {
				connectionStatus = "NOT OK";
				hostStatus = "IO Exception";
			}
		} else {
			connectionStatus = "NOT OK";
		}
	
		responseJson.put("Select Status", selectStatus);
		responseJson.put("Host Status", hostStatus);
		responseJson.put("Connection Status", connectionStatus);
		responseJson.put("date", LocalDateTime.now().toString());
		responseJson.put("driver", driver);
		responseJson.put("dialect", dialect);
		responseJson.put("host", host);
		responseJson.put("port", port);
		responseJson.put("shemaName", shemaName);
		responseJson.put("url", url);
		responseJson.put("Options", connectionOptions);
		return Response.ok(responseJson.toString(), MediaType.APPLICATION_JSON).build();
	}

	@Override
	@GET @Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response getByID(@PathParam("id") Integer id) {
		log.debug("GET /"+id);
		JSONObject responseJson = new JSONObject();
		responseJson.put("status", "OK");
		responseJson.put("date", LocalDateTime.now().toString());
		return Response.ok(responseJson.toString(), MediaType.APPLICATION_JSON).build();
	}

	@Override
	@POST @Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response create(String request) {
		log.debug("POST /");
		JSONObject responseJson = new JSONObject();
		responseJson.put("status", "OK");
		responseJson.put("date", LocalDateTime.now().toString());
		return Response.ok(responseJson.toString(), MediaType.APPLICATION_JSON).build();
	}

	@Override
	@PUT @Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response update(String request, @PathParam("id") Integer id) {
		log.debug("PUT /"+id);
		log.debug("Request: " + request);
		JSONObject responseJson = new JSONObject();
		responseJson.put("status", "OK");
		responseJson.put("date", LocalDateTime.now().toString());
		return Response.ok(responseJson.toString(), MediaType.APPLICATION_JSON).build();
	}

	@Override
	@DELETE @Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response delete(@PathParam("id") Integer id) {
		log.debug("DELETE /"+id);
		JSONObject responseJson = new JSONObject();
		responseJson.put("status", "OK");
		responseJson.put("date", LocalDateTime.now().toString());
		return Response.ok(responseJson.toString(), MediaType.APPLICATION_JSON).build();
	}

	@Override
	public Response getAll(Integer pageNo, Integer pageSize, String sortBy, String sortType, String filters,
			String search) {
		// do not use
		return null;
	}	
}

