package ir.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Rappresenta il dizionario dell'indice invertito.
 */
public class Dictionary {
    private Map<String, Term> terms;
    private Map<String, PostingList> postingLists;

    /**
     * Costruttore per un nuovo dizionario.
     */
    public Dictionary() {
        // Utilizziamo TreeMap per mantenere i termini ordinati alfabeticamente
        this.terms = new TreeMap<>();
        this.postingLists = new HashMap<>();
    }

    /**
     * Aggiunge un termine al dizionario o aggiorna un termine esistente.
     * 
     * @param termText Il testo del termine
     * @return Il termine aggiunto o aggiornato
     */
    public Term addTerm(String termText) {
        Term term = terms.get(termText);
        
        if (term == null) {
            term = new Term(termText);
            terms.put(termText, term);
            postingLists.put(termText, new PostingList());
        }
        
        return term;
    }

    /**
     * Aggiunge un posting per un termine specifico.
     * 
     * @param termText Il testo del termine
     * @param documentId L'ID del documento
     */
    public void addPosting(String termText, int documentId) {
        Term term = addTerm(termText);
        PostingList postingList = postingLists.get(termText);
        
        // Se questo è il primo posting per questo documento, incrementa la document frequency
        Posting existingPosting = postingList.findPosting(documentId);
        if (existingPosting == null) {
            term.incrementDocumentFrequency();
        }
        
        // Aggiungi il posting e incrementa la collection frequency
        postingList.addPosting(documentId);
        term.incrementCollectionFrequency();
    }

    /**
     * Verifica se un termine esiste nel dizionario.
     * 
     * @param termText Il testo del termine
     * @return true se il termine esiste, false altrimenti
     */
    public boolean containsTerm(String termText) {
        return terms.containsKey(termText);
    }

    /**
     * Restituisce un termine dal dizionario.
     * 
     * @param termText Il testo del termine
     * @return Il termine o null se non esiste
     */
    public Term getTerm(String termText) {
        return terms.get(termText);
    }

    /**
     * Restituisce la lista di posting per un termine specifico.
     * 
     * @param termText Il testo del termine
     * @return La lista di posting o null se il termine non esiste
     */
    public PostingList getPostingList(String termText) {
        return postingLists.get(termText);
    }

    /**
     * Restituisce tutti i termini nel dizionario.
     * 
     * @return L'insieme di tutti i termini
     */
    public Set<String> getTerms() {
        return terms.keySet();
    }

    /**
     * Restituisce il numero di termini nel dizionario.
     * 
     * @return Il numero di termini
     */
    public int size() {
        return terms.size();
    }

    /**
     * Restituisce una mappa delle frequenze dei documenti per tutti i termini.
     * 
     * @return Una mappa da termini a frequenze di documenti
     */
    public Map<String, Integer> getDocumentFrequencies() {
        Map<String, Integer> frequencies = new HashMap<>();
        
        for (Map.Entry<String, Term> entry : terms.entrySet()) {
            frequencies.put(entry.getKey(), entry.getValue().getDocumentFrequency());
        }
        
        return frequencies;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        for (String termText : terms.keySet()) {
            Term term = terms.get(termText);
            PostingList postingList = postingLists.get(termText);
            
            sb.append(term).append(" -> ").append(postingList).append("\n");
        }
        
        return sb.toString();
    }
}