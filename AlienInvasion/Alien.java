import javax.swing.ImageIcon;


public class Alien extends Sprite {

    private Bomb bomb;
    private final String shot = "alien.gif";
	private int maxHP, currentHP;

    public Alien(int x, int y, int maxHP) {
        this.x = x;
        this.y = y;
		this.maxHP = maxHP;
		currentHP = maxHP;
        bomb = new Bomb(x, y);
        ImageIcon ii = new ImageIcon(this.getClass().getResource(shot));
        setImage(ii.getImage());

    }
    
    public void damage(int damage)
    {
    	currentHP -= damage;
    }
    
    public int getCurrentHP()
    {
    	return currentHP;
    }

    public void act(int direction) {
        this.x += direction;
    }

    public Bomb getBomb() {
        return bomb;
    }

    public class Bomb extends Sprite {

        private final String bomb = "bomb.png";
        private boolean destroyed;

        public Bomb(int x, int y) {
            setDestroyed(true);
            this.x = x;
            this.y = y;
            ImageIcon ii = new ImageIcon(this.getClass().getResource(bomb));
            setImage(ii.getImage());
        }

        public void setDestroyed(boolean destroyed) {
            this.destroyed = destroyed;
        }

        public boolean isDestroyed() {
            return destroyed;
        }
    }
}
