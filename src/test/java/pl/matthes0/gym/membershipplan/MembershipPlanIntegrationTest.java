package pl.matthes0.gym.membershipplan;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import pl.matthes0.gym.gym.dtos.GymCreateDto;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MembershipPlanIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateAndReturnMembershipPlans() throws Exception {
        GymCreateDto gymDto = new GymCreateDto("Power Gym", "Central 1", "123456");
        MvcResult gymResult = mockMvc.perform(post("/api/gyms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gymDto)))
                .andExpect(status().isCreated())
                .andReturn();
        String response = gymResult.getResponse().getContentAsString();
        Long gymId = ((Number) com.jayway.jsonpath.JsonPath.read(response, "$.id")).longValue();
        String jsonRequest = """
        {
            "name": "Pro Plan",
            "plan": "PREMIUM",
            "monthlyPrice": {
                "amount": 150.12,
                "currency": "PLN"
            },
            "durationInMonths": 12,
            "maxMembers": 100
        }
        """;
        mockMvc.perform(post("/api/gyms/" + gymId + "/membership-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Pro Plan"))
                .andExpect(jsonPath("$.plan").value("PREMIUM"))
                .andExpect(jsonPath("$.durationInMonths").value(12))
                .andExpect(jsonPath("$.maxMembers").value(100))
                .andExpect(jsonPath("$.monthlyPrice.amount").value(150.12))
                .andExpect(jsonPath("$.monthlyPrice.currency").value("PLN"));
        String jsonRequest2 = """
        {
            "name": "Basic Plan",
            "plan": "BASIC",
            "monthlyPrice": {
                "amount": 20.11,
                "currency": "GBP"
            },
            "durationInMonths": 50,
            "maxMembers": 10
        }
        """;
        mockMvc.perform(post("/api/gyms/" + gymId + "/membership-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest2))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Basic Plan"))
                .andExpect(jsonPath("$.plan").value("BASIC"))
                .andExpect(jsonPath("$.durationInMonths").value(50))
                .andExpect(jsonPath("$.maxMembers").value(10))
                .andExpect(jsonPath("$.monthlyPrice.amount").value(20.11))
                .andExpect(jsonPath("$.monthlyPrice.currency").value("GBP"));


        mockMvc.perform(get("/api/gyms/" + gymId + "/membership-plans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Pro Plan"))
                .andExpect(jsonPath("$[0].plan").value("PREMIUM"))
                .andExpect(jsonPath("$[0].durationInMonths").value(12))
                .andExpect(jsonPath("$[0].maxMembers").value(100))
                .andExpect(jsonPath("$[0].monthlyPrice.amount").value(150.12))
                .andExpect(jsonPath("$[0].monthlyPrice.currency").value("PLN"))
                .andExpect(jsonPath("$[1].name").value("Basic Plan"))
                .andExpect(jsonPath("$[1].plan").value("BASIC"))
                .andExpect(jsonPath("$[1].durationInMonths").value(50))
                .andExpect(jsonPath("$[1].maxMembers").value(10))
                .andExpect(jsonPath("$[1].monthlyPrice.amount").value(20.11))
                .andExpect(jsonPath("$[1].monthlyPrice.currency").value("GBP"));
    }
}