package me.PepijnvdBerg.kingdombuild.events;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import me.PepijnvdBerg.kingdombuild.Main;

public class AttackMenu
{
  @SuppressWarnings("unchecked")
public void openMenu(Player p, String kingdom)
  {
    @SuppressWarnings("rawtypes")
	ArrayList<Player> inINV = Main.r.attackInventoryPlayersIn.containsKey(p) ? (ArrayList)Main.r.attackInventoryPlayersIn.get(p) : new ArrayList();
    
    Inventory inv = Bukkit.createInventory(p, 54, "Start een oorlog");
    
    String ownKingdom = ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(p));
    for (Player wp : Bukkit.getOnlinePlayers()) {
      if (wp != p)
      {
        String KD = ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(wp));
        if (KD.equals(ownKingdom))
        {
          boolean in = inINV.contains(wp);
          
          ItemStack i = new ItemStack(Material.SKULL_ITEM, 0, in ? 1 : (short)3);
          SkullMeta m = (SkullMeta)i.getItemMeta();
          m.setOwner(wp.getName());
          m.setDisplayName((in ? ChatColor.GREEN : ChatColor.RED) + wp.getName());
          i.setItemMeta(m);
          inv.addItem(new ItemStack[] { i });
        }
      }
    }
    ItemStack i = new ItemStack(Material.SUGAR_CANE, 1);
    ItemMeta m = i.getItemMeta();
    m.setDisplayName(ChatColor.GREEN + "Start Attack");
    i.setItemMeta(m);
    inv.setItem(53, i);
    
    p.openInventory(inv);
  }
  
  public void check(Player p)
  {
    check(p, p.getLocation());
  }
  
  private int stop = 0;
  
  @SuppressWarnings("rawtypes")
public void check(final Player p, Location loc)
  {
    String ownKingdom = ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(p));
    if (Main.r.attacks.containsKey(ownKingdom))
    {
      String kingdomInWar = (String)Main.r.attacks.get(ownKingdom);
      
      int in = 0;
      for (Player wp : Bukkit.getOnlinePlayers()) {
        if (((ArrayList)Main.r.inAttack.get(ownKingdom)).contains(wp))
        {
          String inWalk = ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(wp == p ? loc.getBlock() : wp.getLocation().getBlock()));
          if (inWalk.equals(kingdomInWar)) {
            in++;
          }
        }
      }
      if (in == 0)
      {
        p.sendMessage(ChatColor.RED + "Ga terug op het gebied anders verloopt de attack!");
        this.stop = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.r, new Runnable()
        {
          int count = 200;
          
          public void run()
          {
            String ownKingdom = ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(p));
            if (Main.r.attacks.containsKey(ownKingdom))
            {
              String kingdomInWar = (String)Main.r.attacks.get(ownKingdom);
              
              int in = 0;
              for (Player wp : Bukkit.getOnlinePlayers()) {
                if (((ArrayList)Main.r.inAttack.get(ownKingdom)).contains(wp))
                {
                  String inWalk = ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(wp == p ? p.getLocation().getBlock() : wp.getLocation().getBlock()));
                  if (inWalk.equals(kingdomInWar)) {
                    in++;
                  }
                }
              }
              if ((this.count == 160) || (this.count == 120) || (this.count == 80) || (this.count == 40)) {
                p.sendMessage(ChatColor.RED + "Ga terug op het gebied anders verloopt de attack!");
              }
              if (in != 0) {
                Bukkit.getScheduler().cancelTask(AttackMenu.this.stop);
              }
            }
            if (this.count == 0) {
              AttackMenu.this.end(ownKingdom);
            }
            this.count -= 1;
          }
        }, 0L, 1L);
      }
    }
  }
  
  public void end(String keys)
  {
    System.out.println(keys + " attack ends.");
    String kingdomInWar = (String)Main.r.attacks.get(keys);
    for (Player wp : Bukkit.getOnlinePlayers())
    {
      String inKD = ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(wp));
      if (inKD.equals(keys)) {
        wp.sendMessage(ChatColor.RED + "De attack tegen " + kingdomInWar + " is afgelopen.");
      }
    }
    Main.r.attackTime.remove(keys);
    Main.r.inAttack.remove(keys);
    Main.r.attacks.remove(keys);

  }
}
