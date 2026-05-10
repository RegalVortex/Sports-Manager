package com.sportsmanager.sport.football;

import com.sportsmanager.core.AbstractPlayer;

import java.util.Random;

public class FootballPlayer extends AbstractPlayer {

    // ── İsim havuzu ─────────────────────────────────────────────────────────
    private static final String[] FIRST_NAMES = {
        "Ahmet", "Mehmet", "Emre", "Burak", "Arda", "Kerem", "Hakan", "Cengiz",
        "Ozan", "Serdar", "Orkun", "Ferdi", "Mert", "Okay", "Yusuf", "Abdülkadir",
        "Berke", "Halil", "Dorukhan", "Berat", "Salih", "Umut", "Barış", "Güven",
        "Carlos", "Diego", "Marco", "Lucas", "Luca", "Ivan", "Alex", "Stefan",
        "Rafael", "Bruno", "André", "Marko", "Nikola", "Edin", "Sven", "Erik",
        "João", "Miguel", "Pedro", "Rui", "Nuno", "Tiago", "Gonçalo", "Diogo",
        "Lamine", "Ansu", "Gavi", "Pedri", "Dani", "Nico", "Ferran", "Yerlan"
    };

    private static final String[] LAST_NAMES = {
        "Yılmaz", "Kaya", "Demir", "Çelik", "Şahin", "Doğan", "Arslan", "Bulut",
        "Güneş", "Polat", "Koç", "Öztürk", "Aydın", "Özkan", "Başar", "Karahan",
        "Erdoğan", "Çalhanoglu", "Akturkoglu", "Kahveci", "Güler", "Yıldız",
        "Silva", "Santos", "Ferreira", "Costa", "Martins", "Pereira", "Lima",
        "Müller", "Wagner", "Becker", "Fischer", "Schneider", "Koch", "Bauer",
        "Popovic", "Jovic", "Kovac", "Lukic", "Milic", "Petrovic", "Djordjevic",
        "Hernandez", "Martinez", "Rodriguez", "Garcia", "Lopez", "Sanchez", "Torres"
    };

    // ── Constructors ─────────────────────────────────────────────────────────
    public FootballPlayer(String name, String position) {
        super(name, position);
        attributes.put("pace", 50);
        attributes.put("shooting", 50);
        attributes.put("passing", 50);
        attributes.put("defending", 50);
        attributes.put("heading", 50);
        attributes.put("stamina", 50);
    }

    public FootballPlayer(String name, String position, int age, int potential) {
        super(name, position, age, potential);
        attributes.put("pace", 50);
        attributes.put("shooting", 50);
        attributes.put("passing", 50);
        attributes.put("defending", 50);
        attributes.put("heading", 50);
        attributes.put("stamina", 50);
    }

    // ── OVR formülü (pozisyona göre ağırlıklı) ──────────────────────────────
    @Override
    public int getOverallRating() {
        int pace     = attributes.getOrDefault("pace", 50);
        int shooting = attributes.getOrDefault("shooting", 50);
        int passing  = attributes.getOrDefault("passing", 50);
        int defending= attributes.getOrDefault("defending", 50);
        int heading  = attributes.getOrDefault("heading", 50);
        int stamina  = attributes.getOrDefault("stamina", 50);

        switch (position) {
            case "GK":
                return (defending * 3 + heading + stamina * 2 + passing) / 7;
            case "CB":
                return (defending * 3 + heading * 2 + stamina + passing) / 7;
            case "LB": case "RB":
                return (defending * 2 + pace * 2 + passing * 2 + stamina) / 7;
            case "CDM": case "CM":
                return (passing * 2 + defending * 2 + stamina * 2 + pace) / 7;
            case "CAM":
                return (passing * 3 + shooting * 2 + pace + stamina) / 7;
            case "LW": case "RW":
                return (pace * 3 + shooting * 2 + passing + stamina) / 7;
            case "ST": case "CF":
                return (shooting * 3 + heading * 2 + pace + stamina) / 7;
            default:
                return (pace + shooting + passing * 2 + defending + heading + stamina) / 7;
        }
    }

    // ── Rastgele isim üretici ────────────────────────────────────────────────
    public static String randomName(Random random) {
        String first = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
        String last  = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
        return first + " " + last;
    }

    // ── Pozisyona göre nitelik aralığı yardımcısı ───────────────────────────
    private static int range(Random r, int min, int max) {
        return min + r.nextInt(max - min + 1);
    }

    // ── Ana üretici (pozisyona özgü nitelikler) ──────────────────────────────
    public static FootballPlayer generateRandom(String position) {
        Random r = new Random();

        int age       = 17 + r.nextInt(19);       // 17–35
        int potential = 65 + r.nextInt(35);        // 65–99

        FootballPlayer p = new FootballPlayer(randomName(r), position, age, potential);

        switch (position) {
            case "GK":
                p.attributes.put("pace",      range(r, 38, 60));
                p.attributes.put("shooting",  range(r, 32, 52));
                p.attributes.put("passing",   range(r, 50, 70));
                p.attributes.put("defending", range(r, 70, 90));
                p.attributes.put("heading",   range(r, 62, 82));
                p.attributes.put("stamina",   range(r, 58, 76));
                break;
            case "CB":
                p.attributes.put("pace",      range(r, 48, 68));
                p.attributes.put("shooting",  range(r, 38, 58));
                p.attributes.put("passing",   range(r, 50, 70));
                p.attributes.put("defending", range(r, 68, 90));
                p.attributes.put("heading",   range(r, 65, 85));
                p.attributes.put("stamina",   range(r, 60, 78));
                break;
            case "LB": case "RB":
                p.attributes.put("pace",      range(r, 62, 82));
                p.attributes.put("shooting",  range(r, 42, 62));
                p.attributes.put("passing",   range(r, 58, 78));
                p.attributes.put("defending", range(r, 60, 80));
                p.attributes.put("heading",   range(r, 50, 70));
                p.attributes.put("stamina",   range(r, 65, 83));
                break;
            case "CDM": case "CM":
                p.attributes.put("pace",      range(r, 54, 74));
                p.attributes.put("shooting",  range(r, 50, 70));
                p.attributes.put("passing",   range(r, 65, 85));
                p.attributes.put("defending", range(r, 57, 77));
                p.attributes.put("heading",   range(r, 54, 72));
                p.attributes.put("stamina",   range(r, 68, 86));
                break;
            case "CAM":
                p.attributes.put("pace",      range(r, 60, 80));
                p.attributes.put("shooting",  range(r, 65, 85));
                p.attributes.put("passing",   range(r, 70, 88));
                p.attributes.put("defending", range(r, 38, 58));
                p.attributes.put("heading",   range(r, 48, 66));
                p.attributes.put("stamina",   range(r, 60, 78));
                break;
            case "LW": case "RW":
                p.attributes.put("pace",      range(r, 70, 90));
                p.attributes.put("shooting",  range(r, 65, 83));
                p.attributes.put("passing",   range(r, 60, 80));
                p.attributes.put("defending", range(r, 35, 55));
                p.attributes.put("heading",   range(r, 45, 65));
                p.attributes.put("stamina",   range(r, 65, 83));
                break;
            case "ST": case "CF":
                p.attributes.put("pace",      range(r, 65, 85));
                p.attributes.put("shooting",  range(r, 72, 90));
                p.attributes.put("passing",   range(r, 50, 70));
                p.attributes.put("defending", range(r, 32, 52));
                p.attributes.put("heading",   range(r, 65, 83));
                p.attributes.put("stamina",   range(r, 65, 83));
                break;
            default:
                p.attributes.put("pace",      range(r, 52, 76));
                p.attributes.put("shooting",  range(r, 52, 76));
                p.attributes.put("passing",   range(r, 52, 76));
                p.attributes.put("defending", range(r, 52, 76));
                p.attributes.put("heading",   range(r, 52, 76));
                p.attributes.put("stamina",   range(r, 52, 76));
        }
        return p;
    }

    /** Geriye dönük uyumluluk — isim artık görmezden gelinir. */
    public static FootballPlayer generateRandom(String position, String name) {
        FootballPlayer player = generateRandom(position);
        if (name != null && !name.isBlank()) {
            player.name = name;
        }
        return player;
    }
}
