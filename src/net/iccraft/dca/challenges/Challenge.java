package net.iccraft.dca.challenges;

import java.util.Collection;
import java.util.HashMap;

import net.iccraft.dca.Utils;

public class Challenge {

	private static HashMap<String, Challenge> challengeList = new HashMap<String, Challenge>();
	private String id;
	
	private String displayName;
	private String description;
	private int[] possibleRequired;
	private ChallengeHandler handler;
	
	public Challenge(String displayName, String description, int[] possibleScores, ChallengeHandler handler) {
		this.displayName = displayName;
		this.description = description;
		this.possibleRequired = possibleScores;
		this.handler = handler;
	}
	
	public Challenge register(String id) {
		String capID = id.toUpperCase() + "_CHALLENGE";
		challengeList.put(capID, this);
		this.id = capID;
		return this;
	}
	
	//---Getters---//
	public String getID() { return id; }
	public String getDisplayName() { return displayName; }
	public String getDescription() { return description; }
	public int[] getPossibleRequired() { return possibleRequired; }
	public int getRandomRequired() { return possibleRequired[Utils.chooseNumber(0, possibleRequired.length)]; }
	public ChallengeHandler getHandler() { return handler; }
	
	//---Static---//
	public static Collection<Challenge> getAllChallenges() { return challengeList.values(); }
	public static Challenge fromID(String id) { return challengeList.get(id); }
	public static Challenge getRandomChallenge() { return challengeList.values().toArray(new Challenge[]{})[Utils.chooseNumber(0, challengeList.size())]; }
}
