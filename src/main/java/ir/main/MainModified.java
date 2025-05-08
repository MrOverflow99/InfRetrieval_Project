package ir.main;

import ir.index.Indexer;
import ir.index.StopList;
import ir.model.Dictionary;
import ir.model.Document;
import ir.model.Posting;
import ir.model.PostingList;
import ir.search.OptimizedQueryProcessor;
import ir.search.QueryProcessor;
import ir.util.DirectoryChecker;
import ir.util.FileLoader;
import ir.util.IndexSerializer;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;


public class MainModified {
    private static final String DOCUMENTS_PATH = "src/main/resources/documents/archive/TUTTO";
    private static final String STOPWORDS_PATH = "src/main/resources/stopwords.txt";
    private static final String INDEX_PATH = "src/main/resources/index.ser";
    private static final String DOCUMENTS_INDEX_PATH = "src/main/resources/documents.ser";
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Sistema di Information Retrieval");
        System.out.println("================================");
        
        try {
            // NUOVO: Verifica il percorso dei documenti
            System.out.println("\nVerifica del percorso dei documenti:");
            int filesFound = DirectoryChecker.checkDirectory(DOCUMENTS_PATH);
            
            if (filesFound == 0) {
                System.out.println("\nATTENZIONE: Nessun file .txt trovato nel percorso specificato!");
                System.out.println("Vuoi cercare percorsi alternativi? (S/N): ");
                String choice = scanner.nextLine().trim().toUpperCase();
                
                if (choice.equals("S")) {
                    DirectoryChecker.suggestAlternativePaths(DOCUMENTS_PATH);
                    System.out.println("\nVuoi continuare comunque? (S/N): ");
                    if (!scanner.nextLine().trim().toUpperCase().equals("S")) {
                        System.out.println("Applicazione terminata.");
                        scanner.close();
                        return;
                    }
                }
            } else {
                System.out.println("Trovati " + filesFound + " file .txt nel percorso specificato.");
            }
            
            // Il resto del codice originale
            boolean indexExists = new File(INDEX_PATH).exists() && new File(DOCUMENTS_INDEX_PATH).exists();
            Dictionary dictionary;
            List<Document> documents;
            
            if (indexExists) {
                System.out.println("Caricamento dell'indice esistente...");
                dictionary = IndexSerializer.deserializeDictionary(INDEX_PATH);
                documents = IndexSerializer.deserializeDocuments(DOCUMENTS_INDEX_PATH);
                System.out.println("Indice caricato con successo.");
                
                // NUOVO: Verifica che ci siano effettivamente documenti
                if (documents == null || documents.isEmpty()) {
                    System.out.println("ATTENZIONE: Nessun documento trovato nell'indice!");
                    System.out.println("Vuoi ricreare l'indice? (S/N): ");
                    if (scanner.nextLine().trim().toUpperCase().equals("S")) {
                        System.out.println("Eliminazione dell'indice esistente...");
                        new File(INDEX_PATH).delete();
                        new File(DOCUMENTS_INDEX_PATH).delete();
                        
                        System.out.println("Creazione di un nuovo indice...");
                        dictionary = createIndex();
                        documents = loadDocuments();
                    }
                }
            } else {
                System.out.println("Creazione di un nuovo indice...");
                dictionary = createIndex();
                documents = loadDocuments();
                System.out.println("Indice creato con successo.");
            }
            
            System.out.println("Statistiche dell'indice:");
            System.out.println("- Numero di termini: " + dictionary.size());
            System.out.println("- Numero di documenti: " + documents.size());
            
            // Resto del codice come l'originale...
            // Crea i processori di query
            QueryProcessor standardProcessor = new QueryProcessor(dictionary);
            OptimizedQueryProcessor optimizedProcessor = new OptimizedQueryProcessor(dictionary);
            
            // Carica la stop list se esiste
            if (FileLoader.isFileReadable(STOPWORDS_PATH)) {
                StopList stopList = new StopList(STOPWORDS_PATH);
                standardProcessor.setStopList(stopList);
                standardProcessor.setUseStopList(true);
                optimizedProcessor.setStopList(stopList);
                optimizedProcessor.setUseStopList(true);
            }
            
            // Attiva lo stemming
            standardProcessor.setUseStemming(true);
            optimizedProcessor.setUseStemming(true);
            
            // Interfaccia utente interattiva
            boolean running = true;
            while (running) {
                System.out.println("\nOpzioni:");
                System.out.println("1. Esegui una query");
                System.out.println("2. Mostra dettagli di un documento");
                System.out.println("3. Mostra statistiche dell'indice");
                System.out.println("4. Esci");
                System.out.print("\nScegli un'opzione (1-4): ");
                
                String choice = scanner.nextLine();
                
                switch (choice) {
                    case "1":
                        executeQuery(scanner, standardProcessor, optimizedProcessor, documents);
                        break;
                    case "2":
                        showDocumentDetails(scanner, documents);
                        break;
                    case "3":
                        showIndexStatistics(dictionary);
                        break;
                    case "4":
                        running = false;
                        break;
                    default:
                        System.out.println("Opzione non valida. Riprova.");
                }
            }
            
        } catch (Exception e) {
            System.err.println("Errore: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
    
    // Il resto dei metodi come l'originale...
    
    /**
     * Crea un nuovo indice invertito.
     * 
     * @return Il dizionario dell'indice
     * @throws IOException Se si verifica un errore di I/O
     */
    private static Dictionary createIndex() throws IOException {
        Indexer indexer = new Indexer();
        
        // Carica la stop list se esiste
        if (FileLoader.isFileReadable(STOPWORDS_PATH)) {
            indexer.loadStopList(STOPWORDS_PATH);
            indexer.setUseStopList(true);
        }
        
        // Attiva lo stemming
        indexer.setUseStemming(true);
        
        // Carica e indicizza i documenti
        indexer.loadDocumentsFromDirectory(DOCUMENTS_PATH);
        indexer.indexAllDocuments();
        
        // Serializza l'indice per uso futuro
        IndexSerializer.serializeDictionary(indexer.getDictionary(), INDEX_PATH);
        IndexSerializer.serializeDocuments(indexer.getDocuments(), DOCUMENTS_INDEX_PATH);
        
        return indexer.getDictionary();
    }
    
    /**
     * Carica i documenti dall'indice serializzato.
     * 
     * @return La lista dei documenti
     * @throws IOException Se si verifica un errore di I/O
     * @throws ClassNotFoundException Se la classe serializzata non è trovata
     */
    private static List<Document> loadDocuments() throws IOException, ClassNotFoundException {
        if (FileLoader.isFileReadable(DOCUMENTS_INDEX_PATH)) {
            return IndexSerializer.deserializeDocuments(DOCUMENTS_INDEX_PATH);
        } else {
            return FileLoader.loadDocumentsFromDirectory(DOCUMENTS_PATH);
        }
    }
    
    /**
     * Esegue una query e visualizza i risultati.
     * 
     * @param scanner Lo scanner per l'input
     * @param standardProcessor Il processore di query standard
     * @param optimizedProcessor Il processore di query ottimizzato
     * @param documents La lista dei documenti
     */
    private static void executeQuery(Scanner scanner, QueryProcessor standardProcessor, 
                                    OptimizedQueryProcessor optimizedProcessor, List<Document> documents) {
        System.out.print("\nInserisci la query (termini separati da spazio): ");
        String query = scanner.nextLine().trim();
        
        if (query.isEmpty()) {
            System.out.println("Query vuota. Riprova.");
            return;
        }
        
        System.out.print("Tipo di query (AND/OR): ");
        String queryType = scanner.nextLine().trim().toUpperCase();
        
        System.out.print("Usa processore ottimizzato (S/N): ");
        boolean useOptimized = scanner.nextLine().trim().equalsIgnoreCase("S");
        
        long startTime = System.nanoTime();
        
        PostingList result;
        String[] terms = query.split("\\s+");
        
        if (useOptimized) {
            if (queryType.equals("AND")) {
                result = optimizedProcessor.processConjunctiveQuery(terms);
            } else {
                result = optimizedProcessor.processDisjunctiveQuery(terms);
            }
        } else {
            if (queryType.equals("AND")) {
                result = standardProcessor.processConjunctiveQuery(terms);
            } else {
                result = standardProcessor.processDisjunctiveQuery(terms);
            }
        }
        
        long endTime = System.nanoTime();
        double executionTime = (endTime - startTime) / 1_000_000.0; // in millisecondi
        
        System.out.println("\nRisultati della query (" + result.size() + " documenti trovati in " + executionTime + " ms):");
        
        if (result.isEmpty()) {
            System.out.println("Nessun documento trovato.");
        } else {
            int count = 0;
            for (Posting posting : result) {
                int docId = posting.getDocumentId();
                Document doc = findDocumentById(documents, docId);
                if (doc != null) {
                    count++;
                    System.out.println(count + ". " + doc.getName() + " (ID: " + doc.getId() + ", Score: " + posting.getFrequency() + ")");
                    
                    if (count == 10) {
                        System.out.print("\nMostrare altri risultati? (S/N): ");
                        if (!scanner.nextLine().trim().equalsIgnoreCase("S")) {
                            break;
                        }
                        count = 0;
                    }
                }
            }
        }
    }
    
    /**
     * Mostra i dettagli di un documento specifico.
     * 
     * @param scanner Lo scanner per l'input
     * @param documents La lista dei documenti
     */
    private static void showDocumentDetails(Scanner scanner, List<Document> documents) {
        System.out.print("\nInserisci l'ID del documento: ");
        try {
            int docId = Integer.parseInt(scanner.nextLine().trim());
            Document doc = findDocumentById(documents, docId);
            
            if (doc != null) {
                System.out.println("\nDettagli del documento:");
                System.out.println("ID: " + doc.getId());
                System.out.println("Nome: " + doc.getName());
                System.out.println("\nContenuto:\n----------");
                
                // Mostra un'anteprima del contenuto (primi 500 caratteri)
                String content = doc.getContent();
                if (content.length() > 500) {
                    System.out.println(content.substring(0, 500) + "...");
                    System.out.print("\nMostrare il contenuto completo? (S/N): ");
                    if (scanner.nextLine().trim().equalsIgnoreCase("S")) {
                        System.out.println("\nContenuto completo:\n-----------------");
                        System.out.println(content);
                    }
                } else {
                    System.out.println(content);
                }
            } else {
                System.out.println("Documento non trovato.");
            }
        } catch (NumberFormatException e) {
            System.out.println("ID non valido. Deve essere un numero intero.");
        }
    }
    
    /**
     * Mostra le statistiche dell'indice.
     * 
     * @param dictionary Il dizionario dell'indice
     */
    private static void showIndexStatistics(Dictionary dictionary) {
        System.out.println("\nStatistiche dell'indice:");
        System.out.println("- Numero di termini unici: " + dictionary.size());
        
        // Trova i termini più frequenti
        String[] mostFrequentTerms = dictionary.getTerms().stream()
                .sorted((t1, t2) -> {
                    int cf1 = dictionary.getTerm(t1).getCollectionFrequency();
                    int cf2 = dictionary.getTerm(t2).getCollectionFrequency();
                    return Integer.compare(cf2, cf1); // ordine decrescente
                })
                .limit(10)
                .toArray(String[]::new);
        
        System.out.println("\nTermini più frequenti:");
        for (int i = 0; i < mostFrequentTerms.length; i++) {
            String term = mostFrequentTerms[i];
            int cf = dictionary.getTerm(term).getCollectionFrequency();
            int df = dictionary.getTerm(term).getDocumentFrequency();
            System.out.println((i + 1) + ". " + term + " (CF: " + cf + ", DF: " + df + ")");
        }
    }
    
    /**
     * Trova un documento per ID.
     * 
     * @param documents La lista dei documenti
     * @param id L'ID del documento
     * @return Il documento trovato o null
     */
    private static Document findDocumentById(List<Document> documents, int id) {
        for (Document doc : documents) {
            if (doc.getId() == id) {
                return doc;
            }
        }
        return null;
    }
}