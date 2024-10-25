package deltaalpha.crypto.service;

import deltaalpha.crypto.dto.LaunchPoolDTO;
import deltaalpha.crypto.jwglgl.SummaryWindow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SummaryService {
    @Autowired
    LaunchPoolService launchPoolService;

    public void statusSummary() {
        construct("Launchpools status summary", launchPoolService
                .getAllLaunchPools().map(dto -> {
                    if(dto.getStatus().equals(LaunchPoolService.STATUS_ACTIVE)) {
                        return new String[] {"Total", "Active"};
                    } else {
                        return new String[] {"Total", "Inactive"};
                    }
                })
                .map(List::of)
                .flatMap(Flux::fromIterable)
        );
    }

    public void launchpoolSummary() {
        construct("Launchpools launchpool summary", launchPoolService
                .getAllLaunchPools().map(LaunchPoolDTO::getLaunchPool)
        );
    }

    public void exchangeSummary() {
        construct("Launchpools exchange summary", launchPoolService
                .getAllLaunchPools().map(LaunchPoolDTO::getExchange)
        );
    }

    public void poolsSummary() {
        construct("Launchpools pools map summary", launchPoolService
                .getAllLaunchPools()
                .map(LaunchPoolDTO::getPools)
                .map(Map::keySet)
                .flatMap(Flux::fromIterable)
        );
    }

    public void construct(String title, Flux<String> flux) {
        SummaryWindow.allocateEntries(title, flux
                .collectMap(k -> k,
                        (k -> flux
                                .filter(p -> p.equals(k))
                                .count()
                                .map(Long::intValue)
                                .block()
                        ),
                        HashMap::new
                )
                .map(map -> map.entrySet().stream().map((e) -> new SummaryWindow.SummaryEntry(e.getKey(), e.getValue())))
                .map(s -> s.toArray(SummaryWindow.SummaryEntry[]::new))
                .block()
        );
    }
}