import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Math.round;

public class SpawnTask implements Runnable
{

    //Constants so it makes it easier to spawn in corner only
    private static final double[][] CORNERS ={{0,0},{0,8},{8,0},{8,8}};
    private static final int MAX_THREADS = 10;



    private int count =1;
    private boolean running = true;
    private List<MoveTask> robots;
    private ExecutorService pool;
    private Object mutex;

    @Override
    public void run()
    {
        //check each corner to see if anything there, if there isnt add a robot

        mutex = new Object();

        robots = new ArrayList<>();
        pool = Executors.newFixedThreadPool(MAX_THREADS);
        System.out.println("Started spawning thread");

        MoveTask intialThread = new MoveTask((int)(Math.random()*1000000),CORNERS[0][0],CORNERS[0][1]);
        pool.execute(intialThread);
        robots.add(intialThread);
        count++;


        while (running)
        {
            try
            {
                //Basically, iterate through every robot, check if they not in the corner, if they are spawn and exit
                for (Robot robot: getRobots())
                {
                    int spawnPoint = (int) (Math.random()*4);

                    if (!(CORNERS[spawnPoint][0] == round(robot.getX()) && CORNERS[spawnPoint][1] == round(robot.getY())))
                    {

                        synchronized (mutex)
                        {

                            if (count <= MAX_THREADS) {

                                MoveTask robotThread = new MoveTask((int)(Math.random()*1000000), CORNERS[spawnPoint][0], CORNERS[spawnPoint][1]);
                                pool.execute(robotThread);
                                robots.add(robotThread);
                                count++;
                            }
                            break;
                        }
                    }
                }
                //if the list is empty and doesnt go through the loop
                if (getRobots().isEmpty())
                {
                    MoveTask robotThread = new MoveTask((int)(Math.random()*1000000), CORNERS[0][0], CORNERS[0][1]);
                    pool.execute(robotThread);
                    robots.add(robotThread);
                    count++;
                }
                Thread.sleep(2000);
            }
            catch (InterruptedException e)
            {
                running = false;
                pool.shutdownNow();
                System.out.println("Stopping Spawning");
            }
        }
    }


    //Basically a wrapper that gets all the robots in the game at the moment
    public List<Robot> getRobots()
    {
        List<Robot> robotList = new ArrayList<>();
        for (MoveTask robot:robots)
        {
            robotList.add(robot.getRobot());
        }

        return robotList;
    }


    //Removes robot from the list and in turn ends the thread.
    public void removeRobot(Kill kill)
    {
        synchronized (mutex) {
            for (MoveTask robot : robots)
            {
                Robot temp = robot.getRobot();
                if (kill.getId() == temp.getId())
                {
                    robot.stop();
                    robots.remove(robot);
                    count--;
                    break;
                }

            }
        }
    }
}
