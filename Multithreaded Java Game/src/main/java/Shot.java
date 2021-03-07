import java.util.List;
//Shot object used to store info about what square the user shot and see if there are any robots
public class Shot
{
    private int x;
    private int y;
    private List<Robot> robots;


    public Shot(int x, int y, List<Robot> robots)
    {
        this.x = x;
        this.y = y;
        this.robots = robots;
    }

    public List<Robot> getRobots() {
        return robots;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
