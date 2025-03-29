package ecs.Components;

public class Movable extends Component {

    public enum Direction {
        Stopped,
        Up,
        Down,
        Left,
        Right
    }

    public double moveInterval; // seconds
    public Direction pendingMove;

    public Movable(double moveInterval) {
        this.moveInterval = moveInterval;
        this.pendingMove = Direction.Stopped;
    }
}
