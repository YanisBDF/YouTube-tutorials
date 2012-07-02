package future;

import de.matthiasmann.twl.utils.PNGDecoder;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import utility.BufferTools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.ARBTextureRectangle.*;

/**
 * @author Oskar Veerhoek
 */
public class HeightmapsDemo {

    private static final String WINDOW_TITLE = "Heightmaps";
    private static final int[] WINDOW_DIMENSIONS = {1280, 720};
    
    private static int texture;
    private static int[] heightmap = new int[1280];

    private static void render() {
        glClear(GL_COLOR_BUFFER_BIT);
//        glBindTexture(GL_TEXTURE_RECTANGLE_ARB, texture);
//        glBegin(GL_QUADS);
//        glTexCoord2f(0, 0);
//        glVertex2d(-1, +1);
//        glTexCoord2f(320, 0);
//        glVertex2d(+1, +1);
//        glTexCoord2f(320, 1);
//        glVertex2d(+1, -1);
//        glTexCoord2f(0, 1);
//        glVertex2d(-1, -1);
//        glEnd();
    }

    private static void logic() {
        // Add logic code here
    }

    private static void input() {
        // Add input handling code here
    }

    private static void cleanUp(boolean asCrash) {
        // Add cleaning code here
        Display.destroy();
        System.exit(asCrash ? 1 : 0);
    }

    private static void setUpMatrices() {
        // Add code for the initialization of the projection matrix here
    }

    private static void setUpTextures() {
        texture = glGenTextures();
        {
            InputStream in = null;
            try {
                in = new FileInputStream("res/heightmap.png");
                PNGDecoder decoder = new PNGDecoder(in);
                ByteBuffer buffer = BufferUtils.createByteBuffer(4 * decoder.getWidth() * decoder.getHeight());
                decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
                buffer.flip();
                in.close();
                glBindTexture(GL_TEXTURE_RECTANGLE_ARB, texture);
                glTexParameteri(GL_TEXTURE_RECTANGLE_ARB, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                glTexParameteri(GL_TEXTURE_RECTANGLE_ARB, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                glTexImage2D(GL_TEXTURE_RECTANGLE_ARB, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
                glBindTexture(GL_TEXTURE_RECTANGLE_ARB, 0);
            } catch (FileNotFoundException ex) {
                System.err.println("Failed to find the texture files.");
                Display.destroy();
                System.exit(1);
            } catch (IOException ex) {
                System.err.println("Failed to load the texture files.");
                Display.destroy();
                System.exit(1);
            }
        }
        glBindTexture(GL_TEXTURE_RECTANGLE_ARB, texture);
        glBegin(GL_LINES);
        glTexCoord2f(0, 0);
        glVertex2d(-1, -1);
        glTexCoord2f(320, 1);
        glVertex2d(+1, -1);
        glEnd();
        FloatBuffer pixels = BufferTools.reserveData(1280 * 3);
        glReadPixels(0, 0, 1280, 1, GL_RGB, GL_FLOAT, pixels);
        for (int i = 0; i < 1280 * 3; i++) {
            if (i < 1277)
            heightmap[i] = (int) (pixels.get(i) * 100f);
        }
        for (int i : heightmap) {
            System.out.println(i);
        }
    }

    private static void setUpStates() {
        glEnable(GL_TEXTURE_RECTANGLE_ARB);
    }

    private static void update() {
        Display.update();
    }

    private static void enterGameLoop() {
        while (!Display.isCloseRequested()) {
            render();
            logic();
            input();
            update();
        }
    }

    private static void setUpDisplay() {
        try {
            Display.setDisplayMode(new DisplayMode(WINDOW_DIMENSIONS[0], WINDOW_DIMENSIONS[1]));
            Display.setVSyncEnabled(true);
            Display.setTitle(WINDOW_TITLE);
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            cleanUp(true);
        }
    }

    public static void main(String[] args) {
        setUpDisplay();
        setUpStates();
        setUpTextures();
        setUpMatrices();
        enterGameLoop();
        cleanUp(false);
    }

}
