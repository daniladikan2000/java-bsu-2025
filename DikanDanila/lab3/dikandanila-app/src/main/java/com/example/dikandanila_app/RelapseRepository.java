package com.example.dikandanila_app;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RelapseRepository extends JpaRepository<Relapse, Long> {
    Optional<Relapse> findTopByOrderByRelapseDateDesc();
}