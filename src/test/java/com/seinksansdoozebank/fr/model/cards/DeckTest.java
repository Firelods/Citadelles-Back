package com.seinksansdoozebank.fr.model.cards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeckTest {

    private Deck districtList;
    private Deck districtListTest;

    @BeforeEach
    void setup() {
        districtList = new Deck();
        districtListTest = new Deck();
    }

    /**
     * We verify that when the deck is created it has got 65 districts cards and the right number of each district
     */

    @Test
    void fillDeckTest() {

        assertEquals(65, districtList.getDeck().size());

        for (District district : District.values()) {
            assertEquals(district.getNumberOfAppearance(), Collections.frequency(districtList.getDeck(), district));
        }

    }


    /**
     * We verify that the cost attributed to each district is between 1 and 5
     */
    @Test
    void getDistrictCostWIthDeckTest() {
        for (int i = 0; i < 65; i++) {
            assertTrue(districtList.getDeck().get(i).getCost() >= 1);
            assertTrue(districtList.getDeck().get(i).getCost() <= 6);
        }
    }

    /**
     * We verify when the list of districts is shuffled that it has differences with the one which is not shuffle.
     */
    @Test
    void shuffleTest() {
        districtListTest.shuffle();
        int differencesCount = 0;
        for (int i = 0; i < 65; i++) {
            if (districtListTest.getDeck().get(i).compareTo(districtList.getDeck().get(i)) != 0) {
                differencesCount++;
            }
        }
        assertTrue(differencesCount > 0);
    }

    /**
     * We verify that if the list is empty, the deck is built again
     * This method will be improved with the milestone 2
     */
    @Test
    void pickDistrictTest() {
        for (int i = 0; i < 65; i++) {
            districtList.pick();
        }
        districtList.pick();
        assertEquals(64, districtList.getDeck().size());
    }

    @Test
    void discard(){
        District districtToDiscard = District.MANOR;
        districtList.discard(districtToDiscard);
        assertEquals(districtToDiscard, districtList.getDeck().get(0));
    }
}
