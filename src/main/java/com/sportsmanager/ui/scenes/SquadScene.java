package com.sportsmanager.ui.scenes;

import com.sportsmanager.core.ICoach;
import com.sportsmanager.core.IPlayer;
import com.sportsmanager.core.ITeam;
import com.sportsmanager.sport.football.FootballTactic;
import com.sportsmanager.sport.volleyball.VolleyballTactic;
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
import java.util.Map;

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

    private static String activeTab = "KADRO";

    // ── Ana sahne ────────────────────────────────────────────────────────────
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
        body.setMaxHeight(Double.MAX_VALUE);
        body.getChildren().add(createSubTabBar());

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setMaxHeight(Double.MAX_VALUE);
        scroll.setStyle("-fx-background: " + BG + "; -fx-background-color: " + BG +
                        "; -fx-border-color: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        if ("KADRO".equals(activeTab)) {
            scroll.setContent(createSquadContent(team));
        } else {
            scroll.setContent(createTacticsContent(team));
        }

        body.getChildren().add(scroll);
        content.setCenter(body);
        content.setMaxHeight(Double.MAX_VALUE);
        outer.getChildren().add(content);

        return new Scene(outer, 480, 850);
    }

    // ── Üst bar ──────────────────────────────────────────────────────────────
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
            activeTab = "KADRO";
            SceneManager.getInstance().navigateTo("dashboard");
        });

        Label title = new Label(team != null ? team.getName().toUpperCase() : "KADRO");
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

    // ── Alt sekme çubuğu ─────────────────────────────────────────────────────
    private static HBox createSubTabBar() {
        HBox bar = new HBox(0);
        bar.setStyle("-fx-background-color: " + CARD + ";");
        bar.setMaxWidth(Double.MAX_VALUE);

        String[] tabs = {"KADRO", "TAKTİK"};
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

    // ── Kadro içeriği ─────────────────────────────────────────────────────────
    private static VBox createSquadContent(ITeam team) {
        VBox vbox = new VBox(0);
        vbox.setStyle("-fx-background-color: " + BG + ";");
        if (team == null) return vbox;

        if (team.getCoach() != null) vbox.getChildren().add(createCoachCard(team.getCoach()));

        List<IPlayer> lineup = team.getStartingLineup();
        List<IPlayer> squad  = team.getSquad();

        if (!lineup.isEmpty()) {
            vbox.getChildren().add(sectionHeader("İLK 11  (" + lineup.size() + " oyuncu)"));
            for (IPlayer p : lineup) vbox.getChildren().add(createPlayerCard(p, true));
        }

        vbox.getChildren().add(sectionHeader("YEDEKLER"));
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

    // ── Antrenör kartı ───────────────────────────────────────────────────────
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
        String spec = coach.getSpecialty() != null ? specialtyTr(coach.getSpecialty()) : "DENGELI";
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

    // ── Bölüm başlığı ────────────────────────────────────────────────────────
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

    // ── Oyuncu kartı (nitelik çubukları dahil) ───────────────────────────────
    private static HBox createPlayerCard(IPlayer p, boolean isStarter) {
        HBox card = new HBox(12);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: " + (isStarter ? CARD : BG) + ";");
        card.setPadding(new Insets(10, 16, 10, 16));

        // Pozisyon rozeti
        StackPane posBadge = new StackPane();
        Rectangle rect = new Rectangle(40, 28);
        rect.setArcWidth(6); rect.setArcHeight(6);
        rect.setFill(Color.web(posColor(p.getPosition())));
        Label posLbl = new Label(shortPos(p.getPosition()));
        posLbl.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        posLbl.setTextFill(Color.WHITE);
        posBadge.getChildren().addAll(rect, posLbl);

        // İsim + meta + nitelik çubukları
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

        // Nitelik mini-çubukları
        HBox attrBars = buildAttrBars(p);

        info.getChildren().addAll(nameLbl, meta, attrBars);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // OVR + Potansiyel
        VBox ovrBox = new VBox(2);
        ovrBox.setAlignment(Pos.CENTER_RIGHT);
        int ovr = p.getOverallRating();
        Label ovrVal = new Label(String.valueOf(ovr));
        ovrVal.setFont(Font.font("Arial", FontWeight.BOLD, 20));
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

    // ── Nitelik mini-çubukları ────────────────────────────────────────────────
    private static HBox buildAttrBars(IPlayer p) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(2, 0, 0, 0));

        Map<String, Integer> attrs = p.getAttributes();
        if (attrs == null || attrs.isEmpty()) return row;

        String[] keys = topAttrKeys(p.getPosition(), attrs);
        for (String key : keys) {
            int val = attrs.getOrDefault(key, 50);
            row.getChildren().add(miniBar(key, val));
        }
        return row;
    }

    private static HBox miniBar(String key, int val) {
        HBox bar = new HBox(4);
        bar.setAlignment(Pos.CENTER_LEFT);

        Label keyLbl = new Label(key.substring(0, Math.min(3, key.length())).toUpperCase());
        keyLbl.setFont(Font.font("Arial", 9));
        keyLbl.setTextFill(Color.web(SUBTEXT));
        keyLbl.setPrefWidth(26);

        // Çubuk arka plan
        StackPane barPane = new StackPane();
        barPane.setAlignment(Pos.CENTER_LEFT);

        Rectangle bg = new Rectangle(52, 5);
        bg.setArcWidth(3); bg.setArcHeight(3);
        bg.setFill(Color.web("#2A3050"));

        Rectangle fill = new Rectangle(Math.max(2, (int)(52 * val / 99.0)), 5);
        fill.setArcWidth(3); fill.setArcHeight(3);
        fill.setFill(Color.web(barColor(val)));
        StackPane.setAlignment(fill, Pos.CENTER_LEFT);

        barPane.getChildren().addAll(bg, fill);

        Label valLbl = new Label(String.valueOf(val));
        valLbl.setFont(Font.font("Arial", FontWeight.BOLD, 9));
        valLbl.setTextFill(Color.web(barColor(val)));
        valLbl.setPrefWidth(20);

        bar.getChildren().addAll(keyLbl, barPane, valLbl);
        return bar;
    }

    private static String[] topAttrKeys(String pos, Map<String, Integer> attrs) {
        // Pozisyona göre en önemli 3 niteliği döndür
        if (pos == null) return attrs.keySet().stream().limit(3).toArray(String[]::new);
        switch (pos.toUpperCase()) {
            case "GK":  return new String[]{"defending", "heading", "stamina"};
            case "CB":  return new String[]{"defending", "heading", "pace"};
            case "LB": case "RB": return new String[]{"pace", "defending", "passing"};
            case "CDM": case "CM": return new String[]{"passing", "defending", "stamina"};
            case "CAM": return new String[]{"passing", "shooting", "pace"};
            case "LW": case "RW": return new String[]{"pace", "shooting", "passing"};
            case "ST": case "CF": return new String[]{"shooting", "heading", "pace"};
            // Voleybol
            case "SETTER": return new String[]{"set", "receive", "stamina"};
            case "LIBERO": return new String[]{"receive", "stamina", "serve"};
            case "MIDDLE_BLOCKER": return new String[]{"block", "spike", "stamina"};
            case "OUTSIDE_HITTER": return new String[]{"spike", "receive", "serve"};
            case "OPPOSITE": return new String[]{"spike", "serve", "block"};
            default:
                return attrs.keySet().stream().limit(3).toArray(String[]::new);
        }
    }

    private static String barColor(int v) {
        if (v >= 80) return GOLD;
        if (v >= 68) return GREEN;
        if (v >= 55) return "#2196F3";
        return SUBTEXT;
    }

    // ── Taktik içeriği ───────────────────────────────────────────────────────
    private static VBox createTacticsContent(ITeam team) {
        VBox vbox = new VBox(14);
        vbox.setStyle("-fx-background-color: " + BG + ";");
        vbox.setPadding(new Insets(16));
        if (team == null) return vbox;

        String currentTactic = team.getTactic() != null ? team.getTactic().getName() : "";
        String sport = SceneManager.getInstance().getSelectedSport();
        boolean isFootball = sport == null || !sport.equalsIgnoreCase("volleyball");

        // Formasyon seçimi
        VBox formCard = new VBox(10);
        formCard.setStyle("-fx-background-color: " + CARD + "; -fx-background-radius: 10;");
        formCard.setPadding(new Insets(16));

        Label formTitle = new Label(isFootball ? "FORMASYON SEÇ" : "TAKTİK SEÇ");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        formTitle.setTextFill(Color.web(SUBTEXT));
        formCard.getChildren().add(formTitle);

        if (isFootball) {
            Object[][] options = {
                {"4-4-2", "Dengeli — klasik dört-dört-iki düzeni"},
                {"4-3-3", "Saldırı ağırlıklı — üçlü hücum hattı"},
                {"5-3-2", "Savunma ağırlıklı — beşli savunma hattı"}
            };
            for (Object[] opt : options) {
                final String tName = (String) opt[0];
                final String tDesc = (String) opt[1];
                boolean active = tName.equals(currentTactic);
                HBox row = createTacticOption(tName, tDesc, active, () -> {
                    team.setTactic(new FootballTactic(tName));
                    SceneManager.getInstance().navigateTo("squad");
                });
                formCard.getChildren().add(row);
            }
        } else {
            Object[][] options = {
                {"OFFENSIVE", "Saldırı ağırlıklı — güçlü servis ve smaç"},
                {"BALANCED",  "Dengeli — genel dengeli yaklaşım"},
                {"DEFENSIVE", "Savunma ağırlıklı — güçlü blok ve ribaund"}
            };
            for (Object[] opt : options) {
                final String tName = (String) opt[0];
                final String tDesc = (String) opt[1];
                boolean active = tName.equalsIgnoreCase(currentTactic);
                HBox row = createTacticOption(tName, tDesc, active, () -> {
                    team.setTactic(new VolleyballTactic(tName));
                    SceneManager.getInstance().navigateTo("squad");
                });
                formCard.getChildren().add(row);
            }
        }
        vbox.getChildren().add(formCard);

        // Mevcut taktik özeti
        VBox summaryCard = new VBox(10);
        summaryCard.setStyle("-fx-background-color: " + CARD + "; -fx-background-radius: 10;");
        summaryCard.setPadding(new Insets(16));
        Label sumTitle = new Label("AKTİF TAKTİK");
        sumTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        sumTitle.setTextFill(Color.web(SUBTEXT));
        Label sumVal = new Label(currentTactic.isEmpty() ? "—" : currentTactic);
        sumVal.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        sumVal.setTextFill(Color.web(GOLD));

        double atk = team.getTactic() != null ? team.getTactic().getAttackModifier() : 1.0;
        double def = team.getTactic() != null ? team.getTactic().getDefenseModifier() : 1.0;

        HBox modsRow = new HBox(16);
        modsRow.setAlignment(Pos.CENTER_LEFT);
        modsRow.getChildren().addAll(
            modChip("⚔ Atak", atk),
            modChip("🛡 Savunma", def)
        );
        summaryCard.getChildren().addAll(sumTitle, sumVal, modsRow);
        vbox.getChildren().add(summaryCard);

        // Antrenman odağı
        if (team.getCoach() != null) {
            VBox trainCard = new VBox(8);
            trainCard.setStyle("-fx-background-color: " + CARD + "; -fx-background-radius: 10;");
            trainCard.setPadding(new Insets(16));
            Label trainTitle = new Label("ANTRENMAN ODAĞI");
            trainTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            trainTitle.setTextFill(Color.web(SUBTEXT));
            String spec = team.getCoach().getSpecialty() != null
                    ? specialtyTr(team.getCoach().getSpecialty()) : "DENGELI";
            Label trainVal = new Label(spec);
            trainVal.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            trainVal.setTextFill(Color.web(TEXT));
            Label trainSub = new Label("Antrenörünüzün uzmanlığına göre oyuncular her hafta gelişir.");
            trainSub.setFont(Font.font("Arial", 11));
            trainSub.setTextFill(Color.web(SUBTEXT));
            trainSub.setWrapText(true);
            trainCard.getChildren().addAll(trainTitle, trainVal, trainSub);
            vbox.getChildren().add(trainCard);
        }

        Region pad = new Region(); pad.setPrefHeight(30);
        vbox.getChildren().add(pad);
        return vbox;
    }

    private static HBox createTacticOption(String name, String desc, boolean active, Runnable onSelect) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 14, 12, 14));
        row.setStyle("-fx-background-color: " + (active ? "#1A2040" : CARD2) +
                     "; -fx-background-radius: 8; -fx-cursor: hand;");

        Label checkLbl = new Label(active ? "✓" : "○");
        checkLbl.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        checkLbl.setTextFill(Color.web(active ? GOLD : SUBTEXT));
        checkLbl.setPrefWidth(22);

        VBox info = new VBox(3);
        Label nameLbl = new Label(name);
        nameLbl.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        nameLbl.setTextFill(Color.web(active ? GOLD : TEXT));
        Label descLbl = new Label(desc);
        descLbl.setFont(Font.font("Arial", 11));
        descLbl.setTextFill(Color.web(SUBTEXT));
        descLbl.setWrapText(true);
        info.getChildren().addAll(nameLbl, descLbl);

        row.getChildren().addAll(checkLbl, info);
        row.setOnMouseClicked(e -> onSelect.run());
        row.setOnMouseEntered(e -> {
            if (!active) row.setStyle("-fx-background-color: #252B3D; -fx-background-radius: 8; -fx-cursor: hand;");
        });
        row.setOnMouseExited(e -> {
            row.setStyle("-fx-background-color: " + (active ? "#1A2040" : CARD2) +
                         "; -fx-background-radius: 8; -fx-cursor: hand;");
        });
        return row;
    }

    private static HBox modChip(String label, double mod) {
        HBox chip = new HBox(6);
        chip.setAlignment(Pos.CENTER_LEFT);
        chip.setPadding(new Insets(6, 12, 6, 12));
        String col = mod > 1.0 ? GREEN : mod < 1.0 ? RED : SUBTEXT;
        chip.setStyle("-fx-background-color: " + col + "22; -fx-background-radius: 6;");
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Arial", 12));
        lbl.setTextFill(Color.web(col));
        String sign = mod > 1.0 ? "+" : mod < 1.0 ? "−" : "=";
        Label valLbl = new Label(sign + Math.round(Math.abs(mod - 1.0) * 100) + "%");
        valLbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        valLbl.setTextFill(Color.web(col));
        chip.getChildren().addAll(lbl, valLbl);
        return chip;
    }

    // ── Yardımcı metodlar ────────────────────────────────────────────────────
    private static String posColor(String pos) {
        if (pos == null) return "#607D8B";
        switch (pos.toUpperCase()) {
            case "GK": return "#5C6BC0";
            case "CB": case "LB": case "RB": case "DEF": return "#1565C0";
            case "CDM": case "CM": case "CAM": case "MID": return "#2E7D32";
            case "LW": case "RW": case "ST": case "CF": case "FWD": return "#C62828";
            case "MIDDLE_BLOCKER": return "#7B1FA2";
            case "OUTSIDE_HITTER": return "#C62828";
            case "SETTER": return "#2E7D32";
            case "OPPOSITE": return "#E65100";
            case "LIBERO": return "#00838F";
            default: return "#607D8B";
        }
    }

    private static String shortPos(String pos) {
        if (pos == null) return "?";
        switch (pos.toUpperCase()) {
            case "MIDDLE_BLOCKER":  return "MB";
            case "OUTSIDE_HITTER":  return "OH";
            case "SETTER":          return "SET";
            case "OPPOSITE":        return "OPP";
            case "LIBERO":          return "LIB";
            default:
                return pos.length() <= 3 ? pos.toUpperCase() : pos.substring(0, 3).toUpperCase();
        }
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

    private static String specialtyTr(String spec) {
        if (spec == null) return "DENGELI";
        switch (spec.toUpperCase()) {
            case "ATTACKING": case "OFFENSIVE": return "SALDIRI";
            case "DEFENSIVE": return "SAVUNMA";
            case "FITNESS":   return "KONDISYON";
            default:          return "DENGELI";
        }
    }
}
