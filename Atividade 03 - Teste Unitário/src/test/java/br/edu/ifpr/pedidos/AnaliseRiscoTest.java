package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AnaliseRiscoTest {
    @Test void rejeitaClienteBloqueado() {
        assertEquals("RECUSADO", new AnaliseRisco().avaliar(new Cliente(false, true, 0), 1, false));
    }
    @Test void rejeitaTotalNegativo() {
        assertThrows(IllegalArgumentException.class, () -> new AnaliseRisco().avaliar(new Cliente(false, false, 1), -1, false));
    }
    @Test void revisaNovoClientePorTotalAlto() {
        assertEquals("REVISAO", new AnaliseRisco().avaliar(new Cliente(false, false, 0), 100_001, false));
    }
    @Test void revisaNovoClientePorEntregaExpressa() {
        assertEquals("REVISAO", new AnaliseRisco().avaliar(new Cliente(false, false, 0), 1, true));
    }
    @Test void aprovaNovoClienteNoLimiteSemExpresso() {
        assertEquals("APROVADO", new AnaliseRisco().avaliar(new Cliente(false, false, 0), 100_000, false));
    }
    @Test void revisaClienteComHistoricoNaoVipPorTotalAlto() {
        assertEquals("REVISAO", new AnaliseRisco().avaliar(new Cliente(false, false, 1), 500_001, false));
    }
    @Test void aprovaClienteVipMesmoComTotalAlto() {
        assertEquals("APROVADO", new AnaliseRisco().avaliar(new Cliente(true, false, 1), 500_001, false));
    }
    @Test void aprovaClienteComHistoricoNoLimite() {
        assertEquals("APROVADO", new AnaliseRisco().avaliar(new Cliente(false, false, 1), 500_000, false));
    }
}
