package ecs.Systems;

import edu.usu.graphics.Color;
import edu.usu.graphics.Graphics2D;
import edu.usu.graphics.Rectangle;
import edu.usu.graphics.Texture;
import org.joml.Vector2f;

public class Renderer extends System {

    private final Graphics2D graphics;
    private final double ROAD_BG_INTERVAL = 0.1;
    private final float ROAD_BG_INTERVAL_MVM = 0.1f;
    private final int ROAD_BG_INTERVAL_NUM = 3;
    private final float BG_TOP_RESET = -1.75f;
    private int roadBgNumVal = 0;
    private double curr_bg_interval = 0.0;
    private float bg_top = -1.75f;

    public Renderer(Graphics2D graphics) {
        super(ecs.Components.Appearance.class,
                ecs.Components.Position.class);

        this.graphics = graphics;
    }

    @Override
    public void update(double elapsedTime) {

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

        // Draw a blue background for the gameplay area
        Rectangle area = new Rectangle(-1f, bg_top, 2f, 2.5f);
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
        var area = new Rectangle(position.getX() - (appearance.width / 2), position.getY() - (appearance.height / 2), appearance.width, appearance.height);
        var center = new Vector2f(position.getX() + appearance.width / 2, position.getY() + appearance.height / 2);
        graphics.draw(appearance.image, area, 0, center, Color.WHITE);
    }
}
