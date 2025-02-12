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


        maze[0][0].setRight(maze[0][1]);
        maze[0][1].setLeft(maze[0][0]);

        maze[0][0].setBottom(maze[1][0]);
        maze[1][0].setTop(maze[0][0]);

//        List<Integer> currentBlockIdx = List.of(0,0);
//
//        Random rand = new Random();
//        HashMap<String, List<Integer>> frontier = new HashMap<>();
//
//        while (!notInMaze.isEmpty()) {
//            notInMaze.remove(currentBlockIdx);
//            HashMap<String, List<Integer>> neighbors = getNeighbors(currentBlockIdx);
//            for (Map.Entry<String, List<Integer>> entry: neighbors.entrySet()) {
//                if (notInMaze.contains(entry.getValue())) {
//                    frontier.put(entry.getKey(), entry.getValue());
//                }
//            }
//
//            int randNum = rand.nextInt(frontier.size());
//            int idx = 0;
//            Map.Entry<String, List<Integer>> nextBlock = null;
//
//            for (Map.Entry<String, List<Integer>> entry: frontier.entrySet()) {
//                if (randNum == idx){
//                    nextBlock = entry;
//                    break;
//                }
//                idx++;
//            }
//
//            List<Integer> nextBlockIdx = nextBlock.getValue();
//
//            switch (nextBlock.getKey()) {
//                case "Top" -> {
//                    maze[currentBlockIdx.getFirst()][currentBlockIdx.getLast()]
//                            .setTop(maze[nextBlockIdx.getFirst()][nextBlockIdx.getLast()]);
//                    maze[nextBlockIdx.getFirst()][nextBlockIdx.getLast()]
//                            .setBottom(maze[currentBlockIdx.getFirst()][currentBlockIdx.getLast()]);
//                }
//                case "Bottom" -> {
//                    maze[currentBlockIdx.getFirst()][currentBlockIdx.getLast()]
//                            .setBottom(maze[nextBlockIdx.getFirst()][nextBlockIdx.getLast()]);
//                    maze[nextBlockIdx.getFirst()][nextBlockIdx.getLast()]
//                            .setTop(maze[currentBlockIdx.getFirst()][currentBlockIdx.getLast()]);
//                }
//                case "Left" -> {
//                    maze[currentBlockIdx.getFirst()][currentBlockIdx.getLast()]
//                            .setLeft(maze[nextBlockIdx.getFirst()][nextBlockIdx.getLast()]);
//                    maze[nextBlockIdx.getFirst()][nextBlockIdx.getLast()]
//                            .setRight(maze[currentBlockIdx.getFirst()][currentBlockIdx.getLast()]);
//                }
//                case "Right" -> {
//                    maze[currentBlockIdx.getFirst()][currentBlockIdx.getLast()]
//                            .setRight(maze[nextBlockIdx.getFirst()][nextBlockIdx.getLast()]);
//                    maze[nextBlockIdx.getFirst()][nextBlockIdx.getLast()]
//                            .setLeft(maze[currentBlockIdx.getFirst()][currentBlockIdx.getLast()]);
//                }
//            }
//            frontier.remove(nextBlock.getKey());
//            currentBlockIdx = nextBlockIdx;
//
//            render(0);
//        }
        cliRender();

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

//        replaceAll(cliMazeText, "____", "______");
        System.out.println(cliMazeText);
        System.out.println("Success!");
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
        if ((block.getFirst() - 1) >= 0){
            neighbors.put("Left", List.of(block.getFirst() - 1, block.get(1)));
        }
        if ((block.getFirst() + 1) < mazeSize){
            neighbors.put("Right", List.of(block.getFirst() + 1, block.get(1)));
        }
        if ((block.getLast() - 1) >= 0){
            neighbors.put("Bottom", List.of(block.getLast() - 1, block.get(1)));
        }
        if ((block.getLast() + 1) < mazeSize){
            neighbors.put("Top", List.of(block.getLast() + 1, block.get(1)));
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
