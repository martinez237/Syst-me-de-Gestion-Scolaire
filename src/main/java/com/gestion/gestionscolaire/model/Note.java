package com.gestion.gestionscolaire.model;

public class Note {
    public enum TypeEvaluation { CONTROLE_CONTINU, EXAMEN }
    
    private int id;
    private int eleveId;
    private int coursId;
    private int trimestre;
    private TypeEvaluation typeEvaluation;
    private Double note;
    private String anneeScolaire;
    private int saisiPar;
    private String eleveNom;
    private String coursNom;
    
    public Note() {}
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getEleveId() { return eleveId; }
    public void setEleveId(int eleveId) { this.eleveId = eleveId; }
    public int getCoursId() { return coursId; }
    public void setCoursId(int coursId) { this.coursId = coursId; }
    public int getTrimestre() { return trimestre; }
    public void setTrimestre(int trimestre) { this.trimestre = trimestre; }
    public TypeEvaluation getTypeEvaluation() { return typeEvaluation; }
    public void setTypeEvaluation(TypeEvaluation typeEvaluation) { this.typeEvaluation = typeEvaluation; }
    public Double getNote() { return note; }
    public void setNote(Double note) { this.note = note; }
    public String getAnneeScolaire() { return anneeScolaire; }
    public void setAnneeScolaire(String anneeScolaire) { this.anneeScolaire = anneeScolaire; }
    public int getSaisiPar() { return saisiPar; }
    public void setSaisiPar(int saisiPar) { this.saisiPar = saisiPar; }
    public String getEleveNom() { return eleveNom; }
    public void setEleveNom(String eleveNom) { this.eleveNom = eleveNom; }
    public String getCoursNom() { return coursNom; }
    public void setCoursNom(String coursNom) { this.coursNom = coursNom; }
}