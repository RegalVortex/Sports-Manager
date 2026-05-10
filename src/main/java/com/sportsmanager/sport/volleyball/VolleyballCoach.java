package com.sportsmanager.sport.volleyball;

import com.sportsmanager.core.ICoach;
import com.sportsmanager.core.IPlayer;

import java.util.List;
import java.util.Random;

public class VolleyballCoach implements ICoach {

    private static final String[] COACH_FIRST = {
        "Daniele", "Giovanni", "Marco", "Roberto", "Hugo",
        "Ferhat", "Mustafa", "Riza", "Nenad", "Zoran",
        "Guidetti", "Baris", "Okan", "Serdar"
    };
    private static final String[] COACH_LAST = {
        "Santarelli", "Guidetti", "Boscaglia", "Piazza", "Conte",
        "Çağlayan", "Karabağ", "Yıldız", "Boskovic", "Petkovic",
        "Üstündağ", "Yavuz", "Şen", "Koç"
    };

    private final String name;
    private final String specialty;
    private final int quality;

    public VolleyballCoach(String name, String specialty) {
        this.name = name;
        this.specialty = specialty == null ? "BALANCED" : specialty.toUpperCase();
        this.quality = new Random().nextInt(10) + 1; // 1-10 rastgele
    }

    public static String randomCoachName() {
        Random r = new Random();
        return COACH_FIRST[r.nextInt(COACH_FIRST.length)] + " " +
               COACH_LAST[r.nextInt(COACH_LAST.length)];
    }

    @Override
    public int getQuality() {
        return quality;
    }
    @Override
    public String getName() {
        return name;
    }

    public String getSpecialty() {
        return specialty;
    }

    @Override
    public void trainPlayers(List<IPlayer> players) {
        if (players == null) return;
        int bonus = quality / 5;

        for (IPlayer player : players) {
            if (player == null || player.isInjured()) continue;

            switch (specialty) {
                case "ATTACKING":
                    player.train("spike", 2 + bonus);
                    player.train("serve", 1 + bonus);
                    break;
                case "DEFENSIVE":
                    player.train("block", 2 + bonus);
                    player.train("receive", 1 + bonus);
                    break;
                case "FITNESS":
                    player.train("stamina", 2 + bonus);
                    break;
                default:
                    player.train("serve", 1 + bonus);
                    player.train("receive", 1 + bonus);
                    break;
            }
        }
    }
}
