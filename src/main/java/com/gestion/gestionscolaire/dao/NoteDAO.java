package com.gestion.gestionscolaire.dao;

import com.gestion.gestionscolaire.config.DatabaseConfig;
import com.gestion.gestionscolaire.model.Note;
import com.gestion.gestionscolaire.model.Anonymat;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NoteDAO {
    
    public boolean sauvegarderNote(Note note) {
        String sql = "INSERT INTO notes (eleve_id, cours_id, trimestre, type_evaluation, note, annee_scolaire, saisi_par) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE note = VALUES(note), saisi_par = VALUES(saisi_par), date_saisie = CURRENT_TIMESTAMP";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, note.getEleveId());
            stmt.setInt(2, note.getCoursId());
            stmt.setInt(3, note.getTrimestre());
            stmt.setString(4, note.getTypeEvaluation().name());
            if (note.getNote() != null) {
                stmt.setDouble(5, note.getNote());
            } else {
                stmt.setNull(5, Types.DECIMAL);
            }
            stmt.setString(6, note.getAnneeScolaire());
            stmt.setInt(7, note.getSaisiPar());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Note> getNotesByClasseEtTrimestre(int classeId, int trimestre, String anneeScolaire) {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT n.*, e.nom as eleve_nom, e.prenom as eleve_prenom, c.nom as cours_nom " +
                    "FROM notes n " +
                    "JOIN eleves e ON n.eleve_id = e.id " +
                    "JOIN cours c ON n.cours_id = c.id " +
                    "WHERE e.classe_id = ? AND n.trimestre = ? AND n.annee_scolaire = ? " +
                    "ORDER BY e.nom, e.prenom, c.nom, n.type_evaluation";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, classeId);
            stmt.setInt(2, trimestre);
            stmt.setString(3, anneeScolaire);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Note note = new Note();
                note.setId(rs.getInt("id"));
                note.setEleveId(rs.getInt("eleve_id"));
                note.setCoursId(rs.getInt("cours_id"));
                note.setTrimestre(rs.getInt("trimestre"));
                note.setTypeEvaluation(Note.TypeEvaluation.valueOf(rs.getString("type_evaluation")));
                Double noteValue = rs.getObject("note", Double.class);
                note.setNote(noteValue);
                note.setAnneeScolaire(rs.getString("annee_scolaire"));
                note.setSaisiPar(rs.getInt("saisi_par"));
                note.setEleveNom(rs.getString("eleve_prenom") + " " + rs.getString("eleve_nom"));
                note.setCoursNom(rs.getString("cours_nom"));
                notes.add(note);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notes;
    }
    
    // Gestion des anonymats
    public boolean genererAnonymats(int classeId, int trimestre, String anneeScolaire) {
        String selectSql = "SELECT id FROM eleves WHERE classe_id = ?";
        String insertSql = "INSERT INTO anonymats (eleve_id, classe_id, trimestre, code_anonymat, annee_scolaire) " +
                          "VALUES (?, ?, ?, ?, ?) " +
                          "ON DUPLICATE KEY UPDATE code_anonymat = VALUES(code_anonymat)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectSql);
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            
            selectStmt.setInt(1, classeId);
            ResultSet rs = selectStmt.executeQuery();
            
            while (rs.next()) {
                int eleveId = rs.getInt("id");
                String codeAnonymat = genererCodeAnonymat();
                
                insertStmt.setInt(1, eleveId);
                insertStmt.setInt(2, classeId);
                insertStmt.setInt(3, trimestre);
                insertStmt.setString(4, codeAnonymat);
                insertStmt.setString(5, anneeScolaire);
                insertStmt.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Double getNoteEleveCours(int eleveId,int coursId,int trimestre,String anneeScolaire,String typeEval){
        String sql="SELECT note FROM notes WHERE eleve_id=? AND cours_id=? AND trimestre=? AND annee_scolaire=? AND type_evaluation=?";
        try(Connection conn=DatabaseConfig.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setInt(1,eleveId);
            stmt.setInt(2,coursId);
            stmt.setInt(3,trimestre);
            stmt.setString(4,anneeScolaire);
            stmt.setString(5,typeEval);
            ResultSet rs=stmt.executeQuery();
            if(rs.next()){
                return rs.getDouble("note");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Anonymat> getAnonymatsByClasseEtTrimestre(int classeId, int trimestre, String anneeScolaire) {
        List<Anonymat> anonymats = new ArrayList<>();
        String sql = "SELECT a.*, e.nom as eleve_nom, e.prenom as eleve_prenom " +
                    "FROM anonymats a " +
                    "JOIN eleves e ON a.eleve_id = e.id " +
                    "WHERE a.classe_id = ? AND a.trimestre = ? AND a.annee_scolaire = ? " +
                    "ORDER BY a.code_anonymat";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, classeId);
            stmt.setInt(2, trimestre);
            stmt.setString(3, anneeScolaire);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Anonymat anonymat = new Anonymat();
                anonymat.setId(rs.getInt("id"));
                anonymat.setEleveId(rs.getInt("eleve_id"));
                anonymat.setClasseId(rs.getInt("classe_id"));
                anonymat.setTrimestre(rs.getInt("trimestre"));
                anonymat.setCodeAnonymat(rs.getString("code_anonymat"));
                anonymat.setAnneeScolaire(rs.getString("annee_scolaire"));
                anonymat.setEleveNom(rs.getString("eleve_prenom") + " " + rs.getString("eleve_nom"));
                anonymats.add(anonymat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return anonymats;
    }
    
    public int getEleveIdByAnonymat(String codeAnonymat, int trimestre, String anneeScolaire) {
        String sql = "SELECT eleve_id FROM anonymats WHERE code_anonymat = ? AND trimestre = ? AND annee_scolaire = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, codeAnonymat);
            stmt.setInt(2, trimestre);
            stmt.setString(3, anneeScolaire);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("eleve_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    private String genererCodeAnonymat() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}