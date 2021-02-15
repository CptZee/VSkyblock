package com.github.Viduality.VSkyblock.Commands;

/*
 * VSkyblock
 * Copyright (C) 2020  Viduality
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.entity.Player;

public class IslandLeaveConfirm implements SubCommand {

    private final VSkyblock plugin;

    public IslandLeaveConfirm(VSkyblock plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(DatabaseCache databaseCache) {
        Player player = databaseCache.getPlayer();
        if (Island.leavemap.asMap().containsKey(player.getUniqueId())) {
            plugin.getDb().getWriter().leavefromIsland(player.getUniqueId());
            ConfigShorts.messagefromString("LeftIsland", player);
            player.getInventory().clear();
            player.getEnderChest().clear();
            player.setExp(0);
            player.setTotalExperience(0);
            player.setScoreboard(plugin.getServer().getScoreboardManager().getMainScoreboard());
            Island.leavemap.asMap().remove(player.getUniqueId());
            Island.playerislands.remove(player.getUniqueId());
            if (!Island.playerislands.containsValue(databaseCache.getIslandname())) {
                plugin.getWorldManager().unloadWorld(databaseCache.getIslandname());
            }
        } else {
            ConfigShorts.messagefromString("LeaveFirst", player);
        }
    }
}
