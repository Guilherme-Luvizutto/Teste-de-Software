
package br.edu.exemplo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CarrinhoTest {

    @Test
    void deveIniciarComTotalZero() {
        Carrinho carrinho = new Carrinho();

        double total = carrinho.getValorTotal();

        assertEquals(0.0, total);
    }

    @Test
    void deveAdicionarUmProduto() {
        Carrinho carrinho = new Carrinho();
        Produto produto = new Produto("Teclado", 100.00);

        carrinho.adicionarProduto(produto);

        assertEquals(100.00, carrinho.getValorTotal());
    }

    @Test
    void deveAdicionarVariosProdutos() {
        Carrinho carrinho = new Carrinho();

        Produto teclado = new Produto("Teclado", 100.00);
        Produto mouse = new Produto("Mouse", 50.00);
        Produto monitor = new Produto("Monitor", 300.00);

        carrinho.adicionarProduto(teclado);
        carrinho.adicionarProduto(mouse);
        carrinho.adicionarProduto(monitor);

        assertEquals(450.00, carrinho.getValorTotal());
    }

    @Test
    void deveRemoverProduto() {
        Carrinho carrinho = new Carrinho();

        Produto teclado = new Produto("Teclado", 100.00);
        Produto mouse = new Produto("Mouse", 50.00);

        carrinho.adicionarProduto(teclado);
        carrinho.adicionarProduto(mouse);

        carrinho.removerProduto(mouse);

        assertEquals(100.00, carrinho.getValorTotal());
    }

    @Test
    void naoDeveAdicionarProdutoComPrecoNegativo() {
        Carrinho carrinho = new Carrinho();
        Produto produto = new Produto("Teclado", -100.00);

        assertThrows(
                IllegalArgumentException.class,
                () -> carrinho.adicionarProduto(produto)
        );
    }

    @Test
    void naoDeveAplicarDescontoAbaixoDeQuinhentos() {
        Carrinho carrinho = new Carrinho();
        carrinho.adicionarProduto(new Produto("Produto", 499.99));

        assertEquals(499.99, carrinho.getValorTotal(), 0.001);
    }

    @Test
    void deveAplicarDescontoDeDezPorCentoEmQuinhentos() {
        Carrinho carrinho = new Carrinho();
        carrinho.adicionarProduto(new Produto("Produto", 500.00));

        assertEquals(450.00, carrinho.getValorTotal(), 0.001);
    }

    @Test
    void deveAplicarDescontoAcimaDeQuinhentos() {
        Carrinho carrinho = new Carrinho();
        carrinho.adicionarProduto(new Produto("Produto", 500.01));

        assertEquals(450.009, carrinho.getValorTotal(), 0.001);
    }

}
