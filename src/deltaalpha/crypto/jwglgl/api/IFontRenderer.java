package deltaalpha.crypto.jwglgl.api;

public interface IFontRenderer {
    void render(GraphicsInterface graphics, String text, float x, float y, float maxWidth);
    String[] splitIntoLines(String text, float maxWidth);
    float getStringWidth(String text);
    float getStringHeight(String text, float maxWidth);
    float charHeight(char ch);
    float charWidth(char ch);

    float hSpacing();
    float spacing();
    float scl();

    void hSpacing(float h);
    void spacing(float s);
    void scl(float s);

    default float getStringHeight(String text) {
        return getStringHeight(text, Float.POSITIVE_INFINITY);
    }

    default void render(GraphicsInterface graphics, String text, float x, float y) {
        render(graphics, text, x, y, Float.POSITIVE_INFINITY);
    }
}