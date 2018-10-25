package net.iccraft.dca.objects;

import net.iccraft.dca.challenges.ChallengePlayer;

public interface RewardExecutor {

	public void onReward(ChallengePlayer player, int amount);
	
}
