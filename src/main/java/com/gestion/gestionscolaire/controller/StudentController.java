package com.gestion.gestionscolaire.controller;

import com.gestion.gestionscolaire.model.Eleve;

import com.gestion.gestionscolaire.service.AuthService;
import com.gestion.gestionscolaire.service.BulletinService;
import com.gestion.gestionscolaire.dao.NoteDAO;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Interface très simple permettant à un élève de consulter son bulletin trimestre par trimestre.
 */
public class StudentController {

    public static class LigneCours {
        private final String cours;
        private final Double cc;
        private final Double exam;
        private final Double moyenne;
        public LigneCours(String cours, Double cc, Double exam) {
            this.cours = cours;
            this.cc = cc;
            this.exam = exam;
            if (cc != null && exam != null) {
                this.moyenne = Math.round(((cc * 0.4) + (exam * 0.6)) * 100.0) / 100.0;
            } else if (cc != null) {
                this.moyenne = cc;
            } else if (exam != null) {
                this.moyenne = exam;
            } else {
                this.moyenne = null;
            }
        }
        public String getCours() { return cours; }
        public Double getCc() { return cc; }
        public Double getExam() { return exam; }
        public Double getMoyenne() { return moyenne; }
    }

    private final BulletinService bulletinService = new BulletinService();
    private final com.gestion.gestionscolaire.dao.CoursDAO coursDAO = new com.gestion.gestionscolaire.dao.CoursDAO();

    public void ouvrirFenetreEleve() {
        Eleve eleve = AuthService.getEleveConnecte();
        if (eleve == null) return;

        Stage stage = new Stage();
        stage.setTitle("Mon Bulletin - " + eleve.getNomComplet() + " (" + eleve.getNumeroEleve() + ")");

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label titre = new Label("Bienvenue " + eleve.getNomComplet() + " - Classe " + eleve.getClasseNom());

        ComboBox<Integer> trimestreCombo = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3));
        trimestreCombo.setPromptText("Trimestre");

        Button afficherBtn = new Button("Afficher Bulletin");

        TableView<LigneCours> table = new TableView<>();
        TableColumn<LigneCours, String> coursCol = new TableColumn<>("Cours");
        coursCol.setCellValueFactory(data -> new javafx.beans.property.ReadOnlyObjectWrapper<>(data.getValue().getCours()));
        TableColumn<LigneCours, Double> ccCol = new TableColumn<>("C.C.");
        ccCol.setCellValueFactory(d -> new javafx.beans.property.ReadOnlyObjectWrapper<>(d.getValue().getCc()));
        TableColumn<LigneCours, Double> examCol = new TableColumn<>("Examen");
        examCol.setCellValueFactory(d -> new javafx.beans.property.ReadOnlyObjectWrapper<>(d.getValue().getExam()));
        TableColumn<LigneCours, Double> moyenneCol = new TableColumn<>("Moyenne");
        moyenneCol.setCellValueFactory(d -> new javafx.beans.property.ReadOnlyObjectWrapper<>(d.getValue().getMoyenne()));
        table.getColumns().addAll(coursCol, ccCol, examCol, moyenneCol);

        Label moyenneLbl = new Label();
        Label mentionLbl = new Label();
        Label rangLbl = new Label();
        moyenneLbl.setStyle("-fx-font-weight:bold");

        afficherBtn.setOnAction(e -> {
            if (trimestreCombo.getValue() != null) {
                String annee = java.time.LocalDate.now().getMonthValue() >= 9 ? java.time.LocalDate.now().getYear() + "-" + (java.time.LocalDate.now().getYear() + 1)
                        : (java.time.LocalDate.now().getYear() - 1) + "-" + java.time.LocalDate.now().getYear();
                var resultat = bulletinService.genererBulletinPourEleve(eleve.getId(), trimestreCombo.getValue(), annee);
                if (resultat != null) {
                    var lignes = new java.util.ArrayList<LigneCours>();
                    NoteDAO noteDAO = new NoteDAO();
                    resultat.getNotesTrimestre().forEach((id, m) -> {
                        int cid = Integer.parseInt(id);
                        String nomCours = coursDAO.getCoursById(cid).getNom();
                        Double cc = noteDAO.getNoteEleveCours(eleve.getId(), cid, trimestreCombo.getValue(), annee, "CONTROLE_CONTINU");
                        Double exam = noteDAO.getNoteEleveCours(eleve.getId(), cid, trimestreCombo.getValue(), annee, "EXAMEN");
                        Double moyenneCours = m; // moyenne déjà calculée par le service
                        lignes.add(new LigneCours(nomCours, cc, exam != null ? exam : moyenneCours));
                    });
                    table.setItems(FXCollections.observableArrayList(lignes));
                    moyenneLbl.setText("Moyenne Générale : " + resultat.getMoyenneGenerale());
                    mentionLbl.setText("Mention : " + resultat.getMention());
                    rangLbl.setText("Rang : " + resultat.getRang());
                }
            }
        });

        root.getChildren().addAll(titre, new Label("Choisissez un trimestre :"), trimestreCombo, afficherBtn, table, moyenneLbl, mentionLbl, rangLbl);

        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }
}
