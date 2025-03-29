package ecs.Components;

import edu.usu.graphics.Color;
import edu.usu.graphics.Texture;

public class Appearance extends Component {
    public Texture image;
    public Color color;
    public float width;
    public float height;

    public Appearance(Texture image, Color color, float width, float height) {
        this.image = image;
        this.color = color;
        this.width = width;
        this.height = height;
    }
}
