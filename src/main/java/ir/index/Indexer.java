package ir.index;

import ir.model.Dictionary;
import ir.model.Document;
import ir.model.PostingList;
import ir.util.FileLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Implementazione robusta dell'indicizzazione con gestione migliorata degli errori.
 */
public class Indexer {
    private List<Document> documents;
    private Dictionary dictionary;
    private StopList stopList;
    private PorterStemmer stemmer;
    private boolean useStopList;
    private boolean useStemming;
    private Pattern tokenPattern;
    
    /**
     * Costruttore dell'indicizzatore.
     */
    public Indexer() {
        documents = new ArrayList<>();
        dictionary = new Dictionary();
        stemmer = new PorterStemmer();
        stopList = new StopList(); // Initialize to avoid NullPointerException
        useStopList = false;
        useStemming = false;
        
        // Pattern per tokenizzare il testo (solo parole, no numeri o simboli)
        // Using \p{L}+ for Unicode letter support
        tokenPattern = Pattern.compile("[\\p{L}]+");
    }
    
    /**
     * Carica una lista di stop words da file.
     * 
     * @param filePath Il percorso del file delle stop words
     * @throws IOException Se si verifica un errore di I/O
     */
    public void loadStopList(String filePath) throws IOException {
        stopList = new StopList(filePath);
    }
    
    /**
     * Imposta l'uso della lista di stop words.
     * 
     * @param useStopList true per usare la lista di stop words
     */
    public void setUseStopList(boolean useStopList) {
        this.useStopList = useStopList;
    }
    
    /**
     * Imposta l'uso dello stemming.
     * 
     * @param useStemming true per usare lo stemming
     */
    public void setUseStemming(boolean useStemming) {
        this.useStemming = useStemming;
    }
    
    /**
     * Carica i documenti da una directory.
     * 
     * @param directoryPath Il percorso della directory
     * @throws IOException Se si verifica un errore di I/O
     */
    public void loadDocumentsFromDirectory(String directoryPath) throws IOException {
        System.out.println("Caricamento documenti da " + directoryPath + "...");
        documents = FileLoader.loadDocumentsFromDirectory(directoryPath);
        System.out.println("Caricati " + documents.size() + " documenti.");
    }
    
    /**
     * Elabora un termine del documento.
     * 
     * @param term Il termine da elaborare
     * @return Il termine elaborato o null se è una stop word
     */
    private String processTerm(String term) {
        if (term == null || term.isEmpty()) {
            return null;
        }
        
        // Converti in minuscolo
        term = term.toLowerCase();
        
        // Verifica se è una stop word
        if (useStopList && stopList != null && stopList.isStopWord(term)) {
            return null;
        }
        
        // Applica lo stemming
        if (useStemming && term.length() > 1) { // Avoid stemming very short terms
            try {
                term = stemmer.stem(term);
            } catch (Exception e) {
                System.err.println("Errore nello stemming del termine '" + term + "': " + e.getMessage());
                // Continua senza stemming in caso di errore
            }
        }
        
        return term.isEmpty() ? null : term;
    }
    
    /**
     * Indicizza un singolo documento.
     * 
     * @param doc Il documento da indicizzare
     */
    public void indexDocument(Document doc) {
        if (doc == null) {
            System.err.println("Errore: tentativo di indicizzare un documento null");
            return;
        }
        
        System.out.println("Indicizzazione documento " + doc.getId() + ": " + doc.getName());
        
        // Controllo se il contenuto del documento è null
        String content = doc.getContent();
        if (content == null || content.isEmpty()) {
            System.err.println("Avviso: il documento " + doc.getId() + " ha contenuto vuoto o null");
            return;
        }
        
        // Mappa temporanea per contare le occorrenze dei termini nel documento
        Map<String, Integer> termFrequencies = new HashMap<>();
        
        // Tokenizzazione del contenuto del documento
        java.util.regex.Matcher matcher = tokenPattern.matcher(content);
        
        // Conteggio delle frequenze dei termini
        while (matcher.find()) {
            String term = matcher.group();
            
            // Elabora il termine
            term = processTerm(term);
            
            // Salta i termini nulli (stop words)
            if (term == null || term.isEmpty()) {
                continue;
            }
            
            // Incrementa la frequenza del termine
            termFrequencies.put(term, termFrequencies.getOrDefault(term, 0) + 1);
        }
        
        // Aggiungi i termini al dizionario
        for (Map.Entry<String, Integer> entry : termFrequencies.entrySet()) {
            String term = entry.getKey();
            int frequency = entry.getValue();
            
            try {
                // Aggiunge il termine al dizionario
                dictionary.addTerm(term);
                
                // Aggiungi il posting ripetutamente in base alla frequenza
                for (int i = 0; i < frequency; i++) {
                    dictionary.addPosting(term, doc.getId());
                }
            } catch (Exception e) {
                System.err.println("Errore nell'aggiungere il termine '" + term + "' al dizionario: " + e.getMessage());
            }
        }
    }
    
    /**
     * Indicizza tutti i documenti caricati.
     */
    public void indexAllDocuments() {
        System.out.println("Inizio indicizzazione di " + documents.size() + " documenti...");
        
        long startTime = System.currentTimeMillis();
        int processedCount = 0;
        int errorCount = 0;
        
        for (Document doc : documents) {
            try {
                indexDocument(doc);
                processedCount++;
                
                // Mostra progresso ogni 100 documenti
                if (processedCount % 100 == 0) {
                    System.out.println("Indicizzati " + processedCount + " documenti su " + documents.size());
                }
            } catch (Exception e) {
                errorCount++;
                System.err.println("Errore nell'indicizzazione del documento " + doc.getId() + ": " + e.getMessage());
                // Continua con il prossimo documento
            }
        }
        
        long endTime = System.currentTimeMillis();
        
        System.out.println("Indicizzazione completata in " + (endTime - startTime) / 1000.0 + " secondi.");
        System.out.println("Documenti elaborati con successo: " + processedCount);
        System.out.println("Documenti con errori: " + errorCount);
        System.out.println("Termini unici nell'indice: " + dictionary.size());
    }
    
    /**
     * Restituisce la lista dei documenti.
     * 
     * @return La lista dei documenti
     */
    public List<Document> getDocuments() {
        return documents;
    }
    
    /**
     * Restituisce il dizionario dell'indice.
     * 
     * @return Il dizionario
     */
    public Dictionary getDictionary() {
        return dictionary;
    }
    
    /**
     * Cerca un termine nel dizionario.
     * 
     * @param term Il termine da cercare
     * @return La posting list del termine o una lista vuota se non trovato
     */
    public PostingList search(String term) {
        if (term == null || term.isEmpty()) {
            return new PostingList();
        }
        
        // Elabora il termine di ricerca
        String processedTerm = processTerm(term);
        
        if (processedTerm == null || processedTerm.isEmpty()) {
            return new PostingList();
        }
        
        PostingList result = dictionary.getPostingList(processedTerm);
        
        // Ritorna una lista vuota invece di null se il termine non è trovato
        return (result != null) ? result : new PostingList();
    }
}