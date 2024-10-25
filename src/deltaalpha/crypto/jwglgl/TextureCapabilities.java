package deltaalpha.crypto.jwglgl;

import static org.lwjgl.opengl.GL11.*;

public class TextureCapabilities {
    private int texture_id;

    public int getCapabilitiesAllocation() {
        return texture_id;
    }

    public void createCapabilities() {
        texture_id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, getCapabilitiesAllocation());
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    }
}