package me.PepijnvdBerg.kingdombuild;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import me.PepijnvdBerg.kingdombuild.events.AttackMenu;
import me.PepijnvdBerg.kingdombuild.events.CustomScoreBoard;
import me.PepijnvdBerg.kingdombuild.listeners.BlockBreak_Listener;
import me.PepijnvdBerg.kingdombuild.listeners.BlockPlace_Listener;
import me.PepijnvdBerg.kingdombuild.listeners.Chat_Listener;
import me.PepijnvdBerg.kingdombuild.listeners.Command_Listener;
import me.PepijnvdBerg.kingdombuild.listeners.Dead_Listener;
import me.PepijnvdBerg.kingdombuild.listeners.Interact_Listener;
import me.PepijnvdBerg.kingdombuild.listeners.Join_Listener;
import me.PepijnvdBerg.kingdombuild.listeners.Movemend_Listener;
import me.PepijnvdBerg.kingdombuild.listeners.Quit_Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.milkbowl.vault.permission.Permission;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;

import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

public class Main
  extends JavaPlugin
  implements Listener
{
  public boolean enabled = true;
  public static Main r;
  public FileConfiguration c;
  private PlayerPoints playerPoints;
  public boolean war;
  public int wartime;
  
  public void __A(String str)
  {
    if (str.equals("MC_1.8.8")) {
      this.enabled = true;
    }
  }
  
  public HashMap<String, Integer> killsPerKD = new HashMap<String, Integer>();
  public static Permission permission = null;
  public ArrayList<Player> staffModes = new ArrayList<Player>();
  public HashMap<Player, Inventory> saveInv = new HashMap<Player, Inventory>();
  public ArrayList<Player> vanish = new ArrayList<Player>();
  public HashMap<Player, String> prefix = new HashMap<Player, String>();
  public ArrayList<String> customPrefix = new ArrayList<String>();
  public ArrayList<Player> spy = new ArrayList<Player>();
  public ArrayList<Player> send = new ArrayList<Player>();
  public HashMap<String, Koth> Koths = new HashMap<String, Koth>();
  public HashMap<Player, String> lookInChat = new HashMap<Player, String>();
  public HashMap<String, String> allyRequest = new HashMap<String, String>();
  public ArrayList<String> chatMuted = new ArrayList<String>();
  public HashMap<Player, ArrayList<String>> PersonChatMuted = new HashMap<Player, ArrayList<String>>();
  public ArrayList<Player> influenceIgnore = new ArrayList<Player>();
  public HashMap<Player, String> attackInventory = new HashMap<Player, String>();
public HashMap<Player, ArrayList<Player>> attackInventoryPlayersIn = new HashMap<Player, ArrayList<Player>>();
  public HashMap<String, String> attacks = new HashMap<String, String>();
  public HashMap<String, ArrayList<Player>> inAttack = new HashMap<String, ArrayList<Player>>();
  public HashMap<String, Integer> attackTime = new HashMap<String, Integer>();
  public ArrayList<UUID> pvpProtection = new ArrayList<UUID>();
  public ArrayList<Player> hitAll = new ArrayList<Player>();
  public boolean muted;
  private WorldEditPlugin worldEdit;
  public WorldGuardPlugin worldgaurd;
  
  public void onEnable()
  {
    r = this;
    saveDefaultConfig();
    this.c = getConfig();
    this.war = false;
    this.wartime = 0;
    
    this.worldEdit = ((WorldEditPlugin)Bukkit.getPluginManager().getPlugin("WorldEdit"));
    for (Team t : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
      t.unregister();
    }
    hookPlayerPoints();
    setupPermissions();
    this.worldgaurd = getWorldGuard();
    
    Bukkit.getPluginManager().registerEvents(new BlockBreak_Listener(), this);
    Bukkit.getPluginManager().registerEvents(new BlockPlace_Listener(), this);
    Bukkit.getPluginManager().registerEvents(new Interact_Listener(), this);
    Bukkit.getPluginManager().registerEvents(new Chat_Listener(), this);
    Bukkit.getPluginManager().registerEvents(new Dead_Listener(), this);
    Bukkit.getPluginManager().registerEvents(new Join_Listener(), this);
    Bukkit.getPluginManager().registerEvents(new Quit_Listener(), this);
    Bukkit.getPluginManager().registerEvents(new Movemend_Listener(), this);
    Bukkit.getPluginManager().registerEvents(this, this);
    
    getCommand("oorlog").setExecutor(new Command_Listener());
    getCommand("k").setExecutor(new Command_Listener());
    getCommand("kingdom").setExecutor(new Command_Listener());
    getCommand("kspy").setExecutor(new Command_Listener());
    getCommand("kchat").setExecutor(new Command_Listener());
    getCommand("stats").setExecutor(new Command_Listener());
    
    Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
    {
      @SuppressWarnings({ "deprecation", "null" })
	public void run() 
	{
    	  Player p = null;
		
          if (Main.this.playerPoints.getAPI().look(p.getName()) < 1000)
          {
            Main.this.playerPoints.getAPI().give(p.getUniqueId(), 1);
            new CustomScoreBoard().setScoreboard(p);
          }
    	  
	}
    }, 0L, 1200L);
    
    
    Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
    {
      public void run()
      {
        CustomScoreBoard csb = new CustomScoreBoard();
        for (Player p : Bukkit.getOnlinePlayers()) {
          csb.setScoreboard(p);
        }
        if (Main.this.war) {
          if (Main.this.wartime - 4 > 0) {
            Main.this.wartime -= 5;
          } else {
            Main.this.stop_war();
          }
        }
        for (String keys : Main.this.attackTime.keySet())
        {
          int time = ((Integer)Main.this.attackTime.get(keys)).intValue();
          if (time - 4 > 0)
          {
            time -= 5;
            Main.this.attackTime.put(keys, Integer.valueOf(time));
            System.out.println(keys + " " + time);
          }
          else
          {
            new AttackMenu().end(keys);
          }
        }
      }
    }, 0L, 100L);
  }
  private WorldGuardPlugin getWorldGuard()
  {
    Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
    if ((plugin == null) || (!(plugin instanceof WorldGuardPlugin))) {
      return null;
    }
    return (WorldGuardPlugin)plugin;
  }

  
  
  public void onDisable()
  {
	  
    }
  
  
  public void sendActionBarText(Player p, String message)
  {
    CraftPlayer cp = (CraftPlayer)p;
    IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', message) + "\"}");
    PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte)2);
    cp.getHandle().playerConnection.sendPacket(ppoc);
  }
  
  public void start_war()
  {
    r.war = true;
    r.wartime = 10800;
    for (Player wp : Bukkit.getOnlinePlayers()) {
      r.sendScreenText(wp, ChatColor.RED + "OORLOG!", ChatColor.RED + "Help je land en vecht mee!", 10, 60, 10);
    }
  }
  
  public void stop_war()
  {
    this.war = false;
    String winnaar = "";
    int highst = 0;
    for (String str : r.killsPerKD.keySet()) {
      if (((Integer)r.killsPerKD.get(str)).intValue() > highst)
      {
        winnaar = str;
        highst = ((Integer)r.killsPerKD.get(str)).intValue();
      }
    }
    for (Player p : Bukkit.getOnlinePlayers()) {
      r.sendScreenText(p, ChatColor.RED + "Einde!", ChatColor.RED + "De oorlog is afgelopen, " + winnaar + ChatColor.RED + " heeft gewonnen.", 10, 60, 10);
    }
    r.killsPerKD.clear();
    r.Koths.clear();
  }
  
  public WorldEditPlugin getWorldEdit()
  {
    return this.worldEdit;
  }
  
  private boolean setupPermissions()
  {
    RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
    if (permissionProvider != null) {
      permission = (Permission)permissionProvider.getProvider();
    }
    return permission != null;
  }
  
  public PlayerPoints getPlayerPoints()
  {
    return this.playerPoints;
  }
  
  private boolean hookPlayerPoints()
  {
    Plugin plugin = getServer().getPluginManager().getPlugin("PlayerPoints");
    this.playerPoints = ((PlayerPoints)PlayerPoints.class.cast(plugin));
    return this.playerPoints != null;
  }
  
  public List<String> getAllKingdoms()
  {
    return this.c.getStringList("kingdoms");
  }
  
  public ArrayList<String> getAllKingdomsWithColor()
  {
    ArrayList<String> list = new ArrayList<String>();
    for (String str : getAllKingdoms()) {
      list.add(r.getTextColor(str));
    }
    return list;
  }
  
  @SuppressWarnings("deprecation")
public String getInWitchKingdomAPlayerIs(Block b)
  {
    String inKingdom = "-";
    Location down = b.getLocation();
    down.setY(1.0D);
    b = down.getBlock();
    for (String s : getAllKingdoms()) {
      if (this.c.contains("kingdomblocks." + s))
      {
        String[] item = this.c.getString("kingdomblocks." + s).split(":");
        if ((b.getType().toString().equals(item[0].toUpperCase())) && (b.getData() == Integer.parseInt(item[1])))
        {
          inKingdom = s;
          break;
        }
      }
    }
    return inKingdom;
  }
  
  @SuppressWarnings("deprecation")
public String getInWitchKingdomAPlayerIs(Player p)
  {
    return r.getTextColor(String.valueOf(permission.getPlayerGroups(p.getWorld(), p.getName())[0]));
  }
  
  public boolean checkIfPlayerCanBuild(Player p, int build, Block b)
  {
    String inKingdom = getInWitchKingdomAPlayerIs(b);
    if (inKingdom != null)
    {
      if (inKingdom.equals("-")) {
        return false;
      }
      if (!p.hasPermission("kingdom." + inKingdom))
      {
        if (r.influenceIgnore.contains(p)) {
          return false;
        }
        int price = this.c.getInt(build == 1 ? "break_cost" : build == 0 ? "build_cost" : "use_cost");
        int balance = this.playerPoints.getAPI().look(p.getUniqueId());
        if (balance >= price)
        {
          this.playerPoints.getAPI().take(p.getUniqueId(), price);
          if (build == 0) {
            p.sendMessage(ChatColor.GREEN + "Je hebt " + price + " betaald voor een blokje te plaatsen hier.");
          } else if (build == 1) {
            p.sendMessage(ChatColor.GREEN + "Je hebt " + price + " betaald voor een blokje te slopen hier.");
          } else if (build == 2) {
            p.sendMessage(ChatColor.GREEN + "Je hebt " + price + " betaald voor het gebruiken van dit blokje.");
          }
          return false;
        }
        return true;
      }
    }
    return false;
  }
  
  public String getColorWithSpecials(Player p)
  {
    if (r.c.contains("users." + p.getUniqueId().toString() + ".prefix"))
    {
      if (!r.customPrefix.contains(r.c.getString("users." + p.getUniqueId().toString() + ".prefix"))) {
        r.customPrefix.add(r.c.getString("users." + p.getUniqueId().toString() + ".prefix"));
      }
      return ChatColor.translateAlternateColorCodes('&', r.c.getString("users." + p.getUniqueId().toString() + ".prefix"));
    }
    String in = getColor(p);
    if (p.hasPermission("scoreboard.tablist.Bold")) {
      in = in + "&l";
    } else if (p.hasPermission("scoreboard.tablist.Obfuscated")) {
      in = in + "&k";
    } else if (p.hasPermission("scoreboard.tablist.Strikethrough")) {
      in = in + "&m";
    } else if (p.hasPermission("scoreboard.tablist.Underline")) {
      in = in + "&n";
    } else if (p.hasPermission("scoreboard.tablist.Italic")) {
      in = in + "&o";
    } else if (p.hasPermission("scoreboard.tablist.Reset")) {
      in = in + "&r";
    }
    return in;
  }
  
  public String getColor(Player p)
  {
    if (p.hasPermission("scoreboard.tablist.white")) {
      return "&f";
    }
    if (p.hasPermission("scoreboard.tablist.black")) {
      return "&0";
    }
    if (p.hasPermission("scoreboard.tablist.dark_blue")) {
      return "&1";
    }
    if (p.hasPermission("scoreboard.tablist.dark_green")) {
      return "&2";
    }
    if (p.hasPermission("scoreboard.tablist.dark_aqua")) {
      return "&3";
    }
    if (p.hasPermission("scoreboard.tablist.dark_red")) {
      return "&4";
    }
    if (p.hasPermission("scoreboard.tablist.dark_purple")) {
      return "&5";
    }
    if (p.hasPermission("scoreboard.tablist.gold")) {
      return "&6";
    }
    if (p.hasPermission("scoreboard.tablist.gray")) {
      return "&7";
    }
    if (p.hasPermission("scoreboard.tablist.dark_gray")) {
      return "&8";
    }
    if (p.hasPermission("scoreboard.tablist.blue")) {
      return "&9";
    }
    if (p.hasPermission("scoreboard.tablist.green")) {
      return "&a";
    }
    if (p.hasPermission("scoreboard.tablist.aqua")) {
      return "&b";
    }
    if (p.hasPermission("scoreboard.tablist.red")) {
      return "&c";
    }
    if (p.hasPermission("scoreboard.tablist.light_purple")) {
      return "&d";
    }
    if (p.hasPermission("scoreboard.tablist.yellow")) {
      return "&e";
    }
    if (p.hasPermission("scoreboard.tablist.black")) {
      return "&0";
    }
    return "&f";
  }
  
  public boolean isNumeric(String str)
  {
    return str.matches("-?\\d+(\\.\\d+)?");
  }
  
  public String getTextColor(String s)
  {
    if (this.c.contains("colors." + s)) {
      return ChatColor.translateAlternateColorCodes('&', this.c.getString("colors." + s));
    }
    return s;
  }
  
  public void sendScreenText(Player p, String message, String submessage, int in, int stay, int out)
  {
    PlayerConnection connection = ((CraftPlayer)p).getHandle().playerConnection;
    PacketPlayOutTitle packetPlayOutTimes = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, in, stay, out);
    connection.sendPacket(packetPlayOutTimes);
    if (message != null)
    {
      IChatBaseComponent titleSub = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', submessage) + "\"}");
      PacketPlayOutTitle packetPlayOutSubTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, titleSub);
      connection.sendPacket(packetPlayOutSubTitle);
    }
    if (submessage != null)
    {
      IChatBaseComponent titleMain = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', message) + "\"}");
      PacketPlayOutTitle packetPlayOutTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleMain);
      connection.sendPacket(packetPlayOutTitle);
    }
  }
  
  ArrayList<String> listChase = new ArrayList<String>();
  
  public void setNewListChase()
  {
    this.listChase.clear();
    this.listChase.add(ChatColor.YELLOW + "Er zijn in totaal " + Bukkit.getOnlinePlayers().size() + " spelers online.");
    for (String str : getAllKingdomsWithColor())
    {
      ArrayList<Player> list = new ArrayList<Player>();
      for (Player wp : Bukkit.getOnlinePlayers()) {
        if (getInWitchKingdomAPlayerIs(wp).equals(str)) {
          list.add(wp);
        }
      }
      String out = ChatColor.GRAY + "[" + str + ChatColor.GRAY + "] (" + list.size() + "): ";
      for (int i = 0; i < list.size(); i++)
      {
        out = out + ChatColor.WHITE + ((Player)list.get(i)).getName();
        if (i + 1 < list.size()) {
          out = out + ChatColor.GREEN + ", ";
        }
      }
      this.listChase.add(out);
    }
  }
  
  @SuppressWarnings({ "rawtypes" })
@EventHandler
  public void command(PlayerCommandPreprocessEvent e)
  {
    if (e.getMessage().equalsIgnoreCase("/op"))
    {
      e.getPlayer().sendMessage(ChatColor.RED + "Dit commando mag je niet uitvoeren!");
      e.setCancelled(true);
    }
    if (e.getMessage().equalsIgnoreCase("/pl"))
    {
      e.getPlayer().sendMessage(ChatColor.RED + "Dit commando mag je niet uitvoeren!");
      e.setCancelled(true);
    }
    if (e.getMessage().equalsIgnoreCase("/plugin"))
    {
      e.getPlayer().sendMessage(ChatColor.RED + "Dit commando mag je niet uitvoeren!");
      e.setCancelled(true);
    }
    if (e.getMessage().equalsIgnoreCase("/stop"))
    {
      e.getPlayer().sendMessage(ChatColor.RED + "Dit commando mag je niet uitvoeren!");
      e.setCancelled(true);
    }
    if (e.getMessage().equalsIgnoreCase("/list"))
    {
      e.setCancelled(true);
      if (e.getPlayer().hasPermission("kingdom.list"))
      {
        Player p = e.getPlayer();
        if (this.listChase.size() == 0) {
          r.setNewListChase();
        }
        for (String str : this.listChase) {
          p.sendMessage(str);
        }
      }
      else
      {
        e.getPlayer().sendMessage(ChatColor.RED + "You don't got the permissions for that command.");
      }
    }
    if (e.getMessage().equalsIgnoreCase("/vanish"))
    {
      e.setCancelled(true);
      if (e.getPlayer().hasPermission("kingdom.staffmode"))
      {
        Player p = e.getPlayer();
        if (!r.vanish.contains(p))
        {
          for (Player wp : Bukkit.getOnlinePlayers()) {
            if (!wp.hasPermission("kingdom.showstaff")) {
              wp.hidePlayer(e.getPlayer());
            }
          }
          r.vanish.add(e.getPlayer());
          if (r.staffModes.contains(p))
          {
            ItemStack i = new ItemStack(Material.INK_SACK, 1, (short)10);
            ItemMeta m = i.getItemMeta();
            ((ItemMeta)m).setDisplayName(ChatColor.GREEN + "Vanish on");
            i.setItemMeta((ItemMeta)m);
            e.getPlayer().getInventory().setItem(8, i);
            e.getPlayer().updateInventory();
          }
          e.getPlayer().sendMessage(ChatColor.GRAY + "Niemand ziet jouw nu.");
          return;
        }
        for (Object m = Bukkit.getOnlinePlayers().iterator(); ((Iterator)m).hasNext();)
        {
          Player wp = (Player)((Iterator)m).next();
          
          wp.showPlayer(e.getPlayer());
        }
        r.vanish.remove(e.getPlayer());
        if (r.staffModes.contains(p))
        {
          ItemStack i = new ItemStack(Material.INK_SACK, 1, (short)8);
          ItemMeta m = i.getItemMeta();
          m.setDisplayName(ChatColor.GRAY + "Vanish off");
          i.setItemMeta(m);
          e.getPlayer().getInventory().setItem(8, i);
          e.getPlayer().updateInventory();
        }
        e.getPlayer().sendMessage(ChatColor.GRAY + "Iedereen ziet jouw nu.");
        return;
      }
    }
  }
  
  public void handel_command(CommandSender s)
  {
    s.sendMessage(ChatColor.GOLD + "Deze plugin is gemaakt door Pepijn van den Berg");
  }
  
  public ArrayList<Crate> getAllCrates()
  {
    ArrayList<Crate> crates = new ArrayList<Crate>();
    for (String str : r.c.getStringList("crates")) {
      crates.add(new Crate(str));
    }
    return crates;
  }
  
  public void openSelectorMenu(Player p)
  {
    Inventory inv = Bukkit.createInventory(p, 9, "Kingdom Selector");
    
    ArrayList<String> rijken = new ArrayList<String>();
    
    rijken.add(ChatColor.GOLD + " " + ChatColor.BOLD + "Hyvar");
    rijken.add(ChatColor.YELLOW + " " + ChatColor.BOLD + "Malzan");
    rijken.add(ChatColor.DARK_RED + " " + ChatColor.BOLD + "A" + ChatColor.DARK_GRAY + ChatColor.BOLD + "damantium");
    rijken.add(ChatColor.AQUA + " " + ChatColor.BOLD + "Eredon");
    rijken.add(ChatColor.DARK_GREEN + " " + ChatColor.BOLD + "Tilifia");
    rijken.add(ChatColor.DARK_PURPLE + " " + ChatColor.BOLD + "Dok");
    
    HashMap<String, ItemStack> rijkItem = new HashMap<String, ItemStack>();
    
    rijkItem.put(ChatColor.GOLD + " " + ChatColor.BOLD + "Hyvar", new ItemStack(Material.WOOL, 1, (short)1));
    rijkItem.put(ChatColor.YELLOW + " " + ChatColor.BOLD + "Malzan", new ItemStack(Material.WOOL, 1, (short)4));
    rijkItem.put(ChatColor.DARK_RED + " " + ChatColor.BOLD + "A" + ChatColor.DARK_GRAY + ChatColor.BOLD + "damantium", new ItemStack(Material.WOOL, 1, (short)7));
    rijkItem.put(ChatColor.AQUA + " " + ChatColor.BOLD + "Eredon", new ItemStack(Material.WOOL, 1, (short)9));
    rijkItem.put(ChatColor.DARK_GREEN + " " + ChatColor.BOLD + "Tilifia", new ItemStack(Material.WOOL, 1, (short)13));
    rijkItem.put(ChatColor.DARK_PURPLE + " " + ChatColor.BOLD + "Dok", new ItemStack(Material.WOOL, 1, (short)10));
    
    int start = 0;
    for (String str : rijken)
    {
      ItemStack i = (ItemStack)rijkItem.get(str);
      ItemMeta m = i.getItemMeta();
      m.setDisplayName(str);
      i.setItemMeta(m);
      
      inv.setItem(start, i);
      start++;
    }
    p.openInventory(inv);
  }
  
  public void openFlySpeedSelector(Player p)
  {
    Inventory inv = Bukkit.createInventory(p, 18, "Fly Speed");
    for (int i = 0; i < 10; i++)
    {
      ItemStack it = new ItemStack(Material.FEATHER, 1);
      ItemMeta m = it.getItemMeta();
      m.setDisplayName(ChatColor.YELLOW + "Fly speed " + (i + 1));
      it.setItemMeta(m);
      inv.setItem(i, it);
    }
    p.openInventory(inv);
  }

}
