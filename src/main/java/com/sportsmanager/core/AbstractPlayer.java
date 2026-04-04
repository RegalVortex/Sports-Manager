package com.sportsmanager.core;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPlayer implements IPlayer {
    protected String name;
    protected String position;
    protected Map<String, Integer> attributes;
    protected boolean injured;
    protected int injuryGamesRemaining;

    public AbstractPlayer(String name, String position) {
        this.name = name;
        this.position = position;
        this.attributes = new HashMap<>();
        this.injured = false;
        this.injuryGamesRemaining = 0;

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
        if (attribute==null||attribute.isBlank()){
            return;
        }

        int currentValue=attributes.getOrDefault(attribute,50);
         int newValue=currentValue+amount;

         if (newValue<0){
             newValue=0;
         }
         if (newValue>100){
             newValue=100;
         }
         attributes.put(attribute,newValue);
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
