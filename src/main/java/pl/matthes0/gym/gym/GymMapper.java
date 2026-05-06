package pl.matthes0.gym.gym;

import org.springframework.stereotype.Component;
import pl.matthes0.gym.gym.dtos.GymCreateDto;
import pl.matthes0.gym.gym.dtos.GymDetailsDto;

@Component
public class GymMapper {

    public Gym toEntity(GymCreateDto gymCreateDto) {
        if (gymCreateDto == null) {
            return null;
        }
        Gym gym = new Gym();
        gym.setName(gymCreateDto.name());
        gym.setAddress(gymCreateDto.address());
        gym.setPhoneNumber(gymCreateDto.phoneNumber());
        return gym;
    }

    public GymDetailsDto toDetailsDto(Gym gym) {
        if (gym == null) {
            return null;
        }
        return new GymDetailsDto(
                gym.getId(),
                gym.getName(),
                gym.getAddress(),
                gym.getPhoneNumber()
        );
    }
}
