package ecs.Components;

import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class Position extends Component {
    public List<Vector2f> previousPositions = new ArrayList<Vector2f>();
    public Vector2f position;
    public float width;
    public float height;

    public Position(float x, float y) {
        this.position = new Vector2f(x, y);
        this.width = width;
        this.height = height;
    }

    public void undoMove(){
        this.position = previousPositions.getLast();
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }
}
