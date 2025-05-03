package ir.model;


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

  
    public int getId() {
        return id;
    }

   
    public String getName() {
        return name;
    }

    
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