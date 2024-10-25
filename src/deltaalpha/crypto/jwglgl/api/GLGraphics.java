package deltaalpha.crypto.jwglgl.api;

import static deltaalpha.crypto.jwglgl.App.*;
import static org.lwjgl.opengl.GL11.*;

public class GLGraphics implements GraphicsInterface {
    public Texture selected = null;
    public String previous = "";
    float GL_MATRIX_X = 0;
    float GL_MATRIX_Y = 0;

    public void loadTexture(Texture texture) {
        selected = texture;
        selected.bind();
        previous = "";
    }

    @Override
    public void reset() {
        color(1, 1, 1, 1);
        selected = null;
        previous = "";
    }

    @Override
    public void loadImage(String path) {
        if(previous.equals(path)) return;
        selected = Texture.get(path);
        selected.bind();
        previous = path;
    }

    @Override
    public void color(float r, float g, float b, float a) {
        glColor4f(r, g, b, a);
    }

    @Override
    public void rect(float x1, float y1, float x2, float y2, float rotation) {
        float centerX = (x1 + x2) / 2;
        float centerY = (y1 + y2) / 2;

        float[] vertices = {
                x1, y1, x2, y1,
                x2, y2, x1, y2
        };
        
        float cos = (float) Math.cos(Math.toRadians(rotation));
        float sin = (float) Math.sin(Math.toRadians(rotation));

        glBegin(GL_QUADS);
        for (int i = 0; i < 4; i++) {
            float xOffset = vertices[i * 2] - centerX;
            float yOffset = vertices[i * 2 + 1] - centerY;

            float rotatedX = cos * xOffset - sin * yOffset + centerX;
            float rotatedY = sin * xOffset + cos * yOffset + centerY;

            glVertex2f(glTransformX(rotatedX), glTransformY(rotatedY));
        }
        glEnd();
    }

    @Override
    public void drawImage(float x1, float y1, float x2, float y2, float rotation) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_TEXTURE_2D);

        float centerX = (x1 + x2 - GL_MATRIX_X) / 2;
        float centerY = (y1 + y2 - GL_MATRIX_Y) / 2;

        float[] vertices = {
                x1, y1,
                x2, y1,
                x2, y2,
                x1, y2
        };

        float[] tex_vertices = {
                0, 1, 1, 1,
                1, 0, 0, 0
        };

        float cos = (float) Math.cos(Math.toRadians(rotation));
        float sin = (float) Math.sin(Math.toRadians(rotation));

        glBegin(GL_QUADS);
        for (int i = 0; i < 4; i++) {
            float xOffset = vertices[i * 2] - centerX;
            float yOffset = vertices[i * 2 + 1] - centerY;

            float rotatedX = cos * xOffset - sin * yOffset + centerX;
            float rotatedY = sin * xOffset + cos * yOffset + centerY;

            glTexCoord2f(tex_vertices[i * 2], tex_vertices[i * 2 + 1]);
            glVertex2f(glTransformX(rotatedX), glTransformY(rotatedY));
        }
        glEnd();

        glDisable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
    }

    @Override
    public void drawImageSized(float x, float y, float w, float h, float rotation) {
        float ax = selected == null ? 1 : selected.getArtifactFixX();
        float ay = selected == null ? 1 : selected.getArtifactFixY();
        GL_MATRIX_X = w * ax - w;
        GL_MATRIX_Y = h * ay - h;
        GraphicsInterface.super.drawImageSized(x, y, w * ax, h * ay, rotation);
        GL_MATRIX_X = 0;
        GL_MATRIX_Y = 0;
    }
}