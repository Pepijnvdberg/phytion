package me.PepijnvdBerg.kingdombuild.listeners;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import me.PepijnvdBerg.kingdombuild.Main;
import me.PepijnvdBerg.kingdombuild.events.AttackMenu;
import me.PepijnvdBerg.kingdombuild.events.CustomScoreBoard;

public class Quit_Listener
  implements Listener
{
  @SuppressWarnings({ "unchecked", "rawtypes" })
@EventHandler
  public void quit(PlayerQuitEvent e)
  {
    new CustomScoreBoard().delPrefix(e.getPlayer());
    Player p = e.getPlayer();
    p.setGameMode(GameMode.SURVIVAL);
    if (Main.r.staffModes.contains(p))
    {
      Main.r.staffModes.remove(p);
      if (Main.r.vanish.contains(p))
      {
        Main.r.vanish.remove(p);
        p.sendMessage(ChatColor.GRAY + "Iedereen ziet jouw nu.");
        for (Player wp : Bukkit.getOnlinePlayers()) {
          wp.showPlayer(p);
        }
      }
      if (Main.r.saveInv.containsKey(p))
      {
        p.getInventory().clear();
        Inventory inv = (Inventory)Main.r.saveInv.get(p);
        for (int i = 0; i < inv.getSize(); i++) {
          p.getInventory().setItem(i, inv.getItem(i));
        }
      }
    }
    String inKD = ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(p));
    Object in = Main.r.inAttack.containsKey(inKD) ? (ArrayList)Main.r.inAttack.get(inKD) : new ArrayList();
    if (((ArrayList)in).contains(p))
    {
      ((ArrayList)in).remove(p);
      Main.r.inAttack.put(inKD, (ArrayList<Player>) in);
      new AttackMenu().check(p);
    }
  }
}
