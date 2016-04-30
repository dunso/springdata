package so.dun.springdata.test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.sql.DataSource;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;

import so.dun.springdata.dao.PersonJpaRepsotory;
import so.dun.springdata.dao.PersonPagingAndStortingRepsotory;
import so.dun.springdata.dao.PersonRepsotory;
import so.dun.springdata.entities.Person;
import so.dun.springdata.services.PersonService;

public class SpringDataTest {

	private ApplicationContext ctx = null;
	private PersonRepsotory  personRepsotory = null;
	private PersonPagingAndStortingRepsotory personPagingAndStortingRepsotory = null;
	private PersonJpaRepsotory personJpaRepsotory = null;
	private PersonService personService;
	
	{
		ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		personRepsotory = ctx.getBean(PersonRepsotory.class);
		personPagingAndStortingRepsotory = ctx.getBean(PersonPagingAndStortingRepsotory.class);
		personJpaRepsotory = ctx.getBean(PersonJpaRepsotory.class);
		personService = ctx.getBean(PersonService.class);
	}
	
	@Test
	public void testDataSource() throws SQLException {
		DataSource dataSource = ctx.getBean(DataSource.class);
		System.out.println(dataSource.getConnection());
	}
	
	@Test
	public void testJpa() {
		
	}
	
	@Test
	public void testHelloWorldSpringData(){
		System.out.println(personRepsotory.getClass().getName());
		Person person = personRepsotory.getByLastName("dunso");
		System.out.println(person);
	}
	
	@Test
	public void testKeyWords1(){
		List<Person> persons = personRepsotory.getByLastNameStartingWithAndIdLessThan("d", 20);
		System.out.println(persons.size());
		
		persons = personRepsotory.getByEmailInAndBirthLessThan(Arrays.asList("admin@dun.so","dunso@dun.so"), new Date());
		System.out.println(persons.size());
	}
	
	@Test
	public void testKeyWords2(){
		List<Person> persons = personRepsotory.getByAddress_IdGreaterThan(1);
		System.out.println(persons);
	}
	
	@Test
	public void testQueryAnnotation(){
		Person person = personRepsotory.getMaxIdPerson();
		System.out.println(person);
	}
	
	@Test
	public void testQueryAnnotationParams1(){
		List<Person> persons = personRepsotory.testQueryAnnotationParams1("dunso", "admin@dun.so");
		System.out.println(persons);
	}
	
	@Test
	public void testQueryAnnotationParams2(){
		List<Person> persons = personRepsotory.testQueryAnnotationParams2("admin@dun.so","dunso");
		System.out.println(persons);
	}

	@Test
	public void testQueryAnnotationLikeParam1(){
		List<Person> persons = personRepsotory.testQueryAnnotationLikeParam1("d","a");
		System.out.println(persons.size());
	}
	
	@Test
	public void testQueryAnnotationLikeParam2(){
		List<Person> persons = personRepsotory.testQueryAnnotationLikeParam2("a","d");
		System.out.println(persons.size());
	}
	
	@Test
	public void testNativeQuery(){
		long count = personRepsotory.getTotalCount();
		System.out.println(count);
	}
	
	@Test
	public void testModifying(){
		personService.updatePersonEmail("dunso@dun.so", 1);
	}
	
	@Test
	public void testCrudRepository(){
		List<Person> persons = new ArrayList<Person>();
		for(int i = 'a'; i <= 'z'; i++){
			Person person = new Person();
			person.setBirth(new Date());
			person.setEmail((char)i + "" + (char)i + "@dun.so");
			person.setLastName((char)i + "" + (char)i);
			persons.add(person);
		}
		personService.savePersons(persons);
	}
	
	@Test
	public void testPagingAndSortingRespository(){
		//pageNo从0开始
		int pageNo = 3;
		int pageSize = 5;
		//Pageable 接口通常使用其PageRequest实现类，其中封装了需要分页的信息
		//排序相关的，Sort封装了排序的信息
		//Order是具体针对某一个属性进行升序还是降序
		Order order1 = new Order(Direction.DESC, "id");
		Order order2 = new Order(Direction.ASC,"email");
		Sort sort = new Sort(order1,order2);
		
		PageRequest pageable = new PageRequest(pageNo, pageSize,sort);
		Page<Person> page = personPagingAndStortingRepsotory.findAll(pageable);
		System.out.println("总记录数："+page.getTotalElements());
		System.out.println("当前第几页："+(page.getNumber()+1));
		System.out.println("总页数："+ page.getTotalPages());
		System.out.println("当前页面的List："+page.getContent());
		System.out.println("当前页面的记录数："+page.getNumberOfElements());
	}
	
	@Test
	public void testJpaRepository(){
		Person person = new Person();
		person.setBirth(new Date());
		person.setEmail("admin@dun.so");
		person.setLastName("dunso");
		person.setId(79);
		Person person2 = personJpaRepsotory.saveAndFlush(person);
		System.out.println(person == person2);
	}
	
	/**
	 * 目标：实现带查询条件的分页。ID>5为条件
	 * 调用JpaSpecificationExecutor的findAll() 方法
	 * Specification:封装了JPA Criteria的查询的条件
	 * Pageable：封装了请求分页的信息：例如pageNo,pageSize,Sort
	 */
	@Test
	public void testJpaSpecificationExecutor(){
		int pageNo = 3-1;
		int pageSize = 5;
		PageRequest pageable = new PageRequest(pageNo, pageSize);
		//通常使用Specification的匿名内部类
		Specification<Person> specification = new Specification<Person>() {
			/**
			 * @param root:代表查询的实体类
			 * @Param query:可以从中得到Root对象，即告知JPA Criteria出啊讯哪一个实体类。
			 * 还可以来添加查询条件，可以结合EntityManager对象得到最终的TypeQuery对象
			 * @param *cb:CriteriaBuilder 对象。用于创建Criteria现骨干对象的工厂，当然可以从中获取到Predicate对象
			 * @return:*Predicate类型，代表一个查询条件。
			 */
			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public Predicate toPredicate(Root<Person> root, CriteriaQuery<?> query,
					CriteriaBuilder cb) {
				Path path = root.get("id");
				Predicate predicate = cb.gt(path, 5);
				return predicate;
			}
		};
		Page<Person> page = personJpaRepsotory.findAll(specification,pageable);
		System.out.println("总记录数："+page.getTotalElements());
		System.out.println("当前第几页："+(page.getNumber()+1));
		System.out.println("总页数："+ page.getTotalPages());
		System.out.println("当前页面的List："+page.getContent());
		System.out.println("当前页面的记录数："+page.getNumberOfElements());
	}
	
	@Test
	public void testCustomRepositoryMethod(){
		personJpaRepsotory.test();
	}
	
	
	
	
	

}
