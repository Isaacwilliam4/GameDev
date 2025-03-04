import edu.usu.graphics.*;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class Game {
    private final Graphics2D graphics;

    private ParticleSystem particleSystemFire;
    private ParticleSystem particleSystemSmoke;
    private ParticleSystemRenderer particleSystemRendererFire;
    private ParticleSystemRenderer particleSystemRendererSmoke;

    public Game(Graphics2D graphics) {
        this.graphics = graphics;
    }

    public void initialize() {
        particleSystemFire = new ParticleSystem(
                new Vector2f(0, 0),
                0.01f, 0.005f,
                0.12f, 0.05f,
                2, 0.5f);

        particleSystemSmoke = new ParticleSystem(
                new Vector2f(0, 0),
                0.015f, 0.004f,
                0.07f, 0.05f,
                3, 1);

        particleSystemRendererFire = new ParticleSystemRenderer();
        particleSystemRendererFire.initialize("resources/images/fire.png");

        particleSystemRendererSmoke = new ParticleSystemRenderer();
        particleSystemRendererSmoke.initialize("resources/images/smoke.png");
    }

    public void shutdown() {
        particleSystemRendererFire.cleanup();
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

        if (glfwGetKey(graphics.getWindow(), GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            glfwSetWindowShouldClose(graphics.getWindow(), true);
        }
    }

    private void update(double elapsedTime) {

        particleSystemFire.update(elapsedTime);
        particleSystemSmoke.update(elapsedTime);
    }

    private void render(long window, double elapsedTime) {
        graphics.begin();

        particleSystemRendererSmoke.render(graphics, particleSystemSmoke);
        particleSystemRendererFire.render(graphics, particleSystemFire);

        graphics.end();
    }
}
