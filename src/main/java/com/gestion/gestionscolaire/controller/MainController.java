package com.gestion.gestionscolaire.controller;

import java.util.ArrayList;
import java.util.List;

import com.gestion.gestionscolaire.model.Anonymat;
import com.gestion.gestionscolaire.model.Classe;
import com.gestion.gestionscolaire.model.Cours;
import com.gestion.gestionscolaire.model.Eleve;
import com.gestion.gestionscolaire.model.Niveau;
import com.gestion.gestionscolaire.model.Note;
import com.gestion.gestionscolaire.model.Utilisateur;
import com.gestion.gestionscolaire.service.AuthService;
import com.gestion.gestionscolaire.service.BulletinService;
import com.gestion.gestionscolaire.service.GestionService;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainController {
    private final AuthService authService = new AuthService();
    private final GestionService gestionService = new GestionService();
    private final BulletinService bulletinService = new BulletinService();
    
    // Variables pour l'actualisation des données
    private TableView<Classe> classesTable;
    private TableView<Eleve> elevesTable;
    private TableView<Cours> coursTable;
    private TableView<Utilisateur> enseignantsTable;
    private TabPane currentTabPane;
    
    public boolean seConnecter(String email, String password) {
        return authService.seConnecter(email, password);
    }
    
    public void ouvrirFenetrePrincipale() {
        Stage stage = new Stage();
        stage.setTitle("Gestion Scolaire - " + AuthService.getUtilisateurConnecte().getNomComplet());
        
        VBox mainLayout = new VBox();
        
        // Barre d'outils en haut
        HBox toolbar = createToolbar(stage);
        
        // Interface principale
        Node mainInterface;
        if (authService.estAdmin()) {
            mainInterface = createAdminInterface();
        } else if (authService.estEnseignant()) {
            mainInterface = createEnseignantInterface();
        } else {
            mainInterface = new Label("Accès non autorisé");
        }
        
        mainLayout.getChildren().addAll(toolbar, mainInterface);
        
        stage.setScene(new Scene(mainLayout, 1000, 700));
        stage.setOnCloseRequest(e -> authService.seDeconnecter());
        stage.show();
    }
    
    private HBox createToolbar(Stage currentStage) {
        HBox toolbar = new HBox(15);
        toolbar.setPadding(new Insets(8, 15, 8, 15));
        toolbar.setStyle("-fx-background-color: linear-gradient(to bottom, #f8f9fa, #e9ecef); " +
                        "-fx-border-color: #dee2e6; -fx-border-width: 0 0 2 0; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 5, 0, 0, 1);");
        toolbar.setAlignment(Pos.CENTER_LEFT);
        
        // Informations utilisateur avec icône
        Label userInfo = new Label("👤 " + AuthService.getUtilisateurConnecte().getNomComplet() + 
                                  " (" + AuthService.getUtilisateurConnecte().getRole() + ")");
        userInfo.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #495057;");
        
        // Date et heure
        Label dateLabel = new Label("📅 " + java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        dateLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #6c757d;");
        
        // Spacer pour pousser les boutons à droite
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Bouton Actualiser
        Button actualiserBtn = new Button("🔄 Actualiser");
        actualiserBtn.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white; " +
                              "-fx-font-weight: bold; -fx-background-radius: 5; " +
                              "-fx-padding: 8 16 8 16; -fx-cursor: hand;");
        actualiserBtn.setOnMouseEntered(e -> actualiserBtn.setStyle(
            "-fx-background-color: #138496; -fx-text-fill: white; " +
            "-fx-font-weight: bold; -fx-background-radius: 5; " +
            "-fx-padding: 8 16 8 16; -fx-cursor: hand;"));
        actualiserBtn.setOnMouseExited(e -> actualiserBtn.setStyle(
            "-fx-background-color: #17a2b8; -fx-text-fill: white; " +
            "-fx-font-weight: bold; -fx-background-radius: 5; " +
            "-fx-padding: 8 16 8 16; -fx-cursor: hand;"));
        actualiserBtn.setOnAction(e -> {
            actualiserDonnees();
            showInfo("✅ Actualisation", "Toutes les données ont été actualisées avec succès !");
        });
        
        // Bouton Déconnexion
        Button deconnexionBtn = new Button("🚪 Déconnexion");
        deconnexionBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; " +
                               "-fx-font-weight: bold; -fx-background-radius: 5; " +
                               "-fx-padding: 8 16 8 16; -fx-cursor: hand;");
        deconnexionBtn.setOnMouseEntered(e -> deconnexionBtn.setStyle(
            "-fx-background-color: #c82333; -fx-text-fill: white; " +
            "-fx-font-weight: bold; -fx-background-radius: 5; " +
            "-fx-padding: 8 16 8 16; -fx-cursor: hand;"));
        deconnexionBtn.setOnMouseExited(e -> deconnexionBtn.setStyle(
            "-fx-background-color: #dc3545; -fx-text-fill: white; " +
            "-fx-font-weight: bold; -fx-background-radius: 5; " +
            "-fx-padding: 8 16 8 16; -fx-cursor: hand;"));
        deconnexionBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation de déconnexion");
            confirm.setHeaderText("Déconnexion de l'application");
            confirm.setContentText("Êtes-vous sûr de vouloir vous déconnecter ?");
            
            if (confirm.showAndWait().get() == ButtonType.OK) {
                authService.seDeconnecter();
                currentStage.close();
                
                // Réouvrir la fenêtre de connexion
                try {
                    new com.gestion.gestionscolaire.MainApp().start(new Stage());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Erreur", "Impossible de rouvrir la fenêtre de connexion");
                }
            }
        });
        
        // Séparateur visuel
        javafx.scene.control.Separator separator = new javafx.scene.control.Separator();
        separator.setOrientation(javafx.geometry.Orientation.VERTICAL);
        separator.setStyle("-fx-background-color: #dee2e6;");
        
        toolbar.getChildren().addAll(userInfo, dateLabel, spacer, actualiserBtn, separator, deconnexionBtn);
        
        return toolbar;
    }
    
    private TabPane createAdminInterface() {
        TabPane tabPane = new TabPane();
        currentTabPane = tabPane; // Stocker la référence
        
        // Onglet Gestion des Classes
        Tab classesTab = new Tab("Classes", createClassesPane());
        classesTab.setClosable(false);
        
        // Onglet Gestion des Élèves
        Tab elevesTab = new Tab("Élèves", createElevesPane());
        elevesTab.setClosable(false);
        
        // Onglet Gestion des Cours
        Tab coursTab = new Tab("Cours", createCoursPane());
        coursTab.setClosable(false);
        
        // Onglet Gestion des Enseignants
        Tab enseignantsTab = new Tab("Enseignants", createEnseignantsPane());
        enseignantsTab.setClosable(false);
        
        // Onglet Anonymats
        Tab anonymatsTab = new Tab("Anonymats", createAnonymatsPane());
        anonymatsTab.setClosable(false);
        
        // Onglet Bulletins
        Tab bulletinsTab = new Tab("Bulletins", createBulletinsPane());
        bulletinsTab.setClosable(false);
        
        tabPane.getTabs().addAll(classesTab, elevesTab, coursTab, enseignantsTab, anonymatsTab, bulletinsTab);
        
        return tabPane;
    }
    
    private TabPane createEnseignantInterface() {
        TabPane tabPane = new TabPane();
        currentTabPane = tabPane; // Stocker la référence
        
        // Onglet Saisie des Notes
        Tab notesTab = new Tab("Saisie des Notes", createNotesPane());
        notesTab.setClosable(false);
        
        // Onglet Mes Classes
        Tab classesTab = new Tab("Mes Classes", createMesClassesPane());
        classesTab.setClosable(false);
        
        tabPane.getTabs().addAll(notesTab, classesTab);
        
        return tabPane;
    }
    
    private VBox createClassesPane() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        
        Label title = new Label("Gestion des Classes");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Formulaire d'ajout/modification
        HBox inputBox = new HBox(10);
        TextField nomField = new TextField();
        nomField.setPromptText("Nom de la classe (ex: 6èmeA)");
        
        ComboBox<Niveau> niveauCombo = new ComboBox<>();
        niveauCombo.setItems(FXCollections.observableArrayList(gestionService.getAllNiveaux()));
        niveauCombo.setPromptText("Niveau");
        
        Button ajouterBtn = new Button("Ajouter");
        Button modifierBtn = new Button("Modifier");
        Button supprimerBtn = new Button("Supprimer");
        
        modifierBtn.setDisable(true);
        supprimerBtn.setDisable(true);
        
        // TableView pour afficher les classes
        classesTable = new TableView<>();
        classesTable.setItems(FXCollections.observableArrayList(gestionService.getAllClasses()));
        
        TableColumn<Classe, String> nomCol = new TableColumn<>("Nom de la classe");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        nomCol.setPrefWidth(150);
        
        TableColumn<Classe, String> niveauCol = new TableColumn<>("Niveau");
        niveauCol.setCellValueFactory(new PropertyValueFactory<>("niveauNom"));
        niveauCol.setPrefWidth(100);
        
        classesTable.getColumns().add(nomCol);
        classesTable.getColumns().add(niveauCol);
        classesTable.setPrefHeight(300);
        
        // Événements des boutons
        ajouterBtn.setOnAction(e -> {
            if (!nomField.getText().isEmpty() && niveauCombo.getValue() != null) {
                if (gestionService.creerClasse(nomField.getText(), niveauCombo.getValue().getId())) {
                    showInfo("Succès", "Classe créée avec succès");
                    nomField.clear();
                    niveauCombo.setValue(null);
                    classesTable.setItems(FXCollections.observableArrayList(gestionService.getAllClasses()));
                } else {
                    showError("Erreur", "Impossible de créer la classe");
                }
            } else {
                showError("Erreur", "Veuillez remplir tous les champs");
            }
        });
        
        modifierBtn.setOnAction(e -> {
            Classe selectedClasse = classesTable.getSelectionModel().getSelectedItem();
            if (selectedClasse != null && !nomField.getText().isEmpty() && niveauCombo.getValue() != null) {
                if (gestionService.modifierClasse(selectedClasse.getId(), nomField.getText(), niveauCombo.getValue().getId())) {
                    showInfo("Succès", "Classe modifiée avec succès");
                    nomField.clear();
                    niveauCombo.setValue(null);
                    classesTable.setItems(FXCollections.observableArrayList(gestionService.getAllClasses()));
                    classesTable.getSelectionModel().clearSelection();
                    modifierBtn.setDisable(true);
                    supprimerBtn.setDisable(true);
                } else {
                    showError("Erreur", "Impossible de modifier la classe");
                }
            }
        });
        
        supprimerBtn.setOnAction(e -> {
            Classe selectedClasse = classesTable.getSelectionModel().getSelectedItem();
            if (selectedClasse != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Confirmation");
                confirm.setHeaderText("Supprimer la classe");
                confirm.setContentText("Êtes-vous sûr de vouloir supprimer la classe " + selectedClasse.getNom() + " ?");
                
                if (confirm.showAndWait().get() == ButtonType.OK) {
                    if (gestionService.supprimerClasse(selectedClasse.getId())) {
                        showInfo("Succès", "Classe supprimée avec succès");
                        classesTable.setItems(FXCollections.observableArrayList(gestionService.getAllClasses()));
                        classesTable.getSelectionModel().clearSelection();
                        modifierBtn.setDisable(true);
                        supprimerBtn.setDisable(true);
                    } else {
                        showError("Erreur", "Impossible de supprimer la classe (vérifiez qu'elle ne contient pas d'élèves)");
                    }
                }
            }
        });
        
        // Sélection dans la table
        classesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nomField.setText(newSelection.getNom());
                // Trouver le niveau correspondant
                for (Niveau niveau : niveauCombo.getItems()) {
                    if (niveau.getId() == newSelection.getNiveauId()) {
                        niveauCombo.setValue(niveau);
                        break;
                    }
                }
                modifierBtn.setDisable(false);
                supprimerBtn.setDisable(false);
            } else {
                nomField.clear();
                niveauCombo.setValue(null);
                modifierBtn.setDisable(true);
                supprimerBtn.setDisable(true);
            }
        });
        
        inputBox.getChildren().addAll(new Label("Nom:"), nomField, new Label("Niveau:"), niveauCombo);
        
        HBox buttonsBox = new HBox(10);
        Button refreshBtn = createRefreshButton("classes");
        buttonsBox.getChildren().addAll(ajouterBtn, modifierBtn, supprimerBtn, refreshBtn);
        
        vbox.getChildren().addAll(title, inputBox, buttonsBox, new Label("Classes existantes (cliquez pour sélectionner):"), classesTable);
        
        return vbox;
    }
    
    private VBox createElevesPane() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        
        Label title = new Label("Gestion des Élèves");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Formulaire d'ajout/modification
        GridPane gridForm = new GridPane();
        gridForm.setHgap(10);
        gridForm.setVgap(10);
        
        TextField nomField = new TextField();
        nomField.setPromptText("Nom");
        TextField prenomField = new TextField();
        prenomField.setPromptText("Prénom");
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Date de naissance");
        ComboBox<Classe> classeCombo = new ComboBox<>();
        classeCombo.setItems(FXCollections.observableArrayList(gestionService.getAllClasses()));
        classeCombo.setPromptText("Classe");
        
        gridForm.add(new Label("Nom:"), 0, 0);
        gridForm.add(nomField, 1, 0);
        gridForm.add(new Label("Prénom:"), 2, 0);
        gridForm.add(prenomField, 3, 0);
        gridForm.add(new Label("Date naissance:"), 0, 1);
        gridForm.add(datePicker, 1, 1);
        gridForm.add(new Label("Classe:"), 2, 1);
        gridForm.add(classeCombo, 3, 1);
        
        // Boutons d'action
        Button ajouterBtn = new Button("Ajouter Élève");
        Button modifierBtn = new Button("Modifier");
        Button supprimerBtn = new Button("Supprimer");
        
        modifierBtn.setDisable(true);
        supprimerBtn.setDisable(true);
        
        // TableView pour afficher tous les élèves
        elevesTable = new TableView<>();
        
        TableColumn<Eleve, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        nomCol.setPrefWidth(100);
        
        TableColumn<Eleve, String> prenomCol = new TableColumn<>("Prénom");
        prenomCol.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        prenomCol.setPrefWidth(100);
        
        TableColumn<Eleve, String> classeCol = new TableColumn<>("Classe");
        classeCol.setCellValueFactory(new PropertyValueFactory<>("classeNom"));
        classeCol.setPrefWidth(100);
        
        TableColumn<Eleve, String> numeroCol = new TableColumn<>("N° Élève");
        numeroCol.setCellValueFactory(new PropertyValueFactory<>("numeroEleve"));
        numeroCol.setPrefWidth(80);
        
        TableColumn<Eleve, String> dateCol = new TableColumn<>("Date naissance");
        dateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getDateNaissance().toString()));
        dateCol.setPrefWidth(120);
        
        elevesTable.getColumns().add(nomCol);
        elevesTable.getColumns().add(prenomCol);
        elevesTable.getColumns().add(classeCol);
        elevesTable.getColumns().add(numeroCol);
        elevesTable.getColumns().add(dateCol);
        elevesTable.setPrefHeight(300);
        
        // Charger tous les élèves
        refreshElevesTable(elevesTable);
        
        // Événements des boutons
        ajouterBtn.setOnAction(e -> {
            if (!nomField.getText().isEmpty() && !prenomField.getText().isEmpty() 
                && datePicker.getValue() != null && classeCombo.getValue() != null) {
                
                if (gestionService.creerEleve(nomField.getText(), prenomField.getText(), 
                                            datePicker.getValue(), classeCombo.getValue().getId())) {
                    showInfo("Succès", "Élève créé avec succès");
                    clearEleveForm(nomField, prenomField, datePicker, classeCombo);
                    refreshElevesTable(elevesTable);
                } else {
                    showError("Erreur", "Impossible de créer l'élève");
                }
            } else {
                showError("Erreur", "Veuillez remplir tous les champs");
            }
        });
        
        modifierBtn.setOnAction(e -> {
            Eleve selectedEleve = elevesTable.getSelectionModel().getSelectedItem();
            if (selectedEleve != null && !nomField.getText().isEmpty() && !prenomField.getText().isEmpty() 
                && datePicker.getValue() != null && classeCombo.getValue() != null) {
                
                if (gestionService.modifierEleve(selectedEleve.getId(), nomField.getText(), prenomField.getText(), 
                                               datePicker.getValue(), classeCombo.getValue().getId())) {
                    showInfo("Succès", "Élève modifié avec succès");
                    clearEleveForm(nomField, prenomField, datePicker, classeCombo);
                    refreshElevesTable(elevesTable);
                    elevesTable.getSelectionModel().clearSelection();
                    modifierBtn.setDisable(true);
                    supprimerBtn.setDisable(true);
                } else {
                    showError("Erreur", "Impossible de modifier l'élève");
                }
            }
        });
        
        supprimerBtn.setOnAction(e -> {
            Eleve selectedEleve = elevesTable.getSelectionModel().getSelectedItem();
            if (selectedEleve != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Confirmation");
                confirm.setHeaderText("Supprimer l'élève");
                confirm.setContentText("Êtes-vous sûr de vouloir supprimer l'élève " + selectedEleve.getNomComplet() + " ?");
                
                if (confirm.showAndWait().get() == ButtonType.OK) {
                    if (gestionService.supprimerEleve(selectedEleve.getId())) {
                        showInfo("Succès", "Élève supprimé avec succès");
                        refreshElevesTable(elevesTable);
                        elevesTable.getSelectionModel().clearSelection();
                        modifierBtn.setDisable(true);
                        supprimerBtn.setDisable(true);
                    } else {
                        showError("Erreur", "Impossible de supprimer l'élève");
                    }
                }
            }
        });
        
        // Sélection dans la table
        elevesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nomField.setText(newSelection.getNom());
                prenomField.setText(newSelection.getPrenom());
                datePicker.setValue(newSelection.getDateNaissance());
                // Trouver la classe correspondante
                for (Classe classe : classeCombo.getItems()) {
                    if (classe.getId() == newSelection.getClasseId()) {
                        classeCombo.setValue(classe);
                        break;
                    }
                }
                modifierBtn.setDisable(false);
                supprimerBtn.setDisable(false);
            } else {
                clearEleveForm(nomField, prenomField, datePicker, classeCombo);
                modifierBtn.setDisable(true);
                supprimerBtn.setDisable(true);
            }
        });
        
        HBox buttonsBox = new HBox(10);
        Button refreshBtn = createRefreshButton("eleves");
        buttonsBox.getChildren().addAll(ajouterBtn, modifierBtn, supprimerBtn, refreshBtn);
        
        vbox.getChildren().addAll(title, gridForm, buttonsBox, 
                                 new Label("Liste des élèves (cliquez pour sélectionner):"), elevesTable);
        
        return vbox;
    }
    
    private VBox createCoursPane() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        
        Label title = new Label("Gestion des Cours");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Formulaire d'ajout/modification
        HBox inputBox = new HBox(10);
        TextField nomField = new TextField();
        nomField.setPromptText("Nom du cours");
        TextField coeffField = new TextField();
        coeffField.setPromptText("Coefficient");
        
        Button ajouterBtn = new Button("Ajouter");
        Button modifierBtn = new Button("Modifier");
        Button supprimerBtn = new Button("Supprimer");
        
        modifierBtn.setDisable(true);
        supprimerBtn.setDisable(true);
        
        // TableView pour un meilleur affichage
        coursTable = new TableView<>();
        coursTable.setItems(FXCollections.observableArrayList(gestionService.getAllCours()));
        
        TableColumn<Cours, String> nomCol = new TableColumn<>("Matière");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        nomCol.setPrefWidth(200);
        
        TableColumn<Cours, Double> coeffCol = new TableColumn<>("Coefficient");
        coeffCol.setCellValueFactory(new PropertyValueFactory<>("coefficient"));
        coeffCol.setPrefWidth(100);
        
        coursTable.getColumns().addAll(nomCol, coeffCol);
        coursTable.setPrefHeight(300);
        
        // Événements des boutons
        ajouterBtn.setOnAction(e -> {
            try {
                if (!nomField.getText().isEmpty() && !coeffField.getText().isEmpty()) {
                    double coeff = Double.parseDouble(coeffField.getText());
                    if (gestionService.creerCours(nomField.getText(), coeff)) {
                        showInfo("Succès", "Cours créé avec succès");
                        nomField.clear();
                        coeffField.clear();
                        coursTable.setItems(FXCollections.observableArrayList(gestionService.getAllCours()));
                    } else {
                        showError("Erreur", "Impossible de créer le cours");
                    }
                } else {
                    showError("Erreur", "Veuillez remplir tous les champs");
                }
            } catch (NumberFormatException ex) {
                showError("Erreur", "Le coefficient doit être un nombre");
            }
        });
        
        modifierBtn.setOnAction(e -> {
            Cours selectedCours = coursTable.getSelectionModel().getSelectedItem();
            try {
                if (selectedCours != null && !nomField.getText().isEmpty() && !coeffField.getText().isEmpty()) {
                    double coeff = Double.parseDouble(coeffField.getText());
                    if (gestionService.modifierCours(selectedCours.getId(), nomField.getText(), coeff)) {
                        showInfo("Succès", "Cours modifié avec succès");
                        nomField.clear();
                        coeffField.clear();
                        coursTable.setItems(FXCollections.observableArrayList(gestionService.getAllCours()));
                        coursTable.getSelectionModel().clearSelection();
                        modifierBtn.setDisable(true);
                        supprimerBtn.setDisable(true);
                    } else {
                        showError("Erreur", "Impossible de modifier le cours");
                    }
                }
            } catch (NumberFormatException ex) {
                showError("Erreur", "Le coefficient doit être un nombre");
            }
        });
        
        supprimerBtn.setOnAction(e -> {
            Cours selectedCours = coursTable.getSelectionModel().getSelectedItem();
            if (selectedCours != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Confirmation");
                confirm.setHeaderText("Supprimer le cours");
                confirm.setContentText("Êtes-vous sûr de vouloir supprimer le cours " + selectedCours.getNom() + " ?");
                
                if (confirm.showAndWait().get() == ButtonType.OK) {
                    if (gestionService.supprimerCours(selectedCours.getId())) {
                        showInfo("Succès", "Cours supprimé avec succès");
                        coursTable.setItems(FXCollections.observableArrayList(gestionService.getAllCours()));
                        coursTable.getSelectionModel().clearSelection();
                        modifierBtn.setDisable(true);
                        supprimerBtn.setDisable(true);
                    } else {
                        showError("Erreur", "Impossible de supprimer le cours (vérifiez qu'il n'a pas de notes associées)");
                    }
                }
            }
        });
        
        // Sélection dans la table
        coursTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nomField.setText(newSelection.getNom());
                coeffField.setText(String.valueOf(newSelection.getCoefficient()));
                modifierBtn.setDisable(false);
                supprimerBtn.setDisable(false);
            } else {
                nomField.clear();
                coeffField.clear();
                modifierBtn.setDisable(true);
                supprimerBtn.setDisable(true);
            }
        });
        
        inputBox.getChildren().addAll(new Label("Nom:"), nomField, new Label("Coefficient:"), coeffField);
        
        HBox buttonsBox = new HBox(10);
        Button refreshBtn = createRefreshButton("cours");
        buttonsBox.getChildren().addAll(ajouterBtn, modifierBtn, supprimerBtn, refreshBtn);
        
        vbox.getChildren().addAll(title, inputBox, buttonsBox, new Label("Cours existants (cliquez pour sélectionner):"), coursTable);
        
        return vbox;
    }
    
    private VBox createEnseignantsPane() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        
        Label title = new Label("Gestion des Enseignants");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Formulaire d'ajout/modification
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        TextField nomField = new TextField();
        TextField prenomField = new TextField();
        TextField emailField = new TextField();
        PasswordField passwordField = new PasswordField();
        
        // Ajout des listes multi-sélection pour classes et cours
        ObservableList<Classe> allClasses = FXCollections.observableArrayList(gestionService.getAllClasses());
        ObservableList<Cours> allCours = FXCollections.observableArrayList(gestionService.getAllCours());
        ListView<Classe> classesListView = new ListView<>(allClasses);
        ObservableMap<Classe, SimpleBooleanProperty> classeCheckMap = FXCollections.observableHashMap();
        classesListView.setCellFactory(CheckBoxListCell.forListView(classe -> {
            SimpleBooleanProperty prop = classeCheckMap.computeIfAbsent(classe, c -> new SimpleBooleanProperty(false));
            prop.addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected) {
                    if (!classesListView.getSelectionModel().getSelectedItems().contains(classe))
                        classesListView.getSelectionModel().select(classe);
                } else {
                    classesListView.getSelectionModel().clearSelection(allClasses.indexOf(classe));
                }
            });
            // Synchronisation sélection <-> case cochée
            classesListView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<? super Classe>) c -> {
                prop.set(classesListView.getSelectionModel().getSelectedItems().contains(classe));
            });
            return prop;
        }));
        classesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        classesListView.setPrefHeight(100);

        ListView<Cours> coursListView = new ListView<>(allCours);
        ObservableMap<Cours, SimpleBooleanProperty> coursCheckMap = FXCollections.observableHashMap();
        coursListView.setCellFactory(CheckBoxListCell.forListView(cours -> {
            SimpleBooleanProperty prop = coursCheckMap.computeIfAbsent(cours, c -> new SimpleBooleanProperty(false));
            prop.addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected) {
                    if (!coursListView.getSelectionModel().getSelectedItems().contains(cours))
                        coursListView.getSelectionModel().select(cours);
                } else {
                    coursListView.getSelectionModel().clearSelection(allCours.indexOf(cours));
                }
            });
            // Synchronisation sélection <-> case cochée
            coursListView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<? super Cours>) c -> {
                prop.set(coursListView.getSelectionModel().getSelectedItems().contains(cours));
            });
            return prop;
        }));
        coursListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        coursListView.setPrefHeight(100);
        
        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Prénom:"), 0, 1);
        grid.add(prenomField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Mot de passe:"), 0, 3);
        grid.add(passwordField, 1, 3);
        grid.add(new Label("Classes attribuées :"), 0, 4);
        grid.add(classesListView, 1, 4);
        grid.add(new Label("Cours attribués :"), 0, 5);
        grid.add(coursListView, 1, 5);
        
        // Boutons d'action
        Button ajouterBtn = new Button("Ajouter Enseignant");
        Button modifierBtn = new Button("Modifier");
        Button supprimerBtn = new Button("Supprimer");
        
        modifierBtn.setDisable(true);
        supprimerBtn.setDisable(true);
        
        // TableView pour afficher les enseignants
        enseignantsTable = new TableView<>();
        
        TableColumn<Utilisateur, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        nomCol.setPrefWidth(120);
        
        TableColumn<Utilisateur, String> prenomCol = new TableColumn<>("Prénom");
        prenomCol.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        prenomCol.setPrefWidth(120);
        
        TableColumn<Utilisateur, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(200);
        
        TableColumn<Utilisateur, String> roleCol = new TableColumn<>("Rôle");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleCol.setPrefWidth(100);
        
        TableColumn<Utilisateur, String> classesCol = new TableColumn<>("Classes");
        classesCol.setCellValueFactory(data -> {
            List<Classe> classes = gestionService.getClassesByEnseignant(data.getValue().getId());
            String noms = classes.stream().map(Classe::getNom).reduce((a, b) -> a + ", " + b).orElse("");
            return new javafx.beans.property.SimpleStringProperty(noms);
        });
        classesCol.setPrefWidth(150);

        TableColumn<Utilisateur, String> coursCol = new TableColumn<>("Cours");
        coursCol.setCellValueFactory(data -> {
            List<Cours> cours = gestionService.getCoursByEnseignant(data.getValue().getId());
            String noms = cours.stream().map(Cours::getNom).reduce((a, b) -> a + ", " + b).orElse("");
            return new javafx.beans.property.SimpleStringProperty(noms);
        });
        coursCol.setPrefWidth(150);

        enseignantsTable.getColumns().addAll(nomCol, prenomCol, emailCol, roleCol, classesCol, coursCol);
        enseignantsTable.setPrefHeight(250);
        
        // Charger la liste des enseignants
        refreshEnseignantsTable(enseignantsTable);
        
        // Événements des boutons
        ajouterBtn.setOnAction(e -> {
            if (!nomField.getText().isEmpty() && !prenomField.getText().isEmpty() 
                && !emailField.getText().isEmpty() && !passwordField.getText().isEmpty()) {
                List<Classe> classesSelectionnees = new ArrayList<>(classesListView.getSelectionModel().getSelectedItems());
                List<Cours> coursSelectionnes = new ArrayList<>(coursListView.getSelectionModel().getSelectedItems());
                if (gestionService.creerEnseignant(nomField.getText(), prenomField.getText(), 
                        emailField.getText(), passwordField.getText(), classesSelectionnees, coursSelectionnes)) {
                    showInfo("Succès", "Enseignant créé avec succès");
                    clearEnseignantForm(nomField, prenomField, emailField, passwordField);
                    refreshEnseignantsTable(enseignantsTable);
                } else {
                    showError("Erreur", "Impossible de créer l'enseignant (email déjà existant ? ou affectation échouée)");
                }
            } else {
                showError("Erreur", "Veuillez remplir tous les champs");
            }
        });
        
        modifierBtn.setOnAction(e -> {
            Utilisateur selectedEnseignant = enseignantsTable.getSelectionModel().getSelectedItem();
            if (selectedEnseignant != null && !nomField.getText().isEmpty() && !prenomField.getText().isEmpty() 
                && !emailField.getText().isEmpty()) {
                if (gestionService.modifierEnseignant(selectedEnseignant.getId(), nomField.getText(), 
                                                    prenomField.getText(), emailField.getText())) {
                    showInfo("Succès", "Enseignant modifié avec succès");
                    clearEnseignantForm(nomField, prenomField, emailField, passwordField);
                    refreshEnseignantsTable(enseignantsTable);
                    enseignantsTable.getSelectionModel().clearSelection();
                    modifierBtn.setDisable(true);
                    supprimerBtn.setDisable(true);
                } else {
                    showError("Erreur", "Impossible de modifier l'enseignant");
                }
            }
        });
        
        supprimerBtn.setOnAction(e -> {
            Utilisateur selectedEnseignant = enseignantsTable.getSelectionModel().getSelectedItem();
            if (selectedEnseignant != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Confirmation");
                confirm.setHeaderText("Supprimer l'enseignant");
                confirm.setContentText("Êtes-vous sûr de vouloir supprimer l'enseignant " + selectedEnseignant.getNomComplet() + " ?");
                
                if (confirm.showAndWait().get() == ButtonType.OK) {
                    if (gestionService.supprimerEnseignant(selectedEnseignant.getId())) {
                        showInfo("Succès", "Enseignant supprimé avec succès");
                        refreshEnseignantsTable(enseignantsTable);
                        enseignantsTable.getSelectionModel().clearSelection();
                        modifierBtn.setDisable(true);
                        supprimerBtn.setDisable(true);
                    } else {
                        showError("Erreur", "Impossible de supprimer l'enseignant");
                    }
                }
            }
        });
        
        // Sélection dans la table
        enseignantsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nomField.setText(newSelection.getNom());
                prenomField.setText(newSelection.getPrenom());
                emailField.setText(newSelection.getEmail());
                passwordField.clear(); // Ne pas afficher le mot de passe
                passwordField.setPromptText("Laissez vide pour conserver");
                modifierBtn.setDisable(false);
                supprimerBtn.setDisable(false);
            } else {
                clearEnseignantForm(nomField, prenomField, emailField, passwordField);
                modifierBtn.setDisable(true);
                supprimerBtn.setDisable(true);
            }
        });
        
        // Info pour l'administrateur
        Label infoSecurity = new Label("Note: Les nouveaux enseignants sont automatiquement créés avec des mots de passe sécurisés (hashés).\nPour modifier: sélectionnez un enseignant dans la liste.");
        infoSecurity.setStyle("-fx-font-size: 10px; -fx-text-fill: #666; -fx-wrap-text: true;");
        infoSecurity.setMaxWidth(500);
        
        HBox buttonsBox = new HBox(10);
        Button refreshBtn = createRefreshButton("enseignants");
        buttonsBox.getChildren().addAll(ajouterBtn, modifierBtn, supprimerBtn, refreshBtn);
        
        vbox.getChildren().addAll(title, grid, buttonsBox, infoSecurity,
                                 new Label("Liste des enseignants (cliquez pour sélectionner):"), enseignantsTable);
        
        return vbox;
    }
    
    private VBox createAnonymatsPane() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        
        Label title = new Label("Gestion des Anonymats");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        HBox controls = new HBox(10);
        ComboBox<Classe> classeCombo = new ComboBox<>();
        classeCombo.setItems(FXCollections.observableArrayList(gestionService.getAllClasses()));
        classeCombo.setPromptText("Classe");
        
        ComboBox<Integer> trimestreCombo = new ComboBox<>();
        trimestreCombo.setItems(FXCollections.observableArrayList(1, 2, 3));
        trimestreCombo.setPromptText("Trimestre");
        
        Button genererBtn = new Button("Générer Anonymats");
        genererBtn.setOnAction(e -> {
            if (classeCombo.getValue() != null && trimestreCombo.getValue() != null) {
                if (gestionService.genererAnonymats(classeCombo.getValue().getId(), trimestreCombo.getValue())) {
                    showInfo("Succès", "Anonymats générés avec succès");
                    afficherAnonymats(vbox, classeCombo.getValue().getId(), trimestreCombo.getValue());
                } else {
                    showError("Erreur", "Impossible de générer les anonymats");
                }
            } else {
                showError("Erreur", "Veuillez sélectionner une classe et un trimestre");
            }
        });
        
        controls.getChildren().addAll(new Label("Classe:"), classeCombo, new Label("Trimestre:"), trimestreCombo, genererBtn);
        
        vbox.getChildren().addAll(title, controls);
        
        return vbox;
    }
    
    private VBox createBulletinsPane() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        
        Label title = new Label("Génération des Bulletins");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        HBox controls = new HBox(10);
        ComboBox<Classe> classeCombo = new ComboBox<>();
        classeCombo.setItems(FXCollections.observableArrayList(gestionService.getAllClasses()));
        classeCombo.setPromptText("Classe");
        
        ComboBox<Integer> trimestreCombo = new ComboBox<>();
        trimestreCombo.setItems(FXCollections.observableArrayList(1, 2, 3));
        trimestreCombo.setPromptText("Trimestre");
        
        Button genererBtn = new Button("Générer Bulletins");
        genererBtn.setOnAction(e -> {
            if (classeCombo.getValue() != null && trimestreCombo.getValue() != null) {
                genererBulletins(vbox, classeCombo.getValue(), trimestreCombo.getValue());
            } else {
                showError("Erreur", "Veuillez sélectionner une classe et un trimestre");
            }
        });
        
        controls.getChildren().addAll(new Label("Classe:"), classeCombo, new Label("Trimestre:"), trimestreCombo, genererBtn);
        
        vbox.getChildren().addAll(title, controls);
        
        return vbox;
    }
    
    private VBox createNotesPane() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        
        Label title = new Label("Saisie des Notes");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Interface de saisie des notes pour enseignants
        ComboBox<Classe> classeCombo = new ComboBox<>();
        // Charger les classes de l'enseignant connecté via ClasseDAO
        try {
            com.gestion.gestionscolaire.dao.ClasseDAO classeDAO = new com.gestion.gestionscolaire.dao.ClasseDAO();
            classeCombo.setItems(FXCollections.observableArrayList(
                classeDAO.getClassesByEnseignant(AuthService.getUtilisateurConnecte().getId())));
        } catch (Exception ex) {
            classeCombo.setItems(FXCollections.observableArrayList(gestionService.getAllClasses()));
        }
        classeCombo.setPromptText("Classe");
        
        ComboBox<Cours> coursCombo = new ComboBox<>();
        coursCombo.setPromptText("Cours");
        
        ComboBox<Integer> trimestreCombo = new ComboBox<>();
        trimestreCombo.setItems(FXCollections.observableArrayList(1, 2, 3));
        trimestreCombo.setPromptText("Trimestre");
        
        ComboBox<Note.TypeEvaluation> typeCombo = new ComboBox<>();
        typeCombo.setItems(FXCollections.observableArrayList(Note.TypeEvaluation.values()));
        typeCombo.setPromptText("Type d'évaluation");
        
        // Charger les cours quand une classe est sélectionnée
        classeCombo.setOnAction(e -> {
            if (classeCombo.getValue() != null) {
                com.gestion.gestionscolaire.dao.CoursDAO coursDAO = new com.gestion.gestionscolaire.dao.CoursDAO();
                coursCombo.setItems(FXCollections.observableArrayList(
                    coursDAO.getCoursByClasseAndEnseignant(classeCombo.getValue().getId(), 
                                                          AuthService.getUtilisateurConnecte().getId())));
            }
        });
        
        Button chargerBtn = new Button("Charger");
        chargerBtn.setOnAction(e -> {
            if (classeCombo.getValue() != null && coursCombo.getValue() != null && 
                trimestreCombo.getValue() != null && typeCombo.getValue() != null) {
                chargerInterfaceSaisie(vbox, classeCombo.getValue(), coursCombo.getValue(), 
                                     trimestreCombo.getValue(), typeCombo.getValue());
            } else {
                showError("Erreur", "Veuillez remplir tous les champs");
            }
        });
        
        VBox controlsBox = new VBox(10);
        HBox ligne1 = new HBox(10);
        ligne1.getChildren().addAll(new Label("Classe:"), classeCombo, new Label("Cours:"), coursCombo);
        HBox ligne2 = new HBox(10);
        ligne2.getChildren().addAll(new Label("Trimestre:"), trimestreCombo, new Label("Type:"), typeCombo, chargerBtn);
        controlsBox.getChildren().addAll(ligne1, ligne2);
        
        vbox.getChildren().addAll(title, controlsBox);
        
        return vbox;
    }
    
    private VBox createMesClassesPane() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        
        Label title = new Label("Mes Classes");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Afficher les classes de l'enseignant
        ListView<Classe> classesList = new ListView<>();
        try {
            com.gestion.gestionscolaire.dao.ClasseDAO classeDAO = new com.gestion.gestionscolaire.dao.ClasseDAO();
            classesList.setItems(FXCollections.observableArrayList(
                classeDAO.getClassesByEnseignant(AuthService.getUtilisateurConnecte().getId())));
        } catch (Exception ex) {
            showError("Erreur", "Impossible de charger les classes");
        }
        
        classesList.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2 && classesList.getSelectionModel().getSelectedItem() != null) {
                Classe classe = classesList.getSelectionModel().getSelectedItem();
                afficherElevesClasse(vbox, classe);
            }
        });
        
        vbox.getChildren().addAll(title, new Label("Double-cliquez sur une classe pour voir les élèves:"), classesList);
        
        return vbox;
    }
    
    // Méthodes utilitaires pour l'affichage
    private void refreshElevesTable(TableView<Eleve> table) {
        table.setItems(FXCollections.observableArrayList(gestionService.getAllEleves()));
    }
    
    private void refreshEnseignantsTable(TableView<Utilisateur> table) {
        table.setItems(FXCollections.observableArrayList(gestionService.getAllEnseignants()));
    }
    
    private void clearEleveForm(TextField nomField, TextField prenomField, DatePicker datePicker, ComboBox<Classe> classeCombo) {
        nomField.clear();
        prenomField.clear();
        datePicker.setValue(null);
        classeCombo.setValue(null);
    }
    
    private void clearEnseignantForm(TextField nomField, TextField prenomField, TextField emailField, PasswordField passwordField) {
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        passwordField.clear();
        passwordField.setPromptText("Mot de passe");
    }
    
    // Méthode d'actualisation des données
    private void actualiserDonnees() {
        try {
            // Actualiser les tables si elles existent
            if (classesTable != null) {
                classesTable.setItems(FXCollections.observableArrayList(gestionService.getAllClasses()));
            }
            if (elevesTable != null) {
                elevesTable.setItems(FXCollections.observableArrayList(gestionService.getAllEleves()));
            }
            if (coursTable != null) {
                coursTable.setItems(FXCollections.observableArrayList(gestionService.getAllCours()));
            }
            if (enseignantsTable != null) {
                enseignantsTable.setItems(FXCollections.observableArrayList(gestionService.getAllEnseignants()));
            }
            
            // Actualiser l'onglet actuel si c'est un TabPane
            if (currentTabPane != null) {
                Tab selectedTab = currentTabPane.getSelectionModel().getSelectedItem();
                if (selectedTab != null) {
                    // Force refresh de l'onglet sélectionné
                    actualiserOngletActuel(selectedTab.getText());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur d'actualisation", "Impossible d'actualiser certaines données : " + e.getMessage());
        }
    }
    
    private void actualiserOngletActuel(String ongletNom) {
        // Actualisation spécifique selon l'onglet actuel
        switch (ongletNom.toLowerCase()) {
            case "classes":
                if (classesTable != null) {
                    classesTable.setItems(FXCollections.observableArrayList(gestionService.getAllClasses()));
                }
                break;
            case "élèves":
                if (elevesTable != null) {
                    elevesTable.setItems(FXCollections.observableArrayList(gestionService.getAllEleves()));
                }
                break;
            case "cours":
                if (coursTable != null) {
                    coursTable.setItems(FXCollections.observableArrayList(gestionService.getAllCours()));
                }
                break;
            case "enseignants":
                if (enseignantsTable != null) {
                    enseignantsTable.setItems(FXCollections.observableArrayList(gestionService.getAllEnseignants()));
                }
                break;
        }
    }
    
    // Créer un bouton d'actualisation local pour chaque onglet
    private Button createRefreshButton(String type) {
        Button refreshBtn = new Button("🔄");
        refreshBtn.setTooltip(new Tooltip("Actualiser les données"));
        refreshBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; " +
                           "-fx-font-weight: bold; -fx-background-radius: 3; " +
                           "-fx-padding: 6 10 6 10; -fx-cursor: hand;");
        
        refreshBtn.setOnAction(e -> {
            switch (type.toLowerCase()) {
                case "classes":
                    if (classesTable != null) {
                        classesTable.setItems(FXCollections.observableArrayList(gestionService.getAllClasses()));
                        showInfo("Actualisation", "Liste des classes actualisée !");
                    }
                    break;
                case "eleves":
                    if (elevesTable != null) {
                        elevesTable.setItems(FXCollections.observableArrayList(gestionService.getAllEleves()));
                        showInfo("Actualisation", "Liste des élèves actualisée !");
                    }
                    break;
                case "cours":
                    if (coursTable != null) {
                        coursTable.setItems(FXCollections.observableArrayList(gestionService.getAllCours()));
                        showInfo("Actualisation", "Liste des cours actualisée !");
                    }
                    break;
                case "enseignants":
                    if (enseignantsTable != null) {
                        enseignantsTable.setItems(FXCollections.observableArrayList(gestionService.getAllEnseignants()));
                        showInfo("Actualisation", "Liste des enseignants actualisée !");
                    }
                    break;
            }
        });
        
        return refreshBtn;
    }
    
    private void afficherAnonymats(VBox parent, int classeId, int trimestre) {
        List<Anonymat> anonymats = gestionService.getAnonymats(classeId, trimestre);
        
        TableView<Anonymat> table = new TableView<>();
        table.setItems(FXCollections.observableArrayList(anonymats));
        
        TableColumn<Anonymat, String> codeCol = new TableColumn<>("Code Anonymat");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("codeAnonymat"));
        
        TableColumn<Anonymat, String> eleveCol = new TableColumn<>("Élève");
        eleveCol.setCellValueFactory(new PropertyValueFactory<>("eleveNom"));
        
        table.getColumns().addAll(codeCol, eleveCol);
        
        if (parent.getChildren().size() > 2) {
            parent.getChildren().subList(2, parent.getChildren().size()).clear();
        }
        parent.getChildren().addAll(new Label("Anonymats générés:"), table);
    }
    
    private void genererBulletins(VBox parent, Classe classe, int trimestre) {
        java.time.LocalDate now = java.time.LocalDate.now();
        int annee = now.getMonthValue() >= 9 ? now.getYear() : now.getYear() - 1;
        String anneeScolaire = annee + "-" + (annee + 1);
        List<BulletinService.ResultatEleve> resultats = bulletinService.genererBulletinClasse(classe.getId(), trimestre, anneeScolaire);
        
        TableView<BulletinService.ResultatEleve> table = new TableView<>();
        table.setItems(FXCollections.observableArrayList(resultats));
        
        TableColumn<BulletinService.ResultatEleve, String> eleveCol = new TableColumn<>("Élève");
        eleveCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEleve().getNomComplet()));
        
        TableColumn<BulletinService.ResultatEleve, Double> moyenneCol = new TableColumn<>("Moyenne");
        moyenneCol.setCellValueFactory(new PropertyValueFactory<>("moyenneGenerale"));
        
        TableColumn<BulletinService.ResultatEleve, String> mentionCol = new TableColumn<>("Mention");
        mentionCol.setCellValueFactory(new PropertyValueFactory<>("mention"));
        
        TableColumn<BulletinService.ResultatEleve, Integer> rangCol = new TableColumn<>("Rang");
        rangCol.setCellValueFactory(new PropertyValueFactory<>("rang"));
        
        table.getColumns().addAll(eleveCol, moyenneCol, mentionCol, rangCol);
        
        if (parent.getChildren().size() > 2) {
            parent.getChildren().subList(2, parent.getChildren().size()).clear();
        }
        parent.getChildren().addAll(new Label("Bulletins - " + classe.getNom() + " - Trimestre " + trimestre + ":"), table);
    }
    
    private void chargerInterfaceSaisie(VBox parent, Classe classe, Cours cours, int trimestre, Note.TypeEvaluation type) {
        // Effacer l'interface précédente
        if (parent.getChildren().size() > 2) {
            parent.getChildren().subList(2, parent.getChildren().size()).clear();
        }
        
        List<Eleve> eleves = gestionService.getElevesByClasse(classe.getId());
        
        VBox saisieBox = new VBox(10);
        Label infoLabel = new Label("Saisie pour: " + classe.getNom() + " - " + cours.getNom() + 
                                   " - Trimestre " + trimestre + " - " + type);
        infoLabel.setStyle("-fx-font-weight: bold;");
        saisieBox.getChildren().add(infoLabel);
        
        // Pour les examens, afficher les anonymats
        if (type == Note.TypeEvaluation.EXAMEN) {
            List<Anonymat> anonymats = gestionService.getAnonymats(classe.getId(), trimestre);
            if (anonymats.isEmpty()) {
                Label warningLabel = new Label("Aucun anonymat généré pour ce trimestre. Demandez à l'administrateur de les générer.");
                warningLabel.setStyle("-fx-text-fill: red;");
                saisieBox.getChildren().add(warningLabel);
                parent.getChildren().add(saisieBox);
                return;
            }
            
            for (Anonymat anonymat : anonymats) {
                HBox eleveBox = new HBox(10);
                eleveBox.setAlignment(Pos.CENTER_LEFT);
                
                Label anonymatLabel = new Label("Anonymat: " + anonymat.getCodeAnonymat());
                anonymatLabel.setPrefWidth(200);
                
                TextField noteField = new TextField();
                noteField.setPromptText("Note /20");
                noteField.setPrefWidth(80);
                noteField.setUserData(anonymat.getEleveId()); // Stocker l'ID de l'élève
                
                eleveBox.getChildren().addAll(anonymatLabel, noteField);
                saisieBox.getChildren().add(eleveBox);
            }
        } else {
            // Pour le contrôle continu, afficher les noms
            for (Eleve eleve : eleves) {
                HBox eleveBox = new HBox(10);
                eleveBox.setAlignment(Pos.CENTER_LEFT);
                
                Label eleveLabel = new Label(eleve.getNomComplet());
                eleveLabel.setPrefWidth(200);
                
                TextField noteField = new TextField();
                noteField.setPromptText("Note /20");
                noteField.setPrefWidth(80);
                noteField.setUserData(eleve.getId()); // Stocker l'ID de l'élève
                
                eleveBox.getChildren().addAll(eleveLabel, noteField);
                saisieBox.getChildren().add(eleveBox);
            }
        }
        
        Button sauvegarderBtn = new Button("Sauvegarder les notes");
        sauvegarderBtn.setOnAction(e -> {
            int notesSauvegardees = 0;
            for (int i = 1; i < saisieBox.getChildren().size() - 1; i++) { // Skip title and button
                HBox eleveBox = (HBox) saisieBox.getChildren().get(i);
                TextField noteField = (TextField) eleveBox.getChildren().get(1);
                
                if (!noteField.getText().isEmpty()) {
                    try {
                        double note = Double.parseDouble(noteField.getText());
                        if (note >= 0 && note <= 20) {
                            int eleveId = (Integer) noteField.getUserData();
                            if (gestionService.sauvegarderNote(eleveId, cours.getId(), trimestre, type, note)) {
                                notesSauvegardees++;
                            }
                        } else {
                            showError("Erreur", "Les notes doivent être entre 0 et 20");
                            return;
                        }
                    } catch (NumberFormatException ex) {
                        showError("Erreur", "Format de note invalide: " + noteField.getText());
                        return;
                    }
                }
            }
            showInfo("Succès", notesSauvegardees + " notes sauvegardées");
        });
        
        saisieBox.getChildren().add(sauvegarderBtn);
        parent.getChildren().add(saisieBox);
    }
    
    private void afficherElevesClasse(VBox parent, Classe classe) {
        if (parent.getChildren().size() > 3) {
            parent.getChildren().subList(3, parent.getChildren().size()).clear();
        }
        
        List<Eleve> eleves = gestionService.getElevesByClasse(classe.getId());
        
        Label titre = new Label("Élèves de la classe " + classe.getNom() + ":");
        titre.setStyle("-fx-font-weight: bold;");
        
        ListView<Eleve> elevesList = new ListView<>();
        elevesList.setItems(FXCollections.observableArrayList(eleves));
        elevesList.setPrefHeight(200);
        
        parent.getChildren().addAll(titre, elevesList);
    }
    
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}