package ir.model;

/**
 * Rappresenta un posting nell'indice invertito, che collega un termine a un documento.
 */
public class Posting implements Comparable<Posting> {
    private int documentId;
    private int frequency; // Frequenza del termine nel documento

    /**
     * Costruttore per un nuovo posting.
     * 
     * @param documentId L'ID del documento
     */
    public Posting(int documentId) {
        this.documentId = documentId;
        this.frequency = 1;
    }

    /**
     * Costruttore per un nuovo posting con frequenza specifica.
     * 
     * @param documentId L'ID del documento
     * @param frequency La frequenza del termine nel documento
     */
    public Posting(int documentId, int frequency) {
        this.documentId = documentId;
        this.frequency = frequency;
    }

    /**
     * Restituisce l'ID del documento.
     * 
     * @return L'ID del documento
     */
    public int getDocumentId() {
        return documentId;
    }

    /**
     * Restituisce la frequenza del termine nel documento.
     * 
     * @return La frequenza del termine
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * Incrementa la frequenza del termine nel documento.
     */
    public void incrementFrequency() {
        this.frequency++;
    }

    /**
     * Imposta la frequenza del termine nel documento.
     * 
     * @param frequency La nuova frequenza
     */
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    @Override
    public String toString() {
        return documentId + ":" + frequency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Posting posting = (Posting) o;
        return documentId == posting.documentId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(documentId);
    }

    @Override
    public int compareTo(Posting other) {
        return Integer.compare(this.documentId, other.documentId);
    }
}