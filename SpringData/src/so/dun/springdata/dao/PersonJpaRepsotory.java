package so.dun.springdata.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import so.dun.springdata.entities.Person;

public interface PersonJpaRepsotory extends JpaRepository<Person, Integer>,JpaSpecificationExecutor<Person>,PersonDao{

}
