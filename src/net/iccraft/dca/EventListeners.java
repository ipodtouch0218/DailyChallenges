package net.iccraft.dca;

import java.io.File;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

import net.iccraft.dca.challenges.ChallengePlayer;
import net.iccraft.dca.objects.ChallengeData;

public class EventListeners implements Listener {

	private DCAMain core;
	public EventListeners(DCAMain core) {
		this.core = core;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (core.getChallengePlayer(e.getPlayer().getUniqueId()) != null) return;
		File file = new File(core.getDataFolder() + "/players/" + e.getPlayer().getUniqueId().toString() + ".yml");
		
		core.getAllChallengePlayers().add(ChallengePlayer.loadFromFile(e.getPlayer().getUniqueId(), file));
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		if (core.getChallengePlayer(e.getPlayer().getUniqueId()) == null) return;
		File file = new File(core.getDataFolder() + "/players/" + e.getPlayer().getUniqueId().toString() + ".yml");
		ChallengePlayer chalPl = core.getChallengePlayer(e.getPlayer().getUniqueId());
		
		chalPl.saveToFile(file);
		core.getAllChallengePlayers().remove(chalPl);
	}
	
	//---Challenge Listeners---//
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if (e.isCancelled()) return;
		ChallengePlayer player = core.getChallengePlayer(e.getPlayer().getUniqueId());
		
		for (ChallengeData data : player.getChallengeData()) {
			int score = data.getChallenge().getHandler().onBlockBreak(e);
			if (!data.isCompleted()) data.addScore(score, player);
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (e.isCancelled()) return;
		if (e.getHand() != EquipmentSlot.HAND) return; //Restrict to one hand only.
		ChallengePlayer player = core.getChallengePlayer(e.getPlayer().getUniqueId());
		
		for (ChallengeData data : player.getChallengeData()) {
			int score = data.getChallenge().getHandler().onInteract(e);
			if (!data.isCompleted()) data.addScore(score, player);
		}
	}
	
	@EventHandler
	public void onFishing(PlayerFishEvent e) {
		ChallengePlayer player = core.getChallengePlayer(e.getPlayer().getUniqueId());
		
		for (ChallengeData data : player.getChallengeData()) {
			int score = data.getChallenge().getHandler().onFishing(e);
			if (!data.isCompleted()) data.addScore(score, player);
		}
	}
	
	@EventHandler
	public void onMobKill(EntityDeathEvent e) {
		if (e.getEntity().getKiller() == null) return;
		ChallengePlayer player = core.getChallengePlayer(e.getEntity().getKiller().getUniqueId());
		
		for (ChallengeData data : player.getChallengeData()) {
			int score = data.getChallenge().getHandler().onMobKill(e);
			if (!data.isCompleted()) data.addScore(score, player);
		}
	}
	
	@EventHandler
	public void onEntityTame(EntityTameEvent e) {
		if (!(e.getOwner() instanceof Player)) return;
		ChallengePlayer player = core.getChallengePlayer(e.getOwner().getUniqueId());
		
		for (ChallengeData data : player.getChallengeData()) {
			int score = data.getChallenge().getHandler().onEntityTame(e);
			if (!data.isCompleted()) data.addScore(score, player);
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.isCancelled()) return;
		if (e.getClickedInventory() == null) return;
		ChallengePlayer player = core.getChallengePlayer(e.getWhoClicked().getUniqueId());
		if (e.getClickedInventory().getName().equals("§6§lDaily Challenges")) { 
			e.setCancelled(true);
			int newValue = -1;
			
			switch(e.getRawSlot()) {
			case 19: {newValue=0; break;}
			case 21: {newValue=1; break;}
			case 23: {newValue=2; break;}
			case 25: {newValue=3; break;}
			}
			
			player.setFocusedChallenge(newValue);
			return; 
		}
		
		for (ChallengeData data : player.getChallengeData()) {
			int score = data.getChallenge().getHandler().onInventoryClick(e);
			if (data.getScore() < data.getRequired()) data.addScore(score, player);
		}
	}
	
	@EventHandler
	public void onEntityClick(PlayerInteractEntityEvent e) {
		if (e.isCancelled()) return;
		if (e.getHand() != EquipmentSlot.HAND) return; //Restrict to one hand only.
		ChallengePlayer player = core.getChallengePlayer(e.getPlayer().getUniqueId());
		
		for (ChallengeData data : player.getChallengeData()) {
			int score = data.getChallenge().getHandler().onEntityClick(e);
			if (!data.isCompleted()) data.addScore(score, player);
		}
	}
	
	@EventHandler
	public void onEntityBreed(EntityBreedEvent e) {
		if (e.isCancelled()) return;
		if (!(e.getBreeder() instanceof Player)) return;
		ChallengePlayer player = core.getChallengePlayer(((Player) e.getBreeder()).getUniqueId());
		
		for (ChallengeData data : player.getChallengeData()) {
			int score = data.getChallenge().getHandler().onEntityBreed(e);
			if (!data.isCompleted()) data.addScore(score, player);
		}
	}
}
