package pl.matthes0.gym.member;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.server.ResponseStatusException;
import pl.matthes0.gym.member.dtos.MemberCreateDto;
import pl.matthes0.gym.member.dtos.MemberDetailsDto;
import pl.matthes0.gym.member.dtos.MemberSimpleDto;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private MemberService memberService;

    private static final Long PLAN_ID = 1L;
    private static final Long MEMBER_ID = 10L;
    private static final String NAME = "John Doe";
    private static final String EMAIL = "john.doe@example.com";

    @Test
    void shouldRegisterMemberSuccessfully() throws Exception {
        MemberCreateDto createDto = new MemberCreateDto(NAME, EMAIL);
        MemberDetailsDto expectedDto = new MemberDetailsDto(MEMBER_ID, NAME, EMAIL, null, Status.ACTIVE, null);

        when(memberService.registerNewMember(eq(PLAN_ID), any())).thenReturn(expectedDto);

        performPost(PLAN_ID, createDto)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(MEMBER_ID))
                .andExpect(jsonPath("$.fullName").value(NAME))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void shouldReturnAllMembers() throws Exception {
        when(memberService.getAllMembers()).thenReturn(List.of(
                new MemberSimpleDto(1L, "Member A", "a@test.com", null, Status.ACTIVE, "Plan A", "Gym A"),
                new MemberSimpleDto(2L, "Member B", "b@test.com", null, Status.CANCELLED, "Plan B", "Gym B")
        ));

        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[1].status").value("CANCELLED"));
    }

    @Test
    void shouldCancelMembershipSuccessfully() throws Exception {
        MemberDetailsDto cancelledDto = new MemberDetailsDto(MEMBER_ID, NAME, EMAIL, null, Status.CANCELLED, null);
        when(memberService.cancelMembership(MEMBER_ID)).thenReturn(cancelledDto);

        mockMvc.perform(patch("/api/members/{id}/cancel", MEMBER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"))
                .andExpect(jsonPath("$.fullName").value(NAME));
    }

    @ParameterizedTest
    @MethodSource("invalidMemberProvider")
    void shouldReturn400ForInvalidData(MemberCreateDto invalidDto) throws Exception {
        performPost(PLAN_ID, invalidDto).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenPlanNotFound() throws Exception {
        when(memberService.registerNewMember(eq(PLAN_ID), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found"));

        performPost(PLAN_ID, new MemberCreateDto(NAME, EMAIL))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenPlanIsFull() throws Exception {
        when(memberService.registerNewMember(eq(PLAN_ID), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Plan is full"));

        performPost(PLAN_ID, new MemberCreateDto(NAME, EMAIL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenMemberToCancelNotFound() throws Exception {
        when(memberService.cancelMembership(MEMBER_ID))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));

        mockMvc.perform(patch("/api/members/{id}/cancel", MEMBER_ID))
                .andExpect(status().isNotFound());
    }


    private ResultActions performPost(Long planId, Object dto) throws Exception {
        return mockMvc.perform(post("/api/membership-plans/{id}/members", planId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
    }

    private static Stream<MemberCreateDto> invalidMemberProvider() {
        return Stream.of(
                new MemberCreateDto("", EMAIL),
                new MemberCreateDto(NAME, "not-an-email"),
                new MemberCreateDto("a".repeat(101), EMAIL)
        );
    }
}