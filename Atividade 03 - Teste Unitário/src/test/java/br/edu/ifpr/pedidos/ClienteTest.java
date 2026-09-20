package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {
    @Test void criaClienteEExponeDados() {
        Cliente c = new Cliente(true, false, 3);
        assertAll(() -> assertTrue(c.vip()), () -> assertFalse(c.bloqueado()), () -> assertEquals(3,c.comprasAnteriores()));
    }
    @Test void rejeitaHistoricoNegativo() { assertThrows(IllegalArgumentException.class, () -> new Cliente(false,false,-1)); }
}
