import java.util.*;

public class MazeUtils {

    public static void setupMaze(MazeCell[][] maze) {
        int mazeSize = maze.length;
        Set<List<Integer>> notInMaze = new HashSet<>();
        Set<List<Integer>> frontier = new HashSet<>();

        for (int x = 0; x < mazeSize; x++) {
            for (int y = 0; y < mazeSize; y++) {
                notInMaze.add(List.of(x, y));
            }
        }

        List<Integer> startCell = List.of(0,0);
        //Add cell to maze, add its neighbors to the frontier
        notInMaze.remove(startCell);

        HashMap<String, List<Integer>> neighbors = MazeUtils.getNeighbors(startCell, mazeSize);
        for (Map.Entry<String, List<Integer>> entry: neighbors.entrySet()) {
            frontier.add(entry.getValue());
        }

        while (!notInMaze.isEmpty()) {
            //Now select a cell from the frontier and add it to the maze
            List<Integer> frontierCell = MazeUtils.getRandomFromHashSet(frontier);
            notInMaze.remove(frontierCell);
            frontier.remove(frontierCell);

            //Get neighbors of selected frontier cell
            HashMap<String, List<Integer>> frontierNeighbors = MazeUtils.getNeighbors(frontierCell, mazeSize);

            //Get the neighbors of the frontier cell that are in the maze, add neighbors not in maze to frontier
            HashMap<String, List<Integer>> frontierNeighborsInMaze = new HashMap<>();
            for (Map.Entry<String, List<Integer>> entry: frontierNeighbors.entrySet()) {
                if (!notInMaze.contains(entry.getValue())) {
                    frontierNeighborsInMaze.put(entry.getKey(), entry.getValue());
                }
                else{
                    frontier.add(entry.getValue());
                }
            }

            //Randomly select one of the frontier neighbors in the maze to connect to
            Map.Entry<String, List<Integer>> randomNeighbor = MazeUtils.getRandomFromHashMap(frontierNeighborsInMaze);

            //Now connect the frontier cell with that random neighbor
            List<Integer> randomNeighborIdx = randomNeighbor.getValue();

            switch (randomNeighbor.getKey()) {
                case "Top" -> {
                    maze[frontierCell.getFirst()][frontierCell.getLast()]
                            .setTop(maze[randomNeighborIdx.getFirst()][randomNeighborIdx.getLast()]);
                    maze[randomNeighborIdx.getFirst()][randomNeighborIdx.getLast()]
                            .setBottom(maze[frontierCell.getFirst()][frontierCell.getLast()]);
                }
                case "Bottom" -> {
                    maze[frontierCell.getFirst()][frontierCell.getLast()]
                            .setBottom(maze[randomNeighborIdx.getFirst()][randomNeighborIdx.getLast()]);
                    maze[randomNeighborIdx.getFirst()][randomNeighborIdx.getLast()]
                            .setTop(maze[frontierCell.getFirst()][frontierCell.getLast()]);
                }
                case "Left" -> {
                    maze[frontierCell.getFirst()][frontierCell.getLast()]
                            .setLeft(maze[randomNeighborIdx.getFirst()][randomNeighborIdx.getLast()]);
                    maze[randomNeighborIdx.getFirst()][randomNeighborIdx.getLast()]
                            .setRight(maze[frontierCell.getFirst()][frontierCell.getLast()]);
                }
                case "Right" -> {
                    maze[frontierCell.getFirst()][frontierCell.getLast()]
                            .setRight(maze[randomNeighborIdx.getFirst()][randomNeighborIdx.getLast()]);
                    maze[randomNeighborIdx.getFirst()][randomNeighborIdx.getLast()]
                            .setLeft(maze[frontierCell.getFirst()][frontierCell.getLast()]);
                }
            }
            notInMaze.remove(randomNeighborIdx);
            frontier.remove(randomNeighborIdx);
        }
    }

    public static List<MazeCell> updateShortestPath(MazeCell[][] maze, MazeCell characterLocation, MazeCell endLocation) {
        Queue<List<MazeCell>> queue = new ArrayDeque<>();
        Set<List<Integer>> visited = new HashSet<>();
        List<MazeCell> path = new ArrayList<>();
        path.add(characterLocation);
        MazeCell cell = path.getLast();
        while (!cell.getIndex().equals(endLocation.getIndex())) {
            if (!visited.contains(cell.getIndex())){
                visited.add(cell.getIndex());
                if (cell.getTop() != null){
                    List<MazeCell> newPath = new ArrayList<>(path);
                    MazeCell nextCell = cell.getTop();
                    newPath.add(nextCell);
                    queue.add(newPath);
                }
                if (cell.getBottom() != null){
                    List<MazeCell> newPath = new ArrayList<>(path);
                    MazeCell nextCell = cell.getBottom();
                    newPath.add(nextCell);
                    queue.add(newPath);
                }
                if (cell.getRight() != null){
                    List<MazeCell> newPath = new ArrayList<>(path);
                    MazeCell nextCell = cell.getRight();
                    newPath.add(nextCell);
                    queue.add(newPath);
                }
                if (cell.getLeft() != null){
                    List<MazeCell> newPath = new ArrayList<>(path);
                    MazeCell nextCell = cell.getLeft();
                    newPath.add(nextCell);
                    queue.add(newPath);
                }
            }
            path = queue.remove();
            cell = path.getLast();

        }

        for (MazeCell[] row: maze){
            for (MazeCell iCell:row){
                iCell.setOnShortestPath(false);
            }
        }

        for (MazeCell iCell: path){
            iCell.setOnShortestPath(true);
        }

        return path;
    }

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
