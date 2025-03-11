package ecs.Components;

public class Movable extends Component {

    public enum Direction {
        Stopped,
        Up,
        Down,
        Left,
        Right
    }

    public Direction facing;
    public int segmentsToAdd = 0;
    public double moveInterval; // seconds
    public double elapsedInterval;

    public Movable(Direction facing, double moveInterval) {
        this.facing = facing;
        this.moveInterval =moveInterval;
    }
}
