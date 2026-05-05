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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    void shouldRegisterMemberAndReturnAllMembers() throws Exception {
        GymCreateDto gymDto = new GymCreateDto("Power Gym", "Central 1", "123456");
        MvcResult gymResult = mockMvc.perform(post("/api/gyms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gymDto)))
                .andExpect(status().isCreated())
                .andReturn();
        String response = gymResult.getResponse().getContentAsString();
        Integer gymId = com.jayway.jsonpath.JsonPath.read(response, "$.id");
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
        Integer planId = com.jayway.jsonpath.JsonPath.read(planResult.getResponse().getContentAsString(), "$.id");

        MemberCreateDto memberDto = new MemberCreateDto("John Doe", "john.doe@example.com");

        mockMvc.perform(post("/api/membership-plans/" + planId + "/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        MemberCreateDto memberDto2 = new MemberCreateDto("Mariusz Pudzianowski", "mariuszpudzianowski@example.com");

        mockMvc.perform(post("/api/membership-plans/" + planId + "/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberDto2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("Mariusz Pudzianowski"))
                .andExpect(jsonPath("$.email").value("mariuszpudzianowski@example.com"));

        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("John Doe"))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$[1].fullName").value("Mariusz Pudzianowski"))
                .andExpect(jsonPath("$[1].email").value("mariuszpudzianowski@example.com"));
    }
    // TODO: ADD MORE CHECKS
}