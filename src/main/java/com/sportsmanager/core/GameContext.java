package com.sportsmanager.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Singleton that holds the active game state.
 *
 * Season number is the single source of truth stored in the league
 * (AbstractLeague.currentSeason).  GameContext delegates to it so that
 * there is no duplicate counter that can drift.
 *
 * Also manages the in-game news feed (max 50 items, FIFO eviction).
 */
public class GameContext {

    private static final int MAX_NEWS = 50;

    private static GameContext instance;
    private ISport activeSport;
    private ILeague league;
    private ITeam playerTeam;
    private SportFactory sportFactory;
    private final List<String> newsFeed = new ArrayList<>();

    private GameContext() {
    }

    public static GameContext getInstance() {
        if (instance == null) {
            instance = new GameContext();
        }
        return instance;
    }

    public void startNewGame(ISport sport) {
        this.activeSport = sport;
        this.league = null;
        this.playerTeam = null;
        this.sportFactory = null;
        this.newsFeed.clear();
    }

    public SportFactory getSportFactory() {
        return sportFactory;
    }

    public void setSportFactory(SportFactory factory) {
        this.sportFactory = factory;
    }

    // ── News feed ─────────────────────────────────────────────────

    /**
     * Append a news item. If the feed exceeds MAX_NEWS entries the
     * oldest item is removed first.
     */
    public void addNews(String news) {
        if (news == null || news.isBlank()) {
            return;
        }
        if (newsFeed.size() >= MAX_NEWS) {
            newsFeed.remove(0);
        }
        newsFeed.add(news);
    }

    /** Return an unmodifiable view of all news items (oldest first). */
    public List<String> getNewsFeed() {
        return Collections.unmodifiableList(newsFeed);
    }

    /**
     * Return the most recent {@code count} news items (oldest-of-recent first).
     * If there are fewer than {@code count} items the whole feed is returned.
     */
    public List<String> getRecentNews(int count) {
        if (count <= 0) {
            return Collections.emptyList();
        }
        int start = Math.max(0, newsFeed.size() - count);
        return Collections.unmodifiableList(newsFeed.subList(start, newsFeed.size()));
    }

    /** Clear the news feed (call on new season). */
    public void clearNews() {
        newsFeed.clear();
    }

    public ISport getSport() {
        return activeSport;
    }

    public void setSport(ISport sport) {
        this.activeSport = sport;
    }

    public ILeague getLeague() {
        return league;
    }

    public void setLeague(ILeague league) {
        this.league = league;
    }

    public ITeam getPlayerTeam() {
        return playerTeam;
    }

    public void setPlayerTeam(ITeam playerTeam) {
        this.playerTeam = playerTeam;
    }

    /**
     * Returns the current season number.
     * Delegates to the active league so there is exactly one counter.
     * Falls back to 1 if no league is set yet.
     */
    public int getCurrentSeason() {
        return league != null ? league.getCurrentSeason() : 1;
    }

    /**
     * @deprecated Season is now advanced exclusively by
     *             {@code AbstractLeague.resetSeason()}.
     *             Calling this method is a no-op to prevent double-increment.
     */
    @Deprecated
    public void nextSeason() {
        // intentionally empty — season counter lives in AbstractLeague
    }
}
