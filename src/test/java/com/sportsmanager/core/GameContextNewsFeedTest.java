package com.sportsmanager.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the GameContext news-feed functionality.
 *
 * Uses reflection to reset the singleton between tests so each test
 * starts with a clean state.
 */
class GameContextNewsFeedTest {

    @BeforeEach
    void resetSingleton() throws Exception {
        Field instance = GameContext.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    void newsFeedIsEmptyOnFreshInstance() {
        GameContext ctx = GameContext.getInstance();
        assertTrue(ctx.getNewsFeed().isEmpty());
    }

    @Test
    void addNewsSavesItem() {
        GameContext ctx = GameContext.getInstance();
        ctx.addNews("Team A won!");
        assertEquals(1, ctx.getNewsFeed().size());
        assertEquals("Team A won!", ctx.getNewsFeed().get(0));
    }

    @Test
    void addNewsIgnoresBlankAndNull() {
        GameContext ctx = GameContext.getInstance();
        ctx.addNews(null);
        ctx.addNews("");
        ctx.addNews("   ");
        assertTrue(ctx.getNewsFeed().isEmpty());
    }

    @Test
    void getRecentNewsReturnsLastN() {
        GameContext ctx = GameContext.getInstance();
        for (int i = 1; i <= 10; i++) {
            ctx.addNews("Item " + i);
        }
        List<String> recent = ctx.getRecentNews(3);
        assertEquals(3, recent.size());
        assertEquals("Item 8",  recent.get(0));
        assertEquals("Item 9",  recent.get(1));
        assertEquals("Item 10", recent.get(2));
    }

    @Test
    void getRecentNewsReturnsAllWhenFewerThanRequested() {
        GameContext ctx = GameContext.getInstance();
        ctx.addNews("Only one");
        List<String> recent = ctx.getRecentNews(5);
        assertEquals(1, recent.size());
    }

    @Test
    void getRecentNewsReturnsEmptyForNonPositiveCount() {
        GameContext ctx = GameContext.getInstance();
        ctx.addNews("Item 1");
        ctx.addNews("Item 2");

        assertTrue(ctx.getRecentNews(0).isEmpty());
        assertTrue(ctx.getRecentNews(-3).isEmpty());
    }

    @Test
    void clearNewsEmptiesFeed() {
        GameContext ctx = GameContext.getInstance();
        ctx.addNews("Item 1");
        ctx.addNews("Item 2");
        ctx.clearNews();
        assertTrue(ctx.getNewsFeed().isEmpty());
    }

    @Test
    void feedCapsAtFiftyItems() {
        GameContext ctx = GameContext.getInstance();
        for (int i = 1; i <= 60; i++) {
            ctx.addNews("Item " + i);
        }
        assertEquals(50, ctx.getNewsFeed().size());
        // Oldest items evicted — first item should be "Item 11"
        assertEquals("Item 11", ctx.getNewsFeed().get(0));
    }

    @Test
    void startNewGameClearsNewsFeed() throws Exception {
        GameContext ctx = GameContext.getInstance();
        ctx.addNews("Some news");
        // startNewGame requires an ISport — pass null (accepted)
        ctx.startNewGame(null);
        assertTrue(ctx.getNewsFeed().isEmpty());
    }
}
