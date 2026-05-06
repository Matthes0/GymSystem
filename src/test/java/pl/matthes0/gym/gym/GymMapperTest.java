package pl.matthes0.gym.gym;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.matthes0.gym.gym.dtos.GymCreateDto;
import pl.matthes0.gym.gym.dtos.GymDetailsDto;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GymMapperTest {
    private final GymMapper mapper = new GymMapper();

    @Test
    @DisplayName("Should correctly map GymCreateDto to Gym entity")
    void shouldMapDtoToEntity() {
        GymCreateDto dto = new GymCreateDto("Power Gym", "Power Street 123-456 Lublin", "111222333");

        Gym result = mapper.toEntity(dto);

        assertNotNull(result);
        assertEquals("Power Gym", result.getName());
        assertEquals("Power Street 123-456 Lublin", result.getAddress());
        assertEquals("111222333", result.getPhoneNumber());
        assertNull(result.getId());
    }

    @Test
    @DisplayName("Should correctly map Gym entity to GymDetailsDto")
    void shouldMapEntityToDetailsDto() {
        Gym gym = new Gym();
        gym.setId(1L);
        gym.setName("Power Gym");
        gym.setAddress("Power Street 123-456 Lublin");
        gym.setPhoneNumber("111222333");
        gym.setMembershipPlans(new ArrayList<>());

        GymDetailsDto result = mapper.toDetailsDto(gym);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Power Gym", result.name());
        assertEquals("Power Street 123-456 Lublin", result.address());
        assertEquals("111222333", result.phoneNumber());
    }

    @Test
    @DisplayName("Should return null when mapping null GymCreateDto")
    void shouldReturnNullWhenToEntityInputIsNull() {
        Gym result = mapper.toEntity(null);

        assertNull(result);
    }

    @Test
    @DisplayName("Should return null when mapping null Gym entity")
    void shouldReturnNullWhenToDetailsDtoInputIsNull() {
        GymDetailsDto result = mapper.toDetailsDto(null);

        assertNull(result);
    }

    @Test
    @DisplayName("Should handle Gym entity with null fields correctly")
    void shouldMapEntityWithPartialData() {
        Gym gym = new Gym();
        gym.setId(99L);
        gym.setName("Minimalist Gym");

        GymDetailsDto result = mapper.toDetailsDto(gym);

        assertNotNull(result);
        assertEquals(99L, result.id());
        assertEquals("Minimalist Gym", result.name());
        assertNull(result.address());
        assertNull(result.phoneNumber());
    }
}