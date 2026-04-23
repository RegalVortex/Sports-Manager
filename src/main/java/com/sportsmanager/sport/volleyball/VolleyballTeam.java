package com.sportsmanager.sport.volleyball;

import com.sportsmanager.core.AbstractTeam;
import com.sportsmanager.core.IPlayer;

import java.util.ArrayList;
import java.util.List;

public class VolleyballTeam extends AbstractTeam {

    public VolleyballTeam(String name, String logoPath) {
        super(name, logoPath);
    }

    @Override
    public boolean validateLineup(List<IPlayer> chosen) {
        if (chosen == null || chosen.size() != 6) {
            return false;
        }

        int setterCount = 0;
        int liberoCount = 0;

        for (IPlayer player : chosen) {
            if (!squad.contains(player)) {
                return false;
            }
            if (player.isInjured()) {
                return false;
            }
            if ("SETTER".equals(player.getPosition())) {
                setterCount++;
            }
            if ("LIBERO".equals(player.getPosition())) {
                liberoCount++;
            }
        }

        return setterCount == 1 && liberoCount == 1;
    }

    public static VolleyballTeam generateRandom(String name, String logoPath) {
        VolleyballTeam team = new VolleyballTeam(name, logoPath);

        team.addPlayerToSquad(VolleyballPlayer.generateRandom("SETTER", name + " Setter1"));
        team.addPlayerToSquad(VolleyballPlayer.generateRandom("SETTER", name + " Setter2"));

        team.addPlayerToSquad(VolleyballPlayer.generateRandom("MIDDLE_BLOCKER", name + " Middle1"));
        team.addPlayerToSquad(VolleyballPlayer.generateRandom("MIDDLE_BLOCKER", name + " Middle2"));

        team.addPlayerToSquad(VolleyballPlayer.generateRandom("OUTSIDE_HITTER", name + " Outside1"));
        team.addPlayerToSquad(VolleyballPlayer.generateRandom("OUTSIDE_HITTER", name + " Outside2"));

        team.addPlayerToSquad(VolleyballPlayer.generateRandom("OPPOSITE", name + " Opposite1"));
        team.addPlayerToSquad(VolleyballPlayer.generateRandom("OPPOSITE", name + " Opposite2"));

        team.addPlayerToSquad(VolleyballPlayer.generateRandom("LIBERO", name + " Libero1"));
        team.addPlayerToSquad(VolleyballPlayer.generateRandom("LIBERO", name + " Libero2"));

        team.addPlayerToSquad(VolleyballPlayer.generateRandom("OUTSIDE_HITTER", name + " Reserve1"));
        team.addPlayerToSquad(VolleyballPlayer.generateRandom("MIDDLE_BLOCKER", name + " Reserve2"));

        List<IPlayer> firstSix = new ArrayList<>();
        firstSix.add(team.squad.get(0)); // Setter1
        firstSix.add(team.squad.get(2)); // Middle1
        firstSix.add(team.squad.get(3)); // Middle2
        firstSix.add(team.squad.get(4)); // Outside1
        firstSix.add(team.squad.get(6)); // Opposite1
        firstSix.add(team.squad.get(8)); // Libero1

        team.setStartingLineup(firstSix);
        team.setTactic(new VolleyballTactic("BALANCED"));
        team.setCoach(new VolleyballCoach(name + " Coach", "ATTACKING"));

        return team;
    }
}