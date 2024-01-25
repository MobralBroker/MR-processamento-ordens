package com.solinfbroker.msprocessamento.repository;

import com.solinfbroker.msprocessamento.model.HistoricoPreco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoricoPrecoRepository extends JpaRepository<HistoricoPreco,Long> {
    List<HistoricoPreco> findByIdAtivo(Long id);
}
