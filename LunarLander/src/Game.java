import Enums.GameState;
import Enums.Menu;
import Enums.Movement;
import Util.GameUtils;
import Util.KeyboardInput;
import edu.usu.graphics.*;
import edu.usu.graphics.Color;
import edu.usu.graphics.Font;
import edu.usu.graphics.Graphics2D;
import edu.usu.graphics.Rectangle;

import java.util.*;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Game {
    private final Graphics2D graphics;
    private final float MAZE_LEFT = -0.5f;
    private final float MAZE_TOP = -0.5f;
    private final float MENU_LEFT = -0.2f;
    private final float MENU_TOP = -0.2f;
    private final float TEXT_HEIGHT = 0.038f;
    private final Color TEXT_COLOR = Color.WHITE;
    private final KeyboardInput inputKeyboard;
    private double timePassed = 0;
    private float score = 0;
    private List<String> scoreList = new ArrayList<>();
    private GameState gameState = GameState.MENU;
    private Menu menuSelect = Menu.PLAYGAME;
    private List<Vector2f> terrain;
    private Font font;
    private float characterRotation = 0f;
    private Vector2f characterLocation = new Vector2f(0f, 0.3f);
    private Ship ship;
    private float ROTATION_SPEED = 0.025f;
    private Vector2f GRAVITY = new Vector2f(0f, 1.0f);
    private float THRUST = -1.2f;
    private  Texture bg;
    private final Rectangle displayRect = new Rectangle(MAZE_LEFT, MAZE_TOP, 2*(Math.abs(MAZE_LEFT)), 2*(Math.abs(MAZE_LEFT)), -1.0f);
    private ParticleSystem particleSystemFire;
    private ParticleSystem particleSystemSmoke;
    private ParticleSystem particleSystemFireExplosion;
    private ParticleSystem particleSystemSmokeExplosion;
    private ParticleSystemRenderer particleSystemRendererFire;
    private ParticleSystemRenderer particleSystemRendererSmoke;
    private boolean shipCrashed = false;

    public Game(Graphics2D graphics) {
        this.graphics = graphics;
        this.inputKeyboard = new KeyboardInput(graphics.getWindow());
    }

    public void initialize() {
        ship = new Ship(new Vector2f(0f, -0.5f),
                        new Vector2f(0f, 0f),
                        GRAVITY,
                (float) Math.PI / 2f
                );
        bg = new Texture("resources/images/spacebg.png");
        font = new Font("resources/fonts/Blacknorthdemo-mLE25.otf", 42, false);

        particleSystemFire = new ParticleSystem(
                ship.getPosition(),
                ship.getForward(),
                0.01f, 0.005f,
                0.12f, 0.05f,
                2, 0.5f, 0.2f);

        particleSystemSmoke = new ParticleSystem(
                ship.getPosition(),
                ship.getForward(),
                0.015f, 0.004f,
                0.07f, 0.05f,
                3, 1, 0.2f);

        particleSystemFireExplosion = new ParticleSystem(
                ship.getPosition(),
                ship.getForward(),
                0.01f, 0.005f,
                0.12f, 0.05f,
                2, 0.5f, (float) (2*Math.PI));

        particleSystemSmokeExplosion = new ParticleSystem(
                ship.getPosition(),
                ship.getForward(),
                0.015f, 0.004f,
                0.07f, 0.05f,
                3, 1, (float) (2*Math.PI));

        particleSystemRendererFire = new ParticleSystemRenderer();
        particleSystemRendererFire.initialize("resources/images/fire.png");

        particleSystemRendererSmoke = new ParticleSystemRenderer();
        particleSystemRendererSmoke.initialize("resources/images/smoke.png");

        particleSystemSmokeExplosion.setTimeToCreate(0.5);
        particleSystemFireExplosion.setTimeToCreate(0.5);
        registerKeys();
    }

    public void startGame(){
        score = 0;
        timePassed = 0;
        initTerrain();
    }

    private void initTerrain() {
        List<Vector2f> terrain = new ArrayList<>();
        terrain.add(new Vector2f(-1, 0));
        terrain.add(new Vector2f(1, 0));
        this.terrain = GameUtils.splitTerrain(terrain,  0.02f, 0.25f);
    }
    private void registerKeys() {
        inputKeyboard.registerCommand(GLFW_KEY_ENTER, true, (double elapsedTime) -> {
            if (gameState == GameState.MENU){
                switch (menuSelect) {
                    case PLAYGAME -> {
                        gameState = GameState.PLAYGAME;
                        startGame();
                    }
                    case HIGHSCORES -> gameState = GameState.HIGHSCORES;
                    case CUSTOMIZECONTROLS -> gameState = GameState.CUSTOMIZECONTROLS;
                    case CREDITS -> gameState = GameState.CREDITS;
                }
            }
        });
        // Register the inputs we want to have invoked
        inputKeyboard.registerCommand(GLFW_KEY_W, true, (double elapsedTime) -> {
            if (gameState == GameState.MENU) {
                menuSelect = menuSelect.previous();
            }
            else{
                makeMove(Movement.UP);
            }
        });
        inputKeyboard.registerCommand(GLFW_KEY_S, true, (double elapsedTime) -> {
            if (gameState == GameState.MENU) {
                menuSelect = menuSelect.next();
            }
            else{
                makeMove(Movement.DOWN);
            }
        });
        inputKeyboard.registerCommand(GLFW_KEY_UP, false, (double elapsedTime) -> {
            if (gameState == GameState.MENU) {
                menuSelect = menuSelect.previous();
            }
            else{
                makeMove(Movement.UP);
            }
        });
        inputKeyboard.registerCommand(GLFW_KEY_DOWN, false, (double elapsedTime) -> {
            if (gameState == GameState.MENU) {
                menuSelect = menuSelect.next();
            }
            else{
                makeMove(Movement.DOWN);
            }
        });
        inputKeyboard.registerCommand(GLFW_KEY_LEFT, false, (double elapsedTime) -> {
            makeMove(Movement.LEFT);
        });
        inputKeyboard.registerCommand(GLFW_KEY_RIGHT, false, (double elapsedTime) -> {
            makeMove(Movement.RIGHT);
        });

        inputKeyboard.registerCommand(GLFW_KEY_ESCAPE, true, (double elapsedTime) -> {
            glfwSetWindowShouldClose(graphics.getWindow(), true);
        });


        inputKeyboard.registerCommand(GLFW_KEY_BACKSPACE, true, (double elapsedTime) -> {
            gameState = GameState.MENU;
        });


        inputKeyboard.registerCommand(GLFW_KEY_F5, true, (double elapsedTime) -> {
            gameState = GameState.HIGHSCORES;
        });

        inputKeyboard.registerCommand(GLFW_KEY_F6, true, (double elapsedTime) -> {
            gameState = GameState.CREDITS;
        });
    }

    private void makeMove(Movement move){
        if (gameState != GameState.MENU) {
            switch (move){
                case RIGHT ->{
                    ship.setRotation(ship.getRotation()+ROTATION_SPEED);
                    particleSystemSmoke.setDirection(ship.getForward());
                    particleSystemFire.setDirection(ship.getForward());
                }
                case LEFT ->{
                    ship.setRotation(ship.getRotation()-ROTATION_SPEED);
                    particleSystemSmoke.setDirection(ship.getForward());
                    particleSystemFire.setDirection(ship.getForward());
                }
                case UP -> {
                    Vector2f thrust = ship.getForward().mul(THRUST);
                    ship.setAcceleration(thrust);
                    particleSystemSmoke.setCenter(ship.getBottom());
                    particleSystemFire.setCenter(ship.getBottom());
                    ship.setThrustActive(true);
                }
            }
        }
    }



    public void shutdown() {

    }

    public void run() {
        // Grab the first time
        double previousTime = glfwGetTime();

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!graphics.shouldClose()) {
            double currentTime = glfwGetTime();
            double elapsedTime = currentTime - previousTime;    // elapsed time is in seconds
            previousTime = currentTime;

            processInput(elapsedTime);
            update(elapsedTime);
            render(graphics.getWindow(), elapsedTime);
        }
    }

    private void processInput(double elapsedTime) {
        // Poll for window events: required in order for window, keyboard, etc events are captured.
        glfwPollEvents();
        inputKeyboard.update(elapsedTime);
    }

    private void update(double elapsedTime) {
        if (gameState == GameState.PLAYGAME){
            List<Vector2f> terrainClone = new ArrayList<>();
            for (Vector2f v: terrain){
                terrainClone.add(new Vector2f(v.x,v.y));
            }

            shipCrashed = GameUtils.hasCrashed(terrainClone,
                    new Vector2f(ship.getPosition().x, ship.getPosition().y),
                    ship.CHARACTER_WIDTH);

            if (!shipCrashed){
                timePassed += elapsedTime;
                ship.update(elapsedTime);
                particleSystemFire.update(elapsedTime, ship.isThrustActive());
                particleSystemSmoke.update(elapsedTime, ship.isThrustActive());
                ship.setAcceleration(GRAVITY);
                ship.setThrustActive(false);
            }
            else{
                particleSystemFireExplosion.setCenter(ship.getPosition());
                particleSystemSmokeExplosion.setCenter(ship.getPosition());
                particleSystemSmokeExplosion.updateWithTimeLimit(elapsedTime);
                particleSystemFireExplosion.updateWithTimeLimit(elapsedTime);
            }

        }
    }

    private void drawMenu(String text, float top, float left, float height) {
        String[] stringArr = text.split("\n");

        int idx = 0;
        for (String str: stringArr){
            float newTop = top + (idx * height);
            if (menuSelect.ordinal() == idx){
                graphics.drawTextByHeight(font, str, left, newTop, height, Color.YELLOW);
            }
            else{
                graphics.drawTextByHeight(font, str, left, newTop, height, TEXT_COLOR);
            }
            idx++;
        }
    }

    private void drawMenu(){
        String menuText =
                """
                    Start a New Game
                    View High Scores
                    Customize Controls
                    View Credits
                """;

        StringBuilder menuBuilder = new StringBuilder();
        menuBuilder.append(menuText);
        drawMenu(menuBuilder.toString(), MENU_TOP, MENU_LEFT, TEXT_HEIGHT);
    }

    private void renderTerrain(){

        for (int i = 0; i < terrain.size() - 1; i++){
            Vector2f pt1 = terrain.get(i);
            Vector2f pt2 = terrain.get(i+1);

            Vector3f tpt1 = new Vector3f(pt1.x, pt1.y, 0);
            Vector3f tpt2 = new Vector3f(pt2.x, pt2.y, 0);
            Vector3f tpt3 = new Vector3f(pt1.x, 1, 0);
            Triangle t1 = new Triangle(tpt1, tpt2, tpt3);

            Vector3f t2pt1 = new Vector3f(pt1.x, 1, 0);
            Vector3f t2pt2 = new Vector3f(pt2.x, pt2.y, 0);
            Vector3f t2pt3 = new Vector3f(pt2.x, 1, 0);
            Triangle t2 = new Triangle(t2pt1, t2pt2, t2pt3);

            graphics.draw(t1, Color.YELLOW);
            graphics.draw(t2, Color.YELLOW);
        }
    }

    private void renderShip(){
        Rectangle r = new Rectangle(ship.getPosition().x - (ship.CHARACTER_WIDTH / 2),
                ship.getPosition().y - (ship.CHARACTER_HEIGHT / 2),
                ship.CHARACTER_WIDTH,
                ship.CHARACTER_HEIGHT);
        graphics.draw(r, ship.getRotation(), ship.getPosition(), Color.RED);
    }

    private void render(long window, double elapsedTime) {
        graphics.begin();

        switch (gameState) {
            case MENU -> {
                drawMenu();
            }
            case PLAYGAME -> {
                graphics.draw(bg, displayRect, Color.WHITE);
                renderTerrain();
                if (!shipCrashed){
                    renderShip();
                    particleSystemRendererSmoke.render(graphics, particleSystemSmoke);
                    particleSystemRendererFire.render(graphics, particleSystemFire);
                }
                else{
                    particleSystemRendererSmoke.render(graphics, particleSystemSmokeExplosion);
                    particleSystemRendererFire.render(graphics, particleSystemFireExplosion);
                }

            }
        }
        graphics.end();
    }

}
