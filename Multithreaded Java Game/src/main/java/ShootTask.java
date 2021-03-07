import java.util.concurrent.BlockingQueue;

import static java.lang.Math.round;

public class ShootTask implements Runnable
{

    private BlockingQueue<Shot> shootingQueue;
    private BlockingQueue<Kill> killQueue;
    private boolean started = true;

    public ShootTask(BlockingQueue<Shot> shootTasks,BlockingQueue<Kill> killTasks)
    {
        shootingQueue = shootTasks;
        killQueue = killTasks;
    }

    //Will consitently check if a shot has been fired and then process it
    @Override
    public void run()
    {
        while (started)
        {

            try {
                Shot shot = shootingQueue.take();

                for (Robot robot:shot.getRobots())
                {

                    if (shot.getX() == round(robot.getX()) && shot.getY()==round(robot.getY()))
                    {
                        Kill kill = new Kill(robot.getId(),robot.getDelay());
                        killQueue.put(kill);
                        break;
                    }

                }
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                started =false;
            }

        }
    }
}
