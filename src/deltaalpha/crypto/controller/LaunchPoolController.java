package deltaalpha.crypto.controller;

import deltaalpha.crypto.CryptoApplication;
import deltaalpha.crypto.dto.LaunchPoolDTO;
import deltaalpha.crypto.jwglgl.MainWindow;
import deltaalpha.crypto.service.LaunchPoolService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/lp")
public class LaunchPoolController {
    @Autowired
    LaunchPoolService launchPoolService;

    @GetMapping("/slp4t")
    public ResponseEntity<Void> slp4t() {
        Mono.fromRunnable(() -> {
            for(int i = 0; i < 4; i++) {
                MainWindow.setCachedLaunchpools(launchPoolService.getAllInactiveLaunchPools().collectList().block());
                try {
                    Thread.sleep(6000);
                } catch(InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            MainWindow.setCachedLaunchpools(new ArrayList<>());
        }).subscribeOn(Schedulers.boundedElastic()).subscribe();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/spl4t_cached")
    public ResponseEntity<Void> slp4tCached() {
        Mono.fromRunnable(() -> {
            List<LaunchPoolDTO> cache = new ArrayList<>();
            for(int i = 0; i < 4; i++) {
                List<LaunchPoolDTO> active = launchPoolService.getAllInactiveLaunchPools().collectList().block();
                assert active != null;
                active.removeIf(cache::contains);
                cache.addAll(active);
                MainWindow.setCachedLaunchpools(active);
                try {
                    Thread.sleep(6000);
                } catch(InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            MainWindow.setCachedLaunchpools(new ArrayList<>());
        })
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorComplete(e -> {
                    throw new RuntimeException(e);
                })
                .subscribe();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/mine/{key}")
    public ResponseEntity<String> mine(@PathVariable String key) {
        if(!CryptoApplication.SECURITY_KEY.equals(key)) {
            return ResponseEntity.badRequest().body("Invalid security key");
        }
        int[] result = new int[2];
        launchPoolService.getAllActiveLaunchPools().doOnNext(dto -> {
            try {
                Thread.sleep(5000);
                launchPoolService.switchStatus(dto.getId());
                result[0]++;
            } catch(InterruptedException ignored) {
                result[1]++;
            }
        }).then().block();
        return ResponseEntity.ok("pools mined: " + result[0] + ", errors: " + result[1]);
    }

    @GetMapping("/switch/{key}/{id}")
    public ResponseEntity<Void> switchStatus(@PathVariable String key, @PathVariable Long id) {
        if(!CryptoApplication.SECURITY_KEY.equals(key)) {
            return ResponseEntity.badRequest().build();
        }
        launchPoolService.switchStatus(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/inactive")
    public ResponseEntity<LaunchPoolDTO[]> getInactiveLaunchPools() {
        var mono = launchPoolService.getAllInactiveLaunchPools().collectList().block();
        Objects.requireNonNull(mono);
        return ResponseEntity.ok(mono.toArray(LaunchPoolDTO[]::new));
    }

    @GetMapping("/active")
    public ResponseEntity<LaunchPoolDTO[]> getActiveLaunchPools() {
        var mono = launchPoolService.getAllActiveLaunchPools().collectList().block();
        Objects.requireNonNull(mono);
        return ResponseEntity.ok(mono.toArray(LaunchPoolDTO[]::new));
    }

    @GetMapping("/raw")
    public ResponseEntity<LaunchPoolDTO[]> getAllLaunchPools() {
        var mono = launchPoolService.getAllLaunchPools().collectList().block();
        Objects.requireNonNull(mono);
        return ResponseEntity.ok(mono.toArray(LaunchPoolDTO[]::new));
    }

    @GetMapping("/period")
    public ResponseEntity<LaunchPoolDTO[]> getLaunchPoolsThatFitInPeriod() {
        var mono = launchPoolService.getLaunchPoolsThatFitInPeriod().collectList().block();
        Objects.requireNonNull(mono);
        return ResponseEntity.ok(mono.toArray(LaunchPoolDTO[]::new));
    }
}