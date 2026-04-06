package com.sportsmanager.sport.football;

import com.sportsmanager.core.AbstractTeam;
import com.sportsmanager.core.IPlayer;

import java.util.ArrayList;
import java.util.List;

public class FootballTeam extends AbstractTeam {

    public FootballTeam(String name, String logoPath) {
        super(name, logoPath);
    }

    @Override
    public boolean validateLineup(List<IPlayer> chosen) {
        if (chosen == null || chosen.size() != 11) {
            return false;
        }

        int goalkeeperCount = 0;

        for (IPlayer player : chosen) {
            if (!squad.contains(player)) {
                return false;
            }
            if (player.isInjured()) {
                return false;
            }
            if ("GK".equals(player.getPosition())) {
                goalkeeperCount++;
            }
        }

        return goalkeeperCount == 1;
    }

    public static FootballTeam generateRandom(String name, String logoPath) {
        FootballTeam team = new FootballTeam(name, logoPath);

        team.addPlayerToSquad(FootballPlayer.generateRandom("GK", name + " GK"));
        team.addPlayerToSquad(FootballPlayer.generateRandom("CB", name + " CB1"));
        team.addPlayerToSquad(FootballPlayer.generateRandom("CB", name + " CB2"));
        team.addPlayerToSquad(FootballPlayer.generateRandom("LB", name + " LB"));
        team.addPlayerToSquad(FootballPlayer.generateRandom("RB", name + " RB"));
        team.addPlayerToSquad(FootballPlayer.generateRandom("CM", name + " CM1"));
        team.addPlayerToSquad(FootballPlayer.generateRandom("CM", name + " CM2"));
        team.addPlayerToSquad(FootballPlayer.generateRandom("CAM", name + " CAM"));
        team.addPlayerToSquad(FootballPlayer.generateRandom("LW", name + " LW"));
        team.addPlayerToSquad(FootballPlayer.generateRandom("RW", name + " RW"));
        team.addPlayerToSquad(FootballPlayer.generateRandom("ST", name + " ST"));

        team.addPlayerToSquad(FootballPlayer.generateRandom("GK", name + " SUB GK"));
        team.addPlayerToSquad(FootballPlayer.generateRandom("CB", name + " SUB CB"));
        team.addPlayerToSquad(FootballPlayer.generateRandom("CM", name + " SUB CM"));
        team.addPlayerToSquad(FootballPlayer.generateRandom("LW", name + " SUB LW"));
        team.addPlayerToSquad(FootballPlayer.generateRandom("ST", name + " SUB ST"));

        List<IPlayer> firstEleven = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            firstEleven.add(team.squad.get(i));
        }
        team.setStartingLineup(firstEleven);
        team.setTactic(new FootballTactic("4-4-2"));
        team.setCoach(new FootballCoach(name + " Coach", "ATTACKING"));

        return team;
    }
}
