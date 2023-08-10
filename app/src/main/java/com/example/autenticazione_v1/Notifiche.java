package com.example.autenticazione_v1;

import java.util.ArrayList;

public class Notifiche {

    private String tipoNotifica, autore, IDRichiestaRiferimento, id;

    public String getId() {
        return id;
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
}
