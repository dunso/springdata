package so.dun.springdata.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import so.dun.springdata.entities.Person;

public interface PersonPagingAndStortingRepsotory extends PagingAndSortingRepository<Person, Integer>{

}
