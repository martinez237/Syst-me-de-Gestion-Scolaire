package com.gestion.gestionscolaire.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import com.gestion.gestionscolaire.config.DatabaseConfig;

public class EnseignementDAO {
    public boolean ajouterAffectations(int enseignantId, List<Integer> classeIds, List<Integer> coursIds) {
        if ((classeIds == null || classeIds.isEmpty()) && (coursIds == null || coursIds.isEmpty())) return false;
        String sql = "INSERT INTO enseignements (enseignant_id, cours_id, classe_id) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection()) {
            // Cas 1 : classes et cours fournis
            if (classeIds != null && !classeIds.isEmpty() && coursIds != null && !coursIds.isEmpty()) {
                for (Integer classeId : classeIds) {
                    for (Integer coursId : coursIds) {
                        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                            stmt.setInt(1, enseignantId);
                            stmt.setInt(2, coursId);
                            stmt.setInt(3, classeId);
                            stmt.executeUpdate();
                        }
                    }
                }
            }
            // Cas 2 : seulement classes
            else if (classeIds != null && !classeIds.isEmpty()) {
                for (Integer classeId : classeIds) {
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setInt(1, enseignantId);
                        stmt.setNull(2, Types.INTEGER);
                        stmt.setInt(3, classeId);
                        stmt.executeUpdate();
                    }
                }
            }
            // Cas 3 : seulement cours
            else if (coursIds != null && !coursIds.isEmpty()) {
                for (Integer coursId : coursIds) {
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setInt(1, enseignantId);
                        stmt.setInt(2, coursId);
                        stmt.setNull(3, Types.INTEGER);
                        stmt.executeUpdate();
                    }
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
