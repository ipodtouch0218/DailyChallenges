package net.iccraft.dca;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Utils {

	private final static Random random = new Random();
	
	public static int chooseNumber(int[] values) { return values[random.nextInt(values.length)]; }
	public static int chooseNumber(int lower, int upper) { return chooseNumber(inBetween(lower,upper)); }

	public static double chooseNumber(double[] values) { return values[random.nextInt(values.length)]; }
	
	public static int[] inBetween(int lower, int upper) {
		if (upper<lower) throw new IllegalArgumentException("Lower bound cannot be lower than upper bound!");
		
		int[] numbers = new int[upper-lower];
		int iteration = 0;
		for (int i = lower; i < upper; i++)  {
			numbers[iteration++] = i;
		}
		return numbers;
	}
			
	public static ItemStack buildItem(ItemStack stack, String name, String... lore) {
		ItemMeta meta = stack.getItemMeta();
		
		if (name != null) {
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		}
		if (lore.length > 0) {
			ArrayList<String> newLore = new ArrayList<String>();
			Arrays.asList(lore).forEach(line -> newLore.add(ChatColor.translateAlternateColorCodes('&', line)));
			meta.setLore(newLore);
		}
		
		stack.setItemMeta(meta);
		return stack;
	}
	
	public static ItemStack buildItem(ItemStack stack, String name, List<String> lore) {
		ItemMeta meta = stack.getItemMeta();
		
		if (name != null) {
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		}
		if (lore.size() > 0) {
			ArrayList<String> newLore = new ArrayList<String>();
			lore.forEach(line -> newLore.add(ChatColor.translateAlternateColorCodes('&', line)));
			meta.setLore(newLore);
		}
		
		stack.setItemMeta(meta);
		return stack;
	}
	
	public static String createProgressBar(double progress) {
		String progressBar = "";
		double loop = progress;
		for(int i = 0; i < 20; i++) {
			if (loop>=5) {
				loop-=5;
				progressBar+="&a|";
			} else if (loop<5 && loop > 2.5) {
				loop=0;
				progressBar+="&6|";
			} else {
				progressBar+="&c|";
			}
		}
		return ChatColor.translateAlternateColorCodes('&', progressBar);
	}
	
	public static void shortenStringIntoList(String longString, int charAmount, List<String> list) {
		String newLine = ChatColor.RESET + "";
		for (String word : longString.split(" ")) {
			if (newLine.length() >= charAmount) {
				list.add(newLine.trim());
				newLine = ChatColor.RESET + "";
			}
			newLine = newLine + " " + word;
		}
		list.add(newLine.trim());
	}
}
