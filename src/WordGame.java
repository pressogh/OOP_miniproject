import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class WordGame extends JFrame {
    Vector<Word> wordVector = new Vector<>();
    private GamePanel panel = new GamePanel();
    private UserCharacter user = new UserCharacter();
    JTextField jtf = new JTextField();

    public WordGame () {
        setTitle("WordGame");
        setSize(800, 500);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container c =  getContentPane();
        c.add(panel, BorderLayout.CENTER);

        jtf.setSize(800, 30);
        jtf.addActionListener(new TextInputListener());
        c.add(jtf, BorderLayout.SOUTH);

        wordVector.add(new Word("Hello", (int) (Math.random() * 800), 0));
        wordVector.add(new Word("World", (int) (Math.random() * 800), 0));
        wordVector.add(new Word("Java", (int) (Math.random() * 800), 0));
        wordVector.add(new Word("C++", (int) (Math.random() * 800), 0));
        wordVector.add(new Word("Python", (int) (Math.random() * 800), 0));
        wordVector.add(new Word("Javascript", (int) (Math.random() * 800), 0));
        wordVector.add(new Word("OOP", (int) (Math.random() * 800), 0));
        wordVector.add(new Word("PP", (int) (Math.random() * 800), 0));

        WordMoveThread wmt = new WordMoveThread();
        wmt.start();
        setVisible(true);
    }

    class GamePanel extends JPanel {
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            user.draw(g);
            for (Word item : wordVector) {
                item.draw(g);
            }
            repaint();
        }
    }

    class WordMoveThread extends Thread {
        @Override
        public void run() {
            while (true) {
                for (Word item : wordVector) {
                    item.y += item.speed;

                    if (user.x > item.x) {
                        item.x += 3;
                    }
                    else if (user.x < item.x) {
                        item.x -= 3;
                    }
                    try {
                        sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    class TextInputListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String text = jtf.getText();
            System.out.println(text);
            jtf.setText("");
        }
    }

    public static void main(String[] args) {
        new WordGame();
    }
}

class Word {
    String word;
    int x, y;
    int speed;
    String item;

    public Word(String word, int x, int y) {
        this.word = word;
        this.x = x;
        this.y = y;
        this.item = Math.random() > 0.1 ? "Life" : "None";
        this.speed = (int) (Math.random() * 9) + 1;
    }

    public void draw(Graphics g) {
        if (item.equals("None")) g.setColor(Color.GREEN);
        else g.setColor(Color.BLACK);
        g.drawString(word, x, y);
    }
}

class UserCharacter {
    int x, y;
    int width, height;
    int life;
    public UserCharacter() {
        this.x = 400;
        this.y = 400;
        this.width = 30;
        this.height = 30;
        this.life = 3;
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillOval(x, y, width, height);
    }
}