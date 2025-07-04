package com.gestion.gestionscolaire.model;

import java.time.LocalDate;

public class Eleve {
    private int id;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private int classeId;
    private String numeroEleve;
    private String classeNom;
    
    public Eleve() {}
    public Eleve(String nom, String prenom, LocalDate dateNaissance, int classeId, String numeroEleve) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.classeId = classeId;
        this.numeroEleve = numeroEleve;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
    public int getClasseId() { return classeId; }
    public void setClasseId(int classeId) { this.classeId = classeId; }
    public String getNumeroEleve() { return numeroEleve; }
    public void setNumeroEleve(String numeroEleve) { this.numeroEleve = numeroEleve; }
    public String getClasseNom() { return classeNom; }
    public void setClasseNom(String classeNom) { this.classeNom = classeNom; }
    
    public String getNomComplet() { return prenom + " " + nom; }
    
    @Override
    public String toString() { return getNomComplet(); }
}