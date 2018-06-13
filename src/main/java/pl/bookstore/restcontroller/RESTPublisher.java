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


import pl.bookstore.hibernatemodel.PublisherDAO;
import pl.bookstore.hibernatemodel.Publisher;
import pl.bookstore.hibernatemodel.Book;
import pl.bookstore.hibernatemodel.DAOFactory;

import java.time.LocalDateTime;

import java.util.Iterator;
import java.util.List;

@Path("/")
public class RESTPublisher extends REST {
	private static final Log log = LogFactory.getLog(RESTPublisher.class);


	@GET @Path("/Publishers")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response getAll(
			@QueryParam("pageNo")		Integer pageNo,
			@QueryParam("pageSize")		Integer pageSize,
			@QueryParam("sortBy")		String sortBy,
			@QueryParam("sortType")		String sortType,
			@QueryParam("filters")		String filters,
			@QueryParam("search")		String search) {

		log.debug("GET Publishers");
		log.debug("pageNo: " + pageNo);
		log.debug("pageSize: " + pageSize);

		JSONObject responseJson = new JSONObject();
		responseJson.put("date", LocalDateTime.now().toString());

		JSONArray  jsonPublishers = new JSONArray();

		PublisherDAO publisherDAO = (PublisherDAO) DAOFactory.getDAO(Publisher.class);
		List<Publisher> publishers = publisherDAO.getWithPagination(pageNo, pageSize, sortBy, sortType, filters, search);
		if (publishers.isEmpty()) {
			log.error("No publishers found!");
		} else {
			for (Iterator<Publisher> i = publishers.iterator();i.hasNext();) {
				Publisher publisher = i.next();
				JSONObject element = new JSONObject();
				element.put("id", publisher.getId());
				element.put("name",	publisher.getName());
				element.put("address", publisher.getAddress());
				element.put("email", publisher.getEmail());				
				jsonPublishers.add(element);
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
		responseJson.put("number of publishers", publisherDAO.getSize());
		responseJson.put("list of publishers", jsonPublishers);
		////////////////
		log.debug("GET response:");
		log.debug(responseJson.toString());

		return Response.ok(responseJson.toString(), MediaType.APPLICATION_JSON).build();
	}

	@Override
	@GET @Path("/Publisher/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response getByID(@PathParam("id") Integer id) {

		log.debug("GET /Category/"+id);
		JSONObject responseJson = new JSONObject();
		responseJson.put("date", LocalDateTime.now().toString());

		Publisher publisher = (Publisher) DAOFactory.getDAO(Publisher.class).findById(id);
		if (publisher == null) {
			log.warn("Publisher not found!");
			return Response.status(Response.Status.NOT_FOUND).build();
		} else {
			log.debug("Publisher found");
			responseJson.put("id",		publisher.getId());
			responseJson.put("name",	publisher.getName());
			responseJson.put("address",	publisher.getAddress());
			responseJson.put("email",	publisher.getEmail());

			log.debug("GET response:");
			log.debug(responseJson.toString());
			return Response.ok(responseJson.toString(), MediaType.APPLICATION_JSON).build();
		}
	}

	@GET @Path("/Publisher/{id}/Books")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response getBooks(
			@PathParam("id")			Integer id,
			@QueryParam("pageNo")		Integer pageNo,
			@QueryParam("pageSize")		Integer pageSize,
			@QueryParam("sortBy")		String sortBy,
			@QueryParam("sortType")		String sortType,
			@QueryParam("filters")		String filters,
			@QueryParam("search")		String search
			) {

		log.debug("GET /Publisher/"+id+"/Books");

		JSONObject responseJson = new JSONObject();
		responseJson.put("date", LocalDateTime.now().toString());

		PublisherDAO publisherDAO = (PublisherDAO) DAOFactory.getDAO(Publisher.class);
		Publisher publisher = publisherDAO.findById(id);
		if (publisher == null) {
			log.warn("Publisher not found!");
			return Response.status(404).entity(responseJson).build();
		} else {
			responseJson.put("id",		publisher.getId());
			responseJson.put("name",	publisher.getName());
			responseJson.put("address",	publisher.getAddress());
			responseJson.put("email",	publisher.getEmail());

			
			List<Book> books = publisherDAO.getAvailableBooksWPagination(publisher, pageNo, pageSize, sortBy, sortType, filters, search);
			
			JSONArray booksByPublisher = new JSONArray();
			Iterator<Book> i = books.iterator();
			while (i.hasNext()) {
				Book book = i.next();
				if(book.isAvailable()) {
					JSONObject bookJson = new JSONObject();
					bookJson.put("title", book.getTitle());
					bookJson.put("price", book.getPrice());
					bookJson.put("amount", book.getAmount());
					bookJson.put("id", book.getId());
					booksByPublisher.add(bookJson);
				}
			}
			responseJson.put("Amount of Books", publisherDAO.getBookCount(publisher));
			responseJson.put("books", booksByPublisher);
		}
		log.debug("GET response:");
		log.debug(responseJson.toString());
		return Response.ok(responseJson.toString(), MediaType.APPLICATION_JSON).build();
	}

	@Override
	@POST @Path("/Publisher/new")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response create(String request) {
		log.debug("POST /Publisher/new");
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

		String name		= (String) requestJson.get("name");
		String address	= (String) requestJson.get("address");
		String email	= (String) requestJson.get("email");

		try {
			Publisher newPublisher = new Publisher(name, address, email, null);
			DAOFactory.getDAO(Publisher.class).persist(newPublisher);
			responseJson.put("id",		newPublisher.getId());
			responseJson.put("name",	newPublisher.getName());
			responseJson.put("address",	newPublisher.getAddress());
			responseJson.put("email",	newPublisher.getEmail());
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
	@PUT @Path("/Publisher/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response update(String request, @PathParam("id") Integer id) {
		log.debug("PUT /Publisher/"+id);
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
		boolean updateAddress	= requestJson.containsKey("address");
		boolean updateEmail		= requestJson.containsKey("email");


		try {
			PublisherDAO publisherDAO = (PublisherDAO) DAOFactory.getDAO(Publisher.class);
			Publisher publisher = publisherDAO.findById(id);
			if (publisher == null) {
				log.error("Publisher with id: " + id +" not found");
				responseJson.put("status", "Publisher not found");
				return Response.status(Response.Status.NOT_FOUND)
						.entity(responseJson.toString())
						.build();
			} else {
				if (updateName)		publisher.setName(		(String)	requestJson.get("name"));
				if (updateAddress)	publisher.setAddress(	(String)	requestJson.get("address"));
				if (updateEmail)	publisher.setEmail(		(String)	requestJson.get("email"));
				publisherDAO.attachDirty(publisher);
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
	@DELETE @Path("/Publisher/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response delete(@PathParam("id") Integer id) {
		log.debug("DELETE /Publisher/"+id);
		JSONObject responseJson = new JSONObject();

		responseJson.put("date", LocalDateTime.now().toString());
		try {
			PublisherDAO publisherDAO = (PublisherDAO) DAOFactory.getDAO(Publisher.class);
			Publisher publisher = publisherDAO.findById(id);
			if (publisher != null) {
				publisherDAO.delete(publisher);
				responseJson.put("status", "OK");
			} else {
				log.warn("Publisher with id: "+ id +" not found!");
				responseJson.put("status", "Publisher not found");
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