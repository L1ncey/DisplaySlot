package dev.nevermind.baimao.listener;

import dev.nevermind.baimao.Main;
import gg.noob.practice.match.Match;
import gg.noob.practice.match.event.MatchEndEvent;
import gg.noob.practice.match.event.MatchStartEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

public class MatchListener implements Listener {
    @EventHandler
    public void MatchStartEvent(MatchStartEvent event) {
        Match match = event.getMatch();

        if(match.getKitType().isHealthShown()) return;

        Main.getInstance().getDisplaySlotHandler().setup(match);
    }

    @EventHandler
    public void MatchEndEvent(MatchEndEvent event) {
        Match match = event.getMatch();

        if(match.getKitType().isHealthShown()) return;

        for(UUID uuid : match.getAllMembers()) {
            Player player = Bukkit.getPlayer(uuid);

            if(player == null) continue;

            Main.getInstance().getDisplaySlotHandler().clearAll(player);
        }
    }
}
