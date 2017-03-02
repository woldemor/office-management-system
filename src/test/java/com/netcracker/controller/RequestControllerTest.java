package com.netcracker.controller;

import com.netcracker.model.dto.RequestDTO;
import com.netcracker.model.entity.Request;
import com.netcracker.repository.data.RequestRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.client.ExpectedCount.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
@ActiveProfiles("test")
@Sql(scripts = "classpath:/sql/test/repository-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class RequestControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private RequestDTO requestDTO;

    private Principal principal = Mockito.mock(Principal.class);

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.stream(converters)
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        Mockito.when(principal.getName()).thenReturn("test2@test.com");
    }

    @Test
    public void addRequestNameNotPresent() throws Exception {
        requestDTO = new RequestDTO();
        requestDTO.setName(null);
        requestDTO.setDescription(null);
        requestDTO.setManager(null);
        requestDTO.setPriority(null);

        mockMvc.perform(post("/api/request/addRequest/")
                .content(this.json(requestDTO))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addRequest() throws Exception {
        requestDTO = new RequestDTO();
        requestDTO.setName("New request");
        requestDTO.setDescription("Make me tea");
        requestDTO.setManager(2L);
        requestDTO.setPriority(2);

        int size = requestRepository.findAll().size();

        mockMvc.perform(post("/api/request/addRequest/")
                .principal(principal)
                .content(this.json(requestDTO))
                .contentType(contentType))
                .andExpect(status().isOk());

        assertEquals(requestRepository.findAll().size(), ++size);
    }

    @Test
    public void getRequest() throws Exception {
        mockMvc.perform(get("/api/request/{requestId}/", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.name", is("Request 2 (closed)")))
                .andExpect(jsonPath("$.description", is("Request 2 description")));
    }

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }


//    @Test
//    public void tryDeleteClosedRequest() throws Exception {
//        mockMvc.perform(post("/api/request/2/delete")
//                .contentType(contentType))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void deleteRequest() throws Exception {
//        mockMvc.perform(post("/api/request/3/delete")
//                .contentType(contentType))
//                .andExpect(status().isOk());
//    }

}