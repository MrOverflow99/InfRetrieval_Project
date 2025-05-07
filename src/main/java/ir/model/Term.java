package ir.model;

/**
 * Rappresenta un termine nel vocabolario dell'indice invertito.
 */
public class Term implements Comparable<Term> {
    private String text;
    private int documentFrequency; // Numero di documenti in cui appare il termine
    private int collectionFrequency; // Numero totale di occorrenze del termine nella collezione

    /**
     * Costruttore per un nuovo termine.
     * 
     * @param text Il testo del termine
     */
    public Term(String text) {
        this.text = text;
        this.documentFrequency = 0;
        this.collectionFrequency = 0;
    }

    /**
     * Restituisce il testo del termine.
     * 
     * @return Il testo del termine
     */
    public String getText() {
        return text;
    }

    /**
     * Restituisce la frequenza del documento per questo termine.
     * 
     * @return Il numero di documenti in cui appare il termine
     */
    public int getDocumentFrequency() {
        return documentFrequency;
    }

    /**
     * Incrementa la frequenza del documento.
     */
    public void incrementDocumentFrequency() {
        this.documentFrequency++;
    }

    /**
     * Imposta la frequenza del documento.
     * 
     * @param documentFrequency La nuova frequenza del documento
     */
    public void setDocumentFrequency(int documentFrequency) {
        this.documentFrequency = documentFrequency;
    }

    /**
     * Restituisce la frequenza di collezione per questo termine.
     * 
     * @return Il numero totale di occorrenze del termine nella collezione
     */
    public int getCollectionFrequency() {
        return collectionFrequency;
    }

    /**
     * Incrementa la frequenza di collezione.
     */
    public void incrementCollectionFrequency() {
        this.collectionFrequency++;
    }

    /**
     * Incrementa la frequenza di collezione di un valore specifico.
     * 
     * @param increment Il valore di incremento
     */
    public void incrementCollectionFrequency(int increment) {
        this.collectionFrequency += increment;
    }

    /**
     * Imposta la frequenza di collezione.
     * 
     * @param collectionFrequency La nuova frequenza di collezione
     */
    public void setCollectionFrequency(int collectionFrequency) {
        this.collectionFrequency = collectionFrequency;
    }

    @Override
    public String toString() {
        return text + " (df=" + documentFrequency + ", cf=" + collectionFrequency + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Term term = (Term) o;
        return text.equals(term.text);
    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }

    @Override
    public int compareTo(Term other) {
        return this.text.compareTo(other.text);
    }
}