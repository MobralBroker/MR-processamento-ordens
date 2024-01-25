package com.solinfbroker.msprocessamento.repository;

import com.solinfbroker.msprocessamento.model.CarteiraModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CarteiraRepository extends JpaRepository<CarteiraModel,Long> {


    Set<CarteiraModel> findByIdAtivoAndIdClienteOrderByDataCompraAsc(Long idAtivo,Long idCliente);
}
