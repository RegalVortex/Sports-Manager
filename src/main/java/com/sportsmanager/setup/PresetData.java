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
                    "Türkiye Futbol Ligi",
                    Arrays.asList("Galatasaray", "Fenerbahçe", "Beşiktaş", "Trabzonspor",
                                  "Başakşehir", "Sivasspor", "Kasımpaşa", "Ankaragücü")
            ));

            leagues.add(new LeaguePreset(
                    "football",
                    "Avrupa Futbol Ligi",
                    Arrays.asList("Madrid FC", "London United", "Milan City", "Paris Stars",
                                  "Bayern Munchen", "Ajax Amsterdam", "Porto FC", "Celtic FC")
            ));
        }

        if ("volleyball".equalsIgnoreCase(sportName)) {
            leagues.add(new LeaguePreset(
                    "volleyball",
                    "Türkiye Voleybol Ligi",
                    Arrays.asList("VakıfBank", "Eczacıbaşı", "Fenerbahçe Voleybol", "Galatasaray Voleybol",
                                  "Arkas Spor", "Halkbank", "Ziraat Bankası", "İstanbul BBSK")
            ));

            leagues.add(new LeaguePreset(
                    "volleyball",
                    "Avrupa Voleybol Ligi",
                    Arrays.asList("Berlin Spikers", "Rome Volley", "Paris Blockers", "Madrid Servers",
                                  "Perugia", "Trentino", "Monza", "Modena")
            ));
        }

        return leagues;
    }
}