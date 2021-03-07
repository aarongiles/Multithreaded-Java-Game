import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import static java.lang.Math.round;


//Contains all the brains of the game, and a lot of variables to keep track of it.
public class GameLogic implements Runnable
{
    private boolean started = true;
    private SpawnTask spawnTask;
    private IncreaseScore increaseScore;
    private ShootTask shootTask;
    private BlockingQueue<Shot> shootingQueue;
    private BlockingQueue<Kill> killQueue;

    private int scoreKills;
    private double fortressX;
    private double fortressY;
    private long startTime;

    public GameLogic()
    {
        //Hard coded for now, possibly change them by user
        fortressX = 3;
        fortressY=3;
        startTime = System.currentTimeMillis();
        shootingQueue = new LinkedBlockingDeque<>(1);
        killQueue = new LinkedBlockingDeque<>(1);

    }

    @Override
    public void run()
    {
        //Start threads
        increaseScore = new IncreaseScore();
        Thread scoreThread = new Thread(increaseScore);
        scoreThread.start();

        spawnTask = new SpawnTask();
        Thread spawnThread = new Thread(spawnTask);
        spawnThread.start();

        shootTask = new ShootTask(shootingQueue,killQueue);
        Thread shootThread = new Thread(shootTask);
        shootThread.start();

        //Sleep a bit while our threads start.
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while(started)
        {
            for (Robot robot: getRobots())
            {
                if (round(robot.getX()) ==fortressX && round(robot.getY())==fortressY)
                {
                    started=false;
                    scoreThread.interrupt();
                    spawnThread.interrupt();
                    shootThread.interrupt();
                    System.out.println("Game over");
                }
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //Calculates the current score.
    public int getScore()
    {
        return   increaseScore.getScore() + scoreKills;
    }
    //Returns all the robots currently spawned
    public List<Robot> getRobots()
    {

        return spawnTask.getRobots();
    }
    //Removes robot at given coordinates
    public void killRobot(int x,int y)
    {
        Shot shot = new Shot(x,y,getRobots());
        try
        {
            shootingQueue.put(shot);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

    }

    public long getTimePassed()
    {
        return System.currentTimeMillis() - startTime;
    }

    public BlockingQueue<Kill> getKillQueue()
    {
        return killQueue;
    }

    public void removeRobot(Kill kill)
    {
        scoreKills += (10 + 100 * (getTimePassed()/kill.getDelay()));
        spawnTask.removeRobot(kill);
    }
}
