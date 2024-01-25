package com.solinfbroker.msprocessamento.service;

import com.solinfbroker.msprocessamento.dtos.OrdemKafka;
import com.solinfbroker.msprocessamento.model.*;
import com.solinfbroker.msprocessamento.repository.CarteiraRepository;
import com.solinfbroker.msprocessamento.repository.ClienteRepository;
import com.solinfbroker.msprocessamento.repository.OperacaoRepository;
import com.solinfbroker.msprocessamento.repository.OrdemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ProcessamentoServiceTest {
    @Mock
    private OrdemRepository ordemRepository;
    @Mock
    private OperacaoRepository operacaoRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private CarteiraRepository carteiraRepository;
    @InjectMocks
    private ProcessamentoService processamentoService;

    @Test
    void processarOrdemCompraErroExecutada() {
        OrdemKafka ordemKafka = mock(OrdemKafka.class);
        Optional<OrdemKafka> ordemKafkaOpt = Optional.of(ordemKafka);

        List<Ordem> ordensVendasAberta = new ArrayList<>();
        Ordem ordem = new Ordem();
        ordem.setStatusOrdem(enumStatus.EXECUTADA);
        Optional<Ordem> ordemOpt = Optional.of(ordem);
        ordensVendasAberta.add(ordem);
        when(ordemRepository.findOrdemAbertaVenda()).thenReturn(ordensVendasAberta);
        when(ordemRepository.findById(anyLong())).thenReturn(ordemOpt);

        processamentoService.processarOrdemCompra(ordemKafka);

        verify(ordemRepository, times(1)).findOrdemAbertaVenda();
        verify(ordemRepository, times(1)).findById(anyLong());
    }

    @Test
    void processarOrdemCompraCompletaCaso1() {
        OrdemKafka ordemKafka = mock(OrdemKafka.class);
        Optional<OrdemKafka> ordemKafkaOpt = Optional.of(ordemKafka);

        List<Ordem> ordensVendasAberta = new ArrayList<>();

        Ordem ordemVenda = new Ordem();
        ordemVenda.setId(1L);
        ordemVenda.setStatusOrdem(enumStatus.ABERTA);
        ordemVenda.setQuantidadeAberto(1);
        ordemVenda.setValorOrdem(10);


        ClienteModel clienteCompra = mock(ClienteModel.class);
        Optional<ClienteModel> clienteCompraOpt = Optional.of(clienteCompra);
        clienteCompraOpt.get().setId(1L);


        Ordem ordemCompra = new Ordem();
        ordemCompra.setId(1L);
        ordemCompra.setQuantidadeAberto(1);
        ordemCompra.setValorOrdem(10);
        ordemCompra.setStatusOrdem(enumStatus.ABERTA);
        ordemCompra.setIdCliente(1L);
        AtivoModel ativo = mock(AtivoModel.class);
        ativo.setId(1l);
        ordemCompra.setAtivo(ativo);
        ordemCompra.setCliente(clienteCompra);

        Optional<Ordem> ordemCompraOpt = Optional.of(ordemCompra);
        ordensVendasAberta.add(ordemVenda);

        Operacao operacao = mock(Operacao.class);

        CarteiraModel carteiraModel = mock(CarteiraModel.class);
        carteiraModel.setQuantidade(10);
        Set<CarteiraModel> carteiraModels = new HashSet<>();
        carteiraModels.add(carteiraModel);

        int indexControle = 0;

        when(ordemRepository.findOrdemAbertaVenda()).thenReturn(ordensVendasAberta);
        when(ordemRepository.findById(anyLong())).thenReturn(ordemCompraOpt);
        when(clienteRepository.findById(anyLong())).thenReturn(clienteCompraOpt);
        when(ordemRepository.save(any())).thenReturn(ordemCompra);
        when(operacaoRepository.save(any())).thenReturn(operacao);
        when(carteiraRepository.findByIdAtivoAndIdClienteOrderByDataCompraAsc(anyLong(),anyLong())).thenReturn(carteiraModels);
        when(operacao.getQuantidade()).thenReturn(1);

        processamentoService.processarOrdemCompra(ordemKafka);

        verify(ordemRepository, times(1)).findOrdemAbertaVenda();
        verify(ordemRepository, times(1)).findById(anyLong());
        verify(clienteRepository, times(2)).findById(anyLong());
        verify(clienteRepository,times(2)).save(any());
        verify(carteiraRepository,times(1)).delete(any());
        verify(carteiraRepository,times(1)).save(any());
    }

    @Test
    void processarOrdemCompraCompletaCaso2() {
        OrdemKafka ordemKafka = mock(OrdemKafka.class);
        Optional<OrdemKafka> ordemKafkaOpt = Optional.of(ordemKafka);

        List<Ordem> ordensVendasAberta = new ArrayList<>();

        Ordem ordemVenda = new Ordem();
        ordemVenda.setId(1L);
        ordemVenda.setStatusOrdem(enumStatus.ABERTA);
        ordemVenda.setQuantidadeAberto(2);
        ordemVenda.setValorOrdem(10);


        ClienteModel clienteCompra = mock(ClienteModel.class);
        Optional<ClienteModel> clienteCompraOpt = Optional.of(clienteCompra);
        clienteCompraOpt.get().setId(1L);


        Ordem ordemCompra = new Ordem();
        ordemCompra.setId(1L);
        ordemCompra.setQuantidadeAberto(1);
        ordemCompra.setValorOrdem(11);
        ordemCompra.setStatusOrdem(enumStatus.ABERTA);
        ordemCompra.setIdCliente(1L);
        AtivoModel ativo = mock(AtivoModel.class);
        ativo.setId(1l);
        ordemCompra.setAtivo(ativo);
        ordemCompra.setCliente(clienteCompra);

        Optional<Ordem> ordemCompraOpt = Optional.of(ordemCompra);
        ordensVendasAberta.add(ordemVenda);

        Operacao operacao = mock(Operacao.class);

        CarteiraModel carteiraModel = mock(CarteiraModel.class);
        carteiraModel.setQuantidade(10);
        Set<CarteiraModel> carteiraModels = new HashSet<>();
        carteiraModels.add(carteiraModel);

        int indexControle = 0;

        when(ordemRepository.findOrdemAbertaVenda()).thenReturn(ordensVendasAberta);
        when(ordemRepository.findById(anyLong())).thenReturn(ordemCompraOpt);
        when(clienteRepository.findById(anyLong())).thenReturn(clienteCompraOpt);
        when(ordemRepository.save(any())).thenReturn(ordemCompra);
        when(operacaoRepository.save(any())).thenReturn(operacao);
        when(carteiraRepository.findByIdAtivoAndIdClienteOrderByDataCompraAsc(anyLong(),anyLong())).thenReturn(carteiraModels);
        //        when(ordemOpt.get().getStatusOrdem().equals(enumStatus.EXECUTADA)).thenReturn(enumStatus.EXECUTADA);
        when(operacao.getQuantidade()).thenReturn(12);

        processamentoService.processarOrdemCompra(ordemKafka);

        verify(ordemRepository, times(1)).findOrdemAbertaVenda();
        verify(ordemRepository, times(1)).findById(anyLong());
        verify(clienteRepository, times(2)).findById(anyLong());
        verify(clienteRepository,times(2)).save(any());
        verify(carteiraRepository,times(1)).delete(any());
        verify(carteiraRepository,times(1)).save(any());
    }



    @Test
    void processarOrdemVendaErroExecutada() {
        OrdemKafka ordemKafka = mock(OrdemKafka.class);
        Optional<OrdemKafka> ordemKafkaOpt = Optional.of(ordemKafka);

        List<Ordem> ordensCompraAberta = new ArrayList<>();
        Ordem ordem = new Ordem();
        ordem.setStatusOrdem(enumStatus.EXECUTADA);
        Optional<Ordem> ordemOpt = Optional.of(ordem);
        ordensCompraAberta.add(ordem);
        when(ordemRepository.findOrdemAbertaCompra()).thenReturn(ordensCompraAberta);
        when(ordemRepository.findById(anyLong())).thenReturn(ordemOpt);

        processamentoService.processarOrdemVenda(ordemKafka);

        verify(ordemRepository, times(1)).findOrdemAbertaCompra();
        verify(ordemRepository, times(1)).findById(anyLong());
    }

    @Test
    void processarOrdemVendaCompletaCaso1() {
        OrdemKafka ordemKafka = mock(OrdemKafka.class);
        Optional<OrdemKafka> ordemKafkaOpt = Optional.of(ordemKafka);

        List<Ordem> ordensCompraAberta = new ArrayList<>();

        Ordem ordemCompra = new Ordem();
        ordemCompra.setId(1L);
        ordemCompra.setStatusOrdem(enumStatus.ABERTA);
        ordemCompra.setQuantidadeAberto(1);
        ordemCompra.setValorOrdem(10);


        ClienteModel clienteVenda = mock(ClienteModel.class);
        Optional<ClienteModel> clienteVendaOpt = Optional.of(clienteVenda);
        clienteVendaOpt.get().setId(1L);


        Ordem ordemVenda = new Ordem();
        ordemVenda.setId(1L);
        ordemVenda.setQuantidadeAberto(1);
        ordemVenda.setValorOrdem(10);
        ordemVenda.setStatusOrdem(enumStatus.ABERTA);
        ordemVenda.setIdCliente(1L);
        AtivoModel ativo = mock(AtivoModel.class);
        ativo.setId(1l);
        ordemVenda.setAtivo(ativo);
        ordemVenda.setCliente(clienteVenda);

        Optional<Ordem> ordemVendaOpt = Optional.of(ordemVenda);
        ordensCompraAberta.add(ordemCompra);

        Operacao operacao = mock(Operacao.class);

        CarteiraModel carteiraModel = mock(CarteiraModel.class);
        carteiraModel.setQuantidade(10);
        Set<CarteiraModel> carteiraModels = new HashSet<>();
        carteiraModels.add(carteiraModel);

        int indexControle = 0;

        when(ordemRepository.findOrdemAbertaCompra()).thenReturn(ordensCompraAberta);
        when(ordemRepository.findById(anyLong())).thenReturn(ordemVendaOpt);
        when(clienteRepository.findById(anyLong())).thenReturn(clienteVendaOpt);
        when(ordemRepository.save(any())).thenReturn(ordemVenda);
        when(operacaoRepository.save(any())).thenReturn(operacao);
        when(carteiraRepository.findByIdAtivoAndIdClienteOrderByDataCompraAsc(anyLong(),anyLong())).thenReturn(carteiraModels);
        //        when(ordemOpt.get().getStatusOrdem().equals(enumStatus.EXECUTADA)).thenReturn(enumStatus.EXECUTADA);
        when(operacao.getQuantidade()).thenReturn(1);

        processamentoService.processarOrdemVenda(ordemKafka);

        verify(ordemRepository, times(1)).findOrdemAbertaCompra();
        verify(ordemRepository, times(1)).findById(anyLong());
        verify(clienteRepository, times(2)).findById(anyLong());
        verify(clienteRepository,times(2)).save(any());
        verify(carteiraRepository,times(1)).delete(any());
        verify(carteiraRepository,times(1)).save(any());
    }

    @Test
    void processarOrdemVendaCompletaCaso2() {
        OrdemKafka ordemKafka = mock(OrdemKafka.class);
        Optional<OrdemKafka> ordemKafkaOpt = Optional.of(ordemKafka);

        List<Ordem> ordensCompraAberta = new ArrayList<>();

        Ordem ordemCompra = new Ordem();
        ordemCompra.setId(1L);
        ordemCompra.setStatusOrdem(enumStatus.ABERTA);
        ordemCompra.setQuantidadeAberto(2);
        ordemCompra.setValorOrdem(10);


        ClienteModel clienteVenda = mock(ClienteModel.class);
        Optional<ClienteModel> clienteVendaOpt = Optional.of(clienteVenda);
        clienteVendaOpt.get().setId(1L);


        Ordem ordemVenda = new Ordem();
        ordemVenda.setId(1L);
        ordemVenda.setQuantidadeAberto(1);
        ordemVenda.setValorOrdem(11);
        ordemVenda.setStatusOrdem(enumStatus.ABERTA);
        ordemVenda.setIdCliente(1L);
        AtivoModel ativo = mock(AtivoModel.class);
        ativo.setId(1l);
        ordemVenda.setAtivo(ativo);
        ordemVenda.setCliente(clienteVenda);

        Optional<Ordem> ordemVendaOpt = Optional.of(ordemCompra);
        ordensCompraAberta.add(ordemVenda);

        Operacao operacao = mock(Operacao.class);

        CarteiraModel carteiraModel = mock(CarteiraModel.class);
        carteiraModel.setQuantidade(10);
        Set<CarteiraModel> carteiraModels = new HashSet<>();
        carteiraModels.add(carteiraModel);

        int indexControle = 0;

        when(ordemRepository.findOrdemAbertaCompra()).thenReturn(ordensCompraAberta);
        when(ordemRepository.findById(anyLong())).thenReturn(ordemVendaOpt);
        when(clienteRepository.findById(anyLong())).thenReturn(clienteVendaOpt);
        when(ordemRepository.save(any())).thenReturn(ordemVenda);
        when(operacaoRepository.save(any())).thenReturn(operacao);
        when(carteiraRepository.findByIdAtivoAndIdClienteOrderByDataCompraAsc(anyLong(),anyLong())).thenReturn(carteiraModels);
        when(operacao.getQuantidade()).thenReturn(12);

        processamentoService.processarOrdemVenda(ordemKafka);

        verify(ordemRepository, times(1)).findOrdemAbertaCompra();
        verify(ordemRepository, times(1)).findById(anyLong());
        verify(clienteRepository, times(2)).findById(anyLong());
        verify(clienteRepository,times(2)).save(any());
        verify(carteiraRepository,times(1)).delete(any());
        verify(carteiraRepository,times(1)).save(any());
    }
}