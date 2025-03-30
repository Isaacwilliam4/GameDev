import ecs.Entities.*;
import ecs.Systems.*;
import ecs.Systems.KeyboardInput;
import edu.usu.graphics.*;

import java.util.ArrayList;
import java.util.List;

public class GameModel {
    private final List<Entity> removeThese = new ArrayList<>();
    private final List<Entity> addThese = new ArrayList<>();
    private Entity player;

    private ecs.Systems.Renderer sysRenderer;
    private ecs.Systems.Collision sysCollision;
    private ecs.Systems.Movement sysMovement;
    private ecs.Systems.KeyboardInput sysKeyboardInput;
    private ecs.Systems.Countdown sysCountdown;
    private ecs.Systems.ParticleSystem sysParticleSystem;
    private ecs.Systems.CarSystem sysCarSystem;
    private Graphics2D graphics;
    public boolean gameOver = false;
    private Font font = new Font("resources/fonts/gunplay3d.otf", 48, false);
    private int score = 0;


    public void initialize(Graphics2D graphics) {
        this.graphics = graphics;
        var texSquare = new Texture("resources/images/square-outline.png");

        sysRenderer = new Renderer(graphics);
        // Remove the exist food
        // Generate another piece of food
        sysCollision = new Collision();
        sysMovement = new Movement();
        sysKeyboardInput = new KeyboardInput(graphics.getWindow());
        sysParticleSystem = new ParticleSystem();
        sysCarSystem = new CarSystem();
//        sysCountdown = new Countdown(
//                graphics,
//                (Entity entity) -> {
//                    removeEntity(entity);
//                    ecs.Entities.Snake.enableControls(player);
//                    var movable = player.get(ecs.Components.Movable.class);
//                    movable.facing = Movable.Direction.Up;
//                    addEntity(player);
//                });

//        initializeBorder(texSquare);
//        initializeObstacles(texSquare);
        initializeSnake(texSquare);
//        addEntity(createFood(texSquare));

//        var countdown = ecs.Entities.Countdown.create(3);
//        addEntity(countdown);
    }

    public void update(double elapsedTime) {
        // Because ECS framework, input processing is now part of the update
        sysKeyboardInput.update(elapsedTime);
        // Now do the normal update
        if (!gameOver) {
            sysMovement.update(elapsedTime);
            sysCarSystem.update(elapsedTime);
        }
        sysCollision.update(elapsedTime);
        sysParticleSystem.update(elapsedTime);

        for (Entity value: sysCarSystem.getCars().values()){
            sysRenderer.add(value);
        }

        for (Entity value: sysCarSystem.getCars().values()){
            sysCollision.add(value);
        }

        removeThese.addAll(sysCarSystem.getEntitiesToRemove());
        removeThese.addAll(sysParticleSystem.getEntitiesToRemove());

        for (var entity : removeThese) {
            removeEntity(entity);
        }
        removeThese.clear();

        for (var entity : addThese) {
            addEntity(entity);
        }
        addThese.clear();

        // Because ECS framework, rendering is now part of the update
        sysRenderer.update(elapsedTime);
//        sysCountdown.update(elapsedTime);

        cleanUp();

        if (sysRenderer.endGame){
            gameOver = true;
        }

        if (sysRenderer.endGameDone){
            final float HEIGHT_MENU_ITEM = 0.075f;
            float top = -0.25f;
            drawText("Game Over, Your Score: " + score + ", \n Press ESC to return to Main Menu", top, HEIGHT_MENU_ITEM, -0.5f, Color.WHITE);
//            renderMenuItem(font, "Game Over, Your Score: " + score + ", \n Press ESC to return to Main Menu", top, HEIGHT_MENU_ITEM, Color.WHITE);
        }
    }

    private float renderMenuItem(Font font, String text, float top, float height, Color color) {
        float width = font.measureTextWidth(text, height);
        graphics.drawTextByHeight(font, text, 0.0f - width / 2, top, height, color);

        return top + height;
    }

    private void drawText(String text, float top, float height, float left, Color color) {
        String[] stringArr = text.split("\n");
        float width = font.measureTextWidth(text, height);
        int idx = 0;
        for (String str: stringArr){
            float newTop = top + (idx * height);
            graphics.drawTextByHeight(font, str, left, newTop, height, color);
            idx++;
        }
    }


    private void cleanUp(){
        sysCarSystem.cleanUp();
        sysParticleSystem.cleanUp();
    }


    private void addEntity(Entity entity) {
        sysKeyboardInput.add(entity);
        sysMovement.add(entity);
        sysCollision.add(entity);
        sysRenderer.add(entity);
//        sysCountdown.add(entity);
    }


    private void removeEntity(Entity entity) {
        sysKeyboardInput.remove(entity.getId());
        sysMovement.remove(entity.getId());
        sysCollision.remove(entity.getId());
        sysRenderer.remove(entity.getId());
        sysCarSystem.remove(entity.getId());
//        sysCountdown.remove(entity.getId());
    }

    private void initializeSnake(Texture square) {

        MyRandom rnd = new MyRandom();
        boolean done = false;
        player = Player.create(0,.3f, graphics);
        Player.enableControls(player);
        addEntity(player);

        sysParticleSystem.add(player);


//        while (!done) {
//            int x = (int) rnd.nextRange(1, GRID_SIZE - 1);
//            int y = (int) rnd.nextRange(1, GRID_SIZE - 1);
//            var proposed = Snake.create(square, x, y);
//            if (!sysCollision.collidesWithAny(proposed)) {
//                addEntity(proposed);
//                player = proposed;
//                done = true;
//            }
//        }
    }


}
