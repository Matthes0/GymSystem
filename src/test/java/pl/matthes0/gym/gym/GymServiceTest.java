package pl.matthes0.gym.gym;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.matthes0.gym.gym.dtos.GymCreateDto;
import pl.matthes0.gym.gym.dtos.GymDetailsDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GymServiceTest {
    @Mock
    private GymRepository repository;
    @Mock
    private GymMapper mapper;
    @InjectMocks
    private GymService service;

    @Test
    void shouldCreateGymSuccessfully() {
        GymCreateDto dto = new GymCreateDto("Iron Paradise", "Miami", "123456789");
        Gym gym = new Gym();
        Gym savedGym = new Gym();
        savedGym.setId(1L);
        GymDetailsDto expectedDto = new GymDetailsDto(1L, "Iron Paradise", "Miami", "123456789");
        when(mapper.toEntity(dto)).thenReturn(gym);
        when(repository.save(gym)).thenReturn(savedGym);
        when(mapper.toDetailsDto(savedGym)).thenReturn(expectedDto);
        GymDetailsDto result = service.createGym(dto);
        assertEquals(expectedDto, result);
        verify(repository).save(gym);
    }

    @Test
    void shouldReturnAllGyms() {
        Gym gym = new Gym();
        when(repository.findAll()).thenReturn(List.of(gym));
        when(mapper.toDetailsDto(gym)).thenReturn(new GymDetailsDto(1L, "Gym", "Addr", "123456789"));
        List<GymDetailsDto> result = service.findAllGyms();
        assertEquals(1, result.size());
        verify(repository, times(1)).findAll();
    }
}