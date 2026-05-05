package pl.matthes0.gym.member;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.matthes0.gym.membershipplan.MembershipPlan;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MemberValidationTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldHaveViolationsWhenFieldsAreBlankOrNull() {
        Member member = new Member();
        member.setFullName("");
        member.setEmail("");
        member.setMembershipStartDate(null);
        member.setStatus(null);
        member.setMembershipPlan(null);

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        List<String> invalidProperties = violations.stream()
                .map(v -> v.getPropertyPath().toString())
                .toList();


        assertEquals(5, violations.size());
        assertTrue(invalidProperties.containsAll(List.of(
                "fullName", "email", "membershipStartDate", "status", "membershipPlan"
        )));
    }

    @Test
    void shouldHaveViolationsWhenFieldsAreTooLong() {
        Member member = new Member();
        member.setFullName("a".repeat(101));
        member.setEmail("a".repeat(96) + "@test.com");
        member.setMembershipStartDate(LocalDate.now());
        member.setStatus(Status.ACTIVE);
        member.setMembershipPlan(new MembershipPlan());

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        List<String> invalidProperties = violations.stream()
                .map(v -> v.getPropertyPath().toString())
                .toList();

        assertEquals(3, violations.size()); // 3 because incorrect email format also pops up as first part of email is too long
        assertTrue(invalidProperties.containsAll(List.of("fullName", "email")));
    }

    @Test
    void shouldHaveViolationWhenEmailFormatIsIncorrect() {
        Member member = new Member();
        member.setFullName("John Doe");
        member.setEmail("not-an-email");
        member.setMembershipStartDate(LocalDate.now());
        member.setStatus(Status.ACTIVE);
        member.setMembershipPlan(new MembershipPlan());

        Set<ConstraintViolation<Member>> violations = validator.validate(member);

        assertEquals(1, violations.size());
        assertEquals("email", violations.iterator().next().getPropertyPath().toString());
        assertEquals("Incorrect email format", violations.iterator().next().getMessage());
    }

    @Test
    void shouldNotHaveViolationsWhenDataIsCorrect() {
        Member member = new Member();
        member.setFullName("Jan Kowalski");
        member.setEmail("jan@kowalski.pl");
        member.setMembershipStartDate(LocalDate.now());
        member.setStatus(Status.ACTIVE);
        member.setMembershipPlan(new MembershipPlan());

        Set<ConstraintViolation<Member>> violations = validator.validate(member);

        assertTrue(violations.isEmpty());
    }
}