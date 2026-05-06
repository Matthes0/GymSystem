package pl.matthes0.gym.member;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import pl.matthes0.gym.member.dtos.MemberCreateDto;
import pl.matthes0.gym.member.dtos.MemberDetailsDto;
import pl.matthes0.gym.member.dtos.MemberSimpleDto;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

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

    @Test
    void shouldRegisterMemberSuccessfully() throws Exception {
        Long planId = 1L;
        MemberCreateDto createDto = new MemberCreateDto("John Doe", "john.doe@example.com");
        MemberDetailsDto expectedDto = new MemberDetailsDto(10L, "John Doe", "john.doe@example.com", null, Status.ACTIVE, null);

        when(memberService.registerNewMember(eq(planId), any(MemberCreateDto.class))).thenReturn(expectedDto);

        mockMvc.perform(post("/api/membership-plans/" + planId + "/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.fullName").value("John Doe"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void shouldReturnAllMembers() throws Exception {
        List<MemberSimpleDto> members = List.of(
                new MemberSimpleDto(1L, "Member A", "a@test.com", null, Status.ACTIVE, "Plan A", "Gym A"),
                new MemberSimpleDto(2L, "Member B", "b@test.com", null, Status.CANCELLED, "Plan B", "Gym B")
        );

        when(memberService.getAllMembers()).thenReturn(members);

        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[1].status").value("CANCELLED"));
    }

    @Test
    void shouldCancelMembershipSuccessfully() throws Exception {
        Long memberId = 1L;
        MemberDetailsDto cancelledDto = new MemberDetailsDto(memberId, "John Doe", "john@test.com", null, Status.CANCELLED, null);

        when(memberService.cancelMembership(memberId)).thenReturn(cancelledDto);

        mockMvc.perform(patch("/api/members/" + memberId + "/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"))
                .andExpect(jsonPath("$.fullName").value("John Doe"));
    }

    @Test
    void shouldReturn400WhenNameIsBlank() throws Exception {
        MemberCreateDto invalidDto = new MemberCreateDto("", "email@test.com");
        performPostAndExpect400(invalidDto);
    }

    @Test
    void shouldReturn400WhenEmailIsInvalid() throws Exception {
        MemberCreateDto invalidDto = new MemberCreateDto("John Doe", "not-an-email");
        performPostAndExpect400(invalidDto);
    }

    @Test
    void shouldReturn400WhenNameIsTooLong() throws Exception {
        MemberCreateDto invalidDto = new MemberCreateDto("a".repeat(101), "email@test.com");
        performPostAndExpect400(invalidDto);
    }

    @Test
    void shouldReturn404WhenPlanNotFound() throws Exception {
        Long planId = 999L;
        when(memberService.registerNewMember(eq(planId), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found"));

        mockMvc.perform(post("/api/membership-plans/" + planId + "/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MemberCreateDto("Name", "test@test.com"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenPlanIsFull() throws Exception {
        Long planId = 1L;
        when(memberService.registerNewMember(eq(planId), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Plan is full"));

        mockMvc.perform(post("/api/membership-plans/" + planId + "/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MemberCreateDto("Name", "test@test.com"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenMemberToCancelNotFound() throws Exception {
        Long memberId = 999L;
        when(memberService.cancelMembership(memberId))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));

        mockMvc.perform(patch("/api/members/" + memberId + "/cancel"))
                .andExpect(status().isNotFound());
    }

    private void performPostAndExpect400(MemberCreateDto dto) throws Exception {
        mockMvc.perform(post("/api/membership-plans/1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}