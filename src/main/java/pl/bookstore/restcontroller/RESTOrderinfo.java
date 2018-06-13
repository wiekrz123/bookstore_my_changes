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

import pl.bookstore.hibernatemodel.Book;
import pl.bookstore.hibernatemodel.BookDAO;
import pl.bookstore.hibernatemodel.DAOFactory;
import pl.bookstore.hibernatemodel.Deliverer;
import pl.bookstore.hibernatemodel.Orderinfo;
import pl.bookstore.hibernatemodel.OrderinfoDAO;
import pl.bookstore.hibernatemodel.Orderitem;
import pl.bookstore.hibernatemodel.OrderitemDAO;
import pl.bookstore.hibernatemodel.OrderitemId;
import pl.bookstore.hibernatemodel.Statusinfo;
import pl.bookstore.hibernatemodel.StatusinfoDAO;
import pl.bookstore.hibernatemodel.User;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;



@Path("/")
public class RESTOrderinfo extends REST {
	private static final Log log = LogFactory.getLog(RESTOrderinfo.class);

	@Override
	@GET @Path("/Orders")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response getAll(
			@QueryParam("pageNo")		Integer pageNo,
			@QueryParam("pageSize")		Integer pageSize,
			@QueryParam("sortBy")		String sortBy,
			@QueryParam("sortType")		String sortType,
			@QueryParam("filters")		String filters,
			@QueryParam("search")		String search) {

		log.debug("GET /Orders");
		log.debug("pageNo: "	+ pageNo);
		log.debug("pageSize: "	+ pageSize);
		log.debug("sortBy: "	+ sortBy);
		log.debug("sortType: "	+ sortType);
		log.debug("filters: "	+ filters);
		log.debug("search: "	+ search);

		JSONObject responseJson = new JSONObject();
		responseJson.put("date", LocalDateTime.now().toString());

		JSONArray  jsonOrders = new JSONArray();

		OrderinfoDAO orderinfoDAO = (OrderinfoDAO) DAOFactory.getDAO(Orderinfo.class);
		List<Orderinfo> orders = orderinfoDAO.getWithPagination(pageNo, pageSize, sortBy, sortType, filters, search);
		if (orders.isEmpty()) {
			log.error("No orders found!");
		} else {
			for (Iterator<Orderinfo> i = orders.iterator(); i.hasNext();) {
				Orderinfo order = i.next();
				JSONObject element = new JSONObject();

				element.put("id",				order.getId());
				element.put("created",			order.getCreated().toString());
				element.put("finished",			order.getFinished().toString());
				element.put("deliveryAddress",	order.getDeliveryAddress());
				element.put("deliveryFee",		order.getDeliveryFee());
				element.put("totalBookPrice",	order.getTotalBookPrice());

				//
				JSONObject deliverer = new JSONObject();
				deliverer.put("id",				order.getDeliverer().getId());
				deliverer.put("name",			order.getDeliverer().getName());
				deliverer.put("deliveryName",	order.getDeliverer().getDeliveryName());
				deliverer.put("address",		order.getDeliverer().getAddress());
				deliverer.put("pricePerPackage",order.getDeliverer().getPricePerPackage());
				deliverer.put("available",		order.getDeliverer().isAvailable());
				element.put("deliverer",		deliverer);
				//
				JSONObject statusinfo = new JSONObject();
				statusinfo.put("id",			order.getStatusinfo().getId());
				statusinfo.put("available",		order.getStatusinfo().isAvailable());
				statusinfo.put("name",			order.getStatusinfo().getName());
				element.put("status",		statusinfo);
				//
				JSONObject user = new JSONObject();
				user.put("id",					order.getUser().getId());
				user.put("name",				order.getUser().getName());
				user.put("surname",				order.getUser().getSurname());
				user.put("email",				order.getUser().getEmail());
				user.put("employee",			order.getUser().isEmployee());
				user.put("active",				order.getUser().isActive());
				element.put("user",				user);
				//
				jsonOrders.add(element);
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
		responseJson.put("number of orders", orderinfoDAO.getOrderCount(filters, search));
		responseJson.put("list of orders", jsonOrders);
		////////////////
		log.debug("GET response:");
		log.debug(responseJson.toString());

		return Response.ok(responseJson.toString(), MediaType.APPLICATION_JSON).build();
	}

	@GET @Path("/Order/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response getByID(
			@QueryParam("pageNo")		Integer pageNo,
			@QueryParam("pageSize")		Integer pageSize,
			@QueryParam("sortBy")		String sortBy,
			@QueryParam("sortType")		String sortType,
			@QueryParam("filters")		String filters,
			@QueryParam("search")		String search,
			@PathParam("id") Integer id) {

		log.debug("GET /Order/"+id);
		JSONObject responseJson = new JSONObject();
		responseJson.put("date", LocalDateTime.now().toString());

		OrderinfoDAO orderinfoDAO = (OrderinfoDAO) DAOFactory.getDAO(Orderinfo.class);
		Orderinfo order = orderinfoDAO.findByIdWDetails(id);
		if (order == null) {
			log.warn("Order not found!");
			return Response.status(Response.Status.NOT_FOUND).build();
		} else {
			log.debug("Order found");
/////////////////////////////////////
			responseJson.put("id",				order.getId());
			responseJson.put("created",			order.getCreated().toString());
			responseJson.put("finished",		order.getFinished().toString());
			responseJson.put("deliveryAddress",	order.getDeliveryAddress());
			responseJson.put("deliveryFee",		order.getDeliveryFee());
			responseJson.put("totalBookPrice",	order.getTotalBookPrice());
			//
			JSONObject delivererJson = new JSONObject();
			delivererJson.put("id",				order.getDeliverer().getId());
			delivererJson.put("name",			order.getDeliverer().getName());
			delivererJson.put("deliveryName",	order.getDeliverer().getDeliveryName());
			delivererJson.put("address",		order.getDeliverer().getAddress());
			delivererJson.put("pricePerPackage",order.getDeliverer().getPricePerPackage());
			delivererJson.put("available",		order.getDeliverer().isAvailable());
			responseJson.put("deliverer",		delivererJson);
			//
			JSONObject statusinfoJson = new JSONObject();
			statusinfoJson.put("id",		order.getStatusinfo().getId());
			statusinfoJson.put("available",	order.getStatusinfo().isAvailable());
			statusinfoJson.put("name",		order.getStatusinfo().getName());
			responseJson.put("status",	statusinfoJson);
			//
			JSONObject userJson = new JSONObject();
			userJson.put("id",				order.getUser().getId());
			userJson.put("name",			order.getUser().getName());
			userJson.put("surname",			order.getUser().getSurname());
			userJson.put("email",			order.getUser().getEmail());
			userJson.put("employee",		order.getUser().isEmployee());
			userJson.put("active",			order.getUser().isActive());
			responseJson.put("user",		userJson);
			//
			JSONArray orderitemsJson = new JSONArray();
			List<Orderitem> orderitems = ( (OrderitemDAO) DAOFactory.getDAO(Orderitem.class)).findOrderitemsByorderIdWPagination(
					order,
					pageNo,
					pageSize,
					sortBy,
					sortType,
					filters,
					search);
			for (Iterator<Orderitem> i = orderitems.iterator(); i.hasNext();) {
				Orderitem orderitem = i.next();
				JSONObject orderitemJson = new JSONObject();
				orderitemJson.put("amount", orderitem.getAmount());
				orderitemJson.put("price",	orderitem.getPrice());
				
				JSONObject orderitemIDJson = new JSONObject();
				orderitemIDJson.put("orderId",	orderitem.getId().getOrderId());
				orderitemIDJson.put("bookId",	orderitem.getId().getBookId());
				orderitemJson.put("id",			orderitemIDJson);
				
				JSONObject bookJson = new JSONObject();
				bookJson.put("id",				orderitem.getBook().getId());
				bookJson.put("isbn",			orderitem.getBook().getIsbn());
				bookJson.put("title",			orderitem.getBook().getTitle());
				//bookJson.put("description",		orderitem.getBook().getDescription());
				bookJson.put("link",			orderitem.getBook().getLink());
				orderitemJson.put("book",		bookJson);
				orderitemsJson.add(orderitemJson);
			}
			
			responseJson.put("orderitems", orderitemsJson);
/////////////////////////////////////
			log.debug("GET response:");
			log.debug(responseJson.toString());
			return Response.ok(responseJson.toString(), MediaType.APPLICATION_JSON).build();
		}
	}

	@Override
	@POST @Path("/Order/new")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Response create(String request) {
		log.debug("POST /Order/new");
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

		Date finished = null;
		if (requestJson.containsKey("finished")) {
			String dateString = (String) requestJson.get("finished");
			// "finished": 											  "2018-05-04T15:50:40.243803600",
			DateTimeFormatter formatter =
					DateTimeFormatter.ofPattern("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'nnnnnnnnn");
			// "finished": 						"2018-05-04T15:50:40.243803600",
			LocalDateTime date = null;
			try {
				date = LocalDateTime.parse(dateString, formatter);
			} catch (java.time.format.DateTimeParseException e) {
				log.error("Error parsing finished date ", e);
				responseJson.put("status", "Error parsing finished date");
				responseJson.put("info", "date format: yyyy-MM-ddTHH:mm:ss.nnnnnnnnn");
				return Response.status(Response.Status.BAD_REQUEST)
						.entity(responseJson.toString())
						.build();
			}
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR,			date.getYear());
			calendar.set(Calendar.MONTH,		date.getMonthValue());
			calendar.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
			calendar.set(Calendar.HOUR_OF_DAY,	date.getHour());
			calendar.set(Calendar.MINUTE, 		date.getMinute());
			calendar.set(Calendar.SECOND, 		date.getSecond());
			//calendar.set(Calendar.MILLISECOND,	date.getNano());
			finished = calendar.getTime();
		}
		
		Date created =  new Date();
		if (requestJson.containsKey("created")) {
			String dateString = (String) requestJson.get("created");
			DateTimeFormatter formatter =
					DateTimeFormatter.ofPattern("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'nnnnnnnnn");
			LocalDateTime date = null;
			try {
				date = LocalDateTime.parse(dateString, formatter);
			} catch (java.time.format.DateTimeParseException e) {
				log.error("Error parsing created date ", e);
				responseJson.put("status", "Error parsing created date");
				responseJson.put("info", "date format: yyyy-MM-ddTHH:mm:ss.nnnnnnnnn");
				return Response.status(Response.Status.BAD_REQUEST)
						.entity(responseJson.toString())
						.build();
			}
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR,			date.getYear());
			calendar.set(Calendar.MONTH,		date.getMonthValue());
			calendar.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
			calendar.set(Calendar.HOUR_OF_DAY,	date.getHour());
			calendar.set(Calendar.MINUTE, 		date.getMinute());
			calendar.set(Calendar.SECOND, 		date.getSecond());
			//calendar.set(Calendar.MILLISECOND,	date.getNano());
			created = calendar.getTime();
		}
		
		
		String deliveryAddress = null;
		if (requestJson.containsKey("deliveryAddress")) {
			deliveryAddress = (String) requestJson.get("deliveryAddress");
		}
		
		BigDecimal deliveryFee = new BigDecimal(0.0);;
		if (requestJson.containsKey("deliveryFee")) {
			deliveryFee = BigDecimal.valueOf((Double)requestJson.get("deliveryFee"));
		}
		
		BigDecimal totalBookPrice = new BigDecimal(0.0);
		if (requestJson.containsKey("deliveryFee")) {
			totalBookPrice = BigDecimal.valueOf((Double) requestJson.get("totalBookPrice"));
		}
		
		Deliverer deliverer = null;
		if (requestJson.containsKey("deliverer")) {
			JSONObject delivererJson = (JSONObject) requestJson.get("deliverer");
			deliverer = (Deliverer) DAOFactory.getDAO(Deliverer.class).findById(Integer.parseInt(delivererJson.get("id").toString()));
		}
		
		StatusinfoDAO statusinfoDAO = (StatusinfoDAO) DAOFactory.getDAO(Statusinfo.class);
		Statusinfo statusinfo = statusinfoDAO.findByName("Nowy");
		if (requestJson.containsKey("status")) {
			JSONObject statusinfoJson = (JSONObject) requestJson.get("status");
			statusinfo = statusinfoDAO.findById(Integer.parseInt(statusinfoJson.get("id").toString()));
		}
		
		User user = null;
		if (requestJson.containsKey("user")) {
			JSONObject userJson = (JSONObject) requestJson.get("user");
			user = (User) DAOFactory.getDAO(User.class).findById(Integer.parseInt(userJson.get("id").toString()));
		}
	
		Set orderitems = null;
		if (requestJson.containsKey("orderitems")) {
			log.error("Creating new Order with orderitems is not supported");
			responseJson.put("status", "Creating new Order with orderitems is not supported");
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(responseJson.toString())
					.build();
			// TODO ? raczej nie
			/*
			orderitems = new HashSet<Orderitem>();
			JSONArray orderitemsJson = (JSONArray) requestJson.get("orderitems");
			for(Iterator<JSONObject> i =  orderitemsJson.iterator(); i.hasNext();  ) {
				JSONObject orderitemJson = i.next();
				
			}
			*/
		}
		
		try {
			Orderinfo newOrder = new Orderinfo(deliverer, statusinfo, user, created, finished,
					deliveryAddress, deliveryFee, totalBookPrice, orderitems);
			DAOFactory.getDAO(Orderinfo.class).persist(newOrder);
			responseJson.put("id",				newOrder.getId());
			responseJson.put("created",			newOrder.getCreated().toString());
			if (newOrder.getFinished() != null) {
				responseJson.put("finished",		newOrder.getFinished().toString());
			}
			
			if (newOrder.getDeliveryAddress() != null) {
				responseJson.put("deliveryAddress",	newOrder.getDeliveryAddress());
			}
			
			responseJson.put("deliveryFee",		newOrder.getDeliveryFee());
			responseJson.put("totalBookPrice",	newOrder.getTotalBookPrice());
			
			if (newOrder.getDeliverer() != null) {
				JSONObject delivererJson = new JSONObject();
				delivererJson.put("id",				newOrder.getDeliverer().getId());
				delivererJson.put("name", 			newOrder.getDeliverer().getName());
				delivererJson.put("deliveryName",	newOrder.getDeliverer().getDeliveryName());
				delivererJson.put("address",		newOrder.getDeliverer().getAddress());
				delivererJson.put("pricePerPackage",newOrder.getDeliverer().getPricePerPackage());
				responseJson.put("deliverer", delivererJson);
			}
			
			if (newOrder.getStatusinfo() != null) {
				JSONObject statusinfoJson = new JSONObject();
				statusinfoJson.put("id",			newOrder.getStatusinfo().getId());
				statusinfoJson.put("name", 			newOrder.getStatusinfo().getName());
				responseJson.put("status",		statusinfoJson);
			}
			
			if (newOrder.getUser() != null) {
				JSONObject userJson = new JSONObject();
				userJson.put("id",					newOrder.getUser().getId());
				userJson.put("name", 				newOrder.getUser().getName());
				userJson.put("email", 				newOrder.getUser().getEmail());
				userJson.put("surname", 			newOrder.getUser().getSurname());
				responseJson.put("user",			userJson);
			}
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
	@PUT @Path("/Order/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response update(String request, @PathParam("id") Integer id) {
		log.debug("PUT /Order/"+id);
		log.debug(request);

		JSONObject responseJson = new JSONObject();
		JSONObject requestJson = null;
		
		Orderinfo order = ((OrderinfoDAO) DAOFactory.getDAO(Orderinfo.class)).findByIdWDetails(id);
		if (order != null) {
			responseJson.put("status", "OK");
		} else {
			log.warn("Order with id: "+ id +" not found!");
			responseJson.put("status", "Order not found");
			return Response.status(Response.Status.NOT_FOUND)
					.entity(responseJson.toString())
					.build();
		}
		
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

		Date finished = null;
		if (requestJson.containsKey("finished")) {
			String dateString = (String) requestJson.get("finished");
			// "finished": 											  "2018-05-04T15:50:40.243803600",
			DateTimeFormatter formatter =
					DateTimeFormatter.ofPattern("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'nnnnnnnnn");
			// "finished": 						"2018-05-04T15:50:40.243803600",
			LocalDateTime date = null;
			try {
				date = LocalDateTime.parse(dateString, formatter);
			} catch (java.time.format.DateTimeParseException e) {
				log.error("Error parsing finished date ", e);
				responseJson.put("status", "Error parsing finished date");
				responseJson.put("info", "date format: yyyy-MM-ddTHH:mm:ss.nnnnnnnnn");
				return Response.status(Response.Status.BAD_REQUEST)
						.entity(responseJson.toString())
						.build();
			}
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR,			date.getYear());
			calendar.set(Calendar.MONTH,		date.getMonthValue());
			calendar.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
			calendar.set(Calendar.HOUR_OF_DAY,	date.getHour());
			calendar.set(Calendar.MINUTE, 		date.getMinute());
			calendar.set(Calendar.SECOND, 		date.getSecond());
			//calendar.set(Calendar.MILLISECOND,	date.getNano());
			finished = calendar.getTime();
		}
		
		Date created =  null;
		if (requestJson.containsKey("created")) {
			String dateString = (String) requestJson.get("created");
			DateTimeFormatter formatter =
					DateTimeFormatter.ofPattern("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'nnnnnnnnn");
			LocalDateTime date = null;
			try {
				date = LocalDateTime.parse(dateString, formatter);
			} catch (java.time.format.DateTimeParseException e) {
				log.error("Error parsing created date ", e);
				responseJson.put("status", "Error parsing created date");
				responseJson.put("info", "date format: yyyy-MM-ddTHH:mm:ss.nnnnnnnnn");
				return Response.status(Response.Status.BAD_REQUEST)
						.entity(responseJson.toString())
						.build();
			}
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR,			date.getYear());
			calendar.set(Calendar.MONTH,		date.getMonthValue());
			calendar.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
			calendar.set(Calendar.HOUR_OF_DAY,	date.getHour());
			calendar.set(Calendar.MINUTE, 		date.getMinute());
			calendar.set(Calendar.SECOND, 		date.getSecond());
			//calendar.set(Calendar.MILLISECOND,	date.getNano());
			created = calendar.getTime();
		}
		
		
		String deliveryAddress = null;
		if (requestJson.containsKey("deliveryAddress")) {
			deliveryAddress = (String) requestJson.get("deliveryAddress");
		}
		
		BigDecimal deliveryFee = null;
		if (requestJson.containsKey("deliveryFee")) {
			deliveryFee = BigDecimal.valueOf((Double)requestJson.get("deliveryFee"));
		}
		
		BigDecimal totalBookPrice = null;
		if (requestJson.containsKey("deliveryFee")) {
			totalBookPrice = BigDecimal.valueOf((Double) requestJson.get("totalBookPrice"));
		}
		
		Deliverer deliverer = null;
		if (requestJson.containsKey("deliverer")) {
			JSONObject delivererJson = (JSONObject) requestJson.get("deliverer");
			deliverer = (Deliverer) DAOFactory.getDAO(Deliverer.class).findById(Integer.parseInt(delivererJson.get("id").toString()));
		}
		
		Statusinfo statusinfo = null;
		if (requestJson.containsKey("status")) {
			JSONObject statusinfoJson = (JSONObject) requestJson.get("status");
			statusinfo = (Statusinfo) DAOFactory.getDAO(Statusinfo.class).findById(Integer.parseInt(statusinfoJson.get("id").toString()));
		}
		
		User user = null;
		if (requestJson.containsKey("user")) {
			JSONObject userJson = (JSONObject) requestJson.get("user");
			user = (User) DAOFactory.getDAO(User.class).findById(Integer.parseInt(userJson.get("id").toString()));
		}
	
		//Set orderitems = null;
		if (requestJson.containsKey("orderitems")) {
			log.error("Creating new Order with orderitems is not supported");
			responseJson.put("status", "Creating new Order with orderitems is not supported");
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(responseJson.toString())
					.build();
			// TODO ? raczej nie
			/*
			orderitems = new HashSet<Orderitem>();
			JSONArray orderitemsJson = (JSONArray) requestJson.get("orderitems");
			for(Iterator<JSONObject> i =  orderitemsJson.iterator(); i.hasNext();  ) {
				JSONObject orderitemJson = i.next();
				
			}
			*/
		}
		
		if (created != null) {
			order.setCreated(created);
		}

		if (finished != null) {
			order.setFinished(finished);
		}
		
		if (deliveryAddress != null) {
			order.setDeliveryAddress(deliveryAddress);
		}
		
		if (deliveryFee != null) {
			order.setDeliveryFee(deliveryFee);
		}
		
		if (totalBookPrice != null) {
			order.setTotalBookPrice(totalBookPrice);
		}
		if (deliverer != null) {
			order.setDeliverer(deliverer);
		}
		if (statusinfo != null) {
			order.setStatusinfo(statusinfo);
		}
		if (user != null) {
			order.setUser(user);
		}
		
		try {
			DAOFactory.getDAO(Orderinfo.class).attachDirty(order);
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
	
	@POST @Path("/Order/{id}/add")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response addPosition(
			String request,
			@PathParam("id") Integer id) {
		OrderinfoDAO orderinfoDAO = (OrderinfoDAO) DAOFactory.getDAO(Orderinfo.class);
		Orderinfo order = orderinfoDAO.findByIdWDetails(id);
		if (order == null) {
			log.warn("Order not found!");
			return Response.status(Response.Status.NOT_FOUND).build();
		} else {
			log.debug("Order found");
		}
		
		JSONObject responseJson = new JSONObject();
		responseJson.put("date", LocalDateTime.now().toString());
		
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

		if(requestJson.containsKey("requestID")) {
			responseJson.put("requestID", requestJson.get("requestID"));
		}
		
		Integer bookID = ((Long) requestJson.get("bookID")).intValue();
		Integer amount = ((Long) requestJson.get("amount")).intValue();
		
		if (bookID == null || amount == null) {
			responseJson.put("status", "bookID and amount can not be null");
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(responseJson.toString())
					.build();
		}
		
		BookDAO bookDAO = (BookDAO) DAOFactory.getDAO(Book.class);
		Book book = bookDAO.findById(bookID);
		if (book == null || book.getAmount() < amount) {
			responseJson.put("status", "book not found");
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(responseJson.toString())
					.build();
		}
		
		Set<Orderitem> orderitems = order.getOrderitems();
		
		Orderitem newItem = new Orderitem(
				new OrderitemId(id, bookID),
				book,
				order,
				amount,
				book.getPrice()	
				);
		orderitems.add(newItem);
		BigDecimal newTotalPrice = book.getPrice().multiply(BigDecimal.valueOf(amount.longValue()));
		newTotalPrice.add(order.getTotalBookPrice());
		order.setTotalBookPrice( newTotalPrice  );
		book.setAmount(book.getAmount() - amount);
		try {
			bookDAO.attachDirty(book);
			DAOFactory.getDAO(Orderitem.class).persist(newItem);
			orderinfoDAO.attachDirty(order);
		} catch (Exception e) {
			responseJson.put("status", "Could not save object");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(responseJson.toString())
					.build();
		}
		
		responseJson.put("Status", "ok");
		return Response.ok(responseJson.toString(), MediaType.APPLICATION_JSON).build();
	}
	
	@PUT @Path("/Order/{id}/{bookID}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response updatePosition(
			String request,
			@PathParam("id")			Integer id,
			@PathParam("bookID")		Integer bookID) {
		OrderinfoDAO orderinfoDAO = (OrderinfoDAO) DAOFactory.getDAO(Orderinfo.class);
		Orderinfo order = orderinfoDAO.findByIdWDetails(id);
		if (order == null) {
			log.warn("Order not found!");
			return Response.status(Response.Status.NOT_FOUND).build();
		} else {
			log.debug("Order found");
		}
		
		Set<Orderitem> orderitems = order.getOrderitems();
		Orderitem orderitem = null;
		for (Iterator<Orderitem> j = orderitems.iterator(); j.hasNext();) {
			Orderitem tmp = j.next();
			if (tmp.getId().getBookId().equals(bookID)) {
				orderitem = tmp;
				break;
			}
		}
		
		if (orderitem == null) {
			log.warn("Orderitem not found!");
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		Book book = orderitem.getBook();
		
		
		JSONObject responseJson = new JSONObject();
		responseJson.put("date", LocalDateTime.now().toString());
		
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

		if(requestJson.containsKey("requestID")) {
			responseJson.put("requestID", requestJson.get("requestID"));
		}
		
		Long na = (Long) requestJson.get("amount");
		if (na == null) {
			responseJson.put("status", "amount can not be null");
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(responseJson.toString())
					.build();
		}
		Integer newAmount = na.intValue();
		
		BigDecimal priceDiff = book.getPrice().multiply(BigDecimal.valueOf(newAmount - orderitem.getAmount()));
		priceDiff.add(order.getTotalBookPrice());
		order.setTotalBookPrice( priceDiff  );
		orderitem.setAmount(newAmount);
		try {
			DAOFactory.getDAO(Book.class).attachDirty(book);
			DAOFactory.getDAO(Orderitem.class).attachDirty(orderitem);
			orderinfoDAO.attachDirty(order);
		} catch (Exception e) {
			responseJson.put("status", "Could not save object");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(responseJson.toString())
					.build();
		}
		
		responseJson.put("Status", "ok");
		return Response.ok(responseJson.toString(), MediaType.APPLICATION_JSON).build();
	}
	
	@DELETE @Path("/Order/{id}/{bookID}")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response deletePosition(
			@PathParam("id")			Integer id,
			@PathParam("bookID")		Integer bookID) {
		OrderinfoDAO orderinfoDAO = (OrderinfoDAO) DAOFactory.getDAO(Orderinfo.class);
		Orderinfo order = orderinfoDAO.findByIdWDetails(id);
		if (order == null) {
			log.warn("Order not found!");
			return Response.status(Response.Status.NOT_FOUND).build();
		} else {
			log.debug("Order found");
		}
		
		Set<Orderitem> orderitems = order.getOrderitems();
		Orderitem orderitem = null;
		for (Iterator<Orderitem> j = orderitems.iterator(); j.hasNext();) {
			Orderitem tmp = j.next();
			if (tmp.getId().getBookId().equals(bookID)) {
				orderitem = tmp;
				break;
			}
		}
		
		if (orderitem == null) {
			log.warn("Orderitem not found!");
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		Book book = orderitem.getBook();
		
		
		JSONObject responseJson = new JSONObject();
		responseJson.put("date", LocalDateTime.now().toString());
		
		BigDecimal priceDiff = book.getPrice().multiply(BigDecimal.valueOf( -orderitem.getAmount()));
		priceDiff.add(order.getTotalBookPrice());
		order.setTotalBookPrice( priceDiff );
		order.getOrderitems().remove(orderitem);
		try {
			orderinfoDAO.attachDirty(order);
			DAOFactory.getDAO(Orderitem.class).delete(orderitem);
		} catch (Exception e) {
			responseJson.put("status", "Could not delete object");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(responseJson.toString())
					.build();
		}
		
		responseJson.put("Status", "ok");
		return Response.ok(responseJson.toString(), MediaType.APPLICATION_JSON).build();
	}
	
	@POST @Path("/Order/{id}/Submit")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response submitOrder(String request, @PathParam("id") Integer id) {
		OrderinfoDAO orderinfoDAO = (OrderinfoDAO) DAOFactory.getDAO(Orderinfo.class);
		Orderinfo order = orderinfoDAO.findByIdWDetails(id);
		if (order == null) {
			log.warn("Order not found!");
			return Response.status(Response.Status.NOT_FOUND).build();
		} else {
			log.debug("Order found");
		}
		
		JSONObject responseJson = new JSONObject();
		responseJson.put("date", LocalDateTime.now().toString());
		
		if (order.getStatusinfo() == null || order.getStatusinfo().getName().equals("Nowy"))
			try {
				order.setStatusinfo((Statusinfo) ((StatusinfoDAO) DAOFactory.getDAO(Statusinfo.class)).findByName("Zatwierdzony"));
				orderinfoDAO.attachDirty(order);
				responseJson.put("Status", "ok");
			} catch (Exception e) {
				log.warn("Could not change status");
				responseJson.put("Status", "Could not change status");
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity(responseJson.toString())
						.build();
			}

		return Response.ok(responseJson.toString(), MediaType.APPLICATION_JSON).build();
	}

	@Override
	@DELETE @Path("/Order/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response delete(@PathParam("id") Integer id) {
		log.debug("DELETE /Order/"+id);
		JSONObject responseJson = new JSONObject();

		responseJson.put("date", LocalDateTime.now().toString());
		try {
			OrderinfoDAO orderinfoDAO = (OrderinfoDAO) DAOFactory.getDAO(Orderinfo.class);
			Orderinfo order = orderinfoDAO.findById(id);
			if (order != null) {
				orderinfoDAO.delete(order);
				responseJson.put("status", "OK");
			} else {
				log.warn("Order with id: "+ id +" not found!");
				responseJson.put("status", "Order not found");
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
	public Response getByID(Integer id) {
		// Do not use
		return null;
	}

}

