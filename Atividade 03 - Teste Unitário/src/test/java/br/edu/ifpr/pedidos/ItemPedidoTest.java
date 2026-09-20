package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ItemPedidoTest {
    @Test void calculaTotalEDisponibilidade() {
        ItemPedido i = new ItemPedido("A", 2500, 3, 5, 1000, false);
        assertEquals(7500, i.totalCentavos());
        assertTrue(i.disponivel());
    }
    @Test void identificaFaltaDeEstoque() { assertFalse(new ItemPedido("A",1000,6,5,1000,false).disponivel()); }
    @Test void quantidadeZeroGeraTotalZero() { assertEquals(0, new ItemPedido("A",1000,0,0,1000,false).totalCentavos()); }
    @Test void rejeitaSkuNuloOuBranco() {
        assertThrows(IllegalArgumentException.class, () -> new ItemPedido(null,1000,1,1,1000,false));
        assertThrows(IllegalArgumentException.class, () -> new ItemPedido(" ",1000,1,1,1000,false));
    }
    @Test void rejeitaPrecoForaDoIntervalo() {
        assertThrows(IllegalArgumentException.class, () -> new ItemPedido("A",0,1,1,1000,false));
        assertThrows(IllegalArgumentException.class, () -> new ItemPedido("A",1_000_001,1,1,1000,false));
    }
    @Test void rejeitaQuantidadeForaDoIntervalo() {
        assertThrows(IllegalArgumentException.class, () -> new ItemPedido("A",1000,-1,1,1000,false));
        assertThrows(IllegalArgumentException.class, () -> new ItemPedido("A",1000,101,1,1000,false));
    }
    @Test void rejeitaEstoqueNegativo() { assertThrows(IllegalArgumentException.class, () -> new ItemPedido("A",1000,1,-1,1000,false)); }
    @Test void rejeitaPesoForaDoIntervalo() {
        assertThrows(IllegalArgumentException.class, () -> new ItemPedido("A",1000,1,1,0,false));
        assertThrows(IllegalArgumentException.class, () -> new ItemPedido("A",1000,1,1,100_001,false));
    }
}
