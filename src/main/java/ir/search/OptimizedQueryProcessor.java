package ir.search;

import ir.index.PorterStemmer;
import ir.index.StopList;
import ir.model.Dictionary;
import ir.model.PostingList;
import ir.model.Term;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Processore di query ottimizzato che riordina i termini in base alla frequenza dei documenti.
 */
public class OptimizedQueryProcessor extends QueryProcessor {
    /**
     * Costruttore per un nuovo processore di query ottimizzato.
     * 
     * @param dictionary Il dizionario dell'indice
     */
    public OptimizedQueryProcessor(Dictionary dictionary) {
        super(dictionary);
    }

    /**
     * Processa una query congiuntiva (AND) con ottimizzazione.
     * I termini vengono ordinati in ordine crescente di frequenza dei documenti.
     * 
     * @param terms I termini della query
     * @return La lista di posting risultante dall'intersezione
     */
    @Override
    public PostingList processConjunctiveQuery(String[] terms) {
        if (terms.length == 0) {
            return new PostingList();
        }
        
        Dictionary dictionary = getDictionary();
        StopList stopList = getStopList();
        PorterStemmer stemmer = getStemmer();
        boolean useStopList = isUseStopList();
        boolean useStemming = isUseStemming();
        
        // Memorizza i termini validi con le loro frequenze di documento
        List<TermFrequency> validTerms = new ArrayList<>();
        
        // Elabora i termini
        for (String termText : terms) {
            termText = termText.toLowerCase();
            
            // Salta se è una stop word
            if (useStopList && stopList.isStopWord(termText)) {
                continue;
            }
            
            // Applica lo stemming se abilitato
            if (useStemming) {
                termText = stemmer.stem(termText);
            }
            
            if (dictionary.containsTerm(termText)) {
                Term term = dictionary.getTerm(termText);
                int docFreq = term.getDocumentFrequency();
                validTerms.add(new TermFrequency(termText, docFreq));
            }
        }
        
        if (validTerms.isEmpty()) {
            return new PostingList();
        }
        
        // Ordina i termini in base alla frequenza dei documenti (crescente)
        validTerms.sort(Comparator.comparingInt(TermFrequency::getFrequency));
        
        // Prendi la posting list del primo termine (quello con la frequenza più bassa)
        PostingList result = dictionary.getPostingList(validTerms.get(0).getTerm());
        
        // Interseca con le posting list degli altri termini
        for (int i = 1; i < validTerms.size(); i++) {
            PostingList nextList = dictionary.getPostingList(validTerms.get(i).getTerm());
            result = result.intersect(nextList);
            
            // Se la posting list è vuota, non serve continuare
            if (result.isEmpty()) {
                break;
            }
        }
        
        return result;
    }
    
    // Classe di supporto per memorizzare un termine con la sua frequenza
    private static class TermFrequency {
        private String term;
        private int frequency;
        
        public TermFrequency(String term, int frequency) {
            this.term = term;
            this.frequency = frequency;
        }
        
        public String getTerm() {
            return term;
        }
        
        public int getFrequency() {
            return frequency;
        }
    }
    
    // Metodi di accesso per le variabili protette della classe padre
    private Dictionary getDictionary() {
        return super.dictionary;
    }
    
    private StopList getStopList() {
        return super.stopList;
    }
    
    private PorterStemmer getStemmer() {
        return super.stemmer;
    }
    
    private boolean isUseStopList() {
        return super.useStopList;
    }
    
    private boolean isUseStemming() {
        return super.useStemming;
    }
}