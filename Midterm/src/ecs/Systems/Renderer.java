package ecs.Systems;

import edu.usu.graphics.Color;
import edu.usu.graphics.Graphics2D;
import edu.usu.graphics.Rectangle;
import edu.usu.graphics.Texture;
import org.joml.Vector2f;

public class Renderer extends System {

    private final Graphics2D graphics;

    public Renderer(Graphics2D graphics) {
        super(ecs.Components.Appearance.class,
                ecs.Components.Position.class);

        this.graphics = graphics;
    }

    @Override
    public void update(double elapsedTime) {

        // Draw a blue background for the gameplay area
        Rectangle area = new Rectangle(-1f, -1f, 2f, 2f);
        Texture bgTex = new Texture("resources/images/road.png");
        graphics.draw(bgTex, area, Color.WHITE);

        // Draw each of the game entities!
        for (var entity : entities.values()) {
            renderEntity(entity);
        }
    }

    private void renderEntity(ecs.Entities.Entity entity) {
        var appearance = entity.get(ecs.Components.Appearance.class);
        var position = entity.get(ecs.Components.Position.class);
        var area = new Rectangle(position.getX() - (position.width / 2), position.getY() - (position.height / 2), position.width, position.height);
        var center = new Vector2f(position.getX() + position.width / 2, position.getY() + position.height / 2);
        graphics.draw(appearance.image, area, 0, center, Color.WHITE);
    }
}
