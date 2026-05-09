package com.sportsmanager.ui.scenes;

import com.sportsmanager.ui.SceneManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class MainMenuScene {

    public static Scene create() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #0D0D0D;");
        root.setPadding(new Insets(60, 30, 40, 30));
        root.setMaxWidth(600);

        // Logo alanı
        Label logo = new Label("SPORTS\nMANAGER");
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 42));
        logo.setTextFill(Color.web("#F0A500"));
        logo.setAlignment(Pos.CENTER);
        logo.setStyle("-fx-text-alignment: center;");

        Label subtitle = new Label("Championship Edition");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.web("#8A8A9A"));

        VBox logoBox = new VBox(8, logo, subtitle);
        logoBox.setAlignment(Pos.CENTER);
        VBox.setMargin(logoBox, new Insets(0, 0, 40, 0));

        // Menü butonları
        VBox menuBox = new VBox(15);
        menuBox.setAlignment(Pos.CENTER);

        HBox newGameBtn = createMenuButton("⚽  Yeni Oyun", "Yeni bir kariyer başlat", true);
        HBox loadGameBtn = createMenuButton("💾  Oyunu Yükle", "Kayıtlı oyuna devam et", false);
        HBox exitBtn = createMenuButton("✕  Çıkış", "Oyundan çık", false);

        newGameBtn.setOnMouseClicked(e ->
                SceneManager.getInstance().navigateTo("sportselect")
        );

        loadGameBtn.setOnMouseClicked(e -> showComingSoon(root, loadGameBtn));

        exitBtn.setOnMouseClicked(e ->
                javafx.application.Platform.exit()
        );

        menuBox.getChildren().addAll(newGameBtn, loadGameBtn, exitBtn);

        // Alt yazı
        Label footer = new Label("© 2025 RegalVortex");
        footer.setFont(Font.font("Arial", 12));
        footer.setTextFill(Color.web("#444455"));
        VBox.setMargin(footer, new Insets(40, 0, 0, 0));

        root.getChildren().addAll(logoBox, menuBox, footer);

        StackPane outer = new StackPane(root);
        outer.setStyle("-fx-background-color: #0D0D0D;");
        StackPane.setAlignment(root, Pos.CENTER);

        return new Scene(outer, 480, 850);
    }

    // ── Geçici "Yakında" bildirimi ─────────────────────────────────────────────
    private static void showComingSoon(VBox root, Node anchor) {
        // Zaten gösteriliyorsa ikinci kez ekleme
        if (root.getChildren().stream().anyMatch(n -> "toast".equals(n.getUserData()))) return;

        HBox toast = new HBox();
        toast.setAlignment(Pos.CENTER);
        toast.setPadding(new Insets(12, 20, 12, 20));
        toast.setStyle("-fx-background-color: #252A3D; -fx-background-radius: 10;");
        toast.setMaxWidth(380);
        toast.setUserData("toast");

        Label msg = new Label("💾  Bu özellik yakında geliyor!");
        msg.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        msg.setTextFill(Color.web("#F0A500"));
        toast.getChildren().add(msg);

        // Butonun altına ekle
        int anchorIndex = root.getChildren().indexOf(anchor.getParent());
        int insertIndex = anchorIndex >= 0 ? anchorIndex + 1 : root.getChildren().size();
        root.getChildren().add(insertIndex, toast);

        // 2.5 saniye sonra kaldır
        Timeline tl = new Timeline(new KeyFrame(Duration.seconds(2.5),
                ev -> root.getChildren().remove(toast)));
        tl.play();
    }

    private static HBox createMenuButton(String title, String subtitle, boolean highlighted) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(18, 20, 18, 20));
        card.setStyle(
                "-fx-background-color: #1E2332;" +
                        "-fx-background-radius: 12;" +
                        "-fx-cursor: hand;"
        );
        card.setMaxWidth(380);
        card.setMinWidth(380);

        VBox textBox = new VBox(4);
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setTextFill(highlighted ? Color.web("#F0A500") : Color.WHITE);

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setFont(Font.font("Arial", 12));
        subtitleLabel.setTextFill(Color.web("#8A8A9A"));

        textBox.getChildren().addAll(titleLabel, subtitleLabel);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        Label arrow = new Label("›");
        arrow.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        arrow.setTextFill(Color.web("#F0A500"));

        card.getChildren().addAll(textBox, arrow);

        // Hover efekti
        card.setOnMouseEntered(e ->
                card.setStyle(
                        "-fx-background-color: #252B3D;" +
                                "-fx-background-radius: 12;" +
                                "-fx-cursor: hand;"
                )
        );
        card.setOnMouseExited(e ->
                card.setStyle(
                        "-fx-background-color: #1E2332;" +
                                "-fx-background-radius: 12;" +
                                "-fx-cursor: hand;"
                )
        );

        return card;
    }
}
