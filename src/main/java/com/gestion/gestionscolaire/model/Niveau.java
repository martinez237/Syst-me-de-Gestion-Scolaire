package com.gestion.gestionscolaire.model;

public class Niveau {
    private int id;
    private String nom;
    private int ordreAffichage;
    
    public Niveau() {}
    public Niveau(String nom, int ordreAffichage) {
        this.nom = nom;
        this.ordreAffichage = ordreAffichage;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public int getOrdreAffichage() { return ordreAffichage; }
    public void setOrdreAffichage(int ordreAffichage) { this.ordreAffichage = ordreAffichage; }
    
    @Override
    public String toString() { return nom; }
}