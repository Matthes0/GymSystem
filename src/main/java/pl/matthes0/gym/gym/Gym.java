package pl.matthes0.gym.gym;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import pl.matthes0.gym.membershipplan.MembershipPlan;

import java.util.List;

@Entity
@Table(name="gyms")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Gym {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    @NotBlank(message = "Name is required")
    private String name;
    @Column(nullable = false)
    @NotBlank(message = "Address is required")
    private String address;
    @Column(nullable = false)
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    @OneToMany(mappedBy = "gym", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<MembershipPlan> membershipPlans;
}
