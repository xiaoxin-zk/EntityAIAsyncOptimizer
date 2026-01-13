package com.example.plugin.commands;

import com.example.plugin.MyLeavesPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class MyCommandExecutor implements CommandExecutor {

    private final MyLeavesPlugin plugin;

    public MyCommandExecutor(MyLeavesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.sendMessage("你好! 这是来自MyLeavesPlugin的问候!");
            player.sendMessage("你的位置: " + player.getLocation());
        } else {
            sender.sendMessage("控制台不能使用此命令!");
        }
        return true;
    }
}