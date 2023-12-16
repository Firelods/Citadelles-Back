package com.seinksansdoozebank.fr.model.character.abstracts;

import com.seinksansdoozebank.fr.model.character.roles.Role;
import com.seinksansdoozebank.fr.model.player.Player;

public abstract class Character {
    private Player player;
    private final Role role;

    protected Character(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return this.role.getName();
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

    public Role getRole() {
        return this.role;
    }
}
