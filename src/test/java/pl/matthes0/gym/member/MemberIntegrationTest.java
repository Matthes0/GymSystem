package pl.matthes0.gym.member;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import pl.matthes0.gym.gym.dtos.GymCreateDto;
import pl.matthes0.gym.member.dtos.MemberCreateDto;
import tools.jackson.databind.ObjectMapper;

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

    @Test
    void shouldRegisterMembersAndReturnAllMembers() throws Exception {
        GymCreateDto gymDto = new GymCreateDto("Power Gym", "Central 1", "123456");
        MvcResult gymResult = mockMvc.perform(post("/api/gyms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gymDto)))
                .andExpect(status().isCreated())
                .andReturn();
        String response = gymResult.getResponse().getContentAsString();
        Long gymId = ((Number) com.jayway.jsonpath.JsonPath.read(response, "$.id")).longValue();
        String planJson = """
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

        MvcResult planResult = mockMvc.perform(post("/api/gyms/" + gymId + "/membership-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(planJson))
                .andExpect(status().isCreated())
                .andReturn();
        Long planId = ((Number) com.jayway.jsonpath.JsonPath.read(planResult.getResponse().getContentAsString(), "$.id")).longValue();

        String expectedDate = java.time.LocalDate.now().toString();

        MemberCreateDto memberDto = new MemberCreateDto("John Doe", "john.doe@example.com");

        mockMvc.perform(post("/api/membership-plans/" + planId + "/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.membershipStartDate").value(expectedDate))
                .andExpect(jsonPath("$.membershipPlanDetailsDto.name").value("Pro Plan"))
                .andExpect(jsonPath("$.membershipPlanDetailsDto.plan").value("PREMIUM"));

        MemberCreateDto memberDto2 = new MemberCreateDto("Mariusz Pudzianowski", "mariuszpudzianowski@example.com");

        mockMvc.perform(post("/api/membership-plans/" + planId + "/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberDto2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("Mariusz Pudzianowski"))
                .andExpect(jsonPath("$.email").value("mariuszpudzianowski@example.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.membershipStartDate").value(expectedDate))
                .andExpect(jsonPath("$.membershipPlanDetailsDto.name").value("Pro Plan"))
                .andExpect(jsonPath("$.membershipPlanDetailsDto.plan").value("PREMIUM"));

        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("John Doe"))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[0].membershipStartDate").value(expectedDate))
                .andExpect(jsonPath("$[0].planName").value("Pro Plan"))
                .andExpect(jsonPath("$[0].gymName").value("Power Gym"))
                .andExpect(jsonPath("$[1].fullName").value("Mariusz Pudzianowski"))
                .andExpect(jsonPath("$[1].email").value("mariuszpudzianowski@example.com"))
                .andExpect(jsonPath("$[1].status").value("ACTIVE"))
                .andExpect(jsonPath("$[1].membershipStartDate").value(expectedDate))
                .andExpect(jsonPath("$[1].planName").value("Pro Plan"))
                .andExpect(jsonPath("$[1].gymName").value("Power Gym"));
    }

    @Test
    void shouldRegisterAndThenCancelMember() throws Exception {
        GymCreateDto gymDto = new GymCreateDto("Power Gym", "Central 1", "123456");
        MvcResult gymResult = mockMvc.perform(post("/api/gyms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gymDto)))
                .andExpect(status().isCreated())
                .andReturn();
        String response = gymResult.getResponse().getContentAsString();
        Long gymId = ((Number) com.jayway.jsonpath.JsonPath.read(response, "$.id")).longValue();
        String planJson = """
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

        MvcResult planResult = mockMvc.perform(post("/api/gyms/" + gymId + "/membership-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(planJson))
                .andExpect(status().isCreated())
                .andReturn();
        Long planId = ((Number) com.jayway.jsonpath.JsonPath.read(planResult.getResponse().getContentAsString(), "$.id")).longValue();

        MemberCreateDto memberDto = new MemberCreateDto("John Doe", "john.doe@example.com");

        MvcResult memberResult = mockMvc.perform(post("/api/membership-plans/" + planId + "/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andReturn();
        Long memberId = ((Number) com.jayway.jsonpath.JsonPath.read(memberResult.getResponse().getContentAsString(), "$.id")).longValue();

        mockMvc.perform(patch("/api/members/" + memberId + "/cancel")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("John Doe"))
                .andExpect(jsonPath("$.status").value("CANCELLED"));
        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("John Doe"))
                .andExpect(jsonPath("$[0].status").value("CANCELLED"))
                .andExpect(jsonPath("$[0].planName").value("Pro Plan"))
                .andExpect(jsonPath("$[0].gymName").value("Power Gym"));
    }
    @Test
    void shouldAllowAddingNewMemberAfterAnotherOneCancelledWhenPlanWasFull() throws Exception {
        GymCreateDto gymDto = new GymCreateDto("Limit Gym", "Full Street 5", "999888777");
        MvcResult gymResult = mockMvc.perform(post("/api/gyms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gymDto)))
                .andExpect(status().isCreated())
                .andReturn();
        Long gymId = ((Number) com.jayway.jsonpath.JsonPath.read(gymResult.getResponse().getContentAsString(), "$.id")).longValue();

        String planJson = """
        {
            "name": "Solo Plan",
            "plan": "BASIC",
            "monthlyPrice": { "amount": 50.00, "currency": "PLN" },
            "durationInMonths": 1,
            "maxMembers": 1
        }
        """;

        MvcResult planResult = mockMvc.perform(post("/api/gyms/" + gymId + "/membership-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(planJson))
                .andExpect(status().isCreated())
                .andReturn();
        Long planId = ((Number) com.jayway.jsonpath.JsonPath.read(planResult.getResponse().getContentAsString(), "$.id")).longValue();

        MemberCreateDto member1Dto = new MemberCreateDto("First Member", "first@test.com");
        MvcResult member1Result = mockMvc.perform(post("/api/membership-plans/" + planId + "/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member1Dto)))
                .andExpect(status().isCreated())
                .andReturn();
        Long member1Id = ((Number) com.jayway.jsonpath.JsonPath.read(member1Result.getResponse().getContentAsString(), "$.id")).longValue();

        MemberCreateDto member2Dto = new MemberCreateDto("Second Member", "second@test.com");
        mockMvc.perform(post("/api/membership-plans/" + planId + "/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member2Dto)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(patch("/api/members/" + member1Id + "/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        mockMvc.perform(post("/api/membership-plans/" + planId + "/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member2Dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("Second Member"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}