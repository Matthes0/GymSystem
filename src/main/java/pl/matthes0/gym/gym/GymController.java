package pl.matthes0.gym.gym;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<GymDetailsDto>> getAllGyms() {
        List<GymDetailsDto> allGymsList = gymService.findAllGyms();
        return ResponseEntity.ok(allGymsList);
    }

    @PostMapping
    public ResponseEntity<GymDetailsDto> createGym(@RequestBody @Valid GymCreateDto gymDto) {
        GymDetailsDto createdGym = gymService.createGym(gymDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGym);
    }

    @GetMapping("/revenue")
    public ResponseEntity<List<RevenueReportDto>> getRevenue() {
        List<RevenueReportDto> revenueReportList = gymService.getRevenue();
        return ResponseEntity.ok(revenueReportList);
    }
}
