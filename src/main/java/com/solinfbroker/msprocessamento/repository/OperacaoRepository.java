package com.solinfbroker.msprocessamento.repository;

import com.solinfbroker.msprocessamento.model.Operacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface OperacaoRepository extends JpaRepository<Operacao,Long> {

    @Query(value = "select op.id as op_id,"+
            "op.quantidade as op_quantidade,"+
            "op.data_execucao as op_data_execucao,"+
            "op.status_operacao as op_status_operacao,"+
            "ord.tipo_ordem as ord_tipo_ordem "+
            "from operacao op "+
            "inner join ordem ord on ord.id = op.id_venda where op.id_venda = ?1 ", nativeQuery = true)
            Set<Optional<Object[]>> findByIdVenda(Long id);


    @Query(value = "select op.id as op_id,"+
            "op.quantidade as op_quantidade,"+
            "op.data_execucao as op_data_execucao,"+
            "op.status_operacao as op_status_operacao,"+
            "ord.tipo_ordem as ord_tipo_ordem "+
            "from operacao op "+
            "inner join ordem ord on ord.id = op.id_compra where op.id_compra = ?1", nativeQuery = true)
    Set<Object[]> findByIdCompra(Long id);
}
