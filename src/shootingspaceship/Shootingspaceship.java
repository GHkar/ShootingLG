package shootingspaceship;

import Code.Enemy.S1EnemyMobile;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

public class Shootingspaceship extends JPanel implements Runnable {

    private Thread th;
    private Player player;
    private Shot[] shots;
    private ArrayList enemies;
    private final int shotSpeed = -2; //-가 앞으로 나감 (절대값 =속도 )
    private final int playerLeftSpeed = -2;
    private final int playerRightSpeed = 2;
    private final int width = 1100;   //창가로
    private final int height = 600;  //창세로
    private final int playerMargin = 10; //창끝여백
    private final int enemyMaxDownSpeed = 1; //적 내려오는 속도
    private final int enemyMaxHorizonSpeed = 1; //적 내려오는 속도
    private final int enemyTimeGap = 2000; //적이 만들어지는 간격 unit: msec
    private final float enemyDownSpeedInc = 0.3f; //아직몰라
    private final int maxEnemySize = 10; //적 나오는 최대개수
    private int enemySize; //적개수
    private javax.swing.Timer timer;
    private boolean playerMoveLeft; //플레이어 왼쪽키검사
    private boolean playerMoveRight; //플레이어 오른쪽키검사
    private Image dbImage;
    private Graphics dbg;
    private Random rand;
    private int maxShotNum = 20; //한창에서 최대 나갈수있는 총알갯수

    public Shootingspaceship() {
        setBackground(Color.black);
        setPreferredSize(new Dimension(width, height));
        player = new Player(width / 2, (int) (height * 0.9), playerMargin, width-playerMargin ); //플레이어생성
        shots = new Shot[ maxShotNum ]; //총생성
        enemies = new ArrayList(); //적생성
        enemySize = 0;  //처음 적개수 
        rand = new Random(1);
        timer = new javax.swing.Timer(enemyTimeGap, new addANewEnemy());
        timer.start();
        addKeyListener(new ShipControl());
        setFocusable(true);
    }

    public void start() {
        th = new Thread(this);
        th.start();
    }

    private class addANewEnemy implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (++enemySize <= maxEnemySize) {
                float downspeed;
                do {
                    downspeed = rand.nextFloat() * enemyMaxDownSpeed; //적마다 속도 랜덤으로지정
                } while (downspeed == 0); //멈출때까지

                float horspeed = rand.nextFloat() * 2 * enemyMaxHorizonSpeed - enemyMaxHorizonSpeed; //가로속도
                //System.out.println("enemySize=" + enemySize + " downspeed=" + downspeed + " horspeed=" + horspeed);

                S1EnemyMobile newEnemy = new S1EnemyMobile(width, (int) (rand.nextFloat()*height), downspeed, horspeed,width, height, enemyDownSpeedInc); //11/7(수) 상->하 에서 좌->우로 변경
                enemies.add(newEnemy); //적 나오는 위치 랜덤
            } else {
                timer.stop();
            }
        }
    }

    private class ShipControl implements KeyListener { //키 눌렀을때
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    playerMoveLeft = true;
                    break;
                case KeyEvent.VK_RIGHT:
                    playerMoveRight = true;
                    break;
                case KeyEvent.VK_UP:
                    // generate new shot and add it to shots array
                    for (int i = 0; i < shots.length; i++) {
                        if (shots[i] == null) {
                            shots[i] = player.generateShot();
                            break;
                        }
                    }
                    break; 
            }
        }

        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    playerMoveLeft = false;
                    break;
                case KeyEvent.VK_RIGHT:
                    playerMoveRight = false;
                    break; //키풀었을때
            }
        }

        public void keyTyped(KeyEvent e) {
        }
    }
    
    public void run() {
        //int c=0;
        //쓰레드란, 실제로 작업을 수행하는 주체
        //지금 수행중인 프로세서를 ()안의 지정한 값으로 변경한다. = Thread.MIN_PRIORITY
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        
        //샷을 발사하고, 적과 플레이어의 동작을 수행하게 하는 반복문
        while (true) {
            //System.out.println( ++c );
            // 샷 배열 안에 있는 샷의 동작
            for (int i = 0; i < shots.length; i++) {
                //샷이 있으면
                if (shots[i] != null) {
                    shots[i].moveShot(shotSpeed);// 샷을 움직임
                    // 샷이 화면 밖으로 넘어가면 삭제
                    if (shots[i].getY() < 0) {
                        shots[i] = null; // 배열에서 샷을 지움
                    }
                }
            }
            //플레이어 왼쪽 오른쪽 동작 수행
            if (playerMoveLeft) {
                player.moveX(playerLeftSpeed);
            } else if (playerMoveRight) {
                player.moveX(playerRightSpeed);
            }
            
            //Iterator 컬렉션에 저장되어 있는 요소들을 읽어오는 것. 즉, 적 리스트를 갖고 옴
            Iterator enemyList = enemies.iterator();
            //적 리스트에 적이 있다면 적 동작을 수행
            while (enemyList.hasNext()) {
                Enemy enemy = (Enemy) enemyList.next();
                if(enemy.getAlive()){  // 적이 살아있다면
                    enemy.move();
                }
                if(enemy.isCollidedWithShot(shots)) // 적이 총알과 부딪혔을 때
                {
                    if (enemy.getHp() <= 0) { // 체력이 0 이하 -->  죽음
                            enemy.setAlive(false);
                        }
                }
            }
            //적을 그림
            repaint();

            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                // do nothing
            }

            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        }
    }

    //전체적인 그래픽을 그리기 위한 설정과 초기화, 배경 설정
    public void initImage(Graphics g) {
        //이미지가 아무것도 없을 때 이미지를 그림
        if (dbImage == null) {
            dbImage = createImage(this.getSize().width, this.getSize().height);
            dbg = dbImage.getGraphics();
        }
        //배경을 검은색으로 칠함
        dbg.setColor(getBackground());  // 색깔 지정 ex) Color.GRAY
        dbg.fillRect(0, 0, this.getSize().width, this.getSize().height);

        dbg.setColor(getForeground());
        //paint (dbg);

        g.drawImage(dbImage, 0, 0, this);
    }
    //플에이어와 적 그리고 샷을 그래픽으로 구현
    public void paintComponent(Graphics g) {
        initImage(g);

        // 플레이어 그리기
        player.drawPlayer(g);
        
        // 적 그리기
        Iterator enemyList = enemies.iterator();
        while (enemyList.hasNext()) {
            Enemy enemy = (Enemy) enemyList.next();
            if(enemy.getAlive()) // 적이 살아있는 상태면 그림
            {
                enemy.draw(g);
            }
//            if (enemy.isCollidedWithShot(shots)) {
//                enemyList.remove();
//            }
            if (enemy.isCollidedWithPlayer(player)) {
                enemyList.remove();
                System.exit(0);
            }
            if(enemy.getAlive()==false){ //적이 죽으면 리스트에서 삭제
                enemyList.remove();
            }
            if(enemy.pass()){ // 벽 통과하면 삭제
                enemyList.remove();
            }
        }

        // 샷 그리기
        for (int i = 0; i < shots.length; i++) {
            if (shots[i] != null) {
                shots[i].drawShot(g);
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        JFrame frame = new JFrame("Shooting");//나오는 창 = 실행 창
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Shootingspaceship ship = new Shootingspaceship();
        frame.getContentPane().add(ship);
        frame.pack();
        frame.setVisible(true);
        ship.start();
    }
}