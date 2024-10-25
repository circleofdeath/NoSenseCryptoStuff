package deltaalpha.crypto.service;

import deltaalpha.crypto.dto.LaunchPoolDTO;
import deltaalpha.crypto.entity.LaunchPoolEntity;
import deltaalpha.crypto.repo.LaunchPoolRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class LaunchPoolService {
    public static final String STATUS_INACTIVE = "Скоро почнеться";
    public static final String STATUS_ACTIVE = "Активний";

    @Autowired
    LaunchPoolRepo launchPoolRepo;

    public String convertMapToString(Map<String, String> str) {
        StringBuilder sb = new StringBuilder("{");
        for (Map.Entry<String, String> entry : str.entrySet()) {
            sb.append(entry.getKey()).append(":=").append(entry.getValue()).append(", ");
        }
        if(sb.toString().endsWith(",")) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.append("}").toString();
    }

    public Map<String, String> convertStringToMap(String str) {
        str = str.substring(1, str.length() - 1);
        String[] arr = str.split(", ");
        Map<String, String> map = new HashMap<>();
        for(String s : arr) {
            String[] entry = s.split("=");
            if(entry[0].endsWith(":")) {
                entry[0] = entry[0].substring(0, entry[0].length() - 1);
            }

            if(entry.length > 1) {
                map.put(entry[0].trim(), entry[1].trim());
            } else {
                map.put(entry[0].trim(), "0%");
            }
        }
        return map;
    }

    public LaunchPoolEntity toEntity(LaunchPoolDTO dto) {
        LaunchPoolEntity entity = new LaunchPoolEntity();
        entity.setId(dto.getId());
        entity.setExchange(dto.getExchange());
        entity.setLaunchPool(dto.getLaunchPool());
        entity.setPools(convertMapToString(dto.getPools()));
        entity.setPeriod(dto.getPeriod());
        entity.setStatus(dto.getStatus());
        return entity;
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

    public void switchStatus(Long id) {
        launchPoolRepo.findById(id).ifPresent(entity -> {
            if(entity.getStatus().equals(STATUS_ACTIVE)) {
                entity.setStatus(STATUS_INACTIVE);
            } else {
                entity.setStatus(STATUS_ACTIVE);
            }
            launchPoolRepo.save(entity);
        });
    }

    public Flux<LaunchPoolDTO> getLaunchPoolsThatFitInPeriod() {
        return getAllLaunchPools().filter(dto -> {
            int currentYear = LocalDate.now().getYear();
            String[] period = dto.getPeriod().replace("UTC", "").split(" — ");
            var formatter = DateTimeFormatter.ofPattern("dd.MM HH:mm yyyy");
            Date startDate = Date.from(LocalDateTime
                    .parse(period[0].trim() + " " + currentYear, formatter)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
            );
            Date endDate = Date.from(LocalDateTime
                    .parse(period[1].trim() + " " + currentYear, formatter)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
            );
            Date now = new Date();
            return !now.before(startDate) && !now.after(endDate);
        });
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