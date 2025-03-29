package ecs.Components;

import edu.usu.graphics.Rectangle;
import org.joml.Vector2f;

public class ParticleComponent extends Component {
    public long name;
    public Vector2f size;
    public Vector2f center;
    public Rectangle area;
    public float rotation;
    public Vector2f direction;
    public float speed;
    public double lifetime;
    public double alive = 0;

    private static long nextName = 0;

    public ParticleComponent(Vector2f center, Vector2f direction, float speed, Vector2f size, double lifetime) {
        this.name = nextName++;
        this.center = center;
        this.direction = direction;
        this.speed = speed;
        this.size = size;
        this.area = new Rectangle(center.x - size.x / 2, center.y - size.y / 2, size.x, size.y);
        this.lifetime = lifetime;
        this.rotation = 0;
    }
}
