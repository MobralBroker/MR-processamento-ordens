package com.solinfbroker.msprocessamento.repository;

import com.solinfbroker.msprocessamento.model.AtivoModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtivoRepository extends JpaRepository<AtivoModel,Long> {
}
