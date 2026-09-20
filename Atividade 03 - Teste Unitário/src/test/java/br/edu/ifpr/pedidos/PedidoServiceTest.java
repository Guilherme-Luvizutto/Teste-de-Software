package br.edu.ifpr.pedidos;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PedidoServiceTest {
    @Test
    void deveFecharPedidoDeClienteComumComFreteDoParanaEPagamentoAprovado() {
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);
        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> { cobrancas.add(total); return true; });
        ResultadoPedido resultado = service.fechar(pedido, cliente);
        assertAll(() -> assertEquals("PAGO", resultado.status()), () -> assertEquals(10_000L, resultado.subtotalCentavos()), () -> assertEquals(0L, resultado.descontoCentavos()), () -> assertEquals(1_200L, resultado.freteCentavos()), () -> assertEquals(11_200L, resultado.totalCentavos()), () -> assertEquals(List.of(11_200L), cobrancas));
    }
    @Test void rejeitaReferenciasNulas() {
        PedidoService s = new PedidoService(t -> true); Pedido p = new Pedido(List.of(new ItemPedido("A",1000,1,1,1000,false)),"PR",false,null); Cliente c = new Cliente(false,false,1);
        assertThrows(NullPointerException.class, () -> s.fechar(null,c));
        assertThrows(NullPointerException.class, () -> s.fechar(p,null));
    }
    @Test void bloqueadoRetornaZeroSemCobrarMesmoComCupomInvalido() {
        AtomicInteger chamadas = new AtomicInteger();
        Pedido p = new Pedido(List.of(new ItemPedido("A",1000,1,0,1000,false)),"PR",false,"DESCONHECIDO");
        ResultadoPedido r = new PedidoService(t -> { chamadas.incrementAndGet(); return true; }).fechar(p,new Cliente(false,true,0));
        assertAll(() -> assertEquals("BLOQUEADO",r.status()), () -> assertEquals(0,r.totalCentavos()), () -> assertEquals(0,chamadas.get()));
    }
    @Test void pedidoSemItensAtivosLancaExcecao() {
        Pedido p = new Pedido(List.of(new ItemPedido("A",1000,0,0,1000,false)),"PR",false,null);
        assertThrows(IllegalArgumentException.class, () -> new PedidoService(t -> true).fechar(p,new Cliente(false,false,1)));
    }
    @Test void faltaEstoqueRetornaSemEstoqueSemCobrarNemValidarCupom() {
        AtomicInteger chamadas = new AtomicInteger();
        Pedido p = new Pedido(List.of(new ItemPedido("A",1000,2,1,1000,false)),"PR",false,"DESCONHECIDO");
        ResultadoPedido r = new PedidoService(t -> { chamadas.incrementAndGet(); return true; }).fechar(p,new Cliente(false,false,1));
        assertAll(() -> assertEquals("SEM_ESTOQUE",r.status()), () -> assertEquals(0,r.totalCentavos()), () -> assertEquals(0,chamadas.get()));
    }
    @Test void riscoEmRevisaoNaoCobra() {
        AtomicInteger chamadas = new AtomicInteger();
        Pedido p = new Pedido(List.of(new ItemPedido("A",200_000,1,1,1000,false)),"PR",false,null);
        ResultadoPedido r = new PedidoService(t -> { chamadas.incrementAndGet(); return true; }).fechar(p,new Cliente(false,false,0));
        assertAll(() -> assertEquals("REVISAO",r.status()), () -> assertEquals(200_000,r.subtotalCentavos()), () -> assertEquals(10_000,r.descontoCentavos()), () -> assertEquals(1_200,r.freteCentavos()), () -> assertEquals(191_200,r.totalCentavos()), () -> assertEquals(0,chamadas.get()));
    }
    @Test void pagamentoRecusadoRetornaValoresCalculados() {
        AtomicInteger chamadas = new AtomicInteger();
        Pedido p = new Pedido(List.of(new ItemPedido("A",1000,1,1,1000,false)),"PR",false,null);
        ResultadoPedido r = new PedidoService(t -> { chamadas.incrementAndGet(); return false; }).fechar(p,new Cliente(false,false,1));
        assertEquals("PAGAMENTO_RECUSADO",r.status()); assertEquals(1,chamadas.get()); assertEquals(2200,r.totalCentavos());
    }
    @Test void pagamentoPodeTentarAteTresVezes() {
        AtomicInteger chamadas = new AtomicInteger();
        Pedido p = new Pedido(List.of(new ItemPedido("A",1000,1,1,1000,false)),"PR",false,null);
        ResultadoPedido r = new PedidoService(t -> { chamadas.incrementAndGet(); throw new IllegalStateException(); }).fechar(p,new Cliente(false,false,1));
        assertEquals("PAGAMENTO_RECUSADO",r.status()); assertEquals(3,chamadas.get());
    }
    @Test void excecaoDoPagamentoPropaga() {
        Pedido p = new Pedido(List.of(new ItemPedido("A",1000,1,1,1000,false)),"PR",false,null);
        assertThrows(IllegalArgumentException.class, () -> new PedidoService(t -> { throw new IllegalArgumentException("erro"); }).fechar(p,new Cliente(false,false,1)));
    }
}
