/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.spacewar;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import static com.mygdx.spacewar.ObjectImage.ObjectType.ENMMISSILE;
import static com.mygdx.spacewar.ObjectImage.ObjectType.ENMSHIP;
import static com.mygdx.spacewar.ObjectImage.ObjectType.ENMSHIPFAST;
import static com.mygdx.spacewar.ObjectImage.ObjectType.ENMSHIPHEALTHY;

/**
 * Строитель врагов
 * @author Katie
 */
public class EnemyBuilder {
    private Array<ObjectImage> enemiesSprites;          /// Спрайты врагов
    private EnemyMissileBuilder missileBuilder;         /// Строитель снарядов
    
    public EnemyBuilder (){
        missileBuilder = new EnemyMissileBuilder();
        enemiesSprites = new Array();
        
        ObjectImage enemiesImg = null;        
        enemiesImg = new ObjectImage("enemies/enemy1.png", ENMSHIP);
        enemiesSprites.add(enemiesImg);
        enemiesImg = new ObjectImage("enemies/enemy2.png", ENMSHIPHEALTHY);
        enemiesSprites.add(enemiesImg);
        enemiesImg = new ObjectImage("enemies/enemy3.png", ENMSHIPFAST);
        enemiesSprites.add(enemiesImg);
    }
    
    /**
     *
     * @param gameLevel
     * @return
     */
    public Array<EnemyShip> generateEnemies(int gameLevel){
        // Определяем тип создаваемого игрока, основываясь на сложности игры
        int enemyIndex = MathUtils.random(1, gameLevel);        
        // Если первый тип, то создаем легкого врага
        if(enemyIndex==1){
            return generateEasyEnemies(1);
        }
        // Иначе если второй тип, то создаем врага с большим хп
        else if(enemyIndex==2){
            return generateHealthyEnemies(1);//generateHealthyEnemy();
        }
        // Иначе если третий тип, то создаем врага, стреляющего по дуге
        else if(enemyIndex==3){
            return generateFastEnemies(1);//generateFastEnemy();
        }            
        return null;
    }
    
    /**
     * Создать легких врагов
     * @param count количество врагов для генерации
     * @return Враги
     */
    private Array<EnemyShip> generateEasyEnemies(int count){
        if (count<=0)
            return null;
        Array<EnemyShip> enms = new Array();
        while(enms.size<count){
            // Создаем отображение врага
            ObjectSprite enemiesView = null;
            // Получаем изображение врага
            ObjectImage enemiesImg = enemiesSprites.first();        
            // Создаем спрайт врага
            enemiesView = new ObjectSprite(enemiesImg, 29, 38, ++(GameSystem.curId));

            // Повторяем то же самое с снарядом врага
            Array<Missile> enemyMissiles = missileBuilder.generateEnemyMissiles(ENMMISSILE);
            // Создаем вражеский корабль
            Array<Weapon> wps = new Array<Weapon>();
            for (Missile m: enemyMissiles){
                wps.add(new Weapon(m));
            }        
            EnemyShip enemy = new EnemyShip(1, (float) 200.0, enemiesView, wps);
            enms.add(enemy);
        }
        // Возвращаем созданных врагов
        return enms;
    }
    
    /**
     * Создать врагов с большим здоровьем
     * @param count количество врагов для генерации
     * @return Враги
     */
    private Array<EnemyShip> generateHealthyEnemies(int count){
        // Заглушка
        return null;
    }
    
    /**
     * Создать врагов стреляющих по дуге
     * @param count количество врагов для генерации
     * @return Враги
     */
    private Array<EnemyShip> generateFastEnemies(int count){
        // Заглушка
        return null;
    }
}
