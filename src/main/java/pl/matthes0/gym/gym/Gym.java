package pl.matthes0.gym.gym;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @Column(unique = true, nullable = false, length = 100)
    @NotBlank(message = "Name is required")
    @Size(max = 100)
    private String name;

    @Column(nullable = false)
    @NotBlank(message = "Address is required")
    @Size(max = 255)
    private String address;

    @Column(nullable = false, length = 20)
    @NotBlank(message = "Phone number is required")
    @Size(max = 20)
    private String phoneNumber;

    @OneToMany(mappedBy = "gym", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<MembershipPlan> membershipPlans;
}
