package com.gestion.gestionscolaire;

import com.gestion.gestionscolaire.config.DatabaseConfig;
import com.gestion.gestionscolaire.controller.MainController;
import com.gestion.gestionscolaire.controller.StudentController;
import com.gestion.gestionscolaire.service.AuthService;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainApp extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Test de la connexion à la base de données
            DatabaseConfig.testConnection();
            
            // Créer la fenêtre de connexion
            VBox loginPane = createLoginPane(primaryStage);
            Scene scene = new Scene(loginPane);
            primaryStage.setMinWidth(350);
            primaryStage.setMinHeight(300);
            scene.widthProperty().addListener((obs,oldV,newV)->{
                double w=newV.doubleValue();
                loginPane.setPrefWidth(w);
            });
            scene.heightProperty().addListener((obs,oldV,newV)->{
                double h=newV.doubleValue();
                loginPane.setPrefHeight(h);
            });
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            
            primaryStage.setTitle("Gestion Scolaire - Connexion");
            primaryStage.setScene(scene);

            primaryStage.show();
            
        } catch (Exception e) {
            showError("Erreur d'initialisation", "Impossible de démarrer l'application : " + e.getMessage());
        }
    }
    
    private VBox createLoginPane(Stage primaryStage) {
        VBox vbox = new VBox(15);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(30));
        
        
        Label title = new Label("Système de Gestion Scolaire");
        title.getStyleClass().addAll("label","title");
        
        Label subtitle = new Label("Connexion");
        
        // Choix du type de connexion
        ToggleGroup tg = new ToggleGroup();
        RadioButton rbPersonnel = new RadioButton("Personnel (admin/enseignant)");
        rbPersonnel.setToggleGroup(tg);
        rbPersonnel.setSelected(true);
        RadioButton rbEleve = new RadioButton("Élève");
        rbEleve.setToggleGroup(tg);
        HBox choixBox = new HBox(10, rbPersonnel, rbEleve);
        choixBox.setAlignment(Pos.CENTER);
        subtitle.setStyle("-fx-font-size: 14px;");
        
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setMaxWidth(250);
        
        PasswordField passwordField = new PasswordField();
        
        // Champ numéro élève
        TextField numeroField = new TextField();
        numeroField.setPromptText("Numéro d'élève");
        numeroField.setMaxWidth(150);
        numeroField.setVisible(false);
        passwordField.setPromptText("Mot de passe");
        passwordField.setMaxWidth(250);
        
        Button loginButton = new Button("Se connecter");
        loginButton.getStyleClass().addAll("button","primary");
        loginButton.setPrefWidth(150);
        
        
        
        
        // basculer visibilité selon radio
        rbPersonnel.setOnAction(e -> {
            emailField.setVisible(true);
            passwordField.setVisible(true);
            numeroField.setVisible(false);
        });
        rbEleve.setOnAction(e -> {
            emailField.setVisible(false);
            passwordField.setVisible(false);
            numeroField.setVisible(true);
        });
        
        loginButton.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText();
            
            if (rbPersonnel.isSelected()) {
                if (email.isEmpty() || password.isEmpty()) {
                    showError("Erreur", "Veuillez remplir tous les champs");
                    return;
                }
            } else {
                if (numeroField.getText().trim().isEmpty()) {
                    showError("Erreur", "Veuillez saisir le numéro d'élève");
                    return;
                }
            }
            
            MainController controller = new MainController();
            AuthService authService = new AuthService();
            boolean ok;
            if (rbPersonnel.isSelected()) {
                ok = controller.seConnecter(email, password);
            } else {
                ok = authService.seConnecterEleve(numeroField.getText().trim());
            }
            if (ok) {
                primaryStage.close();
                if (rbPersonnel.isSelected()) {
                    controller.ouvrirFenetrePrincipale();
                } else {
                    new StudentController().ouvrirFenetreEleve();
                }
            } else {
                showError("Erreur de connexion", "Email ou mot de passe incorrect");
                passwordField.clear();
            }
        });
        
        passwordField.setOnAction(e -> loginButton.fire());
        
        vbox.getChildren().addAll(title, subtitle, choixBox, emailField, passwordField, numeroField, loginButton);
        
        return vbox;
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}