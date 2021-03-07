import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.net.URL ;
import java.util.concurrent.BlockingQueue;

/**
 * A Swing GUI element that displays a grid on which you can draw images, text and lines.
 */
public class SwingArena extends JPanel
{

    private GameLogic game;


    // Represents the image to draw. You can modify this to introduce multiple images.
    private static final String IMAGE_FILE = "1554047213.png";
    private ImageIcon robot1;
    private ImageIcon fortress;
    private static final String FORTRESS_FILE = "rg1024-isometric-tower.png";

    // The following values are arbitrary, and you may need to modify them according to the 
    // requirements of your application.
    private int gridWidth = 9;
    private int gridHeight = 9;

    
    private double gridSquareSize; // Auto-calculated
    
    private LinkedList<ArenaListener> listeners = null;

    /**
     * Creates a new arena object, loading the robot image.
     */
    public SwingArena()
    {

        game = new GameLogic();
        Thread gameThread = new Thread(game);
        gameThread.start();

        System.out.println("Game Thread started");
        // Here's how you get an Image object from an image file (which you provide in the 
        // 'resources/' directory.
        URL url = getClass().getClassLoader().getResource(IMAGE_FILE);
        if(url == null)
        {
            throw new AssertionError("Cannot find image file " + IMAGE_FILE);
        }
        robot1 = new ImageIcon(url);

        URL url1 = getClass().getClassLoader().getResource(FORTRESS_FILE);
        if(url1 == null)
        {
            throw new AssertionError("Cannot find image file " + IMAGE_FILE);
        }
        fortress = new ImageIcon(url1);

    }
    
    

    
    
    /**
     * Adds a callback for when the user clicks on a grid square within the arena. The callback 
     * (of type ArenaListener) receives the grid (x,y) coordinates as parameters to the 
     * 'squareClicked()' method.
     */
    public void addListener(ArenaListener newListener)
    {
        if(listeners == null)
        {
            listeners = new LinkedList<>();
            addMouseListener(new MouseAdapter()
            {
                @Override
                public void mouseClicked(MouseEvent event)
                {
                    int gridX = (int)((double)event.getX() / gridSquareSize);
                    int gridY = (int)((double)event.getY() / gridSquareSize);
                    
                    if(gridX < gridWidth && gridY < gridHeight)
                    {
                        for(ArenaListener listener : listeners)
                        {   
                            listener.squareClicked(gridX, gridY);
                        }
                    }
                }
            });
        }
        listeners.add(newListener);
    }
    
    
    
    /**
     * This method is called in order to redraw the screen, either because the user is manipulating 
     * the window, OR because you've called 'repaint()'.
     *
     * You will need to modify the last part of this method; specifically the sequence of calls to
     * the other 'draw...()' methods. You shouldn't need to modify anything else about it.
     */
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D gfx = (Graphics2D) g;
        gfx.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                             RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        
        // First, calculate how big each grid cell should be, in pixels. (We do need to do this
        // every time we repaint the arena, because the size can change.)
        gridSquareSize = Math.min(
            (double) getWidth() / (double) gridWidth,
            (double) getHeight() / (double) gridHeight);
            
        int arenaPixelWidth = (int) ((double) gridWidth * gridSquareSize);
        int arenaPixelHeight = (int) ((double) gridHeight * gridSquareSize);
            
            
        // Draw the arena grid lines. This may help for debugging purposes, and just generally
        // to see what's going on.
        gfx.setColor(Color.GRAY);
        gfx.drawRect(0, 0, arenaPixelWidth - 1, arenaPixelHeight - 1); // Outer edge

        for(int gridX = 1; gridX < gridWidth; gridX++) // Internal vertical grid lines
        {
            int x = (int) ((double) gridX * gridSquareSize);
            gfx.drawLine(x, 0, x, arenaPixelHeight);
        }
        
        for(int gridY = 1; gridY < gridHeight; gridY++) // Internal horizontal grid lines
        {
            int y = (int) ((double) gridY * gridSquareSize);
            gfx.drawLine(0, y, arenaPixelWidth, y);
        }


        //Draw the fortress
        drawImage(gfx, fortress, 3, 3);

        // Draws each of the robots
        for (Robot robot: game.getRobots())
        {

            drawImage(gfx, robot1, robot.getX(), robot.getY());
            drawLabel(gfx, "Robot:"+robot.getId(), robot.getX(), robot.getY());
        }


    }
    
    
    /** 
     * Draw an image in a specific grid location. *Only* call this from within paintComponent(). 
     *
     * Note that the grid location can be fractional, so that (for instance), you can draw an image 
     * at location (3.5,4), and it will appear on the boundary between grid cells (3,4) and (4,4).
     *     
     * You shouldn't need to modify this method.
     */
    private void drawImage(Graphics2D gfx, ImageIcon icon, double gridX, double gridY)
    {
        // Get the pixel coordinates representing the centre of where the image is to be drawn. 
        double x = (gridX + 0.5) * gridSquareSize;
        double y = (gridY + 0.5) * gridSquareSize;
        
        // We also need to know how "big" to make the image. The image file has a natural width 
        // and height, but that's not necessarily the size we want to draw it on the screen. We 
        // do, however, want to preserve its aspect ratio.
        double fullSizePixelWidth = (double) robot1.getIconWidth();
        double fullSizePixelHeight = (double) robot1.getIconHeight();
        
        double displayedPixelWidth, displayedPixelHeight;
        if(fullSizePixelWidth > fullSizePixelHeight)
        {
            // Here, the image is wider than it is high, so we'll display it such that it's as 
            // wide as a full grid cell, and the height will be set to preserve the aspect 
            // ratio.
            displayedPixelWidth = gridSquareSize;
            displayedPixelHeight = gridSquareSize * fullSizePixelHeight / fullSizePixelWidth;
        }
        else
        {
            // Otherwise, it's the other way around -- full height, and width is set to 
            // preserve the aspect ratio.
            displayedPixelHeight = gridSquareSize;
            displayedPixelWidth = gridSquareSize * fullSizePixelWidth / fullSizePixelHeight;
        }

        // Actually put the image on the screen.
        gfx.drawImage(icon.getImage(), 
            (int) (x - displayedPixelWidth / 2.0),  // Top-left pixel coordinates.
            (int) (y - displayedPixelHeight / 2.0), 
            (int) displayedPixelWidth,              // Size of displayed image.
            (int) displayedPixelHeight, 
            null);
    }
    
    
    /**
     * Displays a string of text underneath a specific grid location. *Only* call this from within 
     * paintComponent(). 
     *
     * You shouldn't need to modify this method.
     */
    private void drawLabel(Graphics2D gfx, String label, double gridX, double gridY)
    {
        gfx.setColor(Color.BLUE);
        FontMetrics fm = gfx.getFontMetrics();
        gfx.drawString(label, 
            (int) ((gridX + 0.5) * gridSquareSize - (double) fm.stringWidth(label) / 2.0), 
            (int) ((gridY + 1.0) * gridSquareSize) + fm.getHeight());
    }
    
    /** 
     * Draws a (slightly clipped) line between two grid coordinates. 
     *
     * You shouldn't need to modify this method.
     */
    private void drawLine(Graphics2D gfx, double gridX1, double gridY1, 
                                          double gridX2, double gridY2)
    {
        gfx.setColor(Color.RED);
        
        // Recalculate the starting coordinate to be one unit closer to the destination, so that it
        // doesn't overlap with any image appearing in the starting grid cell.
        final double radius = 0.5;
        double angle = Math.atan2(gridY2 - gridY1, gridX2 - gridX1);
        double clippedGridX1 = gridX1 + Math.cos(angle) * radius;
        double clippedGridY1 = gridY1 + Math.sin(angle) * radius;
        
        gfx.drawLine((int) ((clippedGridX1 + 0.5) * gridSquareSize), 
                     (int) ((clippedGridY1 + 0.5) * gridSquareSize), 
                     (int) ((gridX2 + 0.5) * gridSquareSize), 
                     (int) ((gridY2 + 0.5) * gridSquareSize));
    }


    public void removeRobot(Kill kill)
    {
        game.removeRobot(kill);
    }

    public int getScore()
    {
        return game.getScore();
    }

    public void killRobot(int x, int y)
    {
         game.killRobot(x,y);
    }

    public BlockingQueue<Kill> getKillQueue()
    {
        return game.getKillQueue();
    }
}
