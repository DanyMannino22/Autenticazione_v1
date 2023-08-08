package com.example.autenticazione_v1;

import java.util.ArrayList;
import java.util.HashMap;

public class Richieste {

    private String autista, passeggero, passeggero3, passeggero4, partenza, destinazione, nomeAutista,  ID;
    private int posti_auto, ora_Partenza, minutiPartenza, posti_disponibili;
    private int giorno, mese, anno;

    //private HashMap<String, String> passeggeri;

    Passeggeri p;

    public Passeggeri getP() {
        return p;
    }

    public void setP(Passeggeri p) {
        this.p = p;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getAutista() {
        return autista;
    }

    public int getGiorno() {
        return giorno;
    }

    public void setGiorno(int giorno) {
        this.giorno = giorno;
    }

    public int getMese() {
        return mese;
    }

    public void setMese(int mese) {
        this.mese = mese;
    }

    public int getAnno() {
        return anno;
    }

    public void setAnno(int anno) {
        this.anno = anno;
    }

    public int getPosti_disponibili() {
        return posti_disponibili;
    }

    public String getNomeAutista() {
        return nomeAutista;
    }

    public void setNomeAutista(String nomeAutista) {
        this.nomeAutista = nomeAutista;
    }

    public void setPosti_disponibili(int posti_disponibili) {
        this.posti_disponibili = posti_disponibili;
    }

    public void setAutista(String autista) {
        this.autista = autista;
    }

    /*public HashMap<String, String> getPasseggeri() {
        return passeggeri;
    }


    public void setPasseggeri(HashMap<String, String> passeggeri) {
        this.passeggeri = passeggeri;
    }
     */
    public String getPasseggero() {
        return passeggero;
    }

    public void setPasseggero(String passeggero2) {
        this.passeggero = passeggero;
    }

    public String getPasseggero3() {
        return passeggero3;
    }

    public void setPasseggero3(String passeggero3) {
        this.passeggero3 = passeggero3;
    }

    public String getPasseggero4() {
        return passeggero4;
    }

    public void setPasseggero4(String passeggero4) {
        this.passeggero4 = passeggero4;
    }

    public String getPartenza() {
        return partenza;
    }

    public void setPartenza(String partenza) {
        this.partenza = partenza;
    }

    public String getDestinazione() {
        return destinazione;
    }

    public void setDestinazione(String destinazione) {
        this.destinazione = destinazione;
    }

    public int getPosti_auto() {
        return posti_auto;
    }

    public void setPosti_auto(int posti_auto) {
        this.posti_auto = posti_auto;
    }

    public int getOra_Partenza() {
        return ora_Partenza;
    }

    public void setOra_Partenza(int ora_Partenza) {
        this.ora_Partenza = ora_Partenza;
    }

    public int getMinutiPartenza() {
        return minutiPartenza;
    }

    public void setMinutiPartenza(int minutiPartenza) {
        this.minutiPartenza = minutiPartenza;
    }

    /*public Richieste(String autista, String passeggeri, String passeggero2, String passeggero3, String passeggero4, String partenza, String destinazione, int posti_auto, int ora_Partenza, int minutiPartenza, int posti_disponibili) {
        this.autista = autista;
        this.passeggeri = passeggeri;
        this.passeggero2 = passeggero2;
        this.passeggero3 = passeggero3;
        this.passeggero4 = passeggero4;
        this.partenza = partenza;
        this.destinazione = destinazione;
        this.posti_auto = posti_auto;
        this.ora_Partenza = ora_Partenza;
        this.minutiPartenza = minutiPartenza;
        this.posti_disponibili = posti_auto;
    }
*/
    public Richieste() {
    }

    public Richieste(String autista, String partenza, String destinazione, int posti_auto, int ora_Partenza, int minutiPartenza, String nomeAutista, String ID, int giorno, int mese, int anno, Passeggeri p) {
        this.autista = autista;
        this.partenza = partenza;
        this.destinazione = destinazione;
        this.posti_auto = posti_auto;
        this.ora_Partenza = ora_Partenza;
        this.minutiPartenza = minutiPartenza;
        this.posti_disponibili = posti_auto;
        this.nomeAutista = nomeAutista;
        this.ID = ID;
        this.giorno = giorno;
        this.mese = mese;
        this.anno = anno;
        this.p = p;
    }
}
