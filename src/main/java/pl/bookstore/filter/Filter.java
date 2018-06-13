package pl.bookstore.filter;

public abstract class Filter {
	
	public abstract String getSortBy();
	public abstract String parseSortBy();
	
	public abstract String parse(String request);
	public abstract String getPattern(String request);
}
