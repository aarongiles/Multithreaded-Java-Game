//Data object to keep track of the robot data
//Delay is random and id random, how ever done in other classes
public class Robot
{
    private int id;
    private int delay;
    private double x;
    private double y;

    public Robot(int id, double x, double y)
    {
        this.id = id;
        this.x = x;
        this.y = y;
        //Randomly generates delay between movements which is between 500-2000ms
        delay = (int) (500 + Math.random() * 2000);
    }

    public void moveX(double amount)
    {
        this.x +=amount;
    }

    public void moveY(double amount)
    {
        this.y +=amount;
    }
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getDelay() {
        return delay;
    }

    public int getId() {
        return id;
    }
}
