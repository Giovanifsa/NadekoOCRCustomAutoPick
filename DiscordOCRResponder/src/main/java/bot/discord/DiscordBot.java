package bot.discord;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

import javax.security.auth.login.LoginException;

import com.sun.jdi.InvalidTypeException;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.TesseractException;

public class DiscordBot {
	public static void main(String args[]) throws TesseractException, IOException, InvalidTypeException, LoginException, NoSuchAlgorithmException {		
		
		if (args.length >= 4) {
			boolean ocr = Boolean.valueOf(args[3]);
			
			Long guildIDLong = null;
			
			if (args.length == 5) {
				guildIDLong = Long.valueOf(args[4]);
			}
			
			new JDAController(args[0], Long.valueOf(args[1]), Boolean.valueOf(args[2]), Boolean.valueOf(args[3]), guildIDLong);
		}
		
		else {
			System.out.println("User token: User session token");
			System.out.println("Nadeko id: Nadeko bot id to search for messages");
			System.out.println("Pick: true to automatically pick flowers, false for only console logging");
			System.out.println("OCR: True to enable optical character recognition (powered by tesseract), false leaves it disabled");
			System.out.println("Guild id: Optional guild (group) to pick flowers from. Leave this blank to pick flowers from all guilds.");
			
			System.out.println("DiscordBot.jar <user token> <nadeko id> <pick> <ocr> [guild id]");
		}
	}
}
