package bot.discord;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class JDAListenerGeneralGuildChat extends ListenerAdapter {
	private final long botID;
	private final Long guildID;

	public JDAListenerGeneralGuildChat(long botID, Long guildID) throws NoSuchAlgorithmException {
		this.botID = botID;
		this.guildID = guildID;
	}

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		if (event.getAuthor().getIdLong() != botID || (guildID != null && event.getGuild().getIdLong() != guildID))
			return;

		System.out.println("\n|------------ MESSAGE RECEIVED");
		System.out.println("| Guild: " + event.getGuild().getName() + " : " + event.getGuild().getIdLong());
		System.out.println("| Author: " + event.getAuthor().getName() + " : " + event.getAuthor().getIdLong());
		System.out.println("| Message: " + event.getMessage().getContentStripped() + " : " + event.getMessage().getIdLong());
		System.out.println("| RawMessage: " + event.getMessage().getContentRaw() + " : " + event.getMessage().getIdLong());
		System.out.println("|-----------------------------");
	}
}