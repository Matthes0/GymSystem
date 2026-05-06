package pl.matthes0.gym.membershipplan;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import pl.matthes0.gym.gym.dtos.GymDetailsDto;
import pl.matthes0.gym.membershipplan.dtos.MembershipPlanCreateDto;
import pl.matthes0.gym.membershipplan.dtos.MembershipPlanDetailsDto;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

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

    @Test
    void shouldCreateMembershipPlanSuccessfully() throws Exception {
        Long gymId = 1L;
        GymDetailsDto gymDto = new GymDetailsDto(gymId, "Power House", "Street 1", "123456789");
        Price price = new Price(new BigDecimal("99.99"), Currency.getInstance("PLN"));
        MembershipPlanCreateDto createDto = new MembershipPlanCreateDto(
                "Standard Plan", Plan.BASIC, price, 1, 50);

        MembershipPlanDetailsDto expectedDto = new MembershipPlanDetailsDto(10L, "Standard Plan", Plan.BASIC, price, 1, 50, gymDto);

        when(membershipPlanService.createMembershipPlan(eq(gymId), any(MembershipPlanCreateDto.class)))
                .thenReturn(expectedDto);
        mockMvc.perform(post("/api/gyms/" + gymId + "/membership-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Standard Plan"))
                .andExpect(jsonPath("$.monthlyPrice.amount").value(99.99))
                .andExpect(jsonPath("$.gymDetailsDto.id").value(gymId))
                .andExpect(jsonPath("$.gymDetailsDto.name").value("Power House"));
    }

    @Test
    void shouldReturnAllPlansForGym() throws Exception {
        Long gymId = 1L;
        GymDetailsDto gymDto = new GymDetailsDto(gymId, "Power House", "Street 1", "123456789");
        Price price = new Price(BigDecimal.ZERO, Currency.getInstance("GBP"));

        when(membershipPlanService.getAllMembershipPlans(gymId)).thenReturn(List.of(
                new MembershipPlanDetailsDto(10L, "Basic", Plan.BASIC, price, 1, 10, gymDto)
        ));
        mockMvc.perform(get("/api/gyms/" + gymId + "/membership-plans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Basic"))
                .andExpect(jsonPath("$[0].gymDetailsDto.name").value("Power House"));
    }

    @Test
    void shouldReturn400WhenNameIsNull() throws Exception {
        MembershipPlanCreateDto dto = new MembershipPlanCreateDto(
                null, Plan.BASIC, new Price(new BigDecimal("10"), Currency.getInstance("PLN")), 1, 10);
        performPostAndExpect400(dto);
    }

    @Test
    void shouldReturn400WhenPlanIsNull() throws Exception {
        MembershipPlanCreateDto dto = new MembershipPlanCreateDto(
                "Name", null, new Price(new BigDecimal("10"), Currency.getInstance("PLN")), 1, 10);
        performPostAndExpect400(dto);
    }

    @Test
    void shouldReturn400WhenPriceIsNull() throws Exception {
        MembershipPlanCreateDto dto = new MembershipPlanCreateDto(
                "Name", Plan.BASIC, null, 1, 10);
        performPostAndExpect400(dto);
    }

    @Test
    void shouldReturn400WhenDurationIsNull() throws Exception {
        MembershipPlanCreateDto dto = new MembershipPlanCreateDto(
                "Name", Plan.BASIC, new Price(new BigDecimal("10"), Currency.getInstance("PLN")), null, 10);
        performPostAndExpect400(dto);
    }

    @Test
    void shouldReturn400WhenMaxMembersIsNull() throws Exception {
        MembershipPlanCreateDto dto = new MembershipPlanCreateDto(
                "Name", Plan.BASIC, new Price(new BigDecimal("10"), Currency.getInstance("PLN")), 1, null);
        performPostAndExpect400(dto);
    }


    @Test
    void shouldReturn400WhenAmountHasTooMuchPrecision() throws Exception {
        Price price = new Price(new BigDecimal("12.121"), Currency.getInstance("EUR"));
        MembershipPlanCreateDto dto = new MembershipPlanCreateDto("Plan", Plan.BASIC, price, 1, 10);
        mockMvc.perform(post("/api/gyms/1/membership-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenAmountIsNegative() throws Exception {
        Price minusPrice = new Price(new BigDecimal("-1"), Currency.getInstance("PLN"));
        MembershipPlanCreateDto dto = new MembershipPlanCreateDto("Plan", Plan.BASIC, minusPrice, 1, 10);

        mockMvc.perform(post("/api/gyms/1/membership-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenAmountIsTooLong() throws Exception {
        Price longPrice = new Price(new BigDecimal("100000000"), Currency.getInstance("PLN"));
        MembershipPlanCreateDto dto = new MembershipPlanCreateDto("Plan", Plan.BASIC, longPrice, 1, 10);

        mockMvc.perform(post("/api/gyms/1/membership-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenAmountIsZero() throws Exception {
        Price zeroPrice = new Price(new BigDecimal("0"), Currency.getInstance("PLN"));
        MembershipPlanCreateDto dto = new MembershipPlanCreateDto("Plan", Plan.BASIC, zeroPrice, 1, 10);

        mockMvc.perform(post("/api/gyms/1/membership-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldAcceptWholeNumberAmount() throws Exception {
        Price price = new Price(new BigDecimal("16"), Currency.getInstance("USD"));
        MembershipPlanCreateDto dto = new MembershipPlanCreateDto("Plan", Plan.BASIC, price, 1, 10);

        when(membershipPlanService.createMembershipPlan(any(), any())).thenReturn(null);

        mockMvc.perform(post("/api/gyms/1/membership-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturn400WhenBadCurrencyFormat() throws Exception {
        String jsonWithInvalidCurrency = """
                {
                    "name": "Standard Plan",
                    "plan": "BASIC",
                    "monthlyPrice": {
                        "amount": 100.00,
                        "currency": "EURO"
                    },
                    "durationInMonths": 1,
                    "maxMembers": 50
                }
                """;

        mockMvc.perform(post("/api/gyms/1/membership-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithInvalidCurrency))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenNameIsTooLong() throws Exception {
        String longName = "a".repeat(101);
        MembershipPlanCreateDto dto = new MembershipPlanCreateDto(
                longName, Plan.BASIC, new Price(new BigDecimal("10"), Currency.getInstance("PLN")), 1, 10);
        performPostAndExpect400(dto);
    }

    @Test
    void shouldReturn400WhenDurationIsNegative() throws Exception {
        MembershipPlanCreateDto dto = new MembershipPlanCreateDto(
                "Plan", Plan.BASIC, new Price(new BigDecimal("10"), Currency.getInstance("PLN")), -5, 10);
        performPostAndExpect400(dto);
    }

    @Test
    void shouldReturn400WhenDurationIsZero() throws Exception {
        MembershipPlanCreateDto dto = new MembershipPlanCreateDto(
                "Plan", Plan.BASIC, new Price(new BigDecimal("10"), Currency.getInstance("PLN")), 0, 10);
        performPostAndExpect400(dto);
    }

    @Test
    void shouldReturn400WhenMaxMembersIsNegative() throws Exception {
        MembershipPlanCreateDto dto = new MembershipPlanCreateDto(
                "Plan", Plan.BASIC, new Price(new BigDecimal("10"), Currency.getInstance("PLN")), 1, -1);
        performPostAndExpect400(dto);
    }

    @Test
    void shouldReturn400WhenMaxMembersIsZero() throws Exception {
        MembershipPlanCreateDto dto = new MembershipPlanCreateDto(
                "Plan", Plan.BASIC, new Price(new BigDecimal("10"), Currency.getInstance("PLN")), 1, 0);
        performPostAndExpect400(dto);
    }

    @Test
    void shouldReturn400WhenDurationHasWrongType() throws Exception {
        String jsonWithInvalidType = """
                {
                    "name": "Pro Plan",
                    "plan": "BASIC",
                    "monthlyPrice": {
                        "amount": 100.00,
                        "currency": "PLN"
                    },
                    "durationInMonths": "twelve",
                    "maxMembers": 50
                }
                """;
        mockMvc.perform(post("/api/gyms/1/membership-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithInvalidType))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenPlanEnumIsInvalid() throws Exception {
        String jsonWithInvalidEnum = """
                {
                    "name": "Pro Plan",
                    "plan": "ULTRA",
                    "monthlyPrice": {
                        "amount": 100.00,
                        "currency": "PLN"
                    },
                    "durationInMonths": 12,
                    "maxMembers": 50
                }
                """;

        mockMvc.perform(post("/api/gyms/1/membership-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithInvalidEnum))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenGettingPlansForNonExistentGym() throws Exception {
        Long nonExistentGymId = 999L;
        when(membershipPlanService.getAllMembershipPlans(nonExistentGymId))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Gym with id 999 not found"));

        mockMvc.perform(get("/api/gyms/" + nonExistentGymId + "/membership-plans"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenCreatingPlanForNonExistentGym() throws Exception {
        Long nonExistentGymId = 999L;
        Price price = new Price(new BigDecimal("100"), Currency.getInstance("PLN"));
        MembershipPlanCreateDto dto = new MembershipPlanCreateDto(
                "Plan", Plan.BASIC, price, 1, 10);

        when(membershipPlanService.createMembershipPlan(eq(nonExistentGymId), any(MembershipPlanCreateDto.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Gym with id 999 not found"));

        mockMvc.perform(post("/api/gyms/" + nonExistentGymId + "/membership-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    private void performPostAndExpect400(MembershipPlanCreateDto dto) throws Exception {
        mockMvc.perform(post("/api/gyms/1/membership-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

}