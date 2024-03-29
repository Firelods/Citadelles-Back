package com.seinksansdoozebank.fr.model.player.custombot;

import com.seinksansdoozebank.fr.model.bank.Bank;
import com.seinksansdoozebank.fr.model.cards.Deck;
import com.seinksansdoozebank.fr.model.character.abstracts.Character;
import com.seinksansdoozebank.fr.model.character.commoncharacters.Bishop;
import com.seinksansdoozebank.fr.model.character.commoncharacters.King;
import com.seinksansdoozebank.fr.model.character.commoncharacters.Merchant;
import com.seinksansdoozebank.fr.model.character.specialscharacters.Assassin;
import com.seinksansdoozebank.fr.model.character.specialscharacters.Thief;
import com.seinksansdoozebank.fr.model.player.Opponent;
import com.seinksansdoozebank.fr.model.player.custombot.strategies.cardchoosing.ICardChoosingStrategy;
import com.seinksansdoozebank.fr.model.player.custombot.strategies.characterchoosing.ChoosingCharacterToTargetFirstPlayer;
import com.seinksansdoozebank.fr.model.player.custombot.strategies.characterchoosing.ICharacterChoosingStrategy;
import com.seinksansdoozebank.fr.model.player.custombot.strategies.warlordeffect.IUsingWarlordEffectStrategy;
import com.seinksansdoozebank.fr.model.player.custombot.strategies.murderereffect.IUsingMurdererEffectStrategy;
import com.seinksansdoozebank.fr.model.player.custombot.strategies.picking.IPickingStrategy;
import com.seinksansdoozebank.fr.model.player.custombot.strategies.thiefeffect.IUsingThiefEffectStrategy;
import com.seinksansdoozebank.fr.view.IView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomBotTest {

    CustomBot spyCustomBot;
    IPickingStrategy mockPickingStrategy;
    ICharacterChoosingStrategy mockCharacterChoosingStrategy;
    IUsingThiefEffectStrategy mockUsingThiefEffectStrategy;
    IUsingMurdererEffectStrategy mockUsingMurdererEffectStrategy;
    IUsingWarlordEffectStrategy mockUsingWarlordEffectStrategy;
    ICardChoosingStrategy mockCardChoosingStrategy;
    IView mockView;

    @BeforeEach
    void setUp() {
        mockView = mock(IView.class);
        mockPickingStrategy = mock(IPickingStrategy.class);
        mockCharacterChoosingStrategy = mock(ICharacterChoosingStrategy.class);
        mockUsingThiefEffectStrategy = mock(IUsingThiefEffectStrategy.class);
        mockUsingMurdererEffectStrategy = mock(IUsingMurdererEffectStrategy.class);
        mockUsingWarlordEffectStrategy = mock(IUsingWarlordEffectStrategy.class);
        mockCardChoosingStrategy = mock(ICardChoosingStrategy.class);
        spyCustomBot = spy(new CustomBot(2, new Deck(), mockView, mock(Bank.class),
                mockPickingStrategy,
                mockCharacterChoosingStrategy,
                mockUsingThiefEffectStrategy,
                mockUsingMurdererEffectStrategy,
                mockUsingWarlordEffectStrategy,
                mockCardChoosingStrategy));
    }

    @Test
    void pickSomethingWithAPickingStrategyShouldUseThePickingStrategyMethod() {
        spyCustomBot.pickSomething();
        verify(mockPickingStrategy).apply(spyCustomBot);
    }

    @Test
    void pickSomethingWithoutAPickingStrategyShouldCallTheSuperMethod() {
        spyCustomBot.pickingStrategy = null;
        spyCustomBot.pickSomething();
        verify(spyCustomBot).randomPickSomething();
    }

    @Test
    void chooseCharacterImplWithAChoosingCharacterStrategyUseTheCharacterChoosingStrategyMethod() {
        spyCustomBot.chooseCharacterImpl(null);
        verify(mockCharacterChoosingStrategy).apply(spyCustomBot, null);
    }

    @Test
    void chooseCharacterImplWithoutACharacterChoosingStrategyShouldCallTheSuperMethod() {
        spyCustomBot.characterChoosingStrategy = null;
        spyCustomBot.chooseCharacterImpl(List.of(new King(), new Merchant()));
        verify(spyCustomBot).randomChooseCharacterImpl(any());
    }

    @Test
    void useThiefEffectWithAUsingThiefEffectStrategyShouldUseTheUsingThiefEffectStrategyMethod() {
        spyCustomBot.setAvailableCharacters(List.of(new Thief(), new King(), new Bishop()));
        spyCustomBot.useEffectThief();
        verify(mockUsingThiefEffectStrategy).apply(spyCustomBot);
    }

    @Test
    void useThiefEffectWithoutAUsingThiefEffectStrategyShouldCallTheSuperMethod() {
        spyCustomBot.usingThiefEffectStrategy = null;
        spyCustomBot.setAvailableCharacters(List.of(new Thief(), new King(), new Bishop()));
        spyCustomBot.useEffectThief();
        verify(spyCustomBot).randomUseThiefEffect();
    }

    @Test
    void useAssassinEffectWithAUsingMurdererEffectStrategyShouldUseTheUsingMurdererEffectStrategyMethod() {
        spyCustomBot.setAvailableCharacters(List.of(new Assassin(), new King(), new Bishop()));
        spyCustomBot.useEffectAssassin();
        verify(mockUsingMurdererEffectStrategy).apply(spyCustomBot, mockView);
    }

    @Test
    void useAssassinEffectWithoutAUsingMurdererEffectStrategyShouldCallTheSuperMethod() {
        spyCustomBot.usingMurdererEffectStrategy = null;
        spyCustomBot.setAvailableCharacters(List.of(new Assassin(), new King(), new Bishop()));
        spyCustomBot.useEffectAssassin();
        verify(spyCustomBot).randomUseMurdererEffect();
    }

    @Test
    void useWarlordEffectWithAUsingWarlordEffectStrategyShouldUseTheUsingWarlordEffectStrategyMethod() {
        Opponent mockOpponent = mock(Opponent.class);
        spyCustomBot.chooseWarlordTarget(List.of(mockOpponent));
        verify(mockUsingWarlordEffectStrategy).apply(spyCustomBot, List.of(mockOpponent));
    }

    @Test
    void useWarlordEffectWithoutAUsingWarlordEffectStrategyShouldCallTheSuperMethod() {
        spyCustomBot.usingWarlordEffectStrategy = null;
        Opponent mockOpponent = mock(Opponent.class);
        spyCustomBot.chooseWarlordTarget(List.of(mockOpponent));
        verify(spyCustomBot).randomUseWarlordEffect(List.of(mockOpponent));
    }

    @Test
    void chooseCharacterLinksThePlayerAndTheCharacter() {
        ICharacterChoosingStrategy spyChoosingStrategy = spy(new ChoosingCharacterToTargetFirstPlayer());
        CustomBot customBotWithARealChoosingStrat = new CustomBot(2, null, mock(IView.class), mock(Bank.class),
                mockPickingStrategy,
                spyChoosingStrategy,
                mockUsingThiefEffectStrategy,
                mockUsingMurdererEffectStrategy,
                mockUsingWarlordEffectStrategy,
                mockCardChoosingStrategy);
        Opponent opponent = mock(Opponent.class);
        when(opponent.getNbGold()).thenReturn(2);
        customBotWithARealChoosingStrat.setOpponents(List.of(opponent));

        Character assassin = new Assassin();
        List<Character> characters = new ArrayList<>(List.of(assassin, new Merchant()));

        customBotWithARealChoosingStrat.chooseCharacter(characters);

        verify(spyChoosingStrategy).apply(customBotWithARealChoosingStrat, characters);
        assertTrue(characters.contains(customBotWithARealChoosingStrat.getCharacter()));
        assertEquals(customBotWithARealChoosingStrat.getCharacter(), assassin);
        assertEquals(customBotWithARealChoosingStrat, assassin.getPlayer());
    }

    @Test
    void testNotEqualsWithBotWithSameStrategies() {
        CustomBot customBot1 = new CustomBot(2, new Deck(), mock(IView.class), mock(Bank.class),
                mockPickingStrategy,
                mockCharacterChoosingStrategy,
                mockUsingThiefEffectStrategy,
                mockUsingMurdererEffectStrategy,
                mockUsingWarlordEffectStrategy,
                mockCardChoosingStrategy);
        CustomBot customBot2 = new CustomBot(2, new Deck(), mock(IView.class), mock(Bank.class),
                mockPickingStrategy,
                mockCharacterChoosingStrategy,
                mockUsingThiefEffectStrategy,
                mockUsingMurdererEffectStrategy,
                mockUsingWarlordEffectStrategy,
                mockCardChoosingStrategy);
        assertNotEquals(customBot1, customBot2);
    }

    @Test
    void testNotEqualsWithBotWithNoStrategies() {
        CustomBot customBot1 = new CustomBot(2, new Deck(), mock(IView.class), mock(Bank.class));
        CustomBot customBot2 = new CustomBot(2, new Deck(), mock(IView.class), mock(Bank.class));
        assertNotEquals(customBot1, customBot2);
    }

    @Test
    void testNotEqualsWithBotWithDifferentStrategies() {
        CustomBot customBot1 = new CustomBot(2, new Deck(), mock(IView.class), mock(Bank.class),
                mockPickingStrategy,
                mockCharacterChoosingStrategy,
                mockUsingThiefEffectStrategy,
                mockUsingMurdererEffectStrategy,
                mockUsingWarlordEffectStrategy,
                mockCardChoosingStrategy);
        CustomBot customBot2 = new CustomBot(2, new Deck(), mock(IView.class), mock(Bank.class),
                mockPickingStrategy,
                mockCharacterChoosingStrategy,
                mockUsingThiefEffectStrategy,
                null,
                mockUsingWarlordEffectStrategy,
                mockCardChoosingStrategy);
        assertNotEquals(customBot1, customBot2);
    }

    @Test
    void testNotEqualsWithNotSameObject() {
        CustomBot customBot1 = new CustomBot(2, new Deck(), mock(IView.class), mock(Bank.class),
                mockPickingStrategy,
                mockCharacterChoosingStrategy,
                mockUsingThiefEffectStrategy,
                mockUsingMurdererEffectStrategy,
                mockUsingWarlordEffectStrategy,
                mockCardChoosingStrategy);
        Character assassin = new Assassin();
        assertNotEquals(customBot1, assassin);
    }

    @Test
    void testHashCode() {
        CustomBot customBot1 = new CustomBot(2, new Deck(), mock(IView.class), mock(Bank.class),
                mockPickingStrategy,
                mockCharacterChoosingStrategy,
                mockUsingThiefEffectStrategy,
                mockUsingMurdererEffectStrategy,
                mockUsingWarlordEffectStrategy,
                mockCardChoosingStrategy);
        CustomBot customBot2 = new CustomBot(2, new Deck(), mock(IView.class), mock(Bank.class),
                mockPickingStrategy,
                mockCharacterChoosingStrategy,
                mockUsingThiefEffectStrategy,
                mockUsingMurdererEffectStrategy,
                mockUsingWarlordEffectStrategy,
                mockCardChoosingStrategy);
        assertNotEquals(customBot1.hashCode(), customBot2.hashCode()); // the two bots are differents
    }

    @Test
    void testToString() {
        assertEquals("Le bot custom " + spyCustomBot.getId(), spyCustomBot.toString());
    }

    @Test
    void testCardChosingStrategyWhenChoosingACard() {
        this.spyCustomBot.chooseCard();
        verify(mockCardChoosingStrategy, times(1)).apply(any());
    }

    @Test
    void testIsNotCallingTheCardChoosingStrategyBecauseThePlayerHasNoStrategy() {
        this.spyCustomBot.cardChoosingStrategy = null;
        this.spyCustomBot.chooseCard();
        verify(mockCardChoosingStrategy, times(0)).apply(any());
        verify(this.spyCustomBot, times(1)).randomChooseCard();
    }
}