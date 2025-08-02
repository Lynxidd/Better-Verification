package me.lynxid.betterVerification.files;

import me.lynxid.betterVerification.discordcommands.DiscordEvents;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Objects;

import static me.lynxid.betterVerification.files.ServerMessages.startMessage;
import static me.lynxid.betterVerification.files.ServerMessages.statusNotConfiguredError;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

public class DiscordFile {

    public static File userFiles;
    public static JDA jda;
    private static Plugin p;
    private static Activity sActivity;
    private static String sText;

    public static void checkDirectory() {
        // Check whether the file directory exists
        userFiles = new File(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin("Better-Verification")).getDataFolder(), File.separator + "UserFiles");
        if (!userFiles.exists()) {
            // If the file doesn't exist then create it
            p.getLogger().info("[Better-Verification] Discord UserFiles not found, attempting to recreate");
            if (!userFiles.mkdir()) {
                p.getLogger().severe("[Better-Verification] Directory already exists!");
            }

        }
    }

    public static void startUp() {
        // Path for accessing plugin
        p = Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("BetterVerification"));
        p.getLogger().info("[Better-Verification] Loading Discord...");
        DiscordFile.checkDirectory();
        // Get Discord settings
        String token = p.getConfig().getString("token");


        // Set up the Discord bot
        p.getLogger().info("[Better-Verification] Loading discord connections...");
        try {
            // Build bot's main instance
            jda = JDABuilder.createDefault(token, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_MEMBERS)
                    .setActivity(Activity.customStatus("Loading Better Verification...")) // Temporary status until the bot is finished loading
                    .addEventListeners(new DiscordEvents()) // Listener for Commands and other Events
                    .setMemberCachePolicy(MemberCachePolicy.ALL.and(MemberCachePolicy.lru(1000)))
                    .build();
            p.getLogger().info("[Better-Verification] Discord bot connected!");

            p.getLogger().info("[Better-Verification] Loading Discord commands...");
            // Register Commands
            CommandListUpdateAction commands = jda.updateCommands();
            commands.addCommands(
                    Commands.slash("verify", "Whitelist and Verify your Minecraft account!")
                            .addOption(STRING, "username", "Your Minecraft username", true)
                            .setGuildOnly(true)
            ).queue();

            // Wait for users
            jda.awaitReady();
            // Call for custom status
            DiscordFile.status();

            // Call for DiscordMessages to try to send the startup message (if enabled)
            startMessage();

        } catch (InvalidTokenException e) {
            p.getLogger().severe("[Better-Verification] The token in discord-config.yml is invalid! Please change the token in the config and restart or reload the server to apply the changes.");
        } catch (InterruptedException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    public static void status() {
        // Get Status text
        String sText = p.getConfig().getString("status-text");
        if (sText == null) {
            statusNotConfiguredError(1);
            return;
        } else if (sText.length() > 128) {
            statusNotConfiguredError(2);
            return;
        }

        // Get Status activity type
        int sTypeInt = p.getConfig().getInt("status-type");

        if (sTypeInt >= 0 && sTypeInt <= 4) {
            switch (sTypeInt) {
                case 0:
                    jda.getEventManager().register(Activity.customStatus(sText));
                case 1:
                    jda.getEventManager().register(Activity.playing(sText));
                case 2:
                    jda.getEventManager().register(Activity.watching(sText));
                case 3:
                    jda.getEventManager().register(Activity.listening(sText));
                case 4:
                    jda.getEventManager().register(Activity.competing(sText));
            }
        } else {
            statusNotConfiguredError(3);
        }
    }
}

