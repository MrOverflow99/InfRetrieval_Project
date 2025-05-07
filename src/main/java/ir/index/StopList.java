package ir.index;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementa una stop list per filtrare termini non significativi.
 */
public class StopList {
    private Set<String> stopWords;

    /**
     * Costruttore per una nuova stop list vuota.
     */
    public StopList() {
        this.stopWords = new HashSet<>();
    }

    /**
     * Costruttore per una stop list da file.
     * 
     * @param filePath Il percorso del file contenente le stop words
     * @throws IOException Se si verifica un errore di I/O
     */
    public StopList(String filePath) throws IOException {
        this();
        loadFromFile(filePath);
    }

    /**
     * Carica le stop words da un file.
     * 
     * @param filePath Il percorso del file
     * @throws IOException Se si verifica un errore di I/O
     */
    public void loadFromFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    stopWords.add(line.toLowerCase());
                }
            }
        }
    }

    /**
     * Aggiunge una stop word alla lista.
     * 
     * @param word La parola da aggiungere
     */
    public void addStopWord(String word) {
        stopWords.add(word.toLowerCase());
    }

    /**
     * Rimuove una stop word dalla lista.
     * 
     * @param word La parola da rimuovere
     */
    public void removeStopWord(String word) {
        stopWords.remove(word.toLowerCase());
    }

    /**
     * Verifica se una parola è una stop word.
     * 
     * @param word La parola da verificare
     * @return true se la parola è una stop word, false altrimenti
     */
    public boolean isStopWord(String word) {
        return stopWords.contains(word.toLowerCase());
    }

    /**
     * Restituisce l'insieme di tutte le stop words.
     * 
     * @return L'insieme di stop words
     */
    public Set<String> getStopWords() {
        return new HashSet<>(stopWords);
    }

    /**
     * Restituisce il numero di stop words nella lista.
     * 
     * @return Il numero di stop words
     */
    public int size() {
        return stopWords.size();
    }
}