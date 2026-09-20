package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class PedidoTest {
    private ItemPedido item(int qtd, int estoque, int peso, boolean fragil) { return new ItemPedido("A",1000,qtd,estoque,peso,fragil); }
    @Test void copiaListaECalculaSubtotalIgnorandoInativos() {
        List<ItemPedido> lista = new ArrayList<>(List.of(item(2,2,1000,false), item(0,0,2000,true)));
        Pedido p = new Pedido(lista,"PR",false,null); lista.clear();
        assertEquals(2000,p.subtotalCentavos());
    }
    @Test void calculaPeso() {
        Pedido p = new Pedido(List.of(item(2,2,1000,false), item(3,3,500,true)),"PR",false,null);
        assertEquals(3500,p.pesoGramas());
    }
    @Test void identificaFragilApenasEmItemAtivo() {
        assertFalse(new Pedido(List.of(item(0,0,1000,true)),"PR",false,null).temFragil());
        assertTrue(new Pedido(List.of(item(1,1,1000,true)),"PR",false,null).temFragil());
    }
    @Test void verificaEstoqueEParaNoPrimeiroIndisponivel() {
        assertTrue(new Pedido(List.of(item(1,1,1000,false),item(2,2,1000,false)),"PR",false,null).estoqueSuficiente());
        assertFalse(new Pedido(List.of(item(2,1,1000,false),item(1,1,1000,false)),"PR",false,null).estoqueSuficiente());
    }
    @Test void aceitaListaVazia() { assertEquals(0,new Pedido(List.of(),"PR",false,null).subtotalCentavos()); }
    @Test void rejeitaListaNulaOuGrande() {
        assertThrows(IllegalArgumentException.class, () -> new Pedido(null,"PR",false,null));
        List<ItemPedido> muitos = new ArrayList<>();
        ItemPedido i = item(1,1,1000,false); for(int n=0;n<101;n++) muitos.add(i);
        assertThrows(IllegalArgumentException.class, () -> new Pedido(muitos,"PR",false,null));
    }
    @Test void rejeitaElementoNulo() { assertThrows(NullPointerException.class, () -> new Pedido(Arrays.asList(item(1,1,1000,false),null),"PR",false,null)); }
    @Test void rejeitaUfInvalida() {
        assertThrows(IllegalArgumentException.class, () -> new Pedido(List.of(),null,false,null));
        assertThrows(IllegalArgumentException.class, () -> new Pedido(List.of(),"pr",false,null));
        assertThrows(IllegalArgumentException.class, () -> new Pedido(List.of(),"PRX",false,null));
    }
}
