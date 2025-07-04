package com.gestion.gestionscolaire.service;

import com.gestion.gestionscolaire.dao.UtilisateurDAO;
import com.gestion.gestionscolaire.model.Utilisateur;
import com.gestion.gestionscolaire.model.Eleve;
import com.gestion.gestionscolaire.dao.EleveDAO;

public class AuthService {
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private static Utilisateur utilisateurConnecte;
    private static Eleve eleveConnecte;
    private final EleveDAO eleveDAO = new EleveDAO();
    
    public static Utilisateur getUtilisateurConnecte() {
        return utilisateurConnecte;
    }
    
    public boolean seConnecter(String email, String motDePasse) {
        Utilisateur user = utilisateurDAO.authentifier(email, motDePasse);
        if (user != null) {
            utilisateurConnecte = user;
            return true;
        }
        return false;
    }
    
    public boolean seConnecterEleve(String numeroEleve) {
        Eleve e = eleveDAO.getEleveByNumero(numeroEleve);
        if (e != null) {
            eleveConnecte = e;
            utilisateurConnecte = null; // s'assurer qu'aucun compte admin/enseignant n'est actif
            return true;
        }
        return false;
    }
    
    public static Eleve getEleveConnecte() {
        return eleveConnecte;
    }
    
    public void seDeconnecter() {
        utilisateurConnecte = null;
        eleveConnecte = null;
    }
    
    public boolean estAdmin() {
        if (eleveConnecte != null) return false;
        return utilisateurConnecte != null && 
               utilisateurConnecte.getRole() == Utilisateur.Role.ADMIN;
    }
    
    public boolean estEnseignant() {
        if (eleveConnecte != null) return false;
        return utilisateurConnecte != null && 
               utilisateurConnecte.getRole() == Utilisateur.Role.ENSEIGNANT;
    }
}