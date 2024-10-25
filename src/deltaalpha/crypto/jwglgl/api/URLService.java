package deltaalpha.crypto.jwglgl.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

public class URLService extends EventService<URL, Resource> {
    private static final URLService INSTANCE = new URLService();

    public static URLService get() {
        return INSTANCE;
    }

    static {
        get().add(0, (event) -> {
            Resource resource = event.getInput();

            if(resource != null && "base".equals(resource.getNamespace())) {
                URL file = URLService.class.getResource("/" + resource.getPath());
                if(file != null) event.returnValue(file);
            }
        });
    }

    public Path path(Resource resource) {
        try {
            return Path.of(publish(resource).toURI());
        } catch(URISyntaxException ignored) {
            return null;
        }
    }

    public OutputStream getOutputStream(Resource resource) throws IOException {
        return publish(resource).openConnection().getOutputStream();
    }

    public InputStream getInputStream(Resource resource) throws IOException {
        return publish(resource).openConnection().getInputStream();
    }
}