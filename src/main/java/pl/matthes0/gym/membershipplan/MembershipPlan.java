package pl.matthes0.gym.membershipplan;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.matthes0.gym.gym.Gym;
import pl.matthes0.gym.member.Member;

import java.util.List;

@Entity
@Table(name="membership_plans")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MembershipPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Name is required")
    @Size(max=100, message = "Name is too long")
    private String name;

    @Column(nullable = false)
    @NotNull(message = "Plan is required")
    @Enumerated(EnumType.STRING)
    private Plan plan;

    @Embedded
    @Valid
    @NotNull(message = "Price details are required")
    private Price monthlyPrice;

    @Column(nullable = false)
    @Positive(message = "Duration must be greater than zero")
    @NotNull(message = "Duration in months is required")
    private Integer durationInMonths;

    @Column(nullable = false)
    @Positive(message = "Max members must be greater than zero")
    @NotNull(message = "Max members are required")
    private Integer maxMembers;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_id", nullable = false)
    @NotNull(message = "Gym is required")
    @JsonBackReference
    private Gym gym;

    @OneToMany(mappedBy = "membershipPlan", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Member> members;
}
