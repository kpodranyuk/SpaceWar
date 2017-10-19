/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.spacewar;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.spacewar.ObjectSprite.ObjectType;
import static com.mygdx.spacewar.ObjectSprite.ObjectType.ENMSHIP;
import static com.mygdx.spacewar.ObjectSprite.ObjectType.MISSILE;
import static com.mygdx.spacewar.ObjectSprite.ObjectType.USRSHIP;
import java.util.ArrayList;

/**
 * Игровая система, управляющая процессом игры
 * @author Katie
 */
public class GameSystem {
    private PlayerShip player;              // Игрок
    private ArrayList<EnemyShip> enemies;   // Враги
    
    /**
     * Конструктор
     */
    public GameSystem(){
        ObjectSprite playersView = new ObjectSprite("ship.png", 34, 37, USRSHIP);
        ObjectSprite playersMissileView = new ObjectSprite("fire/greenpng.png", 22, 11, MISSILE);
        
        ObjectSprite enemiesView = new ObjectSprite("enemies/enemy1.png", 29, 38, ENMSHIP);
        ObjectSprite enemiesMissileView = new ObjectSprite("fire/redpng.png", 22, 11, MISSILE);
        
        StraightTrajectory playersTrajectory = new StraightTrajectory((float) 170.0, false);
        StraightTrajectory enemiesTrajectory = new StraightTrajectory((float) 200.0, true);
        double speed = 150.0;
        Missile playersMissile = new Missile(1, (float) 170.0, playersTrajectory, playersMissileView);
        Missile enemiesMissile = new Missile(1, (float) 200.0, enemiesTrajectory, enemiesMissileView);
        
        player = new PlayerShip(1, (float) speed, playersView, new Weapon(playersMissile));
        enemies = new ArrayList();
        EnemyShip enemy = new EnemyShip(1, (float) 200.0, enemiesView, new Weapon(enemiesMissile));
        enemies.add(enemy);
    }
    
    /**
     * Создать врага
     * @return Отображение врага
     */
    public ObjectSprite generateEnemy(){
        int enemyIndex = MathUtils.random(0, enemies.size()-1);
        return new ObjectSprite(enemies.get(enemyIndex).getView());
    }
    
    public ObjectSprite getEnemiesMissile(ObjectType type){
        if (type == ENMSHIP)
            return this.enemies.get(0).getMissile().getView();
        return null;
    }
    
    /**
     * Получить корабль игрока
     * @return
     */
    public PlayerShip getPlayer(){
        return this.player;
    }
    
    /**
     * Получить корабль по его типу
     * TODO Доделать метод
     * @param type Тип корабля
     * @return Корабль с данным типом
     */
    public Ship getShip(ObjectType type){
        if (type == USRSHIP)
            return this.player;
        if (type == ENMSHIP)
            return this.enemies.get(0);
        return null;
    }
}
