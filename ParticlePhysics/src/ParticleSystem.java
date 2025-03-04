import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ParticleSystem {
    private final HashMap<Long, Particle> particles = new HashMap<>();
    private final MyRandom random = new MyRandom();

    private final Vector2f center;
    private final float sizeMean;
    private final float sizeStdDev;
    private final float speedMean;
    private final float speedStdDev;
    private final float lifetimeMean;
    private final float lifetimeStdDev;

    public ParticleSystem(Vector2f center, float sizeMean, float sizeStdDev, float speedMean, float speedStdDev, float lifetimeMean, float lifetimeStdDev) {
        this.center = center;
        this.sizeMean = sizeMean;
        this.sizeStdDev = sizeStdDev;
        this.speedMean = speedMean;
        this.speedStdDev = speedStdDev;
        this.lifetimeMean = lifetimeMean;
        this.lifetimeStdDev = lifetimeStdDev;
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

    private Particle create() {
        float size = (float) this.random.nextGaussian(this.sizeMean, this.sizeStdDev);
        var p = new Particle(
                new Vector2f(this.center.x, this.center.y),
                this.random.nextCircleVector(),
                (float) this.random.nextGaussian(this.speedMean, this.speedStdDev),
                new Vector2f(size, size),
                this.random.nextGaussian(this.lifetimeMean, this.lifetimeStdDev));

        return p;
    }
}
