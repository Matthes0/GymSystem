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

import static org.junit.jupiter.api.Assertions.*;

class MemberValidationTest {

    private Validator validator;

    private static final String VALID_NAME = "Jan Kowalski";
    private static final String VALID_EMAIL = "jan@kowalski.pl";

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void shouldHaveViolationsWhenFieldsAreBlankOrNull() {
        Member member = createMember("", "", null, null, null);

        List<String> invalidProperties = validateAndGetInvalidProperties(member);

        assertAll("Blank and null fields validation",
                () -> assertEquals(5, invalidProperties.size()),
                () -> assertTrue(invalidProperties.containsAll(List.of(
                        "fullName", "email", "membershipStartDate", "status", "membershipPlan"
                )))
        );
    }

    @Test
    void shouldHaveViolationsWhenFieldsAreTooLong() {
        // Given
        Member member = createMember(
                "a".repeat(101),
                "a".repeat(96) + "@test.com",
                LocalDate.now(),
                Status.ACTIVE,
                new MembershipPlan()
        );

        List<String> invalidProperties = validateAndGetInvalidProperties(member);

        assertAll("Too long fields validation",
                () -> assertEquals(3, invalidProperties.size()), // 3 because incorrect email format also pops up
                () -> assertTrue(invalidProperties.containsAll(List.of("fullName", "email")))
        );
    }

    @Test
    void shouldHaveViolationWhenEmailFormatIsIncorrect() {
        Member member = createMember(VALID_NAME, "not-an-email", LocalDate.now(), Status.ACTIVE, new MembershipPlan());

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        ConstraintViolation<Member> violation = violations.iterator().next();

        assertAll("Email format validation",
                () -> assertEquals(1, violations.size()),
                () -> assertEquals("email", violation.getPropertyPath().toString()),
                () -> assertEquals("Incorrect email format", violation.getMessage())
        );
    }

    @Test
    void shouldNotHaveViolationsWhenDataIsCorrect() {
        Member member = createMember(VALID_NAME, VALID_EMAIL, LocalDate.now(), Status.ACTIVE, new MembershipPlan());

        Set<ConstraintViolation<Member>> violations = validator.validate(member);

        assertTrue(violations.isEmpty());
    }

    private Member createMember(String name, String email, LocalDate date, Status status, MembershipPlan plan) {
        Member member = new Member();
        member.setFullName(name);
        member.setEmail(email);
        member.setMembershipStartDate(date);
        member.setStatus(status);
        member.setMembershipPlan(plan);
        return member;
    }

    private List<String> validateAndGetInvalidProperties(Member member) {
        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        return violations.stream()
                .map(v -> v.getPropertyPath().toString())
                .toList();
    }
}