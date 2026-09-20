import { test, expect } from '@playwright/test';

const casos = [
  {
    senha: 'Abcdef1X',
    confirmacao: 'Abcdef1X',
    esperado: 'Senha cadastrada',
    classe: '8 caracteres - limite mínimo'
  },
  {
    senha: 'Abcdefghij1234567890',
    confirmacao: 'Abcdefghij1234567890',
    esperado: 'Senha cadastrada',
    classe: '20 caracteres - limite máximo'
  },
  {
    senha: 'Abcdef12',
    confirmacao: 'Abcdef12',
    esperado: 'Senha cadastrada',
    classe: 'senha válida acima do mínimo'
  },
  {
    senha: 'Abcde1X',
    confirmacao: 'Abcde1X',
    esperado: 'Senha fora do padrão',
    classe: '7 caracteres - abaixo do mínimo'
  },
  {
    senha: 'Abcdefghij12345678901',
    confirmacao: 'Abcdefghij12345678901',
    esperado: 'Senha fora do padrão',
    classe: '21 caracteres - acima do máximo'
  },
  {
    senha: 'abcdefgh1',
    confirmacao: 'abcdefgh1',
    esperado: 'Senha fora do padrão',
    classe: 'sem letra maiúscula'
  },
  {
    senha: 'ABCDEFGH1',
    confirmacao: 'ABCDEFGH1',
    esperado: 'Senha fora do padrão',
    classe: 'sem letra minúscula'
  },
  {
    senha: 'Abcdefgh',
    confirmacao: 'Abcdefgh',
    esperado: 'Senha fora do padrão',
    classe: 'sem número'
  },
  {
    senha: 'Abc def1',
    confirmacao: 'Abc def1',
    esperado: 'Senha fora do padrão',
    classe: 'com espaço'
  },
  {
    senha: '',
    confirmacao: '',
    esperado: 'Senha fora do padrão',
    classe: 'senha vazia'
  },
  {
    senha: 'Abcdef12',
    confirmacao: 'Abcdef13',
    esperado: 'As senhas não coincidem',
    classe: 'confirmação diferente'
  },
];

test.describe('senha funcional', () => {
  for (const caso of casos) {
    test(`${caso.classe}`, async ({ page }) => {
      await page.goto('/senha');

      await page.getByLabel('Nova senha').fill(caso.senha);
      await page.getByLabel('Confirmar senha').fill(caso.confirmacao);

      await page
        .getByRole('button', { name: 'Cadastrar senha' })
        .click();

      const resultado = page.locator('#resultado');

      await expect(resultado).toBeVisible();
      await expect(resultado).toHaveText(caso.esperado);

      await expect(resultado).toHaveAttribute(
        'role',
        caso.esperado === 'Senha cadastrada'
          ? 'status'
          : 'alert'
      );
    });
  }
});
