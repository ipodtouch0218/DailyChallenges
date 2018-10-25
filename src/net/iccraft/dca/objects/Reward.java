package net.iccraft.dca.objects;

import java.util.HashMap;

import net.iccraft.dca.Utils;
import net.iccraft.dca.challenges.ChallengePlayer;
import net.iccraft.dca.commands.DailyChallengesCMD;

public class Reward {

	private static HashMap<String, Reward> rewardList = new HashMap<String, Reward>();
	private String id;
	
	private RewardExecutor rewardExecutor;
	private int[] possibleValues;
	private String displayName;
	
	public Reward(String displayName, int[] possibleValues, RewardExecutor rewardExecutor) {
		this.displayName = displayName;
		this.possibleValues = possibleValues;
		this.rewardExecutor = rewardExecutor;
	}
	
	public Reward register(String id) {
		String idCap = id.toUpperCase() + "_REWARD";
		rewardList.put(idCap, this);
		this.id = idCap;
		return this;
	}
	
	public void giveReward(ChallengePlayer player) { giveReward(player, getRandomValue()); }
	public void giveReward(ChallengePlayer player, int amount) {
		rewardExecutor.onReward(player, amount);
		player.getPlayer().sendMessage(DailyChallengesCMD.prefix + "You have completed a challenge and earned §f" + displayName.replace("%amount%", amount + ""));
	}
	
	//---Getters---//
	public String getDisplayName() { return displayName; }
	public String getID() { return id; }
	public RewardExecutor getRewardExecutor() { return rewardExecutor; }
	public int[] getPossibleValues() { return possibleValues; }
	public int getRandomValue() { return Utils.chooseNumber(possibleValues); }
	
	//---Static---//
	public static Reward getRandomReward() { return rewardList.values().toArray(new Reward[]{})[Utils.chooseNumber(0, rewardList.size())]; }
	public static Reward fromID(String id) { return rewardList.get(id); }
	
}
