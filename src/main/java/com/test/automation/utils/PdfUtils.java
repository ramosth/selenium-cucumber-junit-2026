package com.test.automation.utils;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * Leitura de arquivos PDF com a biblioteca Apache PDFBox.
 */
public class PdfUtils {

    private PdfUtils() {
    }

    /**
     * Lê todo o texto do PDF em uma única linha, com os espaços normalizados:
     * quebras de linha e espaços especiais viram um espaço simples.
     * Assim, o teste confere trechos com contains("Tax $3.20") sem se preocupar com a diagramação.
     */
    public static String readText(File pdfFile) {
        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true); // lê na ordem visual: o que está na mesma linha fica junto
            String text = stripper.getText(document);
            return text
                    .replace('\u202F', ' ')   // espaço estreito que o navegador usa antes de "AM"/"PM"
                    .replace('\u00A0', ' ')   // espaço "não separável"
                    .replaceAll("\\s+", " ")  // qualquer sequência de espaços e quebras de linha vira um espaço
                    .trim();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler o PDF: " + pdfFile, e);
        }
    }
}