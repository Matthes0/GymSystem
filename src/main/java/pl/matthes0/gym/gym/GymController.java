package pl.matthes0.gym.gym;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.matthes0.gym.gym.dtos.GymCreateDto;
import pl.matthes0.gym.gym.dtos.GymDetailsDto;
import pl.matthes0.gym.gym.dtos.RevenueReportDto;

import java.util.List;

@RestController
@RequestMapping("/api/gyms")
@RequiredArgsConstructor
public class GymController {

    private final GymService gymService;

    @GetMapping
    public List<GymDetailsDto> getAllGyms(){
        return gymService.findAllGyms();
    }

    @PostMapping
    public GymDetailsDto createGym(@RequestBody @Valid GymCreateDto gymDto){
        return gymService.createGym(gymDto);
    }
    @GetMapping("/revenue")
    public List<RevenueReportDto> getRevenue() { return gymService.getRevenue();}
}
