/**
 * Created by srkambbs on 10/3/2015.
 */
public class Cell
{
    public int x;
    public int y;
    public int F = 0;
    public int G = 0;
    public int H = 0;
    public Cell parent;

    public Cell(int x, int y){
        this.x=x;
        this.y=y;
    }
}
