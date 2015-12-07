import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.io.*;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import sun.audio.*;

public class Board extends JPanel implements Runnable, Commons { 

    private Dimension d;
    private ArrayList aliens;
    private Player player;
    private Shot shot;

    private int alienX = 150;
    private int alienY = 5;
    private int direction;
    private int deaths;
    
    private int coins = 0;
    private int points = 0;
    private int level = 1;
    
    private int missileSpeed = 4;
    private int playerSpeed = 1;
    private int attackDamage = 1;
    
    private int bombChance;
    private int alienHP;
    private int alienSpeed;
    private int bombSpeed;
    
    private int[] hsPoints;
    private String[] hsNames;
    private String filename = "highscores.txt";
    private String playerName = "Player";
    private JTextField textField;

	private boolean mainmenu;
	private boolean paused = false;
	private boolean highscore = false;
	private boolean help = false;
	private boolean cds = false;
	private boolean ready = false;
    private boolean ingame;
    private boolean levelPassed;
    private final String expl = "explosion.jpg";
    private final String alienpix = "alien.gif";
    private String message = "Game Over";
	private Color backgroundColor = Color.BLACK;
    
    private Thread animator;
    private LevelManager LM;
    private InterLevelMenu ILM;
    private int counter = 0;
    
    String soundFile = "laser.au";
    InputStream in;
    AudioStream audioStream;
 
    public Board() throws IOException
    {
		ILM = new InterLevelMenu(this);
		LM = new LevelManager();
        addKeyListener(new InputManager());
        addMouseListener(new MouseManager());
        setFocusable(true);
        d = new Dimension(BOARD_WIDTH, BOARD_HEIGTH);
        setBackground(Color.black);
		LM.newLevel(level,this);
		player = new Player();
        setDoubleBuffered(true); 
        mainmenu=true;
        hsPoints = new int[10];
        hsNames = new String[10];
        readHighscores();
       
    }
    
    public void readHighscores() throws IOException
    {
    	FileReader fileReader = new FileReader(filename);
    	BufferedReader bufferedReader = new BufferedReader(fileReader);
    	
    	for(int i=0; i<20; i++)
    	{
    		String line = bufferedReader.readLine();
    		
    		if(i%2==0)
    			hsPoints[i/2] = Integer.parseInt(line);
    		else
    			hsNames[i/2] = line;
    	}    	
    }
    
    public int checkHighscore()
    {
    	for(int i=0; i<10; i++)
    	{
    		if(points >= hsPoints[i])
    			return i;
    	}
    	return -1;
    }
    
    public void writeHighscore() throws IOException
    {
    	FileWriter fileWriter = new FileWriter(filename);
    	BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);    	
        
		for(int i=0; i<20; i++)
		{
			if (i%2==0)
				bufferedWriter.write(hsPoints[i/2]+"");
			else
				bufferedWriter.write(hsNames[i/2]);
			bufferedWriter.newLine();
		}
        
        bufferedWriter.close();
    }

	public void setStats(int bombChance, int alienSpeed, int bombSpeed, int alienHP)
	{
		this.bombChance = bombChance;
		this.alienSpeed = alienSpeed;
		this.bombSpeed = bombSpeed;
		this.alienHP = alienHP;
	}

    public void addNotify() {
        super.addNotify();
    }

    public void gameInit() {
		deaths = 0;
		direction = -1;
		ingame = true;
		levelPassed = false;
		highscore = false;
		mainmenu = false;

        aliens = new ArrayList();

        ImageIcon ii = new ImageIcon(this.getClass().getResource(alienpix));

        for (int i=0; i < 4; i++) {
            for (int j=0; j < 6; j++) {
                Alien alien = new Alien(alienX + 18*j, alienY + 18*i, alienHP);
                alien.setImage(ii.getImage());
                aliens.add(alien);
            }
        }

        player.setStart();
        player.setSpeed(playerSpeed);
        shot = new Shot();

        if (animator == null || !ingame) {
            animator = new Thread(this);
            animator.start();
        }
    }

    public void drawAliens(Graphics g) 
    {
        Iterator it = aliens.iterator();

        while (it.hasNext()) {
            Alien alien = (Alien) it.next();

            if (alien.isVisible()) {
                g.drawImage(alien.getImage(), alien.getX(), alien.getY(), this);
            }

            if (alien.isDying()) {
                alien.die();
            }
        }
    }

    public void drawPlayer(Graphics g) {

        if (player.isVisible()) {
            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
        }

        if (player.isDying()) {
            player.die();
            ingame = false;
        }
    }

    public void drawShot(Graphics g) {
        if (shot.isVisible())
            g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
    }

    public void drawBombing(Graphics g) {

        Iterator i3 = aliens.iterator();

        while (i3.hasNext()) {
            Alien a = (Alien) i3.next();

            Alien.Bomb b = a.getBomb();

            if (!b.isDestroyed()) {
                g.drawImage(b.getImage(), b.getX(), b.getY(), this); 
            }
        }
    }

    public void paint(Graphics g)
    {
      super.paint(g);
	
      g.setColor(backgroundColor);
      g.fillRect(0, 0, d.width, d.height);
      g.setColor(Color.green);   
		
		if(levelPassed)
		{
			g.setFont(new Font("Helvetica", Font.BOLD, 14));
			g.setColor(Color.GREEN);
			g.drawString("Coins: " + coins, 150,BOARD_WIDTH/2-100);
			g.setColor(new Color(0, 32, 48));
        	g.fillRect(30, BOARD_WIDTH/2 - 50, BOARD_WIDTH-80, 40);
        	g.fillRect(30, BOARD_WIDTH/2, BOARD_WIDTH-80, 40);
        	g.fillRect(30, BOARD_WIDTH/2 + 50, BOARD_WIDTH-80, 40);
        	g.setColor(Color.WHITE);
        	g.drawString("ATTACK DAMAGE:" + attackDamage + "----->" + (attackDamage+1) + "                  150",31,BOARD_WIDTH/2 - 25);
        	g.drawString("ATTACK SPEED: " + missileSpeed + "----->" + (missileSpeed+1) + "                     125",31,BOARD_WIDTH/2 + 25);
        	g.drawString("SPEED:" + playerSpeed + "----->" + (playerSpeed+1) + "                                    100",31,BOARD_WIDTH/2 +75);
        	g.setColor(new Color(0, 32, 48));
        	g.fillRect(150, BOARD_WIDTH/2 + 65 + 50, 50, 30);
        	g.setColor(Color.WHITE);
        	g.drawString("OK",165,BOARD_WIDTH/2+135);
		}
		else if(mainmenu)
		{
			g.setFont(new Font("Helvetica", Font.BOLD, 24));
			g.setColor(Color.GREEN);
			g.drawString("ALIEN INVASION",75,BOARD_WIDTH/2-120);
			g.setColor(new Color(0, 32, 48));
			g.fillRect(30, BOARD_WIDTH/2 - 100, BOARD_WIDTH-80, 40);
        	g.fillRect(30, BOARD_WIDTH/2 - 50, BOARD_WIDTH-80, 40);
        	g.fillRect(30, BOARD_WIDTH/2, BOARD_WIDTH-80, 40);
        	g.fillRect(30, BOARD_WIDTH/2 + 50, BOARD_WIDTH-80, 40);
        	g.fillRect(30, BOARD_WIDTH/2 + 100, BOARD_WIDTH-80, 40);
        	g.setColor(Color.WHITE);
        	g.drawString("PLAY",75,BOARD_WIDTH/2-70);
        	g.drawString("HIGHSCORES",75,BOARD_WIDTH/2-20);
        	g.drawString("HELP",75,BOARD_WIDTH/2+30);
        	g.drawString("CREDITS",75,BOARD_WIDTH/2+80);
        	g.drawString("QUIT",75,BOARD_WIDTH/2+130);
		}
		else if(highscore)
		{
			g.setFont(new Font("Helvetica", Font.BOLD, 12));
			g.setColor(Color.GREEN);
			g.drawString("1: " + hsNames[0] + "   " + hsPoints[0],20,BOARD_WIDTH/2-150);
			g.drawString("2: " + hsNames[1] + "   " + hsPoints[1],20,BOARD_WIDTH/2-120);
			g.drawString("3: " + hsNames[2] + "   " + hsPoints[2],20,BOARD_WIDTH/2-90);
			g.drawString("4: " + hsNames[3] + "   " + hsPoints[3],20,BOARD_WIDTH/2-60);
			g.drawString("5: " + hsNames[4] + "   " + hsPoints[4],20,BOARD_WIDTH/2-30);
			g.drawString("6: " + hsNames[5] + "   " + hsPoints[5],20,BOARD_WIDTH/2);
			g.drawString("7: " + hsNames[6] + "   " + hsPoints[6],20,BOARD_WIDTH/2+30);
			g.drawString("8: " + hsNames[7] + "   " + hsPoints[7],20,BOARD_WIDTH/2+60);
			g.drawString("9: " + hsNames[8] + "   " + hsPoints[8],20,BOARD_WIDTH/2+90);
			g.drawString("10: " + hsNames[9] + "   " + hsPoints[9],20,BOARD_WIDTH/2+120);
			g.setColor(new Color(0, 32, 48));
        	g.fillRect(200, BOARD_WIDTH/2-20, 50, 30);
        	g.setColor(Color.WHITE);
        	g.drawString("OK",210,BOARD_WIDTH/2);
		}
		else if(help)
		{
			g.setFont(new Font("Helvetica", Font.BOLD, 18));
			g.setColor(Color.GREEN);
			g.drawString("Press SPACE to shoot",50,BOARD_WIDTH/2-100);
			g.drawString("Press ARROW KEYS to move",50,BOARD_WIDTH/2-50);
			g.drawString("Press ESC to pause",50,BOARD_WIDTH/2);
			g.setColor(new Color(0, 32, 48));
        	g.fillRect(30, BOARD_WIDTH/2 + 50, BOARD_WIDTH-80, 40);
        	g.setColor(Color.WHITE);
        	g.drawString("MAIN MENU",100,BOARD_WIDTH/2 +75);
		}
		else if(cds)
		{
			g.setFont(new Font("Helvetica", Font.BOLD, 18));
			g.setColor(Color.GREEN);
			g.drawString("BERKCAN GUREL",50,BOARD_WIDTH/2-150);
			g.drawString("CEMAL KILINC",50,BOARD_WIDTH/2-100);
			g.drawString("GUNCE KALYONCU",50,BOARD_WIDTH/2-50);
			g.drawString("LEVENT KOKSAL",50,BOARD_WIDTH/2);
			g.setColor(new Color(0, 32, 48));
        	g.fillRect(30, BOARD_WIDTH/2 + 50, BOARD_WIDTH-80, 40);
        	g.setColor(Color.WHITE);
        	g.drawString("MAIN MENU",100,BOARD_WIDTH/2 +75);
		}
		else if(paused)
		{
			g.setFont(new Font("Helvetica", Font.BOLD,24));
			g.setColor(Color.GREEN);
			g.drawString("PAUSE", 130,BOARD_WIDTH/2-100);
			g.setColor(new Color(0, 32, 48));
			g.fillRect(30, BOARD_WIDTH/2 - 50, BOARD_WIDTH-80, 40);
        	g.fillRect(30, BOARD_WIDTH/2, BOARD_WIDTH-80, 40);
        	g.setColor(Color.WHITE);
        	g.drawString("RESUME",75,BOARD_WIDTH/2-20);
        	g.drawString("MAIN MENU",75,BOARD_WIDTH/2+30);
		}
		else if (ingame) {
	
        g.drawLine(0, GROUND, BOARD_WIDTH, GROUND);
        drawAliens(g);
        drawPlayer(g);
        drawShot(g);
        drawBombing(g);
        Font small = new Font("Helvetica", Font.BOLD, 14);
        g.setFont(small);
        g.drawString("Coins: " + coins , 35 , GROUND + 30);
        g.drawString("LEVEL " + level , 150 , GROUND + 30);
        g.drawString("Points: " + points , 250 , GROUND + 30);
      	
      }
      Toolkit.getDefaultToolkit().sync();
      g.dispose();
    }

    public void gameOver() throws IOException
    {
        Graphics g = this.getGraphics();

        g.setColor(Color.black);
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGTH);
        
        int x = checkHighscore();
        
        if(x>-1)
        {
        	playerName = JOptionPane.showInputDialog(null, points+" points!\nEnter your name");
        	
        	for(int i=9; i>x; i--)
        	{
        		hsNames[i] = hsNames[i-1];
        		hsPoints[i] = hsPoints[i-1];
        	}
        	
        	hsNames[x] = playerName;
        	hsPoints[x] = points;

        	writeHighscore();
        }        
		
		player = new Player();
		coins = 0;
    	points = 0;
    	level = 1;
    	
    	alienHP = 1;
    	alienSpeed = 1;
    	bombChance = 1000;
    	bombSpeed = 1;
    
   	 	missileSpeed = 4;
   		playerSpeed = 1;
    	attackDamage = 1;
		
        animator = null;
        highscore = true;
        ingame = false;
        repaint();
    }
    
    public void interLevelMenu()
    {
    	coins-=50;
    	ready = false;
    	
    	while(!ready)
    		repaint();
    		
    	coins+=50;	
    	nextLevel();
    }

	public void nextLevel()
	{
		level++;
		LM.newLevel(level,this);
		gameInit();
		setDoubleBuffered(true);
	}

    public void animationCycle()  {

		if(paused)
			return;
		
        if (deaths == NUMBER_OF_ALIENS_TO_DESTROY) {
            levelPassed = true;
            ingame = false;
            points += 150;
            coins += 50;
        }
		
		counter++;
		if(counter == 100)
		{
			points++;
			counter = 0;
		}
		
        // player

        player.act();

        // shot
        if (shot.isVisible()) {
            Iterator it = aliens.iterator();
            int shotX = shot.getX();
            int shotY = shot.getY();

            while (it.hasNext()) {
                Alien alien = (Alien) it.next();
                int alienX = alien.getX();
                int alienY = alien.getY();

                if (alien.isVisible() && shot.isVisible()) 
                {
                    if (shotX >= (alienX) && 
                        shotX <= (alienX + ALIEN_WIDTH) &&
                        shotY >= (alienY) &&
                        shotY <= (alienY+ALIEN_HEIGHT) ) 
                    {
                    	alien.damage(attackDamage);
                    	if(alien.getCurrentHP()<=0)
                    	{
                    		ImageIcon ii = new ImageIcon(getClass().getResource(expl));
                        	alien.setImage(ii.getImage());
                        	alien.setDying(true);
                        	deaths++;
                        	coins += 10;
                        	points += 20;
                    	}
                        shot.die();
                     }
                }
            }

            int y = shot.getY();
            y -= missileSpeed;
            if (y < 0)
            {
            	shot.die();
            	if(coins - 50 < 0)
            		coins = 0;
            	else
            		coins -= 50;
            }   
            else 
            	shot.setY(y);
        }

        // aliens

         Iterator it1 = aliens.iterator();

         while (it1.hasNext()) {
             Alien a1 = (Alien) it1.next();
             int x = a1.getX();

             if (x  >= BOARD_WIDTH - BORDER_RIGHT && direction != -1) {
                 direction = -1;
                 Iterator i1 = aliens.iterator();
                 while (i1.hasNext()) {
                     Alien a2 = (Alien) i1.next();
                     a2.setY(a2.getY() + GO_DOWN);
                 }
             }

            if (x <= BORDER_LEFT && direction != 1) {
                direction = 1;

                Iterator i2 = aliens.iterator();
                while (i2.hasNext()) {
                    Alien a = (Alien)i2.next();
                    a.setY(a.getY() + GO_DOWN);
                }
            }
        }


        Iterator it = aliens.iterator();

        while (it.hasNext()) {
            Alien alien = (Alien) it.next();
            if (alien.isVisible()) {

                int y = alien.getY();

                if (y > GROUND - ALIEN_HEIGHT) {
                    ingame = false;
                    message = "Invasion!";
                }

                alien.act(direction*alienSpeed);
            }
        }

        // bombs

        Iterator i3 = aliens.iterator();
        Random generator = new Random();

        while (i3.hasNext()) {
            int shot = generator.nextInt(bombChance);
            Alien a = (Alien) i3.next();
            Alien.Bomb b = a.getBomb();
            if (shot == CHANCE && a.isVisible() && b.isDestroyed()) {

                b.setDestroyed(false);
                b.setX(a.getX());
                b.setY(a.getY());   
            }

            int bombX = b.getX();
            int bombY = b.getY();
            int playerX = player.getX();
            int playerY = player.getY();

            if (player.isVisible() && !b.isDestroyed()) {
                if ( bombX >= (playerX) && 
                    bombX <= (playerX+PLAYER_WIDTH) &&
                    bombY >= (playerY) && 
                    bombY <= (playerY+PLAYER_HEIGHT) ) {
                        ImageIcon ii = 
                            new ImageIcon(this.getClass().getResource(expl));
                        player.setImage(ii.getImage());
                        player.setDying(true);
                        b.setDestroyed(true);;
                    }
            }

            if (!b.isDestroyed()) {
                b.setY(b.getY() + bombSpeed);   
                if (b.getY() >= GROUND - BOMB_HEIGHT) {
                    b.setDestroyed(true);
                }
            }
        }
    }

    public void run(){
		
        long beforeTime, timeDiff, sleep;

        beforeTime = System.currentTimeMillis();
		
        while (ingame) {
        	
            repaint();
            animationCycle();
            
			if(levelPassed)
				interLevelMenu();
				
            timeDiff = System.currentTimeMillis() - beforeTime;
            sleep = DELAY - timeDiff;

            if (sleep < 0) 
                sleep = 2;
            
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                System.out.println("interrupted");
            }
            beforeTime = System.currentTimeMillis();
        }
        try
        {
        	gameOver();
        }
        catch (IOException e)
        {
        	System.out.println("IOException");	
        }
        
    }
    
    private class InputManager extends KeyAdapter {

        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);
        }

        public void keyPressed(KeyEvent e){

          player.keyPressed(e);

          int x = player.getX();
          int y = player.getY();

          if (ingame)
          {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                if (!shot.isVisible())
                {
                	try{
            		in = new FileInputStream(soundFile);
        			audioStream = new AudioStream(in);
                	AudioPlayer.player.start(audioStream);
            	}
            	catch(Exception ex)
            	{
            		System.out.println("qwe");
            	}
                	shot = new Shot(x, y);
                }
                    
          }
          else if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
          {
          	if(!paused)
          		paused = true;
          	else
          		paused = false;
          }
          else if(e.getKeyCode() == KeyEvent.VK_U)
          {
          		deaths = NUMBER_OF_ALIENS_TO_DESTROY;
          }
          }
        }
    }
    
    private class MouseManager extends MouseAdapter
    {
    	public void mouseClicked(MouseEvent e)
    	{
    		int mx = e.getX();
    		int my = e.getY();
    		
    		if(mainmenu)
    		{
        		if(mx>=30 && mx<=30+BOARD_WIDTH-80)
        		{
        			if(my>= BOARD_WIDTH/2-100 && my<= BOARD_WIDTH/2-100+40)//play
        			{
        				mainmenu = false;
        				gameInit();
        			}
        			else if(my>= BOARD_WIDTH/2-50 && my<= BOARD_WIDTH/2-50+40)//highscore
        			{
        				mainmenu = false;
        				highscore = true;
        				repaint();
        			}
        			else if(my>= BOARD_WIDTH/2 && my<= BOARD_WIDTH/2+40)//help
        			{
        				help = true;
        				mainmenu = false;
        				repaint();	
        			}
        			else if(my>= BOARD_WIDTH/2+50 && my<= BOARD_WIDTH/2+50+40)//credits
        			{
        				mainmenu = false;
        				cds = true;
        				repaint();
        			}
        			else if(my>= BOARD_WIDTH/2+100 && my<= BOARD_WIDTH/2+100+40)//quit
        			{
        				System.exit(0);
        			}
        		}
    		}
    		else if(highscore)
    		{
    			if(mx>=200 && mx<= 250 && my>=BOARD_WIDTH/2-20 && my<=BOARD_WIDTH/2+10)
    			{
    				highscore = false;
    				mainmenu = true;
    				repaint();
    			}
    		}
    		else if(cds)
    		{
    			if(mx>=30 && mx<=30+BOARD_WIDTH-80)
    				if(my>=BOARD_WIDTH/2+50 && my<=BOARD_WIDTH/2+50+40)
    				{
    					cds = false;
    					mainmenu = true;
    					repaint();
    				}
    		}
    		else if(help)
    		{
    			if(mx>=30 && mx<=30+BOARD_WIDTH-80)
    				if(my>=BOARD_WIDTH/2+50 && my<=BOARD_WIDTH/2+50+40)
    				{
    					help = false;
    					mainmenu = true;
    					repaint();
    				}
    				
    		}
    		if(paused)
    		{
    			if(mx>=30 && mx<=30+BOARD_WIDTH-80)
        		{
        			if(my>= BOARD_WIDTH/2-50 && my<= BOARD_WIDTH/2-50+40)//continue
        			{
        				paused = false;
        			}
        			if(my>= BOARD_WIDTH/2 && my<= BOARD_WIDTH/2+40)//quit
        			{
        				mainmenu = true;
        				paused = false;
        			}
        		}			
    		}
    		if(levelPassed)
    		{
    			if(mx>=30 && mx<=30+BOARD_WIDTH-80)
    			{
    				if(my>=BOARD_WIDTH/2-50 && my<=BOARD_WIDTH/2-50+40) //ad
    				{
    					ILM.buyAD();	
    				}
    				if(my>=BOARD_WIDTH/2 && my<=BOARD_WIDTH/2+40) //as
    				{
    					ILM.buyAS();
    				}	
    				if(my>=BOARD_WIDTH/2+50 && my<=BOARD_WIDTH/2+50+40) //s
    				{
    					ILM.buyS();
    				}
    			}
    			if(mx>=150 && mx<=200 && my>=BOARD_WIDTH/2+65+50 && my<=BOARD_WIDTH/2+65+50+30)//ok
    			{
    				ready = true;
    			}	
    		}
    	}
    }
    
    //getter and setters
    
    public int getMissileSpeed()
    {
    	return missileSpeed;
    }
    
    public int getPlayerSpeed()
    {
    	return playerSpeed;
    }
    
    public int getAttackDamage()
    {
    	return attackDamage;
    }
    
    public void setMissileSpeed(int missileSpeed)
    {
    	this.missileSpeed = missileSpeed;
    }
    
    public void setPlayerSpeed(int playerSpeed)
    {
    	this.playerSpeed = playerSpeed;
    }
    
    public void setAttackDamage(int attackDamage)
    {
    	this.attackDamage = attackDamage;
    }
    
    public int getCoins()
    {
    	return coins;
    }
    
    public void setCoins(int coins)
    {
    	this.coins = coins;
    }

}