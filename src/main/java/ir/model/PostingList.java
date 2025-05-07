package ir.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Rappresenta una lista di posting per un termine specifico.
 */
public class PostingList implements Iterable<Posting> {
    private List<Posting> postings;

    /**
     * Costruttore per una nuova lista di posting.
     */
    public PostingList() {
        this.postings = new ArrayList<>();
    }

    /**
     * Aggiunge un nuovo posting alla lista.
     * 
     * @param documentId L'ID del documento
     */
    public void addPosting(int documentId) {
        Posting posting = new Posting(documentId);
        int index = Collections.binarySearch(postings, posting);
        
        if (index >= 0) {
            // Il documento è già presente, incrementa la frequenza
            postings.get(index).incrementFrequency();
        } else {
            // Il documento non è ancora presente, aggiungi un nuovo posting
            postings.add(~index, posting);
        }
    }

    /**
     * Aggiunge un posting esistente alla lista.
     * 
     * @param posting Il posting da aggiungere
     */
    public void addPosting(Posting posting) {
        int index = Collections.binarySearch(postings, posting);
        
        if (index >= 0) {
            // Il documento è già presente, aggiorna la frequenza
            postings.get(index).setFrequency(posting.getFrequency());
        } else {
            // Il documento non è ancora presente, aggiungi il nuovo posting
            postings.add(~index, posting);
        }
    }

    /**
     * Restituisce la dimensione della lista di posting.
     * 
     * @return Il numero di posting nella lista
     */
    public int size() {
        return postings.size();
    }

    /**
     * Verifica se la lista di posting è vuota.
     * 
     * @return true se la lista è vuota, false altrimenti
     */
    public boolean isEmpty() {
        return postings.isEmpty();
    }

    /**
     * Restituisce tutti i posting nella lista.
     * 
     * @return La lista di posting
     */
    public List<Posting> getPostings() {
        return Collections.unmodifiableList(postings);
    }

    /**
     * Restituisce un posting ad un indice specifico.
     * 
     * @param index L'indice del posting
     * @return Il posting all'indice specificato
     */
    public Posting getPosting(int index) {
        return postings.get(index);
    }

    /**
     * Cerca un posting per un documento specifico.
     * 
     * @param documentId L'ID del documento
     * @return Il posting per il documento o null se non esiste
     */
    public Posting findPosting(int documentId) {
        Posting searchKey = new Posting(documentId);
        int index = Collections.binarySearch(postings, searchKey);
        return index >= 0 ? postings.get(index) : null;
    }

    /**
     * Interseca questa lista di posting con un'altra.
     * 
     * @param other L'altra lista di posting
     * @return Una nuova lista di posting contenente l'intersezione
     */
    public PostingList intersect(PostingList other) {
        PostingList result = new PostingList();
        
        if (this.isEmpty() || other.isEmpty()) {
            return result;
        }
        
        int i = 0, j = 0;
        while (i < this.size() && j < other.size()) {
            Posting p1 = this.getPosting(i);
            Posting p2 = other.getPosting(j);
            
            if (p1.getDocumentId() == p2.getDocumentId()) {
                // Documento comune, aggiungi alla lista risultante
                result.addPosting(new Posting(p1.getDocumentId(), p1.getFrequency() + p2.getFrequency()));
                i++;
                j++;
            } else if (p1.getDocumentId() < p2.getDocumentId()) {
                i++;
            } else {
                j++;
            }
        }
        
        return result;
    }

    /**
     * Unisce questa lista di posting con un'altra.
     * 
     * @param other L'altra lista di posting
     * @return Una nuova lista di posting contenente l'unione
     */
    public PostingList union(PostingList other) {
        PostingList result = new PostingList();
        
        if (this.isEmpty()) {
            return other;
        }
        
        if (other.isEmpty()) {
            return this;
        }
        
        int i = 0, j = 0;
        while (i < this.size() && j < other.size()) {
            Posting p1 = this.getPosting(i);
            Posting p2 = other.getPosting(j);
            
            if (p1.getDocumentId() == p2.getDocumentId()) {
                // Documento comune, aggiungi alla lista risultante con frequenza combinata
                result.addPosting(new Posting(p1.getDocumentId(), p1.getFrequency() + p2.getFrequency()));
                i++;
                j++;
            } else if (p1.getDocumentId() < p2.getDocumentId()) {
                result.addPosting(new Posting(p1.getDocumentId(), p1.getFrequency()));
                i++;
            } else {
                result.addPosting(new Posting(p2.getDocumentId(), p2.getFrequency()));
                j++;
            }
        }
        
        // Aggiungi i posting rimanenti dalla prima lista
        while (i < this.size()) {
            Posting p = this.getPosting(i);
            result.addPosting(new Posting(p.getDocumentId(), p.getFrequency()));
            i++;
        }
        
        // Aggiungi i posting rimanenti dalla seconda lista
        while (j < other.size()) {
            Posting p = other.getPosting(j);
            result.addPosting(new Posting(p.getDocumentId(), p.getFrequency()));
            j++;
        }
        
        return result;
    }

    @Override
    public Iterator<Posting> iterator() {
        return postings.iterator();
    }

    @Override
    public String toString() {
        return postings.toString();
    }
}