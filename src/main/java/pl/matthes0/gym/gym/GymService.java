package pl.matthes0.gym.gym;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.matthes0.gym.gym.dtos.GymCreateDto;
import pl.matthes0.gym.gym.dtos.GymDetailsDto;

import java.util.List;

@Service
public class GymService {

    @Autowired
    private GymRepository gymRepository;

    GymDetailsDto createGym(GymCreateDto gymDto){
        Gym gym = new Gym();
        gym.setName(gymDto.name());
        gym.setAddress(gymDto.address());
        gym.setPhoneNumber(gymDto.phoneNumber());
        gymRepository.save(gym);
        return new GymDetailsDto(
                gym.getId(),
                gym.getName(),
                gym.getAddress(),
                gym.getPhoneNumber()
        );

    }
    List<GymDetailsDto> findAllGyms()
    {
        return gymRepository.findAll().stream()
                .map(gym -> new GymDetailsDto(gym.getId(), gym.getName(), gym.getAddress(), gym.getPhoneNumber()))
                .toList();
    }

}
