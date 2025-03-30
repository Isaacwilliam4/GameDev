package ecs.Entities;

import ecs.Components.Movable;
import ecs.Components.Position;
import edu.usu.graphics.Color;
import edu.usu.graphics.Texture;

public class CarEntity{
    public static Entity create(float x, float y) {
        var car = new Entity();

        var texture = new Texture("resources/images/oncoming.png");
        car.add(new ecs.Components.Appearance(texture, Color.RED, .15f, .15f));
        car.add(new ecs.Components.Position(x,y, 0));
        car.add(new ecs.Components.Collision());
        car.add(new ecs.Components.Movable(0.3f, Movable.Direction.Down));

        return car;
    }
}
