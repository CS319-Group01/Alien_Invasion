import javax.swing.JFrame;
import java.io.IOException;

public class AlienInvasion extends JFrame implements Commons {

    public AlienInvasion() throws IOException
    {
        add(new Board());
        setTitle("Alien Invasion");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(BOARD_WIDTH, BOARD_HEIGTH);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
    }

    public static void main(String[] args) throws IOException {
        new AlienInvasion();
    }
}
