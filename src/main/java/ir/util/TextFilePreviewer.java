package ir.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class TextFilePreviewer {

    /**
     * Stampa il contenuto dei primi 5 file .txt presenti nella directory specificata.
     *
     * @param directoryPath Il percorso della directory contenente i file.
     */
    public static void printFirstFiveTextFiles(String directoryPath) {
        File directory = new File(directoryPath);

        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("Percorso non valido o directory inesistente: " + directoryPath);
            return;
        }

        File[] txtFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));

        if (txtFiles == null || txtFiles.length == 0) {
            System.out.println("Nessun file .txt trovato nella directory: " + directoryPath);
            return;
        }

        // Ordina i file per nome
        Arrays.sort(txtFiles);

        int filesToRead = Math.min(5, txtFiles.length);

        for (int i = 0; i < filesToRead; i++) {
            File file = txtFiles[i];
            System.out.println("\n=== File: " + file.getName() + " ===");

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                int lineCount = 0;
                while ((line = reader.readLine()) != null && lineCount < 10) {
                    System.out.println(line);
                    lineCount++;
                }
                if (lineCount == 10) {
                    System.out.println("... (contenuto troncato)");
                }
            } catch (IOException e) {
                System.out.println("Errore nella lettura del file: " + file.getName());
                e.printStackTrace();
            }
        }
    }
}