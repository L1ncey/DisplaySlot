package dev.nevermind.baimao.DisplaySlot.menu;

import dev.nevermind.baimao.DisplaySlot.DisplaySlotEntry;
import dev.nevermind.baimao.DisplaySlot.DisplaySlotHandler;
import dev.nevermind.baimao.Main;
import dev.nevermind.baimao.MongoDB.MongodbProfile;
import dev.nevermind.baimao.Utils.menu.menu.Button;
import dev.nevermind.baimao.Utils.menu.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisplaySlotSelectMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return ChatColor.YELLOW +  "DisplaySlot";
    }

    @AllArgsConstructor
    private static class DisplaySlotButton extends Button {

        private final DisplaySlotEntry displaySlotEntry;

        @Override
        public String getName(Player player) {
            return displaySlotEntry.getName();
        }

        @Override
        public List<String> getDescription(Player player) {
            List<String> strings = new ArrayList<>();

            MongodbProfile profile = Main.getInstance().getMongodbManager().getProfile(player);

            String permission = profile.isPermission(player, displaySlotEntry.getName()) ? ChatColor.GREEN + "true" : ChatColor.RED + "false";
            String using = Main.getInstance().getMongodbManager().getProfile(player).getUsed().equals(displaySlotEntry.getName()) ? ChatColor.GREEN + "true" : ChatColor.RED + "false";
            strings.add("");
            strings.add(ChatColor.translateAlternateColorCodes('&', "Title: " + displaySlotEntry.getTitle()));
            strings.add("");
            strings.add(ChatColor.AQUA + "Permission: " + permission + ChatColor.AQUA + " Using: " + using);

            return strings;
        }

        @Override
        public Material getMaterial(Player player) {
            MongodbProfile profile = Main.getInstance().getMongodbManager().getProfile(player);

            return profile.isPermission(player, displaySlotEntry.getName()) ? Material.EMERALD : Material.REDSTONE_BLOCK;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {

            MongodbProfile profile = Main.getInstance().getMongodbManager().getProfile(player);

            if(!profile.isPermission(player, displaySlotEntry.getName())) {
                player.sendMessage(ChatColor.RED + "对不起你没有权限使用");
                return;
            }

            if(profile.getUsed().equals(displaySlotEntry.getName())) {
                profile.setUsed("none");
                profile.MongodbSave();
                player.sendMessage(ChatColor.GREEN + "取消成功！");
                return;
            }

            profile.setUsed(displaySlotEntry.getName());
            profile.MongodbSave();
            player.sendMessage(ChatColor.GREEN + "设置成功！");
        }
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();
        DisplaySlotHandler displaySlotHandler = Main.getInstance().getDisplaySlotHandler();
        int i = 0;

        for(DisplaySlotEntry displaySlotEntry : displaySlotHandler.getDisplaySlotEntry()) {
            buttonMap.put(i, new DisplaySlotButton(displaySlotEntry));
            ++i;
        }

        return buttonMap;
    }
}
