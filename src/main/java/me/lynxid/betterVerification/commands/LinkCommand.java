package me.lynxid.betterVerification.commands;

import me.lynxid.betterVerification.files.DiscordFile;
import net.dv8tion.jda.api.JDA;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import static org.bukkit.Bukkit.getLogger;

public class LinkCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] strings) {

        JDA j = DiscordFile.jda;
        String logo = RulesFile.get().getString("logo");

        if (strings.length == 0) {
            sender.sendMessage("Usage: /link <Your Discord username> (do not put your display name)");
            return true;
        }

        if (strings.length == 1) {
            Player p = (Player) sender;
            String playerN = p.getDisplayName();
            UUID playerId = p.getUniqueId();

            File file = new File(userData, File.separator + playerId + ".yml");
            FileConfiguration playerData = YamlConfiguration.loadConfiguration(file);

            User discordUser;

            try {
                discordUser = j.getUsersByName(strings[0], true).getFirst();
            } catch (Exception e) {
                getLogger().info("[Better-Verification]" + playerN + " tried to run '/link' and put in a invalid username! String: " + strings[0]);
                sender.sendMessage(" ");
                sender.sendMessage(" ");
                sender.sendMessage(" ");
                sender.sendMessage(Objects.requireNonNull(logo) + " ");
                sender.sendMessage(" ");
                sender.sendMessage(" ");
                sender.sendMessage(ChatColor.DARK_AQUA + "WafflesSMP - Error Warning!");
                sender.sendMessage(" ");
                sender.sendMessage("""
                        You have inputted a invalid Discord username!
                        \n
                        Please make sure you are placing your Discord **username** not your display name!
                        \n
                        If you think you have put in your correct username and it is still not working please contact our administrators!!""");
                return true;
            }

            String discordName = discordUser.getName();
            String discordId = discordUser.getId();


            try {
                playerData.load(file);
                playerData.set("Discord ID", discordId);
                playerData.save(file);
                sender.sendMessage(ChatColor.GREEN + "Linked Minecraft account " + playerN + " to Discord account " + discordName);
            } catch (IOException | InvalidConfigurationException i) {
                getLogger().severe(i.toString());
            }

        }
        return true;
    }
}
