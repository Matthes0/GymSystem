package pl.matthes0.gym.membershipplan;

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
import pl.matthes0.gym.gym.dtos.GymDetailsDto;
import pl.matthes0.gym.membershipplan.dtos.MembershipPlanCreateDto;
import pl.matthes0.gym.membershipplan.dtos.MembershipPlanDetailsDto;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MembershipPlanController.class)
class MembershipPlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MembershipPlanService membershipPlanService;

    private static final Long GYM_ID = 1L;
    private static final String PLAN_NAME = "Standard Plan";
    private static final Price VALID_PRICE = new Price(new BigDecimal("99.99"), Currency.getInstance("PLN"));

    @Test
    void shouldCreateMembershipPlanSuccessfully() throws Exception {
        GymDetailsDto gymDto = new GymDetailsDto(GYM_ID, "Power House", "Street 1", "123456789");
        MembershipPlanCreateDto createDto = new MembershipPlanCreateDto(PLAN_NAME, Plan.BASIC, VALID_PRICE, 1, 50);
        MembershipPlanDetailsDto expectedDto = new MembershipPlanDetailsDto(10L, PLAN_NAME, Plan.BASIC, VALID_PRICE, 1, 50, gymDto);

        when(membershipPlanService.createMembershipPlan(eq(GYM_ID), any())).thenReturn(expectedDto);

        performPost(GYM_ID, createDto)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.monthlyPrice.amount").value(99.99))
                .andExpect(jsonPath("$.gymDetailsDto.id").value(GYM_ID));
    }

    @Test
    void shouldReturnAllPlansForGym() throws Exception {
        GymDetailsDto gymDto = new GymDetailsDto(GYM_ID, "Power House", "Street 1", "123456789");
        Price price = new Price(BigDecimal.ZERO, Currency.getInstance("GBP"));

        when(membershipPlanService.getAllMembershipPlans(GYM_ID)).thenReturn(List.of(
                new MembershipPlanDetailsDto(10L, "Basic", Plan.BASIC, price, 1, 10, gymDto)
        ));

        mockMvc.perform(get("/api/gyms/{id}/membership-plans", GYM_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].gymDetailsDto.name").value("Power House"));
    }

    @ParameterizedTest
    @MethodSource("invalidDtoProvider")
    void shouldReturn400ForInvalidData(MembershipPlanCreateDto invalidDto) throws Exception {
        performPost(GYM_ID, invalidDto).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenBadJsonFormats() throws Exception {
        List<String> invalidJsons = List.of(
                "{ \"plan\": \"ULTRA\" }",
                "{ \"durationInMonths\": \"twelve\" }",
                "{ \"monthlyPrice\": { \"currency\": \"EURO\" } }"
        );

        for (String json : invalidJsons) {
            mockMvc.perform(post("/api/gyms/{id}/membership-plans", GYM_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    void shouldReturn404WhenGymNotFound() throws Exception {
        when(membershipPlanService.getAllMembershipPlans(GYM_ID))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Gym not found"));

        mockMvc.perform(get("/api/gyms/{id}/membership-plans", GYM_ID))
                .andExpect(status().isNotFound());
    }


    private ResultActions performPost(Long gymId, Object dto) throws Exception {
        return mockMvc.perform(post("/api/gyms/{id}/membership-plans", gymId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
    }

    private static Stream<MembershipPlanCreateDto> invalidDtoProvider() {
        return Stream.of(
                new MembershipPlanCreateDto(null, Plan.BASIC, VALID_PRICE, 1, 10),
                new MembershipPlanCreateDto(PLAN_NAME, null, VALID_PRICE, 1, 10),
                new MembershipPlanCreateDto(PLAN_NAME, Plan.BASIC, null, 1, 10),
                new MembershipPlanCreateDto(PLAN_NAME, Plan.BASIC, VALID_PRICE, null, 10),
                new MembershipPlanCreateDto(PLAN_NAME, Plan.BASIC, VALID_PRICE, 1, null),
                new MembershipPlanCreateDto("a".repeat(101), Plan.BASIC, VALID_PRICE, 1, 10),
                new MembershipPlanCreateDto(PLAN_NAME, Plan.BASIC, new Price(new BigDecimal("-1"), Currency.getInstance("PLN")), 1, 10),
                new MembershipPlanCreateDto(PLAN_NAME, Plan.BASIC, new Price(new BigDecimal("0"), Currency.getInstance("PLN")), 1, 10),
                new MembershipPlanCreateDto(PLAN_NAME, Plan.BASIC, new Price(new BigDecimal("12.121"), Currency.getInstance("PLN")), 1, 10),
                new MembershipPlanCreateDto(PLAN_NAME, Plan.BASIC, VALID_PRICE, -5, 10),
                new MembershipPlanCreateDto(PLAN_NAME, Plan.BASIC, VALID_PRICE, 1, -1)
        );
    }
}