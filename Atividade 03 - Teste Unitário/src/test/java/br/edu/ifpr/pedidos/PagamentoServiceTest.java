package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

class PagamentoServiceTest {
    @Test void rejeitaProcessadorNulo() { assertThrows(NullPointerException.class, () -> new PagamentoService(null)); }
    @Test void rejeitaTotalNaoPositivo() { assertThrows(IllegalArgumentException.class, () -> new PagamentoService(t -> true).pagar(0,1)); }
    @Test void rejeitaLimiteDeTentativasInvalido() {
        PagamentoService p = new PagamentoService(t -> true);
        assertThrows(IllegalArgumentException.class, () -> p.pagar(100,0));
        assertThrows(IllegalArgumentException.class, () -> p.pagar(100,4));
    }
    @Test void aprovacaoOcorreEmUmaChamada() {
        AtomicInteger chamadas = new AtomicInteger();
        PagamentoService p = new PagamentoService(t -> { chamadas.incrementAndGet(); assertEquals(1234,t); return true; });
        assertTrue(p.pagar(1234,3));
        assertEquals(1,chamadas.get());
    }
    @Test void recusaDefinitivaNaoRepete() {
        AtomicInteger chamadas = new AtomicInteger();
        assertFalse(new PagamentoService(t -> { chamadas.incrementAndGet(); return false; }).pagar(100,3));
        assertEquals(1,chamadas.get());
    }
    @Test void indisponibilidadePodeSerSeguidaDeSucesso() {
        AtomicInteger chamadas = new AtomicInteger();
        boolean resultado = new PagamentoService(t -> { if (chamadas.getAndIncrement() == 0) throw new IllegalStateException(); return true; }).pagar(100,3);
        assertTrue(resultado); assertEquals(2,chamadas.get());
    }
    @Test void esgotaTentativasEmIndisponibilidade() {
        AtomicInteger chamadas = new AtomicInteger();
        assertFalse(new PagamentoService(t -> { chamadas.incrementAndGet(); throw new IllegalStateException(); }).pagar(100,3));
        assertEquals(3,chamadas.get());
    }
    @Test void propagadaExcecaoDiferenteDeIndisponibilidade() {
        RuntimeException ex = new RuntimeException("falha");
        RuntimeException recebida = assertThrows(RuntimeException.class, () -> new PagamentoService(t -> { throw ex; }).pagar(100,3));
        assertSame(ex, recebida);
    }
}
