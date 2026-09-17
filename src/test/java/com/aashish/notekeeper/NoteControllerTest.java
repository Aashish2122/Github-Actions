package com.aashish.notekeeper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class NoteControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void contextLoads() {
        // intentionally empty - see below
    }

    @Test
    void listReturnsOk() throws Exception {
        mockMvc.perform(get("/api/notes"))
                .andExpect(status().isOk());
    }

    @Test
    void createReturnsCreatedNote() throws Exception {
        mockMvc.perform(post("/api/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"ci\",\"body\":\"from a test\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("ci"));
    }

    @Test
    void missingNoteReturns404() throws Exception {
        mockMvc.perform(get("/api/notes/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void readinessProbeIsUp() throws Exception {
        mockMvc.perform(get("/actuator/health/readiness"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}