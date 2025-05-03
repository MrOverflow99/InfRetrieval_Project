package ir.model;

/**
 * Rappresenta un documento all'interno del sistema di Information Retrieval.
 */
public class Document {
    private int id;
    private String name;
    private String content;

    /**
     * Costruttore per un nuovo documento.
     * 
     * @param id L'identificatore univoco del documento
     * @param name Il nome del documento
     * @param content Il contenuto testuale del documento
     */
    public Document(int id, String name, String content) {
        this.id = id;
        this.name = name;
        this.content = content;
    }

    /**
     * Restituisce l'ID del documento.
     * 
     * @return L'ID del documento
     */
    public int getId() {
        return id;
    }

    /**
     * Restituisce il nome del documento.
     * 
     * @return Il nome del documento
     */
    public String getName() {
        return name;
    }

    /**
     * Restituisce il contenuto del documento.
     * 
     * @return Il contenuto testuale del documento
     */
    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return id == document.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}