package pl.matthes0.gym.gym;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import pl.matthes0.gym.gym.dtos.GymCreateDto;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

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
    private ObjectMapper objectMapper;

    private static final String GYMS_URL = "/api/gyms";
    private static final String REVENUE_URL = "/api/gyms/revenue";

    @Test
    void shouldCreateAndReturnGyms() throws Exception {
        GymCreateDto dto1 = new GymCreateDto("Something", "Street 123", "123456789");
        GymCreateDto dto2 = new GymCreateDto("The other gym", "Street 321", "987654321");

        performPost(GYMS_URL, dto1).andExpect(status().isCreated());
        performPost(GYMS_URL, dto2).andExpect(status().isCreated());

        mockMvc.perform(get(GYMS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Something"))
                .andExpect(jsonPath("$[1].name").value("The other gym"));
    }

    @Test
    void shouldCalculateRevenueCorrectyForMultipleGymsAndCurrencies() throws Exception {
        long gym1Id = extractId(performPost(GYMS_URL, new GymCreateDto("Big Muscle Gym", "Main St 1", "111")));

        long plan1Id = createPlan(gym1Id, "PLN", 123.45, "BASIC");
        registerMember(plan1Id, "User 1", "u1@test.com");
        registerMember(plan1Id, "User 2", "u2@test.com");

        long plan2Id = createPlan(gym1Id, "PLN", 50.11, "PREMIUM");
        registerMember(plan2Id, "User 3", "u3@test.com");

        long plan3Id = createPlan(gym1Id, "EUR", 20.99, "GROUP");
        registerMember(plan3Id, "User 4", "u4@test.com");

        long gym2Id = extractId(performPost(GYMS_URL, new GymCreateDto("Empty Gym", "Quiet St 2", "222")));
        createPlan(gym2Id, "PLN", 500.00, "BASIC");

        mockMvc.perform(get(REVENUE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.gymName == 'Big Muscle Gym' && @.revenue.currency == 'PLN')].revenue.amount").value(297.01))
                .andExpect(jsonPath("$[?(@.gymName == 'Big Muscle Gym' && @.revenue.currency == 'EUR')].revenue.amount").value(20.99))
                .andExpect(jsonPath("$[?(@.gymName == 'Empty Gym')]").isEmpty());
    }

    @Test
    void shouldExcludeCancelledMembersFromRevenue() throws Exception {
        long gymId = extractId(performPost(GYMS_URL, new GymCreateDto("Calculated Gym", "Test Street", "000")));
        long planId = createPlan(gymId, "PLN", 100.49, "BASIC");

        registerMember(planId, "User 1", "u1@test.com");
        long memberToCancelId = registerMember(planId, "User 2", "u2@test.com");

        mockMvc.perform(patch("/api/members/" + memberToCancelId + "/cancel")).andExpect(status().isOk());

        mockMvc.perform(get(REVENUE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.revenue.currency == 'PLN')].revenue.amount").value(100.49));
    }

    private ResultActions performPost(String url, Object body) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    private long extractId(ResultActions actions) throws Exception {
        String response = actions.andReturn().getResponse().getContentAsString();
        return ((Number) com.jayway.jsonpath.JsonPath.read(response, "$.id")).longValue();
    }

    private long createPlan(long gymId, String currency, double amount, String type) throws Exception {
        Map<String, Object> planData = Map.of(
                "name", "Plan " + type,
                "plan", type,
                "monthlyPrice", Map.of("amount", amount, "currency", currency),
                "durationInMonths", 1,
                "maxMembers", 100
        );
        return extractId(performPost(GYMS_URL + "/" + gymId + "/membership-plans", planData));
    }

    private long registerMember(long planId, String name, String email) throws Exception {
        Map<String, String> memberData = Map.of("fullName", name, "email", email);
        return extractId(performPost("/api/membership-plans/" + planId + "/members", memberData));
    }
}