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
    public int getExpectedLineupSize() {
        return 6;
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

        team.addPlayerToSquad(VolleyballPlayer.generateRandom("SETTER"));        // 0
        team.addPlayerToSquad(VolleyballPlayer.generateRandom("SETTER"));        // 1

        team.addPlayerToSquad(VolleyballPlayer.generateRandom("MIDDLE_BLOCKER")); // 2
        team.addPlayerToSquad(VolleyballPlayer.generateRandom("MIDDLE_BLOCKER")); // 3

        team.addPlayerToSquad(VolleyballPlayer.generateRandom("OUTSIDE_HITTER")); // 4
        team.addPlayerToSquad(VolleyballPlayer.generateRandom("OUTSIDE_HITTER")); // 5

        team.addPlayerToSquad(VolleyballPlayer.generateRandom("OPPOSITE"));       // 6
        team.addPlayerToSquad(VolleyballPlayer.generateRandom("OPPOSITE"));       // 7

        team.addPlayerToSquad(VolleyballPlayer.generateRandom("LIBERO"));         // 8
        team.addPlayerToSquad(VolleyballPlayer.generateRandom("LIBERO"));         // 9

        team.addPlayerToSquad(VolleyballPlayer.generateRandom("OUTSIDE_HITTER")); // 10
        team.addPlayerToSquad(VolleyballPlayer.generateRandom("MIDDLE_BLOCKER")); // 11

        List<IPlayer> firstSix = new ArrayList<>();
        firstSix.add(team.squad.get(0)); // Setter1
        firstSix.add(team.squad.get(2)); // Middle1
        firstSix.add(team.squad.get(3)); // Middle2
        firstSix.add(team.squad.get(4)); // Outside1
        firstSix.add(team.squad.get(6)); // Opposite1
        firstSix.add(team.squad.get(8)); // Libero1

        team.setStartingLineup(firstSix);
        team.setTactic(new VolleyballTactic("BALANCED"));

        // Rastgele antrenör uzmanlığı
        String[] specs = {"ATTACKING", "DEFENSIVE", "BALANCED"};
        String spec = specs[new java.util.Random().nextInt(specs.length)];
        team.setCoach(new VolleyballCoach(VolleyballCoach.randomCoachName(), spec));

        return team;
    }
}
