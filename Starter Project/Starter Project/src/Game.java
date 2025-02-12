import com.sun.tools.javac.Main;
import edu.usu.graphics.*;
import edu.usu.graphics.Color;
import edu.usu.graphics.Graphics2D;
import edu.usu.graphics.Rectangle;

import java.awt.*;
import java.util.*;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class Game {
    private final Graphics2D graphics;
    private MazeCell[][] maze;
    private final int mazeSize = 3;
    private MazeCell characterLocation;
    private Set<List<Integer>> frontier;
    private Set<List<Integer>> notInMaze;

    public Game(Graphics2D graphics) {
        this.graphics = graphics;
    }

    public void initialize() {
        maze = new MazeCell[mazeSize][mazeSize];
        notInMaze = new HashSet<>();
        frontier = new HashSet<>();

        for (int x = 0; x < mazeSize; x++) {
            for (int y = 0; y < mazeSize; y++) {
                maze[x][y] = new MazeCell(x,y);
                notInMaze.add(List.of(x, y));
            }
        }


//        maze[0][0].setRight(maze[0][1]);
//        maze[0][1].setLeft(maze[0][0]);
//
//        maze[0][0].setBottom(maze[1][0]);
//        maze[1][0].setTop(maze[0][0]);

        Random rand = new Random();
        HashMap<String, List<Integer>> frontier = new HashMap<>();
        frontier.put("Top", List.of(0,0));

        while (!notInMaze.isEmpty()) {
            //Randomly select a cell from the frontier to add to the maze
            Map.Entry<String, List<Integer>> addedCell = getRandomFromHashSet(frontier);

            //Add cell to maze, add its neighbors to the frontier
            notInMaze.remove(addedCell.getValue());
            HashMap<String, List<Integer>> neighbors = getNeighbors(addedCell.getValue());
            for (Map.Entry<String, List<Integer>> entry: neighbors.entrySet()) {
                if (notInMaze.contains(entry.getValue())) {
                    frontier.put(entry.getKey(), entry.getValue());
                }
            }

            //Now select a cell from the frontier
            Map.Entry<String, List<Integer>> frontierCell = getRandomFromHashSet(frontier);

            //Get neighbors of selected frontier cell
            HashMap<String, List<Integer>> frontierNeighbors = getNeighbors(frontierCell.getValue());

            //Get the neighbors of the frontier cell that are in the maze
            HashMap<String, List<Integer>> frontierNeighborsInMaze = new HashMap<>();
            for (Map.Entry<String, List<Integer>> entry: frontierNeighbors.entrySet()) {
                if (!notInMaze.contains(entry.getValue())) {
                    frontierNeighborsInMaze.put(entry.getKey(), entry.getValue());
                }
            }

            //Randomly select one of the frontier neighbors in the maze to connect to
            Map.Entry<String, List<Integer>> randomNeighbor = getRandomFromHashSet(frontierNeighbors);

            //Now connect the frontier cell with that random neighbor
            List<Integer> randomNeighborIdx = randomNeighbor.getValue();
            List<Integer> frontierCellIdx = frontierCell.getValue();

            switch (randomNeighbor.getKey()) {
                case "Top" -> {
                    maze[frontierCellIdx.getFirst()][frontierCellIdx.getLast()]
                            .setTop(maze[randomNeighborIdx.getFirst()][randomNeighborIdx.getLast()]);
                    maze[randomNeighborIdx.getFirst()][randomNeighborIdx.getLast()]
                            .setBottom(maze[frontierCellIdx.getFirst()][frontierCellIdx.getLast()]);
                }
                case "Bottom" -> {
                    maze[frontierCellIdx.getFirst()][frontierCellIdx.getLast()]
                            .setBottom(maze[randomNeighborIdx.getFirst()][randomNeighborIdx.getLast()]);
                    maze[randomNeighborIdx.getFirst()][randomNeighborIdx.getLast()]
                            .setTop(maze[frontierCellIdx.getFirst()][frontierCellIdx.getLast()]);
                }
                case "Left" -> {
                    maze[frontierCellIdx.getFirst()][frontierCellIdx.getLast()]
                            .setLeft(maze[randomNeighborIdx.getFirst()][randomNeighborIdx.getLast()]);
                    maze[randomNeighborIdx.getFirst()][randomNeighborIdx.getLast()]
                            .setRight(maze[frontierCellIdx.getFirst()][frontierCellIdx.getLast()]);
                }
                case "Right" -> {
                    maze[frontierCellIdx.getFirst()][frontierCellIdx.getLast()]
                            .setRight(maze[randomNeighborIdx.getFirst()][randomNeighborIdx.getLast()]);
                    maze[randomNeighborIdx.getFirst()][randomNeighborIdx.getLast()]
                            .setLeft(maze[frontierCellIdx.getFirst()][frontierCellIdx.getLast()]);
                }
            }
            notInMaze.remove(frontierCellIdx);
            notInMaze.remove(randomNeighborIdx);

            cliRender();
            System.out.println("----------------------");
        }


    }

    private static Map.Entry<String, List<Integer>> getRandomFromHashSet(HashMap<String, List<Integer>> set){
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

    private void cliRender(){
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

    private static void replaceAll(StringBuilder builder, String from, String to) {
        int index = builder.indexOf(from);
        while (index != -1) {
            builder.replace(index, index + from.length(), to);
            index += to.length(); // Move to the end of the replacement
            index = builder.indexOf(from, index);
        }
    }

    private void cliRenderCell(MazeCell cell, StringBuilder[][] cliMaze) {
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

    private HashMap<String, List<Integer>> getNeighbors(List<Integer> block) {
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
    public void shutdown() {
    }

    public void run() {
        // Grab the first time
        double previousTime = glfwGetTime();

        while (!graphics.shouldClose()) {
            double currentTime = glfwGetTime();
            double elapsedTime = currentTime - previousTime;    // elapsed time is in seconds
            previousTime = currentTime;

            processInput(elapsedTime);
            update(elapsedTime);
            render(elapsedTime);
        }
    }

    private void processInput(double elapsedTime) {
        // Poll for window events: required in order for window, keyboard, etc events are captured.
        glfwPollEvents();

        // If user presses ESC, then exit the program
        if (glfwGetKey(graphics.getWindow(), GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            glfwSetWindowShouldClose(graphics.getWindow(), true);
        }

        //Move Character
//        characterLocation = characterLocation.getBottom();
    }

    private void update(double elapsedTime) {
    }

    private void render(double elapsedTime) {
        graphics.begin();

        for (var row: maze){
            for (var cell:row){
                renderCell(cell);
            }
        }
        graphics.end();
    }

    private void renderCell(MazeCell cell){
        final float MAZE_LEFT = -0.5f;
        final float MAZE_TOP = -0.5f;
        final float CELL_SIZE = 1 / 3.0f;
        final float CELL_WALL_THICKNESS = CELL_SIZE * 0.01f;

        if (cell.getTop() == null){
            float left = MAZE_LEFT + cell.getColumn() * CELL_SIZE;
            float top = MAZE_TOP + cell.getRow() * CELL_SIZE;
            Rectangle r = new Rectangle(left, top, CELL_SIZE, CELL_WALL_THICKNESS);

            graphics.draw(r, Color.YELLOW);
        }
        if (cell.getBottom() == null){
            float left = MAZE_LEFT + cell.getColumn() * CELL_SIZE;
            float top = MAZE_TOP + (cell.getRow() + 1) * CELL_SIZE;
            Rectangle r = new Rectangle(left, top, CELL_SIZE, CELL_WALL_THICKNESS);

            graphics.draw(r, Color.YELLOW);
        }
        if (cell.getLeft() == null){
            float left = MAZE_LEFT + cell.getColumn() * CELL_SIZE;
            float top = MAZE_TOP + cell.getRow() * CELL_SIZE;
            Rectangle r = new Rectangle(left, top, CELL_WALL_THICKNESS, CELL_SIZE);

            graphics.draw(r, Color.YELLOW);
        }
        if (cell.getRight() == null){
            float left = MAZE_LEFT + (cell.getColumn() + 1) * CELL_SIZE;
            float top = MAZE_TOP + cell.getRow() * CELL_SIZE;
            Rectangle r = new Rectangle(left, top, CELL_WALL_THICKNESS, CELL_SIZE);

            graphics.draw(r, Color.YELLOW);
        }
    }
}
