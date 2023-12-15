package com.seinksansdoozebank.fr.model.character.abstracts;

import com.seinksansdoozebank.fr.model.player.Player;

public abstract class Character {
    private Player player;

    @Override
    public abstract String toString();

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
}
