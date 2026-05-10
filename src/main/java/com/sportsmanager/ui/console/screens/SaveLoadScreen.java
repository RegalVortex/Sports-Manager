package com.sportsmanager.ui.console.screens;

import com.sportsmanager.core.GameContext;
import com.sportsmanager.core.ILeague;
import com.sportsmanager.core.ISport;
import com.sportsmanager.core.ITeam;
import com.sportsmanager.core.SportFactory;
import com.sportsmanager.core.SportRegistry;
import com.sportsmanager.save.LoadedGame;
import com.sportsmanager.save.SaveLoadService;
import com.sportsmanager.ui.console.ConsoleInput;
import com.sportsmanager.ui.console.ConsolePrinter;
import com.sportsmanager.ui.console.Screen;
import com.sportsmanager.ui.console.components.AlertRenderer;
import com.sportsmanager.ui.console.components.HeaderRenderer;
import com.sportsmanager.ui.console.components.MenuRenderer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Local save slot management.
 */
public class SaveLoadScreen implements Screen {

    static final String[] SLOT_FILES = {
        "save_slot1.dat", "save_slot2.dat", "save_slot3.dat"
    };

    private final Screen parent;
    private final SportRegistry registry;
    private int phase;
    private String message;

    public SaveLoadScreen(Screen parent) {
        this(parent, new SportRegistry());
    }

    public SaveLoadScreen(Screen parent, SportRegistry registry) {
        this.parent = parent;
        this.registry = registry;
    }

    @Override
    public void render() {
        HeaderRenderer.render(title(), subtitle());
        renderSlotStatus();
        HeaderRenderer.section("Aksiyonlar");
        if (phase == 0) {
            MenuRenderer.render(List.of("Oyunu Kaydet", "Oyunu Yukle", "Kaydi Sil"), true);
        } else {
            ConsolePrinter.line("  Slot sec: 1-" + SLOT_FILES.length + ".");
            ConsolePrinter.navHint();
        }
        if (message != null) {
            ConsolePrinter.blank();
            AlertRenderer.info(message);
            message = null;
        }
        ConsolePrinter.blank();
        ConsolePrinter.prompt();
    }

    @Override
    public Screen handleInput(String input) {
        if (ConsoleInput.isQuit(input)) {
            return null;
        }
        if (ConsoleInput.isHelp(input)) {
            showHelp();
            return this;
        }
        if (ConsoleInput.isBack(input)) {
            if (phase == 0) {
                return parent;
            }
            phase = 0;
            return this;
        }

        if (phase == 0) {
            return handleAction(input);
        }
        if (phase == 1) {
            return handleSave(input);
        }
        if (phase == 2) {
            return handleLoad(input);
        }
        if (phase == 3) {
            return handleDelete(input);
        }
        return this;
    }

    private Screen handleAction(String input) {
        int choice = ConsoleInput.parseChoice(input);
        if (ConsoleInput.inRange(choice, 1, 3)) {
            phase = choice;
        } else {
            message = "Gecersiz secim. 1-3, 0, H veya Q gir.";
        }
        return this;
    }

    private Screen handleSave(String input) {
        int slot = ConsoleInput.parseChoice(input);
        if (!ConsoleInput.inRange(slot, 1, SLOT_FILES.length)) {
            message = "Gecersiz slot. 1-" + SLOT_FILES.length + " sec.";
            return this;
        }

        GameContext ctx = GameContext.getInstance();
        ISport sport = ctx.getSport();
        ILeague league = ctx.getLeague();
        ITeam team = ctx.getPlayerTeam();
        if (sport == null || league == null || team == null) {
            message = "Kaydedilecek aktif oyun yok.";
            phase = 0;
            return this;
        }

        File file = SaveLoadService.resolveSaveFile(SLOT_FILES[slot - 1]);
        if (file.exists() && !confirm("Slot " + slot + " dolu. Uzerine yazilsin mi? [Y/N]")) {
            message = "Kayit iptal edildi.";
            phase = 0;
            return this;
        }

        SaveLoadService.saveGame(SLOT_FILES[slot - 1], sport, league, team);
        message = "Oyun kaydedildi. Sezon " + league.getCurrentSeason()
            + ", Hafta " + league.getCurrentWeek() + ", " + team.getName() + ".";
        phase = 0;
        return this;
    }

    private Screen handleLoad(String input) {
        int slot = ConsoleInput.parseChoice(input);
        if (!ConsoleInput.inRange(slot, 1, SLOT_FILES.length)) {
            message = "Gecersiz slot. 1-" + SLOT_FILES.length + " sec.";
            return this;
        }
        File file = SaveLoadService.resolveSaveFile(SLOT_FILES[slot - 1]);
        if (!file.exists()) {
            message = "Slot " + slot + " bos.";
            return this;
        }
        if (!confirm("Yukleme mevcut ilerlemeyi degistirir. Devam edilsin mi? [Y/N]")) {
            message = "Yukleme iptal edildi.";
            phase = 0;
            return this;
        }

        LoadedGame loaded = SaveLoadService.loadGame(SLOT_FILES[slot - 1], registry);
        if (loaded == null) {
            message = "Yukleme basarisiz. Kayit bozulmus olabilir.";
            phase = 0;
            return this;
        }

        GameContext ctx = GameContext.getInstance();
        ctx.startNewGame(loaded.getSport());
        ctx.setLeague(loaded.getLeague());
        ctx.setPlayerTeam(loaded.getPlayerTeam());
        String sportName = loaded.getSport().getSportName().toLowerCase();
        SportFactory factory = registry.getFactory(sportName);
        ctx.setSportFactory(factory);

        MainDashboardScreen dashboard = new MainDashboardScreen(registry);
        dashboard.setMessage("Slot " + slot + " yuklendi.");
        return dashboard;
    }

    private Screen handleDelete(String input) {
        int slot = ConsoleInput.parseChoice(input);
        if (!ConsoleInput.inRange(slot, 1, SLOT_FILES.length)) {
            message = "Gecersiz slot. 1-" + SLOT_FILES.length + " sec.";
            return this;
        }
        File file = SaveLoadService.resolveSaveFile(SLOT_FILES[slot - 1]);
        if (!file.exists()) {
            message = "Slot " + slot + " zaten bos.";
            phase = 0;
            return this;
        }
        if (!confirm("Slot " + slot + " kalici olarak silinsin mi? [Y/N]")) {
            message = "Silme iptal edildi.";
            phase = 0;
            return this;
        }
        message = file.delete() ? "Slot " + slot + " silindi." : "Slot " + slot + " silinemedi.";
        phase = 0;
        return this;
    }

    private boolean confirm(String prompt) {
        AlertRenderer.confirmPrompt(prompt);
        ConsolePrinter.blank();
        ConsolePrinter.prompt();
        return AlertRenderer.isYes(ConsoleInput.readLine());
    }

    private void renderSlotStatus() {
        for (int i = 0; i < SLOT_FILES.length; i++) {
            File file = SaveLoadService.resolveSaveFile(SLOT_FILES[i]);
            String status = "Bos";
            if (file.exists()) {
                status = "Kayitli " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(file.lastModified()));
            }
            ConsolePrinter.keyValue("Slot " + (i + 1), status);
        }
    }

    private String title() {
        switch (phase) {
            case 1:
                return "Oyunu Kaydet";
            case 2:
                return "Oyunu Yukle";
            case 3:
                return "Kaydi Sil";
            default:
                return "Kaydet / Yukle";
        }
    }

    private String subtitle() {
        switch (phase) {
            case 1:
                return "Mevcut kariyerin kaydedilecegi slotu sec";
            case 2:
                return "Geri yuklenecek kaydi sec";
            case 3:
                return "Silinecek kaydi sec";
            default:
                return "Uc yerel kayit slotu kullanilir";
        }
    }

    private void showHelp() {
        HeaderRenderer.section("Kaydet / Yukle Yardimi");
        ConsolePrinter.line("  Kaydet mevcut sezonu, haftayi, takimi, kadroyu, puanlari ve fiksturu yazar.");
        ConsolePrinter.line("  Yukle onaydan sonra aktif oyunu degistirir.");
        ConsolePrinter.line("  Sil sadece secili yerel kayit slotunu kaldirir.");
        ConsolePrinter.blank();
    }
}
