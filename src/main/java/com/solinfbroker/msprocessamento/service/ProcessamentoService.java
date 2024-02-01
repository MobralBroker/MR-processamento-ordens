package com.solinfbroker.msprocessamento.service;

import com.solinfbroker.msprocessamento.dtos.OrdemKafka;
import com.solinfbroker.msprocessamento.model.*;
import com.solinfbroker.msprocessamento.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class ProcessamentoService {

    private final OrdemRepository ordemRepository;
    private final OperacaoRepository operacaoRepository;
    private final ClienteRepository clienteRepository;
    private final CarteiraRepository carteiraRepository;
    private final AtivoRepository ativoRepository;
    public void processarOrdemCompra(OrdemKafka ordemKafka){
        int indexControle = 0;
        while(indexControle < 5 ){
            Optional<Ordem> ordemCompra = ordemRepository.findById(Long.valueOf(ordemKafka.getId()));
            List<Ordem> ordensVendasAbertas = ordemRepository.findOrdemAbertaVenda(ordemCompra.get().getIdAtivo());
            if(ordemCompra.isPresent() && !ordensVendasAbertas.isEmpty()){
                for (Ordem ordensVendasAberta : ordensVendasAbertas) { //Adicionar esta validação no if abaixo || ordemCompra.get().getCliente().getId() == ordensVendasAberta.getId()
                    Operacao operacao = new Operacao();
                    if (ordemCompra.get().getStatusOrdem().equals(enumStatus.EXECUTADA) || ordemCompra.get().getValorOrdem() < ordensVendasAberta.getValorOrdem()) {
                        return;
                    }


                    //Verificar qual é a quantidade que irá utilizar
                    if (ordensVendasAberta.getQuantidadeAberto() <= ordemCompra.get().getQuantidadeAberto()) {
                        operacao.setQuantidade(ordensVendasAberta.getQuantidadeAberto());
                        ordemCompra.get().setQuantidadeAberto(ordemCompra.get().getQuantidadeAberto() - ordensVendasAberta.getQuantidadeAberto()); //atualiza a quantidade da ordem de compra
                        ordensVendasAberta.setQuantidadeAberto(0);//atualiza a quantidade da ordem de venda em Aberto
                    } else {
                        operacao.setQuantidade(ordemCompra.get().getQuantidadeAberto());
                        ordensVendasAberta.setQuantidadeAberto(ordensVendasAberta.getQuantidadeAberto() - ordemCompra.get().getQuantidadeAberto());//atualiza a quantidade da ordem de venda em Aberto
                        ordemCompra.get().setQuantidadeAberto(0); //atualiza a quantidade da ordem de compra
                    }


                    if (ordemCompra.get().getQuantidadeAberto() > 0) {
                        ordemCompra.get().setStatusOrdem(enumStatus.EXECUTADA_PARCIAL);
                    } else {
                        ordemCompra.get().setStatusOrdem(enumStatus.EXECUTADA);
                    }

                    if (ordensVendasAberta.getQuantidadeAberto() > 0) {
                        ordensVendasAberta.setStatusOrdem(enumStatus.EXECUTADA_PARCIAL);
                    } else {
                        ordensVendasAberta.setStatusOrdem(enumStatus.EXECUTADA);
                    }

                    if (ordemCompra.get().getValorOrdem() == ordensVendasAberta.getValorOrdem() ) {
                        operacao.setValorAtivoExecucao(ordemCompra.get().getValorOrdem());
                    }else if(ordemCompra.get().getValorOrdem() > ordensVendasAberta.getValorOrdem()){
                        operacao.setValorAtivoExecucao(ordensVendasAberta.getValorOrdem());
                    }

                    operacao.setOrdemCompra(ordemCompra.get());
                    operacao.setOrdemVenda(ordensVendasAberta);
                    operacao.setDataExecucao(LocalDateTime.now());
                    operacao.setStatusOperacao(enumStatus.EXECUTADA);

                    indexControle = salvarDados(ordemCompra.get(),ordensVendasAberta,operacao,indexControle);


                }
            }else {
                indexControle = 5;
            }

        }
    }

    public void processarOrdemVenda(OrdemKafka ordemKafka){
        int indexControle = 0;
        while(indexControle < 5 ){
            Optional<Ordem> ordemVenda = ordemRepository.findById(Long.valueOf(ordemKafka.getId()));
            List<Ordem> ordensCompraAbertaList = ordemRepository.findOrdemAbertaCompra(ordemVenda.get().getIdAtivo());
            if(ordemVenda.isPresent() && !ordensCompraAbertaList.isEmpty()){
                for (Ordem ordensCompraAberta : ordensCompraAbertaList) { //Adicionar esta validação no if abaixo || ordemCompra.get().getCliente().getId() == ordensVendasAberta.getId()
                    if (ordemVenda.get().getStatusOrdem().equals(enumStatus.EXECUTADA) || ordemVenda.get().getValorOrdem() > ordensCompraAberta.getValorOrdem()) {
                        return;
                    }
                    Operacao operacao = new Operacao();

                    //Verificar qual é a quantidade que irá utilizar

                    if (ordensCompraAberta.getQuantidadeAberto() <= ordemVenda.get().getQuantidadeAberto()) {
                        operacao.setQuantidade(ordensCompraAberta.getQuantidadeAberto());
                        ordemVenda.get().setQuantidadeAberto(ordemVenda.get().getQuantidadeAberto() - ordensCompraAberta.getQuantidadeAberto()); //atualiza a quantidade da ordem de compra
                        ordensCompraAberta.setQuantidadeAberto(0);//atualiza a quantidade da ordem de venda em Aberto
                    } else {
                        operacao.setQuantidade(ordemVenda.get().getQuantidadeAberto());
                        ordensCompraAberta.setQuantidadeAberto(ordensCompraAberta.getQuantidadeAberto() - ordemVenda.get().getQuantidadeAberto());//atualiza a quantidade da ordem de venda em Aberto
                        ordemVenda.get().setQuantidadeAberto(0); //atualiza a quantidade da ordem de compra
                    }


                    if (ordemVenda.get().getQuantidadeAberto() > 0) {
                        ordemVenda.get().setStatusOrdem(enumStatus.EXECUTADA_PARCIAL);
                    } else {
                        ordemVenda.get().setStatusOrdem(enumStatus.EXECUTADA);
                    }

                    if (ordensCompraAberta.getQuantidadeAberto() > 0) {
                        ordensCompraAberta.setStatusOrdem(enumStatus.EXECUTADA_PARCIAL);
                    } else {
                        ordensCompraAberta.setStatusOrdem(enumStatus.EXECUTADA);
                    }

                    if (ordemVenda.get().getValorOrdem() == ordensCompraAberta.getValorOrdem() ) {
                        operacao.setValorAtivoExecucao(ordemVenda.get().getValorOrdem());
                    }else if(ordemVenda.get().getValorOrdem() < ordensCompraAberta.getValorOrdem()){
                        operacao.setValorAtivoExecucao(ordensCompraAberta.getValorOrdem());
                    }

                    operacao.setOrdemCompra(ordensCompraAberta);
                    operacao.setOrdemVenda(ordemVenda.get());
                    operacao.setDataExecucao(LocalDateTime.now());
                    operacao.setStatusOperacao(enumStatus.EXECUTADA);

                    indexControle = salvarDados(ordensCompraAberta,ordemVenda.get(),operacao,indexControle);

                }
            }else {
                indexControle = 5;
            }

        }
    }

    @Transactional
    public Integer salvarDados(Ordem ordemCompra, Ordem ordemVenda, Operacao operacao, int indexControle){
        try {
            ordemCompra = ordemRepository.save(ordemCompra);
            ordemVenda = ordemRepository.save(ordemVenda);
            operacao = operacaoRepository.save(operacao);
            atualizarAtivo(operacao);
            adicionarPapeisCarteira(operacao, ordemCompra);
            removerPapeisCarteira(operacao,ordemVenda);
            indexControle = 5;
            return indexControle;
        }catch (ObjectOptimisticLockingFailureException e){

            indexControle = indexControle +1;
            return indexControle;
        }
    }

    public void atualizarAtivo(Operacao operacao){
        Optional<AtivoModel> ativoModel = ativoRepository.findById(operacao.getOrdemCompra().getIdAtivo());
        if(ativoModel.isPresent()){
            if(ativoModel.get().getAtualizacao().toLocalDate().isBefore(LocalDate.now())){
                ativoModel.get().setValorMin(operacao.getValorAtivoExecucao());
                ativoModel.get().setValorMax(operacao.getValorAtivoExecucao());
                ativoModel.get().setValor(operacao.getValorAtivoExecucao());
                ativoModel.get().setAtualizacao(LocalDateTime.now());
            }
            if(operacao.getValorAtivoExecucao() > ativoModel.get().getValorMax()) {
                ativoModel.get().setValorMax(operacao.getValorAtivoExecucao());
                ativoModel.get().setValor(operacao.getValorAtivoExecucao());
            } else if (operacao.getValorAtivoExecucao() < ativoModel.get().getValorMin()){
                ativoModel.get().setValorMin(operacao.getValorAtivoExecucao());
                ativoModel.get().setValor(operacao.getValorAtivoExecucao());
            } else {
                ativoModel.get().setValorMax(operacao.getValorAtivoExecucao());
                ativoModel.get().setValor(operacao.getValorAtivoExecucao());
                ativoModel.get().setValorMin(operacao.getValorAtivoExecucao());
            }
            ativoRepository.save(ativoModel.get());
        }
    }

    @Transactional
    public void adicionarPapeisCarteira(Operacao operacao, Ordem ordemCompra){
        Optional<ClienteModel> clienteCompra = clienteRepository.findById(ordemCompra.getIdCliente());
        double valorCompra = operacao.getQuantidade() * operacao.getValorAtivoExecucao();
        double valorBloqueado = operacao.getQuantidade() * ordemCompra.getValorOrdem() ;
        double valorDesbloquear = valorBloqueado - valorCompra;

        if(clienteCompra.isPresent()){
            clienteCompra.get().setValorBloqueado(clienteCompra.get().getValorBloqueado() - valorBloqueado);
            clienteCompra.get().setSaldo(clienteCompra.get().getSaldo() + valorDesbloquear);
            clienteRepository.save(clienteCompra.get());
        }
        CarteiraModel carteiraModel = new CarteiraModel();
        carteiraModel.setQuantidade(operacao.getQuantidade());
        carteiraModel.setIdAtivo(ordemCompra.getAtivo().getId());
        carteiraModel.setIdCliente(ordemCompra.getCliente().getId());
        carteiraModel.setDataCompra(operacao.getDataExecucao());
        carteiraModel.setQuantidadeBloqueada(0);
        carteiraRepository.save(carteiraModel);
    }

    @Transactional
    public void removerPapeisCarteira(Operacao operacao, Ordem ordemVenda){
        Optional<ClienteModel> clienteVenda = clienteRepository.findById(ordemVenda.getIdCliente());
        double valorVenda = operacao.getQuantidade() * operacao.getValorAtivoExecucao();

        if(clienteVenda.isPresent()){
            clienteVenda.get().setSaldo(clienteVenda.get().getSaldo() + valorVenda);
            clienteRepository.save(clienteVenda.get());
//            Set<CarteiraModel> carteiras = carteiraRepository.findByIdAtivoAndIdClienteOrderByDataCompraAsc(ordemVenda.getAtivo().getId(),clienteVenda.get().getId());
            Set<CarteiraModel> carteiras =carteiraRepository.findByIdClienteAndIdAtivoOrderByQuantidadeBloqueadaDesc(clienteVenda.get().getId(),ordemVenda.getAtivo().getId());
            if(!carteiras.isEmpty()){
                Iterator<CarteiraModel> iter = carteiras.iterator();
                Integer quantidadeTotal = operacao.getQuantidade();
                while(iter.hasNext() && operacao.getQuantidade() > 0){
                    CarteiraModel carteiraModel = iter.next();
                    if(carteiraModel.getQuantidadeBloqueada() > operacao.getQuantidade()){
                        carteiraModel.setQuantidadeBloqueada(carteiraModel.getQuantidadeBloqueada() - operacao.getQuantidade());
                        carteiraRepository.save(carteiraModel);
                    }else{
                        operacao.setQuantidade(operacao.getQuantidade() - carteiraModel.getQuantidadeBloqueada());
                        carteiraRepository.delete(carteiraModel);
                    }
                }
                Set<CarteiraModel> carteirasDicp =carteiraRepository.findByIdClienteAndIdAtivoOrderByQuantidadeDesc(clienteVenda.get().getId(),ordemVenda.getAtivo().getId());
                Iterator<CarteiraModel> carteirasDicpIter = carteiras.iterator();
                while(carteirasDicpIter.hasNext() && quantidadeTotal > 0){
                    CarteiraModel carteiraModel = carteirasDicpIter.next();
                    if(carteiraModel.getQuantidade() > quantidadeTotal){
                        carteiraModel.setQuantidade(carteiraModel.getQuantidade() - quantidadeTotal);
                        carteiraRepository.save(carteiraModel);
                    }else{
                        quantidadeTotal = quantidadeTotal - carteiraModel.getQuantidade();
                        carteiraRepository.delete(carteiraModel);
                    }
                }
            }
        }
    }

}
