package Util;

import edu.usu.graphics.Color;
import edu.usu.graphics.Graphics2D;
import edu.usu.graphics.Rectangle;
import org.joml.Vector2f;

import java.util.*;

public class GameUtils {
    public static List<Vector2f> splitTerrain(List<Vector2f> terrain,
                                        float minDist,
                                        float roughnessFactor
    ){
        List<Vector2f> newTerrain = new ArrayList<>(terrain);
        Vector2f left = terrain.get(0);
        Vector2f right = terrain.get(1);
            if (Math.abs(left.x - right.x) < minDist){
            return terrain;
        }
        Random rand = new Random();
        float val = (float) rand.nextGaussian();

        float r = roughnessFactor * val * Math.abs(left.x - right.x);
        float y = 0.5f*(left.y + right.y) + r;

        Vector2f midPoint = new Vector2f((left.x + right.x) / 2f, y);
        int midpointIdx = 1;
        newTerrain.add(midpointIdx, midPoint);

        List<Vector2f> leftLine = newTerrain.subList(0, midpointIdx+1);
        List<Vector2f> rightLine = newTerrain.subList(midpointIdx, midpointIdx+2);

        List<Vector2f> leftList = splitTerrain(leftLine,minDist, roughnessFactor);
        List<Vector2f> rightList = splitTerrain(rightLine, minDist, roughnessFactor);

        leftList.addAll(rightList.subList(1, rightList.size()));
        return leftList;
    }

    private static boolean lineCircleIntersection(Vector2f pt1, Vector2f pt2, Vector2f circleCenter, float circleRadius) {
        pt1 = new Vector2f(pt1.x, pt1.y);
        pt2 = new Vector2f(pt2.x, pt2.y);
        circleCenter = new Vector2f(circleCenter.x, circleCenter.y);
        // Translate points to circle's coordinate system
        Vector2f d = pt2.sub(pt1); // Direction vector of the line
        Vector2f f = pt1.sub(circleCenter); // Vector from circle center to the start of the line

        float a = d.dot(d);
        float b = 2 * f.dot(d);
        float c = f.dot(f) - circleRadius * circleRadius;

        float discriminant = b * b - 4 * a * c;

        // If the discriminant is negative, no real roots and thus no intersection
        if (discriminant < 0) {
            return false;
        }

        // Check if the intersection points are within the segment
        discriminant = (float) Math.sqrt(discriminant);
        float t1 = (-b - discriminant) / (2 * a);
        float t2 = (-b + discriminant) / (2 * a);

        if (t1 >= 0 && t1 <= 1) {
            return true;
        }
        if (t2 >= 0 && t2 <= 1) {
            return true;
        }

        return false;
    }

    public static boolean hasCrashed(List<Vector2f> terrain, Vector2f position, float characterWidth, Graphics2D graphics) {
        for (int i = 0; i < terrain.size()-1; i++) {
            Vector2f pt1 = terrain.get(i);
            Vector2f pt2 = terrain.get(i+1);

            if (lineCircleIntersection(pt1, pt2, position, characterWidth)){
                return true;
            }
        }
        return false;
    }
}
