package me.PepijnvdBerg.kingdombuild.listeners;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.PepijnvdBerg.kingdombuild.Main;
import me.PepijnvdBerg.kingdombuild.events.AttackMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class Dead_Listener
  implements Listener
{
  private HashMap<Player, Player> lastDamage = new HashMap<Player, Player>();
  
  @EventHandler
  public void dead(EntityDamageEvent e)
  {
    if ((e.getEntity() instanceof Player))
    {
      Player p = (Player)e.getEntity();
      String pKD = ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(p));
      if (pKD.equals("default"))
      {
        e.setCancelled(true);
        return;
      }
    }
  }
  
  @SuppressWarnings("rawtypes")
@EventHandler
  public void dead(EntityDamageByEntityEvent e)
  {
    Iterator localIterator2;
    Object ally;
    if (((e.getDamager() instanceof Player)) && ((e.getEntity() instanceof Player)))
    {
      Player p = (Player)e.getEntity();
      Player damager = (Player)e.getDamager();
      if ((!Main.r.war) && (!Main.r.hitAll.contains(damager)))
      {
        String pKD = ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(p));
        String dKD = ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(damager));
        if ((pKD.equals("default")) || (dKD.equals("default")))
        {
          e.setCancelled(true);
          return;
        }
        if (Main.r.c.getStringList("allies." + dKD).contains(pKD))
        {
          List<String> not = Main.r.c.getStringList("no_ally_regions");
          boolean allow = true;
          for (ProtectedRegion r : WGBukkit.getRegionManager(p.getWorld()).getApplicableRegions(p.getLocation())) {
            if (not.contains(r.getId()))
            {
              allow = false;
              break;
            }
          }
          if (allow)
          {
            damager.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.r.c.getString("cant_hit_because_ally")
              .replace("%player%", p.getName())));
            e.setCancelled(true);
            return;
          }
        }
        String walkInKD = Main.r.getInWitchKingdomAPlayerIs(damager.getLocation().getBlock());
        if ((!walkInKD.equals("-")) && (!walkInKD.equals(dKD)))
        {
          List<String> dAllies = Main.r.c.getStringList("allies." + dKD);
          if ((!walkInKD.equals(pKD)) || (!Main.r.attacks.containsKey(dKD)) || (!((String)Main.r.attacks.get(dKD)).equals(pKD)))
          {
            boolean check1 = false;
            for (localIterator2 = dAllies.iterator(); localIterator2.hasNext();)
            {
              ally = (String)localIterator2.next();
              if (Main.r.attacks.containsKey(ally)) {
                if (((String)Main.r.attacks.get(ally)).equals(pKD))
                {
                  check1 = true;
                  break;
                }
              }
            }
            if ((!walkInKD.equals(pKD)) || (!check1))
            {
              e.setCancelled(true);
              damager.sendMessage(ChatColor.RED + "Je kan " + p.getName() + " niet slaan op dit gebied.");
              return;
            }
          }
        }
      }
      if (Main.r.pvpProtection.contains(p.getUniqueId()))
      {
        e.setCancelled(true);
        damager.sendMessage(ChatColor.RED + "Je kan " + p.getName() + " niet slaan omdat hij pvp protectie aan heeft staan.");
        return;
      }
      if (!Main.r.getInWitchKingdomAPlayerIs(p).equals(Main.r.getInWitchKingdomAPlayerIs(damager))) {
        this.lastDamage.put(p, damager);
      } else if (Main.r.c.getStringList("friendly-fire").contains(ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(damager)))) {
        this.lastDamage.put(p, damager);
      } else {
        e.setCancelled(true);
      }
    }
    Object ally1;
    if (((e.getEntity() instanceof Player)) && ((e.getDamager() instanceof Arrow)) && ((((Arrow)e.getDamager()).getShooter() instanceof Player)))
    {
      Player damager = (Player)((Arrow)e.getDamager()).getShooter();
      Player p = (Player)e.getEntity();
      if ((!Main.r.war) && (!Main.r.hitAll.contains(damager)))
      {
        String pKD = ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(p));
        String dKD = ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(damager));
        if ((pKD.equals("default")) || (dKD.equals("default")))
        {
          e.setCancelled(true);
          return;
        }
        if (Main.r.c.getStringList("allies." + dKD).contains(pKD))
        {
          List<String> not = Main.r.c.getStringList("no_ally_regions");
          boolean allow = true;
          for (ally1 = WGBukkit.getRegionManager(p.getWorld()).getApplicableRegions(p.getLocation()).iterator(); ((Iterator)ally1).hasNext();)
          {
            ProtectedRegion r = (ProtectedRegion)((Iterator)ally1).next();
            if (not.contains(r.getId()))
            {
              allow = false;
              break;
            }
          }
          if (allow)
          {
            damager.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.r.c.getString("cant_hit_because_ally")
              .replace("%player%", p.getName())));
            e.setCancelled(true);
            return;
          }
        }
        String walkInKD = Main.r.getInWitchKingdomAPlayerIs(damager.getLocation().getBlock());
        if ((!walkInKD.equals("-")) && (!walkInKD.equals(dKD)))
        {
          List<String> dAllies = Main.r.c.getStringList("allies." + dKD);
          if ((!walkInKD.equals(pKD)) || (!Main.r.attacks.containsKey(dKD)) || (!((String)Main.r.attacks.get(dKD)).equals(pKD)))
          {
            boolean check1 = false;
            for (localIterator2 = dAllies.iterator(); localIterator2.hasNext();)
            {
              ally1 = (String)localIterator2.next();
              if (Main.r.attacks.containsKey(ally1)) {
                if (((String)Main.r.attacks.get(ally1)).equals(pKD))
                {
                  check1 = true;
                  break;
                }
              }
            }
            if ((!walkInKD.equals(pKD)) || (!check1))
            {
              e.setCancelled(true);
              damager.sendMessage(ChatColor.RED + "Je kan " + p.getName() + " niet neerschieten op dit gebied.");
              return;
            }
          }
        }
      }
      if (Main.r.pvpProtection.contains(p.getUniqueId()))
      {
        e.setCancelled(true);
        damager.sendMessage(ChatColor.RED + "Je kan " + p.getName() + " niet slaan omdat hij pvp protectie aan heeft staan.");
        return;
      }
      if (!Main.r.getInWitchKingdomAPlayerIs(p).equals(Main.r.getInWitchKingdomAPlayerIs(damager))) {
        this.lastDamage.put(p, damager);
      } else if (Main.r.c.getStringList("friendly-fire").contains(ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(damager)))) {
        this.lastDamage.put(p, damager);
      } else {
        e.setCancelled(true);
      }
    }
    if (((e.getEntity() instanceof Player)) && ((e.getDamager() instanceof FishHook)))
    {
      Player damager = (Player)((FishHook)e.getDamager()).getShooter();
      Player p = (Player)e.getEntity();
      if ((!Main.r.war) && (!Main.r.hitAll.contains(damager)))
      {
        String pKD = ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(p));
        String dKD = ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(damager));
        if ((pKD.equals("default")) || (dKD.equals("default")))
        {
          e.setCancelled(true);
          return;
        }
        if (Main.r.c.getStringList("allies." + dKD).contains(pKD))
        {
          List<String> not = Main.r.c.getStringList("no_ally_regions");
          boolean allow = true;
          for (ally1 = WGBukkit.getRegionManager(p.getWorld()).getApplicableRegions(p.getLocation()).iterator(); ((Iterator)ally1).hasNext();)
          {
            ProtectedRegion r = (ProtectedRegion)((Iterator)ally1).next();
            if (not.contains(r.getId()))
            {
              allow = false;
              break;
            }
          }
          if (allow)
          {
            damager.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.r.c.getString("cant_hit_because_ally")
              .replace("%player%", p.getName())));
            e.setCancelled(true);
            return;
          }
        }
        String walkInKD = Main.r.getInWitchKingdomAPlayerIs(damager.getLocation().getBlock());
        if ((!walkInKD.equals("-")) && (!walkInKD.equals(dKD)))
        {
          List<String> dAllies = Main.r.c.getStringList("allies." + dKD);
          if ((!walkInKD.equals(pKD)) || (!Main.r.attacks.containsKey(dKD)) || (!((String)Main.r.attacks.get(dKD)).equals(pKD)))
          {
            boolean check1 = false;
            for (String ally11 : dAllies) {
              if (Main.r.attacks.containsKey(ally11)) {
                if (((String)Main.r.attacks.get(ally11)).equals(pKD))
                {
                  check1 = true;
                  break;
                }
              }
            }
            if ((!walkInKD.equals(pKD)) || (!check1))
            {
              e.setCancelled(true);
              damager.sendMessage(ChatColor.RED + "Je kan " + p.getName() + " niet slaan op dit gebied.");
              return;
            }
          }
        }
      }
      if (Main.r.pvpProtection.contains(p.getUniqueId()))
      {
        e.setCancelled(true);
        damager.sendMessage(ChatColor.RED + "Je kan " + p.getName() + " niet slaan omdat hij pvp protectie aan heeft staan.");
        return;
      }
      if (!Main.r.getInWitchKingdomAPlayerIs(p).equals(Main.r.getInWitchKingdomAPlayerIs(damager))) {
        this.lastDamage.put(p, damager);
      } else if (Main.r.c.getStringList("friendly-fire").contains(ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(damager)))) {
        this.lastDamage.put(p, damager);
      } else {
        e.setCancelled(true);
      }
    }
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
@EventHandler
  public void deadEvent(PlayerDeathEvent e)
  {
    Player p = e.getEntity();
    
    Main.r.pvpProtection.add(p.getUniqueId());
    
    String inKD = ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(p));
    ArrayList<Player> in = Main.r.inAttack.containsKey(inKD) ? (ArrayList)Main.r.inAttack.get(inKD) : new ArrayList();
    if (in.contains(p))
    {
      in.remove(p);
      Main.r.inAttack.put(inKD, in);
      new AttackMenu().check(p);
    }
    Main.r.c.set("users." + p.getUniqueId().toString() + ".deaths", Integer.valueOf(Main.r.c.contains("users." + p.getUniqueId().toString() + ".deaths") ? Main.r.c.getInt("users." + p.getUniqueId().toString() + ".deaths") + 1 : 1));
    Main.r.saveConfig();
    if ((this.lastDamage.containsKey(p)) && (Main.r.war))
    {
      Player killer = (Player)this.lastDamage.get(p);
      e.setDeathMessage(null);
      
      Main.r.c.set("users." + killer.getUniqueId().toString() + ".kills", Integer.valueOf(Main.r.c.contains("users." + killer.getUniqueId().toString() + ".kills") ? Main.r.c.getInt("users." + killer.getUniqueId().toString() + ".kills") + 1 : 1));
      Main.r.saveConfig();
      
      String kingdomKiller = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', Main.r.getInWitchKingdomAPlayerIs(killer)));
      Main.r.c.set("leaderboard.kills." + kingdomKiller, Integer.valueOf(Main.r.c.contains("leaderboard.kills." + kingdomKiller) ? Main.r.c.getInt("leaderboard.kills." + kingdomKiller) + 1 : 1));
      Main.r.saveConfig();
      
      String RankK = ChatColor.translateAlternateColorCodes('&', Main.r.c.getString("rank." + (String)Main.r.c.getStringList("ranks").get(Main.r.c.getInt(new StringBuilder("users.").append(killer.getUniqueId().toString()).append(".rank").toString())) + ".prefix"));
      RankK = RankK + " ";
      
      String RankP = ChatColor.translateAlternateColorCodes('&', Main.r.c.getString("rank." + (String)Main.r.c.getStringList("ranks").get(Main.r.c.getInt(new StringBuilder("users.").append(p.getUniqueId().toString()).append(".rank").toString())) + ".prefix"));
      RankP = RankP + " ";
      
      Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', Main.r.c.getString("messages.kill")
        .replace("%player_rank%", RankP)
        .replace("%player_kingdom%", Main.r.getInWitchKingdomAPlayerIs(p))
        .replace("%player%", p.getName())
        .replace("%killer_rank%", RankK)
        .replace("%killer_kingdom%", Main.r.getInWitchKingdomAPlayerIs(killer))
        .replace("%killer%", killer.getName())));
      
      Main.r.killsPerKD.put(Main.r.getInWitchKingdomAPlayerIs(killer), Integer.valueOf((Main.r.killsPerKD.containsKey(Main.r.getInWitchKingdomAPlayerIs(killer)) ? ((Integer)Main.r.killsPerKD.get(Main.r.getInWitchKingdomAPlayerIs(killer))).intValue() : 0) + 1));
      this.lastDamage.remove(p);
    }
    else if (this.lastDamage.containsKey(p))
    {
      Player killer = (Player)this.lastDamage.get(p);
      e.setDeathMessage(null);
      
      Main.r.c.set("users." + killer.getUniqueId().toString() + ".kills", Integer.valueOf(Main.r.c.contains("users." + killer.getUniqueId().toString() + ".kills") ? Main.r.c.getInt("users." + killer.getUniqueId().toString() + ".kills") + 1 : 1));
      Main.r.saveConfig();
      
      String kingdomKiller = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', Main.r.getInWitchKingdomAPlayerIs(killer)));
      Main.r.c.set("leaderboard.kills." + kingdomKiller, Integer.valueOf(Main.r.c.contains("leaderboard.kills." + kingdomKiller) ? Main.r.c.getInt("leaderboard.kills." + kingdomKiller) + 1 : 1));
      Main.r.saveConfig();
      
      String RankK = ChatColor.translateAlternateColorCodes('&', Main.r.c.getString("rank." + (String)Main.r.c.getStringList("ranks").get(Main.r.c.getInt(new StringBuilder("users.").append(killer.getUniqueId().toString()).append(".rank").toString())) + ".prefix"));
      RankK = RankK + " ";
      
      String RankP = ChatColor.translateAlternateColorCodes('&', Main.r.c.getString("rank." + (String)Main.r.c.getStringList("ranks").get(Main.r.c.getInt(new StringBuilder("users.").append(p.getUniqueId().toString()).append(".rank").toString())) + ".prefix"));
      RankP = RankP + " ";
      
      Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', Main.r.c.getString("messages.kill")
        .replace("%player_rank%", RankP)
        .replace("%player_kingdom%", Main.r.getInWitchKingdomAPlayerIs(p))
        .replace("%player%", p.getName())
        .replace("%killer_rank%", RankK)
        .replace("%killer_kingdom%", Main.r.getInWitchKingdomAPlayerIs(killer))
        .replace("%killer%", killer.getName())));
    }
  }
}
