package Models;

import org.joml.Vector2f;

import java.util.*;

public class ParticleSystem {
    private final HashMap<Long, Particle> particles = new HashMap<>();
    private final Random random = new Random();

    private Vector2f center;
    private Vector2f direction;
    private final float sizeMean;
    private final float sizeStdDev;
    private final float speedMean;
    private final float speedStdDev;
    private final float lifetimeMean;
    private final float lifetimeStdDev;
    private final float angleStdDev;
    private double timeToCreate;

    public ParticleSystem(Vector2f center,
                          Vector2f direction,
                          float sizeMean,
                          float sizeStdDev,
                          float speedMean,
                          float speedStdDev,
                          float lifetimeMean,
                          float lifetimeStdDev,
                          float angleStdDev) {
        this.center = center;
        this.direction = direction;
        this.sizeMean = sizeMean;
        this.sizeStdDev = sizeStdDev;
        this.speedMean = speedMean;
        this.speedStdDev = speedStdDev;
        this.lifetimeMean = lifetimeMean;
        this.lifetimeStdDev = lifetimeStdDev;
        this.angleStdDev = angleStdDev;
    }



    public void update(double gameTime, boolean generateParticles) {
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

        if (generateParticles) {
            // Generate some new particles
            for (int i = 0; i < 8; i++) {
                var particle = create();
                particles.put(particle.name, particle);
            }
        }

    }

    public void updateWithTimeLimit(double gameTime) {
        timeToCreate -= gameTime;
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

        if (timeToCreate > 0){
            // Generate some new particles
            for (int i = 0; i < 8; i++) {
                var particle = create();
                particles.put(particle.name, particle);
            }
        }
    }

    public Collection<Particle> getParticles() {
        return this.particles.values();
    }

    private Particle create() {
        Vector2f directionNoisy = new Vector2f(direction);
        float angle = (float) this.random.nextGaussian(0, this.angleStdDev);
        float cosTheta = (float) Math.cos(angle);
        float sinTheta = (float) Math.sin(angle);
        directionNoisy = new Vector2f(
                directionNoisy.x * cosTheta - directionNoisy.y * sinTheta,
                directionNoisy.x * sinTheta + directionNoisy.y * cosTheta
        );

        float size = (float) this.random.nextGaussian(this.sizeMean, this.sizeStdDev);
        var p = new Particle(
                new Vector2f(this.center.x, this.center.y),
                directionNoisy,
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

    public Vector2f getDirection() {
        return direction;
    }

    public void setDirection(Vector2f direction) {
        this.direction = direction;
    }

    public double getTimeToCreate() {
        return timeToCreate;
    }

    public void setTimeToCreate(double timeToCreate) {
        this.timeToCreate = timeToCreate;
    }
}
