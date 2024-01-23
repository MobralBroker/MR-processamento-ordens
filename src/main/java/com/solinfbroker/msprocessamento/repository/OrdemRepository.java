package com.solinfbroker.msprocessamento.repository;


import com.solinfbroker.msprocessamento.model.Ordem;
import com.solinfbroker.msprocessamento.model.enumTipoOrdem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface OrdemRepository extends JpaRepository<Ordem,Long> {

    List<Ordem> findByTipoOrdem(enumTipoOrdem tipo);

    List<Ordem> findByIdCliente(Long idCliente);

    @Query(value = "SELECT * FROM ordem WHERE (status_ordem = 'ABERTA' OR status_ordem = 'EXECUTADA_PARCIAL') AND tipo_ordem = 'ORDEM_VENDA' ORDER BY data_lancamento ASC LIMIT 20;",nativeQuery = true)
    List<Ordem> findOrdemAbertaVenda();
    @Query(value = "SELECT * FROM ordem WHERE status_ordem = 'ABERTA' AND tipo_ordem = 'ORDEM_COMPRA' ORDER BY data_lancamento DESC LIMIT 20;",nativeQuery = true)
    List<Ordem> findOrdemAbertaCompra();

}
