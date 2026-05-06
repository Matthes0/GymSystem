package pl.matthes0.gym.gym;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GymValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void shouldHaveViolationsWhenFieldIsBlank() {
        Gym gym = createGym("", "", "");

        List<String> invalidProperties = validateAndGetInvalidProperties(gym);

        assertAll("Blank fields validation",
                () -> assertEquals(3, invalidProperties.size(), "Should have exactly 3 violations"),
                () -> assertTrue(invalidProperties.containsAll(List.of("name", "address", "phoneNumber")),
                        "Violations should occur on name, address and phoneNumber")
        );
    }

    @Test
    void shouldHaveViolationsWhenFieldIsTooLong() {
        Gym gym = createGym(
                "a".repeat(101),
                "a".repeat(256),
                "1".repeat(21)
        );

        List<String> invalidProperties = validateAndGetInvalidProperties(gym);

        assertAll("Too long fields validation",
                () -> assertEquals(3, invalidProperties.size()),
                () -> assertTrue(invalidProperties.containsAll(List.of("name", "address", "phoneNumber")))
        );
    }

    private Gym createGym(String name, String address, String phone) {
        Gym gym = new Gym();
        gym.setName(name);
        gym.setAddress(address);
        gym.setPhoneNumber(phone);
        return gym;
    }

    private List<String> validateAndGetInvalidProperties(Gym gym) {
        Set<ConstraintViolation<Gym>> violations = validator.validate(gym);
        return violations.stream()
                .map(v -> v.getPropertyPath().toString())
                .toList();
    }
}