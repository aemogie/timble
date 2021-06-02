package com.theaemogie.timble.components;

import com.theaemogie.timble.renderer.Color;
import com.theaemogie.timble.timble.GameObject;
import com.theaemogie.timble.timble.Window;
import imgui.ImGui;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public abstract class Component {

    private static int ID_COUNTER = 0;
    private int UUID = -1;

    public transient GameObject gameObject = null;

    public void start(){

    }

    public void update(Window window, double deltaTime){}

    public void imGui() {

        try {
            Field[] fields = this.getClass().getDeclaredFields();
            if (ImGui.collapsingHeader(this.getClass().getSimpleName())) {
                for (Field field : fields) {

                    boolean isTransient = Modifier.isTransient(field.getModifiers());
                    if (isTransient) {
                        continue;
                    }

                    boolean isPrivate = Modifier.isPrivate(field.getModifiers());
                    if (isPrivate) {
                        field.setAccessible(true);
                    }

                    Class type = field.getType();
                    Object value = field.get(this);
                    String name = field.getName();

                    if (type == int.class) {
                        int val = (int) value;
                        int[] imInt = {val};
                        if (ImGui.dragInt(name , imInt)) {
                            field.set(this, imInt[0]);
                        }
                    }
                    else if (type == float.class) {
                        float val = (float) value;
                        float[] imFloat = {val};
                        if (ImGui.dragFloat(name + ": ", imFloat)) {
                            field.set(this, imFloat[0]);
                        }
                    }
                    else if (type == boolean.class) {
                        boolean val = (boolean) value;
                        if (ImGui.checkbox(name + ": ", val)) {
                            field.set(this, !val);
                        }
                    }
                    else if (type == Vector3f.class) {
                        Vector3f val = (Vector3f) value;
                        float[] imVec = {val.x, val.y, val.z};
                        if (ImGui.dragFloat3(name + ": ", imVec)) {
                            val.set(imVec[0], imVec[1], imVec[2]);
                        }
                    }
                    else if (type == Vector4f.class) {
                        Vector4f val = (Vector4f) value;
                        float[] imVec = {val.x, val.y, val.z, val.w};
                        if (ImGui.dragFloat4(name, imVec)) {
                            val.set(imVec[0], imVec[1], imVec[2], imVec[3]);
                        }
                    }
                    else if (type == Color.class) {
                        Color valColor = (Color)value;
                        Vector4f valVec4 = valColor.toNormVec4();
                        float[] imVec = {valVec4.x, valVec4.y, valVec4.z, valVec4.w};
                        if (ImGui.colorPicker4(name, imVec)) {
                            valColor.setColor(imVec[0], imVec[1], imVec[2], imVec[3], true);
                        }
                    }

                    if (isPrivate) {
                        field.setAccessible(false);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public void generateID() {
        if (this.UUID == -1) this.UUID = ID_COUNTER++;
    }

    public int getUUID() {
        return this.UUID;
    }

    public static void init(int maxID) {
        ID_COUNTER = maxID;
    }
}
