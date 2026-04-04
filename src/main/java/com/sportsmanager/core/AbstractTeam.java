package com.sportsmanager.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractTeam implements ITeam {

    protected String name;
    protected String logoPath;
    protected List<IPlayer> squad;
    protected List<IPlayer> startingLineup;
    protected ITactic tactic;
    protected ICoach coach;
    protected int points;

    public AbstractTeam(String name, String logoPath) {
        this.name = name;
        this.logoPath = logoPath;
        this.squad = new ArrayList<>();
        this.startingLineup = new ArrayList<>();
        this.points = 0;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getLogoPath() {
        return logoPath;
    }

    @Override
    public List<IPlayer> getSquad() {
        return Collections.unmodifiableList(squad);
    }

    @Override
    public List<IPlayer> getStartingLineup() {
        return Collections.unmodifiableList(startingLineup);
    }

    @Override
    public ICoach getCoach() {
        return coach;
    }

    public void setCoach(ICoach coach) {
        this.coach = coach;
    }

    @Override
    public ITactic getTactic() {
        return tactic;
    }

    @Override
    public void setTactic(ITactic tactic) {
        this.tactic = tactic;
    }

    @Override
    public void substitutePlayer(IPlayer out, IPlayer in) {
        if (out == null || in == null) {
            return;
        }
        if (!startingLineup.contains(out)) {
            return;
        }
        if (!squad.contains(in)) {
            return;
        }
        if (in.isInjured()) {
            return;
        }

        int index = startingLineup.indexOf(out);
        startingLineup.set(index, in);
    }

    @Override
    public void addPoints(int points) {
        this.points += points;
    }

    @Override
    public int getPoints() {
        return points;
    }

    public void addPlayerToSquad(IPlayer player) {
        if (player != null && !squad.contains(player)) {
            squad.add(player);
        }
    }

    public List<IPlayer> getAvailablePlayers() {
        List<IPlayer> available = new ArrayList<>();
        for (IPlayer player : squad) {
            if (!player.isInjured()) {
                available.add(player);
            }
        }
        return available;
    }

    public void setStartingLineup(List<IPlayer> lineup) {
        if (lineup == null) {
            return;
        }
        if (validateLineup(lineup)) {
            this.startingLineup = new ArrayList<>(lineup);
        }
    }

    public abstract boolean validateLineup(List<IPlayer> chosen);
}
