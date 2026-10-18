package com.test.automation.utils;

import java.io.File;
import java.time.Instant;

import com.test.automation.config.ConfigReader;

/**
 * Pasta de downloads dos testes: caminho, limpeza e espera pelo arquivo baixado.
 *
 * O Selenium não sabe quando um download termina. Enquanto baixa, o Chrome grava o arquivo
 * como ".crdownload" e só renomeia para a extensão final no fim. Por isso esperamos o
 * arquivo com a extensão certa aparecer na pasta.
 */
public class DownloadUtils {

    private DownloadUtils() {
    }

    /** Caminho absoluto da pasta de downloads (criada se não existir). */
    public static String getDownloadDir() {
        File dir = new File(System.getProperty("user.dir"), ConfigReader.get("download.dir", "downloads"));
        dir.mkdirs();
        return dir.getAbsolutePath();
    }

    /** Apaga os arquivos da pasta de downloads, para o teste não confundir com um download antigo. */
    public static void cleanDownloadDir() {
        File[] files = new File(getDownloadDir()).listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    file.delete();
                }
            }
        }
        Log.info("Pasta de downloads limpa: " + getDownloadDir());
    }

    /**
     * Espera um arquivo que comece com o prefixo e termine com a extensão aparecer na pasta.
     * Devolve o arquivo, ou null se não aparecer no tempo limite.
     */
    public static File waitForFile(String prefix, String extension, int timeoutSeconds) {
        Instant limit = Instant.now().plusSeconds(timeoutSeconds);
        while (Instant.now().isBefore(limit)) {
            File[] found = new File(getDownloadDir())
                    .listFiles((dir, name) -> name.startsWith(prefix) && name.endsWith(extension));
            if (found != null && found.length > 0 && found[0].length() > 0) {
                Log.info("Arquivo baixado: " + found[0].getName());
                return found[0];
            }
            sleep(500);
        }
        Log.warn("Nenhum arquivo '" + prefix + "*" + extension + "' apareceu em " + timeoutSeconds + " segundos.");
        return null;
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}