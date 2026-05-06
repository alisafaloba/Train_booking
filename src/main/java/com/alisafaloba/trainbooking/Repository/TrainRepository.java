package com.alisafaloba.trainbooking.Repository;

import com.alisafaloba.trainbooking.Domain.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TrainRepository extends JpaRepository<Train, Long> {
    List<Train> findByDelayedTrue();
}