package deltaalpha.crypto.jwglgl;

import deltaalpha.crypto.jwglgl.api.DefaultFontRenderer;
import deltaalpha.crypto.jwglgl.api.GLGraphics;
import deltaalpha.crypto.jwglgl.api.GraphicsInterface;
import deltaalpha.crypto.jwglgl.api.IFontRenderer;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class SummaryWindow {
    public static IFontRenderer fontRenderer = new DefaultFontRenderer();
    public static GraphicsInterface graphics = new GLGraphics();
    public static List<float[]> colors = new ArrayList<>();
    public static boolean summaryOpen = false;
    public static float[] animationFrames;
    public static SummaryEntry[] entries;
    public static long window;
    public static String title;

    public static void allocateEntries(String _title, SummaryEntry... _entries) {
        entries = _entries;
        title = _title;
        animationFrames = new float[entries.length];
        GLFW.glfwSetWindowShouldClose(window, true);
    }

    public static void allocateColors(int size) {
        while(colors.size() < size) {
            colors.add(new float[]{
                    (float) Math.max(Math.random(), 0.5f),
                    (float) Math.max(Math.random(), 0.5f),
                    (float) Math.max(Math.random(), 0.5f)
            });
        }
    }

    public static void open() {
        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwShowWindow(window);
        summaryOpen = true;
    }

    public static void close() {
        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwHideWindow(window);
        summaryOpen = false;
    }

    public static void draw(int width, int height) {
        graphics.reset();
        fontRenderer.render(graphics, title, 10, height - 23);
        float mark = 10;

        if(entries != null && entries.length > 0) {
            int max_value = Arrays.stream(entries)
                    .max(Comparator.comparingInt(SummaryEntry::getValue))
                    .get().getValue();

            mark = (int) (Math.ceil(max_value / 10F)) * 10;
        }

        graphics.color(0.5f, 0.5f, 0.5f, 1);
        float dist = height - 100;
        for(int i = 0; i < 10; i++) {
            graphics.rectsized(0, (dist / 10) * i + 50, width - 70, 1);
        }

        graphics.reset();
        graphics.rectsized(width - 70, 0, 10, height);
        graphics.rectsized(0, 48, width - 70, 4);
        graphics.rectsized(0, height - 52, width - 70, 4);
        graphics.rectsized(0, height / 2F - 2, width - 70, 4);
        fontRenderer.render(graphics, (int) mark + "", width - 50, height - 50);
        fontRenderer.render(graphics, (int) (mark / 2) + "", width - 50, height / 2F);
        fontRenderer.render(graphics, "0", width - 50, 50);

        if(entries != null && entries.length > 0) {
            if(animationFrames == null || animationFrames.length != entries.length) {
                animationFrames = new float[entries.length];
            }

            for(int i = 0; i < animationFrames.length; i++) {
                animationFrames[i] += Math.max(0.1f, 1f - animationFrames[i]) / 30F;
                animationFrames[i] = Math.min(1, animationFrames[i]);
            }

            float entryWidth = (width - 70F) / entries.length;
            allocateColors(entries.length);
            for(int i = 0; i < entries.length; i++) {
                float[] rgb = colors.get(i);
                SummaryEntry entry = entries[i];
                graphics.color(rgb[0], rgb[1], rgb[2], 1);
                graphics.rectsized((entryWidth * i) + 10, 52, entryWidth - 20,
                        (height - 104) * animationFrames[i] * (entry.getValue() / mark)
                );
                String text = entry.getName();
                float _width = fontRenderer.getStringWidth(text);
                fontRenderer.scl(Math.min((entryWidth * 0.75f) / _width, 1F));
                fontRenderer.render(graphics, text,
                        (entryWidth * i) + (entryWidth - fontRenderer.getStringWidth(text)) / 2F,
                        23
                );
                fontRenderer.scl(1);
            }
        }
    }

    public static void createCapabilities() {
        window = GLFW.glfwCreateWindow(500, 800, "Crypto Summary", 0, 0);
        GLFW.glfwSetWindowSizeLimits(window, 800, 500, 800, 500);
        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1);

        allocateEntries("Placeholder data",
                new SummaryEntry("Axolotls", 160),
                new SummaryEntry("Rabbits", 107),
                new SummaryEntry("Cats", 154),
                new SummaryEntry("Dogs", 67),
                new SummaryEntry("Horses", 9),
                new SummaryEntry("Snakes", 45)
        );
    }

    @Data
    @AllArgsConstructor
    public static class SummaryEntry {
        private String name;
        private int value;
    }
}
