package net.iccraft.dca.commands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.iccraft.dca.DCAMain;
import net.iccraft.dca.Utils;
import net.iccraft.dca.challenges.ChallengePlayer;
import net.iccraft.dca.objects.ChallengeData;

@SuppressWarnings("deprecation")
public class DailyChallengesCMD implements CommandExecutor {

	public static final String prefix = "§6[§eChallenge§6] §7";
	private DCAMain core;
	public DailyChallengesCMD(DCAMain core) {
		this.core = core;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(prefix + "Only players can use this command, as it opens an inventory!");
			return true;
		}
		Player plPl = (Player) sender;
		if (DCAMain.isWorldBlacklisted(plPl.getWorld())) {
			plPl.sendMessage(prefix + "This world is blacklisted from completing challenges!");
			return true;
		}
		ChallengePlayer chalPl = core.getChallengePlayer(plPl.getUniqueId());
		
		plPl.openInventory(openInv(chalPl));
		return true;
	}
	
	private int[] borderSlots = new int[]{0,1,2,3,5,6,7,8,36,37,38,39,40,41,42,43,44};
	private ItemStack borderItem = Utils.buildItem(new ItemStack(Material.LEGACY_STAINED_GLASS_PANE, 1, (short) 15), " ");
	
	private int[] innerSlots = new int[]{9,10,11,12,13,14,15,16,17,18,20,22,24,26,27,28,29,30,31,32,33,34,35};
	private ItemStack innerItem = Utils.buildItem(new ItemStack(Material.LEGACY_STAINED_GLASS_PANE, 1, (short) 5), " ");
	
	private String[] tips = new String[]{"Click on a Challenge's book to get Real-Time updates on that Challenge!",
										 "Hover over a Challenge to check its Reward before completing it!",
										 "If you have two of the same Challenge, actions count towards both at the same time!",
										 "Challenges refresh daily! Make sure to complete all Challenges for a given day to maxamize rewards!",
										 "Voting for the server daily is another way to earn money! Use /vote for links!",
										 "The same Challenge can have different completion rewards!"};
	
	private Inventory openInv(ChallengePlayer pl) {
		Inventory inv = Bukkit.createInventory(null, 45, "§6§lDaily Challenges");
		for (int slot : borderSlots) inv.setItem(slot, borderItem);
		for (int slot : innerSlots) inv.setItem(slot, innerItem);
		
		ArrayList<String> tipLore = new ArrayList<String>();
		Utils.shortenStringIntoList(tips[Utils.chooseNumber(0, tips.length)], 30, tipLore);
		inv.addItem(Utils.buildItem(new ItemStack(Material.PAPER), "&b&lTIP:", tipLore));
		
		for (ChallengeData data : pl.getChallengeData()) {
			String name = data.getChallenge().getDisplayName().replaceAll("%amount%", "" + data.getRequired());
			ArrayList<String> lore = new ArrayList<String>();
			String progressBar = "&9&lProgress: ";
			
			progressBar+=Utils.createProgressBar(((double) data.getScore()/(double) data.getRequired())*100);
			
			Utils.shortenStringIntoList(data.getChallenge().getDescription(), 30, lore);
			
			lore.add("");
			lore.add("&9&lReward: &f" + data.getReward().getDisplayName().replace("%amount%", "" + data.getRewardAmount()));
			lore.add(progressBar + " &6(" + data.getScore() + "/" + data.getRequired() + ")");
			
			ItemStack stack = null;
			if (data.getScore()>=data.getRequired()) {
				stack = Utils.buildItem(new ItemStack(Material.ENCHANTED_BOOK, 1), "&a" + name, lore);
			} else {
				stack = Utils.buildItem(new ItemStack(Material.LEGACY_BOOK_AND_QUILL, 1), "&c" + name, lore);
			}
			
			inv.addItem(stack);
		}
		
		return inv;
	}

}
