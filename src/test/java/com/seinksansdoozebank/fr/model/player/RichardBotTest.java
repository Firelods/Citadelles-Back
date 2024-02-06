package com.seinksansdoozebank.fr.model.player;

import com.seinksansdoozebank.fr.model.bank.Bank;
import com.seinksansdoozebank.fr.model.cards.Deck;
import com.seinksansdoozebank.fr.model.character.abstracts.Character;
import com.seinksansdoozebank.fr.model.character.commoncharacters.Bishop;
import com.seinksansdoozebank.fr.model.character.commoncharacters.Condottiere;
import com.seinksansdoozebank.fr.model.character.commoncharacters.King;
import com.seinksansdoozebank.fr.model.character.commoncharacters.Merchant;
import com.seinksansdoozebank.fr.model.character.roles.Role;
import com.seinksansdoozebank.fr.model.character.specialscharacters.Architect;
import com.seinksansdoozebank.fr.model.character.specialscharacters.Assassin;
import com.seinksansdoozebank.fr.model.character.specialscharacters.Thief;
import com.seinksansdoozebank.fr.view.Cli;
import com.seinksansdoozebank.fr.view.IView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RichardBotTest {
    RichardBot richardBot;
    IView view;
    Deck deck;

    @BeforeEach
    void setUp() {
        Bank.reset();
        Bank.getInstance().pickXCoin(Bank.MAX_COIN / 2);
        view = mock(Cli.class);
        deck = spy(new Deck());
        richardBot = spy(new RichardBot(10, deck, view));
    }

    @Test
    void getOpponentsAboutToWinWithNoOpponentAboutToWinShouldReturnEmptyList() {
        Opponent opponent1 = mock(Opponent.class);
        when(opponent1.isAboutToWin()).thenReturn(false);
        Opponent opponent2 = mock(Opponent.class);
        when(opponent2.isAboutToWin()).thenReturn(false);
        List<Opponent> opponents = List.of(opponent1, opponent2);
        when(richardBot.getOpponents()).thenReturn(opponents);

        assertFalse(richardBot.anOpponentIsAboutToWin());
    }

    @Test
    void getOpponentsAboutToWinWithNoOpponentAboutToWinShouldReturnFalse() {
        Opponent opponent1 = mock(Opponent.class);
        when(opponent1.isAboutToWin()).thenReturn(false);
        Opponent opponent2 = mock(Opponent.class);
        when(opponent2.isAboutToWin()).thenReturn(true);
        List<Opponent> opponents = List.of(opponent1, opponent2);
        when(richardBot.getOpponents()).thenReturn(opponents);

        assertTrue(richardBot.anOpponentIsAboutToWin());
    }

    @Test
    void getOpponentsAboutToWinWithAnOpponentAboutToWinShouldReturnTrue() {
        Opponent opponent1 = mock(Opponent.class);
        when(opponent1.isAboutToWin()).thenReturn(true);
        Opponent opponent2 = mock(Opponent.class);
        when(opponent2.isAboutToWin()).thenReturn(false);
        List<Opponent> opponents = List.of(opponent1, opponent2);
        when(richardBot.getOpponents()).thenReturn(opponents);

        assertTrue(richardBot.anOpponentIsAboutToWin());
    }

    @Test
    void choseThiefTargetWhenNoOpponentIsAboutToWinShouldCallSuperMethod() {
        List<Character> availableCharacters = List.of(new Thief(), new Bishop(), new King(), new Condottiere());
        when(richardBot.getAvailableCharacters()).thenReturn(availableCharacters);
        when(richardBot.anOpponentIsAboutToWin()).thenReturn(false);

        richardBot.chooseThiefTarget();

        verify(richardBot).useSuperChoseThiefEffect();
    }

    @Test
    void choseThiefTargetWhenOpponentIsAboutToWinAndNoBishopOrCondottiereShouldCallSuperMethod() {
        List<Character> availableCharacters = List.of(new Architect(), new King(), new Thief(), new Merchant());
        when(richardBot.getAvailableCharacters()).thenReturn(availableCharacters);
        when(richardBot.anOpponentIsAboutToWin()).thenReturn(true);

        richardBot.chooseThiefTarget();

        verify(richardBot).useSuperChoseThiefEffect();
    }

    @Test
    void choseThiefTargetWhenOpponentIsAboutToWinAndBishopAvailableShouldReturnBishop() {
        List<Character> availableCharacters = List.of(new Condottiere(), new King(), new Bishop(), new Merchant());
        when(richardBot.getAvailableCharacters()).thenReturn(availableCharacters);
        when(richardBot.anOpponentIsAboutToWin()).thenReturn(true);

        Optional<Character> result = richardBot.chooseThiefTarget();

        Character expectedCharacter = new Bishop();
        assertTrue(result.isPresent());
        verify(richardBot, never()).useSuperChoseThiefEffect();
        assertEquals(expectedCharacter, result.get());
    }

    @Test
    void choseThiefTargetWhenOpponentIsAboutToWinAndCondottiereAvailableShouldReturnCondottiere() {
        List<Character> availableCharacters = List.of(new Architect(), new King(), new Condottiere(), new Merchant());
        when(richardBot.getAvailableCharacters()).thenReturn(availableCharacters);
        when(richardBot.anOpponentIsAboutToWin()).thenReturn(true);

        Optional<Character> result = richardBot.chooseThiefTarget();

        Character expectedCharacter = new Condottiere();
        assertTrue(result.isPresent());
        verify(richardBot, never()).useSuperChoseThiefEffect();
        assertEquals(expectedCharacter, result.get());
    }
    @Test
    void chooseAssassinTargetIfThiefIsPresentAndShouldPreventWealth() {
        // Configuration des joueurs et de leurs rôles
        Player thiefPlayer = spy(new SmartBot(10, deck, view));
        thiefPlayer.chooseCharacter(new ArrayList<>(List.of(new Thief())));

        // Configuration des conditions spécifiques du test
        richardBot.chooseCharacter(new ArrayList<>(List.of(new Assassin())));
        when(richardBot.getAvailableCharacters()).thenReturn(List.of(new Thief()));
        when(richardBot.shouldPreventWealth()).thenReturn(true); // Simuler une condition pour choisir le voleur
        // Exécution de la méthode à tester
        Character target = richardBot.chooseAssassinTarget();

        // Vérification que le personnage ciblé est le voleur, sous condition spécifique
        assertEquals(Role.THIEF, target.getRole(), "The target should be the Thief under specific conditions.");
    }

    @Test
    void chooseAssassinTargetIfCondottiereIsPresentAndThinkCondottiereWillBeChosenByTheLeadingOpponent() {
        // Configuration des joueurs et de leurs rôles
        Player condottierePlayer = spy(new SmartBot(10, deck, view));
        condottierePlayer.chooseCharacter(new ArrayList<>(List.of(new Condottiere())));


        // Configuration des conditions spécifiques du test
        richardBot.chooseCharacter(new ArrayList<>(List.of(new Assassin())));
        when(richardBot.getOpponents()).thenReturn(List.of(condottierePlayer));
        when(richardBot.getAvailableCharacters()).thenReturn(List.of(new Condottiere()));
        when(richardBot.thinkCondottiereHasBeenChosenByTheLeadingOpponent()).thenReturn(true); // Simuler une condition pour choisir le condottiere
        // Exécution de la méthode à tester
        Character target = richardBot.chooseAssassinTarget();

        // Vérification que le personnage ciblé est le condottiere, sous condition spécifique
        assertEquals(Role.CONDOTTIERE, target.getRole(), "The target should be the Condottiere under specific conditions.");
    }

    @Test
    void chooseAssassinTargetIfNoSpecificConditions() {
        Player kingPlayer = spy(new SmartBot(10, deck, view));
        kingPlayer.chooseCharacter(new ArrayList<>(List.of(new King())));

        // Configuration des conditions spécifiques du test
        richardBot.chooseCharacter(new ArrayList<>(List.of(new Assassin())));

        when(richardBot.getAvailableCharacters()).thenReturn(List.of(new King()));
        when(richardBot.getOpponents()).thenReturn(List.of(kingPlayer));

        when(richardBot.shouldPreventWealth()).thenReturn(false); // Simuler une condition pour choisir le voleur
        when(richardBot.thinkCondottiereHasBeenChosenByTheLeadingOpponent()).thenReturn(false); // Simuler une condition pour choisir le condottiere
        // Exécution de la méthode à tester
        Character target = richardBot.chooseAssassinTarget();

        // Vérification que le personnage ciblé est le condottiere, sous condition spécifique
        assertEquals(Role.KING, target.getRole(), "The target should be the King under no specific conditions.");
    }

    @Test
    void shouldPreventWealth() {
        Player opponent = spy(new SmartBot(0, deck, view));
        opponent.increaseGold(8);
        when(richardBot.getOpponents()).thenReturn(List.of(opponent));
        boolean shouldPreventWealth = richardBot.shouldPreventWealth();
        assertTrue(shouldPreventWealth, "The bot should prevent wealth if an opponent has 7 or more gold.");
    }

    @Test
    void shouldNotPreventWealth() {
        Player opponent = spy(new SmartBot(6, deck, view));
        when(richardBot.getOpponents()).thenReturn(List.of(opponent));
        boolean shouldPreventWealth = richardBot.shouldPreventWealth();
        assertFalse(shouldPreventWealth, "The bot should not prevent wealth if no opponent has 7 or more gold.");
    }

    @Test
    void thinkCondottiereHasBeenChosenByTheLeadingOpponent() {
        Player opponent = spy(new SmartBot(10, deck, view));
        opponent.chooseCharacter(new ArrayList<>(List.of(new Condottiere())));
        when(richardBot.getOpponents()).thenReturn(List.of(opponent));
        when(opponent.isAboutToWin()).thenReturn(true);
        boolean thinkCondottiereWillBeChosenByTheLeadingOpponent = richardBot.thinkCondottiereHasBeenChosenByTheLeadingOpponent();
        assertTrue(thinkCondottiereWillBeChosenByTheLeadingOpponent, "The bot should think the Condottiere will be chosen by the leading opponent if he is about to win.");
    }

    @Test
    void thinkCondottiereHasNotBeenChosenByTheLeadingOpponent() {
        Player opponent = spy(new SmartBot(10, deck, view));
        opponent.chooseCharacter(new ArrayList<>(List.of(new Condottiere())));
        when(richardBot.getOpponents()).thenReturn(List.of(opponent));
        when(opponent.isAboutToWin()).thenReturn(false);
        boolean thinkCondottiereWillBeChosenByTheLeadingOpponent = richardBot.thinkCondottiereHasBeenChosenByTheLeadingOpponent();
        assertFalse(thinkCondottiereWillBeChosenByTheLeadingOpponent, "The bot should think the Condottiere will not be chosen by the leading opponent if he is not about to win.");
    }

    @Test
    void thinkThiefHasBeenChosenByTheLeadingOpponentWhenThiefHasBeenSeen() {
        Player opponentThief = spy(new SmartBot(10, deck, view));
        opponentThief.chooseCharacter(new ArrayList<>(List.of(new Thief())));
        Player opponentCondottiere = spy(new SmartBot(10, deck, view));
        opponentCondottiere.chooseCharacter(new ArrayList<>(List.of(new Condottiere())));
        when(richardBot.getOpponents()).thenReturn(List.of(opponentThief, opponentCondottiere));
        when(opponentThief.isAboutToWin()).thenReturn(true);
        when(richardBot.getOpponentsWhichHasChosenCharacterBefore()).thenReturn(List.of(opponentCondottiere));
        when(richardBot.getCharactersSeenInRound()).thenReturn(List.of(new Thief()));
        when(richardBot.getCharactersNotInRound()).thenReturn(List.of());
        boolean thinkThiefWillBeChosenByTheLeadingOpponent = richardBot.thinkThiefHasBeenChosenByTheLeadingOpponent();
        assertTrue(thinkThiefWillBeChosenByTheLeadingOpponent, "The bot should think the Thief has been chosen by the leading opponent if he is about to win.");
    }

    @Test
    void thinkThiefHasBeenChosenByTheLeadingOpponentWhenThiefHasntBeenSeen() {
        Player opponentThief = spy(new SmartBot(10, deck, view));
        opponentThief.chooseCharacter(new ArrayList<>(List.of(new Thief())));
        Player opponentCondottiere = spy(new SmartBot(10, deck, view));
        opponentCondottiere.chooseCharacter(new ArrayList<>(List.of(new Condottiere())));
        when(richardBot.getOpponents()).thenReturn(List.of(opponentThief, opponentCondottiere));
        when(opponentThief.isAboutToWin()).thenReturn(true);
        when(richardBot.getOpponentsWhichHasChosenCharacterBefore()).thenReturn(List.of(opponentThief));
        when(richardBot.getCharactersSeenInRound()).thenReturn(List.of(new Condottiere()));
        when(richardBot.getCharactersNotInRound()).thenReturn(List.of());
        boolean thinkThiefWillBeChosenByTheLeadingOpponent = richardBot.thinkThiefHasBeenChosenByTheLeadingOpponent();
        assertTrue(thinkThiefWillBeChosenByTheLeadingOpponent, "The bot should think the Thief has been chosen by the leading opponent if he is about to win.");
    }

    @Test
    void thinkThiefHasBeenChosenByTheLeadingOpponentWhenThiefIsNotInRound() {
        Player opponentThief = spy(new SmartBot(10, deck, view));
        opponentThief.chooseCharacter(new ArrayList<>(List.of(new Thief())));
        Player opponentCondottiere = spy(new SmartBot(10, deck, view));
        opponentCondottiere.chooseCharacter(new ArrayList<>(List.of(new Condottiere())));
        when(richardBot.getOpponents()).thenReturn(List.of(opponentThief, opponentCondottiere));
        when(opponentThief.isAboutToWin()).thenReturn(true);
        when(richardBot.getOpponentsWhichHasChosenCharacterBefore()).thenReturn(List.of(opponentThief));
        when(richardBot.getCharactersSeenInRound()).thenReturn(List.of());
        when(richardBot.getCharactersNotInRound()).thenReturn(List.of(new Thief()));
        boolean thinkThiefWillBeChosenByTheLeadingOpponent = richardBot.thinkThiefHasBeenChosenByTheLeadingOpponent();
        assertFalse(thinkThiefWillBeChosenByTheLeadingOpponent, "The bot should think the Thief has not been chose because thief is not in round.");
    }
}