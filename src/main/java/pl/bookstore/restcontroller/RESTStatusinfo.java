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

import pl.bookstore.hibernatemodel.Statusinfo;
import pl.bookstore.hibernatemodel.StatusinfoDAO;
import pl.bookstore.hibernatemodel.DAOFactory;
import pl.bookstore.hibernatemodel.Orderinfo;

import java.time.LocalDateTime;

import java.util.Iterator;
import java.util.List;


@Path("/")
public class RESTStatusinfo extends REST {
	private static final Log log = LogFactory.getLog(RESTStatusinfo.class);

	@Override
	@GET @Path("/Statuses")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response getAll(
			@QueryParam("pageNo")		Integer pageNo,
			@QueryParam("pageSize")		Integer pageSize,
			@QueryParam("sortBy")		String sortBy,
			@QueryParam("sortType")		String sortType,
			@QueryParam("filters")		String filters,
			@QueryParam("search")		String search) {

		log.debug("GET /Statuses");
		log.debug("pageNo: "	+ pageNo);
		log.debug("pageSize: "	+ pageSize);
		log.debug("sortBy: "	+ sortBy);
		log.debug("sortType: "	+ sortType);

		JSONObject responseJson = new JSONObject();
		responseJson.put("date", LocalDateTime.now().toString());

		JSONArray  jsonStatuses = new JSONArray();

		StatusinfoDAO statusinfoDAO = (StatusinfoDAO) DAOFactory.getDAO(Statusinfo.class);
		List<Statusinfo> statuses = statusinfoDAO.getWithPagination(pageNo, pageSize, sortBy, sortType, filters, search);
		
		if (statuses.isEmpty()) {
			log.error("No Statuses found!");
		} else {
			for (Iterator<Statusinfo> i = statuses.iterator();i.hasNext();) {
				Statusinfo statusinfo = i.next();
				JSONObject element = new JSONObject();
				element.put("id",			statusinfo.getId());
				element.put("name",			statusinfo.getName());
				element.put("available",	statusinfo.isAvailable());
				jsonStatuses.add(element);
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
		responseJson.put("number of Statuses", statusinfoDAO.getStatusinfoCount());
		responseJson.put("list of Statuses", jsonStatuses);
		////////////////
		log.debug("GET response:");
		log.debug(responseJson.toString());

		return Response.ok(responseJson.toString(), MediaType.APPLICATION_JSON).build();
	}

	@Override
	@GET @Path("/Status/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response getByID(@PathParam("id") Integer id) {

		log.debug("GET /Status/"+id);
		JSONObject responseJson = new JSONObject();
		responseJson.put("date", LocalDateTime.now().toString());

		Statusinfo Statusinfo = (Statusinfo) DAOFactory.getDAO(Statusinfo.class).findById(id);
		if (Statusinfo == null) {
			log.warn("Statusinfo not found!");
			return Response.status(Response.Status.NOT_FOUND).build();
		} else {
			log.debug("Statusinfo found");
			responseJson.put("id",		Statusinfo.getId());
			responseJson.put("name",	Statusinfo.getName());
			responseJson.put("available",	Statusinfo.isAvailable());
			log.debug("GET response:");
			log.debug(responseJson.toString());
			return Response.ok(responseJson.toString(), MediaType.APPLICATION_JSON).build();
		}
	}

	@GET @Path("/Status/{id}/Orders")
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

		log.debug("GET /Status/"+id+"/Orders");
		log.debug("pageNo="+pageNo);
		log.debug("pageSize="+pageSize);
		log.debug("sortBy: "	+ sortBy);
		log.debug("sortType: "	+ sortType);
		log.debug("filters: "	+ filters);
		log.debug("search: "	+ search);

		JSONObject responseJson = new JSONObject();
		responseJson.put("date", LocalDateTime.now().toString());

		StatusinfoDAO statusinfoDAO = (StatusinfoDAO) DAOFactory.getDAO(Statusinfo.class);
		Statusinfo statusinfo = statusinfoDAO.findById(id);
		if (statusinfo == null) {
			log.warn("Statusinfo not found!");
			return Response.status(Response.Status.NOT_FOUND).entity(responseJson).build();
		} else {
			responseJson.put("id",			statusinfo.getId());
			responseJson.put("name",		statusinfo.getName());
			responseJson.put("available",	statusinfo.isAvailable());
			List<Orderinfo> orders = statusinfoDAO.getOrdersWPagination(statusinfo, pageNo, pageSize, sortBy, sortType, filters, search);

			JSONArray ordersByStatusinfo = new JSONArray();
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
				JSONObject user = new JSONObject();
				user.put("id",					order.getUser().getId());
				user.put("name",				order.getUser().getName());
				user.put("surname",				order.getUser().getSurname());
				user.put("email",				order.getUser().getEmail());
				user.put("employee",			order.getUser().isEmployee());
				user.put("active",				order.getUser().isActive());
				orderJson.put("user",			user);
				//

				ordersByStatusinfo.add(orderJson);
			}
			responseJson.put("Amount of Orders", statusinfoDAO.getOrderCount(statusinfo, filters, search));
			responseJson.put("orders", ordersByStatusinfo);
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
	@POST @Path("/Status/new")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response create(String request) {
		log.debug("POST /Status/new");
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
		Boolean available	= (Boolean) requestJson.get("available");

		try {
			Statusinfo newStatusinfo = new Statusinfo(available, name);
			DAOFactory.getDAO(Statusinfo.class).persist(newStatusinfo);
			responseJson.put("id",			newStatusinfo.getId());
			responseJson.put("name",		newStatusinfo.getName());
			responseJson.put("available",	newStatusinfo.isAvailable());

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
	@PUT @Path("/Status/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response update(String request, @PathParam("id") Integer id) {
		log.debug("PUT /Status/"+id);
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
		boolean updateAvailable	= requestJson.containsKey("available");


		try {
			StatusinfoDAO statusinfoDAO = (StatusinfoDAO) DAOFactory.getDAO(Statusinfo.class);
			Statusinfo statusinfo = statusinfoDAO.findById(id);
			if (statusinfo == null) {
				log.error("Statusinfo with id: " + id +" not found");
				responseJson.put("status", "Statusinfo not found");
				return Response.status(Response.Status.NOT_FOUND)
						.entity(responseJson.toString())
						.build();
			} else {
				if (updateName)			statusinfo.setName(			(String)	requestJson.get("name"));
				if (updateAvailable)	statusinfo.setAvailable(	(Boolean)	requestJson.get("available"));

				statusinfoDAO.attachDirty(statusinfo);
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
	@DELETE @Path("/Status/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response delete(@PathParam("id") Integer id) {
		log.debug("DELETE /Status/"+id);
		JSONObject responseJson = new JSONObject();

		responseJson.put("date", LocalDateTime.now().toString());
		try {
			StatusinfoDAO statusinfoDAO = (StatusinfoDAO) DAOFactory.getDAO(Statusinfo.class);
			Statusinfo statusinfo = statusinfoDAO.findById(id);
			if (statusinfo != null) {
				statusinfoDAO.delete(statusinfo);
				responseJson.put("status", "OK");
			} else {
				log.warn("Statusinfo with id: "+ id +" not found!");
				responseJson.put("status", "Statusinfo not found");
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

