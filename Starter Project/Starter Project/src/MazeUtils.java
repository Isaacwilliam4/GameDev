import java.util.*;

public class MazeUtils {
    public static Map.Entry<String, List<Integer>> getRandomFromHashMap(HashMap<String, List<Integer>> set){
        Random rand = new Random();
        int randInt = rand.nextInt(set.size());
        int idx = 0;
        Map.Entry<String, List<Integer>> randCell = null;

        for (Map.Entry<String, List<Integer>> entry: set.entrySet()) {
            if (randInt == idx){
                randCell = entry;
                break;
            }
            idx++;
        }

        return randCell;
    }

    public static List<Integer> getRandomFromHashSet(Set<List<Integer>> set){
        Random rand = new Random();
        int randInt = rand.nextInt(set.size());
        int idx = 0;
        List<Integer> randCell = null;

        for (List<Integer> value: set) {
            if (randInt == idx){
                randCell = value;
                break;
            }
            idx++;
        }

        return randCell;
    }

    public static void cliRender(MazeCell[][] maze){
        int mazeSize = maze.length;
        StringBuilder[][] cliMaze = new StringBuilder[mazeSize][mazeSize];
        for (int x = 0; x < cliMaze.length; x++) {
            for (int y = 0; y < cliMaze[x].length; y++) {
                cliMaze[x][y] = new StringBuilder("    ");
            }
        }

        for (var row: maze){
            for (var cell:row){
                cliRenderCell(cell, cliMaze);
            }
        }

        StringBuilder cliMazeText = new StringBuilder();
        for (int x = 0; x < cliMaze.length; x++) {
            for (int y = 0; y < cliMaze[0].length; y++) {
                cliMazeText.append(cliMaze[x][y].toString());
            }
            cliMazeText.append("\n");
        }

        System.out.println(cliMazeText);
    }

    private static void cliRenderCell(MazeCell cell, StringBuilder[][] cliMaze) {
        if (cell.getTop() == null){
            if ((cell.getRow() - 1) >= 0){
                StringBuilder builder = cliMaze[cell.getRow()-1][cell.getColumn()];
                builder.replace(1, 3,"__");
            }
        }
        if (cell.getBottom() == null){
            StringBuilder builder = cliMaze[cell.getRow()][cell.getColumn()];
            builder.replace(1, 3,"__");
        }
        if (cell.getLeft() == null){
            cliMaze[cell.getRow()][cell.getColumn()].replace(0, 1,"|");
        }
        if (cell.getRight() == null){
            cliMaze[cell.getRow()][cell.getColumn()].replace(3, 4,"|");
        }
    }

    public static HashMap<String, List<Integer>> getNeighbors(List<Integer> block, int mazeSize) {
        HashMap<String, List<Integer>> neighbors = new HashMap<>();
        if ((block.getLast() - 1) >= 0){
            neighbors.put("Left", List.of(block.getFirst(), block.getLast() - 1));
        }
        if ((block.getLast() + 1) < mazeSize){
            neighbors.put("Right", List.of(block.getFirst(), block.getLast() + 1));
        }
        if ((block.getFirst() - 1) >= 0){
            neighbors.put("Top", List.of(block.getFirst() - 1, block.getLast()));
        }
        if ((block.getFirst() + 1) < mazeSize){
            neighbors.put("Bottom", List.of(block.getFirst() + 1, block.getLast()));
        }
        return neighbors;
    }

    public static boolean areNeighbors(MazeCell cell1, MazeCell cell2){
        if (cell1.getTop() != null){
            if (cell1.getTop().equals(cell2)){
                return true;
            }
        }
        if (cell1.getBottom() != null){
            if (cell1.getBottom().equals(cell2)){
                return true;
            }
        }
        if (cell1.getRight() != null){
            if (cell1.getRight().equals(cell2)){
                return true;
            }
        }
        if (cell1.getLeft() != null){
            if (cell1.getLeft().equals(cell2)){
                return true;
            }
        }
        return false;
    }
}
