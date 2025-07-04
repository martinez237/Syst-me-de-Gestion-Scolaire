package com.gestion.gestionscolaire.dao;

import com.gestion.gestionscolaire.config.DatabaseConfig;
import com.gestion.gestionscolaire.model.Cours;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoursDAO {
    
    public List<Cours> getAllCours() {
        List<Cours> cours = new ArrayList<>();
        String sql = "SELECT * FROM cours ORDER BY nom";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                cours.add(mapResultSetToCours(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cours;
    }
    
    public List<Cours> getCoursByEnseignant(int enseignantId) {
        List<Cours> cours = new ArrayList<>();
        String sql = """
            SELECT DISTINCT c.* FROM cours c 
            JOIN enseignements e ON c.id = e.cours_id 
            WHERE e.enseignant_id = ? 
            ORDER BY c.nom
            """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, enseignantId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                cours.add(mapResultSetToCours(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cours;
    }
    
    public Cours getCoursById(int id) {
        String sql = "SELECT * FROM cours WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToCours(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Cours> getCoursByClasseAndEnseignant(int classeId, int enseignantId) {
        List<Cours> cours = new ArrayList<>();
        String sql = """
            SELECT c.* FROM cours c 
            JOIN enseignements e ON c.id = e.cours_id 
            WHERE e.classe_id = ? AND e.enseignant_id = ?
            ORDER BY c.nom
            """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, classeId);
            stmt.setInt(2, enseignantId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                cours.add(mapResultSetToCours(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cours;
    }
    
    public boolean creerCours(Cours cours) {
        String sql = "INSERT INTO cours (nom, coefficient) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cours.getNom());
            stmt.setDouble(2, cours.getCoefficient());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean modifierCours(Cours cours) {
        String sql = "UPDATE cours SET nom = ?, coefficient = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cours.getNom());
            stmt.setDouble(2, cours.getCoefficient());
            stmt.setInt(3, cours.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean supprimerCours(int coursId) {
        String sql = "DELETE FROM cours WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, coursId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private Cours mapResultSetToCours(ResultSet rs) throws SQLException {
        Cours cours = new Cours();
        cours.setId(rs.getInt("id"));
        cours.setNom(rs.getString("nom"));
        cours.setCoefficient(rs.getDouble("coefficient"));
        return cours;
    }
}