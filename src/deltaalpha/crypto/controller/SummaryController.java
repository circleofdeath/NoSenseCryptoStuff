package deltaalpha.crypto.controller;

import deltaalpha.crypto.service.SummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/summary")
public class SummaryController {
    public static final String SUMMARY_RETURN = "Now you can press 's' key on your manager to open summary window!";

    @Autowired
    SummaryService summaryService;

    @GetMapping("/status")
    public ResponseEntity<String> statusSummary() {
        summaryService.statusSummary();
        return ResponseEntity.ok(SUMMARY_RETURN);
    }

    @GetMapping("/launchpool")
    public ResponseEntity<String> launchpoolSummary() {
        summaryService.launchpoolSummary();
        return ResponseEntity.ok(SUMMARY_RETURN);
    }

    @GetMapping("/exchange")
    public ResponseEntity<String> exchangeSummary() {
        summaryService.exchangeSummary();
        return ResponseEntity.ok(SUMMARY_RETURN);
    }

    @GetMapping("/pools")
    public ResponseEntity<String> poolsSummary() {
        summaryService.poolsSummary();
        return ResponseEntity.ok(SUMMARY_RETURN);
    }
}