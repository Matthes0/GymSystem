package pl.matthes0.gym.gym;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.matthes0.gym.gym.dtos.GymCreateDto;

import java.util.List;

@Service
public class GymService {

    @Autowired
    private GymRepository gymRepository;

    void createGym(GymCreateDto gymDto){
        Gym gym = new Gym();
        gym.setName(gymDto.name());
        gym.setAddress(gymDto.address());
        gym.setPhoneNumber(gymDto.phoneNumber());
        gymRepository.save(gym);
    }
    List<Gym> findAllGyms(){
        return gymRepository.findAll();
    }

}
