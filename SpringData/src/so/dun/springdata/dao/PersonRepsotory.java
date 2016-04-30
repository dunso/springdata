package so.dun.springdata.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.data.repository.query.Param;

import so.dun.springdata.entities.Person;


/**
 * 1、Repository是一个空接口，即是一个标记接口
 * 2、若我们定义的接口继承了Repository，则该接口会被IOC容器识别为一个Repository Bean,纳入到
 * IOC容器中，进而可以在该接口中定义满足一定规范的方法
 * 3、也可以通过@RepositoryDefinition注解来替代继承Repository接口
 */

/**
 * 在Repository子接口中声明方法
 * 1、不是随便声明的，需要符合一定的规范
 * 2、查询方法以find | read | get开头
 * 3、设计条件查询时，条件的属性用条件关键字连接
 * 4、要注意的是：条件属性首字母大写
 * 5、支持属性的级联查询，若当前类有符合条件的属性，则优先使用,若需要使用级联属性，则属性之间是有_进行连接
 */
@RepositoryDefinition(domainClass=Person.class,idClass=Integer.class)
public interface PersonRepsotory{ //extends Repository<Person, Integer>{

	//根据lastName来获取对应的Person
	Person getByLastName(String lastName);
	
	//Where lastName like ?% and id < ?
	List<Person> getByLastNameStartingWithAndIdLessThan(String lastName,Integer id);
	
	//Where lastName like %? and id < ?
	List<Person> getByLastNameEndingWithAndIdLessThan(String lastName,Integer id);
	
	//Where Email in(?,?,?) OR birth < ?
	List<Person> getByEmailInAndBirthLessThan(List<String> emails, Date birth);
	
	//Where a.id > ?
	List<Person> getByAddress_IdGreaterThan(Integer id);
	
	//查询id最大的那个Person
	//使用@Query注解可以自定义JPQL语句以实现更灵活的查询
	@Query("SELECT p FROM Person p WHERE p.id = (SELECT max(p2.id) FROM Person p2)")
	Person getMaxIdPerson();
	
	/**
	 * 为@Query注解传递参数的方式
	 * 1、使用占位符
	 * 2、使用命令参数的方式
	 */
	@Query("SELECT p FROM Person p WHERE p.lastName = ?1 AND p.email = ?2")
	List<Person> testQueryAnnotationParams1(String lastName,String email);
	
	@Query("SELECT p FROM Person p WHERE p.lastName = :lastName AND p.email = :email")
	List<Person> testQueryAnnotationParams2(@Param("email") String email, @Param("lastName") String lastName);
	
	//SpringData 允许在占位符上添加%%
	@Query("SELECT p FROM Person p WHERE p.lastName LIKE %?1% OR p.email LIKE %?2%")
	List<Person> testQueryAnnotationLikeParam1(String lastName,String email);
	
	//SpringData 允许在占位符上添加%%
	@Query("SELECT p FROM Person p WHERE p.lastName LIKE %:lastName% OR p.email LIKE %:email%")
	List<Person> testQueryAnnotationLikeParam2(@Param("email") String email, @Param("lastName") String lastName);
	
	//设置nativeQuery=true即可以使用原生的SQL查询
	@Query(value="SELECT count(id) FROM SPRINGDATA_PERSONS",nativeQuery=true)
	long getTotalCount();
	
	/**
	 * 可以通过自定义的jPQL完成UPDATE和DELETE操作，注意，JPQL不支持使用INSERT
	 * 在@Query注解中编写JPQL语句，但必须使用@Modifying进行修饰.以通知SpringData，这是一个UPDATE或DELETE操作
	 * UPDATE或DELETE操作需要使用事务，此时需要定义Service层，在Service层的方法上添加事务操作
	 * 默认情况下，SpringData的每个方法上都有事务，但都是一个只读事务，他们不能完成修改操作！
	 */
	@Modifying
	@Query("UPDATE Person p SET p.email = :email WHERE id = :id")
	void updatePersonEmail(@Param("id") Integer id,@Param("email") String email);
}
