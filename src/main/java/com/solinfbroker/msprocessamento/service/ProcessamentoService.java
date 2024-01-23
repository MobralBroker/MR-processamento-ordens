package com.solinfbroker.msprocessamento.service;

import com.solinfbroker.msprocessamento.dtos.OrdemKafka;
import com.solinfbroker.msprocessamento.model.*;
import com.solinfbroker.msprocessamento.repository.CarteiraRepository;
import com.solinfbroker.msprocessamento.repository.ClienteRepository;
import com.solinfbroker.msprocessamento.repository.OperacaoRepository;
import com.solinfbroker.msprocessamento.repository.OrdemRepository;
import lombok.AllArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProcessamentoService {

    private final OrdemRepository ordemRepository;
    private final OperacaoRepository operacaoRepository;
    private final ClienteRepository clienteRepository;
    private final CarteiraRepository carteiraRepository;
    public void processarOrdemCompra(OrdemKafka ordemKafka){
        int indexControle = 0;
        while(indexControle < 5 ){
            Optional<Ordem> ordemCompra = ordemRepository.findById(Long.valueOf(ordemKafka.getId()));
            List<Ordem> ordensVendasAbertas = ordemRepository.findOrdemAbertaVenda();
            if(ordemCompra.isPresent()){
                for (Ordem ordensVendasAberta : ordensVendasAbertas) { //Adicionar esta validação no if abaixo || ordemCompra.get().getCliente().getId() == ordensVendasAberta.getId()
                    if (ordemCompra.get().getStatusOrdem().equals(enumStatus.EXECUTADA) || ordensVendasAberta.getValorOrdem() != ordemCompra.get().getValorOrdem()) { //TODO tratar quando o valor da ordem de compra for menor que a ordem de venda
                        return;
                    }
                    if (ordensVendasAberta.getValorOrdem() == ordemCompra.get().getValorOrdem()) {
                        Operacao operacao = new Operacao();
                        operacao.setDataExecucao(LocalDateTime.now());

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

                        operacao.setStatusOperacao(enumStatus.EXECUTADA);
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

                        Optional<ClienteModel> clienteModel = clienteRepository.findById(ordemCompra.get().getIdCliente());
                        operacao.setOrdemCompra(ordemCompra.get());
                        operacao.setOrdemVenda(ordensVendasAberta);

//                        if(indexControle < 3){
//                            criarConcorrencia(ordemCompra.get().getId());
//                            System.out.println("Criar concorrencia : "+indexControle);
//                        }

                        indexControle = salvarDados(ordemCompra.get(),ordensVendasAberta,operacao,clienteModel,indexControle);

                        System.out.println("ORDEM EXECUTADA");
                    }
                }

            }

        }
    }

    public void processarOrdemVenda(OrdemKafka ordemKafka){
        List<Ordem> ordensAbertas = ordemRepository.findOrdemAbertaCompra();
        for (Ordem ordemAberta:ordensAbertas){
            if(ordemAberta.getValorOrdem() == ordemKafka.getValorOrdem()){
                System.out.println("executar ordem de venda");
            }
        }
    }

    public Integer salvarDados(Ordem ordemCompra, Ordem ordemVenda, Operacao operacao, Optional<ClienteModel> clienteModel, int indexControle){
        try {
            if (clienteModel.isPresent()) {
                clienteModel.get().setValorBloqueado(clienteModel.get().getValorBloqueado() - (operacao.getQuantidade() * ordemCompra.getValorOrdem()));
                clienteRepository.save(clienteModel.get());
            }
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            ordemRepository.save(ordemCompra);
            ordemRepository.save(ordemVenda);
            operacao = operacaoRepository.save(operacao);
            adicionarPapeisCarteira(operacao, ordemCompra);
            removerPapeisCarteira(operacao,ordemCompra);
            indexControle = 5;
            return indexControle;
        }catch (ObjectOptimisticLockingFailureException e){
            indexControle = indexControle +1;
            return indexControle;
        }
    }

    public void adicionarPapeisCarteira(Operacao operacao, Ordem ordemCompra){
        CarteiraModel carteiraModel = new CarteiraModel();
        carteiraModel.setQuantidade(operacao.getQuantidade());
        carteiraModel.setAtivo(ordemCompra.getAtivo());
        carteiraModel.setCliente(ordemCompra.getCliente());
        carteiraModel.setDataCompra(operacao.getDataExecucao());
        carteiraModel.setQuantidade(operacao.getQuantidade());
        carteiraRepository.save(carteiraModel);
    }

    public void removerPapeisCarteira(Operacao operacao, Ordem ordemVenda){
        List<CarteiraModel> carteiralist = carteiraRepository.findByIdAtivo(ordemVenda.getAtivo().getId());
        for(CarteiraModel carteiraModel: carteiralist){
            if(carteiraModel.getQuantidade() < operacao.getQuantidade()){
                carteiraModel.setQuantidade(carteiraModel.getQuantidade() - operacao.getQuantidade());
            }else{
                operacao.setQuantidade(operacao.getQuantidade() - carteiraModel.getQuantidade());
                carteiraRepository.delete(carteiraModel);
            }

        }
    }

    public void criarConcorrencia(Long id){
        new Thread(() -> {
            Ordem ordem = ordemRepository.findById(id).get();
            ordem.setStatusOrdem(enumStatus.CANCELADA);
            try {
                ordemRepository.save(ordem);
            }catch (ObjectOptimisticLockingFailureException ex){
                System.out.println("opa");
            }
        }).start();
    }


}
