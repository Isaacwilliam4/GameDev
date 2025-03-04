import org.joml.Vector2f;

import java.util.*;

public class ParticleSystem {
    private final HashMap<Long, Particle> particles = new HashMap<>();
    private final Random random = new Random();

    private Vector2f center;
    private final float sizeMean;
    private final float sizeStdDev;
    private final float speedMean;
    private final float speedStdDev;
    private final float lifetimeMean;
    private final float lifetimeStdDev;
    private final float angleStdDev;

    public ParticleSystem(Vector2f center,
                          float sizeMean,
                          float sizeStdDev,
                          float speedMean,
                          float speedStdDev,
                          float lifetimeMean,
                          float lifetimeStdDev,
                          float angleStdDev) {
        this.sizeMean = sizeMean;
        this.sizeStdDev = sizeStdDev;
        this.speedMean = speedMean;
        this.speedStdDev = speedStdDev;
        this.lifetimeMean = lifetimeMean;
        this.lifetimeStdDev = lifetimeStdDev;
        this.angleStdDev = angleStdDev;
    }



    public void update(double gameTime) {
        // Update existing particles
        List<Long> removeMe = new ArrayList<>();
        for (Particle p : particles.values()) {
            if (!p.update(gameTime)) {
                removeMe.add(p.name);
            }
        }

        // Remove dead particles
        for (Long key : removeMe) {
            particles.remove(key);
        }

        // Generate some new particles
        for (int i = 0; i < 8; i++) {
            var particle = create();
            particles.put(particle.name, particle);
        }
    }

    public Collection<Particle> getParticles() {
        return this.particles.values();
    }

    private Particle create(Vector2f direction) {
        Vector2f directionNoisy = new Vector2f(direction);
        directionNoisy.add(new Vector2f((float) this.random.nextGaussian(0, this.angleStdDev),
                (float) this.random.nextGaussian(0, this.angleStdDev)));
        float size = (float) this.random.nextGaussian(this.sizeMean, this.sizeStdDev);
        var p = new Particle(
                new Vector2f(this.center.x, this.center.y),
                direction,
                (float) this.random.nextGaussian(this.speedMean, this.speedStdDev),
                new Vector2f(size, size),
                this.random.nextGaussian(this.lifetimeMean, this.lifetimeStdDev));

        return p;
    }

    public Vector2f getCenter() {
        return center;
    }

    public void setCenter(Vector2f center) {
        this.center = center;
    }
}
