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
    if (startingLineup.contains(in)) {
        return;
    }
    if (in.isInjured()) {
        return;
    }

    List<IPlayer> candidateLineup = new ArrayList<>(startingLineup);
    int index = candidateLineup.indexOf(out);
    candidateLineup.set(index, in);

    if (validateLineup(candidateLineup)) {
        this.startingLineup = candidateLineup;
    }
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

    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public void resetPoints() {
        this.points = 0;
    }

    @Override
    public int getTeamOverallRating() {
        List<IPlayer> lineup = startingLineup.isEmpty() ? squad : startingLineup;
        if (lineup.isEmpty()) return 0;
        int sum = 0;
        for (IPlayer player : lineup) {
            sum += player.getOverallRating();
        }
        return sum / lineup.size();
    }

    public void clearSquad() {
        this.squad.clear();
        this.startingLineup.clear();
    }

    public void clearStartingLineup() {
        this.startingLineup.clear();
    }

    public void autoFixLineup() {
        List<IPlayer> currentLineup = new ArrayList<>(startingLineup);
        boolean changed = false;

        for (int i = 0; i < currentLineup.size(); i++) {
            IPlayer player = currentLineup.get(i);
            if (player.isInjured()) {
                // First try to find a position-matched bench player
                IPlayer replacement = findHealthyBenchPlayer(player.getPosition(), currentLineup);
                // Fall back to any healthy bench player if no positional match
                if (replacement == null) {
                    replacement = findHealthyBenchPlayer(null, currentLineup);
                }
                if (replacement != null) {
                    currentLineup.set(i, replacement);
                    changed = true;
                }
            }
        }

        if (changed && validateLineup(currentLineup)) {
            this.startingLineup = currentLineup;
        }
    }

    /**
     * Finds a healthy bench player (not in {@code currentLineup}).
     * If {@code requiredPosition} is non-null, only players matching that
     * position are considered; otherwise any healthy bench player is returned.
     */
    private IPlayer findHealthyBenchPlayer(String requiredPosition, List<IPlayer> currentLineup) {
        for (IPlayer player : squad) {
            if (!player.isInjured() && !currentLineup.contains(player)) {
                if (requiredPosition == null || requiredPosition.equals(player.getPosition())) {
                    return player;
                }
            }
        }
        return null;
    }

    public abstract boolean validateLineup(List<IPlayer> chosen);
}
