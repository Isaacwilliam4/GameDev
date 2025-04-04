package ecs.Components;

public class Movable extends Component {

    public enum Direction {
        Stopped,
        Up,
        Down,
        Left,
        Right
    }

    public float moveDist;
    public Direction pendingMove;

    public Movable(float moveDist, Direction pendingMove) {
        this.moveDist = moveDist;
        this.pendingMove = pendingMove;
    }
}
