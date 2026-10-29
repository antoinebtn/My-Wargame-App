package app.mywargame;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(properties = {"POSTGRES_PASSWORD=unused-testcontainers", "APP_JWT_SECRET=test-only-jwt-secret-not-used-anywhere-else"})
class ApiFlowTests {

    @Autowired
    WebApplicationContext context;

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test
    void registerLoginAndUseTheApi() throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"Ork@Waaagh.test","password":"dakkadakka","displayName":"Grukk"}"""))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"ork@waaagh.test","password":"dakkadakka","displayName":"Again"}"""))
                .andExpect(status().isConflict());

        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"ork@waaagh.test","password":"wrong-password"}"""))
                .andExpect(status().isUnauthorized());

        String body = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"ork@waaagh.test","password":"dakkadakka"}"""))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String bearer = "Bearer " + JsonPath.read(body, "$.token");

        mvc.perform(get("/api/me").header("Authorization", bearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("Grukk"));

        String paints = mvc.perform(get("/api/paints").param("brand", "citadel_colour").param("q", "abaddon")
                        .header("Authorization", bearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(greaterThan(0)))
                .andReturn().getResponse().getContentAsString();
        Integer paintId = JsonPath.read(paints, "$.content[0].id");

        mvc.perform(post("/api/inventory").header("Authorization", bearer).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"paintId\":" + paintId + "}"))
                .andExpect(status().isOk());
        mvc.perform(post("/api/inventory").header("Authorization", bearer).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"paintId\":" + paintId + "}"))
                .andExpect(jsonPath("$.quantity").value(2));

        mvc.perform(post("/api/armies").header("Authorization", bearer).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Goff Klan","faction":"Orks","gameSystem":"Warhammer 40k"}"""))
                .andExpect(status().isCreated());
        mvc.perform(get("/api/armies").header("Authorization", bearer))
                .andExpect(jsonPath("$[0].name").value("Goff Klan"))
                .andExpect(jsonPath("$[0].progress").value(0));
    }

    @Test
    void rejectsAnonymousRequests() throws Exception {
        mvc.perform(get("/api/armies")).andExpect(status().isUnauthorized());
    }
}
