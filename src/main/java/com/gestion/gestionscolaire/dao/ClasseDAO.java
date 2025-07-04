package com.gestion.gestionscolaire.dao;

import com.gestion.gestionscolaire.config.DatabaseConfig;
import com.gestion.gestionscolaire.model.Classe;
import com.gestion.gestionscolaire.model.Niveau;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClasseDAO {
    
    public List<Niveau> getAllNiveaux() {
        List<Niveau> niveaux = new ArrayList<>();
        String sql = "SELECT * FROM niveaux ORDER BY ordre_affichage";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Niveau niveau = new Niveau();
                niveau.setId(rs.getInt("id"));
                niveau.setNom(rs.getString("nom"));
                niveau.setOrdreAffichage(rs.getInt("ordre_affichage"));
                niveaux.add(niveau);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return niveaux;
    }
    
    public List<Classe> getAllClasses() {
        List<Classe> classes = new ArrayList<>();
        String sql = "SELECT c.*, n.nom as niveau_nom FROM classes c " +
                    "JOIN niveaux n ON c.niveau_id = n.id " +
                    "ORDER BY n.ordre_affichage, c.nom";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Classe classe = new Classe();
                classe.setId(rs.getInt("id"));
                classe.setNom(rs.getString("nom"));
                classe.setNiveauId(rs.getInt("niveau_id"));
                classe.setNiveauNom(rs.getString("niveau_nom"));
                classes.add(classe);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classes;
    }
    
    public List<Classe> getClassesByEnseignant(int enseignantId) {
        List<Classe> classes = new ArrayList<>();
        String sql = "SELECT DISTINCT c.*, n.nom as niveau_nom " +
                    "FROM classes c " +
                    "JOIN niveaux n ON c.niveau_id = n.id " +
                    "JOIN enseignements e ON c.id = e.classe_id " +
                    "WHERE e.enseignant_id = ? " +
                    "ORDER BY n.ordre_affichage, c.nom";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, enseignantId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Classe classe = new Classe();
                classe.setId(rs.getInt("id"));
                classe.setNom(rs.getString("nom"));
                classe.setNiveauId(rs.getInt("niveau_id"));
                classe.setNiveauNom(rs.getString("niveau_nom"));
                classes.add(classe);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classes;
    }
    
    public boolean creerClasse(Classe classe) {
        String sql = "INSERT INTO classes (nom, niveau_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, classe.getNom());
            stmt.setInt(2, classe.getNiveauId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean modifierClasse(Classe classe) {
        String sql = "UPDATE classes SET nom = ?, niveau_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, classe.getNom());
            stmt.setInt(2, classe.getNiveauId());
            stmt.setInt(3, classe.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean supprimerClasse(int classeId) {
        String sql = "DELETE FROM classes WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, classeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Classe getClasseById(int id) {
        String sql = "SELECT c.*, n.nom as niveau_nom FROM classes c " +
                    "JOIN niveaux n ON c.niveau_id = n.id WHERE c.id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Classe classe = new Classe();
                classe.setId(rs.getInt("id"));
                classe.setNom(rs.getString("nom"));
                classe.setNiveauId(rs.getInt("niveau_id"));
                classe.setNiveauNom(rs.getString("niveau_nom"));
                return classe;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}