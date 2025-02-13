import edu.usu.graphics.Color;
import edu.usu.graphics.Graphics2D;

public class InputKeyboard3 {
    public static void main(String[] args) {
        try (Graphics2D graphics = new Graphics2D(1920, 1080, "Input - Keyboard 3")) {
            graphics.initialize(Color.CORNFLOWER_BLUE);
            Game game = new Game(graphics);
            game.initialize();
            game.run();
            game.shutdown();
        }
    }
}