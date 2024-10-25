package deltaalpha.crypto.jwglgl.api;

import lombok.Data;
import org.lwjgl.BufferUtils;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

@Data
public class Texture {
    public static Map<String, Texture> map = new HashMap<>();
    private ByteBuffer data;
    private float artifactFixX = 1;
    private float artifactFixY = 1;
    private int realSize;
    private int height;
    private int width;

    public static Texture get(String path) {
        return get(Resource.get(path).getOrElseThrow(OrElseThrowException::new));
    }

    public static Texture get(Resource path) {
        if(map.containsKey(path.toString())) {
            return map.get(path.toString());
        }
        Texture texture = new Texture();
        texture.load(BufferImageService.get(path));
        map.put(path.toString(), texture);
        return texture;
    }

    public void load(BufferedImage image) {
        if(image == null) {
            throw new RuntimeException("FATAL: Null ptr detected!");
        }

        width = image.getWidth();
        height = image.getHeight();
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive");
        }
        // OpenGL hates non-square textures for some reason
        realSize = Math.max(width, height);
        if(width > height) artifactFixY = (float) width / height;
        if(height > width) artifactFixX = (float) height / width;
        BufferedImage fixed = new BufferedImage(realSize, realSize, BufferedImage.TYPE_INT_ARGB);

        for(int x = 0; x < realSize; x++) {
            for(int y = 0; y < realSize; y++) {
                fixed.setRGB(x, y, 0x00000000);
            }
        }

        for(int x = 0; x < width; x++) {
            for(int y = height - 1; y >= 0; y--) {
                fixed.setRGB(x, realSize - height + y, image.getRGB(x, y));
            }
        }

        int[] data = new int[realSize * realSize * 4];
        fixed.getRGB(0, 0, realSize, realSize, data, 0, realSize);
        this.data = BufferUtils.createByteBuffer(realSize * realSize * 4);

        for(int x = 0; x < realSize; x++) {
            for(int y = 0; y < realSize; y++) {
                int pixel = data[x * realSize + y];
                this.data.put((byte) ((pixel >> 16) & 0xFF));
                this.data.put((byte) ((pixel >> 8) & 0xFF));
                this.data.put((byte) (pixel & 0xFF));
                this.data.put((byte) ((pixel >> 24) & 0xFF));
            }
        }

        this.data.flip();
    }

    public void bind() {
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, realSize, realSize, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
    }
}