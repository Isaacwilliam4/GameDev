import org.joml.Vector2f;

public class Ship {
    private Vector2f position;
    private Vector2f velocity;
    private Vector2f acceleration;
    private float rotation;

    public Ship(Vector2f position, Vector2f velocity, Vector2f acceleration, float rotation) {
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.rotation = rotation;
    }

    public void update(double timeElapsed) {
        float timeElapsedf = (float) timeElapsed;
        Vector2f accelerationdt = new Vector2f(acceleration).mul(timeElapsedf);
        Vector2f velocitydt = new Vector2f(0,0);
        velocitydt.add(accelerationdt);
        velocitydt.mul(timeElapsedf);
        velocity.add(velocitydt);
        Vector2f positiondt = new Vector2f(0,0);
        positiondt.add(velocity);
        positiondt.mul(timeElapsedf);
        position.add(positiondt);
    }

    public Vector2f getPosition() {
        return position;
    }

    public void setPosition(Vector2f position) {
        this.position = position;
    }

    public Vector2f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2f velocity) {
        this.velocity = velocity;
    }

    public Vector2f getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(Vector2f acceleration) {
        this.acceleration = acceleration;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }
}
