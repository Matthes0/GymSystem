package pl.matthes0.gym.gym;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gym")
public class GymController {

    @Autowired
    private GymRepository gymRepository;

    @GetMapping
    public void getAllGyms(){

    }

    @PostMapping
    public void addGym(){

    }
}
