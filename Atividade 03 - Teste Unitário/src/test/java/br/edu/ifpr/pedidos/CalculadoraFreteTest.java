package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculadoraFreteTest {
    private Pedido pedido(String uf, boolean expresso, ItemPedido... itens) { return new Pedido(java.util.List.of(itens), uf, expresso, null); }
    private Cliente comum() { return new Cliente(false, false, 1); }

    @Test void usaTarifaPR() { assertEquals(1200, new CalculadoraFrete().calcular(pedido("PR", false, new ItemPedido("A",1000,1,1,1000,false)), comum(), 1000)); }
    @Test void usaTarifaSPERJ() {
        CalculadoraFrete c = new CalculadoraFrete();
        assertEquals(2000, c.calcular(pedido("SP", false, new ItemPedido("A",1000,1,1,1000,false)), comum(), 1000));
        assertEquals(2000, c.calcular(pedido("RJ", false, new ItemPedido("A",1000,1,1,1000,false)), comum(), 1000));
    }
    @Test void usaTarifaPadrao() { assertEquals(3000, new CalculadoraFrete().calcular(pedido("MG", false, new ItemPedido("A",1000,1,1,1000,false)), comum(), 1000)); }
    @Test void cobraPesoExcedentePorFracao() {
        assertEquals(1800, new CalculadoraFrete().calcular(pedido("PR", false, new ItemPedido("A",1000,1,1,3001,false)), comum(), 1000));
    }
    @Test void freteGratisAcimaDe300ReaisNaEntregaNormal() {
        assertEquals(0, new CalculadoraFrete().calcular(pedido("PR", false, new ItemPedido("A",1000,1,1,5000,false)), comum(), 30000));
    }
    @Test void freteGratisNaoSeAplicaAoExpresso() {
        assertEquals(1500, new CalculadoraFrete().calcular(pedido("PR", true, new ItemPedido("A",1000,1,1,1000,false)), comum(), 30000));
    }
    @Test void vipPagaMetadeDaBase() { assertEquals(600, new CalculadoraFrete().calcular(pedido("PR", false, new ItemPedido("A",1000,1,1,1000,false)), new Cliente(true,false,1), 1000)); }
    @Test void adicionaExpressoEFragilUmaUnicaVez() {
        Pedido p = pedido("PR", true, new ItemPedido("A",1000,1,1,1000,true), new ItemPedido("B",1000,1,1,1000,true));
        assertEquals(3200, new CalculadoraFrete().calcular(p, comum(), 1000));
    }
    @Test void fragilidadeTambemIncideComFreteGratis() {
        Pedido p = pedido("PR", false, new ItemPedido("A",1000,1,1,1000,true));
        assertEquals(500, new CalculadoraFrete().calcular(p, comum(), 30000));
    }
    @Test void rejeitaLiquidoNegativo() { assertThrows(IllegalArgumentException.class, () -> new CalculadoraFrete().calcular(pedido("PR",false,new ItemPedido("A",1000,1,1,1000,false)), comum(), -1)); }
}
