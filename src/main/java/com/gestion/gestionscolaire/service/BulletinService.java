package com.gestion.gestionscolaire.service;

import com.gestion.gestionscolaire.dao.*;
import com.gestion.gestionscolaire.model.*;
import java.util.*;
import java.util.stream.Collectors;

public class BulletinService {
    private final NoteDAO noteDAO = new NoteDAO();
    private final EleveDAO eleveDAO = new EleveDAO();
    private final CoursDAO coursDAO = new CoursDAO();
    
    public class ResultatEleve {
        private Eleve eleve;
        private Map<String, Double> notesTrimestre = new HashMap<>();
        private double moyenneGenerale;
        private String mention;
        private int rang;
        
        // Getters et setters
        public Eleve getEleve() { return eleve; }
        public void setEleve(Eleve eleve) { this.eleve = eleve; }
        public Map<String, Double> getNotesTrimestre() { return notesTrimestre; }
        public void setNotesTrimestre(Map<String, Double> notesTrimestre) { this.notesTrimestre = notesTrimestre; }
        public double getMoyenneGenerale() { return moyenneGenerale; }
        public void setMoyenneGenerale(double moyenneGenerale) { this.moyenneGenerale = moyenneGenerale; }
        public String getMention() { return mention; }
        public void setMention(String mention) { this.mention = mention; }
        public int getRang() { return rang; }
        public void setRang(int rang) { this.rang = rang; }
    }
    
    public List<ResultatEleve> genererBulletinClasse(int classeId, int trimestre, String anneeScolaire) {
        List<Eleve> eleves = eleveDAO.getElevesByClasse(classeId);
        List<Note> notes = noteDAO.getNotesByClasseEtTrimestre(classeId, trimestre, anneeScolaire);
        List<Cours> cours = coursDAO.getAllCours();
        
        Map<String, Double> coefficients = cours.stream()
            .collect(Collectors.toMap(c -> String.valueOf(c.getId()), Cours::getCoefficient));
        
        List<ResultatEleve> resultats = new ArrayList<>();
        
        for (Eleve eleve : eleves) {
            ResultatEleve resultat = new ResultatEleve();
            resultat.setEleve(eleve);
            
            // Calculer les notes par matière
            Map<String, List<Note>> notesParCours = notes.stream()
                .filter(n -> n.getEleveId() == eleve.getId())
                .collect(Collectors.groupingBy(n -> String.valueOf(n.getCoursId())));
            
            Map<String, Double> notesTrimestre = new HashMap<>();
            double sommeNotesPonderees = 0;
            double sommeCoefficients = 0;
            
            for (String coursId : notesParCours.keySet()) {
                List<Note> notesCoursEleve = notesParCours.get(coursId);
                
                // Calculer la moyenne du trimestre pour ce cours (CC + Examen) / 2
                double noteCC = notesCoursEleve.stream()
                    .filter(n -> n.getTypeEvaluation() == Note.TypeEvaluation.CONTROLE_CONTINU)
                    .mapToDouble(n -> n.getNote() != null ? n.getNote() : 0)
                    .findFirst().orElse(0);
                
                double noteExamen = notesCoursEleve.stream()
                    .filter(n -> n.getTypeEvaluation() == Note.TypeEvaluation.EXAMEN)
                    .mapToDouble(n -> n.getNote() != null ? n.getNote() : 0)
                    .findFirst().orElse(0);
                
                double noteTrimestre = (noteCC + noteExamen) / 2;
                notesTrimestre.put(coursId, noteTrimestre);
                
                // Ajouter à la moyenne générale pondérée
                double coeff = coefficients.getOrDefault(coursId, 1.0);
                sommeNotesPonderees += noteTrimestre * coeff;
                sommeCoefficients += coeff;
            }
            
            resultat.setNotesTrimestre(notesTrimestre);
            
            // Calculer la moyenne générale
            double moyenneGenerale = sommeCoefficients > 0 ? sommeNotesPonderees / sommeCoefficients : 0;
            resultat.setMoyenneGenerale(Math.round(moyenneGenerale * 100.0) / 100.0);
            
            // Calculer la mention
            resultat.setMention(calculerMention(moyenneGenerale));
            
            resultats.add(resultat);
        }
        
        // Calculer les rangs
        resultats.sort((r1, r2) -> Double.compare(r2.getMoyenneGenerale(), r1.getMoyenneGenerale()));
        for (int i = 0; i < resultats.size(); i++) {
            resultats.get(i).setRang(i + 1);
        }
        
        return resultats;
    }
    
    public ResultatEleve genererBulletinPourEleve(int eleveId, int trimestre, String anneeScolaire) {
        // Trouver la classe de l'élève
        EleveDAO eleveDAO = new EleveDAO();
        Eleve eleve = eleveDAO.getEleveById(eleveId);
        if (eleve == null) return null;
        int classeId = eleve.getClasseId();

        List<ResultatEleve> bulletinsClasse = genererBulletinClasse(classeId, trimestre, anneeScolaire);
        return bulletinsClasse.stream()
                .filter(r -> r.getEleve().getId() == eleveId)
                .findFirst()
                .orElse(null);
    }
    
    public String calculerMention(double moyenne) {
        if (moyenne >= 16) return "Très Bien";
        else if (moyenne >= 14) return "Bien";
        else if (moyenne >= 12) return "Assez Bien";
        else if (moyenne >= 10) return "Passable";
        else return "Insuffisant";
    }
}