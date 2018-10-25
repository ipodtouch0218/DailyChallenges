package net.iccraft.dca;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.connorlinfoot.actionbarapi.ActionBarAPI;

import net.iccraft.dca.challenges.Challenge;
import net.iccraft.dca.challenges.ChallengeHandler;
import net.iccraft.dca.challenges.ChallengePlayer;
import net.iccraft.dca.commands.DailyChallengesCMD;
import net.iccraft.dca.objects.ChallengeData;
import net.iccraft.dca.objects.Reward;
import net.iccraft.dca.objects.RewardExecutor;

public class DCAMain extends JavaPlugin {

	private ArrayList<ChallengePlayer> challengePlayers = new ArrayList<ChallengePlayer>();
	private static int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR); 
	
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(new EventListeners(this), this);
		getCommand("dailychallenges").setExecutor(new DailyChallengesCMD(this));
		loadRewards();
		loadChallenges();
		
		Bukkit.getOnlinePlayers().forEach(pl -> challengePlayers.add(ChallengePlayer.loadFromFile(pl.getUniqueId(), 
				new File(getDataFolder() + "/players/" + pl.getUniqueId().toString() + ".yml"))));
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				if (dayOfYear != Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
					dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
					Bukkit.broadcastMessage(DailyChallengesCMD.prefix + "Daily Challenges have refreshed! Check §f/dc§7 to see!");
				
					for (Player pl : Bukkit.getOnlinePlayers()) {
						ChallengePlayer clPl = getChallengePlayer(pl.getUniqueId());
						if (clPl.getCreationDay() != dayOfYear) {
							clPl.getChallengeData().clear();
						}
					}
				}
			}
		}, 0L, 20*60L);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				for (ChallengePlayer pl : challengePlayers) {
					if (pl.getPlayer() == null) return;
					if (!pl.getPlayer().getWorld().getName().contains("Survival")) continue;
					if (pl.getFocusedChallenge() == -1) continue;
					ChallengeData data = pl.getChallengeData().get(pl.getFocusedChallenge());
					String name = ChatColor.GOLD + "" + ChatColor.BOLD + data.getChallenge().getDisplayName().replaceAll("%amount%", "" + data.getRequired());
					
					if (data.getScore() >= data.getRequired()) {
						ActionBarAPI.sendActionBar(pl.getPlayer(), name + ": " + ChatColor.GREEN + "✔ Completed", 20*5);
						pl.setFocusedChallenge(-1);
					} else {
						ActionBarAPI.sendActionBar(pl.getPlayer(), name + ": " + Utils.createProgressBar(((double) data.getScore() / (double) data.getRequired())*100) 
						+ ChatColor.GOLD + " (" + data.getScore() + "/" + data.getRequired() + ")");
					}
				}
			}
		}, 0, 20*2L);
	}
	
	@Override
	public void onDisable() {
		for (ChallengePlayer pls : challengePlayers) {
			pls.saveToFile(new File(getDataFolder() + "/players/" + pls.getUniqueId().toString() + ".yml"));
		}
	}
	
	public static int getDayOfYear() { return dayOfYear; }
	public ArrayList<ChallengePlayer> getAllChallengePlayers() { return challengePlayers; }
	public ChallengePlayer getChallengePlayer(UUID uuid) {
		for (ChallengePlayer chalPl : challengePlayers) {
			if (chalPl.getUniqueId().equals(uuid)) return chalPl;
		}
		return null;
	}
	
	private void loadRewards() {
		new Reward("%amount%$", Utils.inBetween(9, 15), new RewardExecutor() {
			public void onReward(ChallengePlayer player, int amount) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give " + player.getPlayer().getName() + " " + amount);
			}
		}).register("MONEY");
		new Reward("%amount% EXP Levels", Utils.inBetween(4, 8), new RewardExecutor() {
			public void onReward(ChallengePlayer player, int amount) {
				player.getPlayer().setLevel(player.getPlayer().getLevel() + amount);
			}
		}).register("XP");
	}
	
	@SuppressWarnings("deprecation")
	private void loadChallenges() {
		new Challenge("Harvest %amount% Crops", "Harvest fully grown/ripe crops. Does not include Pumpkins, Melons, Chorus Fruit, etc.", new int[]{15,20,25}, new ChallengeHandler() {
			private final Material[] crops = new Material[]{Material.LEGACY_CROPS, Material.LEGACY_BEETROOT_BLOCK, Material.POTATO, Material.CARROT, 
					Material.LEGACY_NETHER_WARTS, Material.COCOA};
			
			@SuppressWarnings("deprecation")
			public int onBlockBreak(BlockBreakEvent e) {
				if (!Arrays.asList(crops).contains(e.getBlock().getType())) return 0;
				Material mat = e.getBlock().getType();
				byte data = e.getBlock().getData();
				
				switch (mat) {
				case LEGACY_BEETROOT_BLOCK: if (data == 3) return 1;
				case LEGACY_NETHER_WARTS: if (data == 3) return 1;
				case COCOA: if (data > 7) return 1;
				
				default: if (data == 7) return 1;
				}
				return 0;
			}
		}).register("HARVEST_CROPS");
		
		new Challenge("Fish %amount% Fish", "Use a fishing rod and start fishing for fish.", new int[]{3,5,7,10}, new ChallengeHandler() {
			@SuppressWarnings("deprecation")
			public int onFishing(PlayerFishEvent e) {
				if (e.getState() != State.CAUGHT_FISH) return 0;
				ItemStack item = ((Item) e.getCaught()).getItemStack();
				if (item.getType() == Material.LEGACY_RAW_FISH) {
					return 1;
				}
				return 0;
			}
		}).register("FISHING");
		
		new Challenge("Kill %amount% Hostiles", "Cause a hostile monster's death, either directly or indirectly.", new int[]{20,25,35}, new ChallengeHandler() {
			private final EntityType[] hostiles = new EntityType[]{EntityType.ZOMBIE, EntityType.SKELETON, EntityType.WITHER_SKELETON, EntityType.BLAZE,
					EntityType.CAVE_SPIDER, EntityType.CREEPER, EntityType.ENDERMAN, EntityType.ENDER_DRAGON, EntityType.ENDERMITE, EntityType.SILVERFISH,
					EntityType.EVOKER, EntityType.GHAST, EntityType.HUSK, EntityType.MAGMA_CUBE, EntityType.WITCH, EntityType.SKELETON_HORSE,
					EntityType.SLIME, EntityType.STRAY, EntityType.SPIDER, EntityType.VEX, EntityType.ZOMBIE_VILLAGER, EntityType.ZOMBIE_HORSE, EntityType.WITHER,
					EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN, EntityType.SHULKER, EntityType.PIG_ZOMBIE};
			
			public int onMobKill(EntityDeathEvent e) {
				if (Arrays.asList(hostiles).contains(e.getEntityType())) {
					return 1;
				}
				return 0;
			}
		}).register("KILL_HOSTILES");
		
		new Challenge("Mine %amount% Stone", "Whilst mining, mine stone blocks. An explosive pickaxe makes light work of this challenge.", new int[]{150,250,400}, new ChallengeHandler() {
			@Override
			public int onBlockBreak(BlockBreakEvent e) {
				if (e.getBlock().getType() == Material.STONE) {
					return 1;
				} 
				return 0;
			}
		}).register("MINING_STONE");
		
		new Challenge("Earn %amount% Emeralds", "Trade with Villagers to obtain emeralds!", new int[]{4,6,8,10,12,14}, new ChallengeHandler() {

			public int onEntityClick(PlayerInteractEntityEvent e) {
				if (e.getRightClicked().getType() == EntityType.VILLAGER)
					e.getPlayer().sendMessage(DailyChallengesCMD.prefix + "WARNING! You have an active Villager Challenge! Shift-Clicking Emeralds from Villagers will not properly progress your Challenge!");
				return 0;
			}
			
			public int onInventoryClick(InventoryClickEvent e) {
				if (e.getClickedInventory().getType() != InventoryType.MERCHANT) return 0;
				if (e.getCurrentItem().getType() != Material.EMERALD) return 0;
				if (e.getRawSlot() != 2) return 0;
				if (e.getCursor() != null) {
					if (e.getCursor().getType() == Material.AIR || e.getCursor().getType() == Material.EMERALD) return e.getCurrentItem().getAmount();
				}
				
				return e.getCurrentItem().getAmount();
			}
		}).register("TRADE_VILLAGERS");
		
		new Challenge("Breed two Animals", "Breed two animals to create a baby animal!", new int[]{1}, new ChallengeHandler() {
			public int onEntityBreed(EntityBreedEvent e) {
				return 1;
			}
		}).register("BREED_ANIMALS");
	}
}
