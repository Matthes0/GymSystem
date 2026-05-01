package pl.matthes0.gym.gym;

import jakarta.persistence.*;
import lombok.*;

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
}
