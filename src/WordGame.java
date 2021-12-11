import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Vector;

public class WordGame extends JFrame {
    Vector<Word> wordVector = new Vector<>();
    Vector<String> tempVector;
    Vector<Bullet> bulletVector = new Vector<>();
    private GamePanel panel = new GamePanel();
    private JTextField jtf = new JTextField();
    private UserCharacter user = new UserCharacter();
    private GameData gameData = new GameData();

    public WordGame () {
        setTitle("WordGame");
        setSize(800, 500);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container c =  getContentPane();

        c.add(panel, BorderLayout.CENTER);

        jtf.setSize(800, 30);
        jtf.addActionListener(new TextInputListener());
        c.add(jtf, BorderLayout.SOUTH);

        ScorePanel sp = new ScorePanel();
        sp.setPreferredSize(new Dimension(200, 500));
        c.add(sp, BorderLayout.EAST);

        tempVector = loadDataFromFile("words.txt");

        WordMoveThread wmt = new WordMoveThread();
        wmt.start();
        BulletMovingThread bmt = new BulletMovingThread();
        bmt.start();
        SlowThread st = new SlowThread();
        st.start();
        DrawFireThread dft = new DrawFireThread();
        dft.start();
        WordAddThread wat = new WordAddThread();
        wat.start();
        
        setVisible(true);
    }

    class GamePanel extends JPanel {
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            user.draw(g);
            for (int i = 0; i < bulletVector.size(); i++) {
                bulletVector.get(i).draw(g);
            }

            if (gameData.drawFire) {
                Image bulletFire = new ImageIcon("./bullet_fire.png").getImage();
                g.drawImage(bulletFire, 295, 320, 50, 50, null);
            }
            // for (Word item : wordVector) 이런식으로 하면 오류 발생
            for (int i = 0; i < wordVector.size(); i++) wordVector.get(i).draw(g);
            repaint();
        }
    }

    class ScorePanel extends JPanel {
        public ScorePanel() {
            setBackground(Color.GRAY);
            setLayout(new FlowLayout());
        }
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Word Score 계산
            gameData.score.get(1).scoreNumber = gameData.deletedCount * 10;
            // Life Score 계산
            gameData.score.get(2).scoreNumber = user.life * 100;
            // Stage Score 계산
            gameData.score.get(3).scoreNumber = gameData.stage * 100;
            // Total Score 계산
            gameData.score.get(0).scoreNumber = 0;
            for (int i = 1; i < gameData.score.size(); i++) {
                gameData.score.get(0).scoreNumber += gameData.score.get(i).scoreNumber;
            }

            g.setFont(new Font("Gothic", Font.BOLD, 20));
            g.drawString("STAGE", 50, 100);
            g.drawString(Integer.toString(gameData.stage), 155, 100);

            g.setFont(new Font("Gothic", Font.BOLD, 15));
            for (int i = 0; i < gameData.score.size(); i++) {
                g.setColor(Color.BLACK);
                g.drawString(gameData.score.get(i).scoreName, 30, 150 + i * 50);
            }

            for (int i = 0; i < gameData.score.size(); i++) {
                g.setColor(Color.BLACK);
                g.drawString(gameData.score.get(i).toString(), 150, 150 + i * 50);
            }

            repaint();
        }
    }

    class WordMoveThread extends Thread {
        @Override
        public void run() {
            while (true) {
                Vector<String> deletedWord = new Vector<>();

                for (int i = 0; i < wordVector.size(); i++) {
                    wordVector.get(i).y += wordVector.get(i).speed;

                    if (Math.abs(user.y - wordVector.get(i).y) > 0) {
                        try {
                            wordVector.get(i).x += (user.x - wordVector.get(i).x) * wordVector.get(i).speed / Math.abs(user.y - wordVector.get(i).y);
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println(e);
                        }
                    }
                    if ((wordVector.get(i).x <= user.x - 15 && wordVector.get(i).x >= user.x + 15) || wordVector.get(i).y >= user.y) {
                        deletedWord.add(wordVector.get(i).word);
                        user.life = user.life > 0 ? user.life - 1 : 0;
                    }
                }

                // 유저 캐릭터에 몬스터 피격 시 몬스터 제거
                for (int i = 0; i < deletedWord.size(); i++) {
                    int idx = findTargetIndex(deletedWord.get(i));
                    wordVector.remove(idx);
                }

                try {
                    sleep(gameData.speed - gameData.stage * 10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int findTargetIndex(String target) {
        for (int i = 0; i < wordVector.size(); i++) {
            if (target.equals(wordVector.get(i).word)) return i;
        }
        return -1;
    }

    class BulletMovingThread extends Thread {
        @Override
        public void run() {
            while (true) {
                Vector<Integer> deleteBullet = new Vector<>();
                for (int i = 0; i < bulletVector.size(); i++) {
                    bulletVector.get(i).y -= 5;

                    // wordVector에서 삭제할 단어의 인덱스 검색
                    int targetIndex = findTargetIndex(bulletVector.get(i).target) == -1 ? 0 : findTargetIndex(bulletVector.get(i).target);
                    // 0으로 할 시 좌표가 튀는 버그가 있어 10으로 변경
                    if (Math.abs(wordVector.get(targetIndex).y - bulletVector.get(i).y) > 10) {
                        bulletVector.get(i).x += bulletVector.get(i).weight + (wordVector.get(targetIndex).x - bulletVector.get(i).x) * 20 / Math.abs(wordVector.get(targetIndex).y - bulletVector.get(i).y);
                    }
                    if ((bulletVector.get(i).x <= wordVector.get(targetIndex).x - 100 && bulletVector.get(i).x >= wordVector.get(targetIndex).x + 100) || bulletVector.get(i).y <= wordVector.get(targetIndex).y) {
                        deleteBullet.add(i);
                    }
                }

                // 격추된 bullet 삭제
                for (int i = 0; i < deleteBullet.size(); i++) {
                    int deleteIndex = findTargetIndex(bulletVector.get(deleteBullet.get(i)).target);
                    // 아이템 기능 작동
                    if (wordVector.get(deleteIndex).item.equals("Life") && user.life < 3) {
                        user.life++;
                    }
                    else if (wordVector.get(deleteIndex).item.equals("Slow")) gameData.isSlow = true;
                    wordVector.remove(deleteIndex);
                    bulletVector.remove((int)deleteBullet.get(i));
                    gameData.deletedCount++;
                }
                try {
                    sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class SlowThread extends Thread {
        @Override
        public void run() {
            int check = 0;
            while (true) {
                if (gameData.isSlow) {
                    gameData.speed = 150;

                    if (check >= 1500) {
                        gameData.isSlow = false;
                        check = 0;
                    }
                    check += 10;
                }
                else gameData.speed = 100 - gameData.stage * 10;

                try {
                    sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class DrawFireThread extends Thread {
        @Override
        public void run() {
            while (true) {
                if (!gameData.drawFire) {
                    try {
                        sleep(500);
                        gameData.drawFire = false;
                        repaint();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // 화면에 표시되는 단어의 개수를 유지시키는 스레드
    class WordAddThread extends Thread {
        @Override
        public void run() {
            while (true) {
                if (wordVector.size() < 5 * gameData.stage) {
                    wordVector.add(new Word(tempVector.get(0), (int)(Math.random() * 600), (int)(Math.random() * 100)));
                    tempVector.remove(0);
                }
            }
        }
    }

    class TextInputListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String text = jtf.getText();
            Vector<String> deletedItem = new Vector<>();

            for (int i = 0; i < wordVector.size(); i++) {
                if (wordVector.get(i).word.equals(text)) {
                    deletedItem.add(text);
                }
            }

            for (String item : deletedItem) {
                // bullet 생성
                bulletVector.add(new Bullet(312, 340, item));
                gameData.drawFire = true;
            }

            // 테스트
            System.out.println("Word " + wordVector.size());
            jtf.setText("");
        }
    }

    public static void saveDataToFile(Vector<Word> data, String fileName)  {
        // FileWriter을 이용해 data에 있는 데이터 저장
        FileWriter out;
        try {
            out = new FileWriter(fileName);
            for (Word item : data) {
                out.write(item.word + "\n");
            }
            out.close();
        } catch (IOException e) {
            System.out.println("FileWrite Error!!");
        }
    }
    public Vector<String> loadDataFromFile(String fileName) {
        // BufferedReader을 이용해 파일에서 문자열을 한줄씩 읽어옴
        BufferedReader in;
        Vector<String> res = new Vector<>();
        try {
            in = new BufferedReader(new FileReader(fileName));
            String s;

            while ((s = in.readLine()) != null) {
                res.add(s);
            }
            in.close();
        } catch (FileNotFoundException e) {
            System.out.println("No File Error!!");
        } catch (IOException e) {
            System.out.println("FileRead Error!!");
        }

        return res;
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
        else if (item.equals("Slow")) g.setColor(Color.CYAN);
        g.drawString(word, x, y);
    }
}
class Bullet {
    int x, y;
    int width, height;
    int weight;
    String target;
    public Bullet(int x, int y, String target) {
        this.x = x;
        this.y = y;
        this.target = target;
        width = 8;
        height = 8;
        weight = ((int)(Math.random() * 2) > 0.5 ? -1 : 1) * 10;
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(x, y, width, height);
    }
}

class UserCharacter {
    int x, y;
    int width, height;
    int life;
    public UserCharacter() {
        this.x = 290;
        this.y = 340;
        this.width = 60;
        this.height = 60;
        this.life = 3;
    }

    public void draw(Graphics g) {
        Image heart = new ImageIcon("./heart.png").getImage();
        g.setColor(Color.BLACK);

        Image spaceship = new ImageIcon("./spaceship.png").getImage();
        g.drawImage(spaceship, x, y, width, height, null);
        for (int i = 0; i < life; i++) {
            g.drawImage(heart, x + (i < (life / 2) ? -1 * 30 * i : 30 * i) - 10, y + 60, 20, 20, null);
        }
    }
}

class GameData {
    int stage;
    int speed;
    int deletedCount;
    boolean isSlow;
    boolean drawFire;
    Vector<Score> score = new Vector<>();
    public GameData() {
        stage = 1;
        speed = 100;
        deletedCount = 0;
        isSlow = false;
        drawFire = false;
        score.add(new Score("Total Score"));
        score.add(new Score("Word Score"));
        score.add(new Score("Life Score"));
        score.add(new Score("Stage Bonus"));
    }
}
class Score {
    String scoreName;
    int scoreNumber;
    public Score(String scoreName) {
        this.scoreName = scoreName;
        this.scoreNumber = 0;
    }
    public String toString() {
        return Integer.toString(scoreNumber);
    }
}