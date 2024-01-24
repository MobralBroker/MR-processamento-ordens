package com.solinfbroker.msprocessamento.repository;

import com.solinfbroker.msprocessamento.model.CarteiraModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface CarteiraRepository extends JpaRepository<CarteiraModel,Long> {


    Set<CarteiraModel> findByIdAtivoAndIdClienteOrderByDataCompraAsc(Long idAtivo,Long idCliente);
}
