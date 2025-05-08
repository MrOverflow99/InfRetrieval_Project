package ir.util;

import java.io.File;

/**
 * Utility per verificare il contenuto di una directory
 */
public class DirectoryChecker {
    
    /**
     * Stampa informazioni sui file in una directory
     * 
     * @param directoryPath Percorso della directory da controllare
     * @return Numero di file .txt trovati
     */
    public static int checkDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        
        System.out.println("Verifico la directory: " + directoryPath);
        System.out.println("La directory esiste? " + directory.exists());
        System.out.println("È una directory? " + directory.isDirectory());
        System.out.println("Permessi lettura? " + directory.canRead());
        
        int txtFilesCount = 0;
        
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            
            if (files != null) {
                System.out.println("Numero totale di file/directory: " + files.length);
                
                System.out.println("\nPrimi 5 file .txt (se presenti):");
                int count = 0;
                
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".txt")) {
                        txtFilesCount++;
                        
                        if (count < 5) {
                            System.out.println(" - " + file.getName() + " (" + file.length() + " bytes)");
                            count++;
                        }
                    }
                }
                
                System.out.println("\nTotale file .txt trovati: " + txtFilesCount);
            } else {
                System.out.println("Impossibile leggere il contenuto della directory.");
            }
        }
        
        return txtFilesCount;
    }
    
    /**
     * Verifica e propone percorsi alternativi se la directory originale non contiene file
     * 
     * @param originalPath Percorso originale da verificare
     */
    public static void suggestAlternativePaths(String originalPath) {
        // Verifica il percorso originale
        int filesFound = checkDirectory(originalPath);
        
        if (filesFound == 0) {
            System.out.println("\nNessun file .txt trovato nel percorso specificato. Provo percorsi alternativi...");
            
            // Prova diversi percorsi relativi che potrebbero contenere i file
            String[] alternativePaths = {
                "documents/archive/aclImdb/test/neg",
                "src/main/resources/documents/aclImdb/test/neg",
                "resources/documents/archive/aclImdb/test/neg",
                "src/resources/documents/archive/aclImdb/test/neg",
                "aclImdb/test/neg",
                "test/neg"
            };
            
            for (String path : alternativePaths) {
                System.out.println("\nProvo percorso alternativo: " + path);
                int count = checkDirectory(path);
                if (count > 0) {
                    System.out.println(">>> Trovati " + count + " file .txt in questo percorso alternativo.");
                }
            }
        }
    }
}