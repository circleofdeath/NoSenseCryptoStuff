package deltaalpha.crypto.jwglgl.api;

import io.vavr.control.Either;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class Resource {
    private String namespace;
    private String path;

    public static Either<String, Resource> get(String text) {
        if(StringUtils.isEmpty(text)) return Either.left("Text cannot be empty");
        String[] buffer = text.split(":");
        if(buffer.length != 2) {
            return Either.left("Incorrect structure");
        }
        return get(buffer[0], buffer[1]);
    }

    public static Either<String, Resource> get(String namespace, String path) {
        if(StringUtils.isEmpty(namespace)) return Either.left("Namespace cannot be empty");
        if(StringUtils.isEmpty(path)) return Either.left("Path cannot be empty");
        if(path.contains(":") || namespace.contains(":")) {
            return Either.left("Incorrect structure");
        }
        Resource resource = new Resource();
        resource.setNamespace(namespace);
        resource.setPath(path);
        return Either.right(resource);
    }

    @Override
    public String toString() {
        return namespace + ":" + path;
    }
}