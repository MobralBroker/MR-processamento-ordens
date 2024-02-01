package com.solinfbroker.msprocessamento.service;

import com.solinfbroker.msprocessamento.dtos.OrdemKafka;
import com.solinfbroker.msprocessamento.model.*;
import com.solinfbroker.msprocessamento.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
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

    @Mock
    AtivoRepository ativoRepository;
    @InjectMocks
    private ProcessamentoService processamentoService;

    @Test
    void processarOrdemCompraErroExecutada() {
        OrdemKafka ordemKafka = mock(OrdemKafka.class);
        Optional<OrdemKafka> ordemKafkaOpt = Optional.of(ordemKafka);

        List<Ordem> ordensVendasAberta = new ArrayList<>();
        Ordem ordem = new Ordem();
        ordem.setIdAtivo(1L);
        ordem.setStatusOrdem(enumStatus.EXECUTADA);
        Optional<Ordem> ordemOpt = Optional.of(ordem);
        ordensVendasAberta.add(ordem);
        when(ordemRepository.findOrdemAbertaVenda(anyLong())).thenReturn(ordensVendasAberta);
        when(ordemRepository.findById(anyLong())).thenReturn(ordemOpt);

        processamentoService.processarOrdemCompra(ordemKafka);

        verify(ordemRepository, times(1)).findOrdemAbertaVenda(anyLong());
        verify(ordemRepository, times(1)).findById(anyLong());
    }

    @Test
    void processarOrdemCompraCompletaCaso1() {
        OrdemKafka ordemKafka = mock(OrdemKafka.class);
        ordemKafka.setIdAtivo(1L);
        Optional<OrdemKafka> ordemKafkaOpt = Optional.of(ordemKafka);
        List<Ordem> ordensVendasAberta = new ArrayList<>();
        Ordem ordemVenda = new Ordem();
        ordemVenda.setId(1L);
        ordemVenda.setStatusOrdem(enumStatus.ABERTA);
        ordemVenda.setQuantidadeAberto(1);
        ordemVenda.setValorOrdem(10);
        ordemVenda.setIdAtivo(1L);

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
        ativo.setValor(1d);
        ativo.setAtualizacao(LocalDateTime.now());
        ativo.setValorMin(1);
        ativo.setValorMax( 20);
        Optional<AtivoModel> ativoModelOpt = Optional.of(ativo);
        ordemCompra.setAtivo(ativo);
        ordemCompra.setCliente(clienteCompra);
        ordemCompra.setIdAtivo(1L);

        Optional<Ordem> ordemCompraOpt = Optional.of(ordemCompra);
        ordensVendasAberta.add(ordemVenda);

        Operacao operacao = mock(Operacao.class);
        operacao.setOrdemCompra(ordemCompra);

        CarteiraModel carteiraModel = mock(CarteiraModel.class);
        carteiraModel.setQuantidade(10);
        Set<CarteiraModel> carteiraModels = new HashSet<>();
        carteiraModels.add(carteiraModel);



        int indexControle = 0;

        when(ordemRepository.findOrdemAbertaVenda(anyLong())).thenReturn(ordensVendasAberta);
        when(ordemRepository.findById(anyLong())).thenReturn(ordemCompraOpt);
        when(clienteRepository.findById(anyLong())).thenReturn(clienteCompraOpt);
        when(ordemRepository.save(any())).thenReturn(ordemCompra);
        when(operacaoRepository.save(any())).thenReturn(operacao);
        when(operacao.getQuantidade()).thenReturn(1);
        when(ativoRepository.findById(anyLong())).thenReturn(ativoModelOpt);
        when(operacao.getOrdemCompra()).thenReturn(ordemCompra);
        when(ativoModelOpt.get().getAtualizacao()).thenReturn(LocalDateTime.now().minusDays(1));
        when((carteiraRepository.findByIdClienteAndIdAtivoOrderByQuantidadeBloqueadaDesc(anyLong(),anyLong()))).thenReturn(carteiraModels);

        processamentoService.processarOrdemCompra(ordemKafka);

        verify(ordemRepository, times(1)).findOrdemAbertaVenda(anyLong());
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
        ordemCompra.setIdAtivo(1l);
        ordemCompra.setIdCliente(1L);
        ordemCompra.setAtivo(ativo);
        ordemCompra.setCliente(clienteCompra);

        Optional<Ordem> ordemCompraOpt = Optional.of(ordemCompra);
        ordensVendasAberta.add(ordemVenda);

        Operacao operacao = mock(Operacao.class);
        operacao.setOrdemCompra(ordemCompra);
        CarteiraModel carteiraModel = mock(CarteiraModel.class);
        carteiraModel.setQuantidade(10);
        Set<CarteiraModel> carteiraModels = new HashSet<>();
        carteiraModels.add(carteiraModel);

        int indexControle = 0;

        when(ordemRepository.findOrdemAbertaVenda(anyLong())).thenReturn(ordensVendasAberta);
        when(ordemRepository.findById(anyLong())).thenReturn(ordemCompraOpt);
        when(clienteRepository.findById(anyLong())).thenReturn(clienteCompraOpt);
        when(ordemRepository.save(any())).thenReturn(ordemCompra);
        when(operacaoRepository.save(any())).thenReturn(operacao);
//        when(carteiraRepository.findByIdAtivoAndIdClienteOrderByDataCompraAsc(anyLong(),anyLong())).thenReturn(carteiraModels);
        //        when(ordemOpt.get().getStatusOrdem().equals(enumStatus.EXECUTADA)).thenReturn(enumStatus.EXECUTADA);
        when(operacao.getQuantidade()).thenReturn(12);
        when(operacao.getOrdemCompra()).thenReturn(ordemCompra);

        processamentoService.processarOrdemCompra(ordemKafka);

        verify(ordemRepository, times(1)).findOrdemAbertaVenda(anyLong());
        verify(ordemRepository, times(1)).findById(anyLong());
        verify(clienteRepository, times(2)).findById(anyLong());
        verify(clienteRepository,times(2)).save(any());
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
        when(ordemRepository.findOrdemAbertaCompra(anyLong())).thenReturn(ordensCompraAberta);
        when(ordemRepository.findById(anyLong())).thenReturn(ordemOpt);

        processamentoService.processarOrdemVenda(ordemKafka);

        verify(ordemRepository, times(1)).findOrdemAbertaCompra(anyLong());
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

        when(ordemRepository.findOrdemAbertaCompra(anyLong())).thenReturn(ordensCompraAberta);
        when(ordemRepository.findById(anyLong())).thenReturn(ordemVendaOpt);
        when(clienteRepository.findById(anyLong())).thenReturn(clienteVendaOpt);
        when(ordemRepository.save(any())).thenReturn(ordemVenda);
        when(operacaoRepository.save(any())).thenReturn(operacao);
        when(carteiraRepository.findByIdAtivoAndIdClienteOrderByDataCompraAsc(anyLong(),anyLong())).thenReturn(carteiraModels);
        //        when(ordemOpt.get().getStatusOrdem().equals(enumStatus.EXECUTADA)).thenReturn(enumStatus.EXECUTADA);
        when(operacao.getQuantidade()).thenReturn(1);

        processamentoService.processarOrdemVenda(ordemKafka);

        verify(ordemRepository, times(1)).findOrdemAbertaCompra(anyLong());
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

        when(ordemRepository.findOrdemAbertaCompra(anyLong())).thenReturn(ordensCompraAberta);
        when(ordemRepository.findById(anyLong())).thenReturn(ordemVendaOpt);
        when(clienteRepository.findById(anyLong())).thenReturn(clienteVendaOpt);
        when(ordemRepository.save(any())).thenReturn(ordemVenda);
        when(operacaoRepository.save(any())).thenReturn(operacao);
        when(carteiraRepository.findByIdAtivoAndIdClienteOrderByDataCompraAsc(anyLong(),anyLong())).thenReturn(carteiraModels);
        when(operacao.getQuantidade()).thenReturn(12);

        processamentoService.processarOrdemVenda(ordemKafka);

        verify(ordemRepository, times(1)).findOrdemAbertaCompra(anyLong());
        verify(ordemRepository, times(1)).findById(anyLong());
        verify(clienteRepository, times(2)).findById(anyLong());
        verify(clienteRepository,times(2)).save(any());
        verify(carteiraRepository,times(1)).delete(any());
        verify(carteiraRepository,times(1)).save(any());
    }
}