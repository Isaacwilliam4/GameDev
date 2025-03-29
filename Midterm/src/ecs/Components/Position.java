package ecs.Components;

import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class Position extends Component {
    public List<Vector2i> segments = new ArrayList<>();

    public Position(int x, int y) {
        segments.add(new Vector2i(x, y));
    }

    public int getX() {
        return segments.get(0).x;
    }

    public int getY() {
        return segments.get(0).y;
    }
}
