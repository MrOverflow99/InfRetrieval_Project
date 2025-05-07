package ir.util;

import ir.model.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility per il caricamento di file e documenti.
 */
public class FileLoader {
    
    /**
     * Carica un singolo documento da file.
     * 
     * @param file Il file da caricare come documento
     * @param documentId L'ID da assegnare al documento
     * @return Il documento caricato
     * @throws IOException Se si verifica un errore di I/O
     */
    public static Document loadDocument(File file, int documentId) throws IOException {
        StringBuilder content = new StringBuilder();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        
        return new Document(documentId, file.getName(), content.toString());
    }
    
    /**
     * Carica tutti i documenti da una directory.
     * 
     * @param directoryPath Il percorso della directory
     * @return Lista di documenti caricati
     * @throws IOException Se si verifica un errore di I/O
     */
    public static List<Document> loadDocumentsFromDirectory(String directoryPath) throws IOException {
        List<Document> documents = new ArrayList<>();
        File directory = new File(directoryPath);
        
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IOException("La directory specificata non esiste o non è una directory valida: " + directoryPath);
        }
        
        File[] files = directory.listFiles((dir, name) -> !name.startsWith("."));
        if (files != null) {
            int documentId = 1;
            for (File file : files) {
                if (file.isFile()) {
                    documents.add(loadDocument(file, documentId++));
                }
            }
        }
        
        return documents;
    }
    
    /**
     * Carica documenti da una directory in modo ricorsivo.
     * 
     * @param directoryPath Il percorso della directory
     * @return Lista di documenti caricati
     * @throws IOException Se si verifica un errore di I/O
     */
    public static List<Document> loadDocumentsRecursively(String directoryPath) throws IOException {
        List<Document> documents = new ArrayList<>();
        Path startPath = Paths.get(directoryPath);
        
        if (!Files.exists(startPath) || !Files.isDirectory(startPath)) {
            throw new IOException("La directory specificata non esiste o non è una directory valida: " + directoryPath);
        }
        
        try (Stream<Path> paths = Files.walk(startPath)) {
            List<File> files = paths
                    .filter(Files::isRegularFile)
                    .filter(p -> !p.getFileName().toString().startsWith("."))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
            
            int documentId = 1;
            for (File file : files) {
                documents.add(loadDocument(file, documentId++));
            }
        }
        
        return documents;
    }
    
    /**
     * Carica il contenuto di un file di testo come stringa.
     * 
     * @param filePath Il percorso del file
     * @return Il contenuto del file come stringa
     * @throws IOException Se si verifica un errore di I/O
     */
    public static String loadTextFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        
        return content.toString();
    }
    
    /**
     * Verifica se un file esiste e può essere letto.
     * 
     * @param filePath Il percorso del file
     * @return true se il file esiste ed è leggibile, false altrimenti
     */
    public static boolean isFileReadable(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile() && file.canRead();
    }
    
    /**
     * Verifica se una directory esiste e può essere letta.
     * 
     * @param directoryPath Il percorso della directory
     * @return true se la directory esiste ed è leggibile, false altrimenti
     */
    public static boolean isDirectoryReadable(String directoryPath) {
        File dir = new File(directoryPath);
        return dir.exists() && dir.isDirectory() && dir.canRead();
    }
}