import { test, expect } from '@playwright/test';

const casos = [
  // Caminhos válidos e valores-limite
  {
    cep: '80000000',
    valor: '100,00',
    esperado: 'Frete: R$ 15,00',
    classe: 'CEP iniciado por 8 e pedido abaixo de 200'
  },
  {
    cep: '79999999',
    valor: '100,00',
    esperado: 'Frete: R$ 25,00',
    classe: 'CEP não iniciado por 8 e pedido abaixo de 200'
  },
  {
    cep: '80000000',
    valor: '199,99',
    esperado: 'Frete: R$ 15,00',
    classe: 'valor imediatamente abaixo de 200'
  },
  {
    cep: '80000000',
    valor: '200,00',
    esperado: 'Frete grátis',
    classe: 'limite de frete grátis'
  },
  {
    cep: '79999999',
    valor: '250,00',
    esperado: 'Frete grátis',
    classe: 'acima do limite de frete grátis'
  },

  // Classes inválidas
  {
    cep: '8000000',
    valor: '100,00',
    esperado: 'Dados inválidos',
    classe: 'CEP com 7 dígitos'
  },
  {
    cep: '800000000',
    valor: '100,00',
    esperado: 'Dados inválidos',
    classe: 'CEP com 9 dígitos'
  },
  {
    cep: '8A000000',
    valor: '100,00',
    esperado: 'Dados inválidos',
    classe: 'CEP com caractere não numérico'
  },
  {
    cep: '',
    valor: '100,00',
    esperado: 'Dados inválidos',
    classe: 'CEP vazio'
  },
  {
    cep: '80000000',
    valor: '0,00',
    esperado: 'Dados inválidos',
    classe: 'valor zero'
  },
  {
    cep: '80000000',
    valor: '-1,00',
    esperado: 'Dados inválidos',
    classe: 'valor negativo'
  },
  {
    cep: '80000000',
    valor: 'abc',
    esperado: 'Dados inválidos',
    classe: 'valor não numérico'
  },
  {
    cep: '80000000',
    valor: '100,123',
    esperado: 'Dados inválidos',
    classe: 'mais de 2 casas decimais'
  },
  {
    cep: '80000000',
    valor: '',
    esperado: 'Dados inválidos',
    classe: 'valor vazio'
  },
];

test.describe('frete funcional', () => {
  for (const caso of casos) {
    test(`${caso.classe}`, async ({ page }) => {
      await page.goto('/frete');

      await page.getByLabel('CEP').fill(caso.cep);
      await page.getByLabel('Valor do pedido').fill(caso.valor);

      await page
        .getByRole('button', { name: 'Calcular frete' })
        .click();

      const resultado = page.locator('#resultado');

      await expect(resultado).toBeVisible();
      await expect(resultado).toHaveText(caso.esperado);

      await expect(resultado).toHaveAttribute(
        'role',
        caso.esperado === 'Dados inválidos'
          ? 'alert'
          : 'status'
      );
    });
  }
});
