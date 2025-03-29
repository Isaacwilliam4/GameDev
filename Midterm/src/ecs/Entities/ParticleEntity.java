package ecs.Entities;

import ecs.Components.ParticleComponent;
import ecs.Components.Position;
import edu.usu.graphics.Color;
import edu.usu.graphics.Texture;
import org.joml.Vector2f;

public class ParticleEntity {
    public static Entity create(Texture texture, Vector2f position, Vector2f direction, float speed, Vector2f size, double lifetime) {
        var particle = new Entity();

        particle.add(new ecs.Components.Appearance(texture, Color.WHITE,  size.x, size.y)); // Default white color
        particle.add(new Position(position.x, position.y, 0)); // Convert float position to integer grid coordinates
        particle.add(new ParticleComponent(position, direction, speed, size, lifetime)); // Add particle-specific behavior

        return particle;
    }
}
