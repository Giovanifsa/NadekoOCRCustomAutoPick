package bot.discord;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import com.sun.jdi.InvalidTypeException;

import net.dv8tion.jda.core.entities.Message.Attachment;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.TesseractException;

public class JDAListenerAutoPickBot extends ListenerAdapter {
	private final Tesseract1 tesseract;
	private final SecureRandom random;
	
	private final long botIDLong;
	private final Long guildIDLong;
	private final boolean pick;
	private final boolean ocr;
	private final Path dataPath;

	public JDAListenerAutoPickBot(long botIDLong, boolean pick, boolean ocr, Long guildIDLong) throws NoSuchAlgorithmException, IOException {
		random = SecureRandom.getInstanceStrong();
		
		this.botIDLong = botIDLong;
		this.pick = pick;
		this.ocr = ocr;
		this.guildIDLong = guildIDLong;
		
		tesseract = new Tesseract1();
		
		dataPath = Files.createTempDirectory("discbot");
		
		Files.write(Paths.get(dataPath.toString() + 
				File.separator + "tesseract" + File.separator + "eng.traineddata"), 
				getClass().getResourceAsStream("/eng.traineddata").readAllBytes(), StandardOpenOption.WRITE);
		
		tesseract.setDatapath(dataPath.toString() + File.separator + "tesseract" + File.separator + "eng.traineddata");
	}

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		if (guildIDLong != null && event.getGuild().getIdLong() != guildIDLong) {
			return;
		}
			
		if (event.getAuthor().getIdLong() == botIDLong) {
			if (event.getMessage().getContentStripped().toLowerCase().contains("pick it up by typing")) {
				
				if (ocr) {
					new Thread(() -> {
						if (event.getMessage().getAttachments().size() > 0) {
							Attachment attach = event.getMessage().getAttachments().get(0);
	
							if (attach.isImage()) {
								File f = new File(dataPath.toString() + File.separator + "images" + File.separator + 
										random.nextInt() + System.currentTimeMillis() + 
										attach.getFileName().substring(attach.getFileName().lastIndexOf(".")));
	
								if (attach.download(f)) {
									System.out.println(
											"| Downloaded: " + attach.getFileName() + " to " + f.getAbsolutePath());
	
									try {
										long beginMoment = System.currentTimeMillis();
										String code = JDAController.processImage(tesseract, f);
										long finalMoment = System.currentTimeMillis();
	
										if (pick) {
											event.getChannel().sendMessage(".pick " + code).queue();
										}
										
										System.out.println("Identified code: " + code.trim() + " within "
												+ (finalMoment - beginMoment) + "ms.");
									} catch (TesseractException /* | InterruptedException */ e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (InvalidTypeException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}
						}
					}).start();
				}
				
				else {
					try {
						//Wait a bit so the bot has time to continue operation
						//After sending the image and chat message
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					event.getChannel().sendMessage(".pick").queue();
				}
			}
		}
	}
}

