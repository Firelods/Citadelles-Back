package com.seinksansdoozebank.fr.model.player;
import com.seinksansdoozebank.fr.model.cards.District;

import java.util.List;

import com.seinksansdoozebank.fr.view.IView;

import java.util.ArrayList;
import java.util.Random;

public class Player {
    private static int counter = 1;
    private final int id;
    private int nbGold;
    private final List<District> hand;
    private List<District> citadel;
    private final Random random = new Random();
    private boolean isStuck = false;
    private final IView view;

    public Player(int nbGold, IView view) {
        this.id = counter++;
        this.nbGold = nbGold;
        this.hand = new ArrayList<>();
        this.citadel = new ArrayList<>();
        this.view = view;
    }

    District chooseDistrict() {
        return this.hand.get(random.nextInt(hand.size()));
    }

    public District play() {
        int cnt = 0;
        view.displayPlayerStartPlaying(this);
        view.displayPlayerInfo(this);
        District district = this.chooseDistrict();
        while (district.getCost() > this.nbGold && cnt < 5) {
            district = this.chooseDistrict();
            cnt++;
        }

        this.hand.remove(district);
        this.citadel.add(district);
        this.decreaseGold(district.getCost());
        return district;
    }

    public void decreaseGold(int gold) {
        this.nbGold -= gold;
    }
    public void increaseGold(int gold) {
        this.nbGold += gold;
    }

    public void addDistrictToHand(District district) {
        this.hand.add(district);
    }

    public List<District> getHand() {
        return this.hand;
    }

    public List<District> getCitadel() {
        return this.citadel;
    }

    /**
     * this function should be used only for test
     * @param citadel
     */
    public void setCitadel(List<District> citadel) {
        this.citadel = citadel;
    }

    public int getNbGold() {
        return this.nbGold;
    }

    public int getId() {
        return this.id;
    }

    public static void resetIdCounter() {
        counter = 1;
    }

    public boolean isStuck() {
        return isStuck;
    }

    public int getScore() {
        //calcule de la somme du cout des quartiers de la citadelle
        return citadel.stream().mapToInt(District::getCost).sum();
    }

    @Override
    public String toString() {
        return "Le joueur "+this.id;
    }
}
