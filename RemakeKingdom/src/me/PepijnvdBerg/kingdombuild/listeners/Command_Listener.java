package me.PepijnvdBerg.kingdombuild.listeners;

import com.sk89q.worldedit.bukkit.selections.Selection;

import me.PepijnvdBerg.kingdombuild.Crate;
import me.PepijnvdBerg.kingdombuild.Koth;
import me.PepijnvdBerg.kingdombuild.Main;
import me.PepijnvdBerg.kingdombuild.events.AttackMenu;
import me.PepijnvdBerg.kingdombuild.events.CustomScoreBoard;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Command_Listener implements CommandExecutor {
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (cmd.getName().equalsIgnoreCase("oorlog")) {
      if (sender instanceof Player) {
        Player p = (Player)sender;
        if (!p.hasPermission("oorlog.oorlog")) {
          p.sendMessage(ChatColor.RED + "Je hebt hier geen permissions voor.");
          return false;
        } 
      } 
      if (args.length > 0) {
        if (args[0].equals("stop")) {
          if (!Main.r.war)
            return false; 
          Main.r.stop_war();
          CustomScoreBoard csb = new CustomScoreBoard();
          for (Player p : Bukkit.getOnlinePlayers())
            csb.setScoreboard(p); 
          return false;
        } 
        if (args[0].equals("start")) {
          if (Main.r.war)
            return false; 
          Main.r.start_war();
          CustomScoreBoard csb = new CustomScoreBoard();
          for (Player p : Bukkit.getOnlinePlayers())
            csb.setScoreboard(p); 
          return false;
        } 
        if (args.length > 1) {
          if (args[0].equals("addtime")) {
            if (!Main.r.war)
              return false; 
            Main.r.wartime += Integer.parseInt(args[1]);
            CustomScoreBoard csb = new CustomScoreBoard();
            for (Player p : Bukkit.getOnlinePlayers())
              csb.setScoreboard(p); 
          } 
          if (args[0].equals("deltime")) {
            if (!Main.r.war)
              return false; 
            Main.r.wartime -= Integer.parseInt(args[1]);
            CustomScoreBoard csb = new CustomScoreBoard();
            for (Player p : Bukkit.getOnlinePlayers())
              csb.setScoreboard(p); 
          } 
        } 
      } else {
        sender.sendMessage(ChatColor.GOLD + "--- Help voor /oorlog ---");
        sender.sendMessage(ChatColor.GRAY + "/oorlog start");
        sender.sendMessage(ChatColor.GRAY + "/oorlog stop");
        sender.sendMessage(ChatColor.GRAY + "/oorlog addtime <time>");
        sender.sendMessage(ChatColor.GRAY + "/oorlog deltime <time>");
      } 
    } 
    if (cmd.getName().equalsIgnoreCase("kspy"))
      if (sender instanceof Player) {
        Player p = (Player)sender;
        if (p.hasPermission("kingdom.spy")) {
          if (Main.r.spy.contains(p)) {
            Main.r.spy.remove(p);
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Je hebt de channel &7[&cSpy&7] geleft."));
            return false;
          } 
          Main.r.spy.add(p);
          p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Je hebt de channel &7[&cSpy&7] gejoined."));
          return false;
        } 
        p.sendMessage(ChatColor.RED + "Je hebt hier geen permissions voor.");
        return false;
      }  
    if (cmd.getName().equalsIgnoreCase("k") || cmd.getName().equalsIgnoreCase("kingdom"))
      if (args.length > 0) {
        if (args.length > 1 && (args[0].equalsIgnoreCase("promote") || 
          args[0].equalsIgnoreCase("demote") || 
          args[0].equalsIgnoreCase("setrank") || 
          args[0].equalsIgnoreCase("setprefix") || 
          args[0].equalsIgnoreCase("give") || 
          args[0].equalsIgnoreCase("setkingdom"))) {
          if (Bukkit.getPlayer(args[1]) != null) {
            Player player = Bukkit.getPlayer(args[1]);
            if (args[0].equalsIgnoreCase("promote")) {
              if (sender instanceof Player && 
                !((Player)sender).hasPermission("kingdom.promote")) {
                sender.sendMessage(ChatColor.RED + "Je hebt hier geen permissions voor.");
                return false;
              } 
              List<String> ranks = Main.r.c.getStringList("ranks");
              if (ranks.size() - 1 > Main.r.c.getInt("users." + player.getUniqueId().toString() + ".rank")) {
                Main.r.c.set("users." + player.getUniqueId().toString() + ".rank", Integer.valueOf(Main.r.c.getInt("users." + player.getUniqueId().toString() + ".rank") + 1));
                Main.r.saveConfig();
                if (Main.r.c.getInt("users." + player.getUniqueId().toString() + ".rank") >= Main.r.c.getInt("high_perms_nmr")) {
                  for (String command : Main.r.c.getStringList("high_perms")) {
                    Bukkit.getServer().dispatchCommand((CommandSender)Bukkit.getConsoleSender(), String.format("pex user %s add %s", new Object[] { player.getName(), command }));
                  } 
                } else if (Main.r.c.getInt("users." + player.getUniqueId().toString() + ".rank") < Main.r.c.getInt("high_perms_nmr")) {
                  for (String command : Main.r.c.getStringList("high_perms")) {
                    Bukkit.getServer().dispatchCommand((CommandSender)Bukkit.getConsoleSender(), String.format("pex user %s remove %s", new Object[] { player.getName(), command }));
                  } 
                } 
                return false;
              } 
              sender.sendMessage(ChatColor.RED + "Deze speler heeft al de hoogste rank.");
            } else if (args[0].equalsIgnoreCase("demote")) {
              if (sender instanceof Player && 
                !((Player)sender).hasPermission("kingdom.demote")) {
                sender.sendMessage(ChatColor.RED + "Je hebt hier geen permissions voor.");
                return false;
              } 
              if (Main.r.c.getInt("users." + player.getUniqueId().toString() + ".rank") > 0) {
                Main.r.c.set("users." + player.getUniqueId().toString() + ".rank", Integer.valueOf(Main.r.c.getInt("users." + player.getUniqueId().toString() + ".rank") - 1));
                Main.r.saveConfig();
                if (Main.r.c.getInt("users." + player.getUniqueId().toString() + ".rank") >= Main.r.c.getInt("high_perms_nmr")) {
                  for (String command : Main.r.c.getStringList("high_perms")) {
                    Bukkit.getServer().dispatchCommand((CommandSender)Bukkit.getConsoleSender(), String.format("pex user %s add %s", new Object[] { player.getName(), command }));
                  } 
                } else if (Main.r.c.getInt("users." + player.getUniqueId().toString() + ".rank") < Main.r.c.getInt("high_perms_nmr")) {
                  for (String command : Main.r.c.getStringList("high_perms")) {
                    Bukkit.getServer().dispatchCommand((CommandSender)Bukkit.getConsoleSender(), String.format("pex user %s remove %s", new Object[] { player.getName(), command }));
                  } 
                } 
                return false;
              } 
              sender.sendMessage(ChatColor.RED + "Deze speler heeft al de laagste rank.");
            } else if (args.length > 2) {
              if (args[0].equalsIgnoreCase("setrank")) {
                if (sender instanceof Player && 
                  !((Player)sender).hasPermission("kingdom.setrank")) {
                  sender.sendMessage(ChatColor.RED + "Je hebt hier geen permissions voor.");
                  return false;
                } 
                List<String> ranks = Main.r.c.getStringList("ranks");
                if (ranks.contains(args[2])) {
                  for (int i = 0; i < ranks.size(); i++) {
                    if (((String)ranks.get(i)).equals(args[2])) {
                      Main.r.c.set("users." + player.getUniqueId().toString() + ".rank", Integer.valueOf(i));
                      Main.r.saveConfig();
                      if (Main.r.c.getInt("users." + player.getUniqueId().toString() + ".rank") >= Main.r.c.getInt("high_perms_nmr")) {
                        for (String command : Main.r.c.getStringList("high_perms")) {
                          Bukkit.getServer().dispatchCommand((CommandSender)Bukkit.getConsoleSender(), String.format("pex user %s add %s", new Object[] { player.getName(), command }));
                        } 
                      } else if (Main.r.c.getInt("users." + player.getUniqueId().toString() + ".rank") < Main.r.c.getInt("high_perms_nmr")) {
                        for (String command : Main.r.c.getStringList("high_perms")) {
                          Bukkit.getServer().dispatchCommand((CommandSender)Bukkit.getConsoleSender(), String.format("pex user %s remove %s", new Object[] { player.getName(), command }));
                        } 
                      } 
                      return false;
                    } 
                  } 
                } else {
                  sender.sendMessage(ChatColor.RED + "Die rank bestaat niet.");
                } 
              } else {
                if (args[0].equalsIgnoreCase("setprefix")) {
                  if (sender instanceof Player && 
                    !((Player)sender).hasPermission("kingdom.setprefix")) {
                    sender.sendMessage(ChatColor.RED + "Je hebt hier geen permissions voor.");
                    return false;
                  } 
                  if (!args[2].equals("-")) {
                    Main.r.c.set("users." + player.getUniqueId().toString() + ".prefix", args[2]);
                    Main.r.saveConfig();
                    sender.sendMessage(ChatColor.GREEN + "De tablist naam van " + player.getName() + " is aangepast naar: " + ChatColor.translateAlternateColorCodes('&', args[2]));
                  } else {
                    Main.r.c.set("users." + player.getUniqueId().toString() + ".prefix", null);
                    Main.r.saveConfig();
                    sender.sendMessage(ChatColor.GREEN + "De tablist naam van " + player.getName() + " is verwijderd");
                  } 
                  CustomScoreBoard csb = new CustomScoreBoard();
                  csb.setScoreboard(player);
                  return false;
                } 
                if (args[0].equalsIgnoreCase("give")) {
                  if (sender instanceof Player && 
                    !((Player)sender).hasPermission("kingdom.give")) {
                    sender.sendMessage(ChatColor.RED + "Je hebt hier geen permissions voor.");
                    return false;
                  } 
                  if (args.length > 2) {
                    String crate = args[2];
                    if (!Main.r.c.getStringList("crates").contains(crate)) {
                      sender.sendMessage(ChatColor.RED + "Geen geldige crate opgegeven.");
                      return false;
                    } 
                    int amount = 1;
                    if (args.length == 4)
                      if (Main.r.isNumeric(args[3])) {
                        amount = (Integer.parseInt(args[3]) > 64) ? 64 : Integer.parseInt(args[3]);
                      } else {
                        sender.sendMessage(ChatColor.RED + "Geen geldige aantal opgegeven.");
                        return false;
                      }  
                    (new Crate(crate)).giveCrate(player, amount);
                    return false;
                  } 
                  sender.sendMessage(ChatColor.RED + "Niet alle values zijn gegeven.");
                } else if (args[0].equalsIgnoreCase("setkingdom")) {
                  if (sender instanceof Player && 
                    !((Player)sender).hasPermission("kingdom.setkingdom")) {
                    sender.sendMessage(ChatColor.RED + "Je hebt hier geen permissions voor.");
                    return false;
                  } 
                  List<String> kingdoms = Main.r.getAllKingdoms();
                  if (kingdoms.contains(args[2])) {
                    Bukkit.getServer().dispatchCommand((CommandSender)Bukkit.getConsoleSender(), String.format("pex user %s group set %s", new Object[] { player.getName(), args[2] }));
                    sender.sendMessage("speler " + player.getName() + " is verzet naar " + Main.r.getInWitchKingdomAPlayerIs(player));
                    Main.r.setNewListChase();
                    for (Player wp : Bukkit.getOnlinePlayers())
                      wp.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSpeler &6" + player.getName() + "&a Heeft voor &7[" + Main.r.getInWitchKingdomAPlayerIs(player) + "&7]&a gekozen!")); 
                  } else {
                    sender.sendMessage(ChatColor.RED + "Dat rijk bestaat niet.");
                  } 
                } 
              } 
            } else {
              sender.sendMessage(ChatColor.RED + "Niet alle values zijn gegeven.");
            } 
          } else {
            sender.sendMessage(ChatColor.RED + "Deze speler is niet online.");
          } 
        } else if (args[0].equalsIgnoreCase("staffmode")) {
          if (sender instanceof Player) {
            Player p = (Player)sender;
            if (!p.hasPermission("kingdom.staffmode")) {
              p.sendMessage(ChatColor.RED + "U heeft geen perms hiervoor.");
              return false;
            } 
            if (Main.r.staffModes.contains(p)) {
              p.setGameMode(GameMode.SURVIVAL);
              Main.r.staffModes.remove(p);
              if (Main.r.vanish.contains(p)) {
                Main.r.vanish.remove(p);
                p.sendMessage(ChatColor.GRAY + "Iedereen ziet jouw nu.");
                for (Player wp : Bukkit.getOnlinePlayers())
                  wp.showPlayer(p); 
              } 
              if (Main.r.saveInv.containsKey(p)) {
                p.getInventory().clear();
                Inventory inv = (Inventory)Main.r.saveInv.get(p);
                for (int i = 0; i < inv.getSize(); i++)
                  p.getInventory().setItem(i, inv.getItem(i)); 
              } 
            } else {
              Inventory inv = Bukkit.createInventory((InventoryHolder)p, 36, "");
              for (int j = 0; j < p.getInventory().getSize(); j++)
                inv.setItem(j, p.getInventory().getItem(j)); 
              Main.r.saveInv.put(p, inv);
              p.getInventory().clear();
              p.setGameMode(GameMode.CREATIVE);
              ItemStack i = new ItemStack(Material.STICK, 1);
              ItemMeta m = i.getItemMeta();
              m.setDisplayName(ChatColor.BLUE + "Freeze");
              i.setItemMeta(m);
              p.getInventory().setItem(7, i);
              if (!Main.r.vanish.contains(p)) {
                ItemStack itemStack = new ItemStack(Material.INK_SACK, 1, (short)8);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(ChatColor.GRAY + "Vanish off");
                itemStack.setItemMeta(itemMeta);
                p.getInventory().setItem(8, itemStack);
              } else {
                ItemStack itemStack = new ItemStack(Material.INK_SACK, 1, (short)10);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(ChatColor.GREEN + "Vanish on");
                itemStack.setItemMeta(itemMeta);
                p.getInventory().setItem(8, itemStack);
              } 
              ItemStack i1 = new ItemStack(Material.COMPASS, 1);
              ItemMeta m1 = i1.getItemMeta();
              m1.setDisplayName(ChatColor.BLUE + "Random teleport");
              i1.setItemMeta(m1);
              p.getInventory().setItem(6, i1);
              ItemStack i2 = new ItemStack(Material.CHEST, 1);
              ItemMeta m2 = i2.getItemMeta();
              m2.setDisplayName(ChatColor.GREEN + "Fly speed");
              i2.setItemMeta(m2);
              p.getInventory().setItem(5, i2);
              Main.r.staffModes.add(p);
            } 
          } 
        } else if (args[0].equalsIgnoreCase("setcap")) {
          if (args.length == 3) {
            if (sender instanceof Player) {
              Player p = (Player)sender;
              if (!p.hasPermission("kingdom.koth.create")) {
                p.sendMessage(ChatColor.RED + "U heeft geen perms hiervoor.");
                return false;
              } 
              Selection s = Main.r.getWorldEdit().getSelection(p);
              if (s != null) {
                String koth = args[1];
                String timeString = args[2];
                if (!Main.r.c.contains("koth." + koth)) {
                  if (Main.r.isNumeric(timeString)) {
                    Main.r.c.set("koth." + koth + ".world", s.getMinimumPoint().getWorld().getName());
                    Main.r.c.set("koth." + koth + ".x1", Integer.valueOf(s.getMinimumPoint().getBlockX()));
                    Main.r.c.set("koth." + koth + ".y1", Integer.valueOf(s.getMinimumPoint().getBlockY()));
                    Main.r.c.set("koth." + koth + ".z1", Integer.valueOf(s.getMinimumPoint().getBlockZ()));
                    Main.r.c.set("koth." + koth + ".x2", Integer.valueOf(s.getMaximumPoint().getBlockX()));
                    Main.r.c.set("koth." + koth + ".y2", Integer.valueOf(s.getMaximumPoint().getBlockY()));
                    Main.r.c.set("koth." + koth + ".z2", Integer.valueOf(s.getMaximumPoint().getBlockZ()));
                    Main.r.c.set("koth." + koth + ".time", timeString);
                    Main.r.saveConfig();
                    sender.sendMessage(ChatColor.GREEN + "Succesvol een nieuwe koth gemaakt genaamt: " + koth);
                  } else {
                    sender.sendMessage(ChatColor.RED + "De tijd die is opgegeven is niet geldig.");
                  } 
                } else {
                  sender.sendMessage(ChatColor.RED + "Er bestaat al een koth met die naam.");
                } 
              } else {
                sender.sendMessage(ChatColor.RED + "Je moet een wordedit selectie hebben.");
              } 
            } else {
              sender.sendMessage(ChatColor.RED + "Je kan dit commando alleen uitvoeren als speler.");
            } 
          } else {
            sender.sendMessage(ChatColor.RED + "Gebruik /kingdom setcap <koth> <time>.");
          } 
        } else if (args[0].equalsIgnoreCase("kothstart")) {
          if (args.length == 2) {
            String koth = args[1];
            if (sender instanceof Player && !((Player)sender).hasPermission("kingdom.koth.start")) {
              sender.sendMessage(ChatColor.RED + "U heeft geen perms hiervoor.");
              return false;
            } 
            if (Main.r.war) {
              if (!Main.r.Koths.containsKey(koth)) {
                Main.r.Koths.put(koth, new Koth(koth));
                sender.sendMessage(ChatColor.GREEN + "Koth " + koth + " is getstart.");
              } else {
                sender.sendMessage(ChatColor.RED + "Deze koth is al actief.");
              } 
            } else {
              sender.sendMessage(ChatColor.RED + "Het moet oorlog zijn om een koth te starten.");
            } 
          } else {
            sender.sendMessage(ChatColor.RED + "Gebruik /kingdom <koth>.");
          } 
        } else if (args[0].equalsIgnoreCase("kothstop")) {
          if (args.length == 2) {
            String koth = args[1];
            if (sender instanceof Player && !((Player)sender).hasPermission("kingdom.koth.stop")) {
              sender.sendMessage(ChatColor.RED + "U heeft geen perms hiervoor.");
              return false;
            } 
            if (Main.r.Koths.containsKey(koth)) {
              Main.r.Koths.remove(koth);
              sender.sendMessage(ChatColor.GREEN + "Koth " + koth + " is gestopt.");
            } else {
              sender.sendMessage(ChatColor.RED + "Deze koth is niet actief.");
            } 
          } else {
            sender.sendMessage(ChatColor.RED + "Gebruikt /kingdom <koth>.");
          } 
        } else if (args[0].equalsIgnoreCase("tphere")) {
          if (args.length == 2) {
            String kingdom = args[1];
            if (sender instanceof Player) {
              if (((Player)sender).hasPermission("kingdom.tphere")) {
                if (Main.r.getAllKingdoms().contains(kingdom)) {
                  kingdom = kingdom.toLowerCase();
                  Player p = (Player)sender;
                  for (Player wp : Bukkit.getOnlinePlayers()) {
                    String inK = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', Main.r.getInWitchKingdomAPlayerIs(wp).toLowerCase()));
                    if (inK.equals(kingdom))
                      wp.teleport((Entity)p); 
                  } 
                } else {
                  sender.sendMessage(ChatColor.RED + "Dit kingdom bestaat niet.");
                } 
              } else {
                sender.sendMessage(ChatColor.RED + "U heeft geen permissions voor dit commando.");
              } 
            } else {
              sender.sendMessage(ChatColor.RED + "Dit commando is alleen voor spelers.");
            } 
          } else {
            sender.sendMessage(ChatColor.RED + "Gebruik /kingdom tphere <kingdom>.");
          } 
        } else if (args[0].equalsIgnoreCase("tp")) {
          if (args.length == 2) {
            String target = args[1];
            if (sender instanceof Player) {
              Player p = (Player)sender;
              if (p.hasPermission("kingdom.tp")) {
                if (Bukkit.getPlayer(target) != null) {
                  Player t = Bukkit.getPlayer(target);
                  if (Main.r.getInWitchKingdomAPlayerIs(p).equals(Main.r.getInWitchKingdomAPlayerIs(t))) {
                    p.teleport((Entity)t);
                  } else {
                    sender.sendMessage(ChatColor.RED + "Deze speler zit niet in het zelfde kingdom.");
                  } 
                } else {
                  sender.sendMessage(ChatColor.RED + "Deze speler is niet online.");
                } 
              } else {
                sender.sendMessage(ChatColor.RED + "U heeft geen permissions voor dit commando.");
              } 
            } else {
              sender.sendMessage(ChatColor.RED + "Dit commando is alleen voor spelers.");
            } 
          } else {
            sender.sendMessage(ChatColor.RED + "Gebruik /kingdom tp <speler>.");
          } 
        } else if (args[0].equalsIgnoreCase("msg")) {
          if (args.length > 2) {
            String target = args[1];
            if (sender instanceof Player) {
              Player p = (Player)sender;
              if (p.hasPermission("kingdom.msg")) {
                if (Bukkit.getPlayer(target) != null) {
                  Player t = Bukkit.getPlayer(target);
                  String str = "";
                  for (int i = 2; i < args.length; i++)
                    str = String.valueOf(str) + args[i] + " "; 
                  t.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&c" + p.getName() + " &7-> &cMe&7] &7" + str));
                } else {
                  sender.sendMessage(ChatColor.RED + "Deze speler is niet online.");
                } 
              } else {
                sender.sendMessage(ChatColor.RED + "U heeft geen permissions voor dit commando.");
              } 
            } else {
              sender.sendMessage(ChatColor.RED + "Dit commando is alleen voor spelers.");
            } 
          } else {
            sender.sendMessage(ChatColor.RED + "Gebruik /kingdom msg <speler> <message>.");
          } 
        } else if (args[0].equalsIgnoreCase("influenceignore")) {
          if (sender instanceof Player) {
            Player p = (Player)sender;
            if (p.hasPermission("kingdom.influenceignore")) {
              if (Main.r.influenceIgnore.contains(p)) {
                Main.r.influenceIgnore.remove(p);
                p.sendMessage(ChatColor.GRAY + "Je hebt je influence nu aan.");
              } else {
                Main.r.influenceIgnore.add(p);
                p.sendMessage(ChatColor.GRAY + "Je hebt je influence nu uit.");
              } 
            } else {
              sender.sendMessage(ChatColor.RED + "U heeft geen permissions voor dit commando.");
            } 
          } else {
            sender.sendMessage(ChatColor.RED + "Dit commando is alleen voor spelers.");
          } 
        } else if (args[0].equalsIgnoreCase("joinchannel")) {
          if (args.length == 2) {
            String kingdom = args[1];
            if (sender instanceof Player) {
              if (((Player)sender).hasPermission("kingdom.joinchannel")) {
                if (Main.r.getAllKingdoms().contains(kingdom)) {
                  kingdom = kingdom.toLowerCase();
                  Player p = (Player)sender;
                  if (Main.r.lookInChat.containsKey(p))
                    if (((String)Main.r.lookInChat.get(p)).equalsIgnoreCase(kingdom)) {
                      p.sendMessage(ChatColor.RED + "Je zit al in " + kingdom + "'s chat kanaal.");
                      return false;
                    }  
                  String oldKD = Main.r.lookInChat.containsKey(p) ? (String)Main.r.lookInChat.get(p) : ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(p));
                  p.sendMessage(ChatColor.GREEN + "Je bent " + kingdom + "'s chat kanaal gejoind.");
                  Main.r.lookInChat.put(p, kingdom);
                  for (Player wp : Bukkit.getOnlinePlayers()) {
                    if (wp != p) {
                      String inK = Main.r.lookInChat.containsKey(wp) ? (String)Main.r.lookInChat.get(wp) : ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(wp));
                      if (kingdom.equalsIgnoreCase(inK))
                        wp.sendMessage(ChatColor.translateAlternateColorCodes('&', "%player_kingdom%%player%&a is jou chatkanaal gejoined!"
                              .replace("%player_kingdom%", Main.r.getInWitchKingdomAPlayerIs(p).substring(0, 2))
                              .replace("%player%", p.getName()))); 
                      if (oldKD.equalsIgnoreCase(inK))
                        wp.sendMessage(ChatColor.translateAlternateColorCodes('&', "%player_kingdom%%player%&c heeft jou chatkanaal verlaten!"
                              .replace("%player_kingdom%", Main.r.getInWitchKingdomAPlayerIs(p).substring(0, 2))
                              .replace("%player%", p.getName()))); 
                    } 
                  } 
                } else {
                  sender.sendMessage(ChatColor.RED + "Dit kingdom bestaat niet.");
                } 
              } else {
                sender.sendMessage(ChatColor.RED + "U heeft geen permissions voor dit commando.");
              } 
            } else {
              sender.sendMessage(ChatColor.RED + "Dit commando is alleen voor spelers.");
            } 
          } else {
            sender.sendMessage(ChatColor.RED + "Gebruik /kingdom joinchannel <kingdom>.");
          } 
        } else if (args[0].equalsIgnoreCase("mute")) {
          if (args.length == 2) {
            if (sender instanceof Player && !((Player)sender).hasPermission("kingdom.command.mute")) {
              sender.sendMessage(ChatColor.RED + "U heeft geen perms hiervoor.");
              return false;
            } 
            String chanel = args[1].toLowerCase();
            ArrayList<String> chanels = new ArrayList<>();
            chanels.add("roleplay");
            chanels.add("trade");
            chanels.add("hkm");
            chanels.add("dok");
            chanels.add("malzan");
            chanels.add("eredon");
            chanels.add("hyvar");
            chanels.add("tilifia");
            chanels.add("adamantium");
            if (chanel.equalsIgnoreCase("all")) {
              for (String str : chanels) {
                if (!Main.r.chatMuted.contains(str))
                  Main.r.chatMuted.add(str); 
              } 
              sender.sendMessage(ChatColor.GREEN + "Alle channels staan nu uit.");
              return false;
            } 
            if (chanels.contains(chanel)) {
              if (!Main.r.chatMuted.contains(chanel)) {
                Main.r.chatMuted.add(chanel);
                sender.sendMessage(ChatColor.GREEN + "Het chat kanaal " + chanel + " staat nu uit.");
              } else {
                sender.sendMessage(ChatColor.GREEN + "Het chat kanaal " + chanel + " staat al uit.");
              } 
            } else {
              sender.sendMessage(ChatColor.GREEN + "Het chat kanaal " + chanel + " bestaat uit.");
              sender.sendMessage(ChatColor.RED + "Gebruik: " + ChatColor.GRAY + "roleplay, trade, hkm, dok, malzan, eredon, hyvar, tilifia of adamatium.");
            } 
          } else {
            sender.sendMessage(ChatColor.RED + "Gebruik /kingdom mute <channel>.");
          } 
        } else if (args[0].equalsIgnoreCase("unmute")) {
          if (args.length == 2) {
            if (sender instanceof Player && !((Player)sender).hasPermission("kingdom.command.unmute")) {
              sender.sendMessage(ChatColor.RED + "U heeft geen perms hiervoor.");
              return false;
            } 
            String chanel = args[1].toLowerCase();
            ArrayList<String> chanels = new ArrayList<>();
            chanels.add("roleplay");
            chanels.add("trade");
            chanels.add("hkm");
            chanels.add("dok");
            chanels.add("malzan");
            chanels.add("eredon");
            chanels.add("hyvar");
            chanels.add("tilifia");
            chanels.add("adamantium");
            if (chanel.equalsIgnoreCase("all")) {
              for (String str : chanels) {
                if (Main.r.chatMuted.contains(str))
                  Main.r.chatMuted.remove(str); 
              } 
              sender.sendMessage(ChatColor.GREEN + "Alle channels staan nu uit.");
              return false;
            } 
            if (chanels.contains(chanel)) {
              if (Main.r.chatMuted.contains(chanel)) {
                Main.r.chatMuted.remove(chanel);
                sender.sendMessage(ChatColor.GREEN + "Het chat kanaal " + chanel + " staat nu aan.");
              } else {
                sender.sendMessage(ChatColor.GREEN + "Het chat kanaal " + chanel + " staat al aan.");
              } 
            } else {
              sender.sendMessage(ChatColor.GREEN + "Het chat kanaal " + chanel + " bestaat uit.");
              sender.sendMessage(ChatColor.RED + "Gebruik: " + ChatColor.GRAY + "roleplay, trade, hkm, dok, malzan, eredon, hyvar, tilifia of adamatium.");
            } 
          } else {
            sender.sendMessage(ChatColor.RED + "Gebruik /kingdom unmute <channel>.");
          } 
        } else if (args[0].equalsIgnoreCase("hidechannel")) {
          if (args.length == 2) {
            if (sender instanceof Player) {
              Player p = (Player)sender;
              String chanel = args[1].toLowerCase();
              ArrayList<String> chanels = new ArrayList<>();
              chanels.add("roleplay");
              chanels.add("trade");
              chanels.add("hkm");
              chanels.add("kingdom");
              if (chanels.contains(chanel)) {
                ArrayList<String> muted = Main.r.PersonChatMuted.containsKey(p) ? (ArrayList<String>)Main.r.PersonChatMuted.get(p) : new ArrayList<>();
                if (!muted.contains(chanel)) {
                  muted.add(chanel);
                  Main.r.PersonChatMuted.put(p, muted);
                  sender.sendMessage(ChatColor.GREEN + "Het chat kanaal " + chanel + " staat nu uit.");
                } else {
                  sender.sendMessage(ChatColor.GREEN + "Het chat kanaal " + chanel + " staat al uit.");
                } 
              } else {
                sender.sendMessage(ChatColor.GREEN + "Het chat kanaal " + chanel + " bestaat uit.");
                sender.sendMessage(ChatColor.RED + "Gebruik: " + ChatColor.GRAY + "roleplay, trade, hkm of kingdom.");
              } 
            } 
          } else {
            sender.sendMessage(ChatColor.RED + "Gebruik /kingdom hidechannel <channel>.");
          } 
        } else if (args[0].equalsIgnoreCase("unhidechannel")) {
          if (args.length == 2) {
            if (sender instanceof Player) {
              Player p = (Player)sender;
              String chanel = args[1].toLowerCase();
              ArrayList<String> chanels = new ArrayList<>();
              chanels.add("roleplay");
              chanels.add("trade");
              chanels.add("hkm");
              chanels.add("kingdom");
              if (chanels.contains(chanel)) {
                ArrayList<String> muted = Main.r.PersonChatMuted.containsKey(p) ? (ArrayList<String>)Main.r.PersonChatMuted.get(p) : new ArrayList<>();
                if (muted.contains(chanel)) {
                  muted.remove(chanel);
                  Main.r.PersonChatMuted.put(p, muted);
                  sender.sendMessage(ChatColor.GREEN + "Het chat kanaal " + chanel + " staat nu aan.");
                } else {
                  sender.sendMessage(ChatColor.GREEN + "Het chat kanaal " + chanel + " staat al aan.");
                } 
              } else {
                sender.sendMessage(ChatColor.GREEN + "Het chat kanaal " + chanel + " bestaat uit.");
                sender.sendMessage(ChatColor.RED + "Gebruik: " + ChatColor.GRAY + "roleplay, trade, hkm of kingdom.");
              } 
            } 
          } else {
            sender.sendMessage(ChatColor.RED + "Gebruik /kingdom unhidechannel <channel>.");
          } 
        } else if (args[0].equalsIgnoreCase("ally")) {
          if (args.length == 2) {
            String kingdom = args[1];
            if (sender instanceof Player) {
              Player p = (Player)sender;
              if (!p.hasPermission("kingdom.ally")) {
                sender.sendMessage(ChatColor.RED + "U heeft hier geen toegang voor.");
                return false;
              } 
              if (Main.r.getAllKingdoms().contains(kingdom)) {
                String ownKingdom = ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(p));
                if (kingdom.equalsIgnoreCase(ownKingdom)) {
                  p.sendMessage(ChatColor.RED + "Je kan niet jezelf als ally maken");
                  return false;
                } 
                if (Main.r.allyRequest.containsKey(kingdom) && !Main.r.c.getStringList("allies." + ownKingdom).contains(kingdom)) {
                  List<String> kingdomList = Main.r.c.getStringList("allies." + kingdom);
                  kingdomList.add(ownKingdom);
                  Main.r.c.set("allies." + kingdom, kingdomList);
                  kingdomList = Main.r.c.getStringList("allies." + ownKingdom);
                  kingdomList.add(kingdom);
                  Main.r.c.set("allies." + ownKingdom, kingdomList);
                  Main.r.saveConfig();
                  Main.r.allyRequest.remove(kingdom);
                  for (Player wp : Bukkit.getOnlinePlayers()) {
                    String inKD = ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(wp));
                    if (inKD.equalsIgnoreCase(kingdom))
                      wp.sendMessage(ChatColor.GREEN + "Jullie zijn nu ally met " + ownKingdom); 
                    if (inKD.equalsIgnoreCase(ownKingdom))
                      wp.sendMessage(ChatColor.GREEN + "Jullie zijn nu ally met " + kingdom); 
                  } 
                } else if (Main.r.allyRequest.containsValue(kingdom) && !Main.r.c.getStringList("allies." + ownKingdom).contains(kingdom)) {
                  p.sendMessage(ChatColor.RED + "Er is al een ally verzoek verstuurd naar " + kingdom + ".");
                } else if (Main.r.c.getStringList("allies." + ownKingdom).contains(kingdom)) {
                  List<String> kingdomList = Main.r.c.getStringList("allies." + kingdom);
                  kingdomList.remove(ownKingdom);
                  Main.r.c.set("allies." + kingdom, kingdomList);
                  kingdomList = Main.r.c.getStringList("allies." + ownKingdom);
                  kingdomList.remove(kingdom);
                  Main.r.c.set("allies." + ownKingdom, kingdomList);
                  Main.r.saveConfig();
                  for (Player wp : Bukkit.getOnlinePlayers()) {
                    String inKD = ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(wp));
                    if (inKD.equalsIgnoreCase(kingdom))
                      wp.sendMessage(ChatColor.RED + "Jullie zijn nu geen ally meer met " + ownKingdom); 
                    if (inKD.equalsIgnoreCase(ownKingdom))
                      wp.sendMessage(ChatColor.RED + "Jullie zijn nu geen ally meer met " + kingdom); 
                  } 
                } else {
                  p.sendMessage(ChatColor.GREEN + "Je hebt " + kingdom + " een ally verzoek verstuurd.");
                  Main.r.allyRequest.put(ownKingdom, kingdom);
                  for (Player wp : Bukkit.getOnlinePlayers()) {
                    String inKD = ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(wp));
                    if (inKD.equalsIgnoreCase(kingdom) && wp.hasPermission("kingdom.ally"))
                      wp.sendMessage(ChatColor.GREEN + ownKingdom + " heeft jullie een ally verzoek gestuurd. Met /k ally " + ownKingdom + " kan je het accepteren."); 
                  } 
                } 
              } else {
                sender.sendMessage(ChatColor.RED + "Dit kingdom bestaat niet.");
              } 
            } 
          } else {
            sender.sendMessage(ChatColor.RED + "Gebruik /kingdom ally <kingdom>.");
          } 
        } else if (args[0].equalsIgnoreCase("attack")) {
          if (args.length == 2) {
            String kingdom = args[1];
            if (sender instanceof Player) {
              Player p = (Player)sender;
              if (!p.hasPermission("kingdom.attack")) {
                sender.sendMessage(ChatColor.RED + "U heeft hier geen toegang voor.");
                return false;
              } 
              if (Main.r.getAllKingdoms().contains(kingdom)) {
                String ownKingdom = ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(p));
                String inKingdom = ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(p.getLocation().getBlock()));
                if (!kingdom.equals(ownKingdom)) {
                  if (inKingdom.equals(kingdom)) {
                    List<String> list = Main.r.c.getStringList("allies." + ownKingdom);
                    if (!list.contains(kingdom)) {
                      if (!Main.r.attacks.containsKey(ownKingdom)) {
                        Main.r.attackInventory.put(p, kingdom);
                        (new AttackMenu()).openMenu(p, kingdom);
                      } else {
                        sender.sendMessage(ChatColor.RED + "Jouw kingdom is al in oorlog met dat kingdom.");
                      } 
                    } else {
                      sender.sendMessage(ChatColor.RED + "Jullie zijn allies.");
                    } 
                  } else {
                    sender.sendMessage(ChatColor.RED + "Je moet in " + kingdom + " staan zijn om een attack aan te maken.");
                  } 
                } else {
                  sender.sendMessage(ChatColor.RED + "Een attack met je eigen kingdom? Depressie is geen optie... Praat er over. -Rijksoverheid");
                } 
              } else {
                sender.sendMessage(ChatColor.RED + "Dit kingdom bestaat niet.");
              } 
            } 
          } else {
            sender.sendMessage(ChatColor.RED + "Gebruik /kingdom attack <kingdom>.");
          } 
        } else if (args[0].equalsIgnoreCase("pvpenable")) {
          if (sender instanceof Player) {
            Player p = (Player)sender;
            if (Main.r.pvpProtection.contains(p.getUniqueId())) {
              Main.r.pvpProtection.remove(p.getUniqueId());
              sender.sendMessage(ChatColor.GREEN + "Jouw pvp protectie staat uit.");
            } else {
              sender.sendMessage(ChatColor.RED + "Je hebt geen pvp protectie aanstaan.");
            } 
          } 
        } else if (args[0].equalsIgnoreCase("attackignore")) {
          if (sender instanceof Player) {
            Player p = (Player)sender;
            if (!p.hasPermission("kingdom.attackignore")) {
              p.sendMessage(ChatColor.RED + "U heeft geen permissions hiervoor.");
              return false;
            } 
            if (!Main.r.hitAll.contains(p)) {
              Main.r.hitAll.add(p);
              sender.sendMessage(ChatColor.GREEN + "Je kan nu iedereen overal slaan.");
            } else {
              Main.r.hitAll.remove(p);
              sender.sendMessage(ChatColor.RED + "Je kan nu niet iedereen overal slaan.");
            } 
          } 
        } else if (args[0].equalsIgnoreCase("attackrestore")) {
          if (sender instanceof Player) {
            Player p = (Player)sender;
            if (!p.hasPermission("kingdom.attackrestore")) {
              p.sendMessage(ChatColor.RED + "U heeft geen permissions hiervoor.");
              return false;
            } 
          } 
          if (args.length == 3) {
            String kd = args[1];
            String points = args[2];
            if (Main.r.getAllKingdoms().contains(kd)) {
              if (Main.r.isNumeric(points) && Integer.parseInt(points) >= 0) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                String Date = dateFormat.format(date);
                Main.r.c.set(String.valueOf(kd) + "." + Date, Integer.valueOf(Integer.parseInt(points)));
                Main.r.saveConfig();
              } else {
                sender.sendMessage(ChatColor.RED + "Geen geldig aantal punten");
              } 
            } else {
              sender.sendMessage(ChatColor.RED + "Dat kingdom bestata niet.");
            } 
          } else {
            sender.sendMessage(ChatColor.RED + "Gebruikt: /kingdom attackrestore <kingdom> <punten>");
          } 
        } else {
          sender.sendMessage(ChatColor.GOLD + "--- �f[�cPhyKingdom hulp�f] �6---");
          sender.sendMessage(ChatColor.GRAY + "/k promote <player>");
          sender.sendMessage(ChatColor.GRAY + "/k demote <player>");
          sender.sendMessage(ChatColor.GRAY + "/k setrank <player> <rank number>");
          sender.sendMessage(ChatColor.GRAY + "/k setprefix <player> <prefix>");
          sender.sendMessage(ChatColor.GRAY + "/k give <player> <crate>");
          sender.sendMessage(ChatColor.GRAY + "/k setkingdom <player> <kingdom>");
          sender.sendMessage(ChatColor.GRAY + "/k staffmode <player>");
          sender.sendMessage(ChatColor.GRAY + "/k setcap <koth> <time>");
          sender.sendMessage(ChatColor.GRAY + "/k kothstart <koth>");
          sender.sendMessage(ChatColor.GRAY + "/k kothstop <koth>");
          sender.sendMessage(ChatColor.GRAY + "/k tphere <kingdom>");
          sender.sendMessage(ChatColor.GRAY + "/k tp <player>");
          sender.sendMessage(ChatColor.GRAY + "/k influenceignore");
          sender.sendMessage(ChatColor.GRAY + "/k joinchannel <channel>");
          sender.sendMessage(ChatColor.GRAY + "/k mute <channel>");
          sender.sendMessage(ChatColor.GRAY + "/k unmute <channel>");
          sender.sendMessage(ChatColor.GRAY + "/k hidechannel <channel>");
          sender.sendMessage(ChatColor.GRAY + "/k unhidechannel <channel>");
          sender.sendMessage(ChatColor.GRAY + "/k ally <kingdom>");
          sender.sendMessage(ChatColor.GRAY + "/k attack <kingdom>");
          sender.sendMessage(ChatColor.GRAY + "/k pvpenable");
          sender.sendMessage(ChatColor.GRAY + "/k attackrestore <kingdom> <punten>");
          sender.sendMessage(ChatColor.GRAY + "/k attackignore");
          sender.sendMessage("�f�lPlugin auteur: Phytion");
        } 
      } else {
        sender.sendMessage(ChatColor.RED + "Gebruik /kingdom help");
      }  

    if (cmd.getName().equalsIgnoreCase("stats"))
      if (args.length == 1) {
        String speler = args[0];
        if (Bukkit.getPlayer(speler) != null) {
          Player p = Bukkit.getPlayer(speler);
          int deaths = Main.r.c.contains("users." + p.getUniqueId().toString() + ".deaths") ? Main.r.c.getInt("users." + p.getUniqueId().toString() + ".deaths") : 0;
          int kills = Main.r.c.contains("users." + p.getUniqueId().toString() + ".kills") ? Main.r.c.getInt("users." + p.getUniqueId().toString() + ".kills") : 0;
          sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.r.c.getString("stats.format_top").replace("%player%", p.getName())));
          sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.r.c.getString("stats.format_deaths").replace("%deaths%", (new StringBuilder(String.valueOf(deaths))).toString())));
          sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.r.c.getString("stats.format_kills").replace("%kills%", (new StringBuilder(String.valueOf(kills))).toString())));
        } else {
          sender.sendMessage(ChatColor.RED + "Die speler bestaat niet.");
        } 
      } else {
        sender.sendMessage(ChatColor.RED + "Gebruik: /stats <speler>");
      }  
    return false;
  }
}
