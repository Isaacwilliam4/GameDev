package ecs.Systems;

import ecs.Components.ParticleComponent;
import ecs.Components.ParticleSystemComponent;
import edu.usu.graphics.Color;
import edu.usu.graphics.Graphics2D;
import edu.usu.graphics.Texture;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class ParticleSystem extends System {

    public ParticleSystem() {
        super(ParticleSystemComponent.class);
    }

    @Override
    public void update(double elapsedTime) {
        for (var entity : entities.values()) {
            var systemComponent = entity.get(ParticleSystemComponent.class);

            // Remove expired particles
            List<Long> removeMe = new ArrayList<>();
            for (var particle : systemComponent.particles.values()) {
                particle.alive += elapsedTime;
                if (particle.alive >= particle.lifetime) {
                    removeMe.add(particle.name);
                } else {
                    updateParticle(particle, elapsedTime);
                }
            }
            for (Long key : removeMe) {
                systemComponent.particles.remove(key);
            }

            // Generate new particles
//            systemComponent.timeToCreate -= elapsedTime;
//            if (systemComponent.timeToCreate > 0) {
            for (int i = 0; i < 8; i++) {
                var particle = createParticle(systemComponent);
                systemComponent.particles.put(particle.name, particle);
            }
//            }
            render(systemComponent.graphics);
        }
    }

    private void updateParticle(ParticleComponent particle, double elapsedTime) {
        particle.center.x += (float) (elapsedTime * particle.speed * particle.direction.x);
        particle.center.y += (float) (elapsedTime * particle.speed * particle.direction.y);
        particle.area.left += (float) (elapsedTime * particle.speed * particle.direction.x);
        particle.area.top += (float) (elapsedTime * particle.speed * particle.direction.y);
        particle.rotation += (particle.speed / 0.5f);
    }

    private ParticleComponent createParticle(ParticleSystemComponent system) {
        Vector2f directionNoisy = new Vector2f(system.direction);
        float angle = (float) system.random.nextGaussian() * system.angleStdDev;
        float cosTheta = (float) Math.cos(angle);
        float sinTheta = (float) Math.sin(angle);

        directionNoisy.set(
                system.direction.x * cosTheta - system.direction.y * sinTheta,
                system.direction.x * sinTheta + system.direction.y * cosTheta
        );

        float size = (float) Math.abs(system.random.nextGaussian() * system.sizeStdDev + system.sizeMean);
        return new ParticleComponent(
                new Vector2f(system.center.x, system.center.y),
                directionNoisy,
                (float) Math.abs(system.random.nextGaussian() * system.speedStdDev + system.speedMean),
                new Vector2f(size, size),
                Math.abs(system.random.nextGaussian() * system.lifetimeStdDev + system.lifetimeMean)
        );
    }

    public void render(Graphics2D graphics) {
        for (var entity : entities.values()) {
            var systemComponent = entity.get(ParticleSystemComponent.class);
            for (var particle : systemComponent.particles.values()) {
                graphics.draw(systemComponent.texture, particle.area, particle.rotation, particle.center, Color.WHITE);
            }
        }
    }
}
