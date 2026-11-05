import { test, expect } from '@playwright/test';

const BASE_URL = 'http://localhost:3000';

test.describe('E2E - Fluxo de Autenticação, Proteção de Rotas e Recuperação', () => {

    test('deve exibir o formulário de login com campos e marca padroniza.ba', async ({ page }) => {
        await page.goto(`${BASE_URL}/login`);

        await expect(page.locator('form')).toBeVisible();
        await expect(page.locator('input[type="email"]')).toBeVisible();
        await expect(page.locator('input[type="password"]')).toBeVisible();
        await expect(page.getByRole('button', { name: /entrar no sistema/i })).toBeVisible();
        await expect(page.getByRole('heading', { name: /acesse sua conta/i })).toBeVisible();
        await expect(page.getByText('padroniza.ba').first()).toBeVisible();
    });

    test('deve redirecionar usuário não autenticado ao tentar acessar rotas protegidas', async ({ page }) => {
        // Testando proteção de /usuarios
        await page.goto(`${BASE_URL}/usuarios`);
        await page.waitForURL(/.*login/);
        expect(page.url()).toContain('/login');

        // Testando proteção de /consulta/nova
        await page.goto(`${BASE_URL}/consulta/nova`);
        await page.waitForURL(/.*login/);
        expect(page.url()).toContain('/login');

        // Testando proteção de /dashboard
        await page.goto(`${BASE_URL}/dashboard`);
        await page.waitForURL(/.*login/);
        expect(page.url()).toContain('/login');
    });

    test('deve exibir feedback de erro ao tentar login com credenciais inválidas', async ({ page }) => {
        await page.goto(`${BASE_URL}/login`);

        await page.fill('input[type="email"]', 'consultor.inexistente@padronizaba.com.br');
        await page.fill('input[type="password"]', 'senhaInvalida123');
        await page.click('button[type="submit"]');

        // Aguarda exibição do alerta de erro
        const alertaErro = page.locator('form').getByText(/conta suspensa|e-mail ou senha inválidos|erro ao autenticar/i);
        await expect(alertaErro).toBeVisible({ timeout: 10000 });
    });

    test('deve permitir navegar até a tela de recuperação de acesso e submeter e-mail', async ({ page }) => {
        await page.goto(`${BASE_URL}/login`);

        // Clica em 'Esqueceu a senha?'
        await page.click('text=Esqueceu a senha?');
        await page.waitForURL(/.*recuperar-acesso/);
        expect(page.url()).toContain('/login/recuperar-acesso');

        await expect(page.getByRole('heading', { name: /esqueceu a senha/i })).toBeVisible();

        // Preenche email para recuperação
        await page.fill('input[type="email"]', 'nara.adm@padronizaba.com.br');
        await page.click('button[type="submit"]');

        // Verifica mensagem de confirmação
        await expect(page.getByText(/instruções enviadas/i)).toBeVisible({ timeout: 10000 });
    });
});