package pl.matthes0.gym.member;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import pl.matthes0.gym.gym.dtos.GymCreateDto;
import pl.matthes0.gym.member.dtos.MemberCreateDto;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MemberIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private static final String GYMS_URL = "/api/gyms";
    private static final String MEMBERS_URL = "/api/members";
    private static final String TODAY = LocalDate.now().toString();

    @Test
    void shouldRegisterMembersAndReturnAllMembers() throws Exception {
        long gymId = createGym("Power Gym", "Central 1", "123456");
        long planId = createPlan(gymId, "Pro Plan", "PREMIUM", 150.12, "PLN", 100);

        performPost("/api/membership-plans/" + planId + "/members", new MemberCreateDto("John Doe", "john.doe@example.com"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.membershipStartDate").value(TODAY))
                .andExpect(jsonPath("$.membershipPlanDetailsDto.name").value("Pro Plan"));

        performPost("/api/membership-plans/" + planId + "/members", new MemberCreateDto("Mariusz Pudzianowski", "mariuszpudzianowski@example.com"))
                .andExpect(status().isCreated());

        mockMvc.perform(get(MEMBERS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].fullName").value("John Doe"))
                .andExpect(jsonPath("$[0].gymName").value("Power Gym"))
                .andExpect(jsonPath("$[1].fullName").value("Mariusz Pudzianowski"));
    }

    @Test
    void shouldRegisterAndThenCancelMember() throws Exception {
        long gymId = createGym("Power Gym", "Central 1", "123456");
        long planId = createPlan(gymId, "Pro Plan", "PREMIUM", 150.12, "PLN", 100);
        long memberId = extractId(performPost("/api/membership-plans/" + planId + "/members", new MemberCreateDto("John Doe", "john.doe@example.com")));

        mockMvc.perform(patch(MEMBERS_URL + "/" + memberId + "/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        mockMvc.perform(get(MEMBERS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("CANCELLED"))
                .andExpect(jsonPath("$[0].planName").value("Pro Plan"));
    }

    @Test
    void shouldAllowAddingNewMemberAfterAnotherOneCancelledWhenPlanWasFull() throws Exception {
        long gymId = createGym("Limit Gym", "Full Street 5", "999888777");
        long planId = createPlan(gymId, "Solo Plan", "BASIC", 50.00, "PLN", 1);

        long firstMemberId = extractId(performPost("/api/membership-plans/" + planId + "/members", new MemberCreateDto("First Member", "first@test.com")));

        performPost("/api/membership-plans/" + planId + "/members", new MemberCreateDto("Second Member", "second@test.com"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(patch(MEMBERS_URL + "/" + firstMemberId + "/cancel")).andExpect(status().isOk());

        performPost("/api/membership-plans/" + planId + "/members", new MemberCreateDto("Second Member", "second@test.com"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("Second Member"));

        mockMvc.perform(get(MEMBERS_URL)).andExpect(jsonPath("$.length()").value(2));
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
        return extractId(performPost(GYMS_URL, new GymCreateDto(name, address, phone)).andExpect(status().isCreated()));
    }

    private long createPlan(long gymId, String name, String type, double amount, String currency, int maxMembers) throws Exception {
        Map<String, Object> planData = Map.of(
                "name", name,
                "plan", type,
                "monthlyPrice", Map.of("amount", amount, "currency", currency),
                "durationInMonths", 12,
                "maxMembers", maxMembers
        );
        return extractId(performPost(GYMS_URL + "/" + gymId + "/membership-plans", planData).andExpect(status().isCreated()));
    }
}