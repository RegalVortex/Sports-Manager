package com.sportsmanager.ui.console.screens;

import com.sportsmanager.core.GameContext;
import com.sportsmanager.ui.console.ConsoleInput;
import com.sportsmanager.ui.console.ConsolePrinter;
import com.sportsmanager.ui.console.Screen;
import com.sportsmanager.ui.console.components.HeaderRenderer;

import java.util.List;

/**
 * Shows the complete news feed, newest first.
 */
public class NewsScreen implements Screen {

    private final Screen parent;

    public NewsScreen(Screen parent) {
        this.parent = parent;
    }

    @Override
    public void render() {
        GameContext ctx = GameContext.getInstance();
        List<String> news = ctx.getNewsFeed();

        HeaderRenderer.render("Son Haberler",
            news.size() + " haber");
        ConsolePrinter.blank();

        if (news.isEmpty()) {
            ConsolePrinter.info("Henuz haber yok. Haber uretmek icin sonraki haftayi oyna.");
        } else {
            for (int i = news.size() - 1; i >= 0; i--) {
                ConsolePrinter.line("  " + news.get(i));
            }
        }

        ConsolePrinter.blank();
        ConsolePrinter.navHint();
        ConsolePrinter.blank();
        ConsolePrinter.prompt();
    }

    @Override
    public Screen handleInput(String input) {
        if (ConsoleInput.isQuit(input)) {
            return null;
        }
        if (ConsoleInput.isHelp(input)) {
            ConsolePrinter.blank();
            ConsolePrinter.line("  Haber Yardimi: panel son 3 haberi, bu ekran tum haber akisini gosterir.");
            ConsolePrinter.blank();
            return this;
        }
        return parent;
    }
}
