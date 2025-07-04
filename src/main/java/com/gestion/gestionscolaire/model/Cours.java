package com.gestion.gestionscolaire.model;

public class Cours {
    private int id;
    private String nom;
    private double coefficient;
    
    public Cours() {}
    public Cours(String nom, double coefficient) {
        this.nom = nom;
        this.coefficient = coefficient;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public double getCoefficient() { return coefficient; }
    public void setCoefficient(double coefficient) { this.coefficient = coefficient; }
    
    @Override
    public String toString() { return nom; }
}