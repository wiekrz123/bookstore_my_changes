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

import pl.bookstore.hibernatemodel.Category;
import pl.bookstore.hibernatemodel.CategoryDAO;
import pl.bookstore.hibernatemodel.DAOFactory;
import pl.bookstore.hibernatemodel.Book;

import java.time.LocalDateTime;

import java.util.Iterator;
import java.util.List;

@Path("/")
public class RESTCategory extends REST {
	private static final Log log = LogFactory.getLog(RESTCategory.class);


	@GET @Path("/Categories")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response getAll(
			@QueryParam("pageNo")		Integer pageNo,
			@QueryParam("pageSize")		Integer pageSize,
			@QueryParam("showInvisible")Boolean	showInvisible,
			@QueryParam("sortBy")		String sortBy,
			@QueryParam("sortType")		String sortType) {

		log.debug("GET Categories");
		log.debug("pageNo: "		+ pageNo);
		log.debug("pageSize: "		+ pageSize);
		log.debug("showInvisible: "	+ showInvisible);
		log.debug("sortBy: "		+ sortBy);
		log.debug("sortType: "		+ sortType);	

		JSONObject responseJson = new JSONObject();
		responseJson.put("date", LocalDateTime.now().toString());

		JSONArray  jsonCategories = new JSONArray();

		CategoryDAO categoryDAO = (CategoryDAO) DAOFactory.getDAO(Category.class);
		List<Category> categories = categoryDAO.getWithPagination(showInvisible, pageNo, pageSize, sortBy, sortType);
		if (categories.isEmpty()) {
			log.error("No categories found!");
		} else {
			for (Iterator<Category> i = categories.iterator();i.hasNext();) {
				Category category = i.next();
				JSONObject element = new JSONObject();
				element.put("id", category.getId());
				element.put("name",	category.getName());
				element.put("visible", category.isVisible());
				jsonCategories.add(element);
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
		responseJson.put("number of categories", categoryDAO.getCategoryCount(showInvisible));
		responseJson.put("list of categories", jsonCategories);
		////////////////
		log.debug("GET response:");
		log.debug(responseJson.toString());

		return Response.ok(responseJson.toString(), MediaType.APPLICATION_JSON).build();
	}

	@Override
	@GET @Path("/Category/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response getByID(@PathParam("id") Integer id) {

		log.debug("GET /Category/"+id);
		JSONObject responseJson = new JSONObject();
		responseJson.put("date", LocalDateTime.now().toString());

		Category category = (Category) DAOFactory.getDAO(Category.class).findById(id);
		if (category == null) {
			log.warn("Category not found!");
			return Response.status(Response.Status.NOT_FOUND).build();
		} else {
			log.debug("Category found");
			responseJson.put("id", category.getId());
			responseJson.put("name",	category.getName());
			responseJson.put("visible", category.isVisible());

			log.debug("GET response:");
			log.debug(responseJson.toString());
			return Response.ok(responseJson.toString(), MediaType.APPLICATION_JSON).build();
		}
	}

	@GET @Path("/Category/{id}/Books")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response getBooks(
			@PathParam("id")			Integer id,
			@QueryParam("pageNo")		Integer pageNo,
			@QueryParam("pageSize")		Integer pageSize,
			@QueryParam("sortBy")		String sortBy,
			@QueryParam("sortType")		String sortType,
			@QueryParam("filters")		String filters,
			@QueryParam("search")		String search) {

		log.debug("GET /Category/"+id+"/Books");
		log.debug("pageNo="+pageNo);
		log.debug("pageSize="+pageSize);

		JSONObject responseJson = new JSONObject();
		responseJson.put("date", LocalDateTime.now().toString());
		CategoryDAO categoryDAO = (CategoryDAO) DAOFactory.getDAO(Category.class);
		
		Category category = categoryDAO.findById(id);
		if (category == null) {
			log.warn("Category not found!");
			return Response.status(Response.Status.NOT_FOUND).entity(responseJson).build();
		} else {
			responseJson.put("id",		category.getId());
			responseJson.put("name",	category.getName());
			responseJson.put("visible", category.isVisible());

			List<Book> books = categoryDAO.getAvailableBooksWPagination(category, pageNo, pageSize, sortBy, sortType, filters, search);

			JSONArray booksByCategory = new JSONArray();
			Iterator<Book> i = books.iterator();
			while (i.hasNext()) {
				Book book = i.next();
				if(book.isAvailable()) {
					JSONObject bookJson = new JSONObject();
					bookJson.put("title", book.getTitle());
					bookJson.put("price", book.getPrice());
					bookJson.put("amount", book.getAmount());
					bookJson.put("id", book.getId());
					booksByCategory.add(bookJson);
				}
			}
			responseJson.put("Amount of Books", categoryDAO.getBookCount(category, filters, search));
			responseJson.put("books", booksByCategory);
		}
		log.debug("GET response:");
		log.debug(responseJson.toString());
		return Response.ok(responseJson.toString(), MediaType.APPLICATION_JSON).build();
	}

	@Override
	@POST @Path("/Category/new")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response create(String request) {
		log.debug("POST /Category/new");
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

		String	name	= (String) requestJson.get("name");
		Boolean visible	= (Boolean) requestJson.get("visible");

		try {
			Category newCategory = new Category(visible, name);
			DAOFactory.getDAO(Category.class).persist(newCategory);
			responseJson.put("id", newCategory.getId());
			responseJson.put("name", newCategory.getName());
			responseJson.put("visible", newCategory.isVisible());
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
	@PUT @Path("/Category/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response update(String request, @PathParam("id") Integer id) {
		log.debug("PUT /Category/"+id);
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
		boolean updateVisible	= requestJson.containsKey("visible");

		try {
			CategoryDAO categoryDAO = (CategoryDAO) DAOFactory.getDAO(Category.class);
			Category category = categoryDAO.findById(id);
			if (category == null) {
				log.error("Category with id: " + id +" not found");
				responseJson.put("status", "Category not found");
				return Response.status(Response.Status.NOT_FOUND)
						.entity(responseJson.toString())
						.build();
			} else {
				if (updateName)		category.setName(		(String)	requestJson.get("name"));
				if (updateVisible)	category.setVisible(	(Boolean)	requestJson.get("visible"));
				categoryDAO.attachDirty(category);
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
	@DELETE @Path("/Category/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response delete(@PathParam("id") Integer id) {
		log.debug("DELETE /Category/"+id);
		JSONObject responseJson = new JSONObject();

		responseJson.put("date", LocalDateTime.now().toString());
		try {
			CategoryDAO categoryDAO = (CategoryDAO) DAOFactory.getDAO(Category.class);
			Category category = categoryDAO.findById(id);
			if (category != null) {
				categoryDAO.delete(category);
				responseJson.put("status", "OK");
			} else {
				log.warn("Category with id: "+ id +" not found!");
				responseJson.put("status", "Category not found");
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