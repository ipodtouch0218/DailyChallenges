package net.iccraft.dca.challenges;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

public interface ChallengeHandler {
	
	public default int onBlockBreak(BlockBreakEvent e) { return 0; }
	public default int onInteract(PlayerInteractEvent e) { return 0; }
	public default int onFishing(PlayerFishEvent e) { return 0; }
	public default int onMobKill(EntityDeathEvent e) { return 0; }
	public default int onEntityTame(EntityTameEvent e) { return 0; }
	public default int onInventoryClick(InventoryClickEvent e) { return 0; }
	public default int onEntityClick(PlayerInteractEntityEvent e) { return 0; }
	public default int onEntityBreed(EntityBreedEvent e) { return 0; }
	
	
}
