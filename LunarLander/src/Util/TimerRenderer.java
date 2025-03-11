package Util;

import edu.usu.graphics.Color;
import edu.usu.graphics.Font;
import edu.usu.graphics.Graphics2D;

public class TimerRenderer {
    private final float seconds;
    private float secondsLeft;
    private boolean done = false;

    public TimerRenderer(float seconds) {
        this.seconds = seconds;
        this.secondsLeft = seconds;
    }

    public void update(double timeElapsed) {
        secondsLeft -= timeElapsed;
        if (secondsLeft <= 0) {
            done = true;
        }
    }

    public void render(Graphics2D graphics, Font font) {
        String num = String.format("%02d", (int) secondsLeft + 1);
        graphics.drawTextByHeight(font, num, -.1f, -.1f, .2f, Color.RED);
    }

    public void reset(){
        this.secondsLeft = seconds;
        this.done = false;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
