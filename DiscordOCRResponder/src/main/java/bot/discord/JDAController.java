package bot.discord;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.imageio.ImageIO;
import javax.security.auth.login.LoginException;
import javax.swing.GrayFilter;

import com.mortennobel.imagescaling.ResampleOp;
import com.sun.jdi.InvalidTypeException;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.TesseractException;

public class JDAController {
	private final JDA jda;

	public JDAController(String token, long botIDLong, boolean pick, boolean ocr, Long guildIDLong) throws LoginException, NoSuchAlgorithmException, IOException {
		JDABuilder jdaBuilder = new JDABuilder(AccountType.CLIENT);
		jdaBuilder.setToken(token);
		jda = jdaBuilder.build();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			if (jda != null) {
				jda.shutdownNow();
			}
		}));

		if (ocr) {
			jda.addEventListener(new JDAListenerAutoPickBot(botIDLong, pick, ocr, guildIDLong));
		}
		
		jda.addEventListener(new JDAListenerGeneralGuildChat(botIDLong, guildIDLong));
	}

	public static String processImage(Tesseract1 tesseract, File f)
			throws IOException, TesseractException, InvalidTypeException {
		BufferedImage image = ImageIO.read(f);

		//Custom image processing, change to yours
		if (image.getWidth() == 203 && image.getHeight() == 203) {
			image = new ResampleOp(90, 60).filter(image.getSubimage(0, 0, 37, 20), null);																			// code
		}

		else if (image.getWidth() == 288 && image.getHeight() == 402) {
			image = image.getSubimage(0, 0, 81, 43); // Rectangle with the code
		}

		else if (image.getWidth() == 1280 && image.getHeight() == 1335) {
			image = image.getSubimage(0, 0, 240, 122); // Rectangle with the code
		}
		
		else {
			throw new InvalidTypeException(
				"Unrecognized image: " + f.getAbsolutePath() + " w: " + image.getWidth() + " h: " + image.getHeight());
		}
		
		//Gray scale
		Graphics2D graphics = image.createGraphics();
		graphics.drawImage(
				Toolkit.getDefaultToolkit()
				.createImage(new FilteredImageSource(image.getSource(), new GrayFilter(true, 1))), 0, 0, null);
		
		graphics.dispose();
		
		//Invert colors
		for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = new Color(image.getRGB(x, y), true);
                image.setRGB(x, y, new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue()).getRGB());
            }
        }
		
		//Make image monochromatic
		for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color c = new Color(image.getRGB(x, y));

                //Black characters
                if (c.getRed() < 200 && c.getGreen() < 200 && c.getBlue() < 200) {
                	image.setRGB(x, y, Color.black.getRGB());
                }
                
                //Anything else than black turns into white
                else {
                	image.setRGB(x,  y, Color.white.getRGB());
                }
            }
        }
		
		return tesseract.doOCR(image);
	}
}