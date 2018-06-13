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

import pl.bookstore.hibernatemodel.Author;
import pl.bookstore.hibernatemodel.AuthorDAO;
import pl.bookstore.hibernatemodel.Book;
import pl.bookstore.hibernatemodel.DAOFactory;

import java.time.LocalDateTime;

import java.util.Iterator;
import java.util.List;


@Path("/")
public class RESTAuthor extends REST {
	private static final Log log = LogFactory.getLog(RESTAuthor.class);

	@Override
	@GET @Path("/Authors")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response getAll(
			@QueryParam("pageNo")		Integer pageNo,
			@QueryParam("pageSize")		Integer pageSize,
			@QueryParam("sortBy")		String sortBy,
			@QueryParam("sortType")		String sortType,
			@QueryParam("filters")		String filters,
			@QueryParam("search")		String search) {

		log.debug("GET /Authors");
		log.debug("pageNo: "	+ pageNo);
		log.debug("pageSize: "	+ pageSize);
		log.debug("sortBy: "	+ sortBy);
		log.debug("sortType: "	+ sortType);
		log.debug("filters: "	+ filters);
		log.debug("search: "	+ search);

		JSONObject responseJson = new JSONObject();
		responseJson.put("date", LocalDateTime.now().toString());

		JSONArray  jsonAuthors = new JSONArray();
		AuthorDAO authorDAO = (AuthorDAO) DAOFactory.getDAO(Author.class);
		List<Author> authors = authorDAO.getWithPagination(pageNo, pageSize, sortBy, sortType, filters, search);
		if (authors.isEmpty()) {
			log.error("No authors found!");
		} else {
			for (Iterator<Author> i = authors.iterator();i.hasNext();) {
				Author author = i.next();
				JSONObject element = new JSONObject();
				element.put("id",		author.getId());
				element.put("name",		author.getName());
				element.put("surname",	author.getSurname());
				if (author.getNickname() != null) element.put("nickname", author.getNickname());
				jsonAuthors.add(element);
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
		responseJson.put("number of authors", authorDAO.getAuthorCount(filters, search));
		responseJson.put("list of authors", jsonAuthors);
		////////////////
		log.debug("GET response:");
		log.debug(responseJson.toString());

		return Response.ok(responseJson.toString(), MediaType.APPLICATION_JSON).build();
	}
	
	@Override
	@GET @Path("/Author/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response getByID(@PathParam("id") Integer id) {

		log.debug("GET /Author/"+id);
		JSONObject responseJson = new JSONObject();
		responseJson.put("date", LocalDateTime.now().toString());

		Author author = (Author) DAOFactory.getDAO(Author.class).findById(id);
		if (author == null) {
			log.warn("Author not found!");
			return Response.status(Response.Status.NOT_FOUND).build();
		} else {
			log.debug("Author found");
			responseJson.put("id",		author.getId());
			responseJson.put("name",	author.getName());
			responseJson.put("surname",	author.getSurname());
			if (author.getNickname() != null) responseJson.put("nickname", author.getNickname());
			log.debug("GET response:");
			log.debug(responseJson.toString());
			return Response.ok(responseJson.toString(), MediaType.APPLICATION_JSON).build();
		}
	}

	@GET @Path("/Author/{id}/Books")
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
	
		log.debug("GET /Author/"+id+"/Books");
		log.debug("pageNo="+pageNo);
		log.debug("pageSize="+pageSize);
		log.debug("sortBy: "	+ sortBy);
		log.debug("sortType: "	+ sortType);
		log.debug("filters: "	+ filters);
		log.debug("search: "	+ search);

	
		JSONObject responseJson = new JSONObject();
		responseJson.put("date", LocalDateTime.now().toString());
		AuthorDAO authorDAO = (AuthorDAO) DAOFactory.getDAO(Author.class);
	
		Author author = authorDAO.findById(id);
		if (author == null) {
			log.warn("Author not found!");
			return Response.status(404).entity(responseJson).build();
		} else {
			responseJson.put("id",		author.getId());
			responseJson.put("name",	author.getName());
			responseJson.put("surname",	author.getSurname());
			if (author.getNickname() != null) responseJson.put("nickname", author.getNickname());
			List<Book> books = authorDAO.getAvailableBooksWPagination(author, pageNo, pageSize, sortBy, sortType, filters, search);
	
			JSONArray booksByAuthor = new JSONArray();
			Iterator<Book> i = books.iterator();
			while (i.hasNext()) {
				Book book = i.next();
				if(book.isAvailable()) {
					JSONObject bookJson = new JSONObject();
					bookJson.put("title", book.getTitle());
					bookJson.put("price", book.getPrice());
					bookJson.put("amount", book.getAmount());
					bookJson.put("id", book.getId());
					booksByAuthor.add(bookJson);
				}
			}
			responseJson.put("Amount of Books", authorDAO.getBookCount(author, filters, search));
			responseJson.put("books", booksByAuthor);
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
	@POST @Path("/Author/new")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response create(String request) {
		log.debug("POST /Author/new");
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
		String surname	= (String) requestJson.get("surname");
		String nickname	= (String) requestJson.get("nickname");

		try {
			Author newAuthor = new Author(name, surname, nickname, null);
			DAOFactory.getDAO(Author.class).persist(newAuthor);
			responseJson.put("id",		newAuthor.getId());
			responseJson.put("name",	newAuthor.getName());
			responseJson.put("surname",	newAuthor.getSurname());
			if (newAuthor.getNickname() != null) {
				responseJson.put("nickname", newAuthor.getNickname());
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
	@PUT @Path("/Author/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response update(String request, @PathParam("id") Integer id) {
		log.debug("PUT /Author/"+id);
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

		AuthorDAO authorDAO = (AuthorDAO) DAOFactory.getDAO(Author.class);
		boolean updateName		= requestJson.containsKey("name");
		boolean updateSurname	= requestJson.containsKey("surname");
		boolean updateNickname	= requestJson.containsKey("nickname");

		try {
			Author author = authorDAO.findById(id);
			if (author == null) {
				log.error("Author with id: " + id +" not found");
				responseJson.put("status", "Author not found");
				return Response.status(Response.Status.NOT_FOUND)
						.entity(responseJson.toString())
						.build();
			} else {
				if (updateName)		author.setName(		(String)	requestJson.get("name"));
				if (updateSurname)	author.setSurname(	(String)	requestJson.get("surname"));
				if (updateNickname)	author.setNickname(	(String)	requestJson.get("nickname"));
				authorDAO.attachDirty(author);
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
	@DELETE @Path("/Author/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response delete(@PathParam("id") Integer id) {
		log.debug("DELETE /Author/"+id);
		JSONObject responseJson = new JSONObject();
		AuthorDAO authorDAO = (AuthorDAO) DAOFactory.getDAO(Author.class);

		responseJson.put("date", LocalDateTime.now().toString());
		try {
			Author author = authorDAO.findById(id);
			if (author != null) {
				authorDAO.delete(author);
				responseJson.put("status", "OK");
			} else {
				log.warn("Author with id: "+ id +" not found!");
				responseJson.put("status", "Author not found");
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

