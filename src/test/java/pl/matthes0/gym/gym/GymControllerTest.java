package pl.matthes0.gym.gym;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.matthes0.gym.gym.dtos.GymCreateDto;
import pl.matthes0.gym.gym.dtos.GymDetailsDto;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GymController.class)
public class GymControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GymService gymService;

    @Test
    void shouldCreateGymSuccessfully() throws Exception {
        GymCreateDto createDto = new GymCreateDto("Power House", "Street 1", "123456789");
        GymDetailsDto expectedDto = new GymDetailsDto(1L, "Power House", "Street 1", "123456789");

        when(gymService.createGym(any(GymCreateDto.class))).thenReturn(expectedDto);

        mockMvc.perform(post("/api/gyms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Power House"));
    }

    @Test
    void shouldReturnAllGyms() throws Exception {
        List<GymDetailsDto> gyms = List.of(
                new GymDetailsDto(1L, "Gym A", "Addr A", "111"),
                new GymDetailsDto(2L, "Gym B", "Addr B", "222")
        );
        when(gymService.findAllGyms()).thenReturn(gyms);

        mockMvc.perform(get("/api/gyms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Gym A"))
                .andExpect(jsonPath("$[1].name").value("Gym B"));
    }

    @Test
    void shouldReturnEmptyListWhenNoGymsExist() throws Exception {
        when(gymService.findAllGyms()).thenReturn(List.of());

        mockMvc.perform(get("/api/gyms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0))
                .andExpect(content().string("[]"));
    }

    @Test
    void shouldReturn400WhenGymNameIsBlank() throws Exception {
        GymCreateDto invalidDto = new GymCreateDto(" ", "Address", "123456789");

        mockMvc.perform(post("/api/gyms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenAddressIsBlank() throws Exception {
        GymCreateDto invalidDto = new GymCreateDto("Some name", "", "123456789");

        mockMvc.perform(post("/api/gyms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenPhoneNumberIsBlank() throws Exception {
        GymCreateDto invalidDto = new GymCreateDto("Some name", "Address", "");

        mockMvc.perform(post("/api/gyms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenGymNameIsTooLong() throws Exception {
        GymCreateDto invalidDto = new GymCreateDto("a".repeat(101), "Address", "123456789");

        mockMvc.perform(post("/api/gyms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenAddressIsTooLong() throws Exception {
        GymCreateDto invalidDto = new GymCreateDto("Some name", "a".repeat(256), "123456789");

        mockMvc.perform(post("/api/gyms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenPhoneNumberIsTooLong() throws Exception {
        GymCreateDto invalidDto = new GymCreateDto("Some name", "Address", "1".repeat(25));

        mockMvc.perform(post("/api/gyms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }
}
