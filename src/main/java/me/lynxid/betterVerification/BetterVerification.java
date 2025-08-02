package me.lynxid.betterVerification;

import me.lynxid.betterVerification.files.DiscordFile;
import me.lynxid.betterVerification.files.LanguageFile;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class BetterVerification extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        FileConfiguration lang; // Language access shortcut
        String signature; // Minecraft log prefix

        getLogger().info("[Better-Verification] Starting up...");
        saveDefaultConfig(); // Save the configuration

        getLogger().info("[Better-Verification] Loading language file...");
        LanguageFile.startUp(); // Load the language file
        lang = LanguageFile.get(); // Define language access shortcut
        signature = lang.getString("log-signature"); // Define Minecraft log prefix

        DiscordFile.startUp(); // Start the Discord bot

        getServer().getPluginManager().registerEvents(this, this); // Registered this file as a listening class
//        Objects.requireNonNull(getCommand("link")).setExecutor(new LinkCommand());

        getLogger().info(signature + lang.getString("start-final")); // Announce that the plugin has started
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
