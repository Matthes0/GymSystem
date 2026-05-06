package pl.matthes0.gym.membershipplan;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.matthes0.gym.gym.Gym;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MembershipPlanValidationTest {

    private Validator validator;

    private static final String VALID_NAME = "Standard";
    private static final BigDecimal VALID_AMOUNT = new BigDecimal("99.99");
    private static final String CURRENCY_CODE = "PLN";

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void shouldHaveViolationsWhenFieldsAreNullOrBlank() {
        MembershipPlan plan = new MembershipPlan();
        plan.setName("");
        plan.setPlan(null);
        plan.setMonthlyPrice(null);
        plan.setDurationInMonths(null);
        plan.setMaxMembers(null);
        plan.setGym(null);

        List<String> invalidProperties = validateAndGetInvalidProperties(plan);

        assertAll("Null and blank fields validation",
                () -> assertEquals(6, invalidProperties.size()),
                () -> assertTrue(invalidProperties.containsAll(List.of(
                        "name", "plan", "monthlyPrice", "durationInMonths", "maxMembers", "gym"
                )))
        );
    }

    @Test
    void shouldHaveViolationsWhenNumbersAreInvalid() {
        MembershipPlan plan = createValidPlan();
        plan.setDurationInMonths(0);
        plan.setMaxMembers(-5);

        List<String> invalidProperties = validateAndGetInvalidProperties(plan);

        assertAll("Invalid number values validation",
                () -> assertEquals(2, invalidProperties.size()),
                () -> assertTrue(invalidProperties.containsAll(List.of("durationInMonths", "maxMembers")))
        );
    }

    @Test
    void shouldHaveViolationsWhenNestedPriceIsInvalid() {
        MembershipPlan plan = createValidPlan();
        Price invalidPrice = new Price(new BigDecimal("0.00"), null);
        plan.setMonthlyPrice(invalidPrice);

        List<String> invalidProperties = validateAndGetInvalidProperties(plan);

        assertAll("Nested price object validation",
                () -> assertEquals(2, invalidProperties.size()),
                () -> assertTrue(invalidProperties.containsAll(List.of("monthlyPrice.amount", "monthlyPrice.currency")))
        );
    }

    @Test
    void shouldHaveViolationWhenPriceDigitsAreExceeded() {
        MembershipPlan plan = createValidPlan();
        Price invalidDigits = new Price(new BigDecimal("100.123"), Currency.getInstance(CURRENCY_CODE));
        plan.setMonthlyPrice(invalidDigits);

        Set<ConstraintViolation<MembershipPlan>> violations = validator.validate(plan);

        assertAll("Price digits precision validation",
                () -> assertEquals(1, violations.size()),
                () -> assertEquals("monthlyPrice.amount", violations.iterator().next().getPropertyPath().toString())
        );
    }

    @Test
    void shouldNotHaveViolationsWhenDataIsCorrect() {
        MembershipPlan plan = createValidPlan();

        Set<ConstraintViolation<MembershipPlan>> violations = validator.validate(plan);

        assertTrue(violations.isEmpty());
    }


    private MembershipPlan createValidPlan() {
        MembershipPlan plan = new MembershipPlan();
        plan.setName(VALID_NAME);
        plan.setPlan(Plan.BASIC);
        plan.setMonthlyPrice(new Price(VALID_AMOUNT, Currency.getInstance(CURRENCY_CODE)));
        plan.setDurationInMonths(12);
        plan.setMaxMembers(100);
        plan.setGym(new Gym());
        return plan;
    }

    private List<String> validateAndGetInvalidProperties(MembershipPlan plan) {
        Set<ConstraintViolation<MembershipPlan>> violations = validator.validate(plan);
        return violations.stream()
                .map(v -> v.getPropertyPath().toString())
                .toList();
    }
}