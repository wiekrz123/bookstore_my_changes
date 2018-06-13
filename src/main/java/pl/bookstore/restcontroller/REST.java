package pl.bookstore.restcontroller;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

public abstract class REST {
	public abstract Response getAll(
			@QueryParam("pageNo")		Integer pageNo,
			@QueryParam("pageSize")		Integer pageSize,
			@QueryParam("sortBy")		String sortBy,
			@QueryParam("sortType")		String sortType,
			@QueryParam("filters")		String filters,
			@QueryParam("search")		String search);
	
	public abstract Response getByID(@PathParam("id") Integer id);
	public abstract Response create(String request);
	public abstract Response update(String request, @PathParam("id") Integer id);
	public abstract Response delete(@PathParam("id") Integer id);
}

