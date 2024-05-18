package com.github.jonashonecker.backend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jonashonecker.backend.repo.Item;
import com.github.jonashonecker.backend.repo.ItemDTO;
import com.github.jonashonecker.backend.repo.ItemRepository;
import com.github.jonashonecker.backend.repo.Status;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class SuperKanbanControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    //Utility
    public void postItem(String description, String status) throws Exception {
        String jsonContent = String.format("""
                {
                "description": "%s",
                "status": "%s"
                }""", description, status);

        mockMvc.perform(post("/api/todo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent)
        );
    }

    public List<Item> getAllItems() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        String result = mockMvc.perform(get("/api/todo"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(result, new TypeReference<>() {
        });
    }

    @Test
    @DirtiesContext
    void getItem_return404AndMessage_whenIdNotInRepository() throws Exception {
        //WHEN
        mockMvc.perform(get("/api/todo/1"))
                // THEN
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.message").value("The requested Id: 1 does not exist."));
    }

    @Test
    @DirtiesContext
    void getItem_returnItem_whenIdInRepository() throws Exception {
        //GIVEN
        postItem("Test1", "OPEN");
        Item addedItem = getAllItems().getFirst();
        String url = "/api/todo/" + addedItem.id();

        //WHEN
        mockMvc.perform(get(url))
                // THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(addedItem.id()))
                .andExpect(jsonPath("$.description").value("Test1"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

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
        postItem("Test1", "OPEN");
        postItem("Test2", "OPEN");
        postItem("Test3", "OPEN");

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
                .andExpect(jsonPath("$[0].id").isString());
    }

    @Test
    @DirtiesContext
    void updateItem_returnItemWithUpdatedDescription_whenItemDescriptionUpdated() throws Exception {
        //GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        postItem("abc", "OPEN");
        Item itemToUpdate = getAllItems().getFirst().withDescription("def");
        String itemToUpdateJson = objectMapper.writeValueAsString(itemToUpdate);
        String url = "/api/todo/" + itemToUpdate.id();

        //WHEN
        mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemToUpdateJson))
                //THEN
                .andExpect(content().json(itemToUpdateJson));
    }

    @Test
    @DirtiesContext
    void updateItem_repositoryShouldContainUpdatedItem_whenItemUpdated() throws Exception {
        //GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        postItem("abc", "OPEN");
        Item itemToUpdate = getAllItems().getFirst().withDescription("def");
        String itemToUpdateJson = objectMapper.writeValueAsString(itemToUpdate);
        String url = "/api/todo/" + itemToUpdate.id();

        //WHEN
        String result = mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemToUpdateJson))
                .andReturn()
                .getResponse()
                .getContentAsString();
        Item actual = objectMapper.readValue(result, Item.class);

        //THEN
        Item expected = getAllItems().getFirst();
        assertEquals(expected, actual);
    }

    @Test
    @DirtiesContext
    void deleteItem_repositoryShouldNotContainItem_whenItemDeleted() throws Exception {
        //GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        postItem("abc", "OPEN");
        String itemId = getAllItems().getFirst().id();
        String url = "/api/todo/" + itemId;

        //WHEN
        mockMvc.perform(delete(url));

        //THEN
        assertEquals(List.of(), getAllItems());
    }

    @Test
    @DirtiesContext
    void deleteItem_repositoryShouldNotContainDeletedItemButOthers_whenItemDeleted() throws Exception {
        //GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        postItem("abc", "OPEN");
        String itemId = getAllItems().getFirst().id();
        String url = "/api/todo/" + itemId;

        postItem("def", "OPEN");
        postItem("hij", "IN_PROGRESS");

        //WHEN
        mockMvc.perform(delete(url));

        //THEN
        mockMvc.perform(get("/api/todo"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").isString())
                .andExpect(jsonPath("$[1].id").isString())
                .andExpect(jsonPath("$..status").value(Matchers.containsInAnyOrder("OPEN", "IN_PROGRESS")))
                .andExpect(jsonPath("$..description").value(Matchers.containsInAnyOrder("def", "hij")));


    }
}