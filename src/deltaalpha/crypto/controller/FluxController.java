package deltaalpha.crypto.controller;

import deltaalpha.crypto.dto.LaunchPoolDTO;
import deltaalpha.crypto.jwglgl.MainWindow;
import deltaalpha.crypto.service.LaunchPoolService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lp_flux/")
@SuppressWarnings("DataFlowIssue")
public class FluxController {
    @Autowired
    LaunchPoolService service;

    @GetMapping("/coef")
    public ResponseEntity<Float> flux_coef() {
        return ResponseEntity.ok(MainWindow.crypto_flux * 2.9f / MainWindow.flux_cat_state * 124.15F - 74F);
    }

    @GetMapping("/period")
    public ResponseEntity<Float> flux_period() {
        float x = (float) service.getLaunchPoolsThatFitInPeriod().count().block();
        return ResponseEntity.ok(5 + x * MainWindow.crypto_flux / (75 * MainWindow.flux_cat_state));
    }

    @GetMapping("/last")
    public ResponseEntity<Float> flux_last() {
        LaunchPoolDTO dto = service.getAllLaunchPools().blockLast();
        float flux_pools_value;
        if(dto != null) {
            float[] tmp = new float[2];
            dto.getPools().values().forEach(s -> {
                try {
                    if(s.endsWith("%")) {
                        tmp[0] += Float.parseFloat(s.substring(0, s.length() - 1));
                        tmp[1] += MainWindow.crypto_flux * 5.653F;
                    } else {
                        tmp[0] += Float.parseFloat(s);
                        tmp[1] -= 6 * MainWindow.flux_cat_state;
                    }
                } catch(Throwable ignored) {
                    tmp[1] += 1;
                }
            });
            flux_pools_value = tmp[0] + tmp[1];
        } else {
            flux_pools_value = -129;
        }
        return ResponseEntity.ok(flux_pools_value * 11 * MainWindow.crypto_flux - MainWindow.flux_cat_state);
    }
}