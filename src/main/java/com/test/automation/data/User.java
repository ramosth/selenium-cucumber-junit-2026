package com.test.automation.data;

/**
 * enum: uma lista fechada de valores possíveis. Em vez de digitar "problem_user" (e arriscar errar uma letra), você escreve User.PROBLEM
 * Usuários do Sauce Demo. Todos usam a mesma senha (standard.password no config.properties).
 *
 * Cada usuário tem defeitos de propósito, para treinar a detecção de bugs.
 * Uso: new LoginPage(driver).loginAs(User.PROBLEM)
 */
public enum User {

    STANDARD("standard_user", "Usuário padrão, sem defeitos"),
    LOCKED_OUT("locked_out_user", "Bloqueado: não consegue fazer login"),
    PROBLEM("problem_user", "Imagens erradas, ordenação que não funciona e falhas no carrinho"),
    PERFORMANCE_GLITCH("performance_glitch_user", "Lentidão de ~5 segundos ao carregar a lista de produtos"),
    ERROR("error_user", "Erros em ações: a ordenação abre um alerta e ações do carrinho falham"),
    VISUAL("visual_user", "Falhas visuais: preços exibidos aleatórios e layout desalinhado");

    private final String username;
    private final String description;

    User(String username, String description) {
        this.username = username;
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public String getDescription() {
        return description;
    }

    /** Mostra o username no relatório e no nome do print (ex.: testSortByNameZToA_problem_user). */
    @Override
    public String toString() {
        return username;
    }
}