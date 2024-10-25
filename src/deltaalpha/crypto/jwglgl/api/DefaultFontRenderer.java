package deltaalpha.crypto.jwglgl.api;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Comparator;

public class DefaultFontRenderer implements IFontRenderer {
    private float spacing = 5, scl = 1, hSpacing = 2;
    public static Atlas atlas;

    public static Texture getGlyph(char c) {
        return atlas.get(String.valueOf((short) c), "invalid");
    }

    public static void createCapabilities() {
        atlas = new Atlas(Resource.get("base:textures/font.json").getOrElseThrow(OrElseThrowException::new));
    }

    @Override
    public void render(GraphicsInterface graphics, String text, float x, float y, float maxWidth) {
        String[] lines = splitIntoLines(text, maxWidth);
        float ty = y;
        float tx = x;
        for(String line : lines) {
            for(int j = 0; j < line.length(); j++) {
                char c = line.charAt(j);
                if(graphics instanceof GLGraphics glGraphics) {
                    glGraphics.loadTexture(getGlyph(c));
                }
                float w = charWidth(c);
                float h = charHeight(c);
                graphics.drawImageSized(tx, ty - h / 2, w, h);
                tx += w + spacing * scl;
            }

            ty -= getStringHeight(line) + hSpacing * scl;
            tx = x;
        }
    }

    @Override
    public String[] splitIntoLines(String text, float maxWidth) {
        if(text.contains("\n")) {
            return Arrays
                    .stream(text.split("\n"))
                    .map(s -> splitIntoLines(s, maxWidth))
                    .flatMap(Arrays::stream)
                    .toArray(String[]::new);
        }

        if(maxWidth == Float.POSITIVE_INFINITY) return new String[] {text};
        StringBuilder sb = new StringBuilder();
        String[] lines = new String[0];
        float currentWidth = 0;
        for (char c : text.toCharArray()) {
            float width = charWidth(c);
            if (currentWidth + scl * (width + spacing * (currentWidth == 0 ? 0 : 1)) > maxWidth) {
                lines = ArrayUtils.add(lines, sb.toString());
                sb.setLength(0);
                currentWidth = 0;
            }
            if (width > maxWidth) {
                lines = ArrayUtils.add(lines, String.valueOf(c));
            } else {
                sb.append(c);
                currentWidth += scl * (width + spacing * (currentWidth == 0 ? 0 : 1));
            }
        }
        if(!sb.isEmpty()) lines = ArrayUtils.add(lines, sb.toString());
        return lines;
    }

    @Override
    public float getStringWidth(String text) {
        if(StringUtils.isEmpty(text)) return 0;
        return spacing * (Arrays
                .stream(splitIntoLines(text, Float.POSITIVE_INFINITY))
                .max(Comparator.comparingInt(String::length))
                .orElse("")
                .length() - 1) * scl + Arrays
                .stream(splitIntoLines(text, Float.POSITIVE_INFINITY))
                .map(s -> s
                        .chars()
                        .mapToObj(i -> (char) i)
                        .map(this::charWidth)
                        .reduce(0F, Float::sum)
                )
                .max(Float::compare)
                .orElse(0F);
    }

    @Override
    public float getStringHeight(String text, float maxWidth) {
        String[] lines = splitIntoLines(text, maxWidth);
        if(lines.length == 0) {
            return 0;
        }
        return ((hSpacing * (lines.length - 1)) + Arrays
                .stream(lines)
                .map(str -> str
                        .chars()
                        .mapToObj(c -> (char) c)
                        .map(this::charHeight)
                        .max(Float::compare)
                        .orElse(0F)
                )
                .reduce(0F, Float::sum)
        ) * scl;
    }

    @Override
    public float charHeight(char ch) {
        return getGlyph(ch).getHeight() * scl;
    }

    @Override
    public float charWidth(char ch) {
        return getGlyph(ch).getWidth() * scl;
    }

    @Override
    public float hSpacing() {
        return hSpacing;
    }

    @Override
    public float spacing() {
        return spacing;
    }

    @Override
    public float scl() {
        return scl;
    }

    @Override
    public void hSpacing(float h) {
        this.hSpacing = h;
    }

    @Override
    public void spacing(float s) {
        this.spacing = s;
    }

    @Override
    public void scl(float s) {
        this.scl = s;
    }
}