package engine;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import java.awt.image.BufferedImage;
import java.io.File;

public class SpriteAnimator {
    public Texture[] frames;
    int index = 0;
    int timer = 0;
    int speed = 7;

    public SpriteAnimator(String sheetPath, int frameCount) {
        frames = new Texture[frameCount];

        try {
            File sheetFile = new File(PathsConfig.BASE + sheetPath);
            if (!sheetFile.exists()) {
                System.out.println("Image not found: " + sheetPath);
                return;
            }

            BufferedImage sheet = ImageIO.read(sheetFile);

            int calculatedWidth = sheet.getWidth() / frameCount;
            int calculatedHeight = sheet.getHeight();

            for (int i = 0; i < frameCount; i++) {
                BufferedImage frame = sheet.getSubimage(i * calculatedWidth, 0, calculatedWidth, calculatedHeight);

                File out = File.createTempFile("frame_" + sheetPath.hashCode() + "_" + i, ".png");
                out.deleteOnExit();

                ImageIO.write(frame, "PNG", out);
                frames[i] = TextureIO.newTexture(out, true);

                if (frames[i] != null) {
                    frames[i].setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
                    frames[i].setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Texture next() {
        timer++;
        if (timer >= speed) {
            timer = 0;
            index = (index + 1) % frames.length;
        }
        if (frames[index] == null) {
            return frames[0];
        }
        return frames[index];
    }

    public Texture getCurrentFrame() {
        if (frames[index] == null) {
            return frames[0];
        }
        return frames[index];
    }

    public void reset() {
        index = 0;
        timer = 0;
    }
}