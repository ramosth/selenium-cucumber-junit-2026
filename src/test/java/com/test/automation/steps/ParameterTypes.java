package com.test.automation.steps;

import com.test.automation.data.Product;
import com.test.automation.data.User;

import io.cucumber.java.DataTableType;
import io.cucumber.java.ParameterType;

/**
 * Conversões personalizadas: ensinam o Cucumber a transformar o texto do
 * .feature
 * em objetos Java.
 *
 * Produtos: o .feature usa a CHAVE da constante (BACKPACK, BIKE_LIGHT...) e o
 * Java
 * recebe o Product pronto. Assim o nome completo de cada produto existe em um
 * único
 * lugar (data/Product.java); se o site renomear um produto, só ele muda.
 *
 * Valores: o .feature escreve o valor como aparece na tela ($7.99) e o Java
 * recebe 7.99.
 * (O {double} do Cucumber segue o idioma do .feature e, em pt, esperaria
 * "7,99".)
 *
 * Fica no pacote "steps" porque esse pacote já está no glue do TestRunner.
 */
public class ParameterTypes {

    /**
     * {produto} nas frases. Ex.: "removo o produto BACKPACK ..." ->
     * Product.BACKPACK
     */
    @ParameterType("[A-Z_]+")
    public Product produto(String chave) {
        return Product.valueOf(chave);
    }

    /** {usuario} nas frases. Ex.: "logado com o usuário PROBLEM" -> User.PROBLEM */
    @ParameterType("[A-Z_]+")
    public User usuario(String chave) {
        return User.valueOf(chave);
    }

    /** Cada linha das tabelas de produtos. Ex.: | BACKPACK | -> Product.BACKPACK */
    @DataTableType
    public Product produtoDaTabela(String chave) {
        return Product.valueOf(chave.trim());
    }

    /** {valor} nas frases: valor em dólar, como na tela. Ex.: "$7.99" -> 7.99 */
    @ParameterType("\\$\\d+\\.\\d{2}")
    public Double valor(String texto) {
        return Double.parseDouble(texto.substring(1)); // tira o "$"
    }
}