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

import pl.bookstore.hibernatemodel.DAOFactory;
import pl.bookstore.hibernatemodel.Deliverer;
import pl.bookstore.hibernatemodel.DelivererDAO;
import pl.bookstore.hibernatemodel.Orderinfo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.Iterator;
import java.util.List;


@Path("/")
public class RESTDeliverer extends REST {
	private static final Log log = LogFactory.getLog(RESTDeliverer.class);

	@GET @Path("/Deliverers")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response getAll(
			@QueryParam("showUnavailable")	Boolean	showUnavailable,
			@QueryParam("pageNo")			Integer pageNo,
			@QueryParam("pageSize")			Integer pageSize,
			@QueryParam("sortBy")			String sortBy,
			@QueryParam("sortType")			String sortType) {

		log.debug("GET /Deliverers");
		log.debug("showUnavailable: "	+ showUnavailable);
		log.debug("pageNo: "			+ pageNo);
		log.debug("pageSize: "			+ pageSize);
		log.debug("sortBy: "			+ sortBy);
		log.debug("sortType: "			+ sortType);
		
		JSONObject responseJson = new JSONObject();
		responseJson.put("date", LocalDateTime.now().toString());

		JSONArray  jsonDeliverers = new JSONArray();

		DelivererDAO delivererDAO = (DelivererDAO) DAOFactory.getDAO(Deliverer.class);
		List<Deliverer> deliverers = delivererDAO.getWithPagination(showUnavailable, pageNo, pageSize, sortBy, sortType);
		if (deliverers.isEmpty()) {
			log.error("No deliverers found!");
		} else {
			for (Iterator<Deliverer> i = deliverers.iterator();i.hasNext();) {
				Deliverer deliverer = i.next();
				JSONObject element = new JSONObject();
				element.put("id", deliverer.getId());
				element.put("name",	deliverer.getName());
				element.put("deliveryName", deliverer.getDeliveryName());
				element.put("address", deliverer.getAddress());
				element.put("pricePerPackage", deliverer.getPricePerPackage());
				element.put("available", deliverer.isAvailable());
				jsonDeliverers.add(element);
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
		responseJson.put("number of deliverers", delivererDAO.getSize(showUnavailable));
		responseJson.put("list of deliverers", jsonDeliverers);
		////////////////
		log.debug("GET response:");
		log.debug(responseJson.toString());

		return Response.ok(responseJson.toString(), MediaType.APPLICATION_JSON).build();
	}

	@Override
	@GET @Path("/Deliverer/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response getByID(@PathParam("id") Integer id) {

		log.debug("GET /Deliverer/"+id);
		JSONObject responseJson = new JSONObject();
		responseJson.put("date", LocalDateTime.now().toString());

		Deliverer deliverer = (Deliverer) DAOFactory.getDAO(Deliverer.class).findById(id);
		if (deliverer == null) {
			log.warn("Deliverer not found!");
			return Response.status(Response.Status.NOT_FOUND).build();
		} else {
			log.debug("Deliverer found");

			responseJson.put("id",				deliverer.getId());
			responseJson.put("name",			deliverer.getName());
			responseJson.put("deliveryName",	deliverer.getDeliveryName());
			responseJson.put("address",			deliverer.getAddress());
			responseJson.put("pricePerPackage", deliverer.getPricePerPackage());
			responseJson.put("available",		deliverer.isAvailable());

			log.debug("GET response:");
			log.debug(responseJson.toString());
			return Response.ok(responseJson.toString(), MediaType.APPLICATION_JSON).build();
		}
	}

	@GET @Path("/Deliverer/{id}/Orders")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response getOrders(
			@PathParam("id") Integer id,
			@QueryParam("pageNo")		Integer pageNo,
			@QueryParam("pageSize")		Integer pageSize,
			@QueryParam("sortBy")		String sortBy,
			@QueryParam("sortType")		String sortType,
			@QueryParam("filters")		String filters,
			@QueryParam("search")		String search) {

		log.debug("GET /Deliverer/"+id+"/Orders");
		log.debug("pageNo="+pageNo);
		log.debug("pageSize="+pageSize);

		JSONObject responseJson = new JSONObject();
		responseJson.put("date", LocalDateTime.now().toString());

		DelivererDAO delivererDAO = (DelivererDAO) DAOFactory.getDAO(Deliverer.class);
		Deliverer deliverer = delivererDAO.findById(id);
		if (deliverer == null) {
			log.warn("Deliverer not found!");
			return Response.status(Response.Status.NOT_FOUND).entity(responseJson).build();
		} else {
			responseJson.put("id",				deliverer.getId());
			responseJson.put("name",			deliverer.getName());
			responseJson.put("deliveryName",	deliverer.getDeliveryName());
			responseJson.put("address",			deliverer.getAddress());
			responseJson.put("pricePerPackage", deliverer.getPricePerPackage());
			responseJson.put("available",		deliverer.isAvailable());

			List<Orderinfo> orders = delivererDAO.getOrdersWPagination(deliverer, pageNo, pageSize, sortBy, sortType, filters, search);

			JSONArray ordersByDeliverer = new JSONArray();
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

				JSONObject statusinfo = new JSONObject();
				statusinfo.put("id", order.getStatusinfo().getId());
				statusinfo.put("name", order.getStatusinfo().getName());
				orderJson.put("status", statusinfo);
				
				JSONObject user = new JSONObject();
				user.put("id",		order.getUser().getId());
				user.put("name",	order.getUser().getName());
				user.put("surname",	order.getUser().getSurname());
				user.put("email",	order.getUser().getEmail());
				orderJson.put("user", user);
				
				ordersByDeliverer.add(orderJson);
			}
			
			responseJson.put("orders", ordersByDeliverer);
			
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
			
			responseJson.put("Amount of orders", delivererDAO.getOrderCount(deliverer, filters, search));
		}
		log.debug("GET response:");
		log.debug(responseJson.toString());
		return Response.ok(responseJson.toString(), MediaType.APPLICATION_JSON).build();
	}

	@Override
	@POST @Path("/Deliverer/new")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response create(String request) {
		log.debug("POST /Deliverer/new");
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

		String		name			= (String)		requestJson.get("name");
		String		deliveryName	= (String)		requestJson.get("deliveryName");
		String		address			= (String)		requestJson.get("address");
		BigDecimal	pricePerPackage	= BigDecimal.valueOf( (Double)	requestJson.get("pricePerPackage"));
		Boolean		available		= (Boolean)		requestJson.get("available");

		try {
			Deliverer newDelivererr = new Deliverer(name, deliveryName, address, pricePerPackage, available);
			DAOFactory.getDAO(Deliverer.class).persist(newDelivererr);
			responseJson.put("id",				newDelivererr.getId());
			responseJson.put("name",			newDelivererr.getName());
			responseJson.put("deliveryName",	newDelivererr.getDeliveryName());
			responseJson.put("address",			newDelivererr.getAddress());
			responseJson.put("pricePerPackage",	newDelivererr.getPricePerPackage());				
			responseJson.put("available",		newDelivererr.isAvailable());
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
	@PUT @Path("/Deliverer/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response update(String request, @PathParam("id") Integer id) {
		log.debug("PUT /Deliverer/"+id);
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

		boolean updateName				= requestJson.containsKey("name");
		boolean updateDeliveryName		= requestJson.containsKey("deliveryName");
		boolean updateAddress 			= requestJson.containsKey("address");
		boolean updatePricePerPackage	= requestJson.containsKey("pricePerPackage");
		boolean updateAvailable			= requestJson.containsKey("available");

		try {
			DelivererDAO delivererDAO = (DelivererDAO) DAOFactory.getDAO(Deliverer.class);
			Deliverer deliverer = delivererDAO.findById(id);
			if (deliverer == null) {
				log.error("Deliverer with id: " + id +" not found");
				responseJson.put("status", "Deliverer not found");
				return Response.status(Response.Status.NOT_FOUND)
						.entity(responseJson.toString())
						.build();
			} else {
				if (updateName)				deliverer.setName(
						(String)	requestJson.get("name"));
				
				if (updateDeliveryName)		deliverer.setDeliveryName(
						(String)	requestJson.get("deliveryName"));
				
				if (updateAddress)			deliverer.setAddress(
						(String)	requestJson.get("address"));
				
				if (updatePricePerPackage)	deliverer.setPricePerPackage(
						BigDecimal.valueOf( (Double) requestJson.get("pricePerPackage")));
				
				if (updateAvailable)		deliverer.setAvailable(	
						(Boolean)	requestJson.get("available"));
				
				delivererDAO.attachDirty(deliverer);
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
	@DELETE @Path("/Deliverer/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response delete(@PathParam("id") Integer id) {
		log.debug("DELETE /Deliverer/"+id);
		JSONObject responseJson = new JSONObject();

		responseJson.put("date", LocalDateTime.now().toString());
		try {
			DelivererDAO delivererDAO = (DelivererDAO) DAOFactory.getDAO(Deliverer.class);
			Deliverer deliverer = delivererDAO.findById(id);
			if (deliverer != null) {
				delivererDAO.delete(deliverer);
				responseJson.put("status", "OK");
			} else {
				log.warn("Deliverer with id: "+ id +" not found!");
				responseJson.put("status", "Deliverer not found");
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

	@Override
	public Response getAll(Integer pageNo, Integer pageSize, String sortBy, String sortType, String filters,
			String search) {
		// Do not use
		return null;
	}
}

