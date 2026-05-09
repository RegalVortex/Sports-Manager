package com.sportsmanager.ui.scenes;

import com.sportsmanager.core.ICoach;
import com.sportsmanager.core.IPlayer;
import com.sportsmanager.core.ITeam;
import com.sportsmanager.ui.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class SquadScene {

    private static final String BG      = "#0D0D0D";
    private static final String CARD    = "#1E2332";
    private static final String CARD2   = "#252A3D";
    private static final String GOLD    = "#F0A500";
    private static final String TEXT    = "#FFFFFF";
    private static final String SUBTEXT = "#8A8A9A";
    private static final String GREEN   = "#4CAF50";
    private static final String RED     = "#F44336";
    private static final int    MAX_W   = 600;

    private static String activeTab = "SQUAD";

    public static Scene create() {
        SceneManager sm = SceneManager.getInstance();
        ITeam team = sm.getCurrentPlayerTeam();

        StackPane outer = new StackPane();
        outer.setStyle("-fx-background-color: " + BG + ";");

        BorderPane content = new BorderPane();
        content.setStyle("-fx-background-color: " + BG + ";");
        content.setMaxWidth(MAX_W);
        content.setTop(createTopBar(team));

        VBox body = new VBox(0);
        body.getChildren().add(createSubTabBar());

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + BG + "; -fx-background-color: " + BG + "; -fx-border-color: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        if ("SQUAD".equals(activeTab)) {
            scroll.setContent(createSquadContent(team));
        } else {
            scroll.setContent(createTacticsContent(team));
        }

        body.getChildren().add(scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        content.setCenter(body);

        StackPane.setAlignment(content, Pos.TOP_CENTER);
        outer.getChildren().add(content);

        return new Scene(outer, 480, 850);
    }

    private static HBox createTopBar(ITeam team) {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: " + CARD + ";");
        bar.setPadding(new Insets(14, 16, 14, 16));

        Label back = new Label("←");
        back.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        back.setTextFill(Color.web(GOLD));
        back.setStyle("-fx-cursor: hand;");
        back.setOnMouseClicked(e -> {
            activeTab = "SQUAD";
            SceneManager.getInstance().navigateTo("dashboard");
        });

        Label title = new Label(team != null ? team.getName().toUpperCase() : "SQUAD");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        title.setTextFill(Color.web(TEXT));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        int ovr = team != null ? team.getTeamOverallRating() : 0;
        VBox ovrBox = new VBox(0);
        ovrBox.setAlignment(Pos.CENTER);
        Label ovrVal = new Label(String.valueOf(ovr));
        ovrVal.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        ovrVal.setTextFill(Color.web(GOLD));
        Label ovrLbl = new Label("OVR");
        ovrLbl.setFont(Font.font("Arial", 10));
        ovrLbl.setTextFill(Color.web(SUBTEXT));
        ovrBox.getChildren().addAll(ovrVal, ovrLbl);

        bar.getChildren().addAll(back, title, spacer, ovrBox);
        return bar;
    }

    private static HBox createSubTabBar() {
        HBox bar = new HBox(0);
        bar.setStyle("-fx-background-color: " + CARD + ";");
        bar.setMaxWidth(Double.MAX_VALUE);

        String[] tabs = {"SQUAD", "TACTICS"};
        for (String tab : tabs) {
            VBox cell = new VBox(0);
            cell.setAlignment(Pos.CENTER);
            HBox.setHgrow(cell, Priority.ALWAYS);
            cell.setPadding(new Insets(12, 0, 0, 0));
            cell.setStyle("-fx-cursor: hand;");

            boolean active = tab.equals(activeTab);
            Label lbl = new Label(tab);
            lbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            lbl.setTextFill(Color.web(active ? GOLD : SUBTEXT));

            Region underline = new Region();
            underline.setPrefHeight(2);
            underline.setMaxWidth(Double.MAX_VALUE);
            underline.setStyle("-fx-background-color: " + (active ? GOLD : "transparent") + ";");
            VBox.setMargin(underline, new Insets(10, 0, 0, 0));

            cell.getChildren().addAll(lbl, underline);
            cell.setOnMouseClicked(e -> {
                activeTab = tab;
                SceneManager.getInstance().navigateTo("squad");
            });
            bar.getChildren().add(cell);
        }

        VBox wrap = new VBox(0);
        wrap.getChildren().add(bar);
        Region sep = new Region();
        sep.setPrefHeight(1);
        sep.setStyle("-fx-background-color: #2A3050;");
        wrap.getChildren().add(sep);

        HBox outer = new HBox(wrap);
        HBox.setHgrow(wrap, Priority.ALWAYS);
        return outer;
    }

    private static VBox createSquadContent(ITeam team) {
        VBox vbox = new VBox(0);
        vbox.setStyle("-fx-background-color: " + BG + ";");
        if (team == null) return vbox;

        if (team.getCoach() != null) vbox.getChildren().add(createCoachCard(team.getCoach()));

        List<IPlayer> lineup = team.getStartingLineup();
        List<IPlayer> squad  = team.getSquad();

        if (!lineup.isEmpty()) {
            vbox.getChildren().add(sectionHeader("STARTING LINEUP (" + lineup.size() + ")"));
            for (IPlayer p : lineup) vbox.getChildren().add(createPlayerCard(p, true));
        }

        vbox.getChildren().add(sectionHeader("BENCH"));
        boolean hasBench = false;
        for (IPlayer p : squad) {
            if (!lineup.contains(p)) {
                vbox.getChildren().add(createPlayerCard(p, false));
                hasBench = true;
            }
        }
        if (!hasBench) {
            Label none = new Label("Yedek oyuncu yok.");
            none.setTextFill(Color.web(SUBTEXT));
            none.setFont(Font.font("Arial", 13));
            none.setPadding(new Insets(12, 20, 12, 20));
            vbox.getChildren().add(none);
        }

        Region pad = new Region(); pad.setPrefHeight(30);
        vbox.getChildren().add(pad);
        return vbox;
    }

    private static HBox createCoachCard(ICoach coach) {
        HBox card = new HBox(14);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: " + CARD2 + ";");
        card.setPadding(new Insets(12, 16, 12, 16));

        StackPane icon = new StackPane();
        Circle circle = new Circle(22);
        circle.setFill(Color.web("#2D3450"));
        Label iconLbl = new Label("🎓");
        iconLbl.setFont(Font.font(18));
        icon.getChildren().addAll(circle, iconLbl);

        VBox info = new VBox(3);
        Label nameLbl = new Label(coach.getName());
        nameLbl.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        nameLbl.setTextFill(Color.web(TEXT));

        HBox meta = new HBox(10);
        Label roleLbl = new Label("ANTRENÖR");
        roleLbl.setFont(Font.font("Arial", 11));
        roleLbl.setTextFill(Color.web(SUBTEXT));
        String spec = coach.getSpecialty() != null ? coach.getSpecialty() : "BALANCED";
        Label specLbl = new Label(spec);
        specLbl.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        specLbl.setTextFill(Color.web(GOLD));
        specLbl.setPadding(new Insets(1, 6, 1, 6));
        specLbl.setStyle("-fx-background-color: #2D3450; -fx-background-radius: 4;");
        meta.getChildren().addAll(roleLbl, specLbl);
        info.getChildren().addAll(nameLbl, meta);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox qualBox = new VBox(2);
        qualBox.setAlignment(Pos.CENTER_RIGHT);
        int q = coach.getQuality();
        Label qVal = new Label(q + "/10");
        qVal.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        qVal.setTextFill(Color.web(q >= 8 ? GOLD : q >= 5 ? GREEN : SUBTEXT));
        Label qLbl = new Label("KALİTE");
        qLbl.setFont(Font.font("Arial", 9));
        qLbl.setTextFill(Color.web(SUBTEXT));
        qualBox.getChildren().addAll(qVal, qLbl);

        card.getChildren().addAll(icon, info, spacer, qualBox);

        VBox wrap = new VBox(0, card);
        Region sep = new Region(); sep.setPrefHeight(1);
        sep.setStyle("-fx-background-color: #2A3050;");
        wrap.getChildren().add(sep);
        HBox outer = new HBox(wrap);
        HBox.setHgrow(wrap, Priority.ALWAYS);
        return outer;
    }

    private static HBox sectionHeader(String text) {
        HBox h = new HBox();
        h.setAlignment(Pos.CENTER_LEFT);
        h.setPadding(new Insets(14, 16, 6, 16));
        h.setStyle("-fx-background-color: " + BG + ";");
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        lbl.setTextFill(Color.web(SUBTEXT));
        h.getChildren().add(lbl);
        return h;
    }

    private static HBox createPlayerCard(IPlayer p, boolean isStarter) {
        HBox card = new HBox(12);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: " + (isStarter ? CARD : BG) + ";");
        card.setPadding(new Insets(12, 16, 12, 16));

        StackPane posBadge = new StackPane();
        Rectangle rect = new Rectangle(38, 28);
        rect.setArcWidth(6); rect.setArcHeight(6);
        rect.setFill(Color.web(posColor(p.getPosition())));
        Label posLbl = new Label(shortPos(p.getPosition()));
        posLbl.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        posLbl.setTextFill(Color.WHITE);
        posBadge.getChildren().addAll(rect, posLbl);

        VBox info = new VBox(3);
        Label nameLbl = new Label(p.getName());
        nameLbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        nameLbl.setTextFill(Color.web(p.isInjured() ? RED : TEXT));

        HBox meta = new HBox(8);
        meta.setAlignment(Pos.CENTER_LEFT);
        Label ageLbl = new Label("Yaş " + p.getAge());
        ageLbl.setFont(Font.font("Arial", 11));
        ageLbl.setTextFill(Color.web(SUBTEXT));

        if (p.isInjured()) {
            Label injLbl = new Label("⚕ SAKATI");
            injLbl.setFont(Font.font("Arial", FontWeight.BOLD, 10));
            injLbl.setTextFill(Color.web(RED));
            injLbl.setPadding(new Insets(1, 5, 1, 5));
            injLbl.setStyle("-fx-background-color: #3D1A1A; -fx-background-radius: 4;");
            meta.getChildren().addAll(ageLbl, injLbl);
        } else {
            int form = p.getForm();
            Circle dot = new Circle(5);
            dot.setFill(Color.web(formColor(form)));
            Label formLbl = new Label(formText(form));
            formLbl.setFont(Font.font("Arial", 10));
            formLbl.setTextFill(Color.web(formColor(form)));
            meta.getChildren().addAll(ageLbl, dot, formLbl);
        }
        info.getChildren().addAll(nameLbl, meta);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox ovrBox = new VBox(2);
        ovrBox.setAlignment(Pos.CENTER_RIGHT);
        int ovr = p.getOverallRating();
        Label ovrVal = new Label(String.valueOf(ovr));
        ovrVal.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        ovrVal.setTextFill(Color.web(ovrColor(ovr)));
        Label potLbl = new Label("↑" + p.getPotential());
        potLbl.setFont(Font.font("Arial", 10));
        potLbl.setTextFill(Color.web(p.getPotential() > ovr ? GREEN : SUBTEXT));
        ovrBox.getChildren().addAll(ovrVal, potLbl);

        card.getChildren().addAll(posBadge, info, spacer, ovrBox);

        VBox wrap = new VBox(0, card);
        Region sep = new Region(); sep.setPrefHeight(1);
        sep.setStyle("-fx-background-color: #1A1F30;");
        wrap.getChildren().add(sep);
        HBox outer = new HBox(wrap);
        HBox.setHgrow(wrap, Priority.ALWAYS);
        return outer;
    }

    private static VBox createTacticsContent(ITeam team) {
        VBox vbox = new VBox(14);
        vbox.setStyle("-fx-background-color: " + BG + ";");
        vbox.setPadding(new Insets(16));
        if (team == null) return vbox;

        VBox formCard = new VBox(12);
        formCard.setStyle("-fx-background-color: " + CARD + "; -fx-background-radius: 10;");
        formCard.setPadding(new Insets(16));
        Label formTitle = new Label("FORMASYON");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        formTitle.setTextFill(Color.web(SUBTEXT));
        String sport = SceneManager.getInstance().getSelectedSport();
        String formation = (sport != null && sport.equalsIgnoreCase("volleyball")) ? "6-2 Rotasyon" : "4-3-3";
        Label formVal = new Label(formation);
        formVal.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        formVal.setTextFill(Color.web(GOLD));
        Label formSub = new Label("Dengeli yaklaşım — atak ve savunma eşit.");
        formSub.setFont(Font.font("Arial", 12));
        formSub.setTextFill(Color.web(SUBTEXT));
        formSub.setWrapText(true);
        formCard.getChildren().addAll(formTitle, formVal, formSub);

        VBox trainCard = new VBox(12);
        trainCard.setStyle("-fx-background-color: " + CARD + "; -fx-background-radius: 10;");
        trainCard.setPadding(new Insets(16));
        Label trainTitle = new Label("ANTRENMAN ODAĞI");
        trainTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        trainTitle.setTextFill(Color.web(SUBTEXT));
        String spec = team.getCoach() != null ? team.getCoach().getSpecialty() : "BALANCED";
        Label trainVal = new Label(spec);
        trainVal.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        trainVal.setTextFill(Color.web(TEXT));
        Label trainSub = new Label("Antrenörün uzmanlığına göre belirlenir.");
        trainSub.setFont(Font.font("Arial", 11));
        trainSub.setTextFill(Color.web(SUBTEXT));
        trainSub.setWrapText(true);
        trainCard.getChildren().addAll(trainTitle, trainVal, trainSub);

        vbox.getChildren().addAll(formCard, trainCard);
        Region pad = new Region(); pad.setPrefHeight(30);
        vbox.getChildren().add(pad);
        return vbox;
    }

    private static String posColor(String pos) {
        if (pos == null) return "#607D8B";
        switch (pos.toUpperCase()) {
            case "GK": return "#5C6BC0";
            case "CB": case "LB": case "RB": case "DEF": return "#1565C0";
            case "CDM": case "CM": case "CAM": case "MID": return "#2E7D32";
            case "LW": case "RW": case "ST": case "CF": case "FWD": return "#C62828";
            case "MB": case "OH": case "S": case "L": case "LS": case "RS": return "#E65100";
            default: return "#607D8B";
        }
    }
    private static String shortPos(String pos) {
        if (pos == null) return "?";
        return pos.length() <= 3 ? pos.toUpperCase() : pos.substring(0, 3).toUpperCase();
    }
    private static String ovrColor(int o) {
        return o >= 80 ? GOLD : o >= 70 ? GREEN : o >= 60 ? "#2196F3" : SUBTEXT;
    }
    private static String formColor(int f) {
        switch (f) {
            case 3: return GOLD;
            case 2: return GREEN;
            case 1: return "#FF9800";
            default: return RED;
        }
    }
    private static String formText(int f) {
        switch (f) {
            case 3: return "Mükemmel";
            case 2: return "İyi";
            case 1: return "Normal";
            default: return "Kötü";
        }
    }
}