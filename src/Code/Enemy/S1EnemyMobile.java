/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Code.Enemy;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import shootingspaceship.Enemy;
import shootingspaceship.Player;
import shootingspaceship.Shot;

/**
 *
 * @author 김애리
 */
public class S1EnemyMobile extends Enemy{ //Enemy상속
    
    
    
    public S1EnemyMobile(int x, int y, float delta_x, float delta_y, int max_x, int max_y, float delta_y_inc)
    {
        super(x, y, delta_x, delta_y, max_x, max_y, delta_y_inc);
        collision_distance = 20; //충돌 반경 20으로 설정
    }

    @Override
    public void move() {
        super.move(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isCollidedWithShot(Shot[] shots) {
        return super.isCollidedWithShot(shots); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isCollidedWithPlayer(Player player) {
         return super.isCollidedWithPlayer(player);
    }

    @Override
    public void draw(Graphics g) {
        File f = new File("src/Image/Enemy/S1EnemyMobile.png");
        Image img = null;
        try {
            img = ImageIO.read(f);
        } catch (IOException ex) {
            System.exit(1);
        }
        g.drawImage(img, (int) x_pos-50, (int) y_pos-50, null); // -x 한 값이 클 수록 충돌 반경이 커짐
    }
}
