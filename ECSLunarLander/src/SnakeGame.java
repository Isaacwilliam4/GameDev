import edu.usu.graphics.Color;
import edu.usu.graphics.Graphics2D;

public class SnakeGame {

    public static void main(String[] args) {

        try (Graphics2D graphics = new Graphics2D((int)(1920), (int)(1080), "ECS - Snake Game")) {
            graphics.initialize(Color.BLACK);
            Game game = new Game(graphics);
            game.initialize();
            game.run();
            game.shutdown();
        }
    }
}
