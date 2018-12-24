package it.smartcommunitylab.aac.test;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.smartcommunitylab.aac.controller.RolesController;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {
		AACRolesControllerTestConfig.class }, loader = AnnotationConfigWebContextLoader.class, initializers = ConfigFileApplicationContextInitializer.class)
public class AACRolesControllerTest extends OAuth2AwareControllerTest {

	private static final String TEST_ROLE = "TEST/TEST:TEST";

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}
	
	@Test
	public void testGetRoles() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/userroles/me")
				.contentType(MediaType.APPLICATION_JSON).header("Authorization", userToken);
		mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
	}
	
	@Test
	public void testGetAllRoles() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/userroles/user/" + user.getId())
				.contentType(MediaType.APPLICATION_JSON).header("Authorization", token);
		mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
	}	
	
	@Test
	public void testGetClientRoles() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/userroles/client")
				.contentType(MediaType.APPLICATION_JSON).header("Authorization", token);
		mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
	}		
	
	@Test
	public void testAddDeleteRoles() throws Exception {
		int size = getRoleSize();
		
		RequestBuilder request = MockMvcRequestBuilders.put("/userroles/user/{userId}?roles={roles}", user.getId(), TEST_ROLE)
				.contentType(MediaType.APPLICATION_JSON).header("Authorization", token);
		mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		
		int addSize = getRoleSize();
		assertEquals(size + 1, addSize);
		
		request = MockMvcRequestBuilders.delete("/userroles/user/{userId}?roles={roles}", user.getId(), TEST_ROLE)
				.contentType(MediaType.APPLICATION_JSON).header("Authorization", token).content(TEST_ROLE);
		mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().is2xxSuccessful());		

		int deleteSize = getRoleSize();
		assertEquals(size, deleteSize);		
	}	
	
	private int getRoleSize() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		
		RequestBuilder request = MockMvcRequestBuilders.get("/userroles/user/" + user.getId())
				.contentType(MediaType.APPLICATION_JSON).header("Authorization", token);
		String result = mockMvc.perform(request).andReturn().getResponse().getContentAsString();
		List<?> resultList = mapper.readValue(result, List.class);
		return resultList.size();
	}
}

@TestConfiguration
@EnableWebMvc
@ComponentScan(basePackages = { "it.smartcommunitylab.aac"})
class AACRolesControllerTestConfig {

	@Bean
	public RolesController rolesController() {
		return new RolesController();
	}

	
}
