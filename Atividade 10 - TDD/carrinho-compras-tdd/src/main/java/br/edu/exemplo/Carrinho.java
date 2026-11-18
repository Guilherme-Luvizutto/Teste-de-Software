package br.edu.exemplo;

import java.util.ArrayList;
import java.util.List;

public class Carrinho {

    private List<Produto> produtos = new ArrayList<>();

    public void adicionarProduto(Produto produto) {
        if (produto.getPreco() < 0) {
            throw new IllegalArgumentException(
                    "O preço do produto não pode ser negativo."
            );
        }

        produtos.add(produto);
    }

    public void removerProduto(Produto produto) {
        produtos.remove(produto);
    }

    private double calcularSubtotal() {
        double subtotal = 0.0;

        for (Produto produto : produtos) {
            subtotal += produto.getPreco();
        }

        return subtotal;
    }

    public double getValorTotal() {
        double total = calcularSubtotal();

        if (total >= 500.00) {
            total *= 0.90;
        }

        return total;
    }
}
