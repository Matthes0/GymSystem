package pl.matthes0.gym.member;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.matthes0.gym.membershipplan.MembershipPlan;

import java.time.LocalDate;

@Entity
@Table(name = "members")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name is too long")
    private String fullName;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Email is required")
    @Email(message = "Incorrect email format")
    @Size(max = 100, message = "Email is too long")
    private String email;

    @Column(nullable = false)
    @NotNull(message = "Membership start date is required")
    private LocalDate membershipStartDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Status is required")
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membership_plan_id", nullable = false)
    @JsonBackReference
    @NotNull(message = "Membership plan is required")
    private MembershipPlan membershipPlan;
}
