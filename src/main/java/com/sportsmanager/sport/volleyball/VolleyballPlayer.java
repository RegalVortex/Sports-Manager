package com.sportsmanager.sport.volleyball;

import com.sportsmanager.core.AbstractPlayer;

import java.util.Random;

public class VolleyballPlayer extends AbstractPlayer {

    // ── İsim havuzu ─────────────────────────────────────────────────────────
    private static final String[] FIRST_NAMES = {
        "Hande", "Zehra", "Eda", "Ebrar", "Gözde", "Meryem", "Naz", "Cansu",
        "Saliha", "Kübra", "İlkin", "Elif", "Aslı", "Dilara", "Büşra",
        "Tijana", "Milena", "Jovana", "Isabela", "Gabi", "Carol", "Rosamaria",
        "Zhu", "Li", "Zhang", "Paola", "Lucia", "Elena", "Anna", "Sara",
        "Simone", "Jordan", "Kelsey", "Justine", "Lauren", "Foluke", "Rachael",
        "Cansu", "Başak", "Zeynep", "Şeyma", "Yasemin", "Nihan", "Azra"
    };

    private static final String[] LAST_NAMES = {
        "Şahin", "Ataman", "Akman", "Güneş", "Yıldız", "Çelik", "Karahan",
        "Boskovic", "Mihajlovic", "Popovic", "Stevanovic", "Jovic",
        "Garay", "Drews", "Robinson", "Larson", "Glass", "Hill",
        "Ogbogu", "Thompson", "Plummer", "Adams", "Scott",
        "Egonu", "Sylla", "Bosetti", "Gennari", "Pietrini",
        "Arıca", "Arslan", "Gürbüz", "Acar", "Özdemir", "Türker"
    };

    // ── Constructors ─────────────────────────────────────────────────────────
    public VolleyballPlayer(String name, String position) {
        super(name, position);
        attributes.put("serve", 50);
        attributes.put("spike", 50);
        attributes.put("block", 50);
        attributes.put("receive", 50);
        attributes.put("set", 50);
        attributes.put("stamina", 50);
    }

    public VolleyballPlayer(String name, String position, int age, int potential) {
        super(name, position, age, potential);
        attributes.put("serve", 50);
        attributes.put("spike", 50);
        attributes.put("block", 50);
        attributes.put("receive", 50);
        attributes.put("set", 50);
        attributes.put("stamina", 50);
    }

    // ── OVR formülü ─────────────────────────────────────────────────────────
    @Override
    public int getOverallRating() {
        int serve   = attributes.getOrDefault("serve", 50);
        int spike   = attributes.getOrDefault("spike", 50);
        int block   = attributes.getOrDefault("block", 50);
        int receive = attributes.getOrDefault("receive", 50);
        int set     = attributes.getOrDefault("set", 50);
        int stamina = attributes.getOrDefault("stamina", 50);

        switch (position) {
            case "LIBERO":
                return (receive * 3 + stamina * 2 + serve + set) / 7;
            case "SETTER":
                return (set * 3 + receive * 2 + serve + stamina + spike) / 8;
            case "MIDDLE_BLOCKER":
                return (block * 3 + spike * 2 + stamina * 2 + serve) / 8;
            case "OPPOSITE":
            case "OUTSIDE_HITTER":
                return (spike * 3 + serve * 2 + receive + stamina + block) / 8;
            default:
                return (serve + spike + block + receive + set + stamina) / 6;
        }
    }

    // ── Rastgele isim üretici ─────────────────────────────────────────────
    public static String randomName(Random r) {
        return FIRST_NAMES[r.nextInt(FIRST_NAMES.length)] + " " +
               LAST_NAMES[r.nextInt(LAST_NAMES.length)];
    }

    private static int range(Random r, int min, int max) {
        return min + r.nextInt(max - min + 1);
    }

    // ── Ana üretici (pozisyona özgü nitelikler) ──────────────────────────────
    public static VolleyballPlayer generateRandom(String position) {
        Random r = new Random();

        int age       = 17 + r.nextInt(19);      // 17–35
        int potential = 65 + r.nextInt(35);       // 65–99

        VolleyballPlayer p = new VolleyballPlayer(randomName(r), position, age, potential);

        switch (position) {
            case "SETTER":
                p.attributes.put("serve",   range(r, 55, 75));
                p.attributes.put("spike",   range(r, 42, 62));
                p.attributes.put("block",   range(r, 45, 63));
                p.attributes.put("receive", range(r, 62, 82));
                p.attributes.put("set",     range(r, 75, 92));
                p.attributes.put("stamina", range(r, 62, 80));
                break;
            case "MIDDLE_BLOCKER":
                p.attributes.put("serve",   range(r, 52, 72));
                p.attributes.put("spike",   range(r, 65, 83));
                p.attributes.put("block",   range(r, 72, 90));
                p.attributes.put("receive", range(r, 45, 63));
                p.attributes.put("set",     range(r, 38, 56));
                p.attributes.put("stamina", range(r, 62, 80));
                break;
            case "OUTSIDE_HITTER":
                p.attributes.put("serve",   range(r, 60, 80));
                p.attributes.put("spike",   range(r, 68, 86));
                p.attributes.put("block",   range(r, 52, 70));
                p.attributes.put("receive", range(r, 65, 83));
                p.attributes.put("set",     range(r, 45, 65));
                p.attributes.put("stamina", range(r, 65, 83));
                break;
            case "OPPOSITE":
                p.attributes.put("serve",   range(r, 65, 83));
                p.attributes.put("spike",   range(r, 70, 88));
                p.attributes.put("block",   range(r, 58, 76));
                p.attributes.put("receive", range(r, 48, 66));
                p.attributes.put("set",     range(r, 42, 60));
                p.attributes.put("stamina", range(r, 62, 80));
                break;
            case "LIBERO":
                p.attributes.put("serve",   range(r, 45, 63));
                p.attributes.put("spike",   range(r, 30, 48));
                p.attributes.put("block",   range(r, 32, 50));
                p.attributes.put("receive", range(r, 75, 92));
                p.attributes.put("set",     range(r, 55, 73));
                p.attributes.put("stamina", range(r, 70, 86));
                break;
            default:
                p.attributes.put("serve",   range(r, 52, 76));
                p.attributes.put("spike",   range(r, 52, 76));
                p.attributes.put("block",   range(r, 52, 76));
                p.attributes.put("receive", range(r, 52, 76));
                p.attributes.put("set",     range(r, 52, 76));
                p.attributes.put("stamina", range(r, 52, 76));
        }
        return p;
    }

    /** Geriye dönük uyumluluk — isim artık görmezden gelinir. */
    public static VolleyballPlayer generateRandom(String position, String name) {
        return generateRandom(position);
    }
}