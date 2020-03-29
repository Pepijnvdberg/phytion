package me.PepijnvdBerg.kingdombuild.listeners;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.PepijnvdBerg.kingdombuild.Crate;
import me.PepijnvdBerg.kingdombuild.Main;
import me.PepijnvdBerg.kingdombuild.events.AttackMenu;

public class Interact_Listener
  implements Listener
{
  @SuppressWarnings({ "rawtypes" })
@EventHandler
  public void interact(PlayerInteractEvent e)
  {
    if (e.getPlayer().getItemInHand() != null) {
      if (e.getPlayer().getItemInHand().hasItemMeta())
      {
        for (Crate cr : Main.r.getAllCrates()) {
          if (cr.getTitle().equals(e.getPlayer().getItemInHand().getItemMeta().getDisplayName())) {
            cr.giveReward(e.getPlayer());
          }
        }
        Object m;
        if ((e.getPlayer().hasPermission("kingdom.staffmode")) && (e.getPlayer().getItemInHand().getItemMeta().getDisplayName() != null) && (e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.GRAY + "Vanish off")))
        {
          for (Player wp : Bukkit.getOnlinePlayers()) {
            if (!wp.hasPermission("kingdom.showstaff")) {
              wp.hidePlayer(e.getPlayer());
            }
          }
          Main.r.vanish.add(e.getPlayer());
          ItemStack i = new ItemStack(Material.INK_SACK, 1, (short)10);
          m = i.getItemMeta();
          ((ItemMeta)m).setDisplayName(ChatColor.GREEN + "Vanish on");
          i.setItemMeta((ItemMeta)m);
          e.getPlayer().setItemInHand(i);
          e.getPlayer().updateInventory();
          e.getPlayer().sendMessage(ChatColor.GRAY + "Niemand ziet jouw nu.");
          return;
        }
        if ((e.getPlayer().hasPermission("kingdom.staffmode")) && (e.getPlayer().getItemInHand().getItemMeta().getDisplayName() != null) && (e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Vanish on")))
        {
          for (m = Bukkit.getOnlinePlayers().iterator(); ((Iterator)m).hasNext();)
          {
            Player wp = (Player)((Iterator)m).next();
            
            wp.showPlayer(e.getPlayer());
          }
          Main.r.vanish.remove(e.getPlayer());
          ItemStack i = new ItemStack(Material.INK_SACK, 1, (short)8);
          ItemMeta m1 = i.getItemMeta();
          m1.setDisplayName(ChatColor.GRAY + "Vanish off");
          i.setItemMeta(m1);
          e.getPlayer().setItemInHand(i);
          e.getPlayer().updateInventory();
          e.getPlayer().sendMessage(ChatColor.GRAY + "Iedereen ziet jouw nu.");
          return;
        }
        if ((e.getPlayer().hasPermission("kingdom.staffmode")) && (e.getPlayer().getItemInHand().getItemMeta().getDisplayName() != null) && (e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.BLUE + "Random teleport"))) {
          if ((e.getAction() == Action.RIGHT_CLICK_AIR) || (e.getAction() == Action.RIGHT_CLICK_BLOCK))
          {
            e.setCancelled(true);
            Random ra = new Random();
            e.getPlayer().teleport((Player)Bukkit.getOnlinePlayers().toArray()[ra.nextInt(Bukkit.getOnlinePlayers().size())]);
          }
        }
        if ((e.getPlayer().hasPermission("kingdom.staffmode")) && (e.getPlayer().getItemInHand().getItemMeta().getDisplayName() != null) && (e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Fly speed")))
        {
          e.setCancelled(true);
          Main.r.openFlySpeedSelector(e.getPlayer());
        }
      }
    }
    ArrayList<Material> allowed = new ArrayList<Material>();
    for (String s : Main.r.getConfig().getStringList("use_materials")) {
      allowed.add(Material.valueOf(s.toUpperCase()));
    }
    if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
      if (allowed.contains(e.getClickedBlock().getType())) {
        if (Main.r.checkIfPlayerCanBuild(e.getPlayer(), 2, e.getClickedBlock()))
        {
          e.getPlayer().sendMessage(ChatColor.RED + "Je hebt te weinig influence hiervoor.");
          e.setCancelled(true);
        }
      }
    }
  }
  
  @EventHandler
  public void playerInteract(PlayerInteractEntityEvent e)
  {
    if ((e.getRightClicked() instanceof Player)) {
      if (e.getPlayer().getItemInHand() != null) {
        if (e.getPlayer().getItemInHand().hasItemMeta()) {
          if ((e.getPlayer().getItemInHand().getItemMeta().getDisplayName() != null) && (e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.BLUE + "Freeze"))) {
            if (Bukkit.getPlayer(((Player)e.getRightClicked()).getName()) != null)
            {
              Player wp = (Player)e.getRightClicked();
              e.getPlayer().performCommand("freeze " + wp.getName());
            }
          }
        }
      }
    }
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
@EventHandler
  public void inventory(InventoryClickEvent e)
  {
    Player p = (Player)e.getWhoClicked();
  
    if (e.getInventory().getTitle().equals("Start een oorlog"))
    {
      e.setCancelled(true);
      if (e.getInventory().getTitle().equals("Start een oorlog")) {
        if (e.getCurrentItem() != null) {
          if (e.getCurrentItem().hasItemMeta())
          {
            String inAttack;
            if (e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Start Attack"))
            {
              String ownKingdom = ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(p));
              
              ArrayList<Player> inATT = Main.r.attackInventoryPlayersIn.containsKey(p) ? (ArrayList)Main.r.attackInventoryPlayersIn.get(p) : new ArrayList();
              inATT.add(p);
              
              inAttack = (String)Main.r.attackInventory.get(p);
              p.closeInventory();
              
              DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
              Date date = new Date();
              String Date = dateFormat.format(date);
              
              int c = Main.r.c.contains(ownKingdom + "." + Date) ? Main.r.c.getInt(ownKingdom + "." + Date) : 2;
              c--;
              if (c > -1)
              {
                Main.r.c.set(ownKingdom + "." + Date, Integer.valueOf(c));
                Main.r.saveConfig();
                
                Main.r.attacks.put(ownKingdom, (String)Main.r.attackInventory.get(p));
                Main.r.inAttack.put(ownKingdom, inATT);
                Main.r.attackTime.put(ownKingdom, Integer.valueOf(2699));
                Main.r.attackInventory.remove(p);
                Main.r.attackInventoryPlayersIn.remove(p);
                for (Player wp : inATT) {
                  wp.sendMessage(ChatColor.GREEN + "Jullie zijn nu in attack met " + inAttack + ".");
                }
                
              }
              else
              {
                p.sendMessage(ChatColor.RED + "Jullie hebben het limiet van 2 attacks voor vandaag behaald.");
              }
              return;
            }
            int cKD = 0;
            for (Player wp : Bukkit.getOnlinePlayers())
            {
              String ownKingdom = ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(wp));
              if (ownKingdom.equals(Main.r.attackInventory.get(p))) {
                cKD++;
              }
            }
            System.out.println(cKD);
            
            String name = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
            if (Bukkit.getPlayer(name) != null)
            {
              Player wp = Bukkit.getPlayer(name);
              ArrayList<Player> inATT = Main.r.attackInventoryPlayersIn.containsKey(p) ? (ArrayList)Main.r.attackInventoryPlayersIn.get(p) : new ArrayList();
              if (cKD / 2 > inATT.size())
              {
                if (inATT.contains(wp)) {
                  inATT.remove(wp);
                } else {
                  inATT.add(wp);
                }
                Main.r.attackInventoryPlayersIn.put(p, inATT);
              }
              else
              {
                p.sendMessage(ChatColor.RED + "Je kan niet meer mensen toevoegen aan een attack.");
              }
            }
          }
        }
      }
      new AttackMenu().openMenu(p, (String)Main.r.attackInventory.get(p));
    }
  }
  
  @EventHandler
  public void pickup(PlayerPickupItemEvent e)
  {
    Player p = e.getPlayer();
    if (Main.r.staffModes.contains(p)) {
      e.setCancelled(true);
    }
  }
  
  @EventHandler
  public void drop(PlayerDropItemEvent e)
  {
    Player p = e.getPlayer();
    if (Main.r.staffModes.contains(p)) {
      e.setCancelled(true);
    }
  }
}
