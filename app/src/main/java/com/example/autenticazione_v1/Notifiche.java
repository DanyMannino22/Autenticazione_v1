package com.example.autenticazione_v1;

import java.util.ArrayList;

public class Notifiche {

    private String tipoNotifica, autore, IDRichiestaRiferimento, id, IDnotificaRiferimento;
    private Richieste r;

    public String getId() {
        return id;
    }

    public Richieste getR() {
        return r;
    }

    public String getIDnotificaRiferimento() {
        return IDnotificaRiferimento;
    }

    public void setIDnotificaRiferimento(String IDnotificaRiferimento) {
        this.IDnotificaRiferimento = IDnotificaRiferimento;
    }

    public void setR(Richieste r) {
        this.r = r;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIDRichiestaRiferimento() {
        return IDRichiestaRiferimento;
    }

    public void setIDRichiestaRiferimento(String IDRichiestaRiferimento) {
        this.IDRichiestaRiferimento = IDRichiestaRiferimento;
    }

    private ArrayList<String>destinatari;

    public String getTipoNotifica() {
        return tipoNotifica;
    }

    public void setTipoNotifica(String tipoNotifica) {
        this.tipoNotifica = tipoNotifica;
    }

    public String getAutore() {
        return autore;
    }

    public void setAutore(String autore) {
        this.autore = autore;
    }

    public ArrayList<String> getDestinatari() {
        return destinatari;
    }

    public void setDestinatari(ArrayList<String> destinatari) {
        this.destinatari = destinatari;
    }

    public Notifiche() {
    }

    public Notifiche(String tipoNotifica, String autore, ArrayList<String> destinatari, String IDRichiestaRiferimento, String id) {
        this.tipoNotifica = tipoNotifica;
        this.autore = autore;
        this.destinatari = destinatari;
        this.IDRichiestaRiferimento = IDRichiestaRiferimento;
        this.id = id;
    }

    public Notifiche(String tipoNotifica, String autore, ArrayList<String> destinatari, String IDRichiestaRiferimento, String id, Richieste r) {
        this.tipoNotifica = tipoNotifica;
        this.autore = autore;
        this.destinatari = destinatari;
        this.IDRichiestaRiferimento = IDRichiestaRiferimento;
        this.id = id;
        this.r = r;
    }

    public Notifiche(String tipoNotifica, String autore, ArrayList<String> destinatari, String IDRichiestaRiferimento, String id, Richieste r, String IDnotificaRiferimento) {
        this.tipoNotifica = tipoNotifica;
        this.autore = autore;
        this.destinatari = destinatari;
        this.IDRichiestaRiferimento = IDRichiestaRiferimento;
        this.id = id;
        this.r = r;
        this.IDnotificaRiferimento = IDnotificaRiferimento;
    }
}
