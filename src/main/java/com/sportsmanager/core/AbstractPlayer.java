package com.sportsmanager.core;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPlayer implements IPlayer {
    protected String name;
    protected String position;
    protected Map<String, Integer> attributes;
    protected boolean injured;
    protected int injuryGamesRemaining;
    protected int form;
    protected int age;
    protected int potential;
    protected int matchesPlayed;
    protected int weeksInjured;


    public AbstractPlayer(String name, String position) {
        this(name, position, 23, 75);

    }

    public AbstractPlayer(String name, String position, int age, int potential) {
        this.name = name;
        this.position = position;
        this.attributes = new HashMap<>();
        this.injured = false;
        this.injuryGamesRemaining = 0;
        this.form = 1;
        this.matchesPlayed = 0;
        this.weeksInjured = 0;
        this.age = age;
        this.potential = Math.min(Math.max(potential, 50), 99);
    }

    @Override
    public int getMatchesPlayed() {
        return matchesPlayed;
    }

    @Override
    public int getWeeksInjured() {
        return weeksInjured;
    }

    @Override
    public void incrementMatchesPlayed() {
        matchesPlayed++;
    }
    @Override
    public void growOlder() {
        age++;

        // 30 yaş ve üzeri: her attribute 1-2 puan düşer
        if (age >= 30) {
            int decay = (age >= 33) ? 2 : 1;
            for (String key : attributes.keySet()) {
                int current = attributes.get(key);
                attributes.put(key, Math.max(0, current - decay));
            }
        }
    }

    @Override
    public int getAge() {
        return age;
    }

    @Override
    public int getPotential() {
        return potential;
    }
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPosition() {
        return position;
    }

    @Override
    public Map<String, Integer> getAttributes() {
        return attributes;
    }

    @Override
    public boolean isInjured() {
        return injured;
    }

    @Override
    public int getInjuryGamesRemaining() {
        return injuryGamesRemaining;
    }

    @Override
 
    public int getForm() {
    return form;
    }
    
    @Override
    public void setForm(int form) {
    if (form < 0) form = 0;
    if (form > 3) form = 3;
    this.form = form;
    }
    @Override
    public String getFormLabel() {
        switch (form) {
            case 0: return "Kötü";
            case 1: return "Normal";
            case 2: return "İyi";
            case 3: return "Mükemmel";
            default: return "Normal";
        }
    }

    @Override
    public void setInjured(int games) {
        if (games<=0){
            injured=false;
            injuryGamesRemaining=0;
            return;
        }
        injured=true;
        injuryGamesRemaining=games;
    }

    @Override
    public void decrementInjury() {
        if (!injured){
            return;
        }
        if (injuryGamesRemaining>0){
            injuryGamesRemaining--;
        }
        if (injuryGamesRemaining<=0){
            injured=false;
            injuryGamesRemaining=0;
        }
    }

    @Override
    public void train(String attribute, int amount) {
        if (attribute == null || attribute.isBlank()) {
            return;
        }

        int currentValue = attributes.getOrDefault(attribute, 50);

        // Genç oyuncular (23 yaş altı) potential'e göre bonus alır
        int effectiveAmount = amount;
        if (age <= 23) {
            double potentialBonus = (potential - getOverallRating()) / 50.0;
            effectiveAmount = (int) Math.ceil(amount * (1.0 + potentialBonus));
        }

        int newValue = currentValue + effectiveAmount;

        // Potential'i aşamasın
        int cap = Math.min(potential, 100);
        if (newValue > cap) newValue = cap;
        if (newValue < 0) newValue = 0;

        attributes.put(attribute, newValue);
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setPotential(int potential) {
        this.potential = Math.min(Math.max(potential, 50), 99);
    }

    public void setMatchesPlayed(int matchesPlayed) {
        this.matchesPlayed = Math.max(0, matchesPlayed);
    }

    public void setWeeksInjured(int weeksInjured) {
        this.weeksInjured = Math.max(0, weeksInjured);
    }
    @Override
    public int getOverallRating() {
       if (attributes.isEmpty()){
           return  0;
       }
       int sum=0;
       for (int value:attributes.values()){
           sum+=value;
       }
       return sum/attributes.size();
    }
}
