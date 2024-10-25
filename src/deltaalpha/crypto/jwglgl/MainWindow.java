package deltaalpha.crypto.jwglgl;

import deltaalpha.crypto.dto.LaunchPoolDTO;
import deltaalpha.crypto.jwglgl.api.DefaultFontRenderer;
import deltaalpha.crypto.jwglgl.api.GLGraphics;
import deltaalpha.crypto.jwglgl.api.GraphicsInterface;
import deltaalpha.crypto.jwglgl.api.IFontRenderer;
import deltaalpha.crypto.service.LaunchPoolService;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class MainWindow {
    public static List<LaunchPoolDTO> launchpoolCache = new ArrayList<>();
    public static IFontRenderer fontRenderer = new DefaultFontRenderer();
    public static GraphicsInterface graphics = new GLGraphics();
    public static String cat_img = "base:textures/cat_up.png";
    public static String status_str = "Up";
    public static int page_count = 3;
    public static int flux_cat_state = 1;
    public static int page = 0;
    public static long window;
    public static float crypto_flux;
    public static float scrollY;

    static boolean cat_started = false;
    static int cat_frames = 0;
    static int crypto_flux_period = 0;
    static int cooldown_of_isaac = 0;

    static int width = 720;
    static int height = 720;
    static int pageX;
    static int pageY;

    public static void focusOnWindow() {
        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwShowWindow(window);
        GLFW.glfwFocusWindow(window);
    }

    public static void setCachedLaunchpools(List<LaunchPoolDTO> launchpools) {
        launchpoolCache = launchpools;
        focusOnWindow();
        page = 2;
        cooldown_of_isaac = 60;
    }

    public static boolean isTouchingLaunchPool(int pageX, int pageY, int i) {
        float f = height - 125 * (i + 1) + scrollY;
        return pageX >= 25 && pageX <= width - 25 && pageY >= f && pageY <= f + 100;
    }

    public static boolean isTouchingCat(int pageX, int pageY) {
        return pageX >= 25 && pageX <= 281 && pageY >= height - 281 && pageY <= height - 25;
    }

    public static boolean isTouchingPrev(int pageX, int pageY) {
        return pageX >= 25 && pageX <= 125 && pageY >= 25 && pageY <= 65;
    }

    public static boolean isTouchingNext(int pageX, int pageY) {
        return pageX >= width - 125 && pageX <= width - 25 && pageY >= 25 && pageY <= 65;
    }

    public static void draw(int width, int height) {
        MainWindow.width = width;
        MainWindow.height = height;

        if(crypto_flux_period <= 0) {
            crypto_flux_period = 60;
            crypto_flux = (float) Math.random();
        } else {
            crypto_flux_period--;
        }

        if(cooldown_of_isaac > 0) {
            cooldown_of_isaac--;
        }

        if(cat_started) {
            cat_img = "base:textures/cat.png";
            if(cat_frames <= 15) {
                status_str = "Loading";
            } else if(cat_frames <= 30) {
                status_str = "Loading.";
            } else if(cat_frames <= 45) {
                status_str = "Loading..";
            } else {
                status_str = "Loading...";
            }
            cat_frames++;
            if(cat_frames == 60) {
                cat_started = false;
                cat_frames = 0;
                if(flux_cat_state == 1) {
                    flux_cat_state = -1;
                    cat_img = "base:textures/cat_down.png";
                    status_str = "Down";
                } else {
                    flux_cat_state = 1;
                    cat_img = "base:textures/cat_up.png";
                    status_str = "Up";
                }
            }
        }

        if(isTouchingPrev(pageX, pageY)) {
            graphics.rectsized(25, 25, 100, 40);
            graphics.color(0, 0, 0, 1);
            graphics.rectsized(27, 27, 96, 36);
        }
        graphics.reset();
        if(page == 0) {
            graphics.color(0.5f, 0.5f, 0.5f, 1);
        }
        fontRenderer.render(graphics, "<Prev", 30, 45);
        graphics.reset();
        if(isTouchingNext(pageX, pageY)) {
            graphics.rectsized(width - 125, 25, 100, 40);
            graphics.color(0, 0, 0, 1);
            graphics.rectsized(width - 123, 27, 96, 36);
        }
        graphics.reset();
        if(page == page_count - 1) {
            graphics.color(0.5f, 0.5f, 0.5f, 1);
        }
        fontRenderer.render(graphics, "Next>", width - 118, 45);
        graphics.reset();
        String text = "Page " + (page + 1) + " / " + page_count;
        fontRenderer.render(graphics, text, (width - fontRenderer.getStringWidth(text)) / 2f, 45);

        switch(page) {
            case 0:
                graphics.loadImage(cat_img);
                graphics.drawImageSized(25, height - 281, 256, 256);
                if(isTouchingCat(pageX, pageY) && !cat_started) {
                    graphics.loadImage("base:textures/cross.png");
                    graphics.drawImageSized(25, height - 281, 256, 256);
                }
                fontRenderer.render(graphics,
                        "Shredinger's cat\nStatus: " + status_str +
                                "\nCrypto flux: " + (crypto_flux * 100) + "%",
                        300, height - 40
                );
                fontRenderer.scl(0.75f);
                graphics.color(0.5f, 0.5f, 0.5f, 1);
                fontRenderer.render(graphics, "(clickable)", 300, height - 135);
                graphics.reset();
                fontRenderer.render(graphics, "Press s for summary", 300, height - 165);
                fontRenderer.hSpacing(20);
                fontRenderer.render(graphics, """
                                Possible host: 127.0.0.1:8080; Endpoint list:
                                /lp/raw - All launchpools
                                /lp/active - All active launchpools
                                /lp/inactive - All inactive launchpools
                                /lp/switch/<key>/<id> - Change launchpool status
                                /lp/slp4t - Send to page 3 inactive launchpools
                                   * 4 times with 5 seconds delay
                                /lp/slp4t_cached - like slp4t but don't repeat itself
                                /lp/period - Launchpool that in the period time
                                /lp/mine/<key> - Do all active launchpools
                                /summary/pools - Launchpools summary
                                """,
                        25, height - 296
                );
                fontRenderer.hSpacing(2);
                fontRenderer.scl(1);
                break;
            case 1:
                fontRenderer.scl(0.75f);
                fontRenderer.hSpacing(20);
                fontRenderer.render(graphics, """
                                /summary/exchange - Launchpools exchange summary
                                /summary/lunchpool - Launchpools launchpool summary
                                /summary/status - Launchpools status summary
                                /lp_flux/coef - Flux coefficient
                                /lp_flux/period - Flux period
                                /lp_flux/last - Last flux
                                """,
                        25, height - 50
                );
                fontRenderer.hSpacing(2);
                fontRenderer.scl(1);
                break;
            case 2:
                glScissor(0, 75, width, height - 75);
                glEnable(GL_SCISSOR_TEST);
                if(cooldown_of_isaac > 0) {
                    String txt = "Loading";
                    if(cooldown_of_isaac < 15) {
                        txt += "...";
                    } else if(cooldown_of_isaac < 30) {
                        txt += "..";
                    } else if(cooldown_of_isaac < 45) {
                        txt += ".";
                    } else {
                        txt += "";
                    }
                    fontRenderer.render(graphics, txt,
                            (width - fontRenderer.getStringWidth(txt)) / 2f, height - 150);
                } else if(launchpoolCache == null || launchpoolCache.isEmpty()) {
                    String txt = "No lunchpools exists\nDid you forget run a some command?";
                    fontRenderer.render(graphics, txt,
                            (width - fontRenderer.getStringWidth(txt)) / 2f, height - 150);
                } else {
                    for(int i = 0; i < launchpoolCache.size(); i++) {
                        LaunchPoolDTO lp = launchpoolCache.get(i);
                        graphics.rectsized(25, height - 125 * (i + 1) + scrollY, width - 50, 100);
                        graphics.color(0, 0, 0, 1);
                        graphics.rectsized(27, height - 125 * (i + 1) + 2 + scrollY, width - 54, 96);
                        if(LaunchPoolService.STATUS_ACTIVE.equals(lp.getStatus())) {
                            graphics.color(0, 1, 0, 1);
                        } else {
                            graphics.color(1, 0, 0, 1);
                        }
                        graphics.rectsized(29, height - 125 * (i + 1) + 4 + scrollY, 35, 92);
                        graphics.reset();
                        String __text = "[ID=" + lp.getId() + "] " + lp.getExchange() + " / " + lp.getLaunchPool() + "\n"
                                + lp.getPeriod().replace('—', '-'); // Okay, why this #### char exists?
                        fontRenderer.render(graphics, __text, 75,
                                height - 125 * (i + 1) + scrollY + fontRenderer.getStringHeight(__text)
                        );
                    }

                    for(int i = 0; i < launchpoolCache.size(); i++) {
                        if(isTouchingLaunchPool(pageX, pageY, i)) {
                            StringBuilder b = new StringBuilder();
                            LaunchPoolDTO dto = launchpoolCache.get(i);
                            dto.getPools().forEach((k, v) -> {
                                b.append(k).append(": ").append(v).append("\n");
                            });
                            if(!b.isEmpty()) {
                                b.deleteCharAt(b.length() - 1);
                            } else {
                                b.append("No data");
                            }
                            String _text = b.toString();
                            graphics.rectsized(25,
                                    height - 125 * (i + 1) + scrollY - fontRenderer.getStringHeight(_text) - 50,
                                    width - 50, fontRenderer.getStringHeight(_text) + 25
                            );
                            graphics.color(0, 0, 0, 1);
                            graphics.rectsized(27,
                                    height - 125 * (i + 1) + scrollY - fontRenderer.getStringHeight(_text) - 48,
                                    width - 54, fontRenderer.getStringHeight(_text) + 21
                            );
                            graphics.reset();
                            fontRenderer.render(graphics, _text, 50, height - 125 * (i + 1) + scrollY - 50);
                        }
                    }
                }
                glDisable(GL_SCISSOR_TEST);
                break;
        }
    }

    public static void createCapabilities() {
        window = GLFW.glfwCreateWindow(width, height, "Crypto Monitor 3000", 0, 0);
        GLFW.glfwSetWindowSizeLimits(window, width, height, GLFW.GLFW_DONT_CARE, GLFW.GLFW_DONT_CARE);
        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwShowWindow(window);
        GLFW.glfwSwapInterval(1);

        GLFW.glfwSetScrollCallback(window, (window, xoffset, yoffset) -> {
            scrollY -= 50F * (float) yoffset;
        });

        GLFW.glfwSetCursorPosCallback(window, (window, x, y) -> {
            pageX = (int)x;
            pageY = height - (int)y;
        });

        GLFW.glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
            if(action == GLFW.GLFW_RELEASE) {
                double[] x = new double[1];
                double[] y = new double[1];
                GLFW.glfwGetCursorPos(window, x, y);
                int pageX = (int)x[0];
                int pageY = height - (int)y[0];

                if(isTouchingNext(pageX, pageY) && page < page_count - 1) {
                    page++;
                }

                if(isTouchingPrev(pageX, pageY) && page > 0) {
                    page--;
                }

                switch(page) {
                    case 0:
                        if(isTouchingCat(pageX, pageY) && !cat_started) {
                            System.out.println("Meow!");
                            cat_started = true;
                        }
                        break;
                    case 1, 2:
                        break;
                }
            }
        });

        App.loadIcon(window);
    }
}