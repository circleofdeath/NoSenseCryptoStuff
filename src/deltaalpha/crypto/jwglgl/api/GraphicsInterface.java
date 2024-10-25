package deltaalpha.crypto.jwglgl.api;

public interface GraphicsInterface {
    void reset();
    void loadImage(String path);
    void color(float r, float g, float b, float a);
    void rect(float x1, float y1, float x2, float y2, float rotation);
    void drawImage(float x1, float y1, float x2, float y2, float rotation);

    default void rect(float x1, float y1, float x2, float y2) {
        rect(x1, y1, x2, y2, 0);
    }

    default void rectsized(float x, float y, float w, float h, float rotation) {
        rect(x, y, x + w, y + h, rotation);
    }

    default void rectsized(float x, float y, float w, float h) {
        rectsized(x, y, w, h, 0);
    }

    default void drawImage(float x1, float y1, float x2, float y2) {
        drawImage(x1, y1, x2, y2, 0);
    }

    default void drawImageSized(float x, float y, float w, float h, float rotation) {
        drawImage(x, y, x + w, y + h, rotation);
    }

    default void drawImageSized(float x, float y, float w, float h) {
        drawImageSized(x, y, w, h, 0);
    }
}