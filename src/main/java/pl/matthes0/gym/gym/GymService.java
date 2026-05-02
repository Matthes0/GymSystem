package pl.matthes0.gym.gym;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.matthes0.gym.gym.dtos.GymCreateDto;
import pl.matthes0.gym.gym.dtos.GymDetailsDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GymService {

    private final GymRepository gymRepository;
    private final GymMapper gymMapper;

    @Transactional
    public GymDetailsDto createGym(GymCreateDto gymDto){
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

}
