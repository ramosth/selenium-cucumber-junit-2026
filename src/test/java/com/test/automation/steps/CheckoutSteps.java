package com.test.automation.steps;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.testng.Assert;

import com.test.automation.context.TestContext;
import com.test.automation.data.Product;
import com.test.automation.pages.CartPage;
import com.test.automation.pages.CheckoutCompletePage;
import com.test.automation.pages.CheckoutInformationPage;
import com.test.automation.pages.CheckoutOverviewPage;
import com.test.automation.pages.InventoryPage;
import com.test.automation.pages.components.HeaderComponent;
import com.test.automation.utils.DownloadUtils;
import com.test.automation.utils.Log;
import com.test.automation.utils.PdfUtils;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;

/**
 * Passos do checkout: dados do comprador, resumo do pedido, conclusão,
 * cancelamento e recibo em PDF.
 *
 * Regras do resumo do pedido (tela Checkout: Overview):
 *   Item total = soma dos preços dos itens
 *   Tax        = 8% do item total, arredondado para 2 casas
 *   Total      = item total + tax
 *
 * Recibo em PDF ("Generate PDF order", na tela de pedido concluído):
 *   - nome do arquivo: data e hora do pedido em UTC (ex.: swag-labs-order-2026-10-15_21-52-47.pdf)
 *   - conteúdo: data e hora no horário local, em inglês (ex.: October 15, 2026 at 6:52 PM)
 */
public class CheckoutSteps {

    // ---------- Recibo em PDF ----------
    private static final String FILE_PREFIX = "swag-labs-order-";
    private static final String FILE_EXTENSION = ".pdf";
    private static final int DOWNLOAD_TIMEOUT_SECONDS = 20;
    private static final DateTimeFormatter FILE_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final DateTimeFormatter RECEIPT_DATE =
            DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm a", Locale.US);
    private static final List<String> RECEIPT_FIXED_TEXTS = List.of(
            "Swag Labs", "Order Receipt", "ORDER DETAILS", "Order Date", "SHIP TO", "ITEMS", "DESCRIPTION", "PRICE",
            "Thank you for your order! It has been dispatched, and will arrive just as fast as the pony can get there.");

    // Margem para comparar números com casas decimais (29.99 pode virar 29.989999... no computador)
    private static final double PRICE_DELTA = 0.001;

    private final TestContext context;

    // Informações guardadas entre os passos do recibo em PDF.
    // O PicoContainer cria um CheckoutSteps novo a cada cenário, então nada passa de um cenário para outro.
    private ZonedDateTime horaDoPedido;
    private File pdf;
    private String textoDoPdf;

    public CheckoutSteps(TestContext context) {
        this.context = context;
    }

    // ---------- Navegação e ações ----------

    @Quando("sigo para o checkout")
    public void sigoParaOCheckout() {
        CheckoutInformationPage informationPage = new HeaderComponent(context.getDriver()).openCart().goToCheckout();
        Assert.assertTrue(informationPage.isLoaded(), "A tela de dados do checkout não foi exibida.");
    }

    @Quando("preencho os dados do comprador com nome {string}, sobrenome {string} e CEP {string}")
    public void preenchoOsDadosDoComprador(String nome, String sobrenome, String cep) {
        Log.info("Dados do comprador: '" + nome + "' / '" + sobrenome + "' / '" + cep + "'");
        new CheckoutInformationPage(context.getDriver()).fillInformation(nome, sobrenome, cep);
    }

    @Quando("clico em Continue")
    public void clicoEmContinue() {
        new CheckoutInformationPage(context.getDriver()).clickContinue();
    }

    @Quando("clico em Finish")
    public void clicoEmFinish() {
        // A data do pedido é registrada no Finish: guardamos este momento para conferir o recibo em PDF
        horaDoPedido = ZonedDateTime.now();
        new CheckoutOverviewPage(context.getDriver()).finish();
    }

    @Quando("cancelo nos dados do comprador")
    public void canceloNosDadosDoComprador() {
        new CheckoutInformationPage(context.getDriver()).cancel();
    }

    @Quando("cancelo no resumo do pedido")
    public void canceloNoResumoDoPedido() {
        new CheckoutOverviewPage(context.getDriver()).cancel();
    }

    /** Pré-condição do recibo: chama, em ordem, os passos do checkout até o pedido concluído. */
    @Dado("que concluí a compra com nome {string}, sobrenome {string} e CEP {string}")
    public void queConcluiACompra(String nome, String sobrenome, String cep) {
        sigoParaOCheckout();
        preenchoOsDadosDoComprador(nome, sobrenome, cep);
        clicoEmContinue();
        Assert.assertTrue(new CheckoutOverviewPage(context.getDriver()).isLoaded(),
                "A tela de resumo do pedido não foi exibida.");
        clicoEmFinish();
        Assert.assertTrue(new CheckoutCompletePage(context.getDriver()).isLoaded(),
                "A tela de pedido concluído não foi exibida.");
    }

    // ---------- Validações: dados do comprador ----------

    @Entao("devo ver no checkout a mensagem de erro {string}")
    public void devoVerNoCheckoutAMensagemDeErro(String mensagemEsperada) {
        CheckoutInformationPage informationPage = new CheckoutInformationPage(context.getDriver());
        Assert.assertTrue(informationPage.isErrorMessageDisplayed(), "Era esperada uma mensagem de erro.");
        Assert.assertEquals(informationPage.getErrorMessage(), mensagemEsperada,
                "Mensagem de erro diferente da esperada.");
    }

    @Entao("devo continuar na tela de dados do comprador")
    public void devoContinuarNaTelaDeDadosDoComprador() {
        Assert.assertTrue(new CheckoutInformationPage(context.getDriver()).isLoaded(),
                "Era esperado continuar na tela de dados do checkout.");
    }

    // ---------- Validações: resumo e conclusão ----------

    @Entao("o resumo do pedido deve conter os produtos:")
    public void oResumoDoPedidoDeveConterOsProdutos(List<Product> produtosEsperados) {
        CheckoutOverviewPage overviewPage = new CheckoutOverviewPage(context.getDriver());
        Assert.assertTrue(overviewPage.isLoaded(), "A tela de resumo do pedido não foi exibida.");

        List<String> nomesEsperados = produtosEsperados.stream().map(Product::getName).toList();
        Assert.assertEqualsNoOrder(overviewPage.getItemNames().toArray(), nomesEsperados.toArray(),
                "Os itens do resumo são diferentes dos esperados.");
    }

    @Entao("o resumo do pedido deve mostrar item total {valor}, taxa {valor} e total {valor}")
    public void oResumoDoPedidoDeveMostrarOsValores(Double itemTotal, Double taxa, Double total) {
        CheckoutOverviewPage overviewPage = new CheckoutOverviewPage(context.getDriver());
        Log.info("Valores na tela: Item total " + overviewPage.getItemTotal()
                + " | Tax " + overviewPage.getTax() + " | Total " + overviewPage.getTotal());

        Assert.assertEquals(overviewPage.getItemTotal(), itemTotal, PRICE_DELTA,
                "Item total diferente do esperado (soma dos preços).");
        Assert.assertEquals(overviewPage.getTax(), taxa, PRICE_DELTA,
                "Taxa diferente da esperada (8% do item total).");
        Assert.assertEquals(overviewPage.getTotal(), total, PRICE_DELTA,
                "Total diferente do esperado (item total + taxa).");
    }

    @Entao("devo ver a confirmação do pedido {string}")
    public void devoVerAConfirmacaoDoPedido(String mensagemEsperada) {
        CheckoutCompletePage completePage = new CheckoutCompletePage(context.getDriver());
        Assert.assertTrue(completePage.isLoaded(), "A tela de pedido concluído não foi exibida.");
        Assert.assertEquals(completePage.getConfirmationMessage(), mensagemEsperada,
                "Mensagem de confirmação diferente da esperada.");
    }

    // ---------- Validações: para onde o cancelamento leva ----------

    @Entao("devo estar na página do carrinho")
    public void devoEstarNaPaginaDoCarrinho() {
        Assert.assertTrue(new CartPage(context.getDriver()).isLoaded(), "Era esperado estar no carrinho.");
    }

    @Entao("devo estar na lista de produtos")
    public void devoEstarNaListaDeProdutos() {
        Assert.assertTrue(new InventoryPage(context.getDriver()).isLoaded(),
                "Era esperado estar na lista de produtos.");
    }

    // ---------- Recibo em PDF ----------

    @Quando("gero o recibo em PDF")
    public void geroOReciboEmPdf() {
        CheckoutCompletePage completePage = new CheckoutCompletePage(context.getDriver());
        Assert.assertTrue(completePage.isGeneratePdfButtonDisplayed(), "O botão 'Generate PDF order' não apareceu.");

        DownloadUtils.cleanDownloadDir(); // garante que o arquivo encontrado é o deste cenário
        completePage.clickGeneratePdfOrder();

        pdf = DownloadUtils.waitForFile(FILE_PREFIX, FILE_EXTENSION, DOWNLOAD_TIMEOUT_SECONDS);
        Assert.assertNotNull(pdf, "O PDF não foi baixado em " + DOWNLOAD_TIMEOUT_SECONDS + " segundos.");
        textoDoPdf = PdfUtils.readText(pdf);

        Log.info("PDF baixado: " + pdf.getName());
        context.getExtentTest().info("PDF baixado: " + pdf.getName() + "<br>Texto: " + textoDoPdf);
    }

    @Entao("o nome do arquivo deve ter a data e hora do pedido em UTC")
    public void oNomeDoArquivoDeveTerADataDoPedidoEmUtc() {
        String nome = pdf.getName();
        Assert.assertTrue(nome.startsWith(FILE_PREFIX) && nome.endsWith(FILE_EXTENSION),
                "Nome do arquivo fora do padrão: " + nome);

        String dataHora = nome.substring(FILE_PREFIX.length(), nome.length() - FILE_EXTENSION.length());
        Instant horaDoArquivo = LocalDateTime.parse(dataHora, FILE_DATE).atZone(ZoneOffset.UTC).toInstant();
        long diferencaSegundos = Math.abs(Duration.between(horaDoPedido.toInstant(), horaDoArquivo).getSeconds());

        Log.info("Data no nome do arquivo (UTC): " + dataHora + " | diferença do Finish: " + diferencaSegundos + "s");
        Assert.assertTrue(diferencaSegundos <= 120,
                "A data no nome do arquivo (" + dataHora + " UTC) está longe do horário do pedido.");
    }

    @Entao("o PDF deve conter os textos fixos do recibo")
    public void oPdfDeveConterOsTextosFixosDoRecibo() {
        for (String textoFixo : RECEIPT_FIXED_TEXTS) {
            assertPdfContem(textoFixo, "Texto fixo ausente no PDF");
        }
    }

    @Entao("o PDF deve conter o comprador {string} e o CEP {string}")
    public void oPdfDeveConterOCompradorEOCep(String comprador, String cep) {
        assertPdfContem(comprador, "Nome do comprador ausente no PDF");
        assertPdfContem(cep, "CEP ausente no PDF");
    }

    @Entao("o PDF deve conter os itens com os preços:")
    public void oPdfDeveConterOsItensComOsPrecos(List<Product> produtos) {
        for (Product produto : produtos) {
            // No PDF, cada item aparece como "Nome do produto $preço"; o preço vem do enum Product
            assertPdfContem(produto.getName() + " " + dinheiro(produto.getPrice()), "Item ausente no PDF");
        }
    }

    @Entao("o PDF deve mostrar item total {valor}, taxa {valor} e total {valor}")
    public void oPdfDeveMostrarOsValores(Double itemTotal, Double taxa, Double total) {
        assertPdfContem("Item total " + dinheiro(itemTotal), "Item total incorreto no PDF");
        assertPdfContem("Tax " + dinheiro(taxa), "Taxa incorreta no PDF");
        assertPdfContem("Total " + dinheiro(total), "Total incorreto no PDF");
    }

    @Entao("o PDF deve mostrar a data do pedido no horário local")
    public void oPdfDeveMostrarADataDoPedidoNoHorarioLocal() {
        // Aceita 1 minuto antes ou depois do Finish (o minuto pode virar entre o clique e a geração do PDF)
        boolean encontrou = false;
        for (int minutos = -1; minutos <= 1; minutos++) {
            if (textoDoPdf.contains(horaDoPedido.plusMinutes(minutos).format(RECEIPT_DATE))) {
                encontrou = true;
                break;
            }
        }
        Assert.assertTrue(encontrou,
                "Data do pedido ausente ou fora da tolerância. Esperado perto de: " + horaDoPedido.format(RECEIPT_DATE));
    }

    // ---------- Auxiliares ----------

    /** Valor no formato do recibo: 29.99 -> "$29.99" (sempre com ponto e 2 casas). */
    private String dinheiro(double valor) {
        return String.format(Locale.US, "$%.2f", valor);
    }

    private void assertPdfContem(String esperado, String mensagem) {
        Assert.assertTrue(textoDoPdf.contains(esperado), mensagem + ": '" + esperado + "'");
    }
}