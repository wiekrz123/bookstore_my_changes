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
import pl.bookstore.hibernatemodel.Book;
import pl.bookstore.hibernatemodel.BookDAO;
import pl.bookstore.hibernatemodel.Publisher;
import pl.bookstore.hibernatemodel.Category;
import pl.bookstore.hibernatemodel.DAOFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;


@Path("/")
public class RESTBook extends REST {
	private static final Log log = LogFactory.getLog(RESTBook.class);

	@Override
	@GET @Path("/Books")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response getAll(
			@QueryParam("pageNo")		Integer pageNo,
			@QueryParam("pageSize")		Integer pageSize,
			@QueryParam("sortBy")		String sortBy,
			@QueryParam("sortType")		String sortType,
			@QueryParam("filters")		String filters,
			@QueryParam("search")		String search) {

		log.debug("GET /Books");
		log.debug("pageNo: "	+ pageNo);
		log.debug("pageSize: "	+ pageSize);
		log.debug("sortBy: "	+ sortBy);
		log.debug("sortType: "	+ sortType);
		log.debug("filters: "	+ filters);
		log.debug("search: "	+ search);

		JSONObject responseJson = new JSONObject();
		responseJson.put("date", LocalDateTime.now().toString());

		JSONArray  jsonBooks = new JSONArray();

		BookDAO bookDAO = (BookDAO) DAOFactory.getDAO(Book.class);
		List<Book> books = bookDAO.getWithPagination(pageNo, pageSize, sortBy, sortType, filters, search);
		if (books.isEmpty()) {
			log.error("No books found!");
		} else {
			for (Iterator<Book> i = books.iterator(); i.hasNext();) {
				Book book = i.next();
				JSONObject element = new JSONObject();
				
				element.put("id",			book.getId());
				element.put("isbn",			book.getIsbn());
				element.put("title",		book.getTitle());
				element.put("price",		book.getPrice());
				element.put("amount",		book.getAmount());
				element.put("available",	book.isAvailable());
				element.put("description",	book.getDescription());
				element.put("link",			book.getLink());
				//
				JSONObject publisher = new JSONObject();
				publisher.put("id",			book.getPublisher().getId());
				publisher.put("name",		book.getPublisher().getName());
				publisher.put("address",	book.getPublisher().getAddress());
				publisher.put("email",		book.getPublisher().getEmail());
				element.put("publisher", publisher);
				//
				JSONArray authors = new JSONArray();
				for (Iterator<Author> j = book.getAuthors().iterator(); j.hasNext();) {
					Author author = j.next();
					JSONObject authorJson = new JSONObject();
					authorJson.put("id", author.getId());
					authorJson.put("name", author.getName());
					authorJson.put("surname", author.getSurname());
					if (author.getNickname() != null) {
						authorJson.put("nickname", author.getNickname());
					}
					authors.add(authorJson);
				}
				element.put("authors", authors);
				//
				JSONArray categories = new JSONArray();
				for (Iterator<Category> j = book.getCategories().iterator(); j.hasNext();) {
					Category category = j.next();
					JSONObject categoryJson = new JSONObject();
					categoryJson.put("id",		category.getId());
					categoryJson.put("name",	category.getName());
					categoryJson.put("visible",	category.isVisible());
					categories.add(categoryJson);
				}
				element.put("categories", categories);
				//
				jsonBooks.add(element);
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
		responseJson.put("number of books", bookDAO.getBookCount(filters, search));
		responseJson.put("list of books", jsonBooks);
		////////////////
		log.debug("GET response:");
		log.debug(responseJson.toString());

		return Response.ok(responseJson.toString(), MediaType.APPLICATION_JSON).build();
	}

	@Override
	@GET @Path("/Book/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response getByID(@PathParam("id") Integer id) {

		log.debug("GET /Book/"+id);
		JSONObject responseJson = new JSONObject();
		responseJson.put("date", LocalDateTime.now().toString());

		Book book = ((BookDAO) DAOFactory.getDAO(Book.class)).findByIdWDetails(id);
		if (book == null) {
			log.warn("Book not found!");
			return Response.status(Response.Status.NOT_FOUND).build();
		} else {
			log.debug("Book found");
			responseJson.put("id",			book.getId());
			responseJson.put("isbn",		book.getIsbn());
			responseJson.put("title",		book.getTitle());
			responseJson.put("amount",		book.getAmount());			
			responseJson.put("price",		book.getPrice());
			responseJson.put("available",	book.isAvailable());
			responseJson.put("description",	book.getDescription());		
			responseJson.put("link",	book.getLink());
			
//
			JSONObject publisher = new JSONObject();
			publisher.put("id",				book.getPublisher().getId());
			publisher.put("name",			book.getPublisher().getName());
			publisher.put("address",		book.getPublisher().getAddress());
			publisher.put("email",			book.getPublisher().getEmail());
			responseJson.put("publisher",	publisher);
			//
			JSONArray authors = new JSONArray();
			for (Iterator<Author> j = book.getAuthors().iterator(); j.hasNext();) {
				Author author = j.next();
				JSONObject authorJson = new JSONObject();
				authorJson.put("id", author.getId());
				authorJson.put("name", author.getName());
				authorJson.put("surname", author.getSurname());
				if (author.getNickname() != null) {
					authorJson.put("nickname", author.getNickname());
				}
				authors.add(authorJson);
			}
			responseJson.put("authors", authors);
			//
			JSONArray categories = new JSONArray();
			for (Iterator<Category> j = book.getCategories().iterator(); j.hasNext();) {
				Category category = j.next();
				JSONObject categoryJson = new JSONObject();
				categoryJson.put("id",		category.getId());
				categoryJson.put("name",	category.getName());
				categoryJson.put("visible",	category.isVisible());
				categories.add(categoryJson);
			}
			responseJson.put("categories", categories);
			//

//		
			log.debug("GET response:");
			log.debug(responseJson.toString());
			return Response.ok(responseJson.toString(), MediaType.APPLICATION_JSON).build();
		}
	}

	/*
	@GET @Path("/Books/{id}/Orders")
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
	
		Author author = AuthorDAO.findById(id);
		if (author == null) {
			log.warn("Author not found!");
			return Response.status(404).entity(responseJson).build();
		} else {
			responseJson.put("id",		author.getId());
			responseJson.put("name",	author.getName());
			responseJson.put("surname",	author.getSurname());
			if (author.getNickname() != null) responseJson.put("nickname", author.getNickname());
			List<Book> books = AuthorDAO.getAvailableBooksWPagination(author, pageNo, pageSize, sortBy, sortType, filters, search);
	
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
			responseJson.put("Amount of Books", AuthorDAO.getBookCount(author, filters, search));
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
	*/

	@Override
	@POST @Path("/Book/new")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response create(String request) {
		log.debug("POST /Book/new");
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

		String		isbn		= (String)		requestJson.get("isbn");
		String		title		= (String)		requestJson.get("title");
		BigDecimal	price		= BigDecimal.valueOf((Double) requestJson.get("price"));
		Integer		amount		= Integer.parseInt(requestJson.get("amount").toString());
		Boolean		available	= (Boolean)		requestJson.get("available");
		String		description	= (String)		requestJson.get("description");
		String		link		= (String)		requestJson.get("link");
		
//
		JSONObject publisherJson = (JSONObject)	requestJson.get("publisher");
		Publisher publisher = (Publisher) DAOFactory.getDAO(Publisher.class).findById(Integer.parseInt(publisherJson.get("id").toString()));
		
		Set<Author> authors = new HashSet<Author>();
		JSONArray authorsJson = (JSONArray) requestJson.get("authors");
		for (Iterator<JSONObject> i = authorsJson.iterator(); i.hasNext();) {
			JSONObject author = i.next();
			authors.add(
					(Author) DAOFactory.getDAO(Author.class).findById(Integer.parseInt(author.get("id").toString())));
		}
		
		Set<Category> categories = new HashSet<Category>();
		JSONArray categoriesJson = (JSONArray) requestJson.get("categories");
		for (Iterator<JSONObject> i = categoriesJson.iterator(); i.hasNext();) {
			JSONObject category = i.next();
			categories.add((Category) DAOFactory.getDAO(Category.class)
					.findById(
							Integer.parseInt(category.get("id").toString())));
		}
//
		try {
			Book newBook = new Book(publisher, isbn,
					title, price, amount, available,
					description, link, null,
					authors, categories);
			DAOFactory.getDAO(Book.class).persist(newBook);
			responseJson.put("id",			newBook.getId());
			responseJson.put("isbn",		newBook.getIsbn());
			responseJson.put("title",		newBook.getTitle());
			responseJson.put("price",		newBook.getPrice());
			responseJson.put("amount",		newBook.getAmount());
			responseJson.put("available",	newBook.isAvailable());
			responseJson.put("status",	newBook.getDescription());
			responseJson.put("link",		newBook.getLink());	
			JSONObject savedPublisher = new JSONObject();
			savedPublisher.put("id",		newBook.getPublisher().getId());
			savedPublisher.put("name",		newBook.getPublisher().getName());
			savedPublisher.put("address",	newBook.getPublisher().getAddress());
			savedPublisher.put("email",		newBook.getPublisher().getEmail());
			responseJson.put("publisher",	savedPublisher);
			//
			JSONArray savedAuthors = new JSONArray();
			for (Iterator<Author> j = newBook.getAuthors().iterator(); j.hasNext();) {
				Author author = j.next();
				JSONObject authorJson = new JSONObject();
				authorJson.put("id",		author.getId());
				authorJson.put("name",		author.getName());
				authorJson.put("surname",	author.getSurname());
				if (author.getNickname() != null) {
					authorJson.put("nickname", author.getNickname());
				}
				savedAuthors.add(authorJson);
			}
			responseJson.put("authors", savedAuthors);
			//
			JSONArray savedCategories = new JSONArray();
			for (Iterator<Category> j = newBook.getCategories().iterator(); j.hasNext();) {
				Category category = j.next();
				JSONObject categoryJson = new JSONObject();
				categoryJson.put("id",		category.getId());
				categoryJson.put("name",	category.getName());
				categoryJson.put("visible",	category.isVisible());
				savedCategories.add(categoryJson);
			}
			responseJson.put("categories", savedCategories);
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
	@PUT @Path("/Book/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response update(String request, @PathParam("id") Integer id) {
		log.debug("PUT /Book/"+id);
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

		boolean updateIsbn			= requestJson.containsKey("isbn");
		boolean updateTitle			= requestJson.containsKey("title");
		boolean updatePrice			= requestJson.containsKey("price");
		boolean updateAmount		= requestJson.containsKey("amount");
		boolean updateAvailable		= requestJson.containsKey("available");
		boolean updateDescription	= requestJson.containsKey("description");
		boolean updateLink			= requestJson.containsKey("link");
		boolean updatePublisher		= requestJson.containsKey("publisher");
		boolean updateAuthors		= requestJson.containsKey("authors");
		boolean updateCategories	= requestJson.containsKey("categories");
		BookDAO bookDAO = (BookDAO) DAOFactory.getDAO(Book.class);		
		
		try {
			Book book = bookDAO.findByIdWDetails(id);
			if (book == null) {
				log.error("Book with id: " + id +" not found");
				responseJson.put("status", "Book not found");
				return Response.status(Response.Status.NOT_FOUND)
						.entity(responseJson.toString())
						.build();
			} else {
				if (updateIsbn)
					book.setIsbn(		(String)	requestJson.get("isbn"));
				if (updateTitle)		book.setTitle(		(String)	requestJson.get("title"));
				if (updateAvailable)	book.setAvailable(  (Boolean)	requestJson.get("available"));
				if (updateDescription)	book.setDescription((String)	requestJson.get("description"));
				if (updateLink)			book.setLink(		(String)	requestJson.get("link"));
				
				if (updatePrice)
					book.setPrice(		BigDecimal.valueOf(	(Double)	requestJson.get("price")));
				if (updateAmount)
					book.setAmount(		Integer.parseInt(				requestJson.get("amount").toString()));
				//
				if(updatePublisher) {
					JSONObject publisherJson = (JSONObject)	requestJson.get("publisher");
					Publisher publisher = (Publisher) DAOFactory.getDAO(Publisher.class).findById(Integer.parseInt(publisherJson.get("id").toString()));
					book.setPublisher(publisher);
				}
				
				if (updateAuthors) {
					Set<Author> authors = new HashSet<Author>();
					JSONArray authorsJson = (JSONArray) requestJson.get("authors");
					for (Iterator<JSONObject> i = authorsJson.iterator(); i.hasNext();) {
						JSONObject author = i.next();
						authors.add(
								(Author) DAOFactory.getDAO(Author.class).findById(Integer.parseInt(author.get("id").toString()))
								);
					}
					book.setAuthors(authors);
				}
				
				if (updateCategories) {
					Set<Category> categories = new HashSet<Category>();
					JSONArray categoriesJson = (JSONArray) requestJson.get("categories");
					for (Iterator<JSONObject> i = categoriesJson.iterator(); i.hasNext();) {
						JSONObject category = i.next();
						categories.add((Category) DAOFactory.getDAO(Category.class).findById(Integer.parseInt(category.get("id").toString())));
					}
					book.setCategories(categories);
				}
				
				bookDAO.attachDirty(book);
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
	@DELETE @Path("/Book/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response delete(@PathParam("id") Integer id) {
		log.debug("DELETE /Book/"+id);
		JSONObject responseJson = new JSONObject();

		responseJson.put("date", LocalDateTime.now().toString());
		BookDAO bookDAO = (BookDAO) DAOFactory.getDAO(Book.class);	
		try {
			Book book = bookDAO.findById(id);
			if (book != null) {
				bookDAO.delete(book);
				responseJson.put("status", "OK");
			} else {
				log.warn("Book with id: "+ id +" not found!");
				responseJson.put("status", "Book not found");
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

