package deltaalpha.crypto.jwglgl.api;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class BufferImageService {
    public static Map<String, BufferedImage> map = new HashMap<>();
    public static URLService service;

    public static BufferedImage invalidTexture() {
        return get("base:invalid.png");
    }

    public static BufferedImage get(String path) {
        return get(Resource.get(path).getOrElseThrow(OrElseThrowException::new));
    }

    public static BufferedImage get(Resource resource) {
        if(resource == null) {
            return invalidTexture();
        }

        if(map.containsKey(resource.toString())) {
            return map.get(resource.toString());
        }

        try(InputStream stream = service.getInputStream(resource)) {
            BufferedImage image = ImageIO.read(stream);
            if(image == null) {
                return invalidTexture();
            }
            map.put(resource.toString(), image);
            return image;
        } catch(Throwable ignored) {
            return invalidTexture();
        }
    }

    static {
        service = URLService.get();
    }
}