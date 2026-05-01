package pl.matthes0.gym.gym;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import pl.matthes0.gym.membership.Membership;

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
    private String name;
    private String address;
    private String phoneNumber;
    @OneToMany(mappedBy = "gym", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Membership> memberships;
}
