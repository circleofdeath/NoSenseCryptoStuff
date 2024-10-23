package deltaalpha.crypto.controller;

import deltaalpha.crypto.dto.LaunchPoolDTO;
import deltaalpha.crypto.service.LaunchPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/lp")
public class LaunchPoolController {
    @Autowired
    LaunchPoolService launchPoolService;

    //TODO change launch pool status (switch between active/inactive)

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
}