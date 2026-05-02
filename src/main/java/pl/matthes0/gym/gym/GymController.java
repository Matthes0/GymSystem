package pl.matthes0.gym.gym;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.matthes0.gym.gym.dtos.GymCreateDto;
import pl.matthes0.gym.gym.dtos.GymDetailsDto;

import java.util.List;

@RestController
@RequestMapping("/api/gyms")
public class GymController {

    private final GymService gymService;

    @Autowired
    public GymController(GymService gymService) {
        this.gymService = gymService;
    }

    @GetMapping
    public List<GymDetailsDto> getAllGyms(){
        return gymService.findAllGyms();
    }

    @PostMapping
    public GymDetailsDto addGym(@RequestBody GymCreateDto gymDto){
        return gymService.createGym(gymDto);
    }
}
