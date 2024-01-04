package com.seinksansdoozebank.fr.model.character.specialscharacters;

import com.seinksansdoozebank.fr.model.cards.District;
import com.seinksansdoozebank.fr.model.character.abstracts.Character;
import com.seinksansdoozebank.fr.model.character.roles.Role;

public class Assassin extends Character {

    public Assassin() {
        super(Role.ASSASSIN);
    }

    @Override
    public void useEffect() {
        // No action
    }

    /**
     * Kill the character
     * @param character the character to kill
     */
    public void useEffect(Character character) {
        // Kill the specific character
        if (character.isDead()) {
            throw new IllegalStateException("The character is already dead");
        }
        character.kill();
    }

}
