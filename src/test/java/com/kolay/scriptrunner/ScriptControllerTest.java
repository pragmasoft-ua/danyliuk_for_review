package com.kolay.scriptrunner;

import com.kolay.scriptrunner.model.ScriptDetails;
import com.kolay.scriptrunner.model.ScriptStatus;
import com.kolay.scriptrunner.model.ScriptSummaryDTO;
import com.kolay.scriptrunner.service.ScriptService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@WebMvcTest(ScriptController.class)
public class ScriptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScriptService service;

    @Test
    public void addScript_whenPostMethod() throws Exception {

        ScriptDetails sd = new ScriptDetails(1L, "Lorem ipsum", LocalDateTime.now());
        given(service.addScript("Lorem ipsum", false)).willReturn(sd);

        mockMvc.perform(post("/script")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("Lorem ipsum"))
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Location", "/script/1"));
    }

    @Test
    public void returnJsonArray_whenGetScript() throws Exception {
        List<ScriptSummaryDTO> list = List.of(new ScriptSummaryDTO(1L, ScriptStatus.EXECUTING, LocalDateTime.now(), 100L),
                                              new ScriptSummaryDTO(2L, ScriptStatus.EXECUTING, LocalDateTime.now(), 100L));
        given(service.getScriptsList(null, null)).willReturn(list);

        mockMvc.perform(get("/script")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].status", is("EXECUTING")));
    }

    @Test
    public void returnJson_whenGetScriptById() throws Exception {
        ScriptDetails sd = new ScriptDetails(1L, "Lorem ipsum", LocalDateTime.now());
        given(service.findById(1L)).willReturn(sd);

        mockMvc.perform(get("/script/1")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.body", is("Lorem ipsum")));
    }

    @Test
    public void returnOk_whenDelete() throws Exception {
        given(service.deleteById(1L)).willReturn(200);

        mockMvc.perform(delete("/script/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
