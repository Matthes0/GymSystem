package pl.matthes0.gym.gym;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GymValidationTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    @Test
    void shouldHaveViolationsWhenFieldIsBlank() {
        Gym gym = new Gym();
        gym.setName("");
        gym.setAddress("");
        gym.setPhoneNumber("");

        Set<ConstraintViolation<Gym>> violations = validator.validate(gym);
        List<String> invalidProperties = violations.stream()
                .map(v -> v.getPropertyPath().toString())
                .toList();

        assertEquals(3, violations.size());
        assertTrue(invalidProperties.containsAll(List.of("name", "address", "phoneNumber")));
    }
    @Test
    void shouldHaveViolationsWhenFieldIsTooLong() {
        Gym gym = new Gym();
        gym.setName("a".repeat(105));
        gym.setAddress("a".repeat(300));
        gym.setPhoneNumber("a".repeat(30));

        Set<ConstraintViolation<Gym>> violations = validator.validate(gym);
        List<String> invalidProperties = violations.stream()
                .map(v -> v.getPropertyPath().toString())
                .toList();
        assertEquals(3, violations.size());
        assertTrue(invalidProperties.containsAll(List.of("name", "address", "phoneNumber")));
    }
}
