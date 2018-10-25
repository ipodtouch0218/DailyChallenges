package net.iccraft.dca.objects;

import org.bukkit.ChatColor;
import org.bukkit.Sound;

import com.connorlinfoot.actionbarapi.ActionBarAPI;

import net.iccraft.dca.DCAMain;
import net.iccraft.dca.Utils;
import net.iccraft.dca.challenges.Challenge;
import net.iccraft.dca.challenges.ChallengePlayer;

public class ChallengeData {

	private Challenge challenge;
	private int required;
	private int score;
	
	private Reward reward;
	private int rewardAmount;
	
	public ChallengeData(Challenge challenge, Reward reward) {
		this(challenge, 0, challenge.getRandomRequired(), reward, reward.getRandomValue());
	}
	
	public ChallengeData(Challenge challenge, int score, int required, Reward reward, int rewardAmount) {
		this.challenge = challenge;
		this.required = required;
		this.score = score;
		
		this.reward = reward;
		this.rewardAmount = rewardAmount;
	}
	
	public void setScore(int score) { this.score = score; }
	public void addScore(int score, ChallengePlayer player) { 
		if (score == 0) return;
		if (DCAMain.isWorldBlacklisted(player.getPlayer().getWorld())) return;
		this.score+=score; 
		if (this.score >= required) {
			this.score = required;
			reward.giveReward(player, rewardAmount);
			player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
		} else {
			if (player.getFocusedChallenge() == -1) return;
			if (player.getChallengeData().get(player.getFocusedChallenge()) != this) return;
			String name = ChatColor.GOLD + "" + ChatColor.BOLD + challenge.getDisplayName().replaceAll("%amount%", "" + required);
			ActionBarAPI.sendActionBar(player.getPlayer(), name + ": " + Utils.createProgressBar(((double) this.score / (double) required)*100) 
			+ ChatColor.GOLD + " (" + this.score + "/" + required + ")");
		}
	}
	
	//---Getters---//
	public boolean isCompleted() { return score >= required; }
	
	public Challenge getChallenge() { return challenge; }
	public int getRequired() { return required; }
	public int getScore() { return score; }
	
	public Reward getReward() { return reward; }
	public int getRewardAmount() { return rewardAmount; }
	
	
}
