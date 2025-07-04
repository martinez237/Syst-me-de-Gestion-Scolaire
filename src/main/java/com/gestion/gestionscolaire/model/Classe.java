package com.gestion.gestionscolaire.model;

public class Classe {
    private int id;
    private String nom;
    private int niveauId;
    private String niveauNom;
    
    public Classe() {}
    public Classe(String nom, int niveauId) {
        this.nom = nom;
        this.niveauId = niveauId;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public int getNiveauId() { return niveauId; }
    public void setNiveauId(int niveauId) { this.niveauId = niveauId; }
    public String getNiveauNom() { return niveauNom; }
    public void setNiveauNom(String niveauNom) { this.niveauNom = niveauNom; }
    
    @Override
    public String toString() { return nom; }
}