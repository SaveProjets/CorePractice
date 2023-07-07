package fr.edminecoreteam.corepractice.matchduels;

import fr.edminecoreteam.corepractice.Core;
import fr.edminecoreteam.corepractice.kits.LoadKits;
import fr.edminecoreteam.corepractice.listeners.ItemListeners;
import fr.edminecoreteam.corepractice.matchmaking.GameCheck;
import fr.edminecoreteam.corepractice.utils.LoadWorld;
import fr.edminecoreteam.corepractice.utils.UnloadWorld;
import org.bukkit.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.concurrent.ThreadLocalRandom;

public class GameListeners implements Listener
{
    private static Core core = Core.getInstance();
    public static void startGame(Player p1, Player p2)
    {
        core.getInWaiting().remove(p1);
        core.getInWaiting().remove(p2);
        core.getInLobby().remove(p1);
        core.getInLobby().remove(p2);

        int randomID = ThreadLocalRandom.current().nextInt(100000000, 999999999);

        core.getMatchOppenant().putMatchOppenant(p1, p2);
        core.getMatchOppenant().putMatchOppenant(p2, p1);

        core.getGameType().putInGame(p1, core.getGameCheck().getGame(p1));
        core.getGameType().putInGame(p2, core.getGameCheck().getGame(p2));

        core.getGameID().putGameID(p1, randomID);
        core.getGameID().putGameID(p2, randomID);

        core.getInDuel().add(p1);
        core.getInDuel().add(p2);

        String world = LoadWorld.getRandomSubfolderName("gameTemplate/");
        LoadWorld.createGameWorld(world, core.getGameID().getIDString(p1));
        core.getWorldName().putWorldName(p1, world);
        core.getWorldName().putWorldName(p2, world);

        Location p1Spawn = new Location(Bukkit.getWorld(core.getGameID().getIDString(p1)),
                (float) core.getConfig().getDouble("Arenas." + core.getWorldName().getWorldName(p1) + ".team1.x")
                , (float) core.getConfig().getDouble("Arenas." + core.getWorldName().getWorldName(p1) + ".team1.y")
                , (float) core.getConfig().getDouble("Arenas." + core.getWorldName().getWorldName(p1) + ".team1.z")
                , (float) core.getConfig().getDouble("Arenas." + core.getWorldName().getWorldName(p1) + ".team1.t")
                , (float) core.getConfig().getDouble("Arenas." + core.getWorldName().getWorldName(p1) + ".team1.b"));

        Location p2Spawn = new Location(Bukkit.getWorld(core.getGameID().getIDString(p2)),
                (float) core.getConfig().getDouble("Arenas." + core.getWorldName().getWorldName(p2) + ".team2.x")
                , (float) core.getConfig().getDouble("Arenas." + core.getWorldName().getWorldName(p2) + ".team2.y")
                , (float) core.getConfig().getDouble("Arenas." + core.getWorldName().getWorldName(p2) + ".team2.z")
                , (float) core.getConfig().getDouble("Arenas." + core.getWorldName().getWorldName(p2) + ".team2.t")
                , (float) core.getConfig().getDouble("Arenas." + core.getWorldName().getWorldName(p2) + ".team2.b"));

        p1.teleport(p1Spawn);
        p2.teleport(p2Spawn);

        p1.setGameMode(GameMode.SURVIVAL);
        p2.setGameMode(GameMode.SURVIVAL);

        p1.setAllowFlight(false);
        p1.setFlying(false);

        p2.setAllowFlight(false);
        p2.setFlying(false);

        LoadKits p1Kit = new LoadKits(p1);
        LoadKits p2Kit = new LoadKits(p2);

        p1Kit.equipUnrankedDefaultKit(core.getGameCheck().getGame(p1));
        p2Kit.equipUnrankedDefaultKit(core.getGameCheck().getGame(p2));

        core.getGameCheck().removeSerchGame(p1);
        core.getGameCheck().removeSerchGame(p2);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e)
    {
        Player pDeathEvent = (Player) e.getEntity();
        Location pDeathLoc = pDeathEvent.getLocation();
        String worldName = core.getWorldName().getWorldName(pDeathEvent);
        if (core.getGameID() != null)
        {
            if (core.getInDuel().contains(pDeathEvent))
            {
                e.getDrops().clear();
                e.getEntity().spigot().respawn();

                Player pVictory = core.getMatchOppenant().getMatchOppenant(pDeathEvent);
                Player pDeath = core.getMatchOppenant().getMatchOppenant(pVictory);

                pDeath.sendTitle("§c§lDéfaite...", "§7Peut-être une prochaine fois.");
                pDeath.playSound(pVictory.getLocation(), Sound.VILLAGER_NO, 1.0f, 1.0f);

                pVictory.sendTitle("§a§lVictoire !", "§7C'étais moins une !");
                pVictory.playSound(pVictory.getLocation(), Sound.FIREWORK_LAUNCH, 1.0f, 1.0f);

                core.getInDuel().remove(pVictory);
                core.getInLobby().add(pVictory);

                core.getInDuel().remove(pDeath);
                core.getInLobby().add(pDeath);

                endGame(pVictory);

                pDeath.teleport(pDeathLoc);
                endGame(pDeath);

                Bukkit.getScheduler().runTaskLater(core, () -> {

                    for (Player pLeaves : core.getServer().getWorld(worldName).getPlayers())
                    {
                        leaveGame(pLeaves);
                    }

                    UnloadWorld.deleteWorld(core.getGameID().getIDString(pVictory));

                    core.getGameID().removeFromGameID(pVictory);
                    core.getGameID().removeFromGameID(pDeath);
                }, 100);
            }
        }
    }

    public static void leaveGame(Player p)
    {
        core.getWorldName().removeWorldName(p);
        core.getGameType().removeFromTypeGame(p);

        Location lobbySpawn = new Location(Bukkit.getWorld(core.getConfig().getString("Lobby.world")),
                (float) core.getConfig().getDouble("Lobby.x")
                , (float) core.getConfig().getDouble("Lobby.y")
                , (float) core.getConfig().getDouble("Lobby.z")
                , (float) core.getConfig().getDouble("Lobby.t")
                , (float) core.getConfig().getDouble("Lobby.b"));

        p.getActivePotionEffects().removeAll(p.getActivePotionEffects());
        p.setGameMode(GameMode.ADVENTURE);
        p.setFoodLevel(20);
        p.teleport(lobbySpawn);

        ItemListeners.getLobbyItems(p);
    }

    public static void endGame(Player p)
    {
        Bukkit.getScheduler().runTaskLater(core, () -> {

            p.getActivePotionEffects().removeAll(p.getActivePotionEffects());
            p.getInventory().setHelmet(null);
            p.getInventory().setChestplate(null);
            p.getInventory().setLeggings(null);
            p.getInventory().setBoots(null);

            p.setAllowFlight(true);
            p.setFlying(true);
            p.setGameMode(GameMode.ADVENTURE);
            ItemListeners.getEndUnrankedItems(p);

        }, 3);
    }
}