package com.posto.abastecimento;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void criaEBuscaCombustivel() throws Exception {
        String location = mockMvc.perform(post("/api/v1/combustiveis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Gasolina","precoLitro":5.79}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", is("Gasolina")))
                .andReturn().getResponse().getHeader("Location");

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.precoLitro", is(5.79)));
    }

    @Test
    void criaBombaEAbastecimento() throws Exception {
        String combustivelBody = mockMvc.perform(post("/api/v1/combustiveis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Etanol","precoLitro":3.99}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn().getResponse().getContentAsString();
        Integer combustivelId = JsonPath.read(combustivelBody, "$.id");

        String bombaBody = mockMvc.perform(post("/api/v1/bombas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Bomba 01","combustivelId":%d}
                                """.formatted(combustivelId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.combustivelNome", is("Etanol")))
                .andReturn().getResponse().getContentAsString();
        Integer bombaId = JsonPath.read(bombaBody, "$.id");

        mockMvc.perform(post("/api/v1/abastecimentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"bombaId":%d,"data":"2026-09-29T10:30:00","litros":35.50}
                                """.formatted(bombaId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.precoLitro", is(3.99)))
                .andExpect(jsonPath("$.valorTotal", is(141.65)));

        mockMvc.perform(get("/api/v1/abastecimentos?bombaId={bombaId}&page=0&size=20&sort=data,desc", bombaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].combustivelNome", is("Etanol")));
    }

    @Test
    void requisicaoInvalidaRetorna400() throws Exception {
        mockMvc.perform(post("/api/v1/combustiveis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"","precoLitro":0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Dados invalidos")))
                .andExpect(jsonPath("$.fields.nome").exists())
                .andExpect(jsonPath("$.fields.precoLitro").exists());
    }

    @Test
    void recursoInexistenteRetorna404() throws Exception {
        mockMvc.perform(get("/api/v1/bombas/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Bomba nao encontrada")));
    }
}
