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

        // İlk 11 (4-3-3 düzeni)
        team.addPlayerToSquad(FootballPlayer.generateRandom("GK"));
        team.addPlayerToSquad(FootballPlayer.generateRandom("CB"));
        team.addPlayerToSquad(FootballPlayer.generateRandom("CB"));
        team.addPlayerToSquad(FootballPlayer.generateRandom("LB"));
        team.addPlayerToSquad(FootballPlayer.generateRandom("RB"));
        team.addPlayerToSquad(FootballPlayer.generateRandom("CM"));
        team.addPlayerToSquad(FootballPlayer.generateRandom("CM"));
        team.addPlayerToSquad(FootballPlayer.generateRandom("CAM"));
        team.addPlayerToSquad(FootballPlayer.generateRandom("LW"));
        team.addPlayerToSquad(FootballPlayer.generateRandom("RW"));
        team.addPlayerToSquad(FootballPlayer.generateRandom("ST"));

        // Yedekler
        team.addPlayerToSquad(FootballPlayer.generateRandom("GK"));
        team.addPlayerToSquad(FootballPlayer.generateRandom("CB"));
        team.addPlayerToSquad(FootballPlayer.generateRandom("CM"));
        team.addPlayerToSquad(FootballPlayer.generateRandom("LW"));
        team.addPlayerToSquad(FootballPlayer.generateRandom("ST"));

        List<IPlayer> firstEleven = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            firstEleven.add(team.squad.get(i));
        }
        team.setStartingLineup(firstEleven);
        team.setTactic(new FootballTactic("4-4-2"));

        // Rastgele antrenör uzmanlığı
        String[] specs = {"ATTACKING", "DEFENSIVE", "FITNESS"};
        String spec = specs[new java.util.Random().nextInt(specs.length)];
        team.setCoach(new FootballCoach(FootballCoach.randomCoachName(), spec));

        return team;
    }
}
