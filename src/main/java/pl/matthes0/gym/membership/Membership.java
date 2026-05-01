package pl.matthes0.gym.membership;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.matthes0.gym.gym.Gym;
import pl.matthes0.gym.member.Member;

import java.util.List;

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_id")
    @JsonBackReference
    private Gym gym;
    @OneToMany(mappedBy = "membership", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Member> members;
}
