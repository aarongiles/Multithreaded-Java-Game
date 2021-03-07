import java.awt.*;
import javax.swing.*;

public class App 
{

    public static void main(String[] args) 
    {
            SwingUtilities.invokeLater(() ->
            {

                JToolBar toolbar = new JToolBar();
                JLabel scoreLabel = new JLabel("Score:0");
                toolbar.add(scoreLabel);
                JFrame window = new JFrame("Defend Castle at all costs");
                SwingArena arena = new SwingArena();
                JTextArea logger = new JTextArea();
                JScrollPane loggerArea = new JScrollPane(logger);
                loggerArea.setBorder(BorderFactory.createEtchedBorder());


                arena.addListener((x, y) ->
                {
                    System.out.println("Arena click at (" + x + "," + y + ")");
                    arena.killRobot(x,y);

                });

                Thread killed = new Thread(){
                    public void run()
                    {
                        System.out.println("Kill thread started");
                        while (true)
                        {
                            try {
                                Kill kill = arena.getKillQueue().take();
                                logger.append("Robot: "+kill.getId()+" died\n");
                                arena.removeRobot(kill);
                            }
                            catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                        }

                    }
                };

                Thread refreshGUI = new Thread(){
                    public void run()
                    {
                        while (true)
                        {
                            arena.repaint();
                            scoreLabel.setText("Score:" +arena.getScore());
                            try
                            {
                                Thread.sleep(20);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                };

                refreshGUI.start();
                killed.start();


                JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, arena, logger);

                Container contentPane = window.getContentPane();
                contentPane.setLayout(new BorderLayout());
                contentPane.add(toolbar, BorderLayout.NORTH);
                contentPane.add(splitPane, BorderLayout.CENTER);

                window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                window.setPreferredSize(new Dimension(800, 800));
                window.pack();
                window.setVisible(true);

                splitPane.setDividerLocation(0.75);
            });

    }
}
