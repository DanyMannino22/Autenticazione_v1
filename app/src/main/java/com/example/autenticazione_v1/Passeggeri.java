package com.example.autenticazione_v1;

import java.util.ArrayList;

public class Passeggeri {
    private ArrayList<String> passeggeri;

    public ArrayList<String> getPasseggeri() {
        return passeggeri;
    }

    public void setPasseggeri(ArrayList<String> passeggeri) {
        this.passeggeri = passeggeri;
    }

    public Passeggeri() {
    }

    public Passeggeri(ArrayList<String> passeggeri) {
        this.passeggeri = passeggeri;
    }
}