package ecs.Components;

import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class Position extends Component {
    public List<Vector2i> previousPositions = new ArrayList<Vector2i>();
    public Vector2i position;

    public Position(int x, int y) {
        this.position = new Vector2i(x, y);
    }

    public void undoMove(){
        this.position = previousPositions.getLast();
    }

    public int getX() {
        return position.x;
    }

    public int getY() {
        return position.y;
    }
}
