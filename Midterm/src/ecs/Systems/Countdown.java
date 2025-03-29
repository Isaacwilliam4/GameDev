package ecs.Systems;

import ecs.Entities.Entity;
import edu.usu.graphics.Color;
import edu.usu.graphics.Font;
import edu.usu.graphics.Graphics2D;

public class Countdown extends System {
    private Graphics2D graphics;
    private Font fontTime = new Font("resources/fonts/Roboto-Regular.ttf", 48, false);

    public interface ICountdownComplete {
        void invoke(Entity entity);
    }

    private final Countdown.ICountdownComplete onComplete;

    public Countdown(Graphics2D graphics, ICountdownComplete onComplete) {
        super(ecs.Components.Countdown.class);
        this.graphics = graphics;
        this.onComplete = onComplete;
    }

    @Override
    public void update(double elapsedTime) {
        final float TEXT_HEIGHT = 0.2f;
        for (var entity : entities.values()) {
            var countdown = entity.get(ecs.Components.Countdown.class);
            countdown.timeRemaining -= elapsedTime;

            if (countdown.timeRemaining < 0) {
                this.onComplete.invoke(entity);
            } else {
                String time = String.format("%d", (int) Math.floor(countdown.timeRemaining + 1));
                float width = fontTime.measureTextWidth(time, TEXT_HEIGHT);
                graphics.drawTextByHeight(fontTime, time, 0.0f - width / 2, 0 - TEXT_HEIGHT / 2, TEXT_HEIGHT, Color.YELLOW);
            }
        }
    }
}
