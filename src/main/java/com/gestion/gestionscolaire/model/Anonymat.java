package com.gestion.gestionscolaire.model;

public class Anonymat {
    private int id;
    private int eleveId;
    private int classeId;
    private int trimestre;
    private String codeAnonymat;
    private String anneeScolaire;
    private String eleveNom;
    
    public Anonymat() {}
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getEleveId() { return eleveId; }
    public void setEleveId(int eleveId) { this.eleveId = eleveId; }
    public int getClasseId() { return classeId; }
    public void setClasseId(int classeId) { this.classeId = classeId; }
    public int getTrimestre() { return trimestre; }
    public void setTrimestre(int trimestre) { this.trimestre = trimestre; }
    public String getCodeAnonymat() { return codeAnonymat; }
    public void setCodeAnonymat(String codeAnonymat) { this.codeAnonymat = codeAnonymat; }
    public String getAnneeScolaire() { return anneeScolaire; }
    public void setAnneeScolaire(String anneeScolaire) { this.anneeScolaire = anneeScolaire; }
    public String getEleveNom() { return eleveNom; }
    public void setEleveNom(String eleveNom) { this.eleveNom = eleveNom; }
}