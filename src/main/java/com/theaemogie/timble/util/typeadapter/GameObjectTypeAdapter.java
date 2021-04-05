package com.theaemogie.timble.util.typeadapter;

import com.google.gson.*;
import com.theaemogie.timble.components.Component;
import com.theaemogie.timble.timble.GameObject;
import com.theaemogie.timble.timble.Transform;

import java.lang.reflect.Type;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class GameObjectTypeAdapter implements JsonDeserializer<GameObject> {
    @Override
    public GameObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        JsonArray components = jsonObject.getAsJsonArray("components");
        Transform transform = context.deserialize(jsonObject.get("transform"), Transform.class);

        GameObject gameObject = new GameObject(name, transform);

        for (JsonElement element : components) {
            Component c = context.deserialize(element, Component.class);
            gameObject.addComponent(c);
        }
        return gameObject;
    }
}
