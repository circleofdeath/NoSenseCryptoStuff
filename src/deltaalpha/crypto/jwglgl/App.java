package deltaalpha.crypto.jwglgl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectReader;
//import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import deltaalpha.crypto.jwglgl.api.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryStack.stackPush;

public class App {
    private static final JsonMapper mapper = JsonMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
            .build();

    public static final ObjectReader reader = mapper.reader();
    //public static final ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
    public static final float[] GLFW_WINDOW_SIZE = new float[2];

    public static float glTransformX(float x) {
        return (x) / (GLFW_WINDOW_SIZE[0] / 2f) - 1;
    }

    public static float glTransformY(float y) {
        return (y) / (GLFW_WINDOW_SIZE[1] / 2f) - 1;
    }

    /*
            Texture texture = new Texture();
        texture.load(BufferImageService.get("base:icon.png"));
        GLFWImage.Buffer icon = GLFWImage.malloc(1);
        icon.get(0).set(texture.getRealSize(), texture.getRealSize(), texture.getData());
        GLFW.glfwSetWindowIcon(window, icon);
        icon.free();
     */

    public static void loadIcon(long window) {
        try(InputStream is = SummaryWindow.class.getResourceAsStream("/icon.png")) {
            assert is != null;
            BufferedImage image = ImageIO.read(is);
            int width = image.getWidth();
            int height = image.getHeight();

            if(width != 128 || height != 128) {
                throw new IllegalArgumentException("Icon image must be 128x128");
            }

            ByteBuffer buffer = ByteBuffer.allocateDirect(width * height * 4);
            for(int y = 0; y < height; y++) {
                for(int x = 0; x < width; x++) {
                    int rgb = image.getRGB(x, y);
                    buffer.put((byte) ((rgb >> 16) & 0xFF)); // Red
                    buffer.put((byte) ((rgb >> 8) & 0xFF));  // Green
                    buffer.put((byte) (rgb & 0xFF));         // Blue
                    buffer.put((byte) ((rgb >> 24) & 0xFF)); // Alpha
                }
            }
            buffer.flip();

            try(MemoryStack ignored = stackPush()) {
                GLFWImage.Buffer icon = GLFWImage.malloc(1);
                icon.get(0).set(width, height, buffer);
                GLFW.glfwSetWindowIcon(window, icon);
                icon.free();
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        if(!GLFW.glfwInit()) {
            // How? I have no idea
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        MainWindow.createCapabilities();
        SummaryWindow.createCapabilities();
        GL.createCapabilities();
        DefaultFontRenderer.createCapabilities();

        long mainWindow = MainWindow.window;
        long summaryWindow = SummaryWindow.window;

        GLFW.glfwMakeContextCurrent(summaryWindow);
        new TextureCapabilities().createCapabilities();
        GLFW.glfwMakeContextCurrent(mainWindow);
        new TextureCapabilities().createCapabilities();

        int[] width = new int[1];
        int[] height = new int[1];
        while(true) {
            if(GLFW.glfwGetKey(mainWindow, GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS && !SummaryWindow.summaryOpen) {
                SummaryWindow.open();
            }
            GLFW.glfwMakeContextCurrent(summaryWindow);
            if(GLFW.glfwWindowShouldClose(summaryWindow)) {
                GLFW.glfwSetWindowShouldClose(summaryWindow, false);
                SummaryWindow.close();
            }

            GLFW.glfwMakeContextCurrent(mainWindow);
            if(GLFW.glfwWindowShouldClose(mainWindow)) {
                break;
            }
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            GLFW.glfwGetWindowSize(mainWindow, width, height);
            GLFW_WINDOW_SIZE[0] = width[0];
            GLFW_WINDOW_SIZE[1] = height[0];
            glViewport(0, 0, width[0], height[0]);
            MainWindow.draw(width[0], height[0]);
            GLFW.glfwSwapBuffers(mainWindow);
            GLFW.glfwPollEvents();

            if(SummaryWindow.summaryOpen) {
                GLFW.glfwMakeContextCurrent(summaryWindow);
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                GLFW.glfwGetWindowSize(summaryWindow, width, height);
                GLFW_WINDOW_SIZE[0] = width[0];
                GLFW_WINDOW_SIZE[1] = height[0];
                glViewport(0, 0, width[0], height[0]);
                SummaryWindow.draw(width[0], height[0]);
                GLFW.glfwSwapBuffers(summaryWindow);
            }
        }

        GLFW.glfwDestroyWindow(mainWindow);
        GLFW.glfwDestroyWindow(summaryWindow);
        GLFW.glfwTerminate();
    }
}