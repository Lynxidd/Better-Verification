package me.lynxid.betterVerification.files;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Objects;

import static me.lynxid.betterVerification.files.ServerMessages.langaugeNotConfiguredError;

public class LanguageFile {
    private static Plugin p;
    private static File file;
    private static FileConfiguration langFile;
    private static String lKey;

    public static void startUp() {
        p = Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("BetterVerification"));
        p.getLogger().severe("[Better-Verification] Saving default language file!");
        p.saveResource("languages/en_US.yml",true);

        lKey = p.getConfig().getString("language");

        p.getLogger().severe("[Better-Verification] Checking language key...");
        if (lKey == null) {
            p.getConfig().set("language", "en_US");
            langaugeNotConfiguredError(1);
            lKey = p.getConfig().getString("language");
        }

        p.getLogger().severe("[Better-Verification] language key loaded! Loading language file...");
        String lKeyFinal = "languages/" + lKey + ".yml";

        file = new File(p.getDataFolder(), lKeyFinal);

        if (!file.exists()) {
            if (!lKey.equals("en_US")) {
                p.getConfig().set("language", "en_US");
                langaugeNotConfiguredError(2);
                return;
            }
            p.saveResource("languages/en_US.yml", true);
            langaugeNotConfiguredError(3);
        }

        langFile = YamlConfiguration.loadConfiguration(file);
        p.getLogger().severe(get().getString("log-signature") + get().getString("language-start"));
    }

    public static FileConfiguration get(){
        return langFile;
    }

}
