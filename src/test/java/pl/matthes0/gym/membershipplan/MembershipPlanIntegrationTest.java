package pl.matthes0.gym.membershipplan;

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

    private static final String GYMS_URL = "/api/gyms";

    @Test
    void shouldCreateAndReturnMembershipPlans() throws Exception {
        long gymId = createGym("Power Gym", "Central 1", "123456");

        createPlan(gymId, "Pro Plan", "PREMIUM", 150.12, "PLN", 12, 100)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Pro Plan"))
                .andExpect(jsonPath("$.monthlyPrice.amount").value(150.12));

        createPlan(gymId, "Basic Plan", "BASIC", 20.11, "GBP", 50, 10)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Basic Plan"))
                .andExpect(jsonPath("$.monthlyPrice.currency").value("GBP"));

        mockMvc.perform(get(GYMS_URL + "/" + gymId + "/membership-plans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Pro Plan"))
                .andExpect(jsonPath("$[1].name").value("Basic Plan"));
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

    private long createGym(String name, String address, String phone) throws Exception {
        GymCreateDto gymDto = new GymCreateDto(name, address, phone);
        return extractId(performPost(GYMS_URL, gymDto).andExpect(status().isCreated()));
    }

    private ResultActions createPlan(long gymId, String name, String type, double amount, String currency, int duration, int maxMembers) throws Exception {
        Map<String, Object> planData = Map.of(
                "name", name,
                "plan", type,
                "monthlyPrice", Map.of("amount", amount, "currency", currency),
                "durationInMonths", duration,
                "maxMembers", maxMembers
        );
        return performPost(GYMS_URL + "/" + gymId + "/membership-plans", planData);
    }
}