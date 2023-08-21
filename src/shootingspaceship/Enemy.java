/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shootingspaceship;

import java.awt.Graphics;
import java.awt.Color;

/**
 *
 * @author wgpak
 */
public class Enemy {

    protected float x_pos;
    protected float y_pos;
    protected float delta_x;
    protected float delta_y;
    protected int max_x;
    protected int max_y;
    protected float delta_y_inc;
    protected int collision_distance; //충돌 거리 반경
    private int hp; // 적의 HP
    
    private boolean alive; // HP 구현을 위한 필드
    

    public Enemy(int x, int y, float delta_x, float delta_y, int max_x, int max_y, float delta_y_inc) {
        x_pos = x;
        y_pos = y;
        this.delta_x = delta_x;
        this.delta_y = delta_y;
        this.max_x = max_x;
        this.max_y = max_y;
        this.delta_y_inc = delta_y_inc;
        
        this.hp = 5;
        this.alive = true;
        this.collision_distance = 10;
    }
    public void move() {
        x_pos-=delta_x; // +=좌->우 / -=우->좌 적 움직임
        y_pos+=delta_y; // +=하->상 / -=상->하 적 움직임
        
        if(y_pos>max_y){ //아래쪽 벽에 닿았을 때
            y_pos=max_y;
            delta_y=-delta_y;
        }
        else if(y_pos<0){ //위쪽 벽에 닿았을 때
            y_pos=0;
            delta_y=-delta_y;
        }
    }
// isCollideWithShot Enemy가 총알과 충돌했는지 여부 검사
    public boolean isCollidedWithShot(Shot[] shots) { //Shot 객체 배열을 파라미터로 받는다.
        for (Shot shot : shots) { //배열을 순차 접근해서 총알 각 하나하나를 다 검사한다.
            if (shot == null) {
                continue; //비어 있으면 다음칸 인덱싱
            }
            if (-collision_distance <= (y_pos - shot.getY()) && (y_pos - shot.getY() <= collision_distance)) {
                if (-collision_distance <= (x_pos - shot.getX()) && (x_pos - shot.getX() <= collision_distance)) {
                    /* collision_distance(이하 콜리디)는 총알과 충돌 판정을 내릴 범위를 정해주는 변수다.
                    조건문을 풀어쓰면 => -콜리디(-10) <= 적좌표-총알좌표 <= 콜리디(10) (if문 두번 써서 x와 y 모두를 충족시켜야 함)
                    예를들어 적좌표가 x=50 y=20이다. 그럼 총알좌표가 x는 40~60 사이고 y는 10~30사이에 들어오면
                    조건 충족 시 해당 총알 collided 메소드 호출
                    */
                    if(shot.getAlive()) // shot이 존재하면
                    {
                        hp -= 1;
                        //shot.getDamage;
                        shot.collided(); // 해당 총알객체 메소드 호출 => alive = false;
                        return true; // 충돌했으므로 true 리턴
                    }
                }
            }
        }
        return false; //충돌안했으니 false 리턴
    }

    public boolean isCollidedWithPlayer(Player player) {
        if (-collision_distance <= (y_pos - player.getY()) && (y_pos - player.getY() <= collision_distance)) {
            if (-collision_distance <= (x_pos - player.getX()) && (x_pos - player.getX() <= collision_distance)) {
                /* isCollideWithShot 메소드와 판정방식이 동일하다. 대신 빼는 값이 총알이 아니고 플레이어 좌표이다.
                충돌하면 true를 리턴한다. => 호출한 곳에서 true를 받아 플레이어 사망을 판단할 것으로 추정
                */
                return true;
            }
        }
        return false; // 아니면 false
    }

    public void draw(Graphics g) {
        g.setColor(Color.yellow);
        int[] x_poly = {(int) x_pos, (int) x_pos - 10, (int) x_pos, (int) x_pos + 10};
        int[] y_poly = {(int) y_pos + 15, (int) y_pos, (int) y_pos + 10, (int) y_pos};
        g.fillPolygon(x_poly, y_poly, 4);
    }
    
    
    public boolean getAlive() { //살아있는지 상태를 구함
        return alive;
    }

    public void setAlive(boolean value) { //alive의 상태를 지정
        this.alive = value;
    }
    
    public int getHp() { //Hp를 받아오고
        return hp;
    }
    
    public void setHp(int hp) // Hp를 직접 설정
    {
        this.hp = hp;
    }
    
    public boolean pass(){
        if(x_pos> max_x)// 왼쪽 벽을 통과했을 때
            return true;
        else 
            return false;
    }

//    public int getDamage() {
//        return damage;
//    }
}