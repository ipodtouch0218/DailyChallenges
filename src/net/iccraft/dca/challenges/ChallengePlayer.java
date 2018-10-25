package net.iccraft.dca.challenges;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import net.iccraft.dca.DCAMain;
import net.iccraft.dca.objects.ChallengeData;
import net.iccraft.dca.objects.Reward;

public class ChallengePlayer {

	private ArrayList<ChallengeData> challenges = new ArrayList<ChallengeData>();
	private UUID uuid;
	private int creationDay;
	private int focusedChallenge;
	
	public ChallengePlayer(UUID uuid) {
		this.uuid = uuid;
		this.creationDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
		this.focusedChallenge = -1;
	}
	
	//---Getters---//
	public ArrayList<ChallengeData> getChallengeData() { 
		whileLoop:
		while (challenges.size() < 4) {
			Challenge chal = Challenge.getRandomChallenge();
			
			int repeatedCount = 0;
			for (ChallengeData existing : challenges) {
				if (chal.equals(existing.getChallenge())) repeatedCount++;
				if (repeatedCount >= 1) {
					continue whileLoop;
				}
			}
			challenges.add(new ChallengeData(chal, Reward.getRandomReward()));
		}
		return challenges; 
	}
	public Player getPlayer() { return Bukkit.getPlayer(uuid); }
	public UUID getUniqueId() { return uuid; }
	public int getCreationDay() { return creationDay; }
	public int getFocusedChallenge() { return focusedChallenge; }
	
	//---Setters---//
	public void setFocusedChallenge(int value) { focusedChallenge = value; }
	
	//---Serialization---//
	public void saveToFile(File file) {
		FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);
		
		fileConfig.set("day-of-year", creationDay);
		fileConfig.set("focused-challenge", focusedChallenge);
		
		for (int i = 0; i < 4; i++) {
			if (challenges.size() < i+1) continue;
			ConfigurationSection section = fileConfig.createSection("challenges." + i);
			ChallengeData data = challenges.get(i);
			
			section.set("challenge.id", data.getChallenge().getID());
			section.set("challenge.required", data.getRequired());
			section.set("challenge.score", data.getScore());
			section.set("reward.id", data.getReward().getID());
			section.set("reward.amount", data.getRewardAmount());
		}
		
		try {
			fileConfig.save(file);
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	public static ChallengePlayer loadFromFile(UUID uuid, File file) {
		ChallengePlayer newPlayer = new ChallengePlayer(uuid);
		FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);
		
		if (fileConfig.isConfigurationSection("challenges")) {
			if (DCAMain.getDayOfYear() == fileConfig.getInt("day-of-year"))
			for (int i = 0; i < 4; i++) {
				ConfigurationSection section = fileConfig.getConfigurationSection("challenges." + i);
				Challenge challenge = Challenge.fromID(section.getString("challenge.id"));
				
				if (challenge == null) continue;
				
				int challengeRequired = section.getInt("challenge.required");
				int challengeScore = section.getInt("challenge.score");
				Reward reward = Reward.fromID(section.getString("reward.id"));
				int rewardAmount = section.getInt("reward.amount");
				
				ChallengeData data = new ChallengeData(challenge, challengeScore, challengeRequired, reward, rewardAmount);
				newPlayer.challenges.add(data);
			}
		}
		int focused = fileConfig.isSet("focused-challenge") ? fileConfig.getInt("focused-challenge") : -1;
		newPlayer.setFocusedChallenge(focused);
			
		return newPlayer;
	}
}
