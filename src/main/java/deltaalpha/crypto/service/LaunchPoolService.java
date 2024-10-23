package deltaalpha.crypto.service;

import deltaalpha.crypto.dto.LaunchPoolDTO;
import deltaalpha.crypto.entity.LaunchPoolEntity;
import deltaalpha.crypto.repo.LaunchPoolRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.Map;

@Service
public class LaunchPoolService {
    public static final String STATUS_INACTIVE = "Скоро почнеться";
    public static final String STATUS_ACTIVE = "Активний";

    @Autowired
    LaunchPoolRepo launchPoolRepo;

    public Map<String, String> convertStringToMap(String str) {
        str = str.substring(1, str.length() - 1);
        String[] arr = str.split(", ");
        Map<String, String> map = new HashMap<>();
        for (String s : arr) {
            String[] entry = s.split(":=");
            if (entry.length > 1) {
                map.put(entry[0].trim(), entry[1].trim());
            }
        }
        return map;
    }

    public LaunchPoolDTO toDTO(LaunchPoolEntity entity) {
        return LaunchPoolDTO
                .builder()
                .id(entity.getId())
                .exchange(entity.getExchange())
                .launchPool(entity.getLaunchPool())
                .pools(convertStringToMap(entity.getPools()))
                .period(entity.getPeriod())
                .status(entity.getStatus())
                .build();
    }

    public Flux<LaunchPoolDTO> getAllLaunchPools() {
        return Mono.fromCallable(() -> launchPoolRepo.findAll())
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .map(this::toDTO);
    }

    public Flux<LaunchPoolDTO> getAllActiveLaunchPools() {
        return getAllLaunchPools().filter(dto -> dto.getStatus().equals(STATUS_ACTIVE));
    }

    public Flux<LaunchPoolDTO> getAllInactiveLaunchPools() {
        return getAllLaunchPools().filter(dto -> dto.getStatus().equals(STATUS_INACTIVE));
    }
}