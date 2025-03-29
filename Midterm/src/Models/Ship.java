package Models;

import org.joml.Vector2f;

public class Ship {
    public final float CHARACTER_HEIGHT = 0.05f;
    public final float CHARACTER_WIDTH = 0.05f;
    private Vector2f position;
    private Vector2f velocity;
    private Vector2f acceleration;
    private float rotation;
    private float fuel;
    private boolean thrustActive = false;

    public Ship(Vector2f position, Vector2f velocity, Vector2f acceleration, float rotation, float fuel) {
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.rotation = rotation;
        this.fuel = fuel;
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
        if (thrustActive) {
            fuel -= (float) timeElapsed;
        }
        else if (fuel <= 0){
            thrustActive = false;
        }
    }

    public Vector2f getBottom() {
        Vector2f _position = new Vector2f(position.x, position.y);
        Vector2f _bottom = new Vector2f(0, CHARACTER_WIDTH / 2);

        // Apply 2D rotation to _bottom
        float cosTheta = (float) Math.cos(rotation);
        float sinTheta = (float) Math.sin(rotation);

        float rotatedX = _bottom.x * cosTheta - _bottom.y * sinTheta;
        float rotatedY = _bottom.x * sinTheta + _bottom.y * cosTheta;

        // Translate the rotated vector to be relative to _position
        return new Vector2f(_position.x + rotatedX, _position.y + rotatedY);
    }


    public boolean isThrustActive() {
        return thrustActive;
    }

    public void setThrustActive(boolean thrustActive) {
        this.thrustActive = thrustActive;
    }

    public Vector2f getForward() {
        return new Vector2f((float)Math.cos(rotation + Math.PI / 2), (float)Math.sin(rotation + Math.PI / 2));
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

    public float getFuel() {
        return fuel;
    }

    public void setFuel(float fuel) {
        this.fuel = fuel;
    }
}
