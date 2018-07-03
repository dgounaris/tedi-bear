package server.endpoints.AuthControllerTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.servlet.ServletContext;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import server.endpoints.AuthorizedTests;
import server.endpoints.inputmodels.RegisterInputModel;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration
@WebAppConfiguration
public class RegisterTests extends AuthorizedTests {
	
	@Before
	public void setup() throws Exception {
		setupMockMvc();
	}
	
	@Test
	public void verifyWacLoaded() {
		ServletContext serContext = wac.getServletContext();
		Assert.assertNotNull(serContext);
		Assert.assertTrue(serContext instanceof MockServletContext);
	}
	
	@Test
	public void registerUsersFull() {
		try {
			RegisterInputModel input = new RegisterInputModel();
			input.setEmail("f@f.f");
			input.setPassword("f");
			input.setName("Faidra");
			input.setSurname("Papadopoulou");
			input.setTelNumber("6969696969");
			input.setPicture("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");
			MockHttpServletResponse result = register(input);
			assertEquals(201, result.getStatus());
			input.setEmail("k@k.k");
			input.setPassword("k");
			input.setName("Konstantinos");
			input.setSurname("Spiropoulos");
			input.setTelNumber("6989698969");
			input.setPicture("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");
			result = register(input);
			assertEquals(201, result.getStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void registerUserOnlyEmailPass() {
		try {
			RegisterInputModel input = new RegisterInputModel();
			input.setEmail("a@a.a");
			input.setPassword("a");
			MockHttpServletResponse result = register(input);
			assertEquals(201, result.getStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void registerEmptyPassword() {
		try {
			RegisterInputModel input = new RegisterInputModel();
			input.setEmail("e@e.e");
			MockHttpServletResponse result = register(input);
			assertEquals(400, result.getStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void registerFaultyEmail() {
		try {
			RegisterInputModel input = new RegisterInputModel();
			input.setEmail("aaa.aaa");
			input.setPassword("aaa");
			MockHttpServletResponse result = register(input);
			assertEquals(400, result.getStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void registerEmptyRequest() {
		try {
			RegisterInputModel input = new RegisterInputModel();
			MockHttpServletResponse result = register(input);
			assertEquals(400, result.getStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void registerAndRegisterExistingAccount() {
		try {
			RegisterInputModel input = new RegisterInputModel();
			input.setEmail("b@b.b");
			input.setPassword("b");
			MockHttpServletResponse result = register(input);
			assertEquals(201, result.getStatus());
			//2nd register with same email
			input.setPassword("baltered");
			result = register(input);
			assertEquals(409, result.getStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
}
