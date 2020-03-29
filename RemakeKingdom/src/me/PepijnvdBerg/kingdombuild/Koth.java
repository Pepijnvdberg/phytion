package me.PepijnvdBerg.kingdombuild;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import java.util.ArrayList;
import java.util.Date;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Koth
{
  private String name;
  private Player capper;
  private int timer;
  private Date startTime;
  
  public Koth() {}
  
  public Koth(String n)
  {
    this.name = n;
    this.timer = Integer.parseInt(Main.r.c.getString("koth." + this.name + ".time"));
    this.startTime = null;
    Main.r.Koths.put(this.name, this);
  }
  
  public void claim(Player p)
  {
    this.startTime = new Date();
    this.capper = p;
    Main.r.Koths.put(this.name, this);
    p.sendMessage(ChatColor.GREEN + "Blijf in de koth, je bent hem aan het cappen.");
  }
  
  public void reset()
  {
    this.capper.sendMessage(ChatColor.RED + "Je bent gestopt met het cappen van " + this.name + ".");
    this.capper = null;
    this.startTime = null;
    Main.r.Koths.put(this.name, this);
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public Player getCapper()
  {
    return this.capper;
  }
  
  public  int getTimer()
  {
    int timePassed = this.startTime != null ? (int)((new Date().getTime() - this.startTime.getTime()) / 1000L) : 0;
    if (this.timer - timePassed > 0) {
      return this.timer - timePassed;
    }
    ItemStack reward = new ItemStack(Material.getMaterial(Main.r.c.getString("kothreward.item").toUpperCase()), Main.r.c.getInt("kothreward.amount"), (byte)Main.r.c.getInt("kothreward.itemData"));
    ItemMeta m = reward.getItemMeta();
    m.setDisplayName(ChatColor.translateAlternateColorCodes('&', Main.r.c.getString("kothreward.name")));
    ArrayList<String> lore = new ArrayList<String>();
    for (String str : Main.r.c.getStringList("kothreward.lore")) {
      lore.add(ChatColor.translateAlternateColorCodes('&', str));
    }
    m.setLore(lore);
    reward.setItemMeta(m);
    Main.r.getServer().getScheduler().runTaskLater(Main.r, new Runnable()
    {
      public void run()
      {
        Main.r.Koths.remove(Koth.this.name);
      }
    }, 1L);
    
    this.capper.getInventory().addItem(new ItemStack[] { reward });
    return 0;
  }
  
  public boolean isInKoth(Player p)
  {
    World w = Bukkit.getWorld(Main.r.c.getString("koth." + this.name + ".world"));
    Location locA = new Location(w, Main.r.c.getInt("koth." + this.name + ".x1"), Main.r.c.getInt("koth." + this.name + ".y1"), Main.r.c.getInt("koth." + this.name + ".z1"));
    Location locB = new Location(w, Main.r.c.getInt("koth." + this.name + ".x2"), Main.r.c.getInt("koth." + this.name + ".y2"), Main.r.c.getInt("koth." + this.name + ".z2"));
    CuboidSelection s = new CuboidSelection(w, locA, locB);
    
    Vector min = s.getNativeMinimumPoint();
    Vector max = s.getNativeMaximumPoint();
    if (p.getWorld() == w) {
      if ((min.getBlockX() <= p.getLocation().getBlockX()) && (max.getBlockX() >= p.getLocation().getBlockX())) {
        if ((min.getBlockY() <= p.getLocation().getBlockY()) && (max.getBlockY() >= p.getLocation().getBlockY())) {
          if ((min.getBlockZ() <= p.getLocation().getBlockZ()) && (max.getBlockZ() >= p.getLocation().getBlockZ())) {
            return true;
          }
        }
      }
    }
    return false;
  }
}
