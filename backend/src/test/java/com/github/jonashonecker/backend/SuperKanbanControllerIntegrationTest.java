package com.github.jonashonecker.backend;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jonashonecker.backend.repo.Item;
import com.github.jonashonecker.backend.repo.ItemDTO;
import com.github.jonashonecker.backend.repo.ItemRepository;
import com.github.jonashonecker.backend.repo.Status;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class SuperKanbanControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DirtiesContext
    void getAllItems_returnEmptyList_whenRepositoryIsEmpty() throws Exception {
        //WHEN
        mockMvc.perform(get("/api/todo"))
                //THEN
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DirtiesContext
    void getAllItems_returnAllPersistedObjects_whenRepositoryContainsObjects() throws Exception {
        //GIVEN
        mockMvc.perform(post("/api/todo")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "description": "Test1",
                        "status": "OPEN"
                        }""")
        );
        mockMvc.perform(post("/api/todo")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "description": "Test2",
                        "status": "OPEN"
                        }""")
        );
        mockMvc.perform(post("/api/todo")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "description": "Test3",
                        "status": "OPEN"
                        }""")
        );

        //WHEN
        mockMvc.perform(get("/api/todo"))
                //THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..status").value(Matchers.everyItem(Matchers.equalTo("OPEN"))))
                .andExpect(jsonPath("$..description").value(Matchers.everyItem(Matchers.containsString("Test"))));
    }

    @Test
    @DirtiesContext
    void addItem_returnItem_whenItemWasSavedInRepository() throws Exception {
        //WHEN
        mockMvc.perform(post("/api/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "description": "Test",
                                "status": "OPEN"
                                }
                                """))
                //THEN
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                        "description": "Test",
                        "status": "OPEN"
                        }
                        """))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isString());
    }

    @Test
    @DirtiesContext
    void addItem_repositoryShouldContainElement_whenItemWasSavedInRepository() throws Exception {
        //GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        ItemDTO newItem = new ItemDTO("abc", Status.OPEN);
        String newItemJSON = objectMapper.writeValueAsString(newItem);
        mockMvc.perform(post("/api/todo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newItemJSON));

        //WHEN
        mockMvc.perform(get("/api/todo"))
                //THEN
                .andExpect(jsonPath("$[0].description").value("abc"))
                .andExpect(jsonPath("$[0].status").value("OPEN"))
                .andExpect(jsonPath("$.[0].id").exists())
                .andExpect(jsonPath("$.[0].id").isString());
    }
}