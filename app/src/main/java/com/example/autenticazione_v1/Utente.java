package com.example.autenticazione_v1;

public class Utente {

    private String nome, cognome, cellulare;
    private boolean disponibilitaVeicolo;

    public Utente(String nome, String cognome, String cellulare, boolean disponibilitaVeicolo) {
        this.nome = nome;
        this.cognome = cognome;
        this.cellulare = cellulare;
        this.disponibilitaVeicolo = disponibilitaVeicolo;
    }

    public Utente(){}

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getCellulare() {
        return cellulare;
    }

    public void setCellulare(String cellulare) {
        this.cellulare = cellulare;
    }

    public boolean getDisponibilitaVeicolo() {
        return disponibilitaVeicolo;
    }

    public void setDisponibilitaVeicolo(boolean disponibilitaVeicolo) {
        this.disponibilitaVeicolo = disponibilitaVeicolo;
    }
}
