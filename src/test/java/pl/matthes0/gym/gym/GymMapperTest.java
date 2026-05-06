package pl.matthes0.gym.gym;

import org.junit.jupiter.api.Test;
import pl.matthes0.gym.gym.dtos.GymCreateDto;
import pl.matthes0.gym.gym.dtos.GymDetailsDto;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GymMapperTest {

    private final GymMapper mapper = new GymMapper();

    private static final String GYM_NAME = "Power Gym";
    private static final String GYM_ADDRESS = "Power Street 123-456 Lublin";
    private static final String GYM_PHONE = "111222333";

    @Test
    void shouldMapDtoToEntity() {
        GymCreateDto dto = new GymCreateDto(GYM_NAME, GYM_ADDRESS, GYM_PHONE);

        Gym result = mapper.toEntity(dto);

        assertAll("Entity mapping",
                () -> assertNotNull(result),
                () -> assertEquals(GYM_NAME, result.getName()),
                () -> assertEquals(GYM_ADDRESS, result.getAddress()),
                () -> assertEquals(GYM_PHONE, result.getPhoneNumber()),
                () -> assertNull(result.getId())
        );
    }

    @Test
    void shouldMapEntityToDetailsDto() {
        Gym gym = createGym(1L, GYM_NAME, GYM_ADDRESS, GYM_PHONE);

        GymDetailsDto result = mapper.toDetailsDto(gym);

        assertAll("DTO mapping",
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertEquals(GYM_NAME, result.name()),
                () -> assertEquals(GYM_ADDRESS, result.address()),
                () -> assertEquals(GYM_PHONE, result.phoneNumber())
        );
    }

    @Test
    void shouldReturnNullWhenInputsAreNull() {
        assertAll("Null handling",
                () -> assertNull(mapper.toEntity(null)),
                () -> assertNull(mapper.toDetailsDto(null))
        );
    }

    @Test
    void shouldMapEntityWithPartialData() {
        Gym gym = new Gym();
        gym.setId(99L);
        gym.setName("Minimalist Gym");

        GymDetailsDto result = mapper.toDetailsDto(gym);

        assertAll("Partial data mapping",
                () -> assertNotNull(result),
                () -> assertEquals(99L, result.id()),
                () -> assertEquals("Minimalist Gym", result.name()),
                () -> assertNull(result.address()),
                () -> assertNull(result.phoneNumber())
        );
    }

    private Gym createGym(Long id, String name, String address, String phone) {
        Gym gym = new Gym();
        gym.setId(id);
        gym.setName(name);
        gym.setAddress(address);
        gym.setPhoneNumber(phone);
        gym.setMembershipPlans(new ArrayList<>());
        return gym;
    }
}