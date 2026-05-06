package pl.matthes0.gym.gym;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import pl.matthes0.gym.gym.dtos.GymCreateDto;
import pl.matthes0.gym.gym.dtos.GymDetailsDto;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GymController.class)
class GymControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private GymService gymService;

    private static final String BASE_URL = "/api/gyms";
    private static final String VALID_NAME = "Power House";
    private static final String VALID_ADDR = "Street 1";
    private static final String VALID_PHONE = "123456789";

    @Test
    void shouldCreateGymSuccessfully() throws Exception {
        GymCreateDto createDto = new GymCreateDto(VALID_NAME, VALID_ADDR, VALID_PHONE);
        GymDetailsDto expectedDto = new GymDetailsDto(1L, VALID_NAME, VALID_ADDR, VALID_PHONE);

        when(gymService.createGym(any())).thenReturn(expectedDto);

        performPost(createDto)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(VALID_NAME));
    }

    @Test
    void shouldReturnAllGyms() throws Exception {
        when(gymService.findAllGyms()).thenReturn(List.of(
                new GymDetailsDto(1L, "Gym A", "Addr A", "111"),
                new GymDetailsDto(2L, "Gym B", "Addr B", "222")
        ));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @ParameterizedTest
    @MethodSource("invalidGymProvider")
    void shouldReturn400ForInvalidData(GymCreateDto invalidDto) throws Exception {
        performPost(invalidDto).andExpect(status().isBadRequest());
    }


    private ResultActions performPost(Object dto) throws Exception {
        return mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
    }

    private static Stream<GymCreateDto> invalidGymProvider() {
        return Stream.of(
                new GymCreateDto(" ", VALID_ADDR, VALID_PHONE),
                new GymCreateDto(VALID_NAME, "", VALID_PHONE),
                new GymCreateDto(VALID_NAME, VALID_ADDR, ""),
                new GymCreateDto("a".repeat(101), VALID_ADDR, VALID_PHONE),
                new GymCreateDto(VALID_NAME, "a".repeat(256), VALID_PHONE),
                new GymCreateDto(VALID_NAME, VALID_ADDR, "1".repeat(25))
        );
    }
}
