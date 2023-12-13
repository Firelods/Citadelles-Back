package com.seinksansdoozebank.fr.model.player;

import com.seinksansdoozebank.fr.model.cards.Deck;
import com.seinksansdoozebank.fr.model.cards.District;
import com.seinksansdoozebank.fr.view.Cli;
import com.seinksansdoozebank.fr.view.IView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class PlayerTest {
    List<District> hand = new ArrayList<>();
    Player player;
    IView view;
    Deck deck;

    @BeforeEach
    void setup() {
        view = mock(Cli.class);
        deck = mock(Deck.class);
        District districtCostThree = District.PORT;
        District districtCostFive = District.FORTRESS;
        hand.add(districtCostThree);
        hand.add(districtCostFive);
        player = new RandomBot(10, deck, view);
        player.addDistrictToHand(districtCostThree);
        player.addDistrictToHand(districtCostFive);
    }

    @Test
    @Disabled("This test is not deterministic and will be fiexd in next commit")
    void testPlay() {
        int gold = player.getNbGold();
        Optional<District> playedDistrict = player.play();
        assertTrue(playedDistrict.isPresent());
        assertTrue(gold - playedDistrict.get().getCost() + 2 == player.getNbGold() || gold - playedDistrict.get().getCost() - 2 == player.getNbGold());
    }

    @Test
    void testChooseDistrict() {
        //TODO must be changed to test the new chooseDistrict method
        Optional<District> optionalChosenDistrict = player.chooseDistrict();
        optionalChosenDistrict.ifPresent(district -> assertTrue(hand.contains(district)));
    }

    @Test
    void testUpdateGold() {
        // Arrange
        Player player = new RandomBot(10, deck, view);

        // Act
        player.decreaseGold(3);

        // Assert
        assertEquals(7, player.getNbGold());
    }

    @Test
    void testPlayerInitialization() {
        // Arrange
        List<District> hand = new ArrayList<>();
        hand.add(District.PORT); //district with a cost of 3
        List<District> citadel = new ArrayList<>();

        // Act
        Player player = new RandomBot(10, deck, view);
        player.addDistrictToHand(District.PORT);

        // Assert
        assertEquals(10, player.getNbGold());
        assertEquals(hand, player.getHand());
        assertEquals(citadel, player.getCitadel());
    }

    @Test
    void testResetIdCounter() {
        // Test resetting the ID counter for player
        Player.resetIdCounter();
        Player newPlayer = new RandomBot(10, deck, view);
        assertEquals(1, newPlayer.getId()); // Should start counting from 1 again
    }
}
