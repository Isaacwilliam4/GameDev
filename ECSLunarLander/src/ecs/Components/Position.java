package ecs.Components;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Position extends Component {
    public Vector3f position;

    public Position(float x, float y, float rotation) {
        position = new Vector3f(x, y, rotation);
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public float getRotation() {
        return position.z;
    }


}
