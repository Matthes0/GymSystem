package pl.matthes0.gym.gym;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.matthes0.gym.gym.dtos.GymCreateDto;

import java.util.List;

@RestController
@RequestMapping("/api/gym")
public class GymController {

    private final GymService gymService;

    @Autowired
    public GymController(GymService gymService) {
        this.gymService = gymService;
    }

    @GetMapping
    public List<Gym> getAllGyms(){
        return gymService.findAllGyms();
    }

    @PostMapping
    public void addGym(GymCreateDto gymDto){
        gymService.createGym(gymDto);
    }
}
