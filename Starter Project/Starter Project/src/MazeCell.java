public class MazeCell {
    private MazeCell top;
    private MazeCell bottom;
    private MazeCell left;
    private MazeCell right;

    private int column;
    private int row;

    public MazeCell(int row, int column) {
        this.column = column;
        this.row = row;
    }

    public int getColumn() {
        return column;
    }
    public void setColumn(int column) {
        this.column = column;
    }
    public int getRow() {
        return row;
    }
    public void setRow(int row) {
        this.row = row;
    }
    public MazeCell getBottom(){
        return bottom;
    }
    public MazeCell getTop(){
        return top;
    }
    public MazeCell getLeft(){
        return left;
    }
    public MazeCell getRight(){
        return right;
    }

}
