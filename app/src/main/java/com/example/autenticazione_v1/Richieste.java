package com.example.autenticazione_v1;

public class Richieste {

    private String autista, passeggero1, passeggero2, passeggero3, passeggero4, partenza, destinazione;
    private int posti_auto, ora_Partenza, minutiPartenza;

    public String getAutista() {
        return autista;
    }

    public void setAutista(String autista) {
        this.autista = autista;
    }

    public String getPasseggero1() {
        return passeggero1;
    }

    public void setPasseggero1(String passeggero1) {
        this.passeggero1 = passeggero1;
    }

    public String getPasseggero2() {
        return passeggero2;
    }

    public void setPasseggero2(String passeggero2) {
        this.passeggero2 = passeggero2;
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

    public void setGetPasseggero4(String getPasseggero4) {
        this.passeggero4 = getPasseggero4;
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

    public Richieste(String autista, String passeggero1, String passeggero2, String passeggero3, String passeggero4, String partenza, String destinazione, int posti_auto, int ora_Partenza, int minutiPartenza) {
        this.autista = autista;
        this.passeggero1 = passeggero1;
        this.passeggero2 = passeggero2;
        this.passeggero3 = passeggero3;
        this.passeggero4 = passeggero4;
        this.partenza = partenza;
        this.destinazione = destinazione;
        this.posti_auto = posti_auto;
        this.ora_Partenza = ora_Partenza;
        this.minutiPartenza = minutiPartenza;
    }

    public Richieste() {
    }

    public Richieste(String autista, String partenza, String destinazione, int posti_auto, int ora_Partenza, int minutiPartenza) {
        this.autista = autista;
        this.partenza = partenza;
        this.destinazione = destinazione;
        this.posti_auto = posti_auto;
        this.ora_Partenza = ora_Partenza;
        this.minutiPartenza = minutiPartenza;
    }
}
