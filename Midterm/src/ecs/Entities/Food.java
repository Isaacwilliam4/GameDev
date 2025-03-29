package ecs.Entities;

import edu.usu.graphics.Texture;
import edu.usu.graphics.Color;

public class Food {

    public static Entity create(Texture square, int x, int y) {
        var food = new Entity();

//        food.add(new ecs.Components.Appearance(square, new Color(1.0f, 0.5f, 0.0f)));
//        food.add(new ecs.Components.Position(x, y));
        food.add(new ecs.Components.Collision());
        food.add(new ecs.Components.Food());

        return food;
    }
}
