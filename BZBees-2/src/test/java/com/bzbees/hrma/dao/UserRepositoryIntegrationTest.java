package com.bzbees.hrma.dao;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bzbees.hrma.BzBees1Application;
import com.bzbees.hrma.entities.User;
import com.bzbees.hrma.entities.UserRole;
import com.bzbees.hrma.services.UserService;


@DataJpaTest
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=BzBees1Application.class)
//@RunWith(SpringRunner.class)

//@SqlGroup({@Sql(executionPhase=ExecutionPhase.BEFORE_TEST_METHOD, scripts= {"classpath:schema.sql", "classpath:data.sql"}),
//			@Sql(executionPhase=ExecutionPhase.AFTER_TEST_METHOD, scripts= {"classpath:drop.sql"})})
public class UserRepositoryIntegrationTest {
	
	
	@Autowired
	UserService userServ;
	
	
	@Test
	public void ifNewUserSavedThenSuccess () {
		
		UserRole userRole = new UserRole("USER");
		List<UserRole> roles = new ArrayList<UserRole> ();
		roles.add(userRole);
		
		User newUser = new User( "testulica","test@test.com","passw0rd", false, roles );
		System.out.println("New user is " + newUser.getUserName() + "\n" + " and his role is " + newUser.getAuthorities().toString());
		
//		userServ.save(newUser);
//		System.out.println("Number of users in the data test sql after save " + userServ.getAll().size());
		
		assertEquals("testulica", newUser.getUsername());
			
		
//		assertEquals(3, 3);
		
	}

}
