package com.seinksansdoozebank.fr.model.player;

import com.seinksansdoozebank.fr.model.bank.Bank;
import com.seinksansdoozebank.fr.model.cards.Deck;
import com.seinksansdoozebank.fr.model.character.abstracts.Character;
import com.seinksansdoozebank.fr.model.character.roles.Role;
import com.seinksansdoozebank.fr.model.character.specialscharacters.MagicianTarget;
import com.seinksansdoozebank.fr.model.player.custombot.strategies.StrategyUtils;
import com.seinksansdoozebank.fr.view.IView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * The RichardBot class represents a bot that makes decisions to adapt to strategy of community
 */
public class RichardBot extends SmartBot {
    /**
     * RichardBot constructor
     *
     * @param nbGold the number of gold
     * @param deck   the deck
     * @param view   the view
     * @param bank   the bank
     */
    public RichardBot(int nbGold, Deck deck, IView view, Bank bank) {
        super(nbGold, deck, view, bank);
    }

    boolean anOpponentIsAboutToWin() {
        return this.getOpponents().stream().anyMatch(Opponent::isAboutToWin);
    }

    @Override
    protected Optional<Character> chooseThiefTarget() {
        // get from getAvailableCharacters the characters that are not dead
        List<Role> charactersInTheRound = this.getAvailableCharacters()
                .stream()
                .filter(character -> !character.isDead())
                .map(Character::getRole)
                .toList();
        if (this.anOpponentIsAboutToWin()) {
            if (charactersInTheRound.contains(Role.BISHOP)) {
                return Optional.ofNullable(StrategyUtils.getCharacterFromRoleInList(Role.BISHOP, this.getAvailableCharacters()));
            } else if (charactersInTheRound.contains(Role.WARLORD)) {
                return Optional.ofNullable(StrategyUtils.getCharacterFromRoleInList(Role.WARLORD, this.getAvailableCharacters()));
            }
        }
        return useSuperChoseThiefEffect();
    }

    Optional<Character> useSuperChoseThiefEffect() {
        return super.chooseThiefTarget();
    }

    @Override
    protected Character chooseAssassinTarget() {
        List<Character> charactersList = this.getAvailableCharacters();
        // Conditions spécifiques pour Voleur et Warlord
        Character target = null;
        for (Character character : charactersList) {
            if (character.getRole() == Role.THIEF && (shouldPreventWealth() || thinkThiefHasBeenChosenByTheLeadingOpponent())
                    || character.getRole() == Role.WARLORD && (this.isAboutToWin() || thinkWarlordHasBeenChosenByTheLeadingOpponent())) {
                target = character;
            }
        }
        if (target == null) {
            return super.chooseAssassinTarget();
        }
        return target;
    }

    /**
     * Vérifie si un adversaire a un grand nombre de pièces d'or (7 ou plus)
     *
     * @return true si un adversaire a 7 pièces d'or ou plus, false sinon
     */
    boolean shouldPreventWealth() {
        return this.getOpponents().stream().anyMatch(opponent -> opponent.getNbGold() > 7);
    }

    /**
     * Check if the Warlord will be chosen by the leading opponent
     *
     * @return true if the player think Warlord has been chosen by the leading opponent, false otherwise
     */
    boolean thinkWarlordHasBeenChosenByTheLeadingOpponent() {
        // if leadingOpponent is about to win, he will choose Warlord or Bishop, but the bishop is not killable
        return StrategyUtils.getLeadingOpponent(this.getOpponents()).isAboutToWin();
    }

    /**
     * Check if the Thief has been chosen by the leading opponent
     *
     * @return true if the player think Thief has been chosen by the opponent about to win, false otherwise
     */
    boolean thinkThiefHasBeenChosenByTheLeadingOpponent() {
        if (StrategyUtils.isRoleInCharacterList(Role.THIEF, this.getCharactersNotInRound())) { // if thief is not in the round, we return false (because he can't be chosen)
            return false;
        }
        if (StrategyUtils.isRoleInCharacterList(Role.THIEF, this.getCharactersSeenInRound())) { // if thief has been seen (means that he has been chosen after the player)
            return this.getOpponentsWhichHasChosenCharacterAfter().stream().anyMatch(Opponent::isAboutToWin); // we check if the opponent which is about to win has chosen after the player (could mean that he has chosen the thief)
        } else {
            return this.getOpponentsWhichHasChosenCharacterBefore().stream().anyMatch(Opponent::isAboutToWin); // if the thief has not been seen, we check if the opponents which has chosen before the player are about to win (could mean that the thief has been chosen by one of them)
        }
    }

    /**
     * Get the opponents which has chosen their character before the player
     * make difference between this.getOpponents() and this.getOpponentsWhichHasChosenCharacterBefore()
     *
     * @return the opponents which has chosen their character before the player
     */
    List<Opponent> getOpponentsWhichHasChosenCharacterAfter() {
        return this.getOpponents().stream().filter(opponent -> !this.getOpponentsWhichHasChosenCharacterBefore().contains(opponent)).toList();
    }

    /**
     * Cette méthode nous permet de choisir notre personnage en prenant en compte les cas où un opposant est sur le point
     * de poser son dernier district et de gagner. On doit évaluer si l'opposant doit choisir
     * son caractère en premier deuxième ou troisième et faire en fonction
     *
     * @param characters list of available characters
     * @param opponent   the opponent who gets 7 districts in it citadel
     * @return an optional of the character that will be assigned to the current player
     */
    Optional<Character> chooseCharacterWhenOpponentHasOneDistrictLeft(List<Character> characters, Opponent opponent) {
        //Si l'opposant est deuxième à choisir son role alors, on doit choisir l'assassin
        if (opponent.getPositionInDrawToPickACharacter() == 1 && StrategyUtils.isRoleInCharacterList(Role.ASSASSIN, characters)) {
            return Optional.of(StrategyUtils.getCharacterFromRoleInList(Role.ASSASSIN, characters));
        }
        //Cas où l'opposant est 3ème à choisir
        if (opponent.getPositionInDrawToPickACharacter() == 2) {
            if (StrategyUtils.isRoleInCharacterList(Role.KING, characters)) {
                return Optional.of(StrategyUtils.getCharacterFromRoleInList(Role.KING, characters));
            }
            if (StrategyUtils.isRoleInCharacterList(Role.BISHOP, characters) &&
                    StrategyUtils.isRoleInCharacterList(Role.WARLORD, characters) &&
                    StrategyUtils.isRoleInCharacterList(Role.ASSASSIN, characters)) {
                return whenCharacterContainsBishopWarlordAssassin(characters);
            }
            if (!StrategyUtils.isRoleInCharacterList(Role.WARLORD, characters)) {
                return whenCharacterDoesNotContainWarlord(characters);
            } else if (!StrategyUtils.isRoleInCharacterList(Role.BISHOP, characters)) {
                return whenCharacterDoesNotContainBishop(characters);
            } else {
                return whenCharacterDoesNotContainAssassin(characters);
            }
        }
        if (StrategyUtils.isRoleInCharacterList(Role.ASSASSIN, characters)) {
            return Optional.of(StrategyUtils.getCharacterFromRoleInList(Role.ASSASSIN, characters));
        }
        return Optional.of(characters.get(random.nextInt(characters.size())));
    }

    /**
     * Cas où l'évêque, l'assassin, condottière n'a pas été pris
     * Prendre en compte le rang du perso actuel
     *
     * @return an optional of the character
     */
    Optional<Character> whenCharacterContainsBishopWarlordAssassin(List<Character> characters) {
        if (this.getPositionInDrawToPickACharacter() == 0 && StrategyUtils.isRoleInCharacterList(Role.WARLORD, characters)) {
            return Optional.ofNullable(StrategyUtils.getCharacterFromRoleInList(Role.WARLORD, characters));
        } else if (this.getPositionInDrawToPickACharacter() == 1 && StrategyUtils.isRoleInCharacterList(Role.ASSASSIN, characters)) {
            return Optional.ofNullable(StrategyUtils.getCharacterFromRoleInList(Role.ASSASSIN, characters));
        }
        if (StrategyUtils.isRoleInCharacterList(Role.WARLORD, characters)) {
            return Optional.ofNullable(StrategyUtils.getCharacterFromRoleInList(Role.WARLORD, characters));
        }
        return Optional.ofNullable(StrategyUtils.getRandomCharacterFromList(characters));
    }

    /**
     * Cas où le condottière n'est pas présente dans la liste des perso disponibles
     *
     * @return an optional of the character
     */
    Optional<Character> whenCharacterDoesNotContainWarlord(List<Character> characters) {
        if (this.getPositionInDrawToPickACharacter() == 0 && StrategyUtils.isRoleInCharacterList(Role.ASSASSIN, characters)) {
            return Optional.of(StrategyUtils.getCharacterFromRoleInList(Role.ASSASSIN, characters));
        } else if (this.getPositionInDrawToPickACharacter() == 1 && StrategyUtils.isRoleInCharacterList(Role.MAGICIAN, characters)) {
            return Optional.of(StrategyUtils.getCharacterFromRoleInList(Role.MAGICIAN, characters));
        }
        if (StrategyUtils.isRoleInCharacterList(Role.ASSASSIN, characters)) {
            return Optional.of(StrategyUtils.getCharacterFromRoleInList(Role.ASSASSIN, characters));
        }
        return Optional.of(StrategyUtils.getRandomCharacterFromList(characters));
    }

    /**
     * Cas où l'évêque n'est pas présente dans la liste des perso disponibles
     *
     * @return an optional of the character
     */
    Optional<Character> whenCharacterDoesNotContainBishop(List<Character> characters) {
        if (this.getPositionInDrawToPickACharacter() == 0 && StrategyUtils.isRoleInCharacterList(Role.ASSASSIN, characters)) {
            return Optional.of(StrategyUtils.getCharacterFromRoleInList(Role.ASSASSIN, characters));
        } else if (this.getPositionInDrawToPickACharacter() == 1 && StrategyUtils.isRoleInCharacterList(Role.WARLORD, characters)) {
            return Optional.of(StrategyUtils.getCharacterFromRoleInList(Role.WARLORD, characters));
        }
        if (StrategyUtils.isRoleInCharacterList(Role.ASSASSIN, characters)) {
            return Optional.of(StrategyUtils.getCharacterFromRoleInList(Role.ASSASSIN, characters));
        }
        return Optional.of(StrategyUtils.getRandomCharacterFromList(characters));
    }

    /**
     * Cas où l'assassin n'est pas présente dans la liste des perso disponibles
     *
     * @return an optional of the character
     */
    Optional<Character> whenCharacterDoesNotContainAssassin(List<Character> characters) {
        if (this.getPositionInDrawToPickACharacter() == 0 && StrategyUtils.isRoleInCharacterList(Role.WARLORD, characters)) {
            return Optional.of(StrategyUtils.getCharacterFromRoleInList(Role.WARLORD, characters));
        } else if (this.getPositionInDrawToPickACharacter() == 1 && StrategyUtils.isRoleInCharacterList(Role.BISHOP, characters)) {
            return Optional.of(StrategyUtils.getCharacterFromRoleInList(Role.BISHOP, characters));
        }
        if (StrategyUtils.isRoleInCharacterList(Role.WARLORD, characters)) {
            return Optional.of(StrategyUtils.getCharacterFromRoleInList(Role.WARLORD, characters));
        }
        return Optional.of(StrategyUtils.getRandomCharacterFromList(characters));
    }

    @Override
    public Character chooseCharacterImpl(List<Character> characters) {
        Optional<Character> optionalCharacter;
        Optional<Opponent> optionalOpponent = getOpponents().stream().filter(opponent -> opponent.getCitadel().size() == 7).findFirst();
        if (optionalOpponent.isPresent()) {
            optionalCharacter = chooseCharacterWhenOpponentHasOneDistrictLeft(characters, optionalOpponent.get());
            if (optionalCharacter.isPresent() && characters.contains(optionalCharacter.get())) {
                return optionalCharacter.get();
            }
        }
        List<Character> orderedCharacters = ordinateCharacters(characters);
        optionalCharacter = shouldChooseBecauseLastCardToBuy(characters);
        if (optionalCharacter.isPresent()) {
            return optionalCharacter.get();
        }
        for (Character character : orderedCharacters) {
            switch (character.getRole()) {
                case ASSASSIN -> {
                    if (shouldChooseAssassin()) {
                        return character;
                    }
                }
                case MAGICIAN -> {
                    if (shouldChooseMagician()) {
                        return character;
                    }
                }
                case MERCHANT -> {
                    if (shouldChooseMerchant()) {
                        return character;
                    }
                }
                case ARCHITECT -> {
                    if (shouldChooseArchitect()) {
                        return character;
                    }
                }
                case BISHOP -> {
                    if (shouldChooseBishop()) {
                        return character;
                    }
                }
                case WARLORD -> {
                    if (shouldChooseWarlord()) {
                        return character;
                    }
                }
                default -> {
                    break;
                }
            }
        }
        return super.chooseCharacterImpl(characters);
    }

    /**
     * @param characters list of available characters
     * @return the list of available characters ordered like we want
     */
    List<Character> ordinateCharacters(List<Character> characters) {
        List<Character> copyCharacters = new ArrayList<>(characters);
        List<Character> orderedCharacters = new ArrayList<>();

        Optional<Character> optionalCharacter = copyCharacters.stream().filter(c -> c.getRole() == Role.ASSASSIN).findFirst();
        optionalCharacter.ifPresent(assassin -> {
            orderedCharacters.add(assassin);
            copyCharacters.remove(assassin);
        });
        optionalCharacter = copyCharacters.stream().filter(c -> c.getRole() == Role.MAGICIAN).findFirst();
        optionalCharacter.ifPresent(magician -> {
            orderedCharacters.add(magician);
            copyCharacters.remove(magician);
        });
        optionalCharacter = copyCharacters.stream().filter(c -> c.getRole() == Role.MERCHANT).findFirst();
        optionalCharacter.ifPresent(merchant -> {
            orderedCharacters.add(merchant);
            copyCharacters.remove(merchant);
        });
        optionalCharacter = copyCharacters.stream().filter(c -> c.getRole() == Role.ARCHITECT).findFirst();
        optionalCharacter.ifPresent(architect -> {
            orderedCharacters.add(architect);
            copyCharacters.remove(architect);
        });
        optionalCharacter = copyCharacters.stream().filter(c -> c.getRole() == Role.BISHOP).findFirst();
        optionalCharacter.ifPresent(bishop -> {
            orderedCharacters.add(bishop);
            copyCharacters.remove(bishop);
        });
        optionalCharacter = copyCharacters.stream().filter(c -> c.getRole() == Role.WARLORD).findFirst();
        optionalCharacter.ifPresent(warlord -> {
            orderedCharacters.add(warlord);
            copyCharacters.remove(warlord);
        });
        List<Character> charactersRemaining = new ArrayList<>(copyCharacters);

        orderedCharacters.addAll(charactersRemaining);

        return orderedCharacters;
    }


    /**
     * find the number of players with empty hands in the game
     *
     * @param opponents list of the opponents of the player
     * @return the number of players with empty hands in the game
     */
    int numberOfEmptyHands(List<Opponent> opponents) {
        return (int) opponents.stream().filter(player -> player.getHandSize() == 0).count();
    }

    /**
     * tells us if players with more gold than the bot exist
     *
     * @param opponents list of the opponents of the player
     * @return a boolean
     */
    boolean numberOfPlayerWithMoreGold(List<Opponent> opponents) {
        return opponents.stream().anyMatch(opponent -> this.getNbGold() < opponent.getNbGold());
    }

    /**
     * La méthode check si le joueur a plus de 7 cartes dans la main et si les autres joueurs ont des mains vides et renvoie un booléen.
     * Dans ce cas, il choisit l'assassin.
     *
     * @return a boolean
     */
    boolean shouldChooseAssassin() {
        return getHand().size() >= 7 && numberOfEmptyHands(this.getOpponents()) >= 1;
    }

    /**
     * La méthode check si le joueur a une main vide
     * Dans ce cas, il choisit le magicien.
     *
     * @return a boolean
     */
    boolean shouldChooseMagician() {
        return this.getHand().isEmpty();
    }

    /**
     * La méthode check si le nombre d'or du joueur est inférieur ou égal à 1.
     * Dans ce cas, il choisit le marchand.
     *
     * @return a boolean
     */
    boolean shouldChooseMerchant() {
        return this.getNbGold() <= 1;
    }

    /**
     * La méthode check si le joueur peut poser deux districts en plus dans sa citadelle et si ses opposants ont plus d'or que lui.
     * Dans ce cas, il choisit l'architecte.
     *
     * @return a boolean
     */
    boolean shouldChooseArchitect() {
        return getPriceOfNumbersOfCheaperCards(2) <= this.getNbGold() && numberOfPlayerWithMoreGold(this.getOpponents());
    }


    /**
     * La méthode check si le joueur peut poser au moins un district
     * Dans ce cas, il choisit l'évêque.
     *
     * @return a boolean
     */
    boolean shouldChooseBishop() {
        return getPriceOfNumbersOfCheaperCards(1) <= this.getNbGold();
    }

    /**
     * La méthode check si le joueur ne peut pas construire de district.
     * Dans ce cas, il choisit le condottière.
     *
     * @return a boolean
     */
    boolean shouldChooseWarlord() {
        return getPriceOfNumbersOfCheaperCards(1) > this.getNbGold();
    }

    /**
     * Cette méthode permet au joueur s'il est en position de poser son dernier district
     * dans sa cité de choisir soit l'assassin, soit l'évêque, soit le condottière.
     *
     * @param characters list of characters available
     * @return an optional of character
     */
    Optional<Character> shouldChooseBecauseLastCardToBuy(List<Character> characters) {
        if (isAboutToWin()) {
            return characters.stream().filter(c -> c.getRole() == Role.ASSASSIN || c.getRole() == Role.BISHOP || c.getRole() == Role.WARLORD).findFirst();
        }
        return Optional.empty();
    }

    /**
     * Use the magician effect to switch hand with the opponent which has the most districts
     */
    @Override
    public MagicianTarget useEffectMagician() {
        Opponent leadingOpponent = StrategyUtils.getLeadingOpponent(this.getOpponents());
        if (leadingOpponent.isAboutToWin()) {
            this.view.displayPlayerUseMagicianEffect(this, leadingOpponent);
            return new MagicianTarget(leadingOpponent, null);
        }
        Optional<Opponent> playerWithMostDistricts = this.getOpponents().stream()
                .max(Comparator.comparingInt(Opponent::getHandSize));

        if (playerWithMostDistricts.isPresent()) {
            this.view.displayPlayerUseMagicianEffect(this, playerWithMostDistricts.get());
            return new MagicianTarget(playerWithMostDistricts.get(), null);
        }
        return null;
    }


    @Override
    public String toString() {
        return "Le bot Richard " + this.id;
    }

}
