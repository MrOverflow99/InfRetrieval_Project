package ir.util;

import ir.model.Dictionary;
import ir.model.Document;
import ir.model.Posting;
import ir.model.PostingList;
import ir.model.Term;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility per la serializzazione e deserializzazione dell'indice invertito.
 */
public class IndexSerializer {
    
    /**
     * Serializza un dizionario su file.
     * 
     * @param dictionary Il dizionario da serializzare
     * @param filePath Il percorso del file di output
     * @throws IOException Se si verifica un errore di I/O
     */
    public static void serializeDictionary(Dictionary dictionary, String filePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            // Creiamo una rappresentazione serializzabile del dizionario
            Map<String, SerializableTerm> terms = new HashMap<>();
            Map<String, List<SerializablePosting>> postings = new HashMap<>();
            
            for (String termText : dictionary.getTerms()) {
                Term term = dictionary.getTerm(termText);
                terms.put(termText, new SerializableTerm(
                        termText, 
                        term.getDocumentFrequency(), 
                        term.getCollectionFrequency()));
                
                PostingList postingList = dictionary.getPostingList(termText);
                List<SerializablePosting> serializablePostings = new ArrayList<>();
                
                for (Posting posting : postingList) {
                    serializablePostings.add(new SerializablePosting(
                            posting.getDocumentId(), 
                            posting.getFrequency()));
                }
                
                postings.put(termText, serializablePostings);
            }
            
            // Scriviamo l'indice serializzato
            SerializableIndex index = new SerializableIndex(terms, postings);
            oos.writeObject(index);
        }
    }
    
    /**
     * Deserializza un dizionario da file.
     * 
     * @param filePath Il percorso del file da cui deserializzare
     * @return Il dizionario deserializzato
     * @throws IOException Se si verifica un errore di I/O
     * @throws ClassNotFoundException Se la classe serializzata non è trovata
     */
    public static Dictionary deserializeDictionary(String filePath) throws IOException, ClassNotFoundException {
        Dictionary dictionary = new Dictionary();
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            SerializableIndex index = (SerializableIndex) ois.readObject();
            
            // Ricostruiamo il dizionario
            for (Map.Entry<String, SerializableTerm> entry : index.getTerms().entrySet()) {
                String termText = entry.getKey();
                SerializableTerm serTerm = entry.getValue();
                
                Term term = dictionary.addTerm(termText);
                term.setDocumentFrequency(serTerm.getDocumentFrequency());
                term.setCollectionFrequency(serTerm.getCollectionFrequency());
                
                // Ricostruiamo la lista di posting
                List<SerializablePosting> serPostings = index.getPostings().get(termText);
                PostingList postingList = dictionary.getPostingList(termText);
                
                for (SerializablePosting serPosting : serPostings) {
                    for (int i = 0; i < serPosting.getFrequency(); i++) {
                        postingList.addPosting(serPosting.getDocumentId());
                    }
                }
            }
        }
        
        return dictionary;
    }
    
    /**
     * Serializza l'elenco dei documenti su file.
     * 
     * @param documents La lista dei documenti
     * @param filePath Il percorso del file di output
     * @throws IOException Se si verifica un errore di I/O
     */
    public static void serializeDocuments(List<Document> documents, String filePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            List<SerializableDocument> serializableDocuments = new ArrayList<>();
            
            for (Document doc : documents) {
                serializableDocuments.add(new SerializableDocument(
                        doc.getId(),
                        doc.getName(),
                        doc.getContent()));
            }
            
            oos.writeObject(serializableDocuments);
        }
    }
    
    /**
     * Deserializza l'elenco dei documenti da file.
     * 
     * @param filePath Il percorso del file da cui deserializzare
     * @return La lista dei documenti deserializzati
     * @throws IOException Se si verifica un errore di I/O
     * @throws ClassNotFoundException Se la classe serializzata non è trovata
     */
    @SuppressWarnings("unchecked")
    public static List<Document> deserializeDocuments(String filePath) throws IOException, ClassNotFoundException {
        List<Document> documents = new ArrayList<>();
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            List<SerializableDocument> serDocs = (List<SerializableDocument>) ois.readObject();
            
            for (SerializableDocument serDoc : serDocs) {
                documents.add(new Document(
                        serDoc.getId(),
                        serDoc.getName(),
                        serDoc.getContent()));
            }
        }
        
        return documents;
    }
    
    /**
     * Classe per la serializzazione di un indice.
     */
    private static class SerializableIndex implements Serializable {
        private static final long serialVersionUID = 1L;
        private Map<String, SerializableTerm> terms;
        private Map<String, List<SerializablePosting>> postings;
        
        public SerializableIndex(Map<String, SerializableTerm> terms, Map<String, List<SerializablePosting>> postings) {
            this.terms = terms;
            this.postings = postings;
        }
        
        public Map<String, SerializableTerm> getTerms() {
            return terms;
        }
        
        public Map<String, List<SerializablePosting>> getPostings() {
            return postings;
        }
    }
    
    /**
     * Classe per la serializzazione di un termine.
     */
    private static class SerializableTerm implements Serializable {
        private static final long serialVersionUID = 1L;
        private String text;
        private int documentFrequency;
        private int collectionFrequency;
        
        public SerializableTerm(String text, int documentFrequency, int collectionFrequency) {
            this.text = text;
            this.documentFrequency = documentFrequency;
            this.collectionFrequency = collectionFrequency;
        }
        
        public String getText() {
            return text;
        }
        
        public int getDocumentFrequency() {
            return documentFrequency;
        }
        
        public int getCollectionFrequency() {
            return collectionFrequency;
        }
    }
    
    /**
     * Classe per la serializzazione di un posting.
     */
    private static class SerializablePosting implements Serializable {
        private static final long serialVersionUID = 1L;
        private int documentId;
        private int frequency;
        
        public SerializablePosting(int documentId, int frequency) {
            this.documentId = documentId;
            this.frequency = frequency;
        }
        
        public int getDocumentId() {
            return documentId;
        }
        
        public int getFrequency() {
            return frequency;
        }
    }
    
    /**
     * Classe per la serializzazione di un documento.
     */
    private static class SerializableDocument implements Serializable {
        private static final long serialVersionUID = 1L;
        private int id;
        private String name;
        private String content;
        
        public SerializableDocument(int id, String name, String content) {
            this.id = id;
            this.name = name;
            this.content = content;
        }
        
        public int getId() {
            return id;
        }
        
        public String getName() {
            return name;
        }
        
        public String getContent() {
            return content;
        }
    }
}