package pl.matthes0.gym.membership;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="memberships")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Membership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private MembershipPlan membershipPlan;
    @Embedded
    private Price monthlyPrice;
    private Integer durationInMonths;
    private Integer maxMembers;
}
