package ecs.Entities;

import ecs.Components.Movable;
import ecs.Components.ParticleSystemComponent;
import edu.usu.graphics.Graphics2D;
import edu.usu.graphics.Texture;
import edu.usu.graphics.Color;
import org.joml.Vector2f;

import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class Player {
    public static Entity create(float x, float y, Graphics2D graphics) {
        final float MOVE_INTERVAL = (float) 0.015;

        var player = new Entity();


        var position = new ecs.Components.Position(x, y, 0);
        player.add(position);
        player.add(new ecs.Components.Collision());
        player.add(new ecs.Components.Movable(MOVE_INTERVAL, Movable.Direction.Stopped));
        Texture tex = new Texture("resources/images/player.png");
        Texture particleTex = new Texture("resources/images/particle.png");
        player.add(new ecs.Components.Appearance(tex, Color.WHITE, .15f, .15f));
        player.add(new ecs.Components.ParticleSystemComponent(
                graphics,
                particleTex,
                position.position,
                new Vector2f(0f,1f),
                0.01f,
                0.01f,
                0.01f,
                0.1f,
                1f,
                0.5f,
                3,
                0.1,
                1
        ));

        return player;
    }

    public static void enableControls(Entity player) {
        player.add(new ecs.Components.KeyboardControlled(
                Map.of(
//                        GLFW_KEY_UP, ecs.Components.Movable.Direction.Up,
//                        GLFW_KEY_DOWN, ecs.Components.Movable.Direction.Down,
                        GLFW_KEY_LEFT, ecs.Components.Movable.Direction.Left,
                        GLFW_KEY_RIGHT, ecs.Components.Movable.Direction.Right
                )));
    }
}
