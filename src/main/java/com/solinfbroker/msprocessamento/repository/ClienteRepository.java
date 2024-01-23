package com.solinfbroker.msprocessamento.repository;

import com.solinfbroker.msprocessamento.model.ClienteModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<ClienteModel,Long> {
}
