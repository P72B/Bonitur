package de.p72b.bonitur;

public class Pointer {
    String name = "0";
    String property = "0";
    private DOM dom;
    // row col
    int max[] = {1, 1};


    /**
     * Creates a new pointer object.
     * @param row Maximum amount of rows
     * @param col Maximum amount of cols
     * @param direction Move the pointer in this direction (0 -> lrtb and 1 -> tblr)
     * @param interval Time between two voice inputs in sec
     */
    public Pointer(DOM fileDom) {
        this.dom = fileDom;
        setMaxRowCol(this.dom.getRowsLength() - 1, this.dom.getColsLength() - 1);
    };


    /**
     * Set the pointer position.
     * @param row
     * @param col
     */
    public void setPosition(int row, int col) {
        this.dom.setSelected(true);
        this.dom.setPositionX(col);
        this.dom.setPositionY(row);
    };


    /**
     * Returns the pointer position.
     */
    public int[] getPosition() {
        int[] pos = {this.dom.getPositionY(), this.dom.getPositionX()};
        return pos;
    };


    /**
     * Returns true if the user already specified a position in the table to start speech input.
     * @return
     */
    public boolean isStartPositionSpecified() {
        return this.dom.isSelected();
    };


    public void setStartPosition(boolean b) {
        this.dom.setSelected(b);
    };


    /**
     * Set the max amount of rows and columns.
     * @param row
     * @param col
     */
    public void setMaxRowCol(int row, int col) {
        max[0] = row;
        max[1] = col;
    };

    /**
     * Returns the max amount of rows and columns.
     * @param row
     * @param col
     */
    public int[] getMaxRowCol() {
        return max;
    }


    public void setDirection(int value) {
        this.dom.setDirection(value);
    };


    public void setInterval(double d) {
        this.dom.setInterval(d);
    };


    public int getDirection() {
        return this.dom.getDirection();
    };


    public double getInterval() {
        return this.dom.getInterval();
    };
}
