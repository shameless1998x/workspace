package com.example.orderservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldDownloadHelloDocx() throws Exception {
        mockMvc.perform(get("/word/download"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=hello.docx"))
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .andExpect(result -> {
                    byte[] body = result.getResponse().getContentAsByteArray();
                    if (body.length == 0) {
                        throw new AssertionError("响应体不应为空");
                    }
                });
    }
}
