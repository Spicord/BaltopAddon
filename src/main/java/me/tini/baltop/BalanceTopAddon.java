package me.tini.baltop;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Iterator;
import java.util.Locale;

import org.bukkit.plugin.java.JavaPlugin;
import org.spicord.api.addon.SimpleAddon;
import org.spicord.bot.DiscordBot;

import com.earth2me.essentials.Essentials;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.essentialsx.api.v2.services.BalanceTop.Entry;

public class BalanceTopAddon extends SimpleAddon {

    private static final DecimalFormat FORMAT = new DecimalFormat(
        "###,###.##",
        new DecimalFormatSymbols(Locale.US)
    );

    private Essentials ess;

    public BalanceTopAddon() {
        super("BalanceTop", "baltop", "Tini", "1.0.0");
    }

    @Override
    public void onReady(DiscordBot bot) {
        ess = JavaPlugin.getPlugin(Essentials.class);

        ess.getBalanceTop().calculateBalanceTopMapAsync();

        bot.registerCommand(
            bot.commandBuilder("baltop", "BalanceTop")
                .setExecutor(this::handleCommand)
        );
    }

    private void handleCommand(SlashCommandInteractionEvent event) {
        Iterator<Entry> baltop = ess.getBalanceTop().getBalanceTopCache().values().iterator();

        String result = "";

        for (int i = 0; i < 10 && baltop.hasNext(); i++) {
            final Entry entry = baltop.next();

            final int position = i + 1;
            final String playerName = entry.getDisplayName();
            final String balance = FORMAT.format(entry.getBalance());                        

            result += String.format(
                "#%02d. `%s` - %s\n",
                position,
                playerName.replace("_", "\\_"),
                balance
            );
        }

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("BalanceTop");
        builder.setDescription(result);

        event.replyEmbeds(builder.build()).queue();
    }
}
