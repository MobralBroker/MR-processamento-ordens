package com.solinfbroker.msprocessamento.repository;

import com.solinfbroker.msprocessamento.model.CarteiraModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CarteiraRepository extends JpaRepository<CarteiraModel,Long> {


    Set<CarteiraModel> findByIdAtivoAndIdClienteOrderByDataCompraAsc(Long idAtivo,Long idCliente);

    @Query(value = "SELECT c FROM CarteiraModel c WHERE c.idCliente = :idCliente AND c.idAtivo = :idAtivo AND c.quantidadeBloqueada > 0 ORDER BY c.dataCompra ASC",nativeQuery = true)
    Set<CarteiraModel> findByClienteAtivoAndQuantidadeBloqueada(@Param("idCliente") Long idCliente, @Param("idAtivo") Long idAtivo);

    @Query("SELECT c FROM CarteiraModel c WHERE c.idCliente = :idCliente AND c.idAtivo = :idAtivo ORDER BY c.quantidadeBloqueada DESC")
    Set<CarteiraModel> findByIdClienteAndIdAtivoOrderByQuantidadeBloqueadaDesc(@Param("idCliente") Long idCliente, @Param("idAtivo") Long idAtivo);

    @Query("SELECT c FROM CarteiraModel c WHERE c.idCliente = :idCliente AND c.idAtivo = :idAtivo ORDER BY c.quantidade DESC")
    Set<CarteiraModel> findByIdClienteAndIdAtivoOrderByQuantidadeDesc(@Param("idCliente") Long idCliente, @Param("idAtivo") Long idAtivo);
}
