package com.gestion.gestionscolaire.dao;

import com.gestion.gestionscolaire.config.DatabaseConfig;
import com.gestion.gestionscolaire.model.Eleve;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EleveDAO {
    
    public List<Eleve> getAllEleves() {
        List<Eleve> eleves = new ArrayList<>();
        String sql = "SELECT e.*, c.nom as classe_nom FROM eleves e JOIN classes c ON e.classe_id = c.id ORDER BY c.nom, e.nom, e.prenom";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                eleves.add(mapResultSetToEleve(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eleves;
    }
    
    public List<Eleve> getElevesByClasse(int classeId) {
        List<Eleve> eleves = new ArrayList<>();
        String sql = "SELECT e.*, c.nom as classe_nom FROM eleves e JOIN classes c ON e.classe_id = c.id WHERE e.classe_id = ? ORDER BY e.nom, e.prenom";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, classeId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                eleves.add(mapResultSetToEleve(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eleves;
    }
    
    public Eleve getEleveById(int id) {
        String sql = "SELECT e.*, c.nom as classe_nom FROM eleves e JOIN classes c ON e.classe_id = c.id WHERE e.id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToEleve(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean creerEleve(Eleve eleve) {
        String sql = "INSERT INTO eleves (nom, prenom, date_naissance, classe_id, numero_eleve) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, eleve.getNom());
            stmt.setString(2, eleve.getPrenom());
            stmt.setDate(3, Date.valueOf(eleve.getDateNaissance()));
            stmt.setInt(4, eleve.getClasseId());
            stmt.setString(5, eleve.getNumeroEleve());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean modifierEleve(Eleve eleve) {
        String sql = "UPDATE eleves SET nom = ?, prenom = ?, date_naissance = ?, classe_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, eleve.getNom());
            stmt.setString(2, eleve.getPrenom());
            stmt.setDate(3, Date.valueOf(eleve.getDateNaissance()));
            stmt.setInt(4, eleve.getClasseId());
            stmt.setInt(5, eleve.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean supprimerEleve(int eleveId) {
        String sql = "DELETE FROM eleves WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eleveId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Eleve getEleveByNumero(String numeroEleve) {
        String sql = "SELECT e.*, c.nom as classe_nom FROM eleves e JOIN classes c ON e.classe_id = c.id WHERE e.numero_eleve = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numeroEleve);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToEleve(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private Eleve mapResultSetToEleve(ResultSet rs) throws SQLException {
        Eleve eleve = new Eleve();
        eleve.setId(rs.getInt("id"));
        eleve.setNom(rs.getString("nom"));
        eleve.setPrenom(rs.getString("prenom"));
        eleve.setDateNaissance(rs.getDate("date_naissance").toLocalDate());
        eleve.setClasseId(rs.getInt("classe_id"));
        eleve.setNumeroEleve(rs.getString("numero_eleve"));
        eleve.setClasseNom(rs.getString("classe_nom"));
        return eleve;
    }
}