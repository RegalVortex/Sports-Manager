package com.sportsmanager.setup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PresetData {

    public static List<LeaguePreset> getLeaguesForSport(String sportName) {
        List<LeaguePreset> leagues = new ArrayList<>();

        if ("football".equalsIgnoreCase(sportName)) {
            leagues.add(new LeaguePreset(
                    "football",
                    "Turkish Football League",
                    Arrays.asList("Galatasaray", "Fenerbahce", "Besiktas", "Trabzonspor")
            ));

            leagues.add(new LeaguePreset(
                    "football",
                    "European Football League",
                    Arrays.asList("Madrid FC", "London United", "Milan City", "Paris Stars")
            ));
        }

        if ("volleyball".equalsIgnoreCase(sportName)) {
            leagues.add(new LeaguePreset(
                    "volleyball",
                    "Turkish Volleyball League",
                    Arrays.asList("VakifBank", "Eczacibasi", "Fenerbahce Volleyball", "Galatasaray Volleyball")
            ));

            leagues.add(new LeaguePreset(
                    "volleyball",
                    "European Volleyball League",
                    Arrays.asList("Berlin Spikers", "Rome Volley", "Paris Blockers", "Madrid Servers")
            ));
        }

        return leagues;
    }
}