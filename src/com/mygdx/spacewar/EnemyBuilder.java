/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.spacewar;

import com.badlogic.gdx.utils.Array;
import static com.mygdx.spacewar.ObjectImage.ObjectType.ENMSHIP;
import static com.mygdx.spacewar.ObjectImage.ObjectType.ENMSHIPFAST;
import static com.mygdx.spacewar.ObjectImage.ObjectType.ENMSHIPHEALTHY;

/**
 * Строитель врагов
 * @author Katie
 */
public class EnemyBuilder {
    private Array<ObjectImage> enemiesSprites;          /// Спрайты врагов
    
    public EnemyBuilder (){
        ObjectImage enemiesImg = null;        
        enemiesImg = new ObjectImage("enemies/enemy1.png", ENMSHIP);
        enemiesSprites.add(enemiesImg);
        enemiesImg = new ObjectImage("enemies/enemy2.png", ENMSHIPHEALTHY);
        enemiesSprites.add(enemiesImg);
        enemiesImg = new ObjectImage("enemies/enemy3.png", ENMSHIPFAST);
        enemiesSprites.add(enemiesImg);
    }
    
    public Array<Ship> generateEnemy(int gameLevel){
        return null;
    }
}
