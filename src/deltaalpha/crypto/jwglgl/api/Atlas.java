package deltaalpha.crypto.jwglgl.api;

import deltaalpha.crypto.jwglgl.App;
import lombok.Data;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Data
public class Atlas {
    private Map<String, Texture> map = new HashMap<>();
    private BufferedImage[] sources;

    public static AtlasData readData(Resource file) {
        try {
            return App.reader.readValue(URLService.get().publish(file), AtlasData.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Texture get(String key, String def) {
        return map.getOrDefault(key, get(def));
    }

    public Texture get(String key) {
        return map.get(key);
    }

    public Atlas(Resource file) {
        this(readData(file));
    }

    public Atlas(AtlasData data) {
        sources = Arrays.stream(data.getSources()).map(BufferImageService::get).toArray(BufferedImage[]::new);
        data.getSymbols().forEach((k, v) -> {
            BufferedImage source = sources[v.getSource()];
            Texture out = new Texture();
            BufferedImage image = new BufferedImage(v.getWidth(), v.getHeight(), BufferedImage.TYPE_INT_ARGB);
            for(int x = 0; x < v.getWidth(); x++) {
                for(int y = 0; y < v.getHeight(); y++) {
                    image.setRGB(x, y, source.getRGB(v.getX() + x, v.getY() + y));
                }
            }
            out.load(image);
            map.put(k, out);
        });
    }

    @Data
    public static class AtlasData {
        private Map<String, AtlasTexture> symbols;
        private String[] sources;
    }

    @Data
    public static class AtlasTexture {
        private int source, x, y, width, height;
    }
}