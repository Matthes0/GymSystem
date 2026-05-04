package pl.matthes0.gym.gym;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.matthes0.gym.gym.dtos.GymCreateDto;
import pl.matthes0.gym.gym.dtos.GymDetailsDto;
import pl.matthes0.gym.gym.dtos.RevenueReportDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GymService {

    private final GymRepository gymRepository;
    private final GymMapper gymMapper;

    @Transactional
    public GymDetailsDto createGym(GymCreateDto gymDto){
        if (gymRepository.existsByName(gymDto.name())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Gym with name " + gymDto.name() + " already exists");
        }

        Gym gym = gymMapper.toEntity(gymDto);
        Gym savedGym = gymRepository.save(gym);

        return gymMapper.toDetailsDto(savedGym);

    }
    public List<GymDetailsDto> findAllGyms()
    {
        return gymRepository.findAll().stream()
                .map(gymMapper::toDetailsDto)
                .toList();
    }

    public List<RevenueReportDto> getRevenue() {
        return gymRepository.getRevenueReport();
    }
}
