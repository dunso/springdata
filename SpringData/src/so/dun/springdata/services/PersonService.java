package so.dun.springdata.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import so.dun.springdata.dao.PersonCrudRepsotory;
import so.dun.springdata.dao.PersonRepsotory;
import so.dun.springdata.entities.Person;

@Service
public class PersonService {
	
	@Autowired
	private PersonRepsotory personRepsotory;
	
	@Autowired
	private PersonCrudRepsotory crudPersonRepsotory;
	
	@Transactional
	public void updatePersonEmail(String email, Integer id){
		personRepsotory.updatePersonEmail(id,email);
	}
	
	@Transactional
	public void savePersons(List<Person> persons) {
		crudPersonRepsotory.save(persons);
	}

}
