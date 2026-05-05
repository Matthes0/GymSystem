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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MembershipPlanValidationTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
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

        Set<ConstraintViolation<MembershipPlan>> violations = validator.validate(plan);
        List<String> invalidProperties = violations.stream()
                .map(v -> v.getPropertyPath().toString())
                .toList();

        assertEquals(6, violations.size());
        assertTrue(invalidProperties.containsAll(List.of(
                "name", "plan", "monthlyPrice", "durationInMonths", "maxMembers", "gym"
        )));
    }

    @Test
    void shouldHaveViolationsWhenNumbersAreInvalid() {

        MembershipPlan plan = createValidPlan();
        plan.setDurationInMonths(0);
        plan.setMaxMembers(-5);

        Set<ConstraintViolation<MembershipPlan>> violations = validator.validate(plan);
        List<String> invalidProperties = violations.stream()
                .map(v -> v.getPropertyPath().toString())
                .toList();

        assertEquals(2, violations.size());
        assertTrue(invalidProperties.containsAll(List.of("durationInMonths", "maxMembers")));
    }

    @Test
    void shouldHaveViolationsWhenNestedPriceIsInvalid() {
        MembershipPlan plan = createValidPlan();
        Price invalidPrice = new Price(new BigDecimal("0.00"), null);
        plan.setMonthlyPrice(invalidPrice);

        Set<ConstraintViolation<MembershipPlan>> violations = validator.validate(plan);
        List<String> invalidProperties = violations.stream()
                .map(v -> v.getPropertyPath().toString())
                .toList();
        assertEquals(2, violations.size());
        assertTrue(invalidProperties.containsAll(List.of("monthlyPrice.amount", "monthlyPrice.currency")));
    }

    @Test
    void shouldHaveViolationWhenPriceDigitsAreExceeded() {
        MembershipPlan plan = createValidPlan();
        Price invalidDigits = new Price(new BigDecimal("100.123"), Currency.getInstance("PLN"));
        plan.setMonthlyPrice(invalidDigits);

        Set<ConstraintViolation<MembershipPlan>> violations = validator.validate(plan);

        assertEquals(1, violations.size());
        assertEquals("monthlyPrice.amount", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldNotHaveViolationsWhenDataIsCorrect() {
        MembershipPlan plan = createValidPlan();

        Set<ConstraintViolation<MembershipPlan>> violations = validator.validate(plan);

        assertTrue(violations.isEmpty());
    }

    private MembershipPlan createValidPlan() {
        MembershipPlan plan = new MembershipPlan();
        plan.setName("Standard");
        plan.setPlan(Plan.BASIC);
        plan.setMonthlyPrice(new Price(new BigDecimal("99.99"), Currency.getInstance("PLN")));
        plan.setDurationInMonths(12);
        plan.setMaxMembers(100);
        plan.setGym(new Gym());
        return plan;
    }
}