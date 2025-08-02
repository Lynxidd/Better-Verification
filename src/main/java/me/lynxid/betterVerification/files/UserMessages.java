package me.lynxid.betterVerification.files;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.ExceptionEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

import static me.lynxid.betterVerification.files.DiscordFile.jda;
import static me.lynxid.betterVerification.files.ServerMessages.*;
import static org.bukkit.Bukkit.getLogger;

public class UserMessages {
    private static String mBranding;
    private static Plugin p;
    private static FileConfiguration lang;

    public static void onboardingMessage(User user, String uID, String uName) {
        p = Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("BetterVerification"));
        String botChannel = p.getConfig().getString("bot-channel");
        lang = LanguageFile.get();
        mBranding = getBranding();
        String msg1 = lang.getString("onboarding");
        String uPing  = "\n||<@" + uID + ">||";

        user.openPrivateChannel()
                .flatMap(privateChannel ->
                        privateChannel.sendMessage(mBranding + msg1))
                .onErrorFlatMap(throwable -> {
                    TextChannel channel;
                    userDirectMessagesUnreachable(uName);
                    if (botChannel == null) {
                        botChannelNotConfiguredError(1);
                        return null;
                    } else {
                        channel = jda.getTextChannelById(botChannel);
                        try {
                            return Objects.requireNonNull(channel).createThreadChannel(uName + "-Verification Thread")
                                    .flatMap(c -> c.sendMessage(mBranding + msg1 + uPing));
                        } catch (Exception e) {
                            botChannelNotConfiguredError(2);
                            throw new RuntimeException(e);
                        }
                    }
                }).queue();
    }
}
