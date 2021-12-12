import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.io.*;
import java.util.Comparator;
import java.util.Vector;

public class WordGame extends JFrame {
    Vector<Word> wordVector = new Vector<>();
    Vector<String> bossWordVector = new Vector<>();
    Vector<String> tempVector;
    Vector<Bullet> bulletVector = new Vector<>();
    private GamePanel panel = new GamePanel();
    private JTextField jtf = new JTextField();
    private UserCharacter user = new UserCharacter();
    private GameData gameData = new GameData();
    private Boss boss = new Boss();

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
            boss.draw(g);

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
            gameData.score.get(1).scoreNumber = gameData.totalDeletedCount * 10;
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

    class BossThread extends Thread {
        @Override
        public void run() {
            while (true) {
                if (gameData.isBoss) {
                    for (int j = -180; j < boss.posY; j += 3) {
                        boss.y = j;
                        try {
                            sleep(100);
                        } catch (InterruptedException e) {
                            System.out.println("Boss Interrupted!!");
                        }
                    }

                    while (boss.life > 0) {
                        try {
                            sleep(5000);
                        } catch (InterruptedException e) {
                            System.out.println("Boss Interrupted!!");
                        }
                        wordVector.add(new Word(bossWordVector.get(0), (int) (Math.random() * 200) + 200, 150));
                        bossWordVector.remove(0);

                    }
                    gameData.isBoss = false;
                }
                else {
                    gameData.isBossMoving = true;

                    for (int j = boss.posY; j >= -180; j -= 3) {
                        while (true) {
                            if (bulletVector.size() <= 0) break;
                            bulletVector.remove(0);
                        }
                        while (true) {
                            if (wordVector.size() <= 0) break;
                            wordVector.remove(0);
                        }
                        boss.y = j;
                        try {
                            sleep(100);
                        } catch (InterruptedException e) {
                            System.out.println("Boss Interrupted!!");
                        }
                    }
                    System.out.println("Thread Finished!! " + gameData.isBoss);
                    gameData.isBossMoving = false;
                    gameData.stage++;
                    boss.life = 100;
                    break;
                }
            }
        }
    }

    // 총알을 움직이게 해주는 스레드
    class BulletMovingThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Vector<Integer> deleteBullet = new Vector<>();
                    for (int i = 0; i < bulletVector.size(); i++) {
                        bulletVector.get(i).y -= 8;

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
                        int deleteIndex;
                        deleteIndex = findTargetIndex(bulletVector.get(deleteBullet.get(i)).target);
                        if (deleteIndex == -1) {
                            bulletVector.remove((int) deleteBullet.get(i));
                            continue;
                        }

                        // 아이템 기능 작동
                        if (wordVector.get(deleteIndex).item.equals("Life") && user.life < 5) {
                            user.life++;
                        } else if (wordVector.get(deleteIndex).item.equals("Slow")) gameData.isSlow = true;

                        wordVector.remove(deleteIndex);
                        bulletVector.remove((int) deleteBullet.get(i));


                        if (!gameData.isBoss && !gameData.isBossMoving) {
                            gameData.totalDeletedCount++;
                            gameData.deletedCount++;
                        }
                        if (gameData.isBoss) boss.life = boss.life > 0 ? boss.life - 20 : 0;

                        // 지워진 단어의 개수가 15개 이상이면 boss 등장
                        if (gameData.deletedCount % 3 == 0 && !gameData.isBoss) {
                            gameData.deletedCount = 0;
                            gameData.isBoss = true;
                            BossThread bt = new BossThread();
                            bt.start();
                            while (true) {
                                if (bulletVector.size() <= 0) break;
                                bulletVector.remove(0);
                            }
                            while (true) {
                                if (wordVector.size() <= 0) break;
                                wordVector.remove(0);
                            }
                        }
                    }
                    try {
                        sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    while (true) {
                        if (bulletVector.size() <= 0) break;
                        bulletVector.remove(0);
                    }
                }
            }
        }
    }

    // Slow 아이템의 기능 스레드
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
                // gameData.speed = 100 - gameData.stage * 10 로 하면 너무 어려움
                else gameData.speed = 100;

                try {
                    sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 우주선에서 발사되는 총알의 불꽃을 표시해주는 스레드
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
                System.out.println("Running"); // 이거 없으면 오류
                if (!gameData.isBoss && !gameData.isBossMoving) {
                    if (wordVector.size() < gameData.stage + 3) {
                        wordVector.add(new Word(tempVector.get(0), (int) (Math.random() * 600), (int) (Math.random() * 100)));
                        tempVector.remove(0);
                    }
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
                // 같은 단어를 연속적으로 입력 시 발생하는 오류 해결
                boolean flag = true;
                for (int i = 0; i < bulletVector.size(); i++) {
                    if (bulletVector.get(i).target.equals(item)) {
                        flag = false;
                        break;
                    }
                }

                if (flag) {
                    // bullet 생성
                    bulletVector.add(new Bullet(312, 340, item));
                    gameData.drawFire = true;
                }
            }

            // 테스트
            System.out.println("Word " + wordVector.size());
            System.out.println("IsBossMoving " + gameData.isBossMoving);
            System.out.println("IsBoss " + gameData.isBoss);
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

        res.sort(Comparator.comparingInt(String::length));
        for (int i = res.size() - 200; i < res.size(); i++) {
            bossWordVector.add(res.get(i));
            res.remove(i);
        }
        Collections.shuffle(bossWordVector);
        Collections.shuffle(res);
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
        g.setFont(new Font("Gothic", Font.BOLD, 14));
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
        Image spaceship = new ImageIcon("./spaceship.png").getImage();

        g.drawImage(spaceship, x, y, width, height, null);
        for (int i = 0; i < life; i++) {
            g.drawImage(heart, x + i * 40 - (20 + 10 * i), y + 60, 20, 20, null);
        }
    }
}
class Boss {
    int x, y;
    int width, height;
    int life;
    int posY = 40;
    public Boss() {
        this.x = 250;
        this.y = -180;
        this.width = 120;
        this.height = 120;
        this.life = 100;
    }

    public void draw(Graphics g) {
        Image heart = new ImageIcon("./heart.png").getImage();
        Image spaceship = new ImageIcon("./Boss.png").getImage();

        g.drawImage(spaceship, x, y, width, height, null);
        g.setColor(Color.BLACK);
        g.fillRect(x - 40, y - 30, 200, 20);
        g.setColor(Color.RED);
        g.fillRect(x - 40, y - 30, life * 2, 20);
    }
}
class GameData {
    int stage;
    int speed;
    int deletedCount, totalDeletedCount;
    boolean isSlow;
    boolean isBoss;
    boolean drawFire;
    boolean isBossMoving;
    Vector<Score> score = new Vector<>();
    public GameData() {
        stage = 1;
        speed = 100;
        deletedCount = 0;
        totalDeletedCount = 0;
        isSlow = false;
        isBoss = false;
        drawFire = false;
        isBossMoving = false;
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