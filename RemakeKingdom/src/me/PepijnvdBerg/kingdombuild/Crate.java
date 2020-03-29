package me.PepijnvdBerg.kingdombuild;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Crate
{
  private String name;
  private String title;
  private ArrayList<String> lore = new ArrayList<String>();
  
  public Crate(String name)
  {
    if (Main.r.c.contains("crate." + name))
    {
      this.name = name;
      
      this.title = ChatColor.translateAlternateColorCodes('&', Main.r.c.getString("crate." + name + ".title"));
      for (String str : Main.r.c.getStringList("crate." + name + ".lore")) {
        this.lore.add(ChatColor.translateAlternateColorCodes('&', str));
      }
    }
  }
  
  public void giveCrate(Player p, int amount)
  {
    ItemStack i = new ItemStack(Material.CHEST, amount);
    ItemMeta m = i.getItemMeta();
    m.setDisplayName(this.title);
    m.setLore(this.lore);
    i.setItemMeta(m);
    
    p.getInventory().addItem(new ItemStack[] { i });
  }
  
  public String getTitle()
  {
    return this.title;
  }
  
  public void giveReward(Player p)
  {
    removeInventoryItems(p, Material.CHEST, this.title, 1);
    Random ra = new Random();
    
    List<String> list = Main.r.c.getStringList("crate." + this.name + ".rewards");
    
    String reward = (String)list.get(ra.nextInt(list.size()));
    
    ItemStack i = new ItemStack(Material.valueOf(Main.r.c.getString("crate." + this.name + ".reward." + reward + ".type").toUpperCase()), Main.r.c.getInt("crate." + this.name + ".reward." + reward + ".amount"), (byte)Main.r.c.getInt("crate." + this.name + ".reward." + reward + ".data"));
    ItemMeta m = i.getItemMeta();
    m.setDisplayName(ChatColor.translateAlternateColorCodes('&', Main.r.c.getString("crate." + this.name + ".reward." + reward + ".title")));
    
    ArrayList<String> lore = new ArrayList<String>();
    for (String str : Main.r.c.getStringList("crate." + this.name + ".reward." + reward + ".lore")) {
      lore.add(ChatColor.translateAlternateColorCodes('&', str));
    }
    m.setLore(lore);
    i.setItemMeta(m);
    
    p.getInventory().addItem(new ItemStack[] { i });
  }
  
  private void removeInventoryItems(Player p, Material type, String title, int amount)
  {
    ItemStack[] arrayOfItemStack;
    int j = (arrayOfItemStack = p.getInventory().getContents()).length;
    for (int i = 0; i < j; i++)
    {
      ItemStack is = arrayOfItemStack[i];
      if ((is != null) && (is.getType() == type) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(title)))
      {
        if (is.getAmount() > amount)
        {
          is.setAmount(is.getAmount() - amount);
          p.updateInventory();
          break;
        }
        if (is.getAmount() == amount)
        {
          p.getInventory().remove(is);
          break;
        }
        if (is.getAmount() < amount)
        {
          amount -= is.getAmount();
          p.getInventory().remove(is);
        }
      }
    }
  }
}
