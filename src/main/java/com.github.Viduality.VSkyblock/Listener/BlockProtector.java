package com.github.Viduality.VSkyblock.Listener;

import com.github.Viduality.VSkyblock.Utilitys.IslandCacheHandler;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;


public class BlockProtector implements Listener {


    @EventHandler
    public void onBlockBreak(BlockBreakEvent blockBreakEvent) {
        Player player = blockBreakEvent.getPlayer();
        if (!player.hasPermission("VSkyblock.IgnoreProtected")) {
            if (!(player.getWorld().getEnvironment().equals(World.Environment.NETHER) || player.getWorld().getName().equals(IslandCacheHandler.playerislands.get(player.getUniqueId())))) {
                blockBreakEvent.setCancelled(true);
                ConfigShorts.messagefromString("BlockBreak", player);
            } else {
                if (blockBreakEvent.getBlock().getType().equals(Material.END_PORTAL_FRAME)) {
                    blockBreakEvent.setDropItems(true);
                }
            }
        }
    }


    @EventHandler
    public void onBlockPlace(BlockPlaceEvent blockPlaceEvent) {
        Player player = blockPlaceEvent.getPlayer();
        if (!player.hasPermission("VSkyblock.IgnoreProtected")) {
            if (!(player.getWorld().getEnvironment().equals(World.Environment.NETHER) || player.getWorld().getName().equals(IslandCacheHandler.playerislands.get(player.getUniqueId())))) {
                blockPlaceEvent.setCancelled(true);
                ConfigShorts.messagefromString("BlockPlace", player);
            }
        }
    }



    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent playerBucketEmptyEvent) {
        Player player = playerBucketEmptyEvent.getPlayer();
        if (!player.hasPermission("VSkyblock.IgnoreProtected")) {
            if (!(player.getWorld().getEnvironment().equals(World.Environment.NETHER) || player.getWorld().getName().equals(IslandCacheHandler.playerislands.get(player.getUniqueId())))) {
                playerBucketEmptyEvent.setCancelled(true);
                ConfigShorts.messagefromString("BlockPlace", player);
            }
        }
    }



    @EventHandler
    public void onPlayerBucketFill(PlayerBucketFillEvent playerBucketFillEvent) {
        Player player = playerBucketFillEvent.getPlayer();
        if (!player.hasPermission("VSkyblock.IgnoreProtected")) {
            if (!(player.getWorld().getEnvironment().equals(World.Environment.NETHER) || player.getWorld().getName().equals(IslandCacheHandler.playerislands.get(player.getUniqueId())))) {
                playerBucketFillEvent.setCancelled(true);
                ConfigShorts.messagefromString("BlockBreak", player);
            }
        }
    }

    @EventHandler
    public void onPlayerFrostWalk(EntityBlockFormEvent entityBlockFormEvent) {
        if (entityBlockFormEvent.getEntity() instanceof Player player) {
            if (!(player.getWorld().getEnvironment().equals(World.Environment.NETHER) || player.getWorld().getName().equals(IslandCacheHandler.playerislands.get(player.getUniqueId())))) {
                entityBlockFormEvent.setCancelled(true);
            }
        }
    }
}
