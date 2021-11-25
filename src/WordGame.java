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

        wordVector.add(new Word("Hello", (int) (Math.random() * 800), (int) (Math.random() * 100)));
        wordVector.add(new Word("World", (int) (Math.random() * 800), (int) (Math.random() * 100)));
        wordVector.add(new Word("Java", (int) (Math.random() * 800), (int) (Math.random() * 100)));
        wordVector.add(new Word("C++", (int) (Math.random() * 800), (int) (Math.random() * 100)));
        wordVector.add(new Word("Python", (int) (Math.random() * 800), (int) (Math.random() * 100)));
        wordVector.add(new Word("Javascript", (int) (Math.random() * 800), (int) (Math.random() * 100)));
        wordVector.add(new Word("OOP", (int) (Math.random() * 800), (int) (Math.random() * 100)));
        wordVector.add(new Word("PP", (int) (Math.random() * 800), (int) (Math.random() * 100)));

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
                for (int i = 0; i < wordVector.size(); i++) {
                    wordVector.get(i).y += wordVector.get(i).speed;

                    if (Math.abs(user.y - wordVector.get(i).y) > 0) {
                        wordVector.get(i).x += (user.x - wordVector.get(i).x) * wordVector.get(i).speed / Math.abs(user.y - wordVector.get(i).y);
                    }

                    if ((wordVector.get(i).x <= user.x - 15 && wordVector.get(i).x >= user.x + 15) || wordVector.get(i).y >= user.y) {
                        // wordVector에서 item을 remove하면 word가 이동하는 속도가 바뀌게 됨
                        // 따라서 wordVector에서 item을 삭제하지 않는 방법으로 변경
                        if (wordVector.get(i).word.equals("")) continue;

                        wordVector.get(i).word = "";
                        user.life--;
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

            for (Word item : wordVector) {
                if (item.word.equals(text)) {
                    if (item.item.equals("Life") && user.life < 3) {
                        user.life++;
                    }
                    item.word = "";
                }
            }
            System.out.println("Life " + user.life);
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

        int randItem = (int) (Math.random() * 100) + 1;
        if (randItem >= 1 && randItem <= 10) this.item = "Life";
        else if (randItem > 10 && randItem <= 20) this.item = "Slow";
        else this.item = "None";

        this.speed = (int) (Math.random() * 4) + 1;
    }

    public void draw(Graphics g) {
        if (item.equals("None")) g.setColor(Color.BLACK);
        else if (item.equals("Life")) g.setColor(Color.GREEN);
        else if (item.equals("Slow")) g.setColor(Color.BLUE);
        g.drawString(word, x, y);
    }
}

class UserCharacter {
    int x, y;
    int width, height;
    int life;
    public UserCharacter() {
        this.x = 400;
        this.y = 350;
        this.width = 30;
        this.height = 30;
        this.life = 3;
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillOval(x, y, width, height);

        g.setColor(Color.RED);
        for (int i = 0; i < life; i++) {
            g.fillOval(x + (i < (life / 2) ? -1 * 20 * i : 20 * i) - 13, y + 50, 15, 15);
        }
    }
}