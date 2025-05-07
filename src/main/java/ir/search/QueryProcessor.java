package ir.search;

import ir.index.PorterStemmer;
import ir.index.StopList;
import ir.model.Dictionary;
import ir.model.PostingList;

import java.util.ArrayList;
import java.util.List;

/**
 * Processa le query per il recupero dei documenti.
 */
public class QueryProcessor {
    protected Dictionary dictionary;
    protected StopList stopList;
    protected PorterStemmer stemmer;
    protected boolean useStopList;
    protected boolean useStemming;

    /**
     * Costruttore per un nuovo processore di query.
     * 
     * @param dictionary Il dizionario dell'indice
     */
    public QueryProcessor(Dictionary dictionary) {
        this.dictionary = dictionary;
        this.stopList = new StopList();
        this.stemmer = new PorterStemmer();
        this.useStopList = false;
        this.useStemming = false;
    }

    /**
     * Imposta se utilizzare la stop list durante la ricerca.
     * 
     * @param useStopList true per utilizzare la stop list, false altrimenti
     */
    public void setUseStopList(boolean useStopList) {
        this.useStopList = useStopList;
    }

    /**
     * Imposta se utilizzare lo stemming durante la ricerca.
     * 
     * @param useStemming true per utilizzare lo stemming, false altrimenti
     */
    public void setUseStemming(boolean useStemming) {
        this.useStemming = useStemming;
    }

    /**
     * Imposta la stop list da utilizzare.
     * 
     * @param stopList La stop list
     */
    public void setStopList(StopList stopList) {
        this.stopList = stopList;
    }

    /**
     * Processa una query a termine singolo.
     * 
     * @param term Il termine da cercare
     * @return La lista di posting per il termine
     */
    public PostingList processTerm(String term) {
        term = term.toLowerCase();
        
        // Salta se è una stop word
        if (useStopList && stopList.isStopWord(term)) {
            return new PostingList();
        }
        
        // Applica lo stemming se abilitato
        if (useStemming) {
            term = stemmer.stem(term);
        }
        
        // Cerca il termine nel dizionario
        return dictionary.getPostingList(term) != null ? 
               dictionary.getPostingList(term) : new PostingList();
    }

    /**
     * Processa una query congiuntiva (AND).
     * 
     * @param terms I termini della query
     * @return La lista di posting risultante dall'intersezione
     */
    public PostingList processConjunctiveQuery(String[] terms) {
        if (terms.length == 0) {
            return new PostingList();
        }
        
        List<String> validTerms = new ArrayList<>();
        
        // Elabora i termini
        for (String term : terms) {
            term = term.toLowerCase();
            
            // Salta se è una stop word
            if (useStopList && stopList.isStopWord(term)) {
                continue;
            }
            
            // Applica lo stemming se abilitato
            if (useStemming) {
                term = stemmer.stem(term);
            }
            
            if (dictionary.containsTerm(term)) {
                validTerms.add(term);
            }
        }
        
        if (validTerms.isEmpty()) {
            return new PostingList();
        }
        
        // Prendi la posting list del primo termine
        PostingList result = dictionary.getPostingList(validTerms.get(0));
        
        // Interseca con le posting list degli altri termini
        for (int i = 1; i < validTerms.size(); i++) {
            PostingList nextList = dictionary.getPostingList(validTerms.get(i));
            result = result.intersect(nextList);
            
            // Se la posting list è vuota, non serve continuare
            if (result.isEmpty()) {
                break;
            }
        }
        
        return result;
    }

    /**
     * Processa una query disgiuntiva (OR).
     * 
     * @param terms I termini della query
     * @return La lista di posting risultante dall'unione
     */
    public PostingList processDisjunctiveQuery(String[] terms) {
        if (terms.length == 0) {
            return new PostingList();
        }
        
        PostingList result = new PostingList();
        
        // Elabora i termini
        for (String term : terms) {
            term = term.toLowerCase();
            
            // Salta se è una stop word
            if (useStopList && stopList.isStopWord(term)) {
                continue;
            }
            
            // Applica lo stemming se abilitato
            if (useStemming) {
                term = stemmer.stem(term);
            }
            
            if (dictionary.containsTerm(term)) {
                PostingList termList = dictionary.getPostingList(term);
                result = result.union(termList);
            }
        }
        
        return result;
    }
}