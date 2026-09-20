package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PoliticaDescontoTest {
    private final PoliticaDesconto p = new PoliticaDesconto();
    @Test void vipRecebeDezPorCento() { assertEquals(1000,p.calcular(new Cliente(true,false,1),10000,null)); }
    @Test void comumRecebeCincoPorCentoNoLimite() { assertEquals(2500,p.calcular(new Cliente(false,false,1),50000,null)); }
    @Test void comumAbaixoDoLimiteNaoRecebeDesconto() { assertEquals(0,p.calcular(new Cliente(false,false,1),49999,null)); }
    @Test void cupomNuloOuBrancoMantemDesconto() {
        Cliente c = new Cliente(false,false,1);
        assertEquals(2500,p.calcular(c,50000,null));
        assertEquals(2500,p.calcular(c,50000,"   "));
    }
    @Test void bemVindoElegivelSomaVinteReais() { assertEquals(3000,p.calcular(new Cliente(false,false,0),10000," bemvindo ")); }
    @Test void bemVindoSemElegibilidadeNaoAcrescenta() {
        assertEquals(0,p.calcular(new Cliente(false,false,1),10000,"BEMVINDO"));
        assertEquals(0,p.calcular(new Cliente(false,false,0),9999,"BEMVINDO"));
    }
    @Test void extra10ElegivelSomaDezPorCento() { assertEquals(4000,p.calcular(new Cliente(false,false,1),20000,"EXTRA10")); }
    @Test void extra10AbaixoDoLimiteNaoAcrescenta() { assertEquals(500,p.calcular(new Cliente(false,false,1),10000,"EXTRA10")); }
    @Test void descontoLimitadoA20PorCento() { assertEquals(2000,p.calcular(new Cliente(true,false,0),10000,"BEMVINDO")); }
    @Test void cupomDesconhecidoLancaExcecao() { assertThrows(IllegalArgumentException.class, () -> p.calcular(new Cliente(false,false,0),10000,"OUTRO")); }
    @Test void rejeitaSubtotalNegativo() { assertThrows(IllegalArgumentException.class, () -> p.calcular(new Cliente(false,false,0),-1,null)); }
}
