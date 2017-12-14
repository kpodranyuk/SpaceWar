/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.spacewar;

import com.badlogic.gdx.utils.Array;
import static com.mygdx.spacewar.ObjectImage.ObjectType.ENMMISSILE;

/**
 * Строитель снарядов врагов
 * @author Katie
 */
public class EnemyMissileBuilder {
    private ObjectImage enemiesMissileSprite;  /// Спрайт снаряда врага
    
    public EnemyMissileBuilder(){     
        enemiesMissileSprite = new ObjectImage("fire/redpng.png", ENMMISSILE);
    }
    
    public Array<Missile> generateEnemyMissile(ObjectImage.ObjectType missileType){
        return null;
    }
}
