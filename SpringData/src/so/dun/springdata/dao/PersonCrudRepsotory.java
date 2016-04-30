package so.dun.springdata.dao;

import org.springframework.data.repository.CrudRepository;

import so.dun.springdata.entities.Person;

public interface PersonCrudRepsotory extends CrudRepository<Person, Integer>{

}
