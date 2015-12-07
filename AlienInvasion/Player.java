import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;


public class Player extends Sprite implements Commons{

    private final int START_Y = 280; 
    private final int START_X = 160;

    private final String player = "player.gif";
    private int width;
    private int speed;

    public Player() {

        ImageIcon ii = new ImageIcon(this.getClass().getResource(player));

        width = ii.getImage().getWidth(null); 
		speed = 1;

        setImage(ii.getImage());
        setStart();
    }
    
    public void setStart()
    {
    	setX(START_X);
    	setY(START_Y);
    }

	public void setSpeed(int speed)
	{
		this.speed = speed;
	}
	
	public int getSpeed()
	{
		return speed;
	}

    public void act() {
        x += dx;
        if (x <= 2) 
            x = 2;
        if (x >= BOARD_WIDTH - 2*width) 
            x = BOARD_WIDTH - 2*width;
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT)
        {
            dx = -1 * speed;
        }

        if (key == KeyEvent.VK_RIGHT)
        {
            dx = speed;
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT)
        {
            dx = 0;
        }

        if (key == KeyEvent.VK_RIGHT)
        {
            dx = 0;
        }
    }
}
