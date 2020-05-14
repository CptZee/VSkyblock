package com.github.Viduality.VSkyblock.Commands.Challenges;

/*
 * VSkyblock
 * Copyright (C) 2020  Viduality
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class ChallengesCreator {

    private final VSkyblock plugin = VSkyblock.getInstance();

    private final String ANSI_RED = "\u001B[31m";
    private final String ANSI_RESET = "\u001B[0m";

    public boolean createAllChallenges() {
        if (createChallenges(Challenge.Difficulty.EASY)) {
            if (createChallenges(Challenge.Difficulty.MEDIUM)) {
                if (createChallenges(Challenge.Difficulty.HARD)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean createChallenges(Challenge.Difficulty diff) {
        String difficulty = getDifficulty(diff);
        int highestSlot = 0;
        if (difficulty != null) {
            Set<String> challenges = ConfigShorts.getChallengesConfig().getConfigurationSection(difficulty).getKeys(false);
            HashMap<Integer, Challenge> slotsEasy = new HashMap<>();
            HashMap<Integer, Challenge> slotsMedium = new HashMap<>();
            HashMap<Integer, Challenge> slotsHard = new HashMap<>();
            for (String currentChallenge : challenges) {
                Challenge challenge = new Challenge();
                String challengeName = ConfigShorts.getChallengesConfig().getString(difficulty + "." + currentChallenge + ".Name");
                String challengeType = ConfigShorts.getChallengesConfig().getString(difficulty + "." + currentChallenge + ".Type");
                String shownItem = ConfigShorts.getChallengesConfig().getString(difficulty + "." + currentChallenge + ".ShownItem");
                String description = ConfigShorts.getChallengesConfig().getString(difficulty + "." + currentChallenge + ".Description");
                int slot = ConfigShorts.getChallengesConfig().getInt(difficulty + "." + currentChallenge + ".Slot");
                int radius = ConfigShorts.getChallengesConfig().getInt(difficulty + "." + currentChallenge + ".Radius");
                String neededText = ConfigShorts.getChallengesConfig().getString(difficulty + "." + currentChallenge + ".NeededText");
                String rewardText = ConfigShorts.getChallengesConfig().getString(difficulty + "." + currentChallenge + ".RewardText");
                String repeatRewardText = ConfigShorts.getChallengesConfig().getString(difficulty + "." + currentChallenge + ".RepeatRewardText");

                List<String> neededItems = ConfigShorts.getChallengesConfig().getStringList( difficulty + "." + currentChallenge + ".Needed");
                int neededLevel = ConfigShorts.getChallengesConfig().getInt(difficulty + "." + currentChallenge + ".Needed");
                List<String> rewards = ConfigShorts.getChallengesConfig().getStringList( difficulty + "." + currentChallenge + ".Reward");
                List<String> repeatRewards = ConfigShorts.getChallengesConfig().getStringList( difficulty + "." + currentChallenge + ".RepeatReward");

                if (challengeType != null) {
                    challenge.setChallengeType(getChallengeType(challengeType));
                }
                if (shownItem != null) {
                    if (isMaterial(shownItem)) {
                        challenge.setShownItem(Material.getMaterial(shownItem.toUpperCase()));
                    }
                }
                challenge.setChallengeName(challengeName);
                challenge.setMySQLKey(currentChallenge);
                challenge.setDifficulty(diff);
                challenge.setDescription(description);
                challenge.setSlot(slot);
                challenge.setRadius(radius);
                challenge.setNeededText(neededText);
                challenge.setRewardText(rewardText);
                challenge.setRepeatRewardText(repeatRewardText);
                challenge.setNeededLevel(neededLevel);
                challenge.setNeededItems(createItems(neededItems));
                challenge.setRewards(createItems(rewards));
                challenge.setRepeatRewards(createItems(repeatRewards));
                if (isChallengeValid(challenge)) {
                    switch (diff) {
                        case EASY:
                            if (slotsEasy.get(challenge.getSlot()) != null) {
                                while (slotsEasy.get(challenge.getSlot()) != null) {
                                    challenge.setSlot(challenge.getSlot() + 1);
                                }
                            }
                            Challenges.challengesEasy.put(challengeName, challenge);
                            slotsEasy.put(challenge.getSlot(), challenge);
                            if (challenge.getSlot() > highestSlot) {
                                highestSlot = challenge.getSlot();
                            }
                            break;
                        case MEDIUM:
                            if (slotsMedium.get(challenge.getSlot()) != null) {
                                while (slotsMedium.get(challenge.getSlot()) != null) {
                                    challenge.setSlot(challenge.getSlot() + 1);
                                }
                            }
                            Challenges.challengesMedium.put(challengeName, challenge);
                            slotsMedium.put(challenge.getSlot(), challenge);
                            if (challenge.getSlot() > highestSlot) {
                                highestSlot = challenge.getSlot();
                            }
                            break;
                        case HARD:
                            if (slotsHard.get(challenge.getSlot()) != null) {
                                while (slotsHard.get(challenge.getSlot()) != null) {
                                    challenge.setSlot(challenge.getSlot() + 1);
                                }
                            }
                            Challenges.challengesHard.put(challengeName, challenge);
                            slotsHard.put(challenge.getSlot(), challenge);
                            if (challenge.getSlot() > highestSlot) {
                                highestSlot = challenge.getSlot();
                            }
                            break;
                    }
                } else {
                    System.out.println(ANSI_RED + "Challenge not valid! Could not set challenge properly! Please check your Challenges file! Challenge: " + challenge.getChallengeName() + ANSI_RESET);
                }
            }
        }
        return sortChallenges(diff, highestSlot);
    }

    private boolean sortChallenges(Challenge.Difficulty difficulty, int highestSlot) {
        HashMap<String, Challenge> challengeHashMap;
        switch (difficulty) {
            case EASY:
                challengeHashMap = Challenges.challengesEasy;
                break;
            case MEDIUM:
                challengeHashMap = Challenges.challengesMedium;
                break;
            case HARD:
                challengeHashMap = Challenges.challengesHard;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + difficulty + " while sorting challenges!");
        }
        List<Challenge> sortedChallenges = Arrays.asList(new Challenge[highestSlot]);
        for (Challenge c : challengeHashMap.values()) {
            sortedChallenges.set(c.getSlot() - 1, c);
        }
        boolean b;
        switch (difficulty) {
            case EASY:
                Challenges.sortedChallengesEasy = sortedChallenges;
                b = true;
                break;
            case MEDIUM:
                Challenges.sortedChallengesMedium = sortedChallenges;
                b = true;
                break;
            case HARD:
                Challenges.sortedChallengesHard = sortedChallenges;
                b = true;
                break;
            default:
                b = false;
        }
        return b;
    }

    private String getDifficulty(Challenge.Difficulty difficulty) {
        switch (difficulty) {
            case EASY: return "Easy";
            case MEDIUM: return "Medium";
            case HARD: return "Hard";
            default: return null;
        }
    }


    private Challenge.ChallengeType getChallengeType(String challengeType) {
        switch (challengeType.toUpperCase()) {
            case "ONPLAYER": return Challenge.ChallengeType.onPlayer;
            case "ONISLAND": return Challenge.ChallengeType.onIsland;
            case "ISLANDLEVEL": return Challenge.ChallengeType.islandLevel;
            default: return null;
        }
    }

    private List<ItemStack> createItems(List<String> items) {
        List<ItemStack> itemStacks = new ArrayList<>();
        for (String currentItem : items) {
            if (currentItem.contains(";")) {
                String[] current = currentItem.split(";");
                if (current.length != 2) {
                    return null;
                }
                if (Material.matchMaterial(current[0].toUpperCase()) != null) {
                    if (isInt(current[1])) {
                        ItemStack itemStack = new ItemStack(Material.getMaterial(current[0].toUpperCase()), Integer.parseInt(current[1]));
                        itemStacks.add(itemStack);
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                if (Material.matchMaterial(currentItem.toUpperCase()) != null) {
                    ItemStack itemStack = new ItemStack(Material.getMaterial(currentItem.toUpperCase()), 1);
                    itemStacks.add(itemStack);
                } else {
                    return null;
                }
            }
        }
        return itemStacks;
    }

    private boolean isMaterial(String material) {
        return Material.matchMaterial(material.toUpperCase()) != null;
    }

    private boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private boolean isChallengeValid(Challenge challenge) {
        if (challenge.getChallengeName() != null &&
        challenge.getMySQLKey() != null &&
        challenge.getChallengeType() != null &&
        challenge.getDescription() != null &&
        challenge.getDifficulty() != null &&
        challenge.getShownItem() != null &&
        challenge.getNeededText() != null &&
        challenge.getRewardText() != null &&
        challenge.getSlot() != null &&
        challenge.getRewards() != null) {
            switch (challenge.getChallengeType()) {
                case islandLevel:
                    if (challenge.getNeededLevel() != null) {
                        return true;
                    }
                case onIsland:
                    if (challenge.getNeededItems() != null &&
                        challenge.getRadius() != null) {
                        return true;
                    }
                case onPlayer:
                    if (challenge.getNeededItems() != null &&
                    challenge.getRepeatRewards() != null &&
                    challenge.getRepeatRewardText() != null) {
                        return true;
                    }
                default: return false;
            }
        }
        return false;
    }
}
