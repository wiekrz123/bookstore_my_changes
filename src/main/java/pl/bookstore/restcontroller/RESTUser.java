package pl.bookstore.restcontroller;


import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import pl.bookstore.hibernatemodel.User;
import pl.bookstore.hibernatemodel.UserDAO;
import pl.bookstore.hibernatemodel.DAOFactory;
import pl.bookstore.hibernatemodel.Orderinfo;

import java.time.LocalDateTime;


import java.util.Iterator;
import java.util.List;


@Path("/")
public class RESTUser extends REST {
	private static final Log log = LogFactory.getLog(RESTUser.class);

	@Override
	@GET @Path("/Users")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response getAll(
			@QueryParam("pageNo")		Integer pageNo,
			@QueryParam("pageSize")		Integer pageSize,
			@QueryParam("sortBy")		String sortBy,
			@QueryParam("sortType")		String sortType,
			@QueryParam("filters")		String filters,
			@QueryParam("search")		String search) {

		log.debug("GET /Users");
		log.debug("pageNo: "	+ pageNo);
		log.debug("pageSize: "	+ pageSize);
		log.debug("sortBy: "	+ sortBy);
		log.debug("sortType: "	+ sortType);
		log.debug("filters: "	+ filters);
		log.debug("search: "	+ search);

		JSONObject responseJson = new JSONObject();
		responseJson.put("date", LocalDateTime.now().toString());

		JSONArray  jsonUsers = new JSONArray();

		UserDAO userDAO = (UserDAO) DAOFactory.getDAO(User.class);
		List<User> Users = userDAO.getWithPagination(pageNo, pageSize, sortBy, sortType, filters, search);
		if (Users.isEmpty()) {
			log.error("No Users found!");
		} else {
			for (Iterator<User> i = Users.iterator();i.hasNext();) {
				User User = i.next();
				JSONObject element = new JSONObject();
				element.put("id",		User.getId());
				element.put("name",		User.getName());
				element.put("surname",	User.getSurname());
				element.put("email",	User.getEmail());
				element.put("employee",	User.isEmployee());
				element.put("active",	User.isActive());
				jsonUsers.add(element);
			}
		}
		if (pageNo != null) {
			responseJson.put("pageNo", pageNo);
		} else {
			responseJson.put("pageNo", 1);
		}
		if (pageSize != null) {
			responseJson.put("pageSize", pageSize);
		} else {
			responseJson.put("pageSize", 20);
		}
		responseJson.put("number of Users", userDAO.getUserCount(filters, search));
		responseJson.put("list of Users", jsonUsers);
		////////////////
		log.debug("GET response:");
		log.debug(responseJson.toString());

		return Response.ok(responseJson.toString(), MediaType.APPLICATION_JSON).build();
	}

	@Override
	@GET @Path("/User/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response getByID(@PathParam("id") Integer id) {

		log.debug("GET /User/"+id);
		JSONObject responseJson = new JSONObject();
		responseJson.put("date", LocalDateTime.now().toString());

		User user = (User) DAOFactory.getDAO(User.class).findById(id);
		if (user == null) {
			log.warn("User not found!");
			return Response.status(Response.Status.NOT_FOUND).build();
		} else {
			log.debug("User found");
			responseJson.put("id",			user.getId());
			responseJson.put("name",		user.getName());
			responseJson.put("surname",		user.getSurname());
			responseJson.put("email",		user.getEmail());
			responseJson.put("employee",	user.isEmployee());
			responseJson.put("active",		user.isActive());
			log.debug("GET response:");
			log.debug(responseJson.toString());
			return Response.ok(responseJson.toString(), MediaType.APPLICATION_JSON).build();
		}
	}

	@GET @Path("/User/{id}/Orders")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response getOrders(
			@PathParam("id")			Integer id,
			@QueryParam("pageNo")		Integer pageNo,
			@QueryParam("pageSize")		Integer pageSize,
			@QueryParam("sortBy")		String sortBy,
			@QueryParam("sortType")		String sortType,
			@QueryParam("filters")		String filters,
			@QueryParam("search")		String search) {

		log.debug("GET /User/"+id+"/Orders");
		log.debug("pageNo="+pageNo);
		log.debug("pageSize="+pageSize);
		log.debug("sortBy: "	+ sortBy);
		log.debug("sortType: "	+ sortType);
		log.debug("filters: "	+ filters);
		log.debug("search: "	+ search);

		JSONObject responseJson = new JSONObject();
		responseJson.put("date", LocalDateTime.now().toString());

		UserDAO userDAO = (UserDAO) DAOFactory.getDAO(User.class);
		User User = userDAO.findById(id);
		if (User == null) {
			log.warn("User not found!");
			return Response.status(404).entity(responseJson).build();
		} else {
			responseJson.put("id",		User.getId());
			responseJson.put("name",	User.getName());
			responseJson.put("surname",	User.getSurname());
			responseJson.put("email",	User.getEmail());
			responseJson.put("employee",	User.isEmployee());
			responseJson.put("active",	User.isActive());
			List<Orderinfo> orders = userDAO.getOrdersWPagination(User, pageNo, pageSize, sortBy, sortType, filters, search);

			JSONArray ordersByUser = new JSONArray();
			Iterator<Orderinfo> i = orders.iterator();
			while (i.hasNext()) {
				Orderinfo order = i.next();
				JSONObject orderJson = new JSONObject();
				orderJson.put("id",					order.getId());
				orderJson.put("created",			order.getCreated().toString());
				orderJson.put("finished",			order.getFinished().toString());
				orderJson.put("deliveryAddress",	order.getDeliveryAddress());
				orderJson.put("deliveryFee",		order.getDeliveryFee());
				orderJson.put("totalBookPrice",		order.getTotalBookPrice());
				//
				JSONObject deliverer = new JSONObject();
				deliverer.put("id",					order.getDeliverer().getId());
				deliverer.put("name",				order.getDeliverer().getName());
				deliverer.put("deliveryName",		order.getDeliverer().getDeliveryName());
				deliverer.put("address",			order.getDeliverer().getAddress());
				deliverer.put("pricePerPackage",	order.getDeliverer().getPricePerPackage());
				deliverer.put("available",			order.getDeliverer().isAvailable());
				orderJson.put("deliverer",			deliverer);
				//
				JSONObject statusinfo = new JSONObject();
				statusinfo.put("id",				order.getStatusinfo().getId());
				statusinfo.put("available",			order.getStatusinfo().isAvailable());
				statusinfo.put("name",				order.getStatusinfo().getName());
				orderJson.put("statusinfo",			statusinfo);
				//

				ordersByUser.add(orderJson);
			}
			responseJson.put("Amount of Orders", userDAO.getOrderCount(User, filters, search));
			responseJson.put("orders", ordersByUser);
			if (pageNo != null) {
				responseJson.put("pageNo", pageNo);
			} else {
				responseJson.put("pageNo", 1);
			}

			if (pageNo != null) {
				responseJson.put("pageSize", pageSize);
			} else {
				responseJson.put("pageSize", 20);
			}
		}
		log.debug("GET response:");
		log.debug(responseJson.toString());
		return Response.ok(responseJson.toString(), MediaType.APPLICATION_JSON).build();
	}

	@Override
	@POST @Path("/User/new")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response create(String request) {
		log.debug("POST /User/new");
		log.debug(request);

		JSONObject responseJson = new JSONObject();
		JSONObject requestJson = null;
		try {
			JSONParser parser = new JSONParser();
			log.debug("Parsing json");
			requestJson =  (JSONObject) parser.parse(request);
			log.debug("Parsing finished");
		} catch (Exception e) {
			log.error("Error parsing JSON request string", e);
			responseJson.put("status", "Could not parse request JSON");
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(responseJson.toString())
					.build();
		}

		responseJson.put("date", LocalDateTime.now().toString());

		if(requestJson.containsKey("requestID")) {
			responseJson.put("requestID", requestJson.get("requestID"));
		}
		String name			= (String) requestJson.get("name");
		String surname		= (String) requestJson.get("surname");
		String email		= (String) requestJson.get("email");
		String password		= (String) requestJson.get("password");
		Boolean employee	= (Boolean) requestJson.get("employee");;
		Boolean active		= (Boolean) requestJson.get("active");;

		try {
			User newUser = new User(name, surname, email, password, employee, active);
			DAOFactory.getDAO(User.class).persist(newUser);
			responseJson.put("id",		newUser.getId());
			responseJson.put("name",	newUser.getName());
			responseJson.put("surname",	newUser.getSurname());
			responseJson.put("email",	newUser.getEmail());
			responseJson.put("employee",newUser.isEmployee());
			responseJson.put("active",	newUser.isActive());

		} catch (Exception e) {
			log.error("Could not save object", e);
			responseJson.put("status", "Could not save object");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(responseJson.toString())
					.build();
		}

		responseJson.put("status", "OK");

		log.debug("POST response:");
		log.debug(responseJson.toString());
		return Response.ok(responseJson.toString(), MediaType.APPLICATION_JSON).build();
	}

	@Override
	@PUT @Path("/User/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response update(String request, @PathParam("id") Integer id) {
		log.debug("PUT /User/"+id);
		log.debug(request);
		JSONObject responseJson = new JSONObject();
		JSONObject requestJson = null;

		try {
			JSONParser parser = new JSONParser();
			log.debug("Parsing json");
			requestJson =  (JSONObject) parser.parse(request);
			log.debug("Parsing finished");
		} catch (Exception e) {
			log.error("Error parsing JSON request string", e);
			responseJson.put("status", "Could not parse request JSON");
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(responseJson.toString())
					.build();
		}

		responseJson.put("date", LocalDateTime.now().toString());

		if(requestJson.containsKey("requestID")) {
			responseJson.put("requestID", requestJson.get("requestID"));
		}

		boolean updateName		= requestJson.containsKey("name");
		boolean updateSurname	= requestJson.containsKey("surname");
		boolean updateEmail		= requestJson.containsKey("email");
		boolean updateEmployee	= requestJson.containsKey("employee");
		boolean updateActive	= requestJson.containsKey("active");


		try {
			UserDAO userDAO = (UserDAO) DAOFactory.getDAO(User.class);
			User User = userDAO.findById(id);
			if (User == null) {
				log.error("User with id: " + id +" not found");
				responseJson.put("status", "User not found");
				return Response.status(Response.Status.NOT_FOUND)
						.entity(responseJson.toString())
						.build();
			} else {
				if (updateName)		User.setName(		(String)	requestJson.get("name"));
				if (updateSurname)	User.setSurname(	(String)	requestJson.get("surname"));
				if (updateEmail)	User.setEmail(		(String)	requestJson.get("email"));
				if (updateEmployee)	User.setEmployee(	(Boolean)	requestJson.get("employee"));
				if (updateActive)	User.setActive(		(Boolean)	requestJson.get("active"));

				userDAO.attachDirty(User);
				responseJson.put("status", "OK");
			}
		} catch (Exception e) {
			log.error("Could not update object", e);
			responseJson.put("status", "Could not update object");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(responseJson.toString())
					.build();
		}
		log.debug("PUT response:");
		log.debug(responseJson.toString());
		return Response.ok(responseJson.toString(), MediaType.APPLICATION_JSON).build();
	}

	@Override
	@DELETE @Path("/User/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response delete(@PathParam("id") Integer id) {
		log.debug("DELETE /User/"+id);
		JSONObject responseJson = new JSONObject();

		responseJson.put("date", LocalDateTime.now().toString());
		try {
			UserDAO userDAO = (UserDAO) DAOFactory.getDAO(User.class);
			User User = userDAO.findById(id);
			if (User != null) {
				userDAO.delete(User);
				responseJson.put("status", "OK");
			} else {
				log.warn("User with id: "+ id +" not found!");
				responseJson.put("status", "User not found");
				return Response.status(Response.Status.NOT_FOUND)
						.entity(responseJson.toString())
						.build();
			}
		} catch (Exception e) {
			log.error("Could not delete object", e);
			responseJson.put("status", "Could not delete object");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(responseJson.toString())
					.build();
		}
		log.debug("DELETE response:");
		log.debug(responseJson.toString());
		return Response.ok(responseJson.toString(), MediaType.APPLICATION_JSON).build();
	}
}

