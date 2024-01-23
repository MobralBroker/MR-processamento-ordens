package com.solinfbroker.msprocessamento.repository;

import com.solinfbroker.msprocessamento.model.CarteiraModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarteiraRepository extends JpaRepository<CarteiraModel,Long> {

    List<CarteiraModel> findByIdAtivo(Long idAtivo);
}
