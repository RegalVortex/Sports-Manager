package com.sportsmanager.ui.modern;

import com.sportsmanager.core.AbstractTeam;
import com.sportsmanager.core.AbstractMatch;
import com.sportsmanager.core.GameContext;
import com.sportsmanager.core.ICoach;
import com.sportsmanager.core.ILeague;
import com.sportsmanager.core.IMatch;
import com.sportsmanager.core.IPlayer;
import com.sportsmanager.core.ISport;
import com.sportsmanager.core.ITactic;
import com.sportsmanager.core.ITeam;
import com.sportsmanager.core.LineupWarnings;
import com.sportsmanager.core.MatchResult;
import com.sportsmanager.core.SportFactory;
import com.sportsmanager.core.SportRegistry;
import com.sportsmanager.save.GameSaveData;
import com.sportsmanager.save.LoadedGame;
import com.sportsmanager.save.SaveLoadService;
import com.sportsmanager.setup.GameSetupService;
import com.sportsmanager.setup.LeaguePreset;
import com.sportsmanager.setup.PresetData;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * A fresh responsive JavaFX shell for the project.
 *
 * It keeps the existing game domain intact and replaces the old fixed mobile-like
 * screens with one adaptive desktop interface.
 */
public class ModernSportsManagerUi {

    private static final String BG = "#F4F7FB";
    private static final String SURFACE = "#FFFFFF";
    private static final String INK = "#1E2A32";
    private static final String MUTED = "#6B7785";
    private static final String LINE = "#DCE3EA";
    private static final String SIDEBAR = "#101820";
    private static final String TEAL = "#00A896";
    private static final String CORAL = "#F26457";
    private static final String AMBER = "#FFB703";
    private static final String BLUE = "#2D6CDF";
    private static final String GREEN = "#2E7D55";
    private static final String RED = "#B83B4A";

    private static final String[] SLOT_FILES = {
        "save_slot1.dat", "save_slot2.dat", "save_slot3.dat"
    };

    private final SportRegistry registry = new SportRegistry();
    private final GameSetupService setupService = new GameSetupService(registry);
    private final Map<String, GameSetupService.SetupResult> setupPreviewCache = new HashMap<>();

    private Stage stage;
    private BorderPane shell;
    private VBox sideNav;
    private FlowPane compactNav;
    private ScrollPane scroll;
    private VBox page;
    private ScreenKey currentScreen = ScreenKey.HOME;
    private HomeMode homeMode = HomeMode.MENU;
    private String draftSport;
    private LeaguePreset draftLeague;
    private String draftTeam;
    private CoachProfile draftCoachProfile;
    private String toast;
    private MatchReport lastReport;
    private TrainingReport lastTrainingReport;
    private MatchFlow matchFlow;
    private boolean showMatchLineup;
    private boolean exitConfirmed;

    private enum ScreenKey {
        HOME, DASHBOARD, MATCH, SQUAD, LINEUP, TACTICS, LEAGUE, SEASON, FIXTURES, NEWS, SAVE
    }

    private enum HomeMode {
        MENU, SPORT, LEAGUE, TEAM, COACH, STYLE, LOAD
    }

    private enum PlayStyle {
        DENGELI, HUCUM, DEFANS
    }

    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        stage.setTitle("Sports Manager - Menajer Masasi");
        stage.setMinWidth(760);
        stage.setMinHeight(560);

        shell = new BorderPane();
        shell.setStyle("-fx-background-color: " + BG + ";");

        sideNav = createSideNav();
        compactNav = createCompactNav();

        page = new VBox(18);
        page.setPadding(new Insets(28));
        page.setFillWidth(true);
        page.setStyle("-fx-background-color: " + BG + ";");

        scroll = new ScrollPane(page);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background: " + BG + "; -fx-background-color: " + BG + "; -fx-border-color: transparent;");

        shell.setLeft(sideNav);
        shell.setTop(compactNav);
        shell.setCenter(scroll);

        Scene scene = new Scene(shell, 1180, 760);
        scene.widthProperty().addListener((obs, oldValue, newValue) -> updateResponsiveMode(newValue.doubleValue()));
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            if (!exitConfirmed && !confirmExitWithSave()) {
                event.consume();
            } else {
                exitConfirmed = true;
            }
        });
        updateResponsiveMode(scene.getWidth());
        render();
        stage.show();
    }

    private void updateResponsiveMode(double width) {
        boolean compact = width < 900;
        sideNav.setVisible(!compact);
        sideNav.setManaged(!compact);
        compactNav.setVisible(compact);
        compactNav.setManaged(compact);
        page.setPadding(new Insets(compact ? 16 : 28));
    }

    private void render() {
        page.getChildren().clear();
        rebuildNavigation();

        if (toast != null && !toast.isBlank()) {
            page.getChildren().add(banner(toast));
            toast = null;
        }

        switch (currentScreen) {
            case DASHBOARD:
                renderDashboard();
                break;
            case MATCH:
                renderMatchCenter();
                break;
            case SQUAD:
                renderSquad();
                break;
            case LINEUP:
                renderLineup();
                break;
            case TACTICS:
                renderTactics();
                break;
            case LEAGUE:
                renderLeague();
                break;
            case SEASON:
                renderSeasonSummary();
                break;
            case FIXTURES:
                renderFixtures();
                break;
            case NEWS:
                renderNews();
                break;
            case SAVE:
                renderSaveLoad();
                break;
            default:
                renderHome();
                break;
        }
    }

    private void renderHome() {
        page.getChildren().add(hero(
            "Sports Manager",
            "Yeni kariyer kur, kayitli oyunu yukle ve sezonu tek bir modern menajer masasindan yonet."
        ));

        switch (homeMode) {
            case SPORT:
                renderSportStep();
                break;
            case LEAGUE:
                renderLeagueStep();
                break;
            case TEAM:
                renderTeamStep();
                break;
            case COACH:
                renderCoachStep();
                break;
            case STYLE:
                renderStyleStep();
                break;
            case LOAD:
                renderLoadStep();
                break;
            default:
                renderStartMenu();
                break;
        }
    }

    private void renderStartMenu() {
        TilePane tile = tilePane(280);
        tile.getChildren().add(menuCard("Yeni Oyun", "Spor, lig, takim ve oyun anlayisini adim adim sec.", () -> {
            draftSport = null;
            draftLeague = null;
            draftTeam = null;
            draftCoachProfile = null;
            homeMode = HomeMode.SPORT;
            render();
        }));
        tile.getChildren().add(menuCard("Oyunu Yukle", "Kayitli kariyer slotunu geri yukle.", () -> {
            homeMode = HomeMode.LOAD;
            render();
        }));
        tile.getChildren().add(menuCard("Cikis", "Sports Manager uygulamasini kapat.", this::requestExit));
        page.getChildren().add(tile);
    }

    private void renderSportStep() {
        page.getChildren().add(stepHeader("Adim 1 / 4", "Spor sec"));
        TilePane tile = tilePane(260);
        List<String> sports = new ArrayList<>(registry.getAvailableSports());
        sports.sort(Comparator.naturalOrder());
        for (String sport : sports) {
            tile.getChildren().add(menuCard(turkishSport(sport), turkishSport(sport) + " kariyeri kur.", () -> {
                draftSport = sport;
                draftLeague = null;
                draftTeam = null;
                draftCoachProfile = null;
                homeMode = HomeMode.LEAGUE;
                render();
            }));
        }
        page.getChildren().add(tile);
        page.getChildren().add(backToMenuButton());
    }

    private void renderLeagueStep() {
        page.getChildren().add(stepHeader("Adim 2 / 4", "Lig sec"));
        TilePane tile = tilePane(300);
        for (LeaguePreset preset : PresetData.getLeaguesForSport(draftSport)) {
            tile.getChildren().add(menuCard(preset.getLeagueName(),
                preset.getTeamNames().size() + " takim secilebilir", () -> {
                    draftLeague = preset;
                    draftTeam = null;
                    draftCoachProfile = null;
                    homeMode = HomeMode.TEAM;
                    render();
                }));
        }
        page.getChildren().add(tile);
        page.getChildren().add(backStepButton("Spor Secimine Don", HomeMode.SPORT));
    }

    private void renderTeamStep() {
        page.getChildren().add(stepHeader("Adim 3 / 4", "Takim sec"));
        TilePane tile = tilePane(240);
        if (draftLeague != null) {
            for (String teamName : draftLeague.getTeamNames()) {
                TeamPreview preview = previewTeam(draftSport, draftLeague, teamName);
                String summary = preview == null
                    ? teamName + " ile kariyere basla."
                    : "Guc " + preview.overall + " - Oneri: " + playStyleLabel(preview.recommendedStyle);
                tile.getChildren().add(menuCard(teamName, summary, () -> {
                    draftTeam = teamName;
                    draftCoachProfile = null;
                    if (preview != null) {
                        // A good default: clicking the team carries the suggested style into the final choice screen.
                        toast = teamName + " icin onerilen anlayis: " + playStyleLabel(preview.recommendedStyle) + ".";
                    }
                    homeMode = HomeMode.COACH;
                    render();
                }));
            }
        }
        page.getChildren().add(tile);
        page.getChildren().add(backStepButton("Lig Secimine Don", HomeMode.LEAGUE));
    }

    private void renderCoachStep() {
        page.getChildren().add(stepHeader("Adim 4 / 5", "Antrenor profilini sec"));
        TeamPreview preview = previewTeam(draftSport, draftLeague, draftTeam);
        TilePane tile = tilePane(280);
        for (CoachProfile profile : coachProfiles(draftSport)) {
            VBox box = card(profile.name);
            boolean recommended = preview != null && profileMatchesRecommendation(profile, preview.recommendedStyle);
            box.getChildren().addAll(
                metricRow("Uzmanlik", coachSpecialtyLabel(profile.specialty)),
                metricRow("Kalite", profile.quality + "/10"),
                progress("Egitim Etkisi", profile.quality * 10, profileEffectText(draftSport, profile), recommended ? TEAL : BLUE),
                recommended ? good("Bu kadro icin asistan onerisi") : muted(profile.description)
            );
            Button choose = primaryButton("Bu Antrenoru Sec");
            choose.setOnAction(e -> {
                draftCoachProfile = profile;
                homeMode = HomeMode.STYLE;
                render();
            });
            box.getChildren().add(choose);
            tile.getChildren().add(box);
        }
        page.getChildren().add(tile);
        page.getChildren().add(backStepButton("Takim Secimine Don", HomeMode.TEAM));
    }

    private void renderStyleStep() {
        page.getChildren().add(stepHeader("Adim 5 / 5", "Oyun anlayisini sec"));
        if (draftCoachProfile != null) {
            page.getChildren().add(banner("Secilen antrenor: " + draftCoachProfile.name
                + " - " + coachSpecialtyLabel(draftCoachProfile.specialty)
                + " - kalite " + draftCoachProfile.quality + "/10"));
        }
        TilePane tile = tilePane(280);
        tile.getChildren().add(menuCard("Dengeli", "Skor ve savunma arasinda kontrollu plan.", () -> startCareer(draftSport, draftLeague, draftTeam, draftCoachProfile, PlayStyle.DENGELI)));
        tile.getChildren().add(menuCard("Hucum", "Daha fazla risk, daha fazla gol arayisi.", () -> startCareer(draftSport, draftLeague, draftTeam, draftCoachProfile, PlayStyle.HUCUM)));
        tile.getChildren().add(menuCard("Defans", "Once guvenlik, dusuk riskli oyun plani.", () -> startCareer(draftSport, draftLeague, draftTeam, draftCoachProfile, PlayStyle.DEFANS)));
        page.getChildren().add(tile);
        page.getChildren().add(backStepButton("Antrenor Secimine Don", HomeMode.COACH));
    }

    private void renderLoadStep() {
        page.getChildren().add(stepHeader("Oyunu Yukle", "Yerel kayit slotlarindan birini sec"));
        TilePane tile = tilePane(300);
        for (int i = 0; i < SLOT_FILES.length; i++) {
            int slot = i + 1;
            SaveSummary summary = readSaveSummary(SLOT_FILES[i]);
            VBox box = card("Slot " + slot);
            box.getChildren().add(summary == null
                ? muted("Bos slot")
                : muted(summary.team + " - " + summary.league + " - Sezon " + summary.season + ", Hafta " + summary.week));
            Button load = primaryButton(summary == null ? "Kayit Yok" : "Slotu Yukle");
            load.setDisable(summary == null);
            load.setMaxWidth(Double.MAX_VALUE);
            load.setOnAction(e -> loadSlot(slot));
            box.getChildren().add(load);
            tile.getChildren().add(box);
        }
        page.getChildren().add(tile);
        page.getChildren().add(backToMenuButton());
    }

    private void startCareer(String sportName, LeaguePreset preset, String teamName, CoachProfile coachProfile, PlayStyle style) {
        if (sportName == null || preset == null || teamName == null || coachProfile == null) {
            toast = "Once spor, lig, takim ve antrenor secmelisin.";
            render();
            return;
        }

        GameSetupService.SetupResult result = setupPreviewResult(sportName, preset, teamName);
        setupPreviewCache.clear();
        SportFactory factory = registry.getFactory(sportName);
        GameContext ctx = GameContext.getInstance();
        ctx.startNewGame(result.getSport());
        ctx.setLeague(result.getLeague());
        ctx.setPlayerTeam(result.getPlayerTeam());
        ctx.setSportFactory(factory);
        result.getPlayerTeam().setCoach(new ProfileCoach(coachProfile, sportName));
        applyPlayStyle(result.getPlayerTeam(), factory, style);
        lastReport = null;
        lastTrainingReport = null;
        matchFlow = null;
        showMatchLineup = false;
        homeMode = HomeMode.MENU;
        toast = result.getPlayerTeam().getName() + " ile kariyer basladi. Antrenor: "
            + coachProfile.name + ". Oyun anlayisi: " + playStyleLabel(style) + ".";
        currentScreen = ScreenKey.DASHBOARD;
        render();
    }

    private void renderDashboard() {
        GameContext ctx = GameContext.getInstance();
        if (!hasGame()) {
            renderEmptyState();
            return;
        }

        ILeague league = ctx.getLeague();
        ITeam team = ctx.getPlayerTeam();
        ISport sport = ctx.getSport();

        page.getChildren().add(titleBlock(
            "Menajer Masasi",
            team.getName() + " - " + league.getName() + " - Sezon " + league.getCurrentSeason() + ", Hafta " + league.getCurrentWeek()
        ));

        TilePane stats = tilePane(180);
        stats.getChildren().addAll(
            statCard("Spor", turkishSport(sport.getSportName()), TEAL),
            statCard("Sira", rankText(league, team), BLUE),
            statCard("Puan", String.valueOf(team.getPoints()), AMBER),
            statCard("Takim Gucu", String.valueOf(team.getTeamOverallRating()), GREEN),
            statCard("Karne", league.getWins(team) + "G " + league.getDraws(team) + "B " + league.getLosses(team) + "M", CORAL),
            statCard("Taktik", team.getTactic() == null ? "Yok" : turkishTactic(team.getTactic().getName()), TEAL)
        );
        page.getChildren().add(stats);

        TilePane main = tilePane(360);
        main.getChildren().add(nextMatchCard(league, team));
        main.getChildren().add(readinessCard(team));
        main.getChildren().add(formCard(league, team));
        main.getChildren().add(teamComparisonCard(league, team));
        main.getChildren().add(healthCenterCard(team));
        main.getChildren().add(assistantAdviceCard(league, team));
        page.getChildren().add(main);

        if (lastTrainingReport != null) {
            page.getChildren().add(trainingReportCard(lastTrainingReport));
        }

        if (lastReport != null) {
            page.getChildren().add(matchReportCard(lastReport));
        }

        VBox actions = card("Mac Gunu Aksiyonlari");
        FlowPane actionRow = new FlowPane(10, 10);
        Button play = primaryButton(league.isSeasonOver() ? "Yeni Sezona Basla" : "Mac Merkezini Ac");
        play.setOnAction(e -> {
            if (league.isSeasonOver()) {
                league.resetSeason();
                ctx.clearNews();
                ctx.addNews("Sezon " + league.getCurrentSeason() + " basladi.");
                lastReport = null;
                lastTrainingReport = null;
                matchFlow = null;
                toast = "Sezon " + league.getCurrentSeason() + " basladi.";
                render();
            } else {
                currentScreen = ScreenKey.MATCH;
                render();
            }
        });
        actionRow.getChildren().addAll(play, navAction("Kadro", ScreenKey.LINEUP), navAction("Oyuncular", ScreenKey.SQUAD),
            navAction("Taktikler", ScreenKey.TACTICS),
            navAction("Lig Tablosu", ScreenKey.LEAGUE), navAction("Sezon Ozeti", ScreenKey.SEASON),
            navAction("Kaydet / Yukle", ScreenKey.SAVE));
        actions.getChildren().add(actionRow);
        page.getChildren().add(actions);

        VBox news = card("Son Haberler");
        List<String> recent = ctx.getRecentNews(5);
        if (recent.isEmpty()) {
            news.getChildren().add(muted("Henuz haber yok. Haber uretmek icin bir hafta oyna."));
        } else {
            for (int i = recent.size() - 1; i >= 0; i--) {
                news.getChildren().add(rowLabel(recent.get(i)));
            }
        }
        page.getChildren().add(news);
    }

    private VBox nextMatchCard(ILeague league, ITeam team) {
        VBox box = card("Siradaki Mac");
        IMatch match = findPlayerMatch(league, team, league.getCurrentWeek());
        if (match == null || league.isSeasonOver()) {
            box.getChildren().add(muted(league.isSeasonOver() ? "Sezon tamamlandi." : "Bu hafta mac yok."));
            return box;
        }
        ITeam opponent = match.getHomeTeam().equals(team) ? match.getAwayTeam() : match.getHomeTeam();
        int difficulty = fixtureDifficulty(match, team);
        box.getChildren().addAll(
            bigText(match.getHomeTeam().getName() + " vs " + match.getAwayTeam().getName()),
            metricRow("Saha", match.getHomeTeam().equals(team) ? "Ev" : "Deplasman"),
            metricRow("Rakip", opponent.getName()),
            metricRow("Rakip Gucu", String.valueOf(opponent.getTeamOverallRating())),
            progress("Mac Zorlugu", difficulty, difficultyLabel(difficulty), difficultyColor(difficulty))
        );
        return box;
    }

    private VBox assistantAdviceCard(ILeague league, ITeam team) {
        VBox box = card("Asistan Onerileri");
        List<String> suggestions = assistantSuggestions(league, team);
        if (suggestions.isEmpty()) {
            box.getChildren().add(good("Takim dengeli gorunuyor. Mac merkezinden kadroyu son kez kontrol edebilirsin."));
        } else {
            for (String suggestion : suggestions) {
                box.getChildren().add(rowLabel(suggestion));
            }
        }
        ICoach coach = team.getCoach();
        if (coach != null) {
            box.getChildren().add(new Separator());
            box.getChildren().add(metricRow("Antrenor", coach.getName()));
            box.getChildren().add(metricRow("Odak", coachSpecialtyLabel(coach.getSpecialty())));
        }
        return box;
    }

    private VBox readinessCard(ITeam team) {
        VBox box = card("Takim Hazirligi");
        box.getChildren().addAll(
            progress("Moral", morale(team), moraleText(morale(team)), TEAL),
            progress("Saglik", health(team), injuredCount(team) + " sakat", GREEN),
            metricRow("Ilk Kadro", team.getStartingLineup().size() + "/" + LineupWarnings.expectedLineupSize(team))
        );
        List<String> warnings = LineupWarnings.check(team);
        if (!warnings.isEmpty()) {
            box.getChildren().add(new Separator());
            for (String warning : warnings) {
                Label label = muted(turkishWarning(warning));
                label.setTextFill(javafx.scene.paint.Color.web(RED));
                box.getChildren().add(label);
            }
        }
        return box;
    }

    private VBox formCard(ILeague league, ITeam team) {
        VBox box = card("Son Form");
        List<String> markers = new ArrayList<>();
        for (IMatch match : league.getAllFixtures()) {
            MatchResult result = match.getResult();
            if (result == null || (!result.getHomeTeam().equals(team) && !result.getAwayTeam().equals(team))) {
                continue;
            }
            if (result.getWinner() == null) {
                markers.add("D");
            } else if (result.getWinner().equals(team)) {
                markers.add("W");
            } else {
                markers.add("L");
            }
        }
        HBox row = new HBox(8);
        int start = Math.max(0, markers.size() - 5);
        if (markers.isEmpty()) {
            box.getChildren().add(muted("Henuz mac oynanmadi."));
        } else {
            for (int i = start; i < markers.size(); i++) {
                row.getChildren().add(formPill(markers.get(i)));
            }
            box.getChildren().add(row);
        }

        IMatch match = findPlayerMatch(league, team, league.getCurrentWeek());
        if (match != null && !league.isSeasonOver()) {
            ITeam opponent = match.getHomeTeam().equals(team) ? match.getAwayTeam() : match.getHomeTeam();
            int danger = Math.max(0, Math.min(100, 50 + opponent.getTeamOverallRating() - team.getTeamOverallRating()));
            box.getChildren().add(progress("Rakip tehlikesi", danger, opponent.getName(), danger >= 65 ? CORAL : AMBER));
        }
        return box;
    }

    private List<String> assistantSuggestions(ILeague league, ITeam team) {
        List<String> suggestions = new ArrayList<>();
        List<String> warnings = LineupWarnings.check(team);
        if (!warnings.isEmpty()) {
            suggestions.add("Kadro uyarisi: " + turkishWarning(warnings.get(0)));
        }
        if (injuredCount(team) > 0) {
            suggestions.add("Sakat oyuncular var. Mac oncesi otomatik kadro duzeltmeyi dene.");
        }
        IMatch match = findPlayerMatch(league, team, league.getCurrentWeek());
        if (match != null && !league.isSeasonOver()) {
            ITeam opponent = match.getHomeTeam().equals(team) ? match.getAwayTeam() : match.getHomeTeam();
            int difficulty = fixtureDifficulty(match, team);
            if (difficulty >= 70) {
                suggestions.add(opponent.getName() + " maci zor gorunuyor. Daha dengeli veya defansif plan mantikli.");
            } else if (difficulty <= 40) {
                suggestions.add("Rakip gucu dusuk. Hucum planiyla erken skor arayabilirsin.");
            }
        }
        if (team.getCoach() != null) {
            suggestions.add("Haftalik antrenman odagi: " + coachSpecialtyLabel(team.getCoach().getSpecialty()) + ".");
        }
        if (suggestions.size() > 3) {
            return new ArrayList<>(suggestions.subList(0, 3));
        }
        return suggestions;
    }

    private int fixtureDifficulty(IMatch match, ITeam playerTeam) {
        if (match == null || playerTeam == null) {
            return -1;
        }
        boolean home = match.getHomeTeam().equals(playerTeam);
        boolean away = match.getAwayTeam().equals(playerTeam);
        if (!home && !away) {
            return -1;
        }
        ITeam opponent = home ? match.getAwayTeam() : match.getHomeTeam();
        int base = 50 + opponent.getTeamOverallRating() - playerTeam.getTeamOverallRating();
        if (away) {
            base += 8;
        } else {
            base -= 5;
        }
        return Math.max(10, Math.min(95, base));
    }

    private String difficultyLabel(int difficulty) {
        if (difficulty < 0) {
            return "-";
        }
        if (difficulty >= 75) {
            return "Cok zor";
        }
        if (difficulty >= 60) {
            return "Zor";
        }
        if (difficulty >= 40) {
            return "Dengeli";
        }
        return "Avantajli";
    }

    private String difficultyColor(int difficulty) {
        if (difficulty >= 75) {
            return RED;
        }
        if (difficulty >= 60) {
            return CORAL;
        }
        if (difficulty >= 40) {
            return AMBER;
        }
        return GREEN;
    }

    private void renderMatchCenter() {
        if (!hasGame()) {
            renderEmptyState();
            return;
        }

        GameContext ctx = GameContext.getInstance();
        ILeague league = ctx.getLeague();
        ITeam team = ctx.getPlayerTeam();
        page.getChildren().add(titleBlock("Mac Merkezi",
            "Mac basi kadroyu kontrol et, periyotlar arasinda degisiklik yap ve haftayi spora gore simule et."));

        if (matchFlow != null && matchFlow.complete) {
            renderPostMatchBuffer(ctx);
            return;
        }

        if (league.isSeasonOver()) {
            VBox season = card("Sezon Tamamlandi");
            ITeam champion = league.getChampion();
            season.getChildren().add(bigText("Sampiyon: " + (champion == null ? "Bilinmiyor" : champion.getName())));
            Button next = primaryButton("Yeni Sezona Basla");
            next.setOnAction(e -> {
                league.resetSeason();
                ctx.clearNews();
                lastReport = null;
                lastTrainingReport = null;
                matchFlow = null;
                toast = "Sezon " + league.getCurrentSeason() + " basladi.";
                render();
            });
            season.getChildren().add(next);
            page.getChildren().add(season);
            return;
        }

        ensureMatchFlow(ctx);

        TilePane preview = tilePane(360);
        preview.getChildren().add(nextMatchCard(league, team));
        preview.getChildren().add(readinessCard(team));
        preview.getChildren().add(teamComparisonCard(league, team));
        preview.getChildren().add(assistantAdviceCard(league, team));
        page.getChildren().add(preview);

        VBox lineup = card(matchFlow.currentPeriod == 0 ? "Mac Basi Kadro" : "Periyot Arasi Kadro");
        lineup.getChildren().add(muted("Istersen simule etmeden once veya periyot aralarinda oyuncu degisikligi yapabilirsin."));
        FlowPane lineupActions = new FlowPane(10, 10);
        Button toggleLineup = ghostButton(showMatchLineup ? "Kadro Panelini Gizle" : "Kadroyu Gor / Degistir");
        toggleLineup.setOnAction(e -> {
            showMatchLineup = !showMatchLineup;
            render();
        });
        Button lineupScreen = ghostButton("Kadro Ekranina Git");
        lineupScreen.setOnAction(e -> {
            currentScreen = ScreenKey.LINEUP;
            render();
        });
        Button quickBest = ghostButton("En Iyi Kadroyu Kur");
        quickBest.setOnAction(e -> {
            if (team instanceof AbstractTeam) {
                ((AbstractTeam) team).setStartingLineup(bestLineup(team));
                toast = "En iyi kadro kuruldu.";
            }
            render();
        });
        lineupActions.getChildren().addAll(toggleLineup, lineupScreen, quickBest);
        lineup.getChildren().add(lineupActions);
        if (showMatchLineup) {
            addInlineSubstitutionControls(lineup, team);
        } else {
            lineup.getChildren().add(compactLineupSummary(team));
        }
        page.getChildren().add(lineup);

        VBox fixtures = card("Bu Hafta");
        for (IMatch match : league.getFixturesForWeek(league.getCurrentWeek())) {
            fixtures.getChildren().add(rowLabel(match.getHomeTeam().getName() + " vs " + match.getAwayTeam().getName()));
        }
        page.getChildren().add(fixtures);

        VBox sim = card("Periyot Simulasyonu");
        sim.getChildren().add(metricRow("Spor", turkishSport(ctx.getSport().getSportName())));
        sim.getChildren().add(metricRow("Periyot", periodLabel(ctx.getSport(), matchFlow.currentPeriod + 1)
            + " / " + ctx.getSport().getNumberOfPeriods()));
        Button play = primaryButton(nextPeriodButtonText(ctx.getSport()));
        play.setDisable(matchFlow.complete);
        play.setOnAction(e -> simulateNextPeriod(ctx));
        sim.getChildren().add(play);
        page.getChildren().add(sim);

        if (!matchFlow.notes.isEmpty()) {
            page.getChildren().add(timelineCard("Canli Olay Akisi", matchFlow.notes));
        }

        if (lastReport != null) {
            page.getChildren().add(titleBlock("Son Mac Raporu", "Onceki haftanin mac ozeti"));
            page.getChildren().add(matchReportCard(lastReport));
        }
    }

    private void ensureMatchFlow(GameContext ctx) {
        int week = ctx.getLeague().getCurrentWeek();
        int periods = ctx.getSport().getNumberOfPeriods();
        if (matchFlow != null && matchFlow.complete) {
            return;
        }
        if (matchFlow == null || matchFlow.week != week || matchFlow.totalPeriods != periods) {
            matchFlow = new MatchFlow(week, periods);
        }
    }

    private void simulateNextPeriod(GameContext ctx) {
        ensureMatchFlow(ctx);
        ISport sport = ctx.getSport();
        int next = matchFlow.currentPeriod + 1;
        List<String> periodNotes = periodActionNotes(ctx, next);
        boolean finalPeriod = next >= matchFlow.totalPeriods || isPlannedMatchComplete(ctx);

        matchFlow.currentPeriod = next;
        if (!finalPeriod) {
            matchFlow.notes.add(periodLabel(sport, next) + " tamamlandi. Degisiklik yapmak icin kadro panelini kullanabilirsin.");
            matchFlow.notes.addAll(periodNotes);
            toast = periodLabel(sport, next) + " oynandi.";
        } else {
            matchFlow.notes.addAll(periodNotes);
            playNextWeek();
            matchFlow.complete = true;
            matchFlow.notes.add(periodLabel(sport, next) + " tamamlandi. Mac raporu hazir.");
        }
        render();
    }

    private boolean isPlannedMatchComplete(GameContext ctx) {
        if (!"Volleyball".equalsIgnoreCase(ctx.getSport().getSportName())) {
            return false;
        }
        return matchFlow.homeScore >= 3 || matchFlow.awayScore >= 3;
    }

    private void renderPostMatchBuffer(GameContext ctx) {
        VBox buffer = card("Mac Tamamlandi");
        buffer.getChildren().add(muted("Raporu inceleyip hazir oldugunda sonraki maca gecebilirsin."));
        FlowPane actions = new FlowPane(10, 10);
        Button next = primaryButton(ctx.getLeague().isSeasonOver() ? "Sezon Sonuna Gec" : "Sonraki Maca Hazirlan");
        next.setOnAction(e -> {
            matchFlow = null;
            showMatchLineup = false;
            render();
        });
        Button dashboard = ghostButton("Panele Don");
        dashboard.setOnAction(e -> {
            matchFlow = null;
            showMatchLineup = false;
            currentScreen = ScreenKey.DASHBOARD;
            render();
        });
        actions.getChildren().addAll(next, dashboard);
        buffer.getChildren().add(actions);
        page.getChildren().add(buffer);
        if (!matchFlow.notes.isEmpty()) {
            page.getChildren().add(timelineCard("Mac Zaman Cizelgesi", matchFlow.notes));
        }
        if (lastReport != null) {
            page.getChildren().add(matchReportCard(lastReport));
        }
    }

    private void addInlineSubstitutionControls(VBox target, ITeam team) {
        TilePane lists = tilePane(300);
        ListView<IPlayer> starters = playerList(team.getStartingLineup());
        ListView<IPlayer> bench = playerList(benchPlayers(team));
        VBox starterBox = card("Ilk Kadro");
        VBox benchBox = card("Yedekler");
        starterBox.getChildren().add(starters);
        benchBox.getChildren().add(bench);
        lists.getChildren().addAll(starterBox, benchBox);

        Button replace = primaryButton("Secili Oyunculari Degistir");
        replace.setOnAction(e -> {
            IPlayer out = starters.getSelectionModel().getSelectedItem();
            IPlayer in = bench.getSelectionModel().getSelectedItem();
            if (out == null || in == null) {
                toast = "Bir ilk kadro oyuncusu ve bir yedek secmelisin.";
            } else {
                team.substitutePlayer(out, in);
                toast = team.getStartingLineup().contains(in)
                    ? "Degisiklik yapildi."
                    : "Degisiklik kadro kurallarina uymadi.";
            }
            render();
        });

        Button best = ghostButton("En Iyi Kadro");
        best.setOnAction(e -> {
            if (team instanceof AbstractTeam) {
                ((AbstractTeam) team).setStartingLineup(bestLineup(team));
                toast = "En iyi kadro denendi.";
            }
            render();
        });

        target.getChildren().addAll(lists, new FlowPane(10, 10, replace, best));
    }

    private Node compactLineupSummary(ITeam team) {
        VBox box = new VBox(8);
        box.getChildren().add(metricRow("Ilk Kadro", team.getStartingLineup().size()
            + "/" + LineupWarnings.expectedLineupSize(team)));
        box.getChildren().add(metricRow("Yedek", String.valueOf(benchPlayers(team).size())));
        List<String> warnings = LineupWarnings.check(team);
        if (warnings.isEmpty()) {
            box.getChildren().add(good("Kadro hazir. Detay icin kadro panelini acabilirsin."));
        } else {
            box.getChildren().add(bad(turkishWarning(warnings.get(0))));
        }
        return box;
    }

    private String nextPeriodButtonText(ISport sport) {
        ensureMatchFlow(GameContext.getInstance());
        int next = Math.min(matchFlow.currentPeriod + 1, matchFlow.totalPeriods);
        return periodLabel(sport, next) + " Simule Et";
    }

    private String periodLabel(ISport sport, int period) {
        String periodName = sport.getPeriodName();
        if ("Half".equalsIgnoreCase(periodName)) {
            return period + ". Devre";
        }
        if ("Set".equalsIgnoreCase(periodName)) {
            return period + ". Set";
        }
        return period + ". Periyot";
    }

    private List<String> periodActionNotes(GameContext ctx, int period) {
        IMatch match = findPlayerMatch(ctx.getLeague(), ctx.getPlayerTeam(), matchFlow.week);
        if (match == null) {
            return List.of(periodLabel(ctx.getSport(), period) + ": Takimin bu haftayi mac yapmadan geciyor.");
        }

        String home = match.getHomeTeam().getName();
        String away = match.getAwayTeam().getName();
        int seed = (home + away + period + matchFlow.week).hashCode() & 0x7fffffff;
        if ("Volleyball".equalsIgnoreCase(ctx.getSport().getSportName())) {
            return volleyballPeriodNotes(period, match.getHomeTeam(), match.getAwayTeam(), seed);
        }
        return footballPeriodNotes(period, match.getHomeTeam(), match.getAwayTeam(), seed);
    }

    private List<String> footballPeriodNotes(int period, ITeam homeTeam, ITeam awayTeam, int seed) {
        String home = homeTeam.getName();
        String away = awayTeam.getName();
        String active = seed % 2 == 0 ? home : away;
        String other = active.equals(home) ? away : home;
        int firstMinute = period == 1 ? 18 + seed % 18 : 50 + seed % 18;
        int secondMinute = firstMinute + 9 + seed % 11;
        int thirdMinute = secondMinute + 6 + seed % 10;
        if (period == 1) {
            secondMinute = Math.min(50, secondMinute);
            thirdMinute = Math.min(50, thirdMinute);
        }
        List<String> notes = new ArrayList<>();
        int homeGoals = periodScore(seed, period, true, false);
        int awayGoals = periodScore(seed, period, false, false);
        matchFlow.homeScore += homeGoals;
        matchFlow.awayScore += awayGoals;
        notes.add(periodLabel(new com.sportsmanager.sport.football.FootballSport(), period)
            + " sonunda: " + home + " " + matchFlow.homeScore + " - " + matchFlow.awayScore + " " + away + ".");
        addGoalNotes(notes, homeTeam, period, homeGoals, seed, true);
        addGoalNotes(notes, awayTeam, period, awayGoals, seed, false);
        notes.add(formatFootballMinute(period, firstMinute) + ": " + active + " orta sahada tempoyu yukseltti.");
        notes.add(formatFootballMinute(period, secondMinute) + ": " + other + " kanattan geldi, savunma son anda kapatti.");
        notes.add(formatFootballMinute(period, thirdMinute) + ": " + active + " kaleyi yokladi, kaleci kontrol etti.");
        return notes;
    }

    private void addGoalNotes(List<String> notes, ITeam team, int period, int goals, int seed, boolean home) {
        for (int i = 0; i < goals; i++) {
            IPlayer scorer = pickScorer(team, seed, i, home);
            if (scorer == null) {
                continue;
            }
            scorer.incrementGoalsScored();
            int minute = goalMinute(period, seed, i, home);
            notes.add(formatFootballMinute(period, minute) + ": GOL! " + team.getName()
                + " adina " + scorer.getName() + " fileleri havalandirdi.");
        }
    }

    private IPlayer pickScorer(ITeam team, int seed, int goalIndex, boolean home) {
        List<IPlayer> candidates = new ArrayList<>();
        for (IPlayer player : team.getStartingLineup().isEmpty() ? team.getSquad() : team.getStartingLineup()) {
            if (!player.isInjured() && !"GK".equalsIgnoreCase(player.getPosition())) {
                candidates.add(player);
            }
        }
        if (candidates.isEmpty()) {
            return null;
        }
        candidates.sort(Comparator.comparingInt(IPlayer::getOverallRating).reversed());
        int index = Math.abs(seed + goalIndex * 3 + (home ? 1 : 5)) % Math.min(6, candidates.size());
        return candidates.get(index);
    }

    private int goalMinute(int period, int seed, int goalIndex, boolean home) {
        if (period == 1) {
            int[] firstHalf = {23, 37, 45, 48, 50};
            return firstHalf[Math.abs(seed + goalIndex + (home ? 0 : 2)) % firstHalf.length];
        }
        return 52 + Math.abs(seed / (goalIndex + 2) + (home ? 0 : 9)) % 35;
    }

    private List<String> volleyballPeriodNotes(int period, ITeam homeTeam, ITeam awayTeam, int seed) {
        String home = homeTeam.getName();
        String away = awayTeam.getName();
        String active = seed % 2 == 0 ? home : away;
        String other = active.equals(home) ? away : home;
        int homeScore = 25;
        int awayScore = 18 + seed % 8;
        if (seed % 3 == 0) {
            awayScore = 25;
            homeScore = 18 + seed % 8;
        }
        if (homeScore == awayScore) {
            homeScore += 2;
        }
        if (homeScore > awayScore) {
            matchFlow.homeScore++;
        } else {
            matchFlow.awayScore++;
        }
        List<String> notes = new ArrayList<>();
        notes.add(period + ". set sonucu: " + home + " " + homeScore + " - " + awayScore + " " + away + ".");
        notes.add("Setlerde durum: " + home + " " + matchFlow.homeScore + " - " + matchFlow.awayScore + " " + away + ".");
        notes.add(period + ". set: " + active + " servis baskisiyla ritim buldu.");
        notes.add(period + ". set: " + other + " blokta cevap vermeye calisti.");
        notes.add(period + ". set: " + active + " kritik rallilerde daha sakin kaldi.");
        return notes;
    }

    private int periodScore(int seed, int period, boolean home, boolean volleyball) {
        int mod = volleyball ? 4 : 3;
        int value = home ? seed : seed / 7;
        int score = Math.abs(value + period * (home ? 2 : 3)) % mod;
        if (!volleyball && period == 1) {
            return Math.min(2, score);
        }
        return score;
    }

    private String formatFootballMinute(int period, int minute) {
        if (period == 1 && minute > 45) {
            return "45+" + Math.min(5, minute - 45) + ". dakika";
        }
        return minute + ". dakika";
    }

    private void playNextWeek() {
        GameContext ctx = GameContext.getInstance();
        ILeague league = ctx.getLeague();
        ITeam team = ctx.getPlayerTeam();
        int week = league.getCurrentWeek();
        IMatch playerMatch = findPlayerMatch(league, team, week);
        MatchResult plannedResult = plannedMatchResult(playerMatch);
        Map<IPlayer, Map<String, Integer>> attributesBeforeTraining = snapshotAttributes(team);

        league.advanceWeek();
        lastTrainingReport = buildTrainingReport(week, team, attributesBeforeTraining);

        if (plannedResult != null) {
            alignPlayerMatchResult(ctx.getSport().getSportName(), playerMatch, plannedResult);
        }

        MatchResult result = plannedResult != null ? plannedResult : playerMatch == null ? null : playerMatch.getResult();
        List<String> events = reportEvents(playerMatch, plannedResult);
        int newInjuries = countInjuryEvents(events);
        lastReport = new MatchReport(week, result, events, newInjuries);

        if (result != null) {
            ctx.addNews("Hafta " + week + ": " + result + ".");
        }
        if (newInjuries > 0) {
            ctx.addNews("Hafta " + week + ": " + newInjuries + " yeni sakatlik.");
        }
        if (lastTrainingReport != null && !lastTrainingReport.improvements.isEmpty()) {
            ctx.addNews("Hafta " + week + ": Antrenmanda " + lastTrainingReport.improvements.size()
                + " gelisim notu kaydedildi.");
        }
        List<ITeam> standings = league.getStandings().getTeams();
        if (!standings.isEmpty()) {
            ctx.addNews("Hafta " + week + ": " + standings.get(0).getName() + " "
                + standings.get(0).getPoints() + " puanla lider.");
        }
        ctx.addNews("Hafta " + week + ": " + team.getName() + " siralamada " + rankText(league, team) + ".");
        toast = "Hafta " + week + " simule edildi.";
    }

    private MatchResult plannedMatchResult(IMatch playerMatch) {
        if (playerMatch == null || matchFlow == null || matchFlow.currentPeriod == 0) {
            return null;
        }
        return new MatchResult(playerMatch.getHomeTeam(), playerMatch.getAwayTeam(),
            matchFlow.homeScore, matchFlow.awayScore);
    }

    private void alignPlayerMatchResult(String sportName, IMatch playerMatch, MatchResult plannedResult) {
        if (!(playerMatch instanceof AbstractMatch) || plannedResult == null) {
            return;
        }
        MatchResult actualResult = playerMatch.getResult();
        adjustPoints(actualResult, sportName, -1);
        ((AbstractMatch) playerMatch).restoreResult(plannedResult);
        adjustPoints(plannedResult, sportName, 1);
    }

    private void adjustPoints(MatchResult result, String sportName, int direction) {
        if (result == null) {
            return;
        }
        int[] points = pointsForResult(result, sportName);
        result.getHomeTeam().addPoints(points[0] * direction);
        result.getAwayTeam().addPoints(points[1] * direction);
    }

    private int[] pointsForResult(MatchResult result, String sportName) {
        int home = result.getHomeScore();
        int away = result.getAwayScore();
        if ("Volleyball".equalsIgnoreCase(sportName)) {
            if (home > away) {
                return away <= 1 ? new int[]{3, 0} : new int[]{2, 1};
            }
            if (away > home) {
                return home <= 1 ? new int[]{0, 3} : new int[]{1, 2};
            }
            return new int[]{0, 0};
        }
        if (home > away) {
            return new int[]{3, 0};
        }
        if (away > home) {
            return new int[]{0, 3};
        }
        return new int[]{1, 1};
    }

    private List<String> reportEvents(IMatch playerMatch, MatchResult plannedResult) {
        if (playerMatch == null) {
            return List.of();
        }
        List<String> events = new ArrayList<>();
        for (String event : playerMatch.getCommentary()) {
            if (plannedResult != null && event != null && event.startsWith("Mac bitti:")) {
                continue;
            }
            events.add(event);
        }
        if (plannedResult != null) {
            events.add("Mac bitti: " + plannedResult + ".");
        }
        return events;
    }

    private int countInjuryEvents(List<String> events) {
        int count = 0;
        for (String event : events) {
            if (event != null && event.toLowerCase().contains("sakatlandi")) {
                count++;
            }
        }
        return count;
    }

    private Node matchReportCard(MatchReport report) {
        VBox box = card("Hafta " + report.week + " Raporu");
        if (report.result == null) {
            box.getChildren().add(muted("Takimin bu hafta mac yapmadi."));
        } else {
            box.getChildren().add(bigText(report.result.toString()));
            ITeam team = GameContext.getInstance().getPlayerTeam();
            MatchResult result = report.result;
            String note = result.getWinner() == null ? "Beraberlik" : result.getWinner().equals(team) ? "Galibiyet" : "Maglubiyet";
            box.getChildren().add(metricRow("Sonuc", note));
        }
        if (report.newInjuries > 0) {
            box.getChildren().add(metricRow("Yeni sakatlik", String.valueOf(report.newInjuries)));
        }
        if (!report.events.isEmpty()) {
            box.getChildren().add(new Separator());
            report.events.stream().limit(5).forEach(event -> box.getChildren().add(rowLabel(event)));
        }
        return box;
    }

    private void renderSquad() {
        if (!hasGame()) {
            renderEmptyState();
            return;
        }
        ITeam team = GameContext.getInstance().getPlayerTeam();
        page.getChildren().add(titleBlock("Oyuncu Odasi", team.getName() + " - kadro, form, kondisyon ve roller"));
        TilePane overview = tilePane(360);
        overview.getChildren().add(healthCenterCard(team));
        overview.getChildren().add(developmentSummaryCard(team));
        if (lastTrainingReport != null) {
            overview.getChildren().add(trainingReportCard(lastTrainingReport));
        }
        page.getChildren().add(overview);

        ComboBox<String> position = new ComboBox<>();
        List<String> positions = new ArrayList<>();
        positions.add("Tum");
        for (IPlayer player : team.getSquad()) {
            if (!positions.contains(player.getPosition())) {
                positions.add(player.getPosition());
            }
        }
        position.setItems(FXCollections.observableArrayList(positions));
        position.getSelectionModel().select("Tum");
        CheckBox injuredOnly = new CheckBox("Sadece sakatlar");
        Button apply = ghostButton("Filtrele");
        HBox filters = new HBox(12, field("Pozisyon", position), injuredOnly, apply);
        filters.setAlignment(Pos.BOTTOM_LEFT);

        TableView<IPlayer> table = playerTable();
        table.setItems(FXCollections.observableArrayList(team.getSquad()));
        apply.setOnAction(e -> {
            List<IPlayer> players = new ArrayList<>(team.getSquad());
            if (!"Tum".equals(position.getValue())) {
                players.removeIf(player -> !player.getPosition().equals(position.getValue()));
            }
            if (injuredOnly.isSelected()) {
                players.removeIf(player -> !player.isInjured());
            }
            players.sort(Comparator.comparingInt(IPlayer::getOverallRating).reversed());
            table.setItems(FXCollections.observableArrayList(players));
            if (!table.getItems().isEmpty()) {
                table.getSelectionModel().select(0);
            }
        });

        VBox box = card("Kadro Listesi");
        box.getChildren().addAll(filters, table);

        VBox detail = card("Oyuncu Detayi");
        Runnable refreshDetail = () -> {
            detail.getChildren().clear();
            detail.getChildren().add(new Label("Oyuncu Detayi"));
            IPlayer selected = table.getSelectionModel().getSelectedItem();
            detail.getChildren().add(selected == null ? muted("Ozellikleri gormek icin bir oyuncu sec.") : playerDetail(selected, team));
        };
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldPlayer, newPlayer) -> refreshDetail.run());
        if (!table.getItems().isEmpty()) {
            table.getSelectionModel().select(0);
        }
        refreshDetail.run();

        TilePane layout = tilePane(520);
        layout.getChildren().addAll(box, detail);
        page.getChildren().add(layout);
    }

    private VBox injurySummaryCard(ITeam team) {
        VBox box = card("Sakatlik Raporu");
        box.getChildren().add(bad(injuredCount(team) + " oyuncu mac kadrosu icin uygun degil."));
        for (IPlayer player : team.getSquad()) {
            if (player.isInjured()) {
                box.getChildren().add(rowLabel(player.getName() + " - " + player.getPosition()
                    + " - " + player.getInjuryGamesRemaining() + " hafta yok"));
            }
        }
        return box;
    }

    private VBox healthCenterCard(ITeam team) {
        VBox box = card("Saglik Merkezi");
        int injured = injuredCount(team);
        box.getChildren().add(progress("Kadro sagligi", health(team),
            injured == 0 ? "tam kadro hazir" : injured + " oyuncu tedavide",
            injured == 0 ? GREEN : injured <= 2 ? AMBER : RED));
        if (injured == 0) {
            box.getChildren().add(good("Sakat oyuncu yok. Mac kadrosu icin temiz tablo."));
            return box;
        }
        team.getSquad().stream()
            .filter(IPlayer::isInjured)
            .sorted(Comparator.comparingInt(IPlayer::getInjuryGamesRemaining))
            .forEach(player -> box.getChildren().add(rowLabel(player.getName() + " - "
                + player.getPosition() + " - " + injuryReturnText(player.getInjuryGamesRemaining()))));
        return box;
    }

    private VBox developmentSummaryCard(ITeam team) {
        VBox box = card("Gelisim Ozeti");
        List<IPlayer> players = new ArrayList<>(team.getSquad());
        players.sort(Comparator
            .comparingInt((IPlayer player) -> player.getPotential() - player.getOverallRating())
            .reversed()
            .thenComparing(Comparator.comparingInt(IPlayer::getOverallRating).reversed()));
        int added = 0;
        for (IPlayer player : players) {
            int gap = Math.max(0, player.getPotential() - player.getOverallRating());
            if (gap == 0 && added > 0) {
                continue;
            }
            box.getChildren().add(rowLabel(player.getName() + " - " + player.getPosition()
                + " - gelisim payi " + signed(gap) + " - " + player.getMatchesPlayed() + " mac"));
            added++;
            if (added >= 5) {
                break;
            }
        }
        if (added == 0) {
            box.getChildren().add(muted("Kadroda gelisim payi okunacak oyuncu yok."));
        }
        return box;
    }

    private VBox trainingReportCard(TrainingReport report) {
        VBox box = card("Antrenman Raporu");
        box.getChildren().add(metricRow("Hafta", String.valueOf(report.week)));
        if (report.improvements.isEmpty()) {
            box.getChildren().add(muted("Bu hafta kayda deger ozellik artisi gorulmedi."));
            return box;
        }
        report.improvements.stream()
            .limit(6)
            .forEach(note -> box.getChildren().add(rowLabel(note)));
        if (report.improvements.size() > 6) {
            box.getChildren().add(muted("+" + (report.improvements.size() - 6) + " gelisim notu daha."));
        }
        return box;
    }

    private VBox teamComparisonCard(ILeague league, ITeam team) {
        VBox box = card("Takim Karsilastirma");
        IMatch match = findPlayerMatch(league, team, league.getCurrentWeek());
        if (match == null || league.isSeasonOver()) {
            box.getChildren().add(muted(league.isSeasonOver()
                ? "Sezon tamamlandi. Yeni sezonla rakip karsilastirmasi yenilenir."
                : "Bu hafta karsilastirilacak mac yok."));
            return box;
        }
        ITeam opponent = match.getHomeTeam().equals(team) ? match.getAwayTeam() : match.getHomeTeam();
        SportFactory factory = GameContext.getInstance().getSportFactory();
        if (factory == null && GameContext.getInstance().getSport() != null) {
            factory = registry.getFactory(GameContext.getInstance().getSport().getSportName().toLowerCase());
        }
        box.getChildren().add(bigText(team.getName() + " vs " + opponent.getName()));
        box.getChildren().add(metricRow("Genel guc", compareValues(team.getTeamOverallRating(),
            opponent.getTeamOverallRating())));
        if (factory != null) {
            box.getChildren().add(metricRow("Hucum", compareValues(
                (int) Math.round(averageAttributes(team, attackKeys(factory))),
                (int) Math.round(averageAttributes(opponent, attackKeys(factory))))));
            box.getChildren().add(metricRow("Savunma", compareValues(
                (int) Math.round(averageAttributes(team, defenseKeys(factory))),
                (int) Math.round(averageAttributes(opponent, defenseKeys(factory))))));
        }
        box.getChildren().add(metricRow("Saglik", compareValues(health(team), health(opponent))));
        box.getChildren().add(metricRow("Moral", compareValues(morale(team), morale(opponent))));
        box.getChildren().add(progress("Mac zorlugu", fixtureDifficulty(match, team),
            difficultyLabel(fixtureDifficulty(match, team)), difficultyColor(fixtureDifficulty(match, team))));
        return box;
    }

    private VBox timelineCard(String title, List<String> notes) {
        VBox section = new VBox(12);
        Label heading = new Label(title);
        heading.setWrapText(true);
        heading.setStyle("-fx-text-fill: " + INK + "; -fx-font-size: 20px; -fx-font-weight: 900;");
        TilePane cards = tilePane(320);
        for (Map.Entry<String, List<String>> entry : groupedTimelineNotes(notes).entrySet()) {
            cards.getChildren().add(timelinePeriodCard(entry.getKey(), entry.getValue()));
        }
        section.getChildren().addAll(heading, cards);
        return section;
    }

    private VBox timelinePeriodCard(String title, List<String> notes) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(16));
        box.setStyle("-fx-background-color: " + SURFACE + ";"
            + "-fx-background-radius: 14;"
            + "-fx-border-color: " + LINE + ";"
            + "-fx-border-radius: 14;");
        Label heading = new Label(title);
        heading.setWrapText(true);
        heading.setStyle("-fx-text-fill: " + INK + "; -fx-font-size: 16px; -fx-font-weight: 900;");
        box.getChildren().add(heading);
        for (String note : notes) {
            String text = timelineText(note);
            if (isTimelineScoreNote(note)) {
                box.getChildren().add(timelineScoreLabel(text));
            } else {
                box.getChildren().add(rowLabel(text));
            }
        }
        return box;
    }

    private Label timelineScoreLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setStyle("-fx-background-color: #E7F8F5; -fx-background-radius: 8;"
            + "-fx-padding: 10; -fx-text-fill: #075B51; -fx-font-weight: 900;");
        return label;
    }

    private VBox scoringLeadersCard(ILeague league, int limit) {
        VBox box = card("Gol / Sayi Liderleri");
        List<IPlayer> players = allPlayers(league);
        players.sort(Comparator
            .comparingInt(IPlayer::getGoalsScored)
            .reversed()
            .thenComparing(Comparator.comparingInt(IPlayer::getOverallRating).reversed()));
        int added = 0;
        for (IPlayer player : players) {
            if (player.getGoalsScored() <= 0) {
                break;
            }
            box.getChildren().add(rowLabel((added + 1) + ". " + player.getName()
                + " - " + player.getGoalsScored() + " gol/sayi"));
            added++;
            if (added >= limit) {
                break;
            }
        }
        if (added == 0) {
            box.getChildren().add(muted("Henuz gol/sayi kaydi olusmadi."));
        }
        return box;
    }

    private VBox seasonSnapshotCard(ILeague league, ITeam playerTeam) {
        VBox box = card("Sezon Panosu");
        ITeam leader = league.getStandings().getTeams().isEmpty() ? null : league.getStandings().getTeams().get(0);
        ITeam champion = league.getChampion();
        box.getChildren().add(metricRow("Durum", league.isSeasonOver() ? "Tamamlandi" : "Devam ediyor"));
        box.getChildren().add(metricRow("Sira", rankText(league, playerTeam)));
        box.getChildren().add(metricRow("Puan", String.valueOf(playerTeam.getPoints())));
        box.getChildren().add(metricRow("Lider", leader == null ? "-" : leader.getName() + " (" + leader.getPoints() + "p)"));
        if (league.isSeasonOver()) {
            box.getChildren().add(metricRow("Sampiyon", champion == null ? "-" : champion.getName()));
        }
        ITeam bestAttack = bestTeamBy(league, true);
        ITeam bestDefense = bestTeamBy(league, false);
        box.getChildren().add(metricRow("En iyi hucum", bestAttack == null ? "-" : bestAttack.getName()
            + " (" + goalsFor(league, bestAttack) + ")"));
        box.getChildren().add(metricRow("En iyi savunma", bestDefense == null ? "-" : bestDefense.getName()
            + " (" + goalsAgainst(league, bestDefense) + ")"));
        return box;
    }

    private TableView<IPlayer> playerTable() {
        TableView<IPlayer> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setMinHeight(420);
        styleTable(table);

        TableColumn<IPlayer, String> name = col("Ad", p -> p.getName());
        TableColumn<IPlayer, String> pos = col("Pos", p -> p.getPosition());
        TableColumn<IPlayer, String> age = col("Yas", p -> String.valueOf(p.getAge()));
        TableColumn<IPlayer, String> ovr = col("Guc", p -> String.valueOf(p.getOverallRating()));
        TableColumn<IPlayer, String> form = col("Form", IPlayer::getFormLabel);
        TableColumn<IPlayer, String> goals = col("Gol/Sayi", p -> String.valueOf(p.getGoalsScored()));
        TableColumn<IPlayer, String> status = col("Durum", p -> p.isInjured()
            ? "SAKAT - " + p.getInjuryGamesRemaining() + " hafta"
            : "Hazir");
        table.getColumns().addAll(name, pos, age, ovr, form, goals, status);
        table.setRowFactory(view -> new javafx.scene.control.TableRow<>() {
            @Override
            protected void updateItem(IPlayer player, boolean empty) {
                super.updateItem(player, empty);
                if (empty || player == null) {
                    setStyle("");
                } else if (player.isInjured()) {
                    setStyle("-fx-background-color: #FCECEF; -fx-text-background-color: " + RED + ";");
                } else {
                    setStyle("");
                }
            }
        });
        return table;
    }

    private void renderLineup() {
        if (!hasGame()) {
            renderEmptyState();
            return;
        }
        ITeam team = GameContext.getInstance().getPlayerTeam();
        page.getChildren().add(titleBlock("Kadro Laboratuvari", "Gecerli ilk kadroyu kur ve yedekleri yonet"));

        TilePane tile = tilePane(360);
        ListView<IPlayer> starters = playerList(team.getStartingLineup());
        ListView<IPlayer> bench = playerList(benchPlayers(team));
        VBox startersBox = card("Ilk Kadro");
        VBox benchBox = card("Yedekler");
        startersBox.getChildren().add(starters);
        benchBox.getChildren().add(bench);
        tile.getChildren().addAll(startersBox, benchBox);
        page.getChildren().add(tile);

        VBox controls = card("Kadro Araclari");
        Button replace = primaryButton("Secili Oyunculari Degistir");
        replace.setOnAction(e -> {
            IPlayer out = starters.getSelectionModel().getSelectedItem();
            IPlayer in = bench.getSelectionModel().getSelectedItem();
            if (out == null || in == null) {
                toast = "Once bir ilk kadro oyuncusu ve bir yedek secmelisin.";
            } else {
                team.substitutePlayer(out, in);
                toast = team.getStartingLineup().contains(in) ? "Degisiklik tamamlandi." : "Degisiklik kadro kurallarina uymadi.";
            }
            render();
        });
        Button autoFix = ghostButton("Sakatlari Otomatik Duzelt");
        autoFix.setOnAction(e -> {
            if (team instanceof AbstractTeam) {
                ((AbstractTeam) team).autoFixLineup();
                toast = "Kadro otomatik kontrol edildi.";
            }
            render();
        });
        Button bestXi = ghostButton("En Iyi Kadro");
        bestXi.setOnAction(e -> {
            if (team instanceof AbstractTeam) {
                List<IPlayer> best = bestLineup(team);
                ((AbstractTeam) team).setStartingLineup(best);
                toast = best.size() == LineupWarnings.expectedLineupSize(team)
                    ? "En iyi kadro secildi."
                    : "Uygun oyunculardan tam ve gecerli kadro kurulamadi.";
            }
            render();
        });
        FlowPane buttons = new FlowPane(10, 10, replace, autoFix, bestXi);
        controls.getChildren().add(buttons);
        List<String> warnings = LineupWarnings.check(team);
        if (warnings.isEmpty()) {
            controls.getChildren().add(good("Kadro hazir."));
        } else {
            for (String warning : warnings) {
                controls.getChildren().add(bad(turkishWarning(warning)));
            }
        }
        page.getChildren().add(controls);
    }

    private void renderTactics() {
        if (!hasGame()) {
            renderEmptyState();
            return;
        }
        GameContext ctx = GameContext.getInstance();
        ITeam team = ctx.getPlayerTeam();
        SportFactory factory = ctx.getSportFactory();
        page.getChildren().add(titleBlock("Taktik Tahtasi", "Mac oncesi risk profilini sec"));

        TilePane options = tilePane(260);
        if (factory != null) {
            for (ITactic tactic : factory.getAvailableTactics()) {
                VBox box = card(turkishTactic(tactic.getName()));
                boolean active = team.getTactic() != null && team.getTactic().getName().equalsIgnoreCase(tactic.getName());
                int attackBias = (int) Math.round(tactic.getAttackModifier() * 50);
                int risk = (int) Math.round((tactic.getAttackModifier() - tactic.getDefenseModifier() + 1.0) * 50);
                box.getChildren().addAll(
                    metricRow("Hucum", String.format("%.2f", tactic.getAttackModifier())),
                    metricRow("Defans", String.format("%.2f", tactic.getDefenseModifier())),
                    progress("Hucum egilimi", Math.max(0, Math.min(100, attackBias)), "one cikma istegi", CORAL),
                    progress("Risk", Math.max(0, Math.min(100, risk)), tacticSummary(tactic), risk > 60 ? CORAL : TEAL),
                    active ? good("Aktif taktik") : muted("Secilebilir taktik")
                );
                Button choose = active ? ghostButton("Secili") : primaryButton("Taktigi Kullan");
                choose.setDisable(active);
                choose.setOnAction(e -> {
                    team.setTactic(tactic);
                    toast = "Taktik " + turkishTactic(tactic.getName()) + " olarak degisti.";
                    render();
                });
                box.getChildren().add(choose);
                options.getChildren().add(box);
            }
        }
        page.getChildren().add(options);
    }

    private void renderLeague() {
        if (!hasGame()) {
            renderEmptyState();
            return;
        }
        ILeague league = GameContext.getInstance().getLeague();
        ITeam playerTeam = GameContext.getInstance().getPlayerTeam();
        page.getChildren().add(titleBlock("Lig Tablosu", league.getName()));

        TableView<ITeam> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(FXCollections.observableArrayList(league.getStandings().getTeams()));
        styleTable(table);
        table.setMinHeight(460);
        table.getColumns().addAll(
            col("Takim", ITeam::getName),
            col("P", t -> String.valueOf(played(league, t))),
            col("W", t -> String.valueOf(league.getWins(t))),
            col("B", t -> String.valueOf(league.getDraws(t))),
            col("L", t -> String.valueOf(league.getLosses(t))),
            col("GF", t -> String.valueOf(goalsFor(league, t))),
            col("GA", t -> String.valueOf(goalsAgainst(league, t))),
            col("GD", t -> signed(goalsFor(league, t) - goalsAgainst(league, t))),
            col("Pts", t -> String.valueOf(t.getPoints()))
        );
        table.getSelectionModel().select(playerTeam);
        VBox box = card("Puan Durumu");
        box.getChildren().add(table);
        page.getChildren().add(box);

        TilePane extras = tilePane(360);
        extras.getChildren().add(scoringLeadersCard(league, 8));
        extras.getChildren().add(seasonSnapshotCard(league, playerTeam));
        page.getChildren().add(extras);
    }

    private void renderSeasonSummary() {
        if (!hasGame()) {
            renderEmptyState();
            return;
        }
        ILeague league = GameContext.getInstance().getLeague();
        ITeam playerTeam = GameContext.getInstance().getPlayerTeam();
        page.getChildren().add(titleBlock("Sezon Ozeti",
            league.getName() + " - Sezon " + league.getCurrentSeason() + " kariyer panoramasi"));

        TilePane overview = tilePane(320);
        overview.getChildren().add(seasonSnapshotCard(league, playerTeam));
        overview.getChildren().add(scoringLeadersCard(league, 10));
        overview.getChildren().add(healthCenterCard(playerTeam));
        page.getChildren().add(overview);

        TableView<ITeam> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(FXCollections.observableArrayList(league.getStandings().getTeams()));
        styleTable(table);
        table.setMinHeight(420);
        table.getColumns().addAll(
            col("Sira", t -> rankText(league, t)),
            col("Takim", ITeam::getName),
            col("P", t -> String.valueOf(played(league, t))),
            col("GF", t -> String.valueOf(goalsFor(league, t))),
            col("GA", t -> String.valueOf(goalsAgainst(league, t))),
            col("GD", t -> signed(goalsFor(league, t) - goalsAgainst(league, t))),
            col("Pts", t -> String.valueOf(t.getPoints()))
        );
        table.getSelectionModel().select(playerTeam);
        VBox standings = card("Sezon Tablosu");
        standings.getChildren().add(table);
        page.getChildren().add(standings);
    }

    private void renderFixtures() {
        if (!hasGame()) {
            renderEmptyState();
            return;
        }
        GameContext ctx = GameContext.getInstance();
        ILeague league = ctx.getLeague();
        ITeam playerTeam = ctx.getPlayerTeam();
        page.getChildren().add(titleBlock("Fikstur Merkezi", "Sezon programi ve sonuclar"));

        TableView<IMatch> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(FXCollections.observableArrayList(league.getAllFixtures()));
        styleTable(table);
        table.setMinHeight(500);
        table.getColumns().addAll(
            col("Hafta", m -> String.valueOf(m.getWeek())),
            col("Ev", m -> m.getHomeTeam().getName()),
            col("Dep", m -> m.getAwayTeam().getName()),
            col("Skor", m -> m.getResult() == null ? "-" : m.getResult().getHomeScore() + "-" + m.getResult().getAwayScore()),
            col("Zorluk", m -> fixtureDifficulty(m, playerTeam) < 0 ? "Lig maci" : difficultyLabel(fixtureDifficulty(m, playerTeam))),
            col("Durum", m -> m.isPlayed() ? "Oynandi" : "Yaklasiyor")
        );
        VBox box = card("Tum Fikstur");
        box.getChildren().add(table);
        page.getChildren().add(box);
    }

    private void renderNews() {
        if (!hasGame()) {
            renderEmptyState();
            return;
        }
        page.getChildren().add(titleBlock("Haber Merkezi", "Kariyer haber akisi"));
        List<String> news = GameContext.getInstance().getNewsFeed();
        if (news.isEmpty()) {
            VBox box = card("Basliklar");
            box.getChildren().add(muted("Henuz haber yok. Haber uretmek icin bir hafta oyna."));
            page.getChildren().add(box);
        } else {
            Map<String, List<String>> grouped = groupedNews(news);
            TilePane groups = tilePane(360);
            for (Map.Entry<String, List<String>> entry : grouped.entrySet()) {
                VBox box = card(entry.getKey());
                List<String> items = entry.getValue();
                for (int i = items.size() - 1; i >= 0; i--) {
                    box.getChildren().add(rowLabel(items.get(i)));
                }
                groups.getChildren().add(box);
            }
            page.getChildren().add(groups);
        }
    }

    private void renderSaveLoad() {
        page.getChildren().add(titleBlock("Kayit Merkezi", "Proje yaninda tutulan uc yerel kayit slotu"));
        TilePane slots = tilePane(280);
        for (int i = 0; i < SLOT_FILES.length; i++) {
            int slot = i + 1;
            VBox box = card("Slot " + slot);
            File file = SaveLoadService.resolveSaveFile(SLOT_FILES[i]);
            SaveSummary summary = readSaveSummary(SLOT_FILES[i]);
            if (summary == null) {
                box.getChildren().add(metricRow("Durum", "Bos"));
            } else {
                box.getChildren().addAll(
                    metricRow("Takim", summary.team),
                    metricRow("Lig", summary.league),
                    metricRow("Spor", turkishSport(summary.sport)),
                    metricRow("Ilerleme", "Sezon " + summary.season + ", Hafta " + summary.week),
                    metricRow("Kayit", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(file.lastModified())))
                );
            }
            Button save = primaryButton("Kaydet");
            save.setDisable(!hasGame());
            save.setOnAction(e -> {
                if (!confirmOverwriteSlot(slot)) {
                    return;
                }
                GameContext ctx = GameContext.getInstance();
                boolean saved = SaveLoadService.saveGameResult(SLOT_FILES[slot - 1], ctx.getSport(), ctx.getLeague(), ctx.getPlayerTeam());
                toast = saved ? "Slot " + slot + " kaydedildi." : "Slot " + slot + " kaydedilemedi.";
                render();
            });
            Button load = ghostButton("Yukle");
            load.setDisable(!file.exists());
            load.setOnAction(e -> loadSlot(slot));
            Button delete = dangerButton("Sil");
            delete.setDisable(!file.exists());
            delete.setOnAction(e -> {
                File target = SaveLoadService.resolveSaveFile(SLOT_FILES[slot - 1]);
                toast = target.delete() ? "Slot " + slot + " silindi." : "Slot " + slot + " silinemedi.";
                render();
            });
            FlowPane actions = new FlowPane(8, 8, save, load, delete);
            box.getChildren().add(actions);
            slots.getChildren().add(box);
        }
        page.getChildren().add(slots);
    }

    private void loadSlot(int slot) {
        if (!confirmLoadWithActiveGame()) {
            return;
        }
        LoadedGame loaded = SaveLoadService.loadGame(SLOT_FILES[slot - 1], registry);
        if (loaded == null) {
            toast = "Slot " + slot + " bos veya yuklenemiyor.";
            render();
            return;
        }
        GameContext ctx = GameContext.getInstance();
        ctx.startNewGame(loaded.getSport());
        ctx.setLeague(loaded.getLeague());
        ctx.setPlayerTeam(loaded.getPlayerTeam());
        String sportName = loaded.getSport().getSportName().toLowerCase();
        ctx.setSportFactory(registry.getFactory(sportName));
        lastReport = null;
        lastTrainingReport = null;
        matchFlow = null;
        showMatchLineup = false;
        currentScreen = ScreenKey.DASHBOARD;
        toast = "Slot " + slot + " yuklendi.";
        render();
    }

    private void requestExit() {
        if (confirmExitWithSave()) {
            exitConfirmed = true;
            stage.close();
        }
    }

    private boolean confirmExitWithSave() {
        if (!hasGame()) {
            return true;
        }

        GameContext ctx = GameContext.getInstance();
        ButtonType saveAndExit = new ButtonType("Kaydet ve Cik", ButtonBar.ButtonData.YES);
        ButtonType exitWithoutSave = new ButtonType("Kaydetmeden Cik", ButtonBar.ButtonData.NO);
        ButtonType cancel = new ButtonType("Vazgec", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", saveAndExit, exitWithoutSave, cancel);
        alert.setTitle("Cikis");
        alert.setHeaderText("Cikmadan once kaydetmek ister misin?");
        alert.setContentText(ctx.getPlayerTeam().getName() + " kariyerindeki son degisiklikleri kaydedebilirsin.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isEmpty() || result.get() == cancel) {
            return false;
        }
        if (result.get() == saveAndExit) {
            return saveCurrentGameWithDialog("Cikis iptal edildi. Farkli bir slot deneyebilirsin.");
        }
        return true;
    }

    private boolean saveCurrentGameWithDialog(String failureContent) {
        List<String> choices = new ArrayList<>();
        for (int i = 0; i < SLOT_FILES.length; i++) {
            choices.add(exitSlotLabel(i + 1, readSaveSummary(SLOT_FILES[i])));
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Kayit Slotu");
        dialog.setHeaderText("Hangi slota kaydedilsin?");
        dialog.setContentText("Slot sec:");

        Optional<String> selected = dialog.showAndWait();
        if (selected.isEmpty()) {
            return false;
        }

        int slot = parseSlotNumber(selected.get());
        if (slot < 1 || slot > SLOT_FILES.length) {
            return false;
        }
        if (!confirmOverwriteSlot(slot)) {
            return false;
        }

        GameContext ctx = GameContext.getInstance();
        boolean saved = SaveLoadService.saveGameResult(SLOT_FILES[slot - 1], ctx.getSport(), ctx.getLeague(), ctx.getPlayerTeam());
        if (!saved) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Kayit Hatasi");
            error.setHeaderText("Oyun kaydedilemedi.");
            error.setContentText(failureContent);
            error.showAndWait();
        }
        return saved;
    }

    private boolean confirmOverwriteSlot(int slot) {
        if (!SaveLoadService.resolveSaveFile(SLOT_FILES[slot - 1]).exists()) {
            return true;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Slot Uzerine Yazma");
        alert.setHeaderText("Slot " + slot + " zaten dolu.");
        alert.setContentText("Bu kaydi yeni kariyer durumunla degistirmek istiyor musun?");
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private boolean confirmLoadWithActiveGame() {
        if (!hasGame()) {
            return true;
        }
        ButtonType saveAndLoad = new ButtonType("Kaydet ve Yukle", ButtonBar.ButtonData.YES);
        ButtonType loadWithoutSave = new ButtonType("Kaydetmeden Yukle", ButtonBar.ButtonData.NO);
        ButtonType cancel = new ButtonType("Vazgec", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", saveAndLoad, loadWithoutSave, cancel);
        alert.setTitle("Kayit Yukle");
        alert.setHeaderText("Aktif kariyerin var.");
        alert.setContentText("Baska bir kaydi yuklemeden once mevcut kariyeri kaydetmek ister misin?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isEmpty() || result.get() == cancel) {
            return false;
        }
        if (result.get() == saveAndLoad) {
            return saveCurrentGameWithDialog("Yukleme iptal edildi. Farkli bir slot deneyebilirsin.");
        }
        return true;
    }

    private void renderEmptyState() {
        page.getChildren().add(hero("Aktif kariyer yok", "Menajer araclarini kullanmadan once kariyer olustur veya kayit yukle."));
        Button home = primaryButton("Kariyer Kurulumuna Git");
        home.setOnAction(e -> {
            currentScreen = ScreenKey.HOME;
            render();
        });
        page.getChildren().add(home);
    }

    private VBox createSideNav() {
        VBox nav = new VBox(10);
        nav.setPadding(new Insets(22));
        nav.setPrefWidth(240);
        nav.setStyle("-fx-background-color: " + SIDEBAR + ";");
        return nav;
    }

    private FlowPane createCompactNav() {
        FlowPane nav = new FlowPane(8, 8);
        nav.setPadding(new Insets(12));
        nav.setStyle("-fx-background-color: " + SIDEBAR + ";");
        return nav;
    }

    private void rebuildNavigation() {
        sideNav.getChildren().clear();
        compactNav.getChildren().clear();

        Label brand = new Label("SPORTS MANAGER");
        brand.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: 800;");
        Label sub = new Label("Menajer Masasi");
        sub.setStyle("-fx-text-fill: #B9C6CF; -fx-font-size: 12px;");
        sideNav.getChildren().addAll(brand, sub, spacer(12));

        addNav("Kurulum", ScreenKey.HOME);
        addNav("Panel", ScreenKey.DASHBOARD);
        addNav("Mac", ScreenKey.MATCH);
        addNav("Oyuncular", ScreenKey.SQUAD);
        addNav("Kadro", ScreenKey.LINEUP);
        addNav("Taktik", ScreenKey.TACTICS);
        addNav("Lig", ScreenKey.LEAGUE);
        addNav("Sezon", ScreenKey.SEASON);
        addNav("Fikstur", ScreenKey.FIXTURES);
        addNav("Haber", ScreenKey.NEWS);
        addNav("Kayit", ScreenKey.SAVE);
    }

    private void addNav(String label, ScreenKey screen) {
        boolean active = currentScreen == screen;
        Button side = new Button(label);
        side.setMaxWidth(Double.MAX_VALUE);
        side.setAlignment(Pos.CENTER_LEFT);
        side.setDisable(screen != ScreenKey.HOME && !hasGame() && screen != ScreenKey.SAVE);
        side.setStyle(navStyle(active));
        side.setOnAction(e -> {
            currentScreen = screen;
            render();
        });
        sideNav.getChildren().add(side);

        Button top = new Button(label);
        top.setDisable(side.isDisable());
        top.setStyle(navStyle(active));
        top.setOnAction(e -> {
            currentScreen = screen;
            render();
        });
        compactNav.getChildren().add(top);
    }

    private String navStyle(boolean active) {
        return "-fx-background-color: " + (active ? TEAL : "transparent") + ";"
            + "-fx-text-fill: " + (active ? "#071014" : "#EAF2F5") + ";"
            + "-fx-font-weight: 700;"
            + "-fx-background-radius: 8;"
            + "-fx-padding: 10 12;";
    }

    private VBox hero(String heading, String body) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(28));
        box.setStyle("-fx-background-color: linear-gradient(to right, #101820, #1D3B43);"
            + "-fx-background-radius: 18;");
        Label h = new Label(heading);
        h.setWrapText(true);
        h.setStyle("-fx-text-fill: white; -fx-font-size: 32px; -fx-font-weight: 900;");
        Label b = new Label(body);
        b.setWrapText(true);
        b.setStyle("-fx-text-fill: #D5E8EA; -fx-font-size: 15px;");
        box.getChildren().addAll(h, b);
        return box;
    }

    private VBox titleBlock(String heading, String body) {
        VBox box = new VBox(4);
        Label h = new Label(heading);
        h.setWrapText(true);
        h.setStyle("-fx-text-fill: " + INK + "; -fx-font-size: 30px; -fx-font-weight: 900;");
        Label b = new Label(body);
        b.setWrapText(true);
        b.setStyle("-fx-text-fill: " + MUTED + "; -fx-font-size: 14px;");
        box.getChildren().addAll(h, b);
        return box;
    }

    private VBox card(String title) {
        VBox box = new VBox(12);
        box.setPadding(new Insets(18));
        box.setStyle("-fx-background-color: " + SURFACE + ";"
            + "-fx-background-radius: 14;"
            + "-fx-border-color: " + LINE + ";"
            + "-fx-border-radius: 14;");
        Label label = new Label(title);
        label.setWrapText(true);
        label.setStyle("-fx-text-fill: " + INK + "; -fx-font-size: 16px; -fx-font-weight: 800;");
        box.getChildren().add(label);
        return box;
    }

    private VBox statCard(String label, String value, String accent) {
        VBox box = card(label);
        box.setMinHeight(112);
        Label val = new Label(value == null ? "-" : value);
        val.setWrapText(true);
        val.setStyle("-fx-text-fill: " + accent + "; -fx-font-size: 24px; -fx-font-weight: 900;");
        box.getChildren().add(val);
        return box;
    }

    private TilePane tilePane(double tileWidth) {
        TilePane tile = new TilePane();
        tile.setHgap(14);
        tile.setVgap(14);
        tile.setPrefTileWidth(tileWidth);
        tile.setTileAlignment(Pos.TOP_LEFT);
        tile.setMaxWidth(Double.MAX_VALUE);
        return tile;
    }

    private Node field(String label, Node control) {
        VBox box = new VBox(6);
        Label l = new Label(label);
        l.setStyle("-fx-text-fill: " + MUTED + "; -fx-font-size: 12px; -fx-font-weight: 700;");
        box.getChildren().addAll(l, control);
        VBox.setVgrow(control, Priority.NEVER);
        return box;
    }

    private Button primaryButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + TEAL + "; -fx-text-fill: #071014;"
            + "-fx-font-weight: 800; -fx-background-radius: 9; -fx-padding: 10 16;");
        return button;
    }

    private Button ghostButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #EAF2F5; -fx-text-fill: " + INK + ";"
            + "-fx-font-weight: 700; -fx-background-radius: 9; -fx-padding: 10 16;");
        return button;
    }

    private Button dangerButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #FFE7E4; -fx-text-fill: " + RED + ";"
            + "-fx-font-weight: 800; -fx-background-radius: 9; -fx-padding: 10 16;");
        return button;
    }

    private Button navAction(String label, ScreenKey key) {
        Button button = ghostButton(label);
        button.setOnAction(e -> {
            currentScreen = key;
            render();
        });
        return button;
    }

    private VBox menuCard(String title, String subtitle, Runnable action) {
        VBox box = card(title);
        box.setMinHeight(150);
        box.getChildren().add(muted(subtitle));
        Region push = new Region();
        VBox.setVgrow(push, Priority.ALWAYS);
        Button button = primaryButton(title);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(e -> action.run());
        box.getChildren().addAll(push, button);
        return box;
    }

    private Node stepHeader(String step, String title) {
        VBox box = card(step);
        box.getChildren().add(bigText(title));
        if (draftSport != null) {
            box.getChildren().add(metricRow("Spor", turkishSport(draftSport)));
        }
        if (draftLeague != null) {
            box.getChildren().add(metricRow("Lig", draftLeague.getLeagueName()));
        }
        return box;
    }

    private Button backToMenuButton() {
        Button back = ghostButton("Ana Menuye Don");
        back.setOnAction(e -> {
            homeMode = HomeMode.MENU;
            render();
        });
        return back;
    }

    private Button backStepButton(String label, HomeMode mode) {
        Button back = ghostButton(label);
        back.setOnAction(e -> {
            homeMode = mode;
            render();
        });
        return back;
    }

    private Label formPill(String marker) {
        Label label = new Label(marker);
        label.setMinSize(38, 38);
        label.setAlignment(Pos.CENTER);
        String color = "W".equals(marker) ? GREEN : "D".equals(marker) ? AMBER : RED;
        label.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white;"
            + "-fx-background-radius: 999; -fx-font-weight: 900;");
        return label;
    }

    private Node playerDetail(IPlayer player, ITeam team) {
        VBox box = new VBox(10);
        box.getChildren().addAll(
            bigText(player.getName()),
            metricRow("Pozisyon", player.getPosition()),
            metricRow("Rol", team.getStartingLineup().contains(player) ? "Ilk kadro" : "Yedek"),
            metricRow("Yas", String.valueOf(player.getAge())),
            metricRow("Guc", String.valueOf(player.getOverallRating())),
            metricRow("Potansiyel", String.valueOf(player.getPotential())),
            metricRow("Gelisim Payi", signed(Math.max(0, player.getPotential() - player.getOverallRating()))),
            metricRow("Form", player.getFormLabel()),
            metricRow("Gol/Sayi", String.valueOf(player.getGoalsScored())),
            metricRow("Durum", player.isInjured() ? "Sakat " + player.getInjuryGamesRemaining() + " hafta" : "Hazir"),
            metricRow("Mac", String.valueOf(player.getMatchesPlayed()))
        );
        box.getChildren().add(new Separator());
        player.getAttributes().entrySet().stream()
            .sorted(java.util.Map.Entry.comparingByKey())
            .forEach(entry -> box.getChildren().add(attributeBar(entry.getKey(), entry.getValue())));
        return box;
    }

    private Node attributeBar(String label, int value) {
        return progress(label, Math.max(0, Math.min(100, value)), String.valueOf(value), value >= 75 ? GREEN : value >= 55 ? AMBER : CORAL);
    }

    private List<IPlayer> bestLineup(ITeam team) {
        List<IPlayer> healthy = new ArrayList<>();
        for (IPlayer player : team.getSquad()) {
            if (!player.isInjured()) {
                healthy.add(player);
            }
        }
        healthy.sort(Comparator.comparingInt(IPlayer::getOverallRating).reversed());

        if (LineupWarnings.teamHasGkPlayer(team)) {
            List<IPlayer> best = new ArrayList<>();
            IPlayer gk = pickBest(healthy, "GK", new HashSet<>());
            if (gk != null) {
                best.add(gk);
            }
            for (IPlayer player : healthy) {
                if (best.size() >= 11) {
                    break;
                }
                if (!best.contains(player)) {
                    best.add(player);
                }
            }
            return best;
        }

        List<IPlayer> best = new ArrayList<>();
        Set<IPlayer> used = new HashSet<>();
        IPlayer setter = pickBest(healthy, "SETTER", used);
        IPlayer libero = pickBest(healthy, "LIBERO", used);
        if (setter != null) {
            best.add(setter);
            used.add(setter);
        }
        if (libero != null) {
            best.add(libero);
            used.add(libero);
        }
        for (IPlayer player : healthy) {
            if (best.size() >= 6) {
                break;
            }
            if (!used.contains(player)) {
                best.add(player);
                used.add(player);
            }
        }
        return best;
    }

    private IPlayer pickBest(List<IPlayer> players, String position, Set<IPlayer> used) {
        for (IPlayer player : players) {
            if (!used.contains(player) && position.equalsIgnoreCase(player.getPosition())) {
                return player;
            }
        }
        return null;
    }

    private String tacticSummary(ITactic tactic) {
        if (tactic.getAttackModifier() > tactic.getDefenseModifier()) {
            return "one alan oyun";
        }
        if (tactic.getDefenseModifier() > tactic.getAttackModifier()) {
            return "sonucu korur";
        }
        return "dengeli yapi";
    }

    private void applyPlayStyle(ITeam team, SportFactory factory, PlayStyle style) {
        if (team == null || factory == null || style == null) {
            return;
        }
        ITactic selected = null;
        for (ITactic tactic : factory.getAvailableTactics()) {
            if (selected == null) {
                selected = tactic;
            }
            if (style == PlayStyle.HUCUM && tactic.getAttackModifier() > selected.getAttackModifier()) {
                selected = tactic;
            } else if (style == PlayStyle.DEFANS && tactic.getDefenseModifier() > selected.getDefenseModifier()) {
                selected = tactic;
            } else if (style == PlayStyle.DENGELI
                    && Math.abs(tactic.getAttackModifier() - tactic.getDefenseModifier())
                    < Math.abs(selected.getAttackModifier() - selected.getDefenseModifier())) {
                selected = tactic;
            }
        }
        if (selected != null) {
            team.setTactic(selected);
        }
    }

    private TeamPreview previewTeam(String sportName, LeaguePreset preset, String teamName) {
        if (sportName == null || preset == null || teamName == null) {
            return null;
        }
        try {
            GameSetupService.SetupResult result = setupPreviewResult(sportName, preset, teamName);
            ITeam team = result.getPlayerTeam();
            int overall = team.getTeamOverallRating();
            PlayStyle style = recommendStyle(team, registry.getFactory(sportName));
            return new TeamPreview(overall, style);
        } catch (RuntimeException e) {
            return null;
        }
    }

    private GameSetupService.SetupResult setupPreviewResult(String sportName, LeaguePreset preset, String teamName) {
        String key = sportName + "|" + preset.getLeagueName() + "|" + teamName;
        return setupPreviewCache.computeIfAbsent(key, ignored -> setupService.createGame(sportName, preset, teamName));
    }

    private List<CoachProfile> coachProfiles(String sportName) {
        List<CoachProfile> profiles = new ArrayList<>();
        profiles.add(new CoachProfile(
            "Hucum Ustasi",
            "ATTACKING",
            8,
            "Skor uretimini ve one alan oyuncularin gelisimini hizlandirir."
        ));
        profiles.add(new CoachProfile(
            "Savunma Mimari",
            "DEFENSIVE",
            8,
            "Takimin geride daha saglam kalmasina ve savunma oyuncularinin gelisimine odaklanir."
        ));
        profiles.add(new CoachProfile(
            "Kondisyon Uzmani",
            "FITNESS",
            7,
            "Tempo, dayaniklilik ve sakatlik sonrasi toparlanma icin guvenli profildir."
        ));
        return profiles;
    }

    private boolean profileMatchesRecommendation(CoachProfile profile, PlayStyle style) {
        if (style == PlayStyle.HUCUM) {
            return "ATTACKING".equalsIgnoreCase(profile.specialty);
        }
        if (style == PlayStyle.DEFANS) {
            return "DEFENSIVE".equalsIgnoreCase(profile.specialty);
        }
        return "FITNESS".equalsIgnoreCase(profile.specialty);
    }

    private String profileEffectText(String sportName, CoachProfile profile) {
        if ("Volleyball".equalsIgnoreCase(sportName)) {
            if ("ATTACKING".equalsIgnoreCase(profile.specialty)) {
                return "smac + servis";
            }
            if ("DEFENSIVE".equalsIgnoreCase(profile.specialty)) {
                return "blok + manset";
            }
            return "dayaniklilik";
        }
        if ("ATTACKING".equalsIgnoreCase(profile.specialty)) {
            return "sut + hiz";
        }
        if ("DEFENSIVE".equalsIgnoreCase(profile.specialty)) {
            return "savunma + kafa";
        }
        return "dayaniklilik";
    }

    private String coachSpecialtyLabel(String specialty) {
        if ("ATTACKING".equalsIgnoreCase(specialty)) {
            return "Hucum";
        }
        if ("DEFENSIVE".equalsIgnoreCase(specialty)) {
            return "Savunma";
        }
        if ("FITNESS".equalsIgnoreCase(specialty)) {
            return "Kondisyon";
        }
        if ("BALANCED".equalsIgnoreCase(specialty)) {
            return "Dengeli";
        }
        return specialty == null || specialty.isBlank() ? "Belirsiz" : specialty;
    }

    private PlayStyle recommendStyle(ITeam team, SportFactory factory) {
        if (team == null || factory == null) {
            return PlayStyle.DENGELI;
        }

        double attack = averageAttributes(team, attackKeys(factory));
        double defense = averageAttributes(team, defenseKeys(factory));

        if (attack >= defense + 4) {
            return PlayStyle.HUCUM;
        }
        if (defense >= attack + 4) {
            return PlayStyle.DEFANS;
        }
        return PlayStyle.DENGELI;
    }

    private List<String> attackKeys(SportFactory factory) {
        String sport = factory.createSport().getSportName();
        if ("Volleyball".equalsIgnoreCase(sport)) {
            return List.of("spike", "serve", "set");
        }
        return List.of("shooting", "pace", "passing");
    }

    private List<String> defenseKeys(SportFactory factory) {
        String sport = factory.createSport().getSportName();
        if ("Volleyball".equalsIgnoreCase(sport)) {
            return List.of("block", "receive", "stamina");
        }
        return List.of("defending", "heading", "stamina");
    }

    private double averageAttributes(ITeam team, List<String> keys) {
        int total = 0;
        int count = 0;
        for (IPlayer player : team.getStartingLineup().isEmpty() ? team.getSquad() : team.getStartingLineup()) {
            for (String key : keys) {
                total += player.getAttributes().getOrDefault(key, 50);
                count++;
            }
        }
        return count == 0 ? 50 : total / (double) count;
    }

    private String playStyleLabel(PlayStyle style) {
        if (style == PlayStyle.HUCUM) {
            return "Hucum";
        }
        if (style == PlayStyle.DEFANS) {
            return "Defans";
        }
        return "Dengeli";
    }

    private String turkishSport(String sport) {
        if (sport == null) {
            return "-";
        }
        if ("football".equalsIgnoreCase(sport) || "Football".equalsIgnoreCase(sport)) {
            return "Futbol";
        }
        if ("volleyball".equalsIgnoreCase(sport) || "Volleyball".equalsIgnoreCase(sport)) {
            return "Voleybol";
        }
        return sport;
    }

    private String turkishTactic(String tactic) {
        if (tactic == null) {
            return "-";
        }
        if ("OFFENSIVE".equalsIgnoreCase(tactic)) {
            return "Hucum";
        }
        if ("BALANCED".equalsIgnoreCase(tactic)) {
            return "Dengeli";
        }
        if ("DEFENSIVE".equalsIgnoreCase(tactic)) {
            return "Defans";
        }
        return tactic;
    }

    private String turkishWarning(String warning) {
        if (warning == null) {
            return "";
        }
        if (warning.startsWith("Starting lineup is empty")) {
            return "Ilk kadro bos - oyuncu secilmemis.";
        }
        if (warning.startsWith("Injured player in lineup:")) {
            return warning.replace("Injured player in lineup:", "Ilk kadroda sakat oyuncu:")
                .replace("wk remaining", "hafta kaldi");
        }
        if (warning.startsWith("No goalkeeper")) {
            return "Ilk kadroda kaleci (GK) yok.";
        }
        if (warning.startsWith("Lineup has")) {
            return warning.replace("Lineup has", "Kadroda")
                .replace("players (expected", "oyuncu var (beklenen")
                .replace(").", ").");
        }
        return warning;
    }

    private Label bigText(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setStyle("-fx-text-fill: " + INK + "; -fx-font-size: 20px; -fx-font-weight: 900;");
        return label;
    }

    private Label muted(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setStyle("-fx-text-fill: " + MUTED + "; -fx-font-size: 13px;");
        return label;
    }

    private Label good(String text) {
        Label label = muted(text);
        label.setTextFill(javafx.scene.paint.Color.web(GREEN));
        return label;
    }

    private Label bad(String text) {
        Label label = muted(text);
        label.setTextFill(javafx.scene.paint.Color.web(RED));
        return label;
    }

    private Label rowLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setStyle("-fx-background-color: #F6FAFC; -fx-background-radius: 8;"
            + "-fx-padding: 10; -fx-text-fill: " + INK + ";");
        return label;
    }

    private HBox metricRow(String label, String value) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        Label left = muted(label);
        left.setMinWidth(120);
        Label right = new Label(value == null ? "-" : value);
        right.setWrapText(true);
        right.setStyle("-fx-text-fill: " + INK + "; -fx-font-weight: 800;");
        row.getChildren().addAll(left, right);
        return row;
    }

    private Node progress(String label, int percent, String note, String color) {
        VBox box = new VBox(5);
        HBox meta = new HBox(8);
        meta.setAlignment(Pos.CENTER_LEFT);
        Label l = muted(label);
        Label p = new Label(Math.max(0, Math.min(100, percent)) + "%");
        p.setStyle("-fx-text-fill: " + INK + "; -fx-font-weight: 900;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        meta.getChildren().addAll(l, spacer, p);

        StackPane track = new StackPane();
        track.setMinHeight(10);
        track.setMaxHeight(10);
        track.setStyle("-fx-background-color: #E5EDF3; -fx-background-radius: 999;");
        Region fill = new Region();
        fill.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 999;");
        fill.prefWidthProperty().bind(track.widthProperty().multiply(Math.max(0, Math.min(100, percent)) / 100.0));
        StackPane.setAlignment(fill, Pos.CENTER_LEFT);
        track.getChildren().add(fill);
        box.getChildren().addAll(meta, track, muted(note));
        return box;
    }

    private Label banner(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setStyle("-fx-background-color: #E7F8F5; -fx-text-fill: #075B51;"
            + "-fx-background-radius: 10; -fx-padding: 12; -fx-font-weight: 800;");
        return label;
    }

    private Region spacer(double height) {
        Region region = new Region();
        region.setPrefHeight(height);
        return region;
    }

    private <T> TableColumn<T, String> col(String title, java.util.function.Function<T, String> getter) {
        TableColumn<T, String> column = new TableColumn<>(title);
        column.setCellValueFactory(data -> new SimpleStringProperty(getter.apply(data.getValue())));
        return column;
    }

    private void styleTable(TableView<?> table) {
        table.setStyle("-fx-background-color: white; -fx-border-color: " + LINE + "; -fx-border-radius: 10;");
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private ListView<IPlayer> playerList(List<IPlayer> players) {
        ListView<IPlayer> list = new ListView<>(FXCollections.observableArrayList(players));
        list.setMinHeight(360);
        list.setCellFactory(view -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(IPlayer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.getPosition() + "  " + item.getName() + "  Guc " + item.getOverallRating()
                        + (item.isInjured() ? "  SAKAT " + item.getInjuryGamesRemaining() + " hafta" : ""));
                    setStyle(item.isInjured()
                        ? "-fx-background-color: #FCECEF; -fx-text-fill: " + RED + "; -fx-font-weight: 800;"
                        : "");
                }
            }
        });
        return list;
    }

    private String injuryReturnText(int weeks) {
        if (weeks <= 1) {
            return "gelecek hafta donebilir";
        }
        return weeks + " hafta sonra donus bekleniyor";
    }

    private String compareValues(int ours, int theirs) {
        int diff = ours - theirs;
        String marker = diff > 0 ? " avantaj " : diff < 0 ? " geride " : " esit ";
        return ours + " - " + theirs + marker + "(" + signed(diff) + ")";
    }

    private String timelineText(String note) {
        if (note == null || note.isBlank()) {
            return "-";
        }
        if (note.toLowerCase().contains("tamamlandi")) {
            return note.replace("Degisiklik yapmak icin kadro panelini kullanabilirsin.",
                "Kadro degisikligi icin panel acik.");
        }
        if (note.contains("GOL!") || note.toLowerCase().contains("set sonucu")
                || note.toLowerCase().contains("devre sonunda")
                || note.toLowerCase().contains("setlerde durum")) {
            return note;
        }
        if (note.toLowerCase().contains("sakatlandi")) {
            return "Saglik: " + note;
        }
        if (note.toLowerCase().contains("formu")) {
            return "Form: " + note;
        }
        return "Olay: " + note;
    }

    private boolean isTimelineScoreNote(String note) {
        String value = note == null ? "" : note.toLowerCase();
        return value.contains("sonucu")
            || value.contains("sonunda")
            || value.contains("setlerde durum");
    }

    private Map<String, List<String>> groupedTimelineNotes(List<String> notes) {
        Map<String, List<String>> grouped = new LinkedHashMap<>();
        String current = "Genel";
        for (String note : notes) {
            String key = timelineGroupKey(note);
            if (key != null) {
                current = key;
            }
            grouped.computeIfAbsent(current, ignored -> new ArrayList<>()).add(note);
        }
        return grouped;
    }

    private String timelineGroupKey(String note) {
        String value = note == null ? "" : note.toLowerCase();
        for (int i = 1; i <= 5; i++) {
            if (value.startsWith(i + ". set") || value.contains(i + ". set")) {
                return i + ". Set";
            }
            if (value.startsWith(i + ". devre") || value.contains(i + ". devre")) {
                return i + ". Devre";
            }
        }
        return null;
    }

    private List<IPlayer> allPlayers(ILeague league) {
        List<IPlayer> players = new ArrayList<>();
        for (ITeam team : league.getTeams()) {
            players.addAll(team.getSquad());
        }
        return players;
    }

    private ITeam bestTeamBy(ILeague league, boolean attack) {
        ITeam best = null;
        for (ITeam team : league.getTeams()) {
            if (best == null) {
                best = team;
                continue;
            }
            if (attack && goalsFor(league, team) > goalsFor(league, best)) {
                best = team;
            } else if (!attack && goalsAgainst(league, team) < goalsAgainst(league, best)) {
                best = team;
            }
        }
        return best;
    }

    private Map<IPlayer, Map<String, Integer>> snapshotAttributes(ITeam team) {
        Map<IPlayer, Map<String, Integer>> snapshot = new HashMap<>();
        for (IPlayer player : team.getSquad()) {
            snapshot.put(player, new HashMap<>(player.getAttributes()));
        }
        return snapshot;
    }

    private TrainingReport buildTrainingReport(int week, ITeam team, Map<IPlayer, Map<String, Integer>> before) {
        List<String> improvements = new ArrayList<>();
        for (IPlayer player : team.getSquad()) {
            Map<String, Integer> oldValues = before.get(player);
            if (oldValues == null) {
                continue;
            }
            List<String> deltas = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : player.getAttributes().entrySet()) {
                int delta = entry.getValue() - oldValues.getOrDefault(entry.getKey(), entry.getValue());
                if (delta > 0) {
                    deltas.add(entry.getKey() + " " + signed(delta));
                }
            }
            if (!deltas.isEmpty()) {
                improvements.add(player.getName() + " - " + String.join(", ", deltas));
            }
        }
        return new TrainingReport(week, improvements);
    }

    private Map<String, List<String>> groupedNews(List<String> news) {
        Map<String, List<String>> grouped = new LinkedHashMap<>();
        grouped.put("Mac ve Skor", new ArrayList<>());
        grouped.put("Antrenman", new ArrayList<>());
        grouped.put("Saglik", new ArrayList<>());
        grouped.put("Lig Guncel", new ArrayList<>());
        grouped.put("Genel", new ArrayList<>());
        for (String item : news) {
            grouped.get(newsCategory(item)).add(item);
        }
        grouped.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        return grouped;
    }

    private String newsCategory(String news) {
        String value = news == null ? "" : news.toLowerCase();
        if (value.contains("sakat")) {
            return "Saglik";
        }
        if (value.contains("antrenman") || value.contains("gelisim")) {
            return "Antrenman";
        }
        if (value.contains("lider") || value.contains("siralamada") || value.contains("sezon")) {
            return "Lig Guncel";
        }
        if (value.contains("hafta") && (value.contains(":") || value.contains("-"))) {
            return "Mac ve Skor";
        }
        return "Genel";
    }

    private boolean hasGame() {
        GameContext ctx = GameContext.getInstance();
        return ctx.getSport() != null && ctx.getLeague() != null && ctx.getPlayerTeam() != null;
    }

    private IMatch findPlayerMatch(ILeague league, ITeam team, int week) {
        for (IMatch match : league.getFixturesForWeek(week)) {
            if (match.getHomeTeam().equals(team) || match.getAwayTeam().equals(team)) {
                return match;
            }
        }
        return null;
    }

    private List<IPlayer> benchPlayers(ITeam team) {
        List<IPlayer> bench = new ArrayList<>();
        for (IPlayer player : team.getSquad()) {
            if (!team.getStartingLineup().contains(player)) {
                bench.add(player);
            }
        }
        return bench;
    }

    private int morale(ITeam team) {
        if (team.getSquad().isEmpty()) {
            return 50;
        }
        int formSum = 0;
        for (IPlayer player : team.getSquad()) {
            formSum += player.getForm();
        }
        int formScore = (int) Math.round((formSum / (team.getSquad().size() * 3.0)) * 60);
        int healthScore = 40 - Math.min(40, injuredCount(team) * 8);
        return Math.max(0, Math.min(100, formScore + healthScore));
    }

    private int health(ITeam team) {
        if (team.getSquad().isEmpty()) {
            return 100;
        }
        return (int) Math.round(((team.getSquad().size() - injuredCount(team)) * 100.0) / team.getSquad().size());
    }

    private int injuredCount(ITeam team) {
        int count = 0;
        for (IPlayer player : team.getSquad()) {
            if (player.isInjured()) {
                count++;
            }
        }
        return count;
    }

    private int totalInjured(ILeague league) {
        int count = 0;
        for (ITeam team : league.getTeams()) {
            count += injuredCount(team);
        }
        return count;
    }

    private int played(ILeague league, ITeam team) {
        return league.getWins(team) + league.getDraws(team) + league.getLosses(team);
    }

    private int goalsFor(ILeague league, ITeam team) {
        int value = 0;
        for (IMatch match : league.getAllFixtures()) {
            MatchResult result = match.getResult();
            if (result == null) {
                continue;
            }
            if (result.getHomeTeam().equals(team)) {
                value += result.getHomeScore();
            } else if (result.getAwayTeam().equals(team)) {
                value += result.getAwayScore();
            }
        }
        return value;
    }

    private int goalsAgainst(ILeague league, ITeam team) {
        int value = 0;
        for (IMatch match : league.getAllFixtures()) {
            MatchResult result = match.getResult();
            if (result == null) {
                continue;
            }
            if (result.getHomeTeam().equals(team)) {
                value += result.getAwayScore();
            } else if (result.getAwayTeam().equals(team)) {
                value += result.getHomeScore();
            }
        }
        return value;
    }

    private String rankText(ILeague league, ITeam team) {
        int rank = league.getStandings().getRankOf(team);
        return rank + ".";
    }

    private String moraleText(int morale) {
        if (morale >= 75) {
            return "guvenli";
        }
        if (morale >= 45) {
            return "dengeli";
        }
        return "dikkat istiyor";
    }

    private String signed(int value) {
        return value > 0 ? "+" + value : String.valueOf(value);
    }

    private String exitSlotLabel(int slot, SaveSummary summary) {
        if (summary == null) {
            return "Slot " + slot + " - Bos";
        }
        return "Slot " + slot + " - " + summary.team + " / " + summary.league
            + " / Sezon " + summary.season + ", Hafta " + summary.week;
    }

    private int parseSlotNumber(String label) {
        if (label == null || !label.startsWith("Slot ")) {
            return -1;
        }
        int end = label.indexOf(' ', 5);
        String raw = end == -1 ? label.substring(5) : label.substring(5, end);
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private SaveSummary readSaveSummary(String path) {
        File file = SaveLoadService.resolveSaveFile(path);
        if (!file.exists()) {
            return null;
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            GameSaveData data = (GameSaveData) in.readObject();
            return new SaveSummary(
                data.getSportName(),
                data.getLeagueName(),
                data.getPlayerTeamName(),
                data.getCurrentSeason(),
                data.getCurrentWeek()
            );
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            return null;
        }
    }

    private static class TrainingReport {
        private final int week;
        private final List<String> improvements;

        TrainingReport(int week, List<String> improvements) {
            this.week = week;
            this.improvements = improvements;
        }
    }

    private static class MatchReport {
        private final int week;
        private final MatchResult result;
        private final List<String> events;
        private final int newInjuries;

        MatchReport(int week, MatchResult result, List<String> events, int newInjuries) {
            this.week = week;
            this.result = result;
            this.events = events;
            this.newInjuries = newInjuries;
        }
    }

    private static class MatchFlow {
        private final int week;
        private final int totalPeriods;
        private final List<String> notes = new ArrayList<>();
        private int currentPeriod;
        private int homeScore;
        private int awayScore;
        private boolean complete;

        MatchFlow(int week, int totalPeriods) {
            this.week = week;
            this.totalPeriods = totalPeriods;
            this.currentPeriod = 0;
            this.homeScore = 0;
            this.awayScore = 0;
        }
    }

    private static class SaveSummary {
        private final String sport;
        private final String league;
        private final String team;
        private final int season;
        private final int week;

        SaveSummary(String sport, String league, String team, int season, int week) {
            this.sport = sport;
            this.league = league;
            this.team = team;
            this.season = season;
            this.week = week;
        }
    }

    private static class CoachProfile {
        private final String name;
        private final String specialty;
        private final int quality;
        private final String description;

        CoachProfile(String name, String specialty, int quality, String description) {
            this.name = name;
            this.specialty = specialty;
            this.quality = quality;
            this.description = description;
        }
    }


    private static final class TeamPreview {
        final int overall;
        final PlayStyle recommendedStyle;

        TeamPreview(int overall, PlayStyle recommendedStyle) {
            this.overall = overall;
            this.recommendedStyle = recommendedStyle;
        }
    }

    private static class ProfileCoach implements ICoach, Serializable {
        private static final long serialVersionUID = 1L;

        private final String name;
        private final String specialty;
        private final int quality;
        private final String sportName;

        ProfileCoach(CoachProfile profile, String sportName) {
            this.name = profile.name;
            this.specialty = profile.specialty;
            this.quality = profile.quality;
            this.sportName = sportName;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getSpecialty() {
            return specialty;
        }

        @Override
        public int getQuality() {
            return quality;
        }

        @Override
        public void trainPlayers(List<IPlayer> players) {
            if (players == null) {
                return;
            }
            int bonus = quality / 5;
            for (IPlayer player : players) {
                if (player == null || player.isInjured()) {
                    continue;
                }
                if ("Volleyball".equalsIgnoreCase(sportName)) {
                    trainVolleyballPlayer(player, bonus);
                } else {
                    trainFootballPlayer(player, bonus);
                }
            }
        }

        private void trainFootballPlayer(IPlayer player, int bonus) {
            if ("ATTACKING".equalsIgnoreCase(specialty)) {
                player.train("shooting", 2 + bonus);
                player.train("pace", 1 + bonus);
            } else if ("DEFENSIVE".equalsIgnoreCase(specialty)) {
                player.train("defending", 2 + bonus);
                player.train("heading", 1 + bonus);
            } else {
                player.train("stamina", 2 + bonus);
            }
        }

        private void trainVolleyballPlayer(IPlayer player, int bonus) {
            if ("ATTACKING".equalsIgnoreCase(specialty)) {
                player.train("spike", 2 + bonus);
                player.train("serve", 1 + bonus);
            } else if ("DEFENSIVE".equalsIgnoreCase(specialty)) {
                player.train("block", 2 + bonus);
                player.train("reception", 1 + bonus);
            } else {
                player.train("serve", 1 + bonus);
                player.train("reception", 1 + bonus);
            }
        }
    }
}
