package me.lynxid.betterVerification.files;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.bukkit.Bukkit;
import org.bukkit.block.data.type.Switch;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

import static me.lynxid.betterVerification.files.DiscordFile.jda;

public class ServerMessages {
    private static Plugin p;
    private static String log;
    private static String mBranding;
    private static String signature;
    private static FileConfiguration lang;

    public static void startMessage() {
        // Path for accessing plugin
        p = Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("BetterVerification"));
        // Check if the startup message is enabled
        if (p.getConfig().getBoolean("startup-message")) {
            // Get Discord channels
            getLog();

            // Get message components
            getBranding();

            // Get langauge loader
            lang = LanguageFile.get();

            // Send message
            Objects.requireNonNull(jda.getTextChannelById(log)).sendMessage(mBranding + lang.getString("startup")).queue();
            p.getLogger().info(signature + lang.getString("discord-start")); // Announce Discord startup
        }
    }

    public static void getLog() {
        log = p.getConfig().getString("log-channel");
        signature = lang.getString("log-signature");
        if (log == null) {
            p.getLogger().severe(signature + lang.getString("log-not-configured"));
        }
    }

    public static String getBranding() {
        mBranding = p.getConfig().getString("branding");
        if (mBranding == null) {
            p.getLogger().severe(signature + lang.getString("branding-left-empty-B"));
            getLog();
            mBranding = "# Better Verification";
            Objects.requireNonNull(jda.getTextChannelById(log)).sendMessage( mBranding + lang.getString("branding-left-empty-A")).queue();
        }
        return "# " + mBranding;
    }

    public static void statusNotConfiguredError(int situation) {
        getLog();
        getBranding();
        switch (situation) {
            case 1: // Error one happens if the text of the Status is left empty
                Objects.requireNonNull(jda.getTextChannelById(log)).sendMessage(mBranding + lang.getString("status-text-left-empty-A")).queue();
                p.getLogger().severe(signature + lang.getString("status-text-left-empty-A"));
            case 2: // Error two happens if the text of the Status is over 128 characters
                Objects.requireNonNull(jda.getTextChannelById(log)).sendMessage(mBranding + lang.getString("status-text-too-long-A")).queue();
                p.getLogger().severe(signature + lang.getString("status-text-too-long-B"));
            case 3: // Error three happens if the Status type is invalid
                Objects.requireNonNull(jda.getTextChannelById(log)).sendMessage(mBranding + lang.getString("status-type-invalid-A")).queue();
                p.getLogger().severe(signature + lang.getString("status-type-invalid-B"));
        }
    }

    public static void langaugeNotConfiguredError(int sitation) {
        getLog();
        getBranding();
        switch (sitation) {
            case 1: // This happens if the language identifier is left empty
                Objects.requireNonNull(jda.getTextChannelById(log)).sendMessage(mBranding + lang.getString(" language-key-left-empty-A")).queue();
                p.getLogger().severe(signature + lang.getString(" language-key-left-empty-B"));
            case 2: // This happens if the language identifier does not match any files
                Objects.requireNonNull(jda.getTextChannelById(log)).sendMessage(mBranding + lang.getString("language-key-invalid-A")).queue();
                p.getLogger().severe(signature + lang.getString("language-key-invalid-B"));
            case 3: // This happens if the en_US file is removed while the key is supposed to be en_US
                Objects.requireNonNull(jda.getTextChannelById(log)).sendMessage(mBranding + lang.getString("default-language-file-missing-A")).queue();
                p.getLogger().severe(signature + lang.getString("default-language-file-missing-A"));
        }
    }

    public static void botChannelNotConfiguredError(int sitation) {
        getLog();
        getBranding();
        switch (sitation) {
            case 1: // This happens if the bot commands channel ID is left empty
                Objects.requireNonNull(jda.getTextChannelById(log)).sendMessage(mBranding + lang.getString("bot-channel-left-empty-A")).queue();
                p.getLogger().severe(signature + lang.getString("bot-channel-left-empty-B"));
            case 2: // This happens if the bot commands channel ID is invalid
                Objects.requireNonNull(jda.getTextChannelById(log)).sendMessage(mBranding + lang.getString("bot-channel-invalid-A")).queue();
                p.getLogger().severe(signature + lang.getString("bot-channel-invalid-B"));
        }
    }

    public static void userDirectMessagesUnreachable(String uName) {
        getLog();
        getBranding();
        p.getLogger().info(signature + uName + " ran discord command '/verify' and does not have direct messages open!");
    }

    public static void userVerificationError(SlashCommandInteractionEvent e, int sitation, String stringReceived) {
        getLog();
        getBranding();
        switch (sitation) {
            case 1: // This happens if a user inputs a string that is too long when trying to verify their Minecraft account
                e.reply(mBranding + lang.getString("username-input-too-long") + "```" + stringReceived + "```").setEphemeral(true).queue();

                case 2:
                e.reply(mBranding + lang.getString("username-input-too-short") + "```" + stringReceived + "```").setEphemeral(true).queue();
        }
    }
}
