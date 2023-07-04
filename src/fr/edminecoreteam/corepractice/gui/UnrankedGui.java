package fr.edminecoreteam.corepractice.gui;

import fr.edminecoreteam.corepractice.Core;
import fr.edminecoreteam.corepractice.listeners.ItemListeners;
import fr.edminecoreteam.corepractice.matchmaking.UnrankedMatchMaking;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class UnrankedGui implements Listener
{
    private static Core core = Core.getInstance();

    @EventHandler
    public void inventoryClick(InventoryClickEvent e) {
        Player p = (Player)e.getWhoClicked();
        ItemStack it = e.getCurrentItem();
        if (it == null) {
            return;
        }
        if (it.getType() == Material.IRON_SWORD && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§f§lUnranked §7• Clique")) { e.setCancelled(true); }
        if (e.getView().getTopInventory().getTitle().equals("§8Unranked")) {
            if (it.getType() == Material.STAINED_GLASS_PANE) { e.setCancelled(true); }


            if (it.getType() == Material.PAPER && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§d§lNodebuff"))
            {
                e.setCancelled(true);
                UnrankedMatchMaking matchMaking = new UnrankedMatchMaking(p);

                matchMaking.start("nodebuff");
                ItemListeners.foundGameItems(p);
            }
        }
    }

    public static void gui(Player p) {

        Inventory inv = Bukkit.createInventory(null, 54, "§8Unranked");

        ItemStack deco = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)0);
        ItemMeta decoM = deco.getItemMeta();
        decoM.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
        decoM.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
        decoM.setDisplayName("§r");
        deco.setItemMeta(decoM);
        inv.setItem(0, deco); inv.setItem(8, deco); inv.setItem(9, deco); inv.setItem(17, deco);
        inv.setItem(45, deco); inv.setItem(53, deco); inv.setItem(36, deco); inv.setItem(44, deco);

        /*ItemStack blocs = new ItemStack(Material.NAME_TAG, 1);
        ItemMeta blocsM = blocs.getItemMeta();
        blocsM.setDisplayName("§a§lVos blocs");
        ArrayList<String> loreblocs = new ArrayList<String>();
        loreblocs.add("");
        loreblocs.add(" §dInformation:");
        loreblocs.add(" §f▶ §7Ici, visionnez vos articles");
        loreblocs.add(" §f▶ §7débloqués et utilisez-les !");
        loreblocs.add("");
        blocsM.setLore(loreblocs);
        blocs.setItemMeta(blocsM);
        inv.setItem(19, blocs);*/

        p.openInventory(inv);
        p.playSound(p.getLocation(), Sound.HORSE_ARMOR, 1.0f, 1.0f);
        new BukkitRunnable() {
            int t = 0;
            public void run() {

                if (!p.getOpenInventory().getTitle().contains("§8Choix de votre bloc")) { cancel(); }

                ++t;
                if (t == 10) {
                    t = 0;
                }
            }
        }.runTaskTimer((Plugin)core, 0L, 10L);
    }
}
