package me.greenem.AdvansedInvSave;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;


//import com.google.common.collect.Multimap;

//import net.minecraft.server.v1_16_R3.GameRules.GameRuleCategory;
//import net.minecraft.server.v1_16_R3.NBTTagCompound;

import org.bukkit.inventory.meta.ItemMeta;

//import net.minecraft.server.v1_8_R3.PlayerSelector;

/**
 * Copyright (c) Greenem
 * Please contact me if you need to use big parts of this projects for yourself
 ***/

public class MainFileInv extends JavaPlugin implements Listener {
	
	/**
	 * INFORMATION
	 * I was working on this plugin for a long time (and started a long time ago too), the code is not organised at all, maybe I'll change that later if I want.  
	 * Bacically, this is what any of my plugins' code looks before the release XD
	 * ***/

	
	private Logger log = getLogger();
	public Properties props = new Properties();
	
	public boolean debug = false;
//	public File authDirectory;
//	public File inventoriesDirectory;
	
	public String sharedPathString = null;
	
	public File dataDirectory;
	public File authDataDirectory;
	public File localMainInvDirectory;
	public File sharedMainInvDirectory;
	public File localQuickInvSaveDirectory;
	public File sharedQuickInvSaveDirectory;
	public File localExtendedInvSaveDirectory;
	public File localExtendedFullInvsDirectory;
	public File localExtendedWardrobeDirectory;
	public File localExtendedItemsDirectory;
	
	public File sharedExtendedInvSaveDirectory;
	public File sharedExtendedFullInvsDirectory;
	public File sharedExtendedWardrobeDirectory;
	public File sharedExtendedItemsDirectory;
	
	public File configsDirectory;
	public File mainConfigDirectory;
	
	public boolean logCatchedErrors = false;
	public boolean enableInvMenu = true;
	public boolean enableLocalStorage = false;
	public boolean enableSharedStorage = false;
	public boolean enableWholeInventories = false;
	public boolean enableWardrobe = false;
	public boolean enableSingleItems = false;
	
	public boolean customJoinMessageEnabled = true;
	public boolean customLeaveMessageEnabled = true;
	public String customJoinMessageText = "§f§lthe-player-name §7joined the game";
	public String customLeaveMessageText = "§f§lthe-player-name §7left the game";
	public File sharedStoragePath = null;
	
	public Map <Player, Inventory> MainInvs = new HashMap<>();
	public Map <Player, Inventory> SelectModeInvs = new HashMap<>();
	public Map <Player, Inventory> SavedFullInvs = new HashMap<>();
	public Map <Player, Inventory> WardrobeInvs = new HashMap<>();
	public Map <Player, Inventory> SingleItemsInvs = new HashMap<>();
	public Map <Player, Inventory> AddItemInvs = new HashMap<>();
	public Map <Player, Inventory> EditItemInvs = new HashMap<>();
	public Map <Player, Inventory> AddGearInvs = new HashMap<>();
	public Map <Player, Inventory> EditGearInvs = new HashMap<>();
	public Map <Player, Inventory> EditFullInvInvs = new HashMap<>();
	
	public Map <Player, ItemStack> CurrentSingleItem = new HashMap<>();
	public Map <Player, String> ActionOnCurrentSingleItem = new HashMap<>();
	
	public Map <Player, List<ItemStack>> CurrentEditGear = new HashMap<>();
	public Map <Player, List<ItemStack>> TempEditGear = new HashMap<>();
	public Map <Player, Integer> CurrentEditGearElt = new HashMap<>();
	
	public Map <Player, Integer> SingleItemsPage = new HashMap<>();
	public Map <Player, Integer> WardrobePage = new HashMap<>();
	public Map <Player, Integer> FullInventoriesPage = new HashMap<>();
	public Map <Player, String> LosPage = new HashMap<>();
	
	public Map <Player, String> LocalOrShared = new HashMap<>();
	
	public Map <Player, Map <Player, Inventory>> WhereIsPlayer = new HashMap<>();
	
	public boolean Restarting = false;
	
	public ItemStack warningBell = createGuiItem(Material.BELL, 1, ChatColor.YELLOW + "WARNING!", ChatColor.GOLD + "You don't have the permission to load anything now");
	public ItemStack goBackItem = createGuiItem(Material.RED_STAINED_GLASS, 1, ChatColor.RED + "Go back");
	public ItemStack revertItem = createGuiItem(Material.RED_CONCRETE, 1, ChatColor.RED + "Revert changes");
	public ItemStack confirmItem = createGuiItem(Material.LIME_STAINED_GLASS, 1, ChatColor.GREEN + "Confirm");
	public ItemStack takeSmthItem = createGuiItem(Material.HOPPER, 1, ChatColor.AQUA + "Take");
	public ItemStack deleteItem = createGuiItem(Material.TNT, 1, ChatColor.AQUA + "Delete");
	public ItemStack createNewItem = createGuiItem(Material.BEACON, 1, ChatColor.RED + "Create new");
	public ItemStack wardrobeEditItem = createGuiItem(Material.FIREWORK_STAR, 1, ChatColor.WHITE + "Edit armor set");
	public ItemStack generalBg = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, 1, " ");
	public ItemStack whiteBg = createGuiItem(Material.WHITE_STAINED_GLASS_PANE, 1, " ");
	
	public ItemStack addGearNoItem = createGuiItem(Material.WHITE_STAINED_GLASS_PANE, 1, "(No item)");
	public ItemStack addGearEditingPane = createGuiItem(Material.PURPLE_STAINED_GLASS_PANE, 1, "");
	public ItemStack addGearUnready = createGuiItem(Material.GRAY_DYE, 1, ChatColor.GRAY + "Not added");
	public ItemStack addGearAdded = createGuiItem(Material.LIME_DYE, 1, ChatColor.GREEN + "Added");
	public ItemStack addGearEditingDye = createGuiItem(Material.PURPLE_DYE, 1, ChatColor.DARK_PURPLE + "Editing");
	public ItemStack addGearNoConfirm = createGuiItem(Material.PURPLE_STAINED_GLASS, 1, ChatColor.RED + "Select an armor slot first!");
	public ItemStack addGearConfirm = createGuiItem(Material.PURPLE_CONCRETE, 1, ChatColor.DARK_PURPLE + "Save", ChatColor.GRAY + "Set at the armor slot");
	public ItemStack addGearFromWearingReady = createGuiItem(Material.HOPPER_MINECART, 1, ChatColor.WHITE + "Copy from your current armor");
	public ItemStack addGearFromWearingDone = createGuiItem(Material.PURPLE_CONCRETE, 1, ChatColor.DARK_GREEN + "Copied.");
	
	public Material tipMaterial = Material.BIRCH_SIGN;
	
	public String messagePrefix = ChatColor.GOLD + "[AdvansedInvSave] ";
	
	public String needToAuth = messagePrefix + ChatColor.RED + "You cannot use this command before logging in successfully!";
	public String messageAddedPerms = messagePrefix + ChatColor.YELLOW + "Now you " + ChatColor.GREEN + "have" + ChatColor.YELLOW + " the permission to load inventories";
	public String messageRemovedPerms = messagePrefix + ChatColor.YELLOW + "Now you " + ChatColor.RED + "don't have" + ChatColor.YELLOW + " the permission to load inventories";
	public String messageDontHavePermsQuick = messagePrefix + ChatColor.RED + "At the moment you can only save your inventory. If you have lost your items due to some kind of autorization plugin bug, don't overwrite your invenroty save and contact the admins.";
	public String messageQuickSavedInv = messagePrefix + ChatColor.YELLOW + "" + ChatColor.BOLD + "Saved" + ChatColor.RESET + "" + ChatColor.GREEN + " inventory successfuly";
	public String messageQuickLoadedInv = messagePrefix + ChatColor.YELLOW + "" + ChatColor.BOLD + "Loaded" + ChatColor.RESET + "" + ChatColor.GREEN + " inventory successfuly";
	public String messageLoadedSingleItem = messagePrefix + ChatColor.GREEN + "Loaded the item successfully";
	public String messageFullInventory = messagePrefix + ChatColor.RED + "You don't have enough free inventory space!";
	public String messageDontHavePermsUniversal = messagePrefix + ChatColor.RED + "You don't have permission to do this!";
	public String messageFeatureDisabled = messagePrefix + ChatColor.RED + "This feature was disabled by admins.";
	public String messageAddedOneLoadPerms = messagePrefix + ChatColor.YELLOW + "you have been " + ChatColor.GREEN + "given" + ChatColor.YELLOW + " permission to load your inventory once";
	
	public Sound failure = Sound.ITEM_CHORUS_FRUIT_TELEPORT;
	
	public ArrayList<ItemStack> emptyItemStacksArrayList = new ArrayList<>();
	
	@Override
	public void onDisable() {
		Restarting = true;
		for (Player p : WhereIsPlayer.keySet()) {
			p.closeInventory();
		}
		log(ChatColor.RED + "AdvansedInvSave plugin has been disabled.");
	}

	@Override
	public void onEnable() {
		Restarting = false;
		log(ChatColor.GREEN + "AdvansedInvSave plugin has been enabled!");
		getServer().getPluginManager().registerEvents(this, this);
		initializeFiles();
		emptyItemStacksArrayList.add(new ItemStack(Material.AIR));
		emptyItemStacksArrayList.add(new ItemStack(Material.AIR));
		emptyItemStacksArrayList.add(new ItemStack(Material.AIR));
		emptyItemStacksArrayList.add(new ItemStack(Material.AIR));
		
		loadConfig();
		copyHelpFile();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		Player p = getServer().getPlayerExact(sender.getName());
		//Plugin plugin = getServer().getPluginManager().getPlugin("");
		if(cmd.getName().equalsIgnoreCase("inv")){
			invCommand(p);
//			pl.class.			
//			else p.sendMessage(ChatColor.RED + "You have already signed in");
		}
		
		if(cmd.getName().equalsIgnoreCase("test1")){
			test1Command(p);
		}
		
		if(cmd.getName().equalsIgnoreCase("invsave")){
			if (!(sender instanceof Player)) {
				if (sender instanceof ConsoleCommandSender) {
					log(ChatColor.RED + "This comand is only for players.");
				}
				return false;
			}
			if(!ifPlayerIsAddedinAuthFile(p)) {
				saveInventory(new File(getLosPath(p, "quick") + "/saved"), p);
			}
			else {
				p.sendMessage(needToAuth);
				return false;
			}
			p.sendMessage(messageQuickSavedInv);
		}
		if(cmd.getName().equalsIgnoreCase("invload")){
			if (!(sender instanceof Player)) {
				if (sender instanceof ConsoleCommandSender) {
					log(ChatColor.RED + "This comand is only for players.");
				}
				return false;
			}
			if(!ifPlayerIsAddedinAuthFile(p)) {
				quickLoadInventory(p);
			}
			else {
				p.sendMessage(needToAuth);
				return false;
			}
		}
		if(cmd.getName().equalsIgnoreCase("permsinvadd")){
			try {
				addPerms(sender, args);
			} catch (IOException e) {
				if(logCatchedErrors) e.printStackTrace();
			}
		}
		if(cmd.getName().equalsIgnoreCase("permsinvremove")){
			try {
				removePerms(sender, args);
			} catch (IOException e) {
				if(logCatchedErrors) e.printStackTrace();
			}
		}
		if(cmd.getName().equalsIgnoreCase("reloadadvinvsaveconfig")){
			loadConfig();
			sender.sendMessage(ChatColor.GREEN + "Successfully reloaded the AdvansedInvSave config");
		}
		if(cmd.getName().equalsIgnoreCase("permsinvoneload")){
			addPermsOneLoad(sender, args);
		}
		
		return false;
	}
	
	public void copyHelpFile() {
		File f = new File(mainConfigDirectory, "confighelp.txt");
		f.getParentFile().mkdirs();
		if (!f.exists()) {
			InputStream is = getClass().getResourceAsStream("/confighelp.txt");
			OutputStream os = null;
			try {
				os = new FileOutputStream(f);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			byte[] buffer = new byte[4096];
			int length;
			try {
				while ((length = is.read(buffer)) > 0) {
					os.write(buffer, 0, length);
				}
				os.close();
				is.close();
			} catch (IOException e) {
				if(logCatchedErrors) e.printStackTrace();
			}
		}
	}
	
	public void loadConfig() {
		File f = new File(mainConfigDirectory, "mainconfig.yml");
		f.getParentFile().mkdirs();

		YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
		YamlConfiguration cOld = YamlConfiguration.loadConfiguration(f);
		// YamlConfiguration toDelete = YamlConfiguration.loadConfiguration(f);
		String currentKey = null;
		// String currentString;
//		boolean currentBoolean;
//		int currentInt;
//		int currentLong;

		try {
			currentKey = "log-catched-errors";
			// toDelete.set(currentKey, null);
			if (c.get(currentKey) == null) {
				c.set(currentKey, true);
			} else {
				// currentBoolean = (boolean) c.get(currentKey);
				// debug = currentBoolean;
				try {
					logCatchedErrors = (boolean) c.get(currentKey);
				} catch (ClassCastException e) {

				}
			}

			currentKey = "enable-inv-menu";
			if (c.get(currentKey) == null) {
				c.set(currentKey, true);
			} else {
				enableInvMenu = (boolean) c.get(currentKey);
			}

			currentKey = "enable-local-storage";
			if (c.get(currentKey) == null) {
				c.set(currentKey, false);
			} else {
				enableLocalStorage = (boolean) c.get(currentKey);
			}

			currentKey = "enable-shared-storage";
			if (c.get(currentKey) == null) {
				c.set(currentKey, false);
			} else {
				enableSharedStorage = (boolean) c.get(currentKey);
			}

			currentKey = "enable-whole-inventories";
			if (c.get(currentKey) == null) {
				c.set(currentKey, false);
			} else {
				enableWholeInventories = (boolean) c.get(currentKey);
			}

			currentKey = "enable-wardrobe";
			if (c.get(currentKey) == null) {
				c.set(currentKey, false);
			} else {
				enableWardrobe = (boolean) c.get(currentKey);
			}

			currentKey = "enable-single-items";
			if (c.get(currentKey) == null) {
				c.set(currentKey, false);
			} else {
				enableSingleItems = (boolean) c.get(currentKey);
			}

			currentKey = "shared-storage-path";
			if (c.get(currentKey) == null) {
				c.set(currentKey, "");
			} else {
				sharedPathString = (String) c.get(currentKey);
				if (!sharedPathString.equals("")) {
					// log("Shared path: " + sharedPath);
					File theFile = new File(sharedPathString);
					if (theFile.exists() && theFile.isDirectory()) {
						sharedStoragePath = theFile;
					}
				}
			}
		} catch (Throwable t) {
			log("Error while reading \"" + currentKey + "\" from the config");
		}

		// DONE

		boolean same = true;

		for (String key : c.getKeys(false)) {
			if (!cOld.contains(key)) {
				same = false;
			}
		}

		if (!same) {
			try {
				c.save(f);
			} catch (IOException e) {
				if(logCatchedErrors) e.printStackTrace();
			}
		}
	}

	//--------------------------------------------------------------------COMMANDS--------------------------------------------------------------------------------------------
	
	public void invCommand(Player p) {
		if(!ifPlayerIsAddedinAuthFile(p)) {
			if(enableInvMenu) {
				openUniversalView(p, "Main", MainInvs, "Advanced Inventories Menu", 45, Material.GRAY_STAINED_GLASS_PANE);
			}
			else {
				p.sendMessage(messageFeatureDisabled);
			}
		}
		else {
			p.sendMessage(needToAuth);
			return;
		}
	}
	
	public void test1Command(Player p) {
		//addSingleItem(p, p.getItemInHand());
//		p.playSound(p.getLocation(), Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1, 1);
//		if(true) {
//			return;
//		}
		
		YamlConfiguration c = null;
		List<List<ItemStack>> all = null;
		List<ItemStack> armorset = new ArrayList<>();
		
		c = YamlConfiguration.loadConfiguration(new File(getLosPath(p, "wardrobe"), p.getName() + ".yml"));
		all = (List<List<ItemStack>>) c.get("armor sets");
		if(all==null) {
			all = new ArrayList<>();
		}
		
		armorset.add(p.getInventory().getBoots());
		armorset.add(p.getInventory().getLeggings());
		armorset.add(p.getInventory().getChestplate());
		armorset.add(p.getInventory().getHelmet());
		
		all.add(armorset);
		// p.getInventory().addItem(item);
		
		c.set("armor sets", all);
		//log(c.getValues(true).toString());
		try {
			c.save(new File(getLosPath(p, "wardrobe"), p.getName() + ".yml"));
		} catch (IOException e) {
			if(logCatchedErrors) e.printStackTrace();
		}
		
		// log(c.getValues(false) + "");
		// log(c.getValues(true) + "");
		// item = ItemStack.deserialize(c.getValues(false));
		// log(item + "");
		
		//ItemStack item = ItemStack.deserialize
//		ItemStack item = ItemStack.deserialize(s);
	}
	
	public void test2() {
		getServer().dispatchCommand(getServer().getConsoleSender(), "");
	}
	
	//--------------------------------------------------------------------OPEN-INV--------------------------------------------------------------------------------------------
	
	public void SelectModeOpen(Player p) {
		openUniversalView(p, "SelectMode", SelectModeInvs, "Choose Save Mode", 45, Material.GRAY_STAINED_GLASS_PANE);
	}
	
	public void SingleItemsOpen(Player p) {
		openUniversalView(p, "Items", SingleItemsInvs, "Saved Items", 54, Material.GRAY_STAINED_GLASS_PANE);
	}
	
	public void AddItemOpen(Player p) {
		openUniversalView(p, "AddItem", AddItemInvs, "Add Item", 27, Material.GRAY_STAINED_GLASS_PANE);
	}
	
	public void EditItemOpen(Player p) {
		openUniversalView(p, "EditItem", EditItemInvs, "Edit Item", 27, Material.GRAY_STAINED_GLASS_PANE);
	}
	
	public void WardrobeOpen(Player p) {
		openUniversalView(p, "Wardrobe", WardrobeInvs, "Wardrobe", 54, Material.GRAY_STAINED_GLASS_PANE);
	}
	
	public void AddGearOpen(Player p) {
		openUniversalView(p, "AddGear", AddGearInvs, "Edit Armor Set", 54, Material.GRAY_STAINED_GLASS_PANE);
	}
	
	//--------------------------------------------------------------------INVS--------------------------------------------------------------------------------------------
	
	
	//--------------------------------------------------------------------INVS--------------------------------------------------------------------------------------------
	
	public void fillInvWithItemStack(Inventory inv, ItemStack is) {
		for (int i = 0; i < inv.getSize(); i++) {
			inv.setItem(i, is);
		}
	}
	
	
	public void makeInvMain(Player p, Inventory inv) {
		inv.setItem(12, createGuiItem(Material.CHEST, 1, ChatColor.GREEN + "Local storage"));
		inv.setItem(14, createGuiItem(Material.ENDER_CHEST, 1, ChatColor.LIGHT_PURPLE + "Shared storage"));
		inv.setItem(29, createGuiItem(Material.BARREL, 1, ChatColor.RED + "Quick save"));
		inv.setItem(33, createGuiItem(Material.DROPPER, 1, ChatColor.RED + "Quick load"));
		inv.setItem(44, createGuiItem(Material.LODESTONE, 1, ChatColor.GRAY + "Other"));
//		updateWarning(p, ifPlayerHasAccess(p));
		if(!ifPlayerHasAccess(p)) {
			inv.setItem(36, warningBell);
		}
		else {
			inv.setItem(36, generalBg);
		}
	}
	
	
	public void makeInvSelectMode(Player p, Inventory inv) {
		ItemStack item = null;
		ItemMeta meta = null;
		
		inv.setItem(20, createGuiItem(Material.CHEST_MINECART, 1, ChatColor.GOLD +  "Whole inventories"));
		inv.setItem(22, createGuiItem(Material.ARMOR_STAND, 1, ChatColor.DARK_GREEN + "Wardrobe"));
		
		item = createGuiItem(Material.DIAMOND_SWORD, 1, ChatColor.AQUA + "Items");
		meta = item.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(meta);
		inv.setItem(24, item);
		
		//log(createGuiItem(Material.DIAMOND_SWORD, 1, ChatColor.AQUA + "Items").getItemMeta().getItemFlags().toString());
		//log(createGuiItem(Material.DIAMOND_SWORD, 1, ChatColor.AQUA + "Items").getItemMeta().getAttributeModifiers() + "");
		//log(createGuiItem(Material.DIAMOND_SWORD, 1, ChatColor.AQUA + "Items").getItemMeta() + "");
		//net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(createGuiItem(Material.DIAMOND_SWORD, 1, ChatColor.AQUA + "Items"));
		//if (nmsItem.hasTag()) {
			//NBTTagCompound tag = nmsItem.getTag();
			//log(tag.getString("Damage"));
		//}
//		updateWarning(p, ifPlayerHasAccess(p));
		inv.setItem(40, goBackItem);
		if(!ifPlayerHasAccess(p)) {
			inv.setItem(36, warningBell);
		}
		else {
			inv.setItem(36, generalBg);
		}
	}
	
	
	public void makeInvSingleItems(Player p, Inventory inv) {
//		ItemStack item = null;
//		ItemMeta meta = null;
		
		YamlConfiguration c = null;
		List<ItemStack> items = null;
		
		if(!SingleItemsPage.containsKey(p)) {
			SingleItemsPage.put(p, 1);
		}
		
		//log(LocalOrShared.get(p));
		
		updateSingleItemsPage(p);
		
		if(true) {
			return;
		}
		
		c = YamlConfiguration.loadConfiguration(new File(getLosPath(p, "items"), p.getName() + ".yml"));
		items = (ArrayList<ItemStack>) c.get("items");
		if(items==null) {
			items = new ArrayList<>();
		}
		
		if(inv!=null) {
			int page = -1;
			if(SingleItemsPage.containsKey(p)) {
				page = SingleItemsPage.get(p).intValue();
			}
			
			int itemsOnThisPage = items.size();
			if(itemsOnThisPage>45) {
				itemsOnThisPage = 45;
			}
			for (int i = 0; i < itemsOnThisPage; i++) {
//				log(i + "");
				inv.setItem(i, items.get(i));
			}
			for (int i = items.size(); i < inv.getSize()-9; i++) {
				inv.setItem(i, new ItemStack(Material.AIR));
			}
			
			if(items.size() > 45) {
				inv.setItem(53, createGuiItem(Material.ARROW, 1, ChatColor.WHITE + "Next Page", ChatColor.GRAY + "To page " + (page+1)));
			}
			else {
				inv.setItem(53, generalBg);
			}
			
			if(page>1) {
				inv.setItem(45, createGuiItem(Material.ARROW, 1, ChatColor.WHITE + "Previous Page", ChatColor.GRAY + "To page " + (page-1)));
			}
			else {
				inv.setItem(45, generalBg);
			}
			
			inv.setItem(48, goBackItem);
			inv.setItem(50, createNewItem);
			
			if(!ifPlayerHasAccess(p)) {
				inv.setItem(45, warningBell);
			}
			else {
				inv.setItem(45, generalBg);
			}
		}
	}
	
	
	public void updateSingleItemsPage(Player p) {
		Inventory inv = SingleItemsInvs.get(p);
		if(inv!=null) {
			int page = -1;
			if(SingleItemsPage.containsKey(p)) {
				page = SingleItemsPage.get(p).intValue();
			}
//			if(LosPage.containsKey(p) && LocalOrShared.containsKey(p) && LocalOrShared)
			if(LocalOrShared.containsKey(p)) LosPage.put(p, LocalOrShared.get(p));
//			boolean makePrevArrow = false;
			//log(page + "");
			
			YamlConfiguration c = YamlConfiguration.loadConfiguration(new File(getLosPath(p, "items"), p.getName() + ".yml"));
			ArrayList<ItemStack> items = (ArrayList<ItemStack>) c.get("items");
			
			if(items==null) {
				items = new ArrayList<>();
			}
			
			int itemsOnThisPage = items.size();
			itemsOnThisPage = items.size() - 45 * (SingleItemsPage.get(p)-1);
			
			if(itemsOnThisPage>45) {
				itemsOnThisPage = 45;
			}
			
//			log("For1 from 0 to " + itemsOnThisPage);
			
			if(itemsOnThisPage > 0) {
				for (int i = 0; i < itemsOnThisPage; i++) {
//					log(i + "");
					inv.setItem(i, items.get((page-1)*45 + i));
				}
			}
			
//			log("For2 from " + itemsOnThisPage+ " to " + (inv.getSize()-9));
			
			if(inv.getSize()-9 > itemsOnThisPage) {
				for (int i = itemsOnThisPage; i < inv.getSize()-9; i++) {
					inv.setItem(i, new ItemStack(Material.AIR));
				}
			}
			
			if(items.size() - 45 * (SingleItemsPage.get(p)-1) > 45) {
				inv.setItem(53, createGuiItem(Material.ARROW, 1, ChatColor.WHITE + "Next Page", ChatColor.GRAY + "To page " + (page+1)));
			}
			else {
				inv.setItem(53, generalBg);
			}
			
			//log((page>1) + "");
			inv.setItem(45, generalBg);
			inv.setItem(46, generalBg);
			
			if(page>1) {
				inv.setItem(45, createGuiItem(Material.ARROW, 1, ChatColor.WHITE + "Previous Page", ChatColor.GRAY + "To page " + (page-1)));
			}
			else {
				inv.setItem(45, generalBg);
			}
			
			inv.setItem(48, goBackItem);
			inv.setItem(50, createNewItem);
			
			int placeForWarning = 45;
			if(page>1) {
				placeForWarning = 46;
			}
			
			if(!ifPlayerHasAccess(p)) {
				inv.setItem(placeForWarning, warningBell);
			}
			else {
				inv.setItem(placeForWarning, generalBg);
			}
		}
	}
	
	
	public void makeInvAddItem(Player p, Inventory inv) {
		ItemStack item = null;
//		if(ActionOnCurrentSingleItem.get(p).equalsIgnoreCase("add")) {
//			item = CurrentSingleItem.get(p);
//			inv.setItem(4, item);
//		}
//		else {
		inv.setItem(4, new ItemStack(Material.AIR));
//		}
		inv.setItem(0, createGuiItem(Material.BIRCH_SIGN, 1, ChatColor.GOLD + "Tip:", ChatColor.YELLOW + "Click on item in your inventory to select it"));
		inv.setItem(21, goBackItem);
		inv.setItem(23, createGuiItem(Material.LIME_STAINED_GLASS, 1, ChatColor.GREEN + "Confirm"));
		
		if(!ifPlayerHasAccess(p)) {
			inv.setItem(18, warningBell);
		}
		else {
			inv.setItem(18, generalBg);
		}
	}
	
	
	public void updateInvAddItem(Player p, Inventory inv) {
		//log(inv.getType().toString());
		//if(CurrentSingleItem.containsKey(p)) log(CurrentSingleItem.get(p).getType().toString());
		ItemStack item = null;
		if(ActionOnCurrentSingleItem.get(p).equalsIgnoreCase("add")) {
			item = CurrentSingleItem.get(p);
			inv.setItem(4, item);
		}
		else {
			inv.setItem(4, new ItemStack(Material.AIR));
		}
	}
	
	
	public void makeInvEditItem(Player p, Inventory inv) {
		ItemStack item = null;
		if(ActionOnCurrentSingleItem.get(p).equals("edit")) {
			//log("edit");
			if(CurrentSingleItem.containsKey(p) && CurrentSingleItem.get(p)!=null) {
				//log("nonnull item");
				item = CurrentSingleItem.get(p);
				
				inv.setItem(4, item); //later
//				inv.setItem(9, createGuiItem(Material.BIRCH_SIGN, 1, ChatColor.GOLD + "Tip:", ChatColor.YELLOW + "Click on item in your inventory to select it"));
				inv.setItem(20, deleteItem);
				inv.setItem(22, goBackItem);
				inv.setItem(24, takeSmthItem);
//				inv.setItem(24, confirmItem);
				
				if(!ifPlayerHasAccess(p)) {
					inv.setItem(18, warningBell);
				}
				else {
					inv.setItem(18, generalBg);
				}
			}
		}
	}
	
	
	public void makeInvWardrobe(Player p, Inventory inv) {
		if(!WardrobePage.containsKey(p)) {
			WardrobePage.put(p, 1);
		}
		
		updateWardrobe(p);
	}
	
	
	public void updateWardrobe(Player p) {
		Inventory inv = WardrobeInvs.get(p);
		if(inv!=null) {
			int page = -1;
			if(WardrobePage.containsKey(p)) {
				page = WardrobePage.get(p).intValue();
			}
			
			if(WardrobePage.containsKey(p)) LosPage.put(p, LocalOrShared.get(p));
			
			YamlConfiguration c = YamlConfiguration.loadConfiguration(new File(getLosPath(p, "wardrobe"), p.getName() + ".yml"));
			List<List<ItemStack>> all = (List<List<ItemStack>>) c.get("armor sets");
			List<ItemStack> sets = new ArrayList<>();
			
			if(all==null) {
				all = new ArrayList<>();
			}
			
			int setsOnThisPage = all.size();
			setsOnThisPage = all.size() - 9 * (WardrobePage.get(p)-1);
			
			if(setsOnThisPage > 9) {
				setsOnThisPage = 9;
			}
			
//			log("For1 from 0 to " + itemsOnThisPage);
			
			if(setsOnThisPage > 0) {
				for (int i = 0; i < setsOnThisPage; i++) {
//					log(i + "");
					inv.setItem(i, all.get((page-1)*9 + i).get(3));
					inv.setItem(i+9*1, all.get((page-1)*9 + i).get(2));
					inv.setItem(i+9*2, all.get((page-1)*9 + i).get(1));
					inv.setItem(i+9*3, all.get((page-1)*9 + i).get(0));
				}
			}
			
//			log("For2 from " + itemsOnThisPage+ " to " + (inv.getSize()-9));
			
			if(inv.getSize()-9 > setsOnThisPage*4) {
				for (int i = setsOnThisPage; i < 9; i++) {
					inv.setItem(i, new ItemStack(Material.AIR));
					inv.setItem(i+9*1, new ItemStack(Material.AIR));
					inv.setItem(i+9*2, new ItemStack(Material.AIR));
					inv.setItem(i+9*3, new ItemStack(Material.AIR));
					inv.setItem(i+9*4, whiteBg);
				}
			}
			
			for (int i = 36; i < 36+setsOnThisPage; i++) {
				inv.setItem(i, wardrobeEditItem);
			}
			
			if(all.size() - 9 * (WardrobePage.get(p)-1) > 9) {
				inv.setItem(53, createGuiItem(Material.ARROW, 1, ChatColor.WHITE + "Next Page", ChatColor.GRAY + "To page " + (page+1)));
			}
			else {
				inv.setItem(53, generalBg);
			}
			
			//log((page>1) + "");
			inv.setItem(45, generalBg);
			inv.setItem(46, generalBg);
			
			if(page>1) {
				inv.setItem(45, createGuiItem(Material.ARROW, 1, ChatColor.WHITE + "Previous Page", ChatColor.GRAY + "To page " + (page-1)));
			}
			else {
				inv.setItem(45, generalBg);
			}
			
			inv.setItem(48, goBackItem);
			inv.setItem(50, createNewItem);
			
			int placeForWarning = 45;
			if(page>1) {
				placeForWarning = 46;
			}
			
			if(!ifPlayerHasAccess(p)) {
				inv.setItem(placeForWarning, warningBell);
			}
			else {
				inv.setItem(placeForWarning, generalBg);
			}
		}
	}

	
	public void makeInvAddGear(Player p) {
		Inventory inv = AddGearInvs.get(p);
		if(debug) log("addgear");
		
		fillGearMap(p);
		
		if(inv!=null) {
			List<ItemStack> set = new ArrayList<>();
			
			ItemStack tip = createGuiItem(tipMaterial, 1, ChatColor.BLUE + "Tip:", ChatColor.AQUA + "(will be added later)");
			inv.setItem(0, tip);
			
			inv.setItem(10, addGearNoItem);
			inv.setItem(19, addGearNoItem);
			inv.setItem(28, addGearNoItem);
			inv.setItem(37, addGearNoItem);
			
			inv.setItem(11, addGearUnready);
			inv.setItem(20, addGearUnready);
			inv.setItem(29, addGearUnready);
			inv.setItem(38, addGearUnready);
			
			inv.setItem(24, new ItemStack(Material.AIR));
			inv.setItem(31, revertItem);
			inv.setItem(33, addGearFromWearingReady);
			inv.setItem(35, addGearNoConfirm);
			
			
			/*if(setsOnThisPage > 0) {
				for (int i = 0; i < setsOnThisPage; i++) {
//					log(i + "");
					inv.setItem(i, all.get((page-1)*9 + i).get(3));
					inv.setItem(i+9*1, all.get((page-1)*9 + i).get(2));
					inv.setItem(i+9*2, all.get((page-1)*9 + i).get(1));
					inv.setItem(i+9*3, all.get((page-1)*9 + i).get(0));
				}
			}
			
//			log("For2 from " + itemsOnThisPage+ " to " + (inv.getSize()-9));
			
			if(inv.getSize()-9 > setsOnThisPage*4) {
				for (int i = setsOnThisPage; i < 9; i++) {
					inv.setItem(i, new ItemStack(Material.AIR));
					inv.setItem(i+9*1, new ItemStack(Material.AIR));
					inv.setItem(i+9*2, new ItemStack(Material.AIR));
					inv.setItem(i+9*3, new ItemStack(Material.AIR));
					inv.setItem(i+9*4, whiteBg);
				}
			}
			
			for (int i = 36; i < 36+setsOnThisPage; i++) {
				inv.setItem(i, wardrobeEditItem);
			}*/
			
			inv.setItem(48, goBackItem);
			inv.setItem(50, confirmItem);
			
			if(!ifPlayerHasAccess(p)) {
				inv.setItem(45, warningBell);
			}
			else {
				inv.setItem(45, generalBg);
			}
		}
	}
	
	
	protected ItemStack createGuiItem(final Material material, int amount, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, amount);
        final ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);

        return item;
    }
	
	
	protected ItemStack createGuiItem(final Material material, int amount, final String name) {
		final ItemStack item = new ItemStack(material, amount);
        final ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        
        return item;
	}
	
	
	protected ItemStack createGuiItem(final Material material, int amount, final String name, final ArrayList<String> lore) {
        final ItemStack item = new ItemStack(material, amount);
        final ItemMeta meta = item.getItemMeta();
        
        String[] loreArray = new String[lore.size()];
        for(int i = 0; i < lore.size(); i++) {
        	loreArray[i] = lore.get(i);
        }
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(loreArray));
        item.setItemMeta(meta);
        
        return item;
    }
	
	
	public void openUniversalView(Player p, String type, Map<Player, Inventory> map, String name, int size, Material bg) {
		Inventory inv = null;
		if(map.containsKey(p)) {
			inv = map.get(p);
		}
		else {
			//int size = 45;
			//Material bg = Material.GRAY_STAINED_GLASS_PANE;
			//String name = "Inventory Tools Menu";
			ItemStack bgis = createGuiItem(bg, 1, " ");
			
			inv = Bukkit.createInventory(null, size, name);
			fillInvWithItemStack(inv, bgis);
			postOpenView(p, inv, type);
			map.put(p, inv);
		}
		WhereIsPlayer.put(p, map);
		postOpenView(p, inv, type);
		p.openInventory(inv);
	}
	
	
	public void postOpenView(Player p, Inventory inv, String type) {
		if(type.equalsIgnoreCase("Main")) {
			makeInvMain(p, inv);
		}
		else if(type.equalsIgnoreCase("SelectMode")) {
			makeInvSelectMode(p, inv);
		}
		else if(type.equalsIgnoreCase("SavedFull")) {
			
		}
		else if(type.equalsIgnoreCase("Wardrobe")) {
			makeInvWardrobe(p, inv);
		}
		else if(type.equalsIgnoreCase("AddGear")) {
			makeInvAddGear(p);
		}
		else if(type.equalsIgnoreCase("Items")) {
			makeInvSingleItems(p, inv);
		}
		else if(type.equalsIgnoreCase("Other")) {
			
		}
		else if(type.equalsIgnoreCase("AddItem")) {
			makeInvAddItem(p, inv);
		}
		else if(type.equalsIgnoreCase("EditItem")) {
			makeInvEditItem(p, inv);
		}
		else if(type.equalsIgnoreCase("Recover")) {
			
		}
		else if(type.equalsIgnoreCase("Settings")) {
			
		}
	}
	
	
	public void openUniversalView(Player p, String type, Map<Player, Inventory> map, String name, int size) {
		openUniversalView(p, type, map, name, size, Material.GRAY_STAINED_GLASS_PANE);
	}
	
	
	public void openUniversalView(Player p, String type, Map<Player, Inventory> map, String name) {
		openUniversalView(p, type, map, name, 54);
	}
	
	
	public void openUniversalView(Player p, String type, Map<Player, Inventory> map, String name, Material bg) {
		openUniversalView(p, type, map, name, 54, bg);
	}
	
	
	public void addSingleItem(Player p) {
		ItemStack stack = null;
		YamlConfiguration c = null;
		List<ItemStack> items = null;
		
		stack = CurrentSingleItem.get(p);
		if(stack!=null) {
			c = YamlConfiguration.loadConfiguration(new File(getLosPath(p, "items"), p.getName() + ".yml"));
			items = (ArrayList<ItemStack>) c.get("items");
			if(items==null) {
				items = new ArrayList<>();
			}
			items.add(stack);
			
			c.set("items", items);
			try {
				c.save(new File(getLosPath(p, "items"), p.getName() + ".yml"));
			} catch (IOException e) {
				if(logCatchedErrors) e.printStackTrace();
			}
			makeInvSingleItems(p, SingleItemsInvs.get(p));
		}
	}
	
	
	public void removeSingleItem(Player p) {
		ItemStack stack = null;
		YamlConfiguration c = null;
		List<ItemStack> items = null;
		
		if(ActionOnCurrentSingleItem.get(p).equalsIgnoreCase("edit")) {
			stack = CurrentSingleItem.get(p);
			if(stack!=null) {
				c = YamlConfiguration.loadConfiguration(new File(getLosPath(p, "items"), p.getName() + ".yml"));
				items = (ArrayList<ItemStack>) c.get("items");
				if(items==null) {
					items = new ArrayList<>();
				}
				if(items.contains(stack)) {
					items.remove(stack);
				}
				
				c.set("items", items);
				try {
					c.save(new File(getLosPath(p, "items"), p.getName() + ".yml"));
				} catch (IOException e) {
					if(logCatchedErrors) e.printStackTrace();
				}
				makeInvSingleItems(p, SingleItemsInvs.get(p));
			}
		}
	}
	
	
	public void takeSingleItem(Player p) {
		ItemStack stack = null;
		YamlConfiguration c = null;
		
		if(ActionOnCurrentSingleItem.get(p).equalsIgnoreCase("edit")) {
			stack = CurrentSingleItem.get(p);
			if(stack!=null) {
				if(ifPlayerHasAccess(p)) {
					if(p.getInventory().firstEmpty()!=-1) {
						p.getInventory().addItem(stack.clone());
						p.sendMessage(messageLoadedSingleItem);
					}
					else {
						p.sendMessage(messageFullInventory);
						//p.playSound(p.getLocation(), failure, 1, 1);
					}
				}
				else {
					p.sendMessage(messageDontHavePermsUniversal);
					//p.playSound(p.getLocation(), failure, 1, 1);
				}
			}
		}
	}
	
	public void checkGearEditClick(InventoryClickEvent e) {
		Inventory eInv = e.getInventory();
		Inventory eClickedInv = e.getClickedInventory();
		ItemStack item = e.getCurrentItem();
		int slot = e.getSlot();
		if(e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();
			Inventory inv = AddGearInvs.get(p);
			Integer elt = CurrentEditGearElt.get(p);
			if(inv!=null) {
				if(eClickedInv!=null) {
					//log("1 " + (eClickedInv.equals(AddGearInvs.get(p))));
					//log("1 " + (eClickedInv.equals(p.getInventory())));
					//log("1 " + (item!=null && elt!=null));
					if(AddGearInvs.containsKey(p) && eClickedInv.equals(AddGearInvs.get(p))) {
						if(debug) log("up " + slot);
						if(slot==10 || slot==11) {
							if(elt==null || elt!=0) {
								CurrentEditGearElt.put(p, 0);
							}
							else {
								CurrentEditGearElt.put(p, null);
							}
							updateAddGearItemonRight(p, eInv, CurrentEditGearElt.get(p));
						}
						else if(slot==19 || slot==20) {
							if(elt==null || elt!=1) {
								CurrentEditGearElt.put(p, 1);
							}
							else {
								CurrentEditGearElt.put(p, null);
							}
							if(debug) log(CurrentEditGearElt.toString());
							updateAddGearItemonRight(p, eInv, CurrentEditGearElt.get(p));
						}
						else if(slot==28 || slot==29) {
							if(elt==null || elt!=2) {
								CurrentEditGearElt.put(p, 2);
							}
							else {
								CurrentEditGearElt.put(p, null);
							}
							updateAddGearItemonRight(p, eInv, CurrentEditGearElt.get(p));
						}
						else if(slot==37 || slot==38) {
							if(elt==null || elt!=3) {
								CurrentEditGearElt.put(p, 3);
							}
							else {
								CurrentEditGearElt.put(p, null);
							}
							updateAddGearItemonRight(p, eInv, CurrentEditGearElt.get(p));
						}
						else if(slot == 33) {
							if(elt!=null) {
								TempEditGear.put(p, (List<ItemStack>) emptyItemStacksArrayList.clone());
								if(elt==0) {
									TempEditGear.get(p).set(elt, p.getInventory().getHelmet());
								}
								if(elt==1) {
									TempEditGear.get(p).set(elt, p.getInventory().getChestplate());
								}
								if(elt==2) {
									TempEditGear.get(p).set(elt, p.getInventory().getLeggings());
								}
								if(elt==3) {
									TempEditGear.get(p).set(elt, p.getInventory().getBoots());
								}
								updateAddGearItemonRight(p, inv, elt);
							}
							else {
								CurrentEditGearElt.put(p, 5);
								List<ItemStack> list = new ArrayList<>();
								list.add(p.getInventory().getHelmet());
								list.add(p.getInventory().getChestplate());
								list.add(p.getInventory().getLeggings());
								list.add(p.getInventory().getBoots());
								updateEditGearSlotLeft(p, inv, 10, 0);
								updateEditGearSlotLeft(p, inv, 19, 1);
								updateEditGearSlotLeft(p, inv, 28, 2);
								updateEditGearSlotLeft(p, inv, 37, 3);
//								eClickedInv.setItem(19, CurrentEditGear.get(p).get(1));
								TempEditGear.put(p, list);
							}
						}
						else if(slot == 31) {
							TempEditGear.put(p, new ArrayList<>());
							renderEditGearItems(p, inv);
							renderEditGearDyes(p, inv);
						}
						else if(slot == 35) {
							for (int i = 0; i < 4; i++) {
								if(TempEditGear.get(p).get(i)!=null && !TempEditGear.get(p).get(i).getType().equals(Material.AIR)) {
									CurrentEditGear.get(p).set(i, TempEditGear.get(p).get(i));
								}
							}
							TempEditGear.put(p, (List<ItemStack>) emptyItemStacksArrayList.clone());
							renderEditGearItems(p, inv);
							renderEditGearDyes(p, inv);
						}
					}
					else if (eInv.equals(AddGearInvs.get(p)) && eClickedInv.equals(p.getInventory())) {
						if(debug) log("down");
						if(item!=null && elt!=null && !item.getType().equals(Material.AIR)) {
							fillGearMap(p);
							if(debug) log("current elt: " + elt.intValue() + " / 3");
							TempEditGear.put(p, (List<ItemStack>) emptyItemStacksArrayList.clone());
							TempEditGear.get(p).set(elt.intValue(), item);
							e.setCancelled(true);
						}
					}
//					log("CurrentEditGear: " + CurrentEditGear.toString());
//					log("CurrentEditGearElt: " + CurrentEditGearElt.toString());
//					log("TempEditGear: " + TempEditGear.toString());	
//					log("CurrentSingleItem: " + CurrentSingleItem.toString());
					renderEditGearItems(p, inv);
					renderEditGearDyes(p, inv);
				}
			}
		}
	}
	
	public void renderEditGearItems(Player p, Inventory inv) {
		if(debug) log("CurrentEditGear: " + CurrentEditGear.toString());
		if(debug) log("CurrentEditGearElt: " + CurrentEditGearElt.toString());
		if(debug) log("TempEditGear: " + TempEditGear.toString());
		for(int i = 0; i < 4; i++) {
			ItemStack temp = TempEditGear.get(p).get(i);
			if(temp!=null && !temp.getType().equals(Material.AIR)) {
				inv.setItem(10 + i*9, temp);
			}
			else {
				ItemStack current = CurrentEditGear.get(p).get(i);
				if(current!=null && !current.getType().equals(Material.AIR)) {
					inv.setItem(10 + i*9, current);
				}
				else {
					inv.setItem(10 + i*9, addGearNoItem);
				}
			}
		}
	}
	
	public void renderEditGearDyes(Player p, Inventory inv) {
		ArrayList<Integer> slots = new ArrayList<>();
		slots.add(11);
		slots.add(20);
		slots.add(29);
		slots.add(38);
		
		if(CurrentEditGear.get(p)==null) {
			for (Integer i : slots) {
				inv.setItem(i, addGearUnready);
			}
			return;
		}
		if(CurrentEditGearElt.get(p)==null) return;
		int elt = CurrentEditGearElt.get(p);
		
		for (int i = 0; i < 4; i++) {
			ItemStack current = CurrentEditGear.get(p).get(i);
			ItemStack temp = TempEditGear.get(p).get(i);
			if(current!=null || (current!=null && !current.getType().equals(Material.AIR))) {
				inv.setItem(slots.get(i), addGearAdded);
			}
			else if((current==null || current.getType().equals(Material.AIR)) && (temp!=null || (temp!=null && !temp.getType().equals(Material.AIR)))) {
				inv.setItem(slots.get(i), addGearEditingDye);
				inv.setItem(slots.get(i)-1, addGearEditingPane);
			}
			else {
				inv.setItem(slots.get(i), addGearUnready);
			}
		}
		
		if(elt!=5) {
			slots.remove(elt);
		}
		else {
			slots = new ArrayList<>();
		}
		for (int i : slots) {
			inv.setItem(i, addGearUnready);
		}
//		for 
	}
	
	public void updateEditGearSlotLeft(Player p, Inventory inv, int slot, int el) {
		ItemStack item = CurrentEditGear.get(p).get(el);
		if(item!=null && !item.getType().equals(Material.AIR)) {
			inv.setItem(slot, item);
		}
		else {
			inv.setItem(slot, addGearNoItem);
		}
	}
	
	public void updateAddGearItemonRight(Player p, Inventory inv, Integer num) {
		if(debug) log("uagior 1 " + (num!=null));
		if(num!=null) {
			if(debug) log("uagior 2 " + (CurrentEditGear.get(p)!=null));
			if(debug) log("uagior 3 " + (CurrentEditGear.get(p).size()>num.intValue()));
			if(debug) log("uagior 4 " + (CurrentEditGear.get(p).get(num.intValue())!=null));
			if(debug) log("uagior 5 " + (!CurrentEditGear.get(p).get(num.intValue()).equals(new ItemStack(Material.AIR))));
		}
		if(num!=null && CurrentEditGear.get(p)!=null && CurrentEditGear.get(p).size()>num.intValue() && CurrentEditGear.get(p).get(num.intValue())!=null && !CurrentEditGear.get(p).get(num.intValue()).equals(new ItemStack(Material.AIR))) {
			int numInt = num.intValue();
			inv.setItem(24, CurrentEditGear.get(p).get(numInt));
			if(debug) log("uagior set 24 item");
		}
		else {
			inv.setItem(24, new ItemStack(Material.AIR));
		}
	}
	
	public void fillGearMap(Player p) {
		if(!CurrentEditGear.containsKey(p)) {
			List<ItemStack> list = new ArrayList<ItemStack>();
			list.add(new ItemStack(Material.AIR));
			list.add(new ItemStack(Material.AIR));
			list.add(new ItemStack(Material.AIR));
			list.add(new ItemStack(Material.AIR));
			CurrentEditGear.put(p, list);
			TempEditGear.put(p, list);
		}
		if(!CurrentEditGearElt.containsKey(p)) {
			CurrentEditGearElt.put(p, null);
		}
	}
	
	//--------------------------------------------------------------------FILES--------------------------------------------------------------------------------------------
	
	
	
	//--------------------------------------------------------------------FILES--------------------------------------------------------------------------------------------
	
	public void saveInventory (File dir, Player p) {
		YamlConfiguration c = new YamlConfiguration();
		c.set("inventory.armor", p.getInventory().getArmorContents());
		c.set("inventory.content", p.getInventory().getContents());
		try {
			c.save(new File(dir, p.getName()+".yml"));
		} catch (IOException e) {
			if(logCatchedErrors) e.printStackTrace();
		}
	}

	
	public void loadInventory(File dir, Player p) throws IOException {
		//File file = new File(inventoriesDirectory, p.getName()+".yml");
		YamlConfiguration c = null;
		ItemStack[] content = null;
		c = YamlConfiguration.loadConfiguration(new File(dir, p.getName()+".yml"));
		try{
			content = ((List<ItemStack>) c.get("inventory.armor")).toArray(new ItemStack[0]);
		}
		catch(NullPointerException e1) {
			//ignore
		}
		if(content!=null) p.getInventory().setArmorContents(content);
		try{
			content = ((List<ItemStack>) c.get("inventory.content")).toArray(new ItemStack[0]);
		}
		catch(NullPointerException e1) {
			//ignore
		}
		if(content!=null) p.getInventory().setContents(content);
	}

	
	public void saveAirInFile(File dir, Player p){
		int air = p.getRemainingAir();
		saveAnythingInFile(p, "air", Integer.toString(air), dir);
		//log("saveAirInFile");
	}
	
	
	public int getAirFromFile(File dir, Player p) {
		String air = getAnythingFromFile(p, "air", dir);
		//log(air);
		if(air != null) return Integer.parseInt(air);
		return 300;
	}

	
	public void saveAnythingInFile(Player p, String name, String value, File dir) {
		//if(AuthPlayers.contains(p) && name != "auth") return;
		FileOutputStream fos = null;
		props.clear();
		FileReader reader = null;
		try {
			File f = new File(dir, p.getName()+".properties");
			if (f.exists()) {
				try {
					reader = new FileReader(f);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				props.load(reader);
				reader.close();
				if(props.getProperty(name) != null && props.getProperty(name).equals(value)){
					return;
				}
			}
			props.setProperty(name, value);
			fos = new FileOutputStream(f);
			props.store(fos, "");
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if(fos != null) {
				try {
					fos.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		}
	}
	
	
	public String getAnythingFromFile(Player p, String name, File dir) {
		FileOutputStream fos = null;
		props.clear();
		FileReader reader = null;
		try {
			File f = new File(dir, p.getName()+".properties");
			if (f.exists()) {
				try {
					reader = new FileReader(f);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				props.load(reader);
				reader.close();
				return (props.getProperty(name));
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if(fos != null) {
				try {
					fos.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		}
		return null;
	}

	
	public String[] getFileAuthPlayers() {
		YamlConfiguration c = null;
		String[] content = null;
		c = YamlConfiguration.loadConfiguration(new File(authDataDirectory, "authInfo.yml"));
		try{
			content = ((List<String>) c.get("playersinauth")).toArray(new String[0]);
		}
		catch(NullPointerException e1) {
			//ignore
		}
		return content;
	}
	
	
	public boolean ifPlayerIsAddedinAuthFile(Player p) {
		String[] players = getFileAuthPlayers();
		ArrayList<String> list = new ArrayList<>();
		if(players!=null) {
			for (int i = 0; i<players.length; i++) {
				if(players[i].equals(p.getName())) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	public void test1() {
		Inventory inv = Bukkit.createInventory(null, 45, "Inventory Tools");
	}
	
	public void addPermsOneLoad(CommandSender sender, String[] args) {
		if(!(sender instanceof ConsoleCommandSender)) {
			if(sender instanceof Player) {
				sender.sendMessage(ChatColor.RED + "This comand is only for console.");
			}
			return;
		}
		if(args.length!=1) {
			log(ChatColor.RED + "Arguments' length should be 1.");
			return;
		}
		if(getServer().getPlayerExact(args[0]) == null) {
			log(ChatColor.YELLOW + "[Warning] The player is not online.");
			return;
		}
		Player p = getServer().getPlayer(args[0]);
		if(p!=null && ifPlayerHasAccess(p) == true) {
			log(ChatColor.YELLOW + "[Warning] The player is already added.");
			return;
		}
		YamlConfiguration c = new YamlConfiguration();
		String[] players = null;
		try {
			players = getOneLoadAccessPlayers();
		} catch (IOException e) {
			if(logCatchedErrors) e.printStackTrace();
		}
		ArrayList<String> list = new ArrayList<>();
		if(players!=null) {
			for (int i = 0; i<players.length; i++) {
				list.add(players[i]);
			}
		}
		if(p!=null) {
			list.add(p.getName());
		}
		c.set("oneloadaccess", list);
		try {
			c.save(new File(localMainInvDirectory, "invLoadAccess.yml"));
		} catch (IOException e) {
			if(logCatchedErrors) e.printStackTrace();
		}
		updateWarning(p, true);
		p.sendMessage(messageAddedOneLoadPerms);
		log(ChatColor.GREEN + "Done.");
	}
	
	public void addPerms(CommandSender sender, String[] args) throws IOException {
		if(!(sender instanceof ConsoleCommandSender)) {
			if(sender instanceof Player) {
				sender.sendMessage(ChatColor.RED + "This comand is only for console.");
			}
			return;
		}
		if(args.length!=1) {
			log(ChatColor.RED + "Arguments' length should be 1.");
			return;
		}
		//log(getServer().getPlayerExact(args[0]) + "");
		if(getServer().getPlayerExact(args[0]) == null) {
			log(ChatColor.YELLOW + "[Warning] The player is not online.");
			return;
		}
		Player p = getServer().getPlayer(args[0]);
		if(p!=null && ifPlayerHasAccess(p) == true) {
			log(ChatColor.YELLOW + "[Warning] The player is already added.");
			return;
		}
		YamlConfiguration c = new YamlConfiguration();
		String[] players = getAccessPlayers();
		ArrayList<String> list = new ArrayList<>();
		if(players!=null) {
			for (int i = 0; i<players.length; i++) {
				list.add(players[i]);
			}
		}
		if(p!=null) {
			list.add(p.getName());
		}
		c.set("invloadaccess", list);
		c.save(new File(localMainInvDirectory, "invLoadAccess.yml"));
		updateWarning(p, true);
		p.sendMessage(messageAddedPerms);
		log(ChatColor.GREEN + "Done.");
	}
	
	public void removeOneLoadPerms(Player p) {
		YamlConfiguration cOneLoad = new YamlConfiguration();
		String[] playersOneLoad = null;
		try {
			playersOneLoad = getOneLoadAccessPlayers();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		ArrayList<String> listOneLoad = new ArrayList<String>();
		if(playersOneLoad!=null) {
			for (int i = 0; i<playersOneLoad.length; i++) {
				listOneLoad.add(playersOneLoad[i]);
			}
		}
		if(p!=null && listOneLoad.contains(p.getName())) {
			listOneLoad.remove(p.getName());
		}
		cOneLoad.set("oneloadaccess", listOneLoad);
		try {
			cOneLoad.save(new File(localMainInvDirectory, "invLoadAccess.yml"));
		} catch (IOException e) {
			if(logCatchedErrors) e.printStackTrace();
		}
	}
	
	public void removePerms(CommandSender sender, String[] args) throws IOException {
		if(!(sender instanceof ConsoleCommandSender)) {
			if(sender instanceof Player) {
				sender.sendMessage(ChatColor.RED + "This comand is only for console.");
			}
			return;
		}
		if(args.length!=1) {
			log(ChatColor.RED + "Arguments' length should be 1.");
			return;
		}
		if(getServer().getPlayerExact(args[0]) == null) {
			log(ChatColor.YELLOW + "[Warning] The player is not online.");
			return;
		}
		Player p = getServer().getPlayerExact(args[0]);
		if(p!=null && ifPlayerHasAccess(p) == false) {
			log(ChatColor.YELLOW + "[Warning] The player is already removed.");
			return;
		}
		YamlConfiguration c = new YamlConfiguration();
		String[] players = getAccessPlayers();
		ArrayList<String> list = new ArrayList<String>();
		if(players!=null) {
			for (int i = 0; i<players.length; i++) {
				list.add(players[i]);
			}
		}
		if(p!=null && list.contains(p.getName())) {
			list.remove(p.getName());
		}
		c.set("invloadaccess", list);
		c.save(new File(localMainInvDirectory, "invLoadAccess.yml"));
		
		
		YamlConfiguration cOneLoad = new YamlConfiguration();
		String[] playersOneLoad = getOneLoadAccessPlayers();
		ArrayList<String> listOneLoad = new ArrayList<String>();
		if(playersOneLoad!=null) {
			for (int i = 0; i<playersOneLoad.length; i++) {
				listOneLoad.add(playersOneLoad[i]);
			}
		}
		if(p!=null && listOneLoad.contains(p.getName())) {
			listOneLoad.remove(p.getName());
		}
		cOneLoad.set("oneloadaccess", listOneLoad);
		cOneLoad.save(new File(localMainInvDirectory, "invLoadAccess.yml"));
		
		
		updateWarning(p, false);
		p.sendMessage(messageRemovedPerms);
		log(ChatColor.GREEN + "Done.");
	}
	
	public void updateWarning(Player p, boolean hasAccessNow){
		Map<Player, Inventory> map = WhereIsPlayer.get(p);
		//log(map.equals(EditItemInvs) + "");
		if(map==null){
			//log("no");
			return;
		}
		Inventory inv = map.get(p);
		if(map == MainInvs) {
			if(hasAccessNow) {
				inv.setItem(36, generalBg);
			}
			else {
				inv.setItem(36, warningBell);
			}
		}
		else if(map == SelectModeInvs) {
			if(hasAccessNow) {
				inv.setItem(36, generalBg);
			}
			else {
				inv.setItem(36, warningBell);
			}
		}
		else if(map == SavedFullInvs) {
			
		}
		else if(map == WardrobeInvs) {
			
		}
		else if(map == SingleItemsInvs) {
			if(hasAccessNow) {
				inv.setItem(45, generalBg);
			}
			else {
				inv.setItem(45, warningBell);
			}
		}
		else if(map == AddItemInvs) {
			if(hasAccessNow) {
				inv.setItem(18, generalBg);
			}
			else {
				inv.setItem(18, warningBell);
			}
		}
		else if(map == EditItemInvs) {
			if(hasAccessNow) {
				inv.setItem(18, generalBg);
			}
			else {
				inv.setItem(18, warningBell);
			}
		}
	}
	
	
	public String[] getAccessPlayers() throws IOException {
		YamlConfiguration c = null;
		String[] content = null;
		c = YamlConfiguration.loadConfiguration(new File(localMainInvDirectory, "invLoadAccess.yml"));
		try{
			content = ((List<String>) c.get("invloadaccess")).toArray(new String[0]);
		}
		catch(NullPointerException e1) {
			//ignore
		}
		return content;
	}
	
	public String[] getOneLoadAccessPlayers() throws IOException {
		YamlConfiguration c = null;
		String[] content = null;
		c = YamlConfiguration.loadConfiguration(new File(localMainInvDirectory, "invLoadAccess.yml"));
		try{
			content = ((List<String>) c.get("oneloadaccess")).toArray(new String[0]);
		}
		catch(NullPointerException e1) {
			//ignore
		}
		return content;
	}
	
	public boolean ifPlayerHasAccess(Player p) {
		String[] players = null;
		try {
			players = getAccessPlayers();
		} catch (IOException e) {
			if(logCatchedErrors) e.printStackTrace();
		}
		ArrayList<String> list = new ArrayList<>();
		if(players!=null) {
			for (int i = 0; i<players.length; i++) {
				if(players[i].equals(p.getName())) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean ifPlayerHasOneLoadAccess(Player p) {
		String[] players = null;
		try {
			players = getOneLoadAccessPlayers();
		} catch (IOException e) {
			if(logCatchedErrors) e.printStackTrace();
		}
		ArrayList<String> list = new ArrayList<>();
		if(players!=null) {
			for (int i = 0; i<players.length; i++) {
				if(players[i].equals(p.getName())) {
					return true;
				}
			}
		}
		return false;
	}
	
	public int quickLoadInventory(Player p) {
		int exists = -1;
		if(!ifPlayerHasAccess(p) && !ifPlayerHasOneLoadAccess(p)) {
			p.sendMessage(messageDontHavePermsQuick);
			//p.playSound(p.getLocation(), failure, 1, 1);
			p.closeInventory();
			return exists;
		}
		File f = new File(localMainInvDirectory, "invLoadAccess.yml");
		FileOutputStream fos = null;
		if(f!=null && !f.exists() && f.getParentFile()!=null) {
			f.getParentFile().mkdirs();
		}
		//log(f.exists() + "");
		if(!f.exists()) {
			exists = 1;
//			props.clear();
//			fos = new FileOutputStream(f);
//			props.store(fos, "");
//			if(fos != null) {
//				try {
//					fos.close();
//				} catch (IOException e2) {
//					e2.printStackTrace();
//				}
//			}
			YamlConfiguration c = new YamlConfiguration();
			List<String> array = new ArrayList<>();
			//array.add("-");
			c.set("invloadaccess", array);
			try {
				c.save(new File(localMainInvDirectory, "invLoadAccess.yml"));
			} catch (IOException e) {
				if(logCatchedErrors) e.printStackTrace();
			}
		}
		else {
			exists = 0;
		}
		//File file = new File(inventoriesDirectory, p.getName()+".yml");
		if((p.isOp() && !ifPlayerHasAccess(p) && !ifPlayerHasOneLoadAccess(p))) {
			return exists;
		}
		YamlConfiguration c = null;
		ItemStack[] content = null;
		c = YamlConfiguration.loadConfiguration(new File(getLosPath(p, "quick") + "/saved", p.getName()+".yml"));
		try{
			content = ((List<ItemStack>) c.get("inventory.armor")).toArray(new ItemStack[0]);
		}
		catch(NullPointerException e1) {
			//ignore
		}
		if(content!=null) p.getInventory().setArmorContents(content);
		try{
			content = ((List<ItemStack>) c.get("inventory.content")).toArray(new ItemStack[0]);
		}
		catch(NullPointerException e1) {
			//ignore
		}
		if(content!=null) p.getInventory().setContents(content);
		
		if(ifPlayerHasOneLoadAccess(p)) {
			//removeOneLoadPerms(Bukkit.getConsoleSender(), new String[] {p.getName()});
			removeOneLoadPerms(p);
		}
		
		p.sendMessage(messageQuickLoadedInv);
		
		p.closeInventory();
		return exists;
	}
	
	
	public void initializeFiles() {
		//sharedPathString = "G:\\SERVERS\\Shared";
		
//		authDirectory = new File(getServer().getWorldContainer().getCanonicalPath() + "\\files\\authorization");
//		inventoriesDirectory = new File(getServer().getWorldContainer().getCanonicalPath(), "\\files\\inventories");
		dataDirectory = new File(getServer().getWorldContainer(), "files/data");
		authDataDirectory = new File(dataDirectory, "authorizationData");
		
		localMainInvDirectory = new File(getServer().getWorldContainer(), "files/invmain");
		localQuickInvSaveDirectory = new File(localMainInvDirectory, "quick");
		localExtendedInvSaveDirectory = new File(localMainInvDirectory, "extended");
		localExtendedFullInvsDirectory = new File(localExtendedInvSaveDirectory, "fullinvs");
		localExtendedWardrobeDirectory = new File(localExtendedInvSaveDirectory, "wardrobe");
		localExtendedItemsDirectory = new File(localExtendedInvSaveDirectory, "items");
		
		sharedMainInvDirectory = new File(sharedPathString, "files/invmain");
		sharedQuickInvSaveDirectory = new File(sharedMainInvDirectory, "quick");
		sharedExtendedInvSaveDirectory = new File(sharedMainInvDirectory, "extended");
		sharedExtendedFullInvsDirectory = new File(sharedExtendedInvSaveDirectory, "fullinvs");
		sharedExtendedWardrobeDirectory = new File(sharedExtendedInvSaveDirectory, "wardrobe");
		sharedExtendedItemsDirectory = new File(sharedExtendedInvSaveDirectory, "items");
		
		configsDirectory = new File(getServer().getWorldContainer(), "configs");
		mainConfigDirectory = new File(configsDirectory, "AdvansedInvSave");
		
		authDataDirectory.mkdirs();
		
		localQuickInvSaveDirectory.mkdirs();
		localExtendedFullInvsDirectory.mkdirs();
		localExtendedWardrobeDirectory.mkdirs();
		localExtendedItemsDirectory.mkdirs();
		
		sharedQuickInvSaveDirectory.mkdirs();
		sharedExtendedFullInvsDirectory.mkdirs();
		sharedExtendedWardrobeDirectory.mkdirs();
		sharedExtendedItemsDirectory.mkdirs();
	}
	
	//----------------------------------------------------------------LOCAL-OR-SHARED--------------------------------------------------------------------------------------------
	
	
	
	public File getLosPath(Player p, String type) {
		if(!LocalOrShared.containsKey(p) || LocalOrShared.get(p).equalsIgnoreCase("local")) {
			if(type.equalsIgnoreCase("fullinvs")) {
				return localExtendedFullInvsDirectory;
			}
			else if(type.equalsIgnoreCase("wardrobe")) {
				return localExtendedWardrobeDirectory;
			}
			else if(type.equalsIgnoreCase("items")) {
				return localExtendedItemsDirectory;
			}
			else if(type.equalsIgnoreCase("maininv")) {
				return localMainInvDirectory;
			}
			else if(type.equalsIgnoreCase("quick")) {
				return localQuickInvSaveDirectory;
			}
		}
		else if (LocalOrShared.get(p).equalsIgnoreCase("shared")) {
			if(type.equalsIgnoreCase("fullinvs")) {
				return sharedExtendedFullInvsDirectory;
			}
			else if(type.equalsIgnoreCase("wardrobe")) {
				return sharedExtendedWardrobeDirectory;
			}
			else if(type.equalsIgnoreCase("items")) {
				return sharedExtendedItemsDirectory;
			}
			else if(type.equalsIgnoreCase("maininv")) {
				return sharedMainInvDirectory;
			}
			else if(type.equalsIgnoreCase("quick")) {
				return sharedQuickInvSaveDirectory;
			}
		}
		return null;
	}
	
	//--------------------------------------------------------------------EVENTS--------------------------------------------------------------------------------------------
		
	@EventHandler
	public void invClick(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		Inventory clInv = e.getClickedInventory();
		ItemStack item = e.getCurrentItem();
		Player p = (Player) e.getWhoClicked();
		if(clInv!=null && !clInv.equals(p.getInventory())) {
			if(WhereIsPlayer.containsKey(p)) {
				if(clInv.equals(WhereIsPlayer.get(p).get(p))) {
//					log("Inv clicked");
					e.setCancelled(true);
					onGuiClick(p, e, clInv);
				}
			}
		}
		//log(e.getClickedInventory().getType().toString());
		//log(p.getInventory().getType().toString());
//		if(e.getInventory().equals(MainInvs.get(p))){
//			//log("2. ");
//			if(e.getClickedInventory().equals(p.getInventory())) {
//				log(e.getCurrentItem().getType().toString());
//			}
//		}
		else if(inv.equals(AddItemInvs.get(p)) && clInv.equals(p.getInventory())) {
			if(item!=null) {
				e.setCancelled(true);
				CurrentSingleItem.put(p, item.clone());
				updateInvAddItem(p, inv);
			}
		}
		checkGearEditClick(e);
		//log(CurrentEditGear.toString());
		//log(CurrentEditGearElt.toString());
		//log(CurrentSingleItem.toString());
	}
	
	
	public void onGuiClick(Player p, InventoryClickEvent e, Inventory inv) {
		ItemStack item = e.getCurrentItem();
		if (inv.equals(MainInvs.get(p)) && item.getType().equals(Material.BARREL)) {
			saveInventory(new File(getLosPath(p, "quick") + "/saved"), p);
			p.closeInventory();
			p.sendMessage(messageQuickSavedInv);
		} else if (inv.equals(MainInvs.get(p)) && item.getType().equals(Material.DROPPER)) {
			quickLoadInventory(p);
		} else if (inv.equals(MainInvs.get(p)) && item.getType().equals(Material.CHEST)) {
			if (enableLocalStorage) {
				LocalOrShared.put(p, "local");
				// log("set local: " + LocalOrShared.get(p));
				SelectModeOpen(p);
			} else {
				p.sendMessage(messageFeatureDisabled);
			}

		} else if (inv.equals(MainInvs.get(p)) && item.getType().equals(Material.ENDER_CHEST)) {
			if (enableSharedStorage && sharedStoragePath != null && sharedStoragePath.exists()
					&& sharedStoragePath.isDirectory()) {
				LocalOrShared.put(p, "shared");
				// log("set shared: " + LocalOrShared.get(p));
				SelectModeOpen(p);
			} else {
				p.sendMessage(messageFeatureDisabled);
			}
		} else if (inv.equals(SelectModeInvs.get(p)) && item.equals(goBackItem)) {
			invCommand(p);
		} else if (inv.equals(SelectModeInvs.get(p)) && item.getType().equals(Material.DIAMOND_SWORD)) {
			if (enableSingleItems) {
				if (LocalOrShared.containsKey(p) && LosPage.containsKey(p)
						&& !LocalOrShared.get(p).equals(LosPage.get(p))) {
					SingleItemsPage.put(p, 1);
				}
				SingleItemsOpen(p);
			} else {
				p.sendMessage(messageFeatureDisabled);
			}
		} else if (inv.equals(SelectModeInvs.get(p)) && item.getType().equals(Material.ARMOR_STAND)) {
			if (enableWardrobe) {
				if (LocalOrShared.containsKey(p) && LosPage.containsKey(p)
						&& !LocalOrShared.get(p).equals(LosPage.get(p))) {
					WardrobePage.put(p, 1);
				}
				WardrobeOpen(p);
			} else {
				p.sendMessage(messageFeatureDisabled);
			}
		} else if (inv.equals(SingleItemsInvs.get(p)) && e.getSlot() < 45 && item != null) {
			// log("edit0");
			ActionOnCurrentSingleItem.put(p, "edit");
			CurrentSingleItem.put(p, item);
			EditItemOpen(p);
		} else if (item != null) {
			if (inv.equals(SingleItemsInvs.get(p)) && item.equals(goBackItem)) {
				SelectModeOpen(p);
			} else if (inv.equals(SingleItemsInvs.get(p)) && item.getType().equals(Material.BEACON)) {
				CurrentSingleItem.put(p, null);
				ActionOnCurrentSingleItem.put(p, "add");
				AddItemOpen(p);
			} else if (inv.equals(SingleItemsInvs.get(p)) && item.getType().equals(Material.ARROW)) {
				if (item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.WHITE + "Next page")) {
					SingleItemsPage.put(p, SingleItemsPage.get(p) + 1);
				}
				if (item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.WHITE + "Previous page")) {
					SingleItemsPage.put(p, SingleItemsPage.get(p) - 1);
				}
				updateSingleItemsPage(p);
			} else if (inv.equals(AddItemInvs.get(p)) && item.equals(confirmItem)) {
				addSingleItem(p);
				SingleItemsOpen(p);
			} else if (inv.equals(AddItemInvs.get(p)) && item.equals(goBackItem)) {
				// log("go back items");
				CurrentSingleItem.put(p, null);
				SingleItemsOpen(p);
			}
			// else if(inv.equals(EditItemInvs.get(p)) && item.equals(confirmItem)) { //to
			// do
			// //addSingleItem(p);
			// //SingleItemsOpen(p);
			// }
			else if (inv.equals(EditItemInvs.get(p)) && item.equals(goBackItem)) {
				// log("go back items");
				CurrentSingleItem.put(p, null);
				SingleItemsOpen(p);
			} else if (inv.equals(EditItemInvs.get(p)) && item.equals(deleteItem)) {
				removeSingleItem(p);
				SingleItemsOpen(p);
			} else if (inv.equals(EditItemInvs.get(p)) && item.equals(takeSmthItem)) {
				takeSingleItem(p);
				p.closeInventory();
			} else if (inv.equals(WardrobeInvs.get(p)) && item.equals(createNewItem)) {
				AddGearOpen(p);
			}
		}
//		log("slot " + e.getSlot());
		//if(p.getOpenInventory().getTopInventory().getType().equals(InventoryType.CHEST)){
			
		//}
	}
	
	public void log(String s) {
		Bukkit.getConsoleSender().sendMessage(s);
	}
	
	public void log(int s) {
		log(s + "");
	}
	
	public void log(boolean s) {
		log(s + "");
	}
	
	public void log(double s) {
		log(s + "");
	}
	
	public void log(float s) {
		log(s + "");
	}
	
	public void log(char s) {
		log(s + "");
	}
	
	public void log(short s) {
		log(s + "");
	}
}