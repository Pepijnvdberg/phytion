package me.PepijnvdBerg.kingdombuild.listeners;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.PepijnvdBerg.kingdombuild.Main;

public class Chat_Listener
  implements Listener
{
  @SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
@EventHandler(priority=EventPriority.LOWEST)
  public void chat(AsyncPlayerChatEvent e)
  {
    final Player p = e.getPlayer();
    e.setCancelled(true);
    if ((Main.r.muted) && (!p.hasPermission("kingdom.mute.bypass")))
    { 
      p.sendMessage(ChatColor.RED + "De chat is gemute.");
      e.setCancelled(true);
      return;
    }
    if ((Main.r.send.contains(p)) && (!p.hasPermission("kingdom.antispam.bypass")))
    {
      p.sendMessage(ChatColor.RED + "Niet zo snel praten wacht nog even.");
      e.setCancelled(true);
      return;
    }
    Main.r.send.add(p);
    Main.r.getServer().getScheduler().runTaskLater(Main.r, new Runnable()
    {
      public void run()
      {
        Main.r.send.remove(p);
      }
    }, 60L);
    
    
    String Rank = ChatColor.translateAlternateColorCodes('&', Main.r.c.getString("rank." + (String)Main.r.c.getStringList("ranks").get(Main.r.c.getInt(new StringBuilder("users.").append(p.getUniqueId().toString()).append(".rank").toString())) + ".prefix"));
    Rank = Rank + " ";
    String inKingdom = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', Main.r.getInWitchKingdomAPlayerIs(p).toLowerCase()));
    String Kingdom = ChatColor.GRAY + "[" + Main.r.getInWitchKingdomAPlayerIs(p) + ChatColor.GRAY + "] ";
    if (!ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(p)).equals("default"))
    {
      if (e.getMessage().startsWith("!"))
      {
        if (((Main.r.chatMuted.contains("roleplay")) || (Main.r.chatMuted.contains(inKingdom))) && (!p.hasPermission("kingdom.mutebypass")))
        {
          e.setCancelled(true);
          p.sendMessage(ChatColor.RED + "Deze chat is gemute voor jou.");
          return;
        }
        e.setCancelled(true);
        for (Player wp : Bukkit.getOnlinePlayers()) {
          if (!Main.r.getInWitchKingdomAPlayerIs(wp).equals("default"))
          {
            @SuppressWarnings({ })
			ArrayList<String> muted = Main.r.PersonChatMuted.containsKey(wp) ? (ArrayList)Main.r.PersonChatMuted.get(wp) : new ArrayList();
            if (!muted.contains("roleplay"))
            {
              String chanel = ChatColor.GRAY + "{" + ChatColor.WHITE + "Roleplay" + ChatColor.GRAY + "} ";
              wp.sendMessage(chanel + Kingdom + Rank + (p.hasPermission("staff.chat") ? ChatColor.WHITE : ChatColor.GRAY) + p.getDisplayName() + (p.hasPermission("staff.chat") ? ChatColor.WHITE : ChatColor.GRAY) + ": " + (p.hasPermission("kingdom.coloredchat") ? ChatColor.translateAlternateColorCodes('&', e.getMessage().substring(1)) : e.getMessage().substring(1)));
            }
          }
        }
      }
      else if (e.getMessage().startsWith("$"))
      {
        if (((Main.r.chatMuted.contains("trade")) || (Main.r.chatMuted.contains(inKingdom))) && (!p.hasPermission("kingdom.mutebypass")))
        {
          e.setCancelled(true);
          p.sendMessage(ChatColor.RED + "Deze chat is gemute voor jou.");
          return;
        }
        e.setCancelled(true);
        for (Player wp : Bukkit.getOnlinePlayers()) {
          if (!Main.r.getInWitchKingdomAPlayerIs(wp).equals("default"))
          {
            ArrayList<String> muted = Main.r.PersonChatMuted.containsKey(wp) ? (ArrayList)Main.r.PersonChatMuted.get(wp) : new ArrayList();
            if (!muted.contains("trade"))
            {
              String chanel = ChatColor.GRAY + "{" + ChatColor.BLUE + "Trade" + ChatColor.GRAY + "} ";
              wp.sendMessage(chanel + Kingdom + Rank + (p.hasPermission("staff.chat") ? ChatColor.WHITE : ChatColor.GRAY) + p.getDisplayName() + (p.hasPermission("staff.chat") ? ChatColor.WHITE : ChatColor.GRAY) + ": " + (p.hasPermission("kingdom.coloredchat") ? ChatColor.translateAlternateColorCodes('&', e.getMessage().substring(1)) : e.getMessage().substring(1)));
            }
          }
        }
      }
      else if (e.getMessage().startsWith("%"))
      {
        if (((Main.r.chatMuted.contains("hkm")) || (Main.r.chatMuted.contains(inKingdom))) && (!p.hasPermission("kingdom.mutebypass")))
        {
          e.setCancelled(true);
          p.sendMessage(ChatColor.RED + "Deze chat is gemute voor jou.");
          return;
        }
        e.setCancelled(true);
        if (p.hasPermission("kingdom.hkm")) {
          for (Player wp : Bukkit.getOnlinePlayers()) {
            if (wp.hasPermission("kingdom.hkm")) {
              if (!Main.r.getInWitchKingdomAPlayerIs(wp).equals("default"))
              {
                ArrayList<String> muted = Main.r.PersonChatMuted.containsKey(wp) ? (ArrayList)Main.r.PersonChatMuted.get(wp) : new ArrayList();
                if (!muted.contains("hkm"))
                {
                  String chanel = ChatColor.GRAY + "{" + ChatColor.GOLD + "HKM" + ChatColor.GRAY + "} ";
                  wp.sendMessage(chanel + Kingdom + Rank + (p.hasPermission("staff.chat") ? ChatColor.WHITE : ChatColor.GRAY) + p.getDisplayName() + (p.hasPermission("staff.chat") ? ChatColor.WHITE : ChatColor.GRAY) + ": " + (p.hasPermission("kingdom.coloredchat") ? ChatColor.translateAlternateColorCodes('&', e.getMessage().substring(1)) : e.getMessage().substring(1)));
                }
              }
            }
          }
        } else {
          p.sendMessage(ChatColor.RED + "Je hebt hier geen permissions voor.");
        }
      }
      else if (e.getMessage().startsWith("*"))
      {
        e.setCancelled(true);
        if (p.hasPermission("kingdom.staff")) {
          for (Player wp : Bukkit.getOnlinePlayers()) {
            if (!Main.r.getInWitchKingdomAPlayerIs(wp).equals("default"))
            {
              String chanel = ChatColor.WHITE + "{" + ChatColor.DARK_AQUA + "Staff" + ChatColor.WHITE + "} ";
              wp.sendMessage(chanel + Kingdom + Rank + (p.hasPermission("staff.chat") ? ChatColor.WHITE : ChatColor.GRAY) + p.getDisplayName() + (p.hasPermission("staff.chat") ? ChatColor.WHITE : ChatColor.GRAY) + ": " + (p.hasPermission("kingdom.coloredchat") ? ChatColor.translateAlternateColorCodes('&', e.getMessage().substring(1)) : e.getMessage().substring(1)));
            }
          }
        } else {
          p.sendMessage(ChatColor.RED + "Je hebt hier geen permissions voor.");
        }
      }
      else
      {
        if ((Main.r.chatMuted.contains(inKingdom)) && (!p.hasPermission("kingdom.mutebypass")))
        {
          e.setCancelled(true);
          p.sendMessage(ChatColor.RED + "Deze chat is gemute voor jou.");
          return;
        }
        e.setCancelled(true);
        if (Main.r.getInWitchKingdomAPlayerIs(p).equals("geen")) {
          return;
        }
        if (Main.r.lookInChat.containsKey(p)) {
          for (Player wp : Bukkit.getOnlinePlayers())
          {
            String inK = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', Main.r.getInWitchKingdomAPlayerIs(wp).toLowerCase()));
            if ((inK.equalsIgnoreCase((String)Main.r.lookInChat.get(p))) || (((String)Main.r.lookInChat.get(p)).equals(Main.r.lookInChat.get(wp))) || (Main.r.spy.contains(wp)))
            {
              ArrayList<String> muted = Main.r.PersonChatMuted.containsKey(wp) ? (ArrayList)Main.r.PersonChatMuted.get(wp) : new ArrayList();
              if (!muted.contains("kingdom")) {
                wp.sendMessage(Kingdom + Rank + (p.hasPermission("staff.chat") ? ChatColor.WHITE : ChatColor.GRAY) + p.getDisplayName() + (p.hasPermission("staff.chat") ? ChatColor.WHITE : ChatColor.GRAY) + ": " + (p.hasPermission("kingdom.coloredchat") ? ChatColor.translateAlternateColorCodes('&', e.getMessage()) : e.getMessage()));
              }
            }
          }
        } else {
          for (Player wp : Bukkit.getOnlinePlayers())
          {
            String inK = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', Main.r.getInWitchKingdomAPlayerIs(p).toLowerCase()));
            if (((!(Main.permission.getPlayerGroups(wp.getWorld(), wp.getName())[0] != null ? Main.r.getTextColor(String.valueOf(Main.permission.getPlayerGroups(wp.getWorld(), wp.getName())[0])) : "Onbekend").equals(Main.r.getInWitchKingdomAPlayerIs(p))) || (Main.r.lookInChat.containsKey(wp))) && (!Main.r.spy.contains(wp)))
            {
              if (!inK.equalsIgnoreCase(Main.r.lookInChat.containsKey(wp) ? (String)Main.r.lookInChat.get(wp) : "")) {}
            }
            else
            {
              ArrayList<String> muted = Main.r.PersonChatMuted.containsKey(wp) ? (ArrayList)Main.r.PersonChatMuted.get(wp) : new ArrayList();
              if (!muted.contains("kingdom")) {
                wp.sendMessage(Kingdom + Rank + (p.hasPermission("staff.chat") ? ChatColor.WHITE : ChatColor.GRAY) + p.getDisplayName() + (p.hasPermission("staff.chat") ? ChatColor.WHITE : ChatColor.GRAY) + ": " + (p.hasPermission("kingdom.coloredchat") ? ChatColor.translateAlternateColorCodes('&', e.getMessage()) : e.getMessage()));
              }
            }
          }
        }
      }
    }
    else
    {
      p.sendMessage(ChatColor.RED + "Zonder kingdom kan je niet praten.");
      e.setCancelled(true);
    }
  }
}
