package Util;

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
}
