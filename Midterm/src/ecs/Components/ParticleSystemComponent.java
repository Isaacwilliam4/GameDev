package ecs.Components;

import edu.usu.graphics.Graphics2D;
import edu.usu.graphics.Texture;
import org.joml.Vector2f;
import java.util.HashMap;
import java.util.Random;

public class ParticleSystemComponent extends Component {
    public Texture texture;
    public final HashMap<Long, ParticleComponent> particles = new HashMap<>();
    public final Random random = new Random();
    public Graphics2D graphics;
    public Vector2f center;
    public Vector2f direction;
    public final float sizeMean;
    public final float sizeStdDev;
    public final float speedMean;
    public final float speedStdDev;
    public final float lifetimeMean;
    public final float lifetimeStdDev;
    public final float angleStdDev;
    public double timeToCreate;
    public double totalLifeTime;
    public double currentLifeTime = 0;

    public ParticleSystemComponent(Graphics2D graphics,
                                   Texture texture,
                                   Vector2f center,
                                   Vector2f direction,
                                   float sizeMean,
                                   float sizeStdDev,
                                   float speedMean,
                                   float speedStdDev,
                                   float lifetimeMean,
                                   float lifetimeStdDev,
                                   float angleStdDev,
                                   double timeToCreate,
                                   double totalLifeTime) {
        this.graphics = graphics;
        this.texture = texture;
        this.center = center;
        this.direction = direction;
        this.sizeMean = sizeMean;
        this.sizeStdDev = sizeStdDev;
        this.speedMean = speedMean;
        this.speedStdDev = speedStdDev;
        this.lifetimeMean = lifetimeMean;
        this.lifetimeStdDev = lifetimeStdDev;
        this.angleStdDev = angleStdDev;
        this.timeToCreate = timeToCreate;
        this.totalLifeTime = totalLifeTime;
    }
}
