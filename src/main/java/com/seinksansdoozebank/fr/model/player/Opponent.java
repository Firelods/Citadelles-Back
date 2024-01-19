package com.seinksansdoozebank.fr.model.player;

import com.seinksansdoozebank.fr.model.cards.Card;
import com.seinksansdoozebank.fr.model.character.abstracts.Character;

import java.util.List;

public interface Opponent {

    int getNbGold();

    int nbDistrictsInCitadel();

    List<Card> getCitadel();

    Character getOpponentCharacter();
}
