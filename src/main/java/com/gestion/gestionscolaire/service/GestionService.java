package com.gestion.gestionscolaire.service;

import java.time.LocalDate;
import java.util.List;

import com.gestion.gestionscolaire.dao.ClasseDAO;
import com.gestion.gestionscolaire.dao.CoursDAO;
import com.gestion.gestionscolaire.dao.EleveDAO;
import com.gestion.gestionscolaire.dao.EnseignementDAO;
import com.gestion.gestionscolaire.dao.NoteDAO;
import com.gestion.gestionscolaire.dao.UtilisateurDAO;
import com.gestion.gestionscolaire.model.Anonymat;
import com.gestion.gestionscolaire.model.Classe;
import com.gestion.gestionscolaire.model.Cours;
import com.gestion.gestionscolaire.model.Eleve;
import com.gestion.gestionscolaire.model.Niveau;
import com.gestion.gestionscolaire.model.Note;
import com.gestion.gestionscolaire.model.Utilisateur;

public class GestionService {
    private final ClasseDAO classeDAO = new ClasseDAO();
    private final EleveDAO eleveDAO = new EleveDAO();
    private final CoursDAO coursDAO = new CoursDAO();
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private final NoteDAO noteDAO = new NoteDAO();
    private final EnseignementDAO enseignementDAO = new EnseignementDAO();
    
    // Gestion des classes
    public List<Niveau> getAllNiveaux() {
        return classeDAO.getAllNiveaux();
    }
    
    public List<Classe> getAllClasses() {
        return classeDAO.getAllClasses();
    }
    
    public boolean creerClasse(String nom, int niveauId) {
        Classe classe = new Classe(nom, niveauId);
        return classeDAO.creerClasse(classe);
    }
    
    public boolean modifierClasse(int id, String nom, int niveauId) {
        Classe classe = new Classe(nom, niveauId);
        classe.setId(id);
        return classeDAO.modifierClasse(classe);
    }
    
    public boolean supprimerClasse(int classeId) {
        return classeDAO.supprimerClasse(classeId);
    }
    
    // Gestion des élèves
    public List<Eleve> getElevesByClasse(int classeId) {
        return eleveDAO.getElevesByClasse(classeId);
    }
    
    public List<Eleve> getAllEleves() {
        return eleveDAO.getAllEleves();
    }
    
    public boolean creerEleve(String nom, String prenom, LocalDate dateNaissance, int classeId) {
        String numeroEleve = genererNumeroEleve();
        Eleve eleve = new Eleve(nom, prenom, dateNaissance, classeId, numeroEleve);
        return eleveDAO.creerEleve(eleve);
    }
    
    public boolean modifierEleve(int id, String nom, String prenom, LocalDate dateNaissance, int classeId) {
        Eleve eleve = new Eleve(nom, prenom, dateNaissance, classeId, "");
        eleve.setId(id);
        return eleveDAO.modifierEleve(eleve);
    }
    
    public boolean supprimerEleve(int eleveId) {
        return eleveDAO.supprimerEleve(eleveId);
    }
    
    // Gestion des cours
    public List<Cours> getAllCours() {
        return coursDAO.getAllCours();
    }
    
    public boolean creerCours(String nom, double coefficient) {
        Cours cours = new Cours(nom, coefficient);
        return coursDAO.creerCours(cours);
    }
    
    public boolean modifierCours(int id, String nom, double coefficient) {
        Cours cours = new Cours(nom, coefficient);
        cours.setId(id);
        return coursDAO.modifierCours(cours);
    }
    
    public boolean supprimerCours(int coursId) {
        return coursDAO.supprimerCours(coursId);
    }
    
    // Gestion des enseignants
    public List<Utilisateur> getAllEnseignants() {
        return utilisateurDAO.getAllEnseignants();
    }
    
    public boolean creerEnseignant(String nom, String prenom, String email, String motDePasse) {
        Utilisateur enseignant = new Utilisateur(nom, prenom, email, motDePasse, Utilisateur.Role.ENSEIGNANT);
        return utilisateurDAO.creerEnseignant(enseignant);
    }
    
    public boolean creerEnseignant(String nom, String prenom, String email, String motDePasse, List<Classe> classes, List<Cours> cours) {
        Utilisateur enseignant = new Utilisateur(nom, prenom, email, motDePasse, Utilisateur.Role.ENSEIGNANT);
        boolean created = utilisateurDAO.creerEnseignant(enseignant);
        if (created) {
            // Récupérer l'ID de l'enseignant nouvellement créé
            Utilisateur nouvelEnseignant = utilisateurDAO.getUtilisateurById(utilisateurDAO.getAllEnseignants().stream()
                .filter(u -> u.getEmail().equals(email)).findFirst().map(Utilisateur::getId).orElse(-1));
            int enseignantId = nouvelEnseignant != null ? nouvelEnseignant.getId() : -1;
            if (enseignantId > 0) {
                List<Integer> classeIds = classes.stream().map(Classe::getId).toList();
                List<Integer> coursIds = cours.stream().map(Cours::getId).toList();
                return enseignementDAO.ajouterAffectations(enseignantId, classeIds, coursIds);
            }
        }
        return false;
    }
    
    public boolean modifierEnseignant(int id, String nom, String prenom, String email) {
        Utilisateur enseignant = new Utilisateur(nom, prenom, email, "", Utilisateur.Role.ENSEIGNANT);
        enseignant.setId(id);
        return utilisateurDAO.modifierEnseignant(enseignant);
    }
    
    public boolean supprimerEnseignant(int enseignantId) {
        return utilisateurDAO.supprimerEnseignant(enseignantId);
    }
    
    public List<Classe> getClassesByEnseignant(int enseignantId) {
        return classeDAO.getClassesByEnseignant(enseignantId);
    }

    public List<Cours> getCoursByEnseignant(int enseignantId) {
        return coursDAO.getCoursByEnseignant(enseignantId);
    }
    
    // Gestion des anonymats
    public boolean genererAnonymats(int classeId, int trimestre) {
        String anneeScolaire = getAnneeScolaireActuelle();
        return noteDAO.genererAnonymats(classeId, trimestre, anneeScolaire);
    }
    
    public List<Anonymat> getAnonymats(int classeId, int trimestre) {
        String anneeScolaire = getAnneeScolaireActuelle();
        return noteDAO.getAnonymatsByClasseEtTrimestre(classeId, trimestre, anneeScolaire);
    }
    
    // Gestion des notes
    public boolean sauvegarderNote(int eleveId, int coursId, int trimestre, 
                                  Note.TypeEvaluation type, Double note) {
        Note noteObj = new Note();
        noteObj.setEleveId(eleveId);
        noteObj.setCoursId(coursId);
        noteObj.setTrimestre(trimestre);
        noteObj.setTypeEvaluation(type);
        noteObj.setNote(note);
        noteObj.setAnneeScolaire(getAnneeScolaireActuelle());
        noteObj.setSaisiPar(AuthService.getUtilisateurConnecte().getId());
        
        return noteDAO.sauvegarderNote(noteObj);
    }
    
    public List<Note> getNotesByClasse(int classeId, int trimestre) {
        String anneeScolaire = getAnneeScolaireActuelle();
        return noteDAO.getNotesByClasseEtTrimestre(classeId, trimestre, anneeScolaire);
    }
    
    // Méthodes utilitaires
    private String genererNumeroEleve() {
        return "E" + String.format("%03d", (int)(Math.random() * 1000));
    }
    
    private String getAnneeScolaireActuelle() {
        LocalDate now = LocalDate.now();
        int annee = now.getMonthValue() >= 9 ? now.getYear() : now.getYear() - 1;
        return annee + "-" + (annee + 1);
    }
}