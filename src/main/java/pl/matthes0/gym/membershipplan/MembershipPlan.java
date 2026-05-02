package pl.matthes0.gym.membershipplan;


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
@Table(name="membership_plans")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MembershipPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private Plan plan;
    @Embedded
    private Price monthlyPrice;
    private Integer durationInMonths;
    private Integer maxMembers;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_id")
    @JsonBackReference
    private Gym gym;
    @OneToMany(mappedBy = "membershipPlan", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Member> members;
}
