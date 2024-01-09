package com.seinksansdoozebank.fr.model.character.abstracts;

import com.seinksansdoozebank.fr.model.character.roles.Role;
import com.seinksansdoozebank.fr.model.player.Player;

import java.util.List;
import java.util.Optional;

public abstract class Character {
    private Player player;
    private final Role role;
    private boolean isDead = false;
    private Player savedThief ;

    protected Character(Role role) {
        this.role = role;
    }

    /**
     * Set the player of the character
     *
     * @param player the player to set
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Character character)) {
            return false;
        }
        return this.toString().equals(character.toString());
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    public Role getRole() {
        return this.role;
    }

    public abstract void useEffect();

    @Override
    public String toString() {
        return this.role.getName();
    }

    /**
     * Kill the character
     */
    public void kill() {
        this.isDead = true;
    }

    public boolean isDead() {
        return this.isDead;
    }


    /**
     * @param player
     */
    public void setSavedThief(Player player) {
        if(savedThief==null){
            savedThief=player;
        }
        else{savedThief = null;}
    }

    /**
     * getter
     * @return a player
     */
    public Player getSavedThief() {
        return savedThief;
    }


    /**
     * this method decrease the number of gold of the character which is stolen and
     * refreshes the attribute goldWillBeStolen
     */
    public void isStolen() {
        //We add the number of gold stolen to the number of gold of the thief
        getSavedThief().increaseGold(this.getPlayer().getNbGold());

        this.getPlayer().decreaseGold(this.getPlayer().getNbGold());
        this.setSavedThief(null);
    }
}