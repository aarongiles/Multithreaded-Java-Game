//Data object storing data about what has been killed
public class Kill
{
    private int id;
    private int delay;

    public Kill(int id, int delay)
    {
        this.id = id;
        this.delay = delay;
    }

    public int getId() {
        return id;
    }

    public int getDelay() {
        return delay;
    }
}
