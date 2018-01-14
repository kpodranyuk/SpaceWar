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
    
    public Array<Missile> generateEnemyMissiles(ObjectImage.ObjectType missileType){
        if(missileType==ENMMISSILE)
            return geneateDefaultMissile();
        return null;
    }
    
    private Array<Missile> geneateDefaultMissile(){
        ObjectSprite enemiesMissileView = new ObjectSprite(enemiesMissileSprite, 22, 11, ++(GameSystem.curId));        
        // Снаряд легкого врага летит по прямой траектории
        StraightTrajectory enemiesTrajectory = new StraightTrajectory((float) 250.0, true);
        // Создаем вражеский снаряд
        Missile enemiesMissile = new Missile(1, (float) 250.0, enemiesTrajectory, enemiesMissileView);
        
        Array<Missile> ms = new Array();
        ms.add(enemiesMissile);
        return ms;        
    }
}
