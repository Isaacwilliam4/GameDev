package ecs.Systems;

import ecs.Components.Collision;
import ecs.Components.ParticleSystemComponent;
import ecs.Entities.Entity;
import edu.usu.graphics.*;
import org.joml.Vector2f;

import java.awt.geom.Rectangle2D;

public class Renderer extends System {

    private final Graphics2D graphics;
    private final double ROAD_BG_INTERVAL = 0.1;
    private final float ROAD_BG_INTERVAL_MVM = 0.1f;
    private final int ROAD_BG_INTERVAL_NUM = 3;
    private final float BG_TOP_RESET = -1.75f;
    private int roadBgNumVal = 0;
    private double curr_bg_interval = 0.0;
    private float bg_top = -1.75f;
    private final Texture bgTex = new Texture("resources/images/road.png");
    public boolean endGame = false;
    public boolean endGameDone = false;
    public double endGameTime = 3;
    public double endGameCurrTime = 0;

    public Renderer(Graphics2D graphics) {
        super(ecs.Components.Appearance.class,
                ecs.Components.Position.class);

        this.graphics = graphics;
    }

    public void initialize() {

    }

    @Override
    public void update(double elapsedTime) {
        if (!endGame) {
            curr_bg_interval += elapsedTime;
            if (curr_bg_interval > ROAD_BG_INTERVAL) {
                curr_bg_interval = 0.0;
                roadBgNumVal += 1;
                if (roadBgNumVal > ROAD_BG_INTERVAL_NUM) {
                    roadBgNumVal = 0;
                    bg_top = BG_TOP_RESET;
                }
                bg_top = bg_top - ROAD_BG_INTERVAL_MVM * bg_top;
            }
        }

        // Draw a blue background for the gameplay area
        Rectangle area = new Rectangle(-1f, bg_top, 2f, 2.5f);
        graphics.draw(bgTex, area, Color.WHITE);

        if (endGame){
            endGameCurrTime += elapsedTime;
            if (endGameCurrTime > endGameTime) {
                endGameDone = true;
            }
        }

        // Draw each of the game entities!
        for (var entity : entities.values()) {
            renderEntity(entity);
        }
    }


    private void renderEntity(Entity entity) {

        if (entity.contains(ecs.Components.Collision.class) & entity.get(Collision.class).isCollided & entity.contains(ParticleSystemComponent.class)) {
            endGame = true;
            var particleSystem = entity.get(ParticleSystemComponent.class);
            for (var particle : particleSystem.particles.values()) {
                graphics.draw(particleSystem.texture, particle.area, particle.rotation, particle.center, Color.WHITE);
            }
        }
        else{
            var appearance = entity.get(ecs.Components.Appearance.class);
            var position = entity.get(ecs.Components.Position.class);
            var area = new Rectangle(position.getX() - (appearance.width / 2), position.getY() - (appearance.height / 2), appearance.width, appearance.height);
            var center = new Vector2f(position.getX() + appearance.width / 2, position.getY() + appearance.height / 2);
            graphics.draw(appearance.image, area, position.rotation, center, Color.WHITE);
        }

    }

//    public void renderEntity(Entity entity) {
//        // Check if the entity contains a collision component and a particle system component
//        if (entity.contains(ecs.Components.Collision.class) && entity.get(Collision.class).isCollided && entity.contains(ParticleSystemComponent.class)) {
//            endGame = true;
//            var particleSystem = entity.get(ParticleSystemComponent.class);
//            // Render all particles in the particle system
//            for (var particle : particleSystem.particles.values()) {
//                graphics.draw(particleSystem.texture, particle.area, particle.rotation, particle.center, Color.WHITE);
//            }
//        } else {
//            // Get the appearance and position components of the entity
//            var appearance = entity.get(ecs.Components.Appearance.class);
//            var position = entity.get(ecs.Components.Position.class);
//
//            // Calculate the main area for the entity (based on its width and height)
//            var area = new Rectangle(position.getX() - (appearance.width / 2), position.getY() - (appearance.height / 2), appearance.width, appearance.height);
//
//            // Render the main image of the entity
//            graphics.draw(appearance.image, area, position.rotation, new Vector2f(position.getX() + appearance.width / 2, position.getY() + appearance.height / 2), Color.WHITE);
//
//            // Calculate the half width and half height to get the corners
//            float halfWidth = appearance.width / 2;
//            float halfHeight = appearance.height / 2;
//
//            // Top-left corner (small red rectangle for debugging)
//            float topLeftX = position.getX() - halfWidth;
//            float topLeftY = position.getY() - halfHeight;
//
//            graphics.draw(new Rectangle(topLeftX, topLeftY, 0.01f, 0.01f, 1), Color.RED); // Small red rectangle at top-left corner
//
//            // Top-right corner (small green rectangle for debugging)
//            float topRightX = position.getX() + halfWidth;
//            float topRightY = position.getY() - halfHeight;
//            graphics.draw(new Rectangle(topRightX, topRightY, 0.01f, 0.01f), Color.RED); // Small green rectangle at top-right corner
//
//            // Bottom-left corner (small blue rectangle for debugging)
//            float bottomLeftX = position.getX() - halfWidth;
//            float bottomLeftY = position.getY() + halfHeight;
//            graphics.draw(new Rectangle(bottomLeftX, bottomLeftY, 0.01f, 0.01f), Color.RED); // Small blue rectangle at bottom-left corner
//
//            // Bottom-right corner (small yellow rectangle for debugging)
//            float bottomRightX = position.getX() + halfWidth;
//            float bottomRightY = position.getY() + halfHeight;
//            graphics.draw(new Rectangle(bottomRightX, bottomRightY, 0.01f, 0.01f), Color.RED); // Small yellow rectangle at bottom-right corner
//        }
//    }

}
