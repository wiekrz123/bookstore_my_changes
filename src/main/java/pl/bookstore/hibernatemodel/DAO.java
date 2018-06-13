package pl.bookstore.hibernatemodel;

import java.util.List;

public abstract class DAO {

		public abstract void persist(Object transientInstance);
		public abstract void attachDirty(Object instance);
		public abstract void delete(Object persistentInstance);
		public abstract Object findById(Integer id);

		@SuppressWarnings("rawtypes")
		public abstract List getWithPagination(
				Integer pageNo,
				Integer pageSize,
				String sortBy,
				String sortType,
				String filters,
				String search);
}
