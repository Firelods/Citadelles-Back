package com.seinksansdoozebank.fr.model.player;

import com.seinksansdoozebank.fr.model.cards.Card;
import com.seinksansdoozebank.fr.model.cards.Deck;
import com.seinksansdoozebank.fr.model.cards.District;
import com.seinksansdoozebank.fr.model.cards.DistrictType;
import com.seinksansdoozebank.fr.model.character.abstracts.Character;
import com.seinksansdoozebank.fr.model.character.commoncharacters.Bishop;
import com.seinksansdoozebank.fr.model.character.commoncharacters.Condottiere;
import com.seinksansdoozebank.fr.model.character.commoncharacters.King;
import com.seinksansdoozebank.fr.model.character.commoncharacters.Merchant;
import com.seinksansdoozebank.fr.view.Cli;
import com.seinksansdoozebank.fr.view.IView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SmartBotTest {
    SmartBot spySmartBot;
    IView view;
    Deck deck;
    Card cardCostThree;
    Card cardCostFive;

    @BeforeEach
    void setup() {
        view = mock(Cli.class);
        deck = spy(new Deck());
        cardCostThree = new Card(District.DONJON);
        cardCostFive = new Card(District.FORTRESS);
        spySmartBot = spy(new SmartBot(10, deck, view));
    }

    @Test
    void playWithEmptyChosenDistrictShouldPickDistrictAndBuild() {
        Optional<Card> optDistrict = Optional.empty();
        List<Card> emptyList = List.of();
        doReturn(optDistrict).when(spySmartBot).chooseCard();
        spySmartBot.chooseCharacter(new ArrayList<>(List.of(new King())));
        spySmartBot.play();

        verify(view, times(1)).displayPlayerStartPlaying(spySmartBot);
        verify(view, times(1)).displayPlayerRevealCharacter(spySmartBot);
        verify(spySmartBot, times(1)).pickTwoCardKeepOneDiscardOne();
        verify(spySmartBot, times(1)).playACard();
        verify(view, times(1)).displayPlayerPlaysCard(spySmartBot, emptyList);
        verify(view, times(2)).displayPlayerInfo(spySmartBot);
    }

    @Test
    void playWithUnbuildableDistrictShouldPickGoldAndBuild() {
        Optional<Card> optDistrict = Optional.of(cardCostThree);
        doReturn(optDistrict).when(spySmartBot).chooseCard();
        doReturn(false).when(spySmartBot).canPlayCard(any(Card.class));
        doReturn(Optional.of(cardCostThree)).when(spySmartBot).playACard();
        spySmartBot.chooseCharacter(new ArrayList<>(List.of(new Bishop(), new King(), new Merchant(), new Condottiere())));
        spySmartBot.play();

        verify(view, times(1)).displayPlayerStartPlaying(spySmartBot);
        verify(view, times(1)).displayPlayerRevealCharacter(spySmartBot);
        verify(spySmartBot, times(1)).pickGold();
        verify(spySmartBot, times(1)).playACard();
        verify(view, times(1)).displayPlayerPlaysCard(spySmartBot, List.of(cardCostThree));
        verify(view, times(2)).displayPlayerInfo(spySmartBot);
    }

    @Test
    void playWithUBuildableDistrictShouldBuildAndPickSomething() {
        Optional<Card> optDistrict = Optional.of(cardCostThree);
        doReturn(optDistrict).when(spySmartBot).chooseCard();
        doReturn(true).when(spySmartBot).canPlayCard(any(Card.class));
        spySmartBot.chooseCharacter(new ArrayList<>(List.of(new Bishop(), new King(), new Merchant(), new Condottiere())));
        spySmartBot.play();

        verify(view, times(1)).displayPlayerStartPlaying(spySmartBot);
        verify(view, times(1)).displayPlayerRevealCharacter(spySmartBot);
        verify(spySmartBot, times(1)).playACard();
        verify(view, times(1)).displayPlayerPlaysCard(spySmartBot, List.of(optDistrict.get()));
        verify(spySmartBot, times(1)).pickSomething();
        verify(view, times(2)).displayPlayerInfo(spySmartBot);
    }

    @Test
    void pickSomethingWithEmptyChoosenDistrict() {
        Optional<Card> optDistrict = Optional.empty();
        doReturn(optDistrict).when(spySmartBot).playACard();
        spySmartBot.pickSomething();

        verify(spySmartBot, times(0)).pickGold();
        verify(spySmartBot, times(1)).pickTwoCardKeepOneDiscardOne();
    }

    @Test
    void pickSomethingWithChoosenDistrictAndNotEnoughGold() {
        spySmartBot.decreaseGold(spySmartBot.getNbGold());
        Optional<Card> optDistrict = Optional.of(cardCostThree);
        doReturn(optDistrict).when(spySmartBot).chooseCard();
        spySmartBot.pickSomething();
        assertTrue(spySmartBot.getNbGold() < cardCostThree.getDistrict().getCost());
        verify(spySmartBot, times(1)).pickGold();
        verify(spySmartBot, times(0)).pickTwoCardKeepOneDiscardOne();
    }

    @Test
    void pickSomethingWithChoosenDistrictAndEnoughGold() {
        Optional<Card> optDistrict = Optional.of(cardCostThree);
        doReturn(optDistrict).when(spySmartBot).chooseCard();
        spySmartBot.pickSomething();
        assertTrue(spySmartBot.getNbGold() >= cardCostThree.getDistrict().getCost());
        verify(spySmartBot, times(0)).pickGold();
        verify(spySmartBot, times(1)).pickTwoCardKeepOneDiscardOne();
    }

    @Test
    void pickTwoDistrictKeepOneDiscardOneShouldkeepTheCheaperOne() {
        boolean handIsEmpty = spySmartBot.getHand().isEmpty();
        spySmartBot.pickTwoCardKeepOneDiscardOne();

        assertTrue(handIsEmpty);
        assertEquals(1, spySmartBot.getHand().size());
        verify(view, times(1)).displayPlayerPickCard(spySmartBot);
        verify(deck, times(2)).pick();
        verify(deck, times(1)).discard(any(Card.class));
        assertTrue(spySmartBot.getHand().get(0).getDistrict().getCost() <= deck.getDeck().get(0).getDistrict().getCost());
    }

    @Test
    void chooseDistrictWithEmptyHand() {
        boolean handIsEmpty = spySmartBot.getHand().isEmpty();
        Optional<Card> chosenDistrict = spySmartBot.chooseCard();
        assertTrue(chosenDistrict.isEmpty());
        assertTrue(handIsEmpty);
    }

    @Test
    void chooseDistrictShouldReturnANotAlreadyBuiltDistrict() {
        spySmartBot.getHand().add(cardCostThree);
        spySmartBot.getHand().add(cardCostFive);
        when(spySmartBot.getCitadel()).thenReturn(List.of(cardCostThree));
        Optional<Card> chosenDistrict = spySmartBot.chooseCard();
        assertTrue(chosenDistrict.isPresent());
        assertEquals(cardCostFive, chosenDistrict.get());
    }

    @Test
    void getCheaperDistricWithEmptyListtShouldReturnEmptyOptional() {
        Optional<Card> cheaperCard = spySmartBot.getCheaperCard(List.of());
        assertTrue(cheaperCard.isEmpty());
    }

    @Test
    void getCheaperDistrictShouldReturnTheCheaperDistrict() {
        List<Card> districtList = List.of(cardCostFive, cardCostThree, cardCostFive);
        Optional<Card> cheaperCard = spySmartBot.getCheaperCard(districtList);
        assertTrue(cheaperCard.isPresent());
        assertEquals(cardCostThree, cheaperCard.get());
    }

    @Test
    void getDistrictTypeFrequencyList() {
        List<Card> districtList = List.of(cardCostFive, cardCostThree, cardCostFive);
        List<DistrictType> districtTypeFrequencyList = spySmartBot.getDistrictTypeFrequencyList(districtList);
        assertEquals(2, districtTypeFrequencyList.size());
        assertEquals(DistrictType.SOLDIERLY, districtTypeFrequencyList.get(0));
        assertEquals(DistrictType.PRESTIGE, districtTypeFrequencyList.get(1));
    }

    List<Character> createCharactersList() {
        Bishop bishop = new Bishop();
        King king = new King();
        Merchant merchant = new Merchant();
        Condottiere condottiere = new Condottiere();

        List<Character> characters = new ArrayList<>();
        characters.add(bishop);
        characters.add(king);
        characters.add(merchant);
        characters.add(condottiere);
        return characters;
    }

    @Test
    void chooseCharacterWhenMostOwnedDistrictTypeCharacterIsAvailable() {
        List<Character> characters = createCharactersList();
        Card manorCard = new Card(District.MANOR);
        Card castleCard = new Card(District.CASTLE);
        Card palaceCard = new Card(District.PALACE);
        Card laboratoryCard = new Card(District.LABORATORY);

        ArrayList<Card> citadel = new ArrayList<>();
        citadel.add(manorCard);
        citadel.add(castleCard);
        citadel.add(palaceCard);
        citadel.add(laboratoryCard);

        when(spySmartBot.getCitadel()).thenReturn(citadel);
        spySmartBot.chooseCharacter(characters);
        assertEquals(characters.get(1), spySmartBot.getCharacter());
    }

    @Test
    void chooseCharacterWhenMostOwnedDistrictTypeCharacterIsNotAvailable() {
        List<Character> characters = createCharactersList();
        characters.remove(1); // Remove the king
        Card manorCard = new Card(District.MANOR);
        Card castleCard = new Card(District.CASTLE);
        Card palaceCard = new Card(District.PALACE);
        Card laboratoryCard = new Card(District.FORTRESS);

        ArrayList<Card> citadel = new ArrayList<>();
        citadel.add(manorCard);
        citadel.add(castleCard);
        citadel.add(palaceCard);
        citadel.add(laboratoryCard);

        when(spySmartBot.getCitadel()).thenReturn(citadel);
        spySmartBot.chooseCharacter(characters);
        assertEquals(characters.get(2), spySmartBot.getCharacter());
    }

    @Test
    void chooseCharacterWhenMostOwnedDistrictTypeCharacterIsNotAvailableAndSecondNeither() {
        List<Character> characters = createCharactersList();
        characters.remove(1); // Remove the king
        characters.remove(2); // Remove the condottiere
        Card manorCard = new Card(District.MANOR);
        Card castleCard = new Card(District.CASTLE);
        Card palaceCard = new Card(District.PALACE);
        Card laboratoryCard = new Card(District.FORTRESS);
        Card barrackCard = new Card(District.BARRACK);
        Card cornerShopCard = new Card(District.CORNER_SHOP);

        ArrayList<Card> citadel = new ArrayList<>();
        citadel.add(manorCard);
        citadel.add(castleCard);
        citadel.add(palaceCard);
        citadel.add(laboratoryCard);
        citadel.add(barrackCard);
        citadel.add(cornerShopCard);

        when(spySmartBot.getCitadel()).thenReturn(citadel);
        spySmartBot.chooseCharacter(characters);
        assertEquals(characters.get(1), spySmartBot.getCharacter());
    }

    @Test
    void testChooseColorCourtyardOfMiracleGetTheCorrectColor() {
        // Set a citadel with 4 district with different colors
        Card manorCard = new Card(District.TEMPLE);
        Card castleCard = new Card(District.MANOR);
        Card palaceCard = new Card(District.TAVERN);
        Card laboratoryCard = new Card(District.CEMETERY);
        // Add the Courtyard of miracle
        Card courtyardOfMiracleCard = new Card(District.COURTYARD_OF_MIRACLE);
        ArrayList<Card> citadel = new ArrayList<>(
                List.of(manorCard, castleCard, palaceCard, laboratoryCard, courtyardOfMiracleCard)
        );
        when(spySmartBot.getCitadel()).thenReturn(citadel);
        spySmartBot.chooseColorCourtyardOfMiracle();
        assertEquals(DistrictType.SOLDIERLY, spySmartBot.getColorCourtyardOfMiracleType());
    }
}