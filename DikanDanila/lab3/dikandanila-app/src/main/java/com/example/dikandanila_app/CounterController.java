package com.example.dikandanila_app;

import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CounterController {

    private final RelapseRepository repository;

    public CounterController(RelapseRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Relapse lastRelapse = repository.findTopByOrderByRelapseDateDesc().orElse(null);

        long days = 0;
        long hours = 0;

        if (lastRelapse != null) {
            LocalDateTime now = LocalDateTime.now();
            days = ChronoUnit.DAYS.between(lastRelapse.getRelapseDate(), now);
            hours = ChronoUnit.HOURS.between(lastRelapse.getRelapseDate(), now) % 24;
        } else {
            days = 999;
        }

        return Map.of(
                "days", days,
                "hours", hours,
                "lastReason", lastRelapse != null ? lastRelapse.getReason() : "Я чист!"
        );
    }

    @GetMapping("/history")
    public List<Relapse> getHistory() {
        return repository.findAll();
    }

    @PostMapping("/reset")
    public void reset(@RequestBody Map<String, String> body) {
        Relapse relapse = new Relapse();
        relapse.setRelapseDate(LocalDateTime.now());
        relapse.setReason(body.get("reason"));
        repository.save(relapse);
    }

    @DeleteMapping("/clear")
    public void clearHistory() {
        repository.deleteAll();
    }
}