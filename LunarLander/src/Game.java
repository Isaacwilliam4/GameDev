import Enums.*;
import Models.ParticleSystem;
import Models.Ship;
import Util.GameUtils;
import Util.KeyboardInput;
import Util.ParticleSystemRenderer;
import Util.TimerRenderer;
import edu.usu.audio.Sound;
import edu.usu.audio.SoundManager;
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
    private GameState pendingGameState = GameState.MENU;
    private float LEVEL1_WIDTH = 0.1f;
    private float LEVEL2_WIDTH = 0.05f;

    private Menu menuSelect = Menu.NONE;
    private PauseSelect pauseSelect = PauseSelect.CONTINUE;
    private List<Vector2f> terrain;
    private Font font;
    private final float MAX_ROTATION = 5f;
    private final float MAX_VELOCITY = 0.02f;
    private float characterRotation = 0f;
    private Vector2f characterLocation = new Vector2f(0f, 0.3f);
    private Ship ship;
    private float ROTATION_SPEED = 0.025f;
    private Vector2f GRAVITY = new Vector2f(0f, 1.0f);
    private float THRUST = -1.2f;
    private  Texture bg;
    private  Texture lunarLander;
    private final Rectangle displayRect = new Rectangle(-1, -1, 2, 2, -1.0f);
    private ParticleSystem particleSystemFire;
    private ParticleSystem particleSystemSmoke;
    private ParticleSystem particleSystemFireExplosion;
    private ParticleSystem particleSystemSmokeExplosion;
    private ParticleSystemRenderer particleSystemRendererFire;
    private ParticleSystemRenderer particleSystemRendererSmoke;
    private boolean shipCrashed;
    private boolean shipLanded;
    private float safeZoneWidth = 0.1f;
    private Level level = Level.LEVEL_1;
    private HashSet<Integer> safeZoneIdxs = new HashSet<>();
    private TimerRenderer timerRenderer = new TimerRenderer(3);
    private TimerRenderer endGameRnderer = new TimerRenderer(1);
    private boolean startNewLevel;
    private boolean scoreAdded;
    private boolean finalScoreAdded;
    private SoundManager audio;
    private Sound shipThrust;
    private Sound safeLanding;
    private Sound crash;
    private boolean victorySoundPlayed;
    private boolean explosionSoundPlayed;

    public Game(Graphics2D graphics) {
        this.graphics = graphics;
        this.inputKeyboard = new KeyboardInput(graphics.getWindow());
    }

    public void initialize() {
        audio = new SoundManager();
        shipThrust = audio.load("thrust", "resources/sounds/thrust.ogg", true);
        safeLanding = audio.load("safeLanding", "resources/sounds/finished.ogg", false);
        crash = audio.load("explosion", "resources/sounds/explosion.ogg", false);
        lunarLander = new Texture("resources/images/lunarLander.png");
        bg = new Texture("resources/images/spacebg.jpg");
        font = new Font("resources/fonts/Blacknorthdemo-mLE25.otf", 100, false);
        particleSystemRendererFire = new ParticleSystemRenderer();
        particleSystemRendererFire.initialize("resources/images/fire.png");

        particleSystemRendererSmoke = new ParticleSystemRenderer();
        particleSystemRendererSmoke.initialize("resources/images/smoke.png");
        resetGame();
    }

    private void resetLevel(){
        explosionSoundPlayed = false;
        victorySoundPlayed = false;
        startNewLevel = true;
        shipLanded = false;
        shipCrashed = false;
        endGameRnderer.reset();
        timerRenderer.reset();
        scoreAdded = false;
        finalScoreAdded = false;

        ship = new Ship(new Vector2f(0f, -0.5f),
                new Vector2f(0f, 0f),
                GRAVITY,
                (float) Math.PI / 2f,
                10
        );
        particleSystemFire = new ParticleSystem(
                ship.getBottom(),
                ship.getForward(),
                0.01f, 0.005f,
                0.12f, 0.05f,
                2, 0.5f, 0.1f);

        particleSystemSmoke = new ParticleSystem(
                ship.getBottom(),
                ship.getForward(),
                0.01f, 0.005f,
                0.12f, 0.05f,
                2, 0.5f, 0.1f);

        particleSystemFireExplosion = new ParticleSystem(
                ship.getPosition(),
                ship.getForward(),
                0.01f, 0.005f,
                0.12f, 0.05f,
                1f, 0.5f, (float) (2*Math.PI));

        particleSystemSmokeExplosion = new ParticleSystem(
                ship.getPosition(),
                ship.getForward(),
                0.015f, 0.004f,
                0.07f, 0.05f,
                1, .5f, (float) (2*Math.PI));
        particleSystemSmokeExplosion.setTimeToCreate(0.2);
        particleSystemFireExplosion.setTimeToCreate(0.2);
        initTerrain();
    }

    private void resetGame(){
        scoreAdded = false;
        score = 0;
        timePassed = 0;
        menuSelect = Menu.NONE;
        resetLevel();
    }

    private boolean terrainIsGood(){
        for (Vector2f v : terrain){
            if (v.y > 0.5f | v.y < -0.5f){
                return false;
            }
        }
        return true;
    }

    private void initTerrain() {
        List<Vector2f> terrain = new ArrayList<>();
        terrain.add(new Vector2f(-1, 0));
        terrain.add(new Vector2f(1, 0));
        this.safeZoneIdxs.clear();
        boolean terrainIsGood = false;
        switch (level) {
            case LEVEL_1 -> {
                while (!terrainIsGood){
                    this.terrain = GameUtils.splitTerrain(terrain,  0.02f, 0.3f);
                    terrainIsGood = terrainIsGood();
                }
                Random rand = new Random();
                int midIdx = this.terrain.size() / 2;
                int safeZoneIdx1 = rand.nextInt(5, midIdx - 20);
                int safeZoneIdx2 = rand.nextInt(midIdx, this.terrain.size() - 50);
                this.safeZoneIdxs.addAll(GameUtils.addSafeZone(this.terrain, safeZoneIdx1, LEVEL1_WIDTH));
                this.safeZoneIdxs.addAll(GameUtils.addSafeZone(this.terrain, safeZoneIdx2, LEVEL1_WIDTH));
            }
            case LEVEL_2 -> {
                while (!terrainIsGood){
                    this.terrain = GameUtils.splitTerrain(terrain,  0.02f, 0.3f);
                    terrainIsGood = terrainIsGood();
                }
                Random rand = new Random();
                int safeZoneIdx1 = rand.nextInt(50, this.terrain.size() - 50);
                this.safeZoneIdxs = new HashSet<>();
                this.safeZoneIdxs.addAll(GameUtils.addSafeZone(this.terrain, safeZoneIdx1, LEVEL2_WIDTH));
            }

        }

    }

    private void registerKeys() {
        inputKeyboard.clear();
        switch (gameState) {
            case MENU -> {
                inputKeyboard.registerCommand(GLFW_KEY_ENTER, true, (double elapsedTime) -> {
                    switch (menuSelect) {
                        case PLAYGAME -> {
                            pendingGameState = GameState.PLAYGAME;
                            resetGame();
                        }
                        case HIGHSCORES -> pendingGameState = GameState.HIGHSCORES;
                        case CUSTOMIZECONTROLS -> pendingGameState = GameState.CUSTOMIZECONTROLS;
                        case CREDITS -> pendingGameState = GameState.CREDITS;
                    }
                });
                inputKeyboard.registerCommand(GLFW_KEY_W, true, (double elapsedTime) -> menuSelect = menuSelect.previous());
                inputKeyboard.registerCommand(GLFW_KEY_S, true, (double elapsedTime) -> menuSelect = menuSelect.next());
                inputKeyboard.registerCommand(GLFW_KEY_UP, true, (double elapsedTime) -> menuSelect = menuSelect.previous());
                inputKeyboard.registerCommand(GLFW_KEY_DOWN, true, (double elapsedTime) -> menuSelect = menuSelect.next());
            }
            case PLAYGAME -> {
                inputKeyboard.registerBothCommand(GLFW_KEY_UP,  (double elapsedTime) -> makeMove(Movement.UP), (double elapsedTime) -> ship.setThrustActive(false));
                inputKeyboard.registerCommand(GLFW_KEY_DOWN, false, (double elapsedTime) -> makeMove(Movement.DOWN));
                inputKeyboard.registerCommand(GLFW_KEY_LEFT, false, (double elapsedTime) -> makeMove(Movement.LEFT));
                inputKeyboard.registerCommand(GLFW_KEY_RIGHT, false, (double elapsedTime) -> makeMove(Movement.RIGHT));
                inputKeyboard.registerCommand(GLFW_KEY_ESCAPE, true, (double elapsedTime) -> pendingGameState = GameState.GAMEPAUSED);
            }
            case GAMEPAUSED -> {
                inputKeyboard.registerCommand(GLFW_KEY_ENTER, true, (double elapsedTime) -> {
                    switch (pauseSelect) {
                        case CONTINUE -> pendingGameState = GameState.PLAYGAME;
                        case QUIT -> pendingGameState = GameState.MENU;
                    }
                });
                inputKeyboard.registerCommand(GLFW_KEY_UP, true, (double elapsedTime) -> pauseSelect = pauseSelect.previous());
                inputKeyboard.registerCommand(GLFW_KEY_DOWN, true, (double elapsedTime) -> pauseSelect = pauseSelect.next());
            }
            case HIGHSCORES, CUSTOMIZECONTROLS, CREDITS, ENDGAME -> {
                inputKeyboard.registerCommand(GLFW_KEY_ESCAPE, true, (double elapsedTime) -> pendingGameState = GameState.MENU);
            }
        }
    }

    private void updateGameState() {
        if (pendingGameState != null) {
            gameState = pendingGameState;
            pendingGameState = null;
            registerKeys();
        }
    }


    private void makeMove(Movement move){
        if (gameState != GameState.MENU & !shipLanded & ship.getFuel() > 0) {
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
        timePassed += elapsedTime;
        updateGameState();
        switch (gameState) {
            case PLAYGAME -> {
                if (ship.getFuel() < 0){
                    ship.setThrustActive(false);
                }
                shipLanded = GameUtils.hasLanded(terrain,
                        ship.getPosition(),
                        ship.CHARACTER_WIDTH / 2.5f,
                        ship,
                        safeZoneIdxs,
                        MAX_ROTATION,
                        MAX_VELOCITY);
                if (shipLanded){
                    switch (level) {
                        case LEVEL_1 -> {
                            if (!scoreAdded){
                                score += 1000;
                                score += (float) (10000 / timePassed);
                                scoreAdded = true;
                            }
                            timerRenderer.update(elapsedTime);
                            if (timerRenderer.isDone()){
                                level = level.next();
                                resetLevel();
                            }
                        }
                        case LEVEL_2 -> {
                            if (!scoreAdded){
                                score += 2000;
                                score += (float) (20000 / timePassed);
                                scoreAdded = true;
                            }
                            pendingGameState = GameState.ENDGAME;
                        }
                    }
                }
                else{
                    shipCrashed = GameUtils.hasCrashed(terrain,
                            ship.getPosition(),
                            ship.CHARACTER_WIDTH / 2.5f);
                    if (startNewLevel){
                        elapsedTime = 0;
                        startNewLevel = false;
                    }
                    if (!shipCrashed){
                        if (ship.isThrustActive()){
                            Vector2f thrust = ship.getForward().mul(THRUST);
                            ship.setAcceleration(thrust);
                            particleSystemSmoke.setCenter(ship.getBottom());
                            particleSystemFire.setCenter(ship.getBottom());
                        }
                        timePassed += elapsedTime;
                        ship.update(elapsedTime);
                        particleSystemFire.update(elapsedTime, ship.isThrustActive());
                        particleSystemSmoke.update(elapsedTime, ship.isThrustActive());
                        ship.setAcceleration(GRAVITY);
                    }
                    else{
                        endGameRnderer.update(elapsedTime);
                        if (endGameRnderer.isDone()){
                            pendingGameState = GameState.ENDGAME;
                        }
                        particleSystemFireExplosion.setCenter(ship.getPosition());
                        particleSystemSmokeExplosion.setCenter(ship.getPosition());
                        particleSystemSmokeExplosion.updateWithTimeLimit(elapsedTime);
                        particleSystemFireExplosion.updateWithTimeLimit(elapsedTime);
                    }
                }
            }

            case ENDGAME -> {
                level = Level.LEVEL_1;
                if (!finalScoreAdded){
                    scoreList.add(String.format("%.0f", score) + "\n");
                    finalScoreAdded = true;
                }
            }
        }
    }

    private void drawSelect(String text, Enum selected){
        String[] stringArr = text.split("\n");
        int idx = 0;
        for (String str: stringArr){
            float newTop = MENU_TOP + (idx * TEXT_HEIGHT);
            if (selected.ordinal() == idx){
                graphics.drawTextByHeight(font, str, MENU_LEFT, newTop, TEXT_HEIGHT, Color.YELLOW);
            }
            else{
                graphics.drawTextByHeight(font, str, MENU_LEFT, newTop, TEXT_HEIGHT, TEXT_COLOR);
            }
            idx++;
        }
    }

    private void drawText(String text){
        String[] stringArr = text.split("\n");
        int idx = 0;
        for (String str: stringArr){
            float newTop = MENU_TOP + (idx * TEXT_HEIGHT);
            graphics.drawTextByHeight(font, str, MENU_LEFT, newTop, TEXT_HEIGHT, TEXT_COLOR);
            idx++;
        }
    }

    private void drawTextWithLeftTop(String text, float left, float top){
        String[] stringArr = text.split("\n");
        int idx = 0;
        for (String str: stringArr){
            float newTop = top + (idx * TEXT_HEIGHT);
            graphics.drawTextByHeight(font, str, left, newTop, TEXT_HEIGHT, TEXT_COLOR);
            idx++;
        }
    }

    private void drawTextWithLeftTopAndColor(String text, float left, float top, float textHeight, List<Color> colors){
        String[] stringArr = text.split("\n");
        int idx = 0;
        for (String str: stringArr){
            float newTop = top + (idx * textHeight);
            graphics.drawTextByHeight(font, str, left, newTop, textHeight, colors.get(idx));
            idx++;
        }
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

            Color gray = new Color(0.5f, 0.5f, 0.5f);
            graphics.draw(t1, gray);
            graphics.draw(t2, gray);
        }

        for (int i = 0; i < terrain.size() - 1; i++) {
            Vector2f pt1 = terrain.get(i);
            Vector2f pt2 = terrain.get(i + 1);

            float dist = pt1.distance(pt2);

            float angle = (float) Math.atan2(pt2.y - pt1.y, pt2.x - pt1.x); // Get angle between points
//            float angle = (float) 0; // Get angle between points

            Color color = Color.WHITE;
            if (safeZoneIdxs.contains(i) & safeZoneIdxs.contains(i + 1)){
                color = Color.GREEN;
            }

            Rectangle r = new Rectangle(pt1.x, pt1.y, 0.003f, dist + 0.002f); // Increased height for visibility
            graphics.draw(r, angle - (float)(Math.PI / 2), pt1, color); // Rotate around pt1
        }

    }

    private void renderStatus(){

        Color fuelColor = Color.GREEN;
        Color velocityColor = Color.GREEN;
        Color angleColor = Color.RED;
        float angleDegrees = (float) (Math.abs(Math.toDegrees(ship.getRotation())) % 360);
        if (ship.getFuel() < 0){
            fuelColor = Color.RED;
        }
        if (ship.getVelocity().length() > MAX_VELOCITY){
            velocityColor = Color.RED;
        }
        if (angleDegrees > 355 | angleDegrees < 5){
            angleColor = Color.GREEN;
        }

        List<Color> colors = new ArrayList<>();
        colors.add(fuelColor);
        colors.add(velocityColor);
        colors.add(angleColor);

        String text = String.format(
                "Ship Fuel: %.2f%nShip Velocity: %.2f%nShip Angle: %.2f%n",
                Math.max(ship.getFuel(), 0),
                ship.getVelocity().length() * 100,
                angleDegrees
        );

        drawTextWithLeftTopAndColor(text, .5f, -.5f, 0.028f,colors);
    }

    private void renderShip(){
        if (ship.isThrustActive()){
            if (!shipThrust.isPlaying()){
                shipThrust.play();
            }
        }
        Rectangle r = new Rectangle(ship.getPosition().x - (ship.CHARACTER_WIDTH / 2),
                ship.getPosition().y - (ship.CHARACTER_HEIGHT / 2),
                ship.CHARACTER_WIDTH,
                ship.CHARACTER_HEIGHT);
        graphics.draw(lunarLander, r, ship.getRotation(), ship.getPosition(), Color.WHITE);
//        graphics.draw(r, ship.getRotation(), ship.getPosition(), Color.RED);
    }

    private void render(long window, double elapsedTime) {
        graphics.begin();

        if (!ship.isThrustActive() | shipCrashed | shipLanded){
            if (shipThrust.isPlaying()){
                shipThrust.stop();
            }
        }
        switch (gameState) {
            case MENU -> {
                String menuText =
                        """
                            Start a New Game
                            View High Scores
                            Customize Controls
                            View Credits
                        """;
                drawSelect(menuText, menuSelect);
            }
            case PLAYGAME -> {
                renderStatus();
                graphics.draw(bg, displayRect, Color.WHITE);
                renderTerrain();
                if (shipLanded){
                    if (!safeLanding.isPlaying() & !victorySoundPlayed){
                        safeLanding.play();
                        victorySoundPlayed = true;
                    }
                    if (Objects.requireNonNull(level) == Level.LEVEL_1) {
                        this.timerRenderer.render(graphics, font);
                    }
                    renderShip();
                }
                else{
                    if (!shipCrashed){
                        renderShip();
                        particleSystemRendererSmoke.render(graphics, particleSystemSmoke);
                        particleSystemRendererFire.render(graphics, particleSystemFire);
                    }
                    else{
                        if (!crash.isPlaying() & !explosionSoundPlayed){
                            crash.play();
                        }
                        particleSystemRendererSmoke.render(graphics, particleSystemSmokeExplosion);
                        particleSystemRendererFire.render(graphics, particleSystemFireExplosion);
                    }
                }
            }
            case GAMEPAUSED -> {
                String pauseText =
                        """
                            Continue
                            Quit
                        """;
                drawSelect(pauseText, pauseSelect);
            }
            case CREDITS -> {
                drawText("Game made by Isaac Peterson.");
            }
            case HIGHSCORES -> {
                StringBuilder highscores = new StringBuilder();
                highscores.append("High Scores \n");
                for (String s: scoreList){
                    highscores.append(s);
                }
                drawText(highscores.toString());
            }
            case ENDGAME -> {
                drawText("Game Over\n Your Score: " + String.format("%.0f", score) + "\n" + "Press Esc to Return to Menu\n");
            }
        }
        graphics.end();
    }

}
