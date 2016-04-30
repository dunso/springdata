package so.dun.springdata.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import so.dun.springdata.entities.Person;

public class PersonJpaRepsotoryImpl implements PersonDao {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public void test() {
		Person person = entityManager.find(Person.class, 1);
		System.out.println(person);
	}

}
