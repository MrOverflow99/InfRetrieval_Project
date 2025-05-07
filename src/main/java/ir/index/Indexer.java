package ir.index;

import ir.model.Dictionary;
import ir.model.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Gestisce l'indicizzazione dei documenti.
 */
public class Indexer {
    private Dictionary dictionary;
    private StopList stopList;
    private PorterStemmer stemmer;
    private List<Document> documents;
    private boolean useStopList;
    private boolean useStemming;

    /**
     * Costruttore per un nuovo indicizzatore.
     */
    public Indexer() {
        this.dictionary = new Dictionary();
        this.stopList = new StopList();
        this.stemmer = new PorterStemmer();
        this.documents = new ArrayList<>();
        this.useStopList = false;
        this.useStemming = false;
    }

    /**
     * Imposta se utilizzare la stop list durante l'indicizzazione.
     * 
     * @param useStopList true per utilizzare la stop list, false altrimenti
     */
    public void setUseStopList(boolean useStopList) {
        this.useStopList = useStopList;
    }

    /**
     * Imposta se utilizzare lo stemming durante l'indicizzazione.
     * 
     * @param useStemming true per utilizzare lo stemming, false altrimenti
     */
    public void setUseStemming(boolean useStemming) {
        this.useStemming = useStemming;
    }

    /**
     * Carica una stop list da file.
     * 
     * @param filePath Il percorso del file contenente le stop words
     * @throws IOException Se si verifica un errore di I/O
     */
    public void loadStopList(String filePath) throws IOException {
        this.stopList = new StopList(filePath);
        this.useStopList = true;
    }

    /**
     * Carica un documento da un file.
     * 
     * @param file Il file del documento
     * @return Il documento caricato
     * @throws IOException Se si verifica un errore di I/O
     */
    public Document loadDocument(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        
        Document doc = new Document(documents.size() + 1, file.getName(), content.toString());
        documents.add(doc);
        
        return doc;
    }

    /**
     * Carica tutti i documenti da una directory.
     * 
     * @param dirPath Il percorso della directory
     * @throws IOException Se si verifica un errore di I/O
     */
    public void loadDocumentsFromDirectory(String dirPath) throws IOException {
        File dir = new File(dirPath);
        
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IOException("La directory specificata non esiste o non è una directory valida");
        }
        
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && !file.isHidden()) {
                    loadDocument(file);
                }
            }
        }
    }

    /**
     * Indicizza un documento.
     * 
     * @param document Il documento da indicizzare
     */
    public void indexDocument(Document document) {
        String content = document.getContent().toLowerCase();
        
        // Pattern per estrarre le parole (solo lettere e numeri)
        Pattern pattern = Pattern.compile("\\b[a-zA-Z0-9]+\\b");
        Matcher matcher = pattern.matcher(content);
        
        while (matcher.find()) {
            String term = matcher.group().toLowerCase();
            
            // Salta se è una stop word
            if (useStopList && stopList.isStopWord(term)) {
                continue;
            }
            
            // Applica lo stemming se abilitato
            if (useStemming) {
                term = stemmer.stem(term);
            }
            
            // Aggiungi il termine all'indice
            dictionary.addPosting(term, document.getId());
        }
    }

    /**
     * Indicizza tutti i documenti caricati.
     */
    public void indexAllDocuments() {
        for (Document document : documents) {
            indexDocument(document);
        }
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
     * Restituisce la lista dei documenti.
     * 
     * @return La lista dei documenti
     */
    public List<Document> getDocuments() {
        return documents;
    }

    /**
     * Restituisce il documento con l'ID specificato.
     * 
     * @param id L'ID del documento
     * @return Il documento o null se non esiste
     */
    public Document getDocument(int id) {
        for (Document doc : documents) {
            if (doc.getId() == id) {
                return doc;
            }
        }
        return null;
    }
}