package br.edu.exemplo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProdutoControllerIntegrationTest {

 @Autowired
 MockMvc mockMvc;

 @Autowired
 ObjectMapper objectMapper;

 @Test
 void deveCadastrarProdutoPelaApi() throws Exception {

  mockMvc.perform(post("/produtos")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{\"nome\":\"Mouse\",\"preco\":80.0}"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").isNumber())
          .andExpect(jsonPath("$.nome").value("Mouse"))
          .andExpect(jsonPath("$.preco").value(80.0));
 }

 @Test
 void deveBuscarProdutoPelaApi() throws Exception {

  String resposta = mockMvc.perform(post("/produtos")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{\"nome\":\"Mouse\",\"preco\":80.0}"))
          .andExpect(status().isOk())
          .andReturn()
          .getResponse()
          .getContentAsString();

  JsonNode json = objectMapper.readTree(resposta);
  long id = json.get("id").asLong();

  mockMvc.perform(get("/produtos/" + id))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(id))
          .andExpect(jsonPath("$.nome").value("Mouse"))
          .andExpect(jsonPath("$.preco").value(80.0));
 }
}