package ecs.Components;

import org.joml.Vector2f;

public class Movable extends Component {
    public enum Move {
        UP, ROTATE_LEFT, ROTATE_RIGHT
    }

    public Vector2f velocity;
    public Vector2f acceleration;
    public float rotation;
    public float rotationSpeed;
//    public double moveInterval; // seconds
//    public double elapsedInterval;


    public Movable(Vector2f velocity, Vector2f acceleration, float rotation, float rotationSpeed) {
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.rotation = rotation;
        this.rotationSpeed = rotationSpeed;
//        this.moveInterval = moveInterval;
    }
}
