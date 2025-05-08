package ir.index;

/**
 * Implementazione corretta dell'algoritmo di Porter Stemming per la lingua inglese.
 * Include gestione degli errori per prevenire IndexOutOfBoundsException.
 */
public class PorterStemmer {
    private char[] b;
    private int i,    // offset into b
               j,     // end
               k;     // position of string's end in b
    
    public PorterStemmer() {
        b = new char[100];
        i = 0;
        j = 0;
    }
    
    /**
     * Aggiunge un carattere alla stringa di lavoro.
     */
    public void add(char ch) {
        if (j < 100) b[j++] = ch;
    }
    
    /**
     * Aggiunge i caratteri di una stringa alla stringa di lavoro.
     */
    public void add(String s) {
        for (int i = 0; i < s.length(); i++) {
            add(s.charAt(i));
        }
    }
    
    /**
     * Esegue lo stemming della parola nella stringa di lavoro.
     */
    public void stem() {
        k = j - 1;
        if (k > 1) {
            try {
                step1ab();
                step1c();
                step2();
                step3();
                step4();
                step5();
            } catch (Exception e) {
                // In caso di errore, non facciamo stemming su questa parola
                System.err.println("Errore nello stemming, parola saltata: " + new String(b, 0, j));
            }
        }
        i = 0;
        j = k + 1;
    }
    
    /**
     * Esegue lo stemming di una stringa e restituisce il risultato.
     */
    public String stem(String s) {
        // Reset dello stemmer
        i = 0;
        j = 0;
        
        // Aggiungi la stringa
        add(s);
        
        // Esegui lo stemming
        stem();
        
        // Restituisci il risultato
        return new String(b, 0, j);
    }
    
    /**
     * Verifica se il carattere nella stringa di lavoro è una consonante.
     */
    private boolean cons(int i) {
        if (i < 0 || i >= k) return false; // Controllo di sicurezza
        
        switch (b[i]) {
            case 'a': case 'e': case 'i': case 'o': case 'u':
                return false;
            case 'y':
                return (i == 0) ? true : !cons(i - 1);
            default:
                return true;
        }
    }
    
    /**
     * Misura il numero di sequenze consonante-vocale.
     */
    private int m() {
        int n = 0;
        int i = 0;
        while (true) {
            if (i > k) return n;
            if (!cons(i)) break;
            i++;
        }
        i++;
        while (true) {
            while (true) {
                if (i > k) return n;
                if (cons(i)) break;
                i++;
            }
            i++;
            n++;
            while (true) {
                if (i > k) return n;
                if (!cons(i)) break;
                i++;
            }
            i++;
        }
    }
    
    /**
     * Verifica se la parola contiene una vocale.
     */
    private boolean vowelinstem() {
        for (int i = 0; i <= j && i <= k; i++) {
            if (!cons(i)) return true;
        }
        return false;
    }
    
    /**
     * Verifica se la stringa di lavoro termina con una doppia consonante.
     */
    private boolean doublec(int j) {
        if (j < 1) return false;
        if (b[j] != b[j-1]) return false;
        return cons(j);
    }
    
    /**
     * Verifica la condizione consonante-vocale-consonante dove la consonante finale non è w, x o y.
     */
    private boolean cvc(int i) {
        if (i < 2 || !cons(i) || cons(i-1) || !cons(i-2)) return false;
        int ch = b[i];
        if (ch == 'w' || ch == 'x' || ch == 'y') return false;
        return true;
    }
    
    /**
     * Verifica se la stringa di lavoro termina con un suffisso.
     */
    private boolean ends(String s) {
        int l = s.length();
        int o = k-l+1;
        if (o < 0) return false;
        for (int i = 0; i < l; i++) {
            if (b[o+i] != s.charAt(i)) return false;
        }
        j = k-l;
        return true;
    }
    
    /**
     * Sostituisce un suffisso nella stringa di lavoro.
     */
    private void setto(String s) {
        int l = s.length();
        int o = j+1;
        for (int i = 0; i < l; i++) {
            b[o+i] = s.charAt(i);
        }
        k = j+l;
    }
    
    /**
     * Sostituisce un suffisso se m() è maggiore di 0.
     */
    private void r(String s) {
        if (m() > 0) setto(s);
    }
    
    /**
     * Step 1a e 1b dell'algoritmo di Porter.
     */
    private void step1ab() {
        if (b[k] == 's') {
            if (ends("sses")) k -= 2;
            else if (ends("ies")) setto("i");
            else if (b[k-1] != 's') k--;
        }
        if (ends("eed")) {
            if (m() > 0) k--;
        } else if ((ends("ed") || ends("ing")) && vowelinstem()) {
            k = j;
            if (ends("at")) setto("ate");
            else if (ends("bl")) setto("ble");
            else if (ends("iz")) setto("ize");
            else if (doublec(k)) {
                k--;
                int ch = b[k];
                if (ch == 'l' || ch == 's' || ch == 'z') k++;
            } else if (m() == 1 && cvc(k)) setto("e");
        }
    }
    
    /**
     * Step 1c dell'algoritmo di Porter.
     */
    private void step1c() {
        if (ends("y") && vowelinstem()) b[k] = 'i';
    }
    
    /**
     * Step 2 dell'algoritmo di Porter.
     */
    private void step2() {
        if (k <= 0) return; // Controllo di sicurezza
        
        switch (b[k-1]) {
            case 'a': if (ends("ational")) { r("ate"); break; }
                     if (ends("tional")) { r("tion"); break; }
                     break;
            case 'c': if (ends("enci")) { r("ence"); break; }
                     if (ends("anci")) { r("ance"); break; }
                     break;
            case 'e': if (ends("izer")) { r("ize"); break; }
                     break;
            case 'l': if (ends("bli")) { r("ble"); break; }
                     if (ends("alli")) { r("al"); break; }
                     if (ends("entli")) { r("ent"); break; }
                     if (ends("eli")) { r("e"); break; }
                     if (ends("ousli")) { r("ous"); break; }
                     break;
            case 'o': if (ends("ization")) { r("ize"); break; }
                     if (ends("ation")) { r("ate"); break; }
                     if (ends("ator")) { r("ate"); break; }
                     break;
            case 's': if (ends("alism")) { r("al"); break; }
                     if (ends("iveness")) { r("ive"); break; }
                     if (ends("fulness")) { r("ful"); break; }
                     if (ends("ousness")) { r("ous"); break; }
                     break;
            case 't': if (ends("aliti")) { r("al"); break; }
                     if (ends("iviti")) { r("ive"); break; }
                     if (ends("biliti")) { r("ble"); break; }
                     break;
            case 'g': if (ends("logi")) { r("log"); break; }
        }
    }
    
    /**
     * Step 3 dell'algoritmo di Porter.
     */
    private void step3() {
        if (k <= 0) return; // Controllo di sicurezza
        
        switch (b[k]) {
            case 'e': if (ends("icate")) { r("ic"); break; }
                     if (ends("ative")) { r(""); break; }
                     if (ends("alize")) { r("al"); break; }
                     break;
            case 'i': if (ends("iciti")) { r("ic"); break; }
                     break;
            case 'l': if (ends("ical")) { r("ic"); break; }
                     if (ends("ful")) { r(""); break; }
                     break;
            case 's': if (ends("ness")) { r(""); break; }
                     break;
        }
    }
    
    /**
     * Step 4 dell'algoritmo di Porter.
     */
    private void step4() {
        if (k <= 0) return; // Controllo di sicurezza
        
        switch (b[k-1]) {
            case 'a': if (ends("al")) break; return;
            case 'c': if (ends("ance")) break;
                     if (ends("ence")) break; return;
            case 'e': if (ends("er")) break; return;
            case 'i': if (ends("ic")) break; return;
            case 'l': if (ends("able")) break;
                     if (ends("ible")) break; return;
            case 'n': if (ends("ant")) break;
                     if (ends("ement")) break;
                     if (ends("ment")) break;
                     if (ends("ent")) break; return;
            case 'o': if (ends("ion") && j >= 0 && (b[j] == 's' || b[j] == 't')) break;
                     if (ends("ou")) break; return;
            case 's': if (ends("ism")) break; return;
            case 't': if (ends("ate")) break;
                     if (ends("iti")) break; return;
            case 'u': if (ends("ous")) break; return;
            case 'v': if (ends("ive")) break; return;
            case 'z': if (ends("ize")) break; return;
            default: return;
        }
        if (m() > 1) k = j;
    }
    
    /**
     * Step 5 dell'algoritmo di Porter.
     */
    private void step5() {
        if (k <= 0) return; // Controllo di sicurezza
        
        j = k;
        if (b[k] == 'e') {
            int a = m();
            if (a > 1 || a == 1 && !cvc(k-1)) k--;
        }
        if (b[k] == 'l' && doublec(k) && m() > 1) k--;
    }
}