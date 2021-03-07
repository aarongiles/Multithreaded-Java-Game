//Will move the robot and create one upon creating
//Slowly animates at 50fps the robot to move
//Does not take into account other robots on the board
public class MoveTask implements Runnable
{
    private Robot robot;
    private boolean running = true;

    public MoveTask(int id, double x, double y)
    {
        robot = new Robot(id,x,y);
    }

    @Override
    public void run()
    {
        while (running)
        {
            try
            {
                //Determine random movement to walk in one of the four surrounding squares
                int movement = (int) (1+ (Math.random()*4));
                for (int i=0; i<20;i++)
                {
                    switch (movement)
                    {
                        case 1:
                            if (robot.getY() >=1)
                            {
                                robot.moveY(-0.05);
                            }
                            break;

                        case 2:
                            if (robot.getX() <=8)
                            {
                                robot.moveX(0.05);
                            }
                            break;

                        case 3:
                            if (robot.getY() <=8)
                            {
                                robot.moveY(0.05);
                            }
                            break;

                        case 4:
                            if (robot.getX() >=1)
                            {
                                robot.moveX(-0.05);
                            }
                            break;
                    }
                    //slowly move robot
                    Thread.sleep(20);
                }
                Thread.sleep(robot.getDelay());
            }
            catch (InterruptedException e)
            {
                System.out.println("Robot stopped at: "+robot.getX()+","+robot.getY());
                running =false;
            }
        }

    }

    public void stop()
    {
        running = false;
    }

    public Robot getRobot()
    {
        return robot;
    }
}
