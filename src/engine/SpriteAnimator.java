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

    public SpriteAnimator(String sheetPath, int frameCount, int frameWidth, int frameHeight) {
        frames = new Texture[frameCount];

        try {
            File sheetFile = new File(PathsConfig.BASE + sheetPath);
            if (!sheetFile.exists()) return;

            BufferedImage sheet = ImageIO.read(sheetFile);

            for (int i = 0; i < frameCount; i++) {
                if (i * frameWidth >= sheet.getWidth()) break;

                BufferedImage frame = sheet.getSubimage(i * frameWidth, 0, frameWidth, frameHeight);

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
        }
    }

    public Texture next() {
        timer++;
        if (timer >= speed) {
            timer = 0;
            index = (index + 1) % frames.length;
        }
        return frames[index];
    }

    public Texture getCurrentFrame() {
        return frames[index];
    }

    public void reset() {
        index = 0;
        timer = 0;
    }
}