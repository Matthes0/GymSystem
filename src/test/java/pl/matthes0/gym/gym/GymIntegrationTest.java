package pl.matthes0.gym.gym;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GymIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;


    @Test
    void shouldCreateAndReturnGyms() throws Exception {
        GymCreateDto dto1 = new GymCreateDto("Something", "Street 123", "123456789");
        mockMvc.perform(post("/api/gyms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Something"))
                .andExpect(jsonPath("$.address").value("Street 123"))
                .andExpect(jsonPath("$.phoneNumber").value("123456789"));

        GymCreateDto dto2 = new GymCreateDto("The other gym", "Street 321", "987654321");

        mockMvc.perform(post("/api/gyms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("The other gym"));

        mockMvc.perform(get("/api/gyms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Something"))
                .andExpect(jsonPath("$[0].address").value("Street 123"))
                .andExpect(jsonPath("$[0].phoneNumber").value("123456789"))
                .andExpect(jsonPath("$[1].name").value("The other gym"))
                .andExpect(jsonPath("$[1].address").value("Street 321"))
                .andExpect(jsonPath("$[1].phoneNumber").value("987654321"));
    }

    @Test
    void shouldCalculateRevenueCorrectyForMultipleGymsAndCurrencies() throws Exception {
        GymCreateDto gym1Dto = new GymCreateDto("Big Muscle Gym", "Main St 1", "111");
        MvcResult gym1Result = mockMvc.perform(post("/api/gyms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gym1Dto))).andExpect(status().isCreated()).andReturn();
        Long gym1Id = ((Number) com.jayway.jsonpath.JsonPath.read(gym1Result.getResponse().getContentAsString(), "$.id")).longValue();

        Long plan1Id = createPlan(gym1Id, "PLN", 123.45, "BASIC");
        registerMember(plan1Id, "User 1", "u1@test.com");
        registerMember(plan1Id, "User 2", "u2@test.com");

        Long plan2Id = createPlan(gym1Id, "PLN", 50.11, "PREMIUM");
        registerMember(plan2Id, "User 3", "u3@test.com");

        Long plan3Id = createPlan(gym1Id, "EUR", 20.99, "GROUP");
        registerMember(plan3Id, "User 4", "u4@test.com");

        GymCreateDto gym2Dto = new GymCreateDto("Empty Gym", "Quiet St 2", "222");
        MvcResult gym2Result = mockMvc.perform(post("/api/gyms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gym2Dto))).andExpect(status().isCreated()).andReturn();
        Long gym2Id = ((Number) com.jayway.jsonpath.JsonPath.read(gym2Result.getResponse().getContentAsString(), "$.id")).longValue();

        createPlan(gym2Id, "PLN", 500.00, "BASIC");

        // Gym 1 PLN: (123.45 * 2) + 50.11 = 246.90 + 50.11 = 297.01
        // Gym 1 EUR: 20.99
        // Gym 2 Not exists

        mockMvc.perform(get("/api/gyms/revenue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.gymName == 'Big Muscle Gym' && @.revenue.currency == 'PLN')].revenue.amount").value(297.01))
                .andExpect(jsonPath("$[?(@.gymName == 'Big Muscle Gym' && @.revenue.currency == 'EUR')].revenue.amount").value(20.99))
                .andExpect(jsonPath("$[?(@.gymName == 'Empty Gym')]").isEmpty());
    }

    @Test
    void shouldExcludeCancelledMembersFromRevenue() throws Exception {
        GymCreateDto gymDto = new GymCreateDto("Calculated Gym", "Test Street", "000");
        MvcResult gymResult = mockMvc.perform(post("/api/gyms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gymDto)))
                .andExpect(status().isCreated())
                .andReturn();
        Long gymId = ((Number) com.jayway.jsonpath.JsonPath.read(gymResult.getResponse().getContentAsString(), "$.id")).longValue();

        Long planId = createPlan(gymId, "PLN", 100.49, "BASIC");
        registerMember(planId, "User 1", "u1@test.com");
        Long memberToCancelId = registerMember(planId, "User 2", "u2@test.com");
        mockMvc.perform(get("/api/gyms/revenue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.gymName == 'Calculated Gym' && @.revenue.currency == 'PLN')].revenue.amount").value(200.98));
        mockMvc.perform(patch("/api/members/" + memberToCancelId + "/cancel")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/gyms/revenue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.gymName == 'Calculated Gym' && @.revenue.currency == 'PLN')].revenue.amount").value(100.49));
    }

    private Long createPlan(Long gymId, String currency, double amount, String type) throws Exception {
        String json = """
                {
                    "name": "Plan %s",
                    "plan": "%s",
                    "monthlyPrice": { "amount": %s, "currency": "%s" },
                    "durationInMonths": 1, "maxMembers": 100
                }
                """.formatted(type, type, amount, currency);

        MvcResult res = mockMvc.perform(post("/api/gyms/" + gymId + "/membership-plans")
                .contentType(MediaType.APPLICATION_JSON).content(json)).andReturn();
        return ((Number) com.jayway.jsonpath.JsonPath.read(res.getResponse().getContentAsString(), "$.id")).longValue();
    }

    private Long registerMember(Long planId, String name, String email) throws Exception {
        String json = """
                { "fullName": "%s", "email": "%s" }
                """.formatted(name, email);
        MvcResult res = mockMvc.perform(post("/api/membership-plans/" + planId + "/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        return ((Number) com.jayway.jsonpath.JsonPath.read(res.getResponse().getContentAsString(), "$.id")).longValue();
    }
}
