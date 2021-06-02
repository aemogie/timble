package com.theaemogie.timble.renderer;

import com.theaemogie.timble.physics.primitives.Line;
import com.theaemogie.timble.timble.Window;
import com.theaemogie.timble.util.AssetPool;
import com.theaemogie.timble.util.TimbleMath;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.theaemogie.timble.util.PresetsSettings.MAX_LINES;
import static com.theaemogie.timble.util.StringUtils.resourcePath;
import static org.lwjgl.opengl.GL30.*;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class DebugDraw {
    
    private static List<Line> lines = new ArrayList<>();
    //7 floats per vertex. 2 vertices per line.
    private static float[] vertexArray = new float[MAX_LINES * 7 * 2];
    private static Shader shader = AssetPool.getShader(resourcePath("shaders/Debug.glsl"));

    private static int vaoID, vboID;

    private static boolean started = false;

    public static void start() {
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, (long) vertexArray.length * Float.BYTES, GL_DYNAMIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 7 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 4, GL_FLOAT, false, 7 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glLineWidth(2.0f);
    }

    public static void beginFrame() {
        if (!started) {
            start();
            started = true;
        }

        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).beginFrame() < 0) {
                lines.remove(i);
                i--;
            }
        }
    }

    public static void draw(Window window) {
        if (lines.size() <= 0) return;

        int index = 0;
        for (Line line : lines) {
            for (int i = 0; i < 2; i++) {
                Vector2f position = i == 0 ? line.getStart() : line.getEnd();
                Vector4f color = line.getColor().toNormVec4();

                vertexArray[index] = position.x;
                vertexArray[index + 1] = position.y;
                vertexArray[index + 2] = -10.0f;

                vertexArray[index + 3] = color.x;
                vertexArray[index + 4] = color.y;
                vertexArray[index + 5] = color.z;
                vertexArray[index + 6] = color.w;
                index += 7;
            }
        }

        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertexArray, 0, lines.size() * 7 * 2));

        shader.use();
        shader.uploadMat4f("uProjection", window.getCurrentScene().getCamera().getProjectionMatrix());
        shader.uploadMat4f("uView", window.getCurrentScene().getCamera().getViewMatrix());

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawArrays(GL_LINES, 0, lines.size() * 3 * 2);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        shader.detach();
    }

    //region Draw Line

    public static void addLine(Vector2f from, Vector2f to, Color color, int lifetime) {
        if (lines.size() >= MAX_LINES) return;
        DebugDraw.lines.add(new Line(from, to, color, lifetime));
    }

    public static void addLine(Vector2f from, Vector2f to, Color color) {
        addLine(to, from, color, 1);
    }

    public static void addLine(Vector2f from, Vector2f to) {
        addLine(to, from, new Color(0, 1, 0));
    }

    //endregion

    //region Draw OBB

    public static void addBox(Vector2f center, Vector2f dimensions, float rotation, Color color, int lifetime) {
        Vector2f min = new Vector2f(center).sub(new Vector2f(dimensions).div(2));
        Vector2f max = new Vector2f(center).add(new Vector2f(dimensions).div(2));

        Vector2f[] vertices = {
                new Vector2f(min.x, min.y),
                new Vector2f(min.x, max.y),
                new Vector2f(max.x, max.y),
                new Vector2f(max.x, min.y),
        };

        if (rotation != 0.0f) {
            for (Vector2f vertex : vertices) {
                TimbleMath.rotate(vertex, center, rotation);
            }
        }

        addLine(vertices[0], vertices[1], color, lifetime);
        addLine(vertices[1], vertices[2], color, lifetime);
        addLine(vertices[2], vertices[3], color, lifetime);
        addLine(vertices[0], vertices[3], color, lifetime);
    }

    public static void addBox(Vector2f center, Vector2f dimensions, float rotation, Color color) {
        addBox(center, dimensions, rotation, color, 1);
    }

    public static void addBox(Vector2f center, Vector2f dimensions, float rotation) {
        addBox(center, dimensions, rotation, new Color(0, 1, 0));
    }

    public static void addBox(Vector2f center, Vector2f dimensions) {
        addBox(center, dimensions, 0.0f);
    }

    //endregion

    //region Draw Circle

    public static void addCircle(Vector2f center, float radius, int segments, Color color, int lifetime) {
        Vector2f[] points = new Vector2f[segments];

        int increment = 360 / points.length;
        int currentAngle = 0;

        for (int i = 0; i < points.length; i++) {
            Vector2f tmp = new Vector2f(radius, 0);
            TimbleMath.rotate(tmp, new Vector2f(), currentAngle);
            points[i] = new Vector2f(tmp).add(center);

            if (i > 0) {
                addLine(points[i - 1], points[i], color, lifetime);
            }
            currentAngle += increment;
        }
        addLine(points[points.length - 1], points[0], color, lifetime);
    }

    public static void addCircle(Vector2f center, float radius, int segments, Color color) {
        addCircle(center, radius, segments, color, 1);
    }

    public static void addCircle(Vector2f center, float radius, int segments) {
        addCircle(center, radius, segments, new Color(0, 1, 0));
    }

    public static void addCircle(Vector2f center, float radius) {
        addCircle(center, radius, 12);
    }

    //endregion
}
