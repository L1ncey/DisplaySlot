package dev.nevermind.baimao.DisplaySlot.command;

import dev.nevermind.baimao.DisplaySlot.DisplaySlotEntry;
import dev.nevermind.baimao.DisplaySlot.menu.DisplaySlotSelectMenu;
import dev.nevermind.baimao.Main;
import dev.nevermind.baimao.MongoDB.MongodbProfile;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.C;

import java.util.Arrays;
import java.util.stream.Collectors;

public class DisplaySlotCommand extends Command {
    public DisplaySlotCommand(String name) {
        super(name);
        this.usageMessage = "/ds help | /displayslot help";
        this.setAliases(Arrays.asList("ds", "displayslot"));
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player p;
        MongodbProfile mongodbProfile;
        DisplaySlotEntry displaySlotEntry;

        switch (args.length) {
            case 0:
            if(!(commandSender instanceof Player)) {
                return false;
            }

            Player player = (Player) commandSender;
            new DisplaySlotSelectMenu().openMenu(player);

            return true;
            case 1:
                if (args[0].equalsIgnoreCase("help")) {

                    if (!commandSender.hasPermission("ds.admin")) return false;

                    commandSender.sendMessage("/displayslot");
                    commandSender.sendMessage("/displayslot create <Name> <DisplayName> <level>");
                    commandSender.sendMessage("/displayslot delete <Name>");
                    commandSender.sendMessage("/displayslot give <PlayerName> <Name>");
                    commandSender.sendMessage("/displayslot remove <PlayerName> <Name>");
                    commandSender.sendMessage("/displayslot set <Name> <DisplayName> <level>");
                    return true;
                }
            case 2:
                if (args[0].equalsIgnoreCase("delete")) {

                    if (!commandSender.hasPermission("ds.admin")) return false;

                    displaySlotEntry = Main.getInstance().getDisplaySlotHandler().getDisplaySlot(args[1]);

                    if (displaySlotEntry == null) {
                        commandSender.sendMessage(ChatColor.RED + "插槽不存在！");
                        return false;
                    }

                    Main.getInstance().getDisplaySlotHandler().remove(args[1]);
                    commandSender.sendMessage(ChatColor.GREEN + "操作成功!");
                    return true;
                }
            case 3:
                if(args[0].equalsIgnoreCase("give")) {

                    if (!commandSender.hasPermission("ds.admin")) return false;

                    p = Bukkit.getPlayer(args[1]);
                    mongodbProfile = Main.getInstance().getMongodbManager().getProfile(p);
                    if(p == null) {
                        commandSender.sendMessage(ChatColor.RED + "玩家不在线");
                        return false;
                    } else if(Main.getInstance().getDisplaySlotHandler().getDisplaySlot(args[2]) == null) {
                        commandSender.sendMessage(ChatColor.RED + "不存在此插槽");
                        return false;
                    } else if (mongodbProfile.isPermission(p, args[2])) {
                        commandSender.sendMessage(ChatColor.RED + "玩家已拥有此插槽");
                        return false;
                    }

                    mongodbProfile.getObtained().add(args[2]);
                    mongodbProfile.MongodbSave();
                    commandSender.sendMessage(ChatColor.GREEN + "操作成功!");
                    return true;
                }
            case 4:
                if(args[0].equalsIgnoreCase("create")) {

                    if (!commandSender.hasPermission("ds.admin")) return false;

                    if(args[1].equals("none")) {
                        commandSender.sendMessage(ChatColor.RED + "创建失败");
                        return false;
                    }
                    else if(Main.getInstance().getDisplaySlotHandler().getDisplaySlot(args[1]) != null) {
                        commandSender.sendMessage(ChatColor.RED + "存在此插槽");
                        return false;
                    } else if (!NumberUtils.isNumber(args[3])) {
                        commandSender.sendMessage(ChatColor.RED + "非法输入");
                        return false;
                    } else if (Integer.parseInt(args[3]) > 10 || Integer.parseInt(args[3]) <= 0) {
                        commandSender.sendMessage(ChatColor.RED + "等级大小 1 至 10 范围内");
                        return false;
                    }

                    displaySlotEntry = new DisplaySlotEntry(args[1], args[2], Integer.parseInt(args[3]), "ds." + args[1]);
                    Main.getInstance().getDisplaySlotHandler().getDisplaySlotEntry().add(displaySlotEntry);
                    Main.getInstance().getDisplaySlotHandler().save(displaySlotEntry);
                    commandSender.sendMessage(ChatColor.GREEN + "创建成功");

                    return true;
                }
                if(args[0].equalsIgnoreCase("set")) {

                    if (!commandSender.hasPermission("ds.admin")) return false;

                    if(Main.getInstance().getDisplaySlotHandler().getDisplaySlot(args[1]) == null) {
                        commandSender.sendMessage(ChatColor.RED + "不存在此插槽");
                        return false;
                    } else if (!NumberUtils.isNumber(args[3])) {
                        commandSender.sendMessage(ChatColor.RED + "非法输入");
                        return false;
                    } else if (Integer.parseInt(args[3]) > 10 || Integer.parseInt(args[3]) <= 0) {
                        commandSender.sendMessage(ChatColor.RED + "等级大小 1 至 10 范围内");
                        return false;
                    }

                    Main.getInstance().getDisplaySlotHandler().getDisplaySlot(args[1]).setTitle(args[2]);
                    Main.getInstance().getDisplaySlotHandler().getDisplaySlot(args[1]).setLevel(Integer.parseInt(args[3]));
                    Main.getInstance().getDisplaySlotHandler().save(Main.getInstance().getDisplaySlotHandler().getDisplaySlot(args[1]));
                    commandSender.sendMessage(ChatColor.GREEN + "设置成功");

                    return true;
                }
        }
        return false;
    }
}
