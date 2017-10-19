/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.spacewar;

/**
 * Вражеский корабль
 * @author Katie
 */
public class EnemyShip extends Ship{
    
    /**
     * Конструктор вражеского корабля
     * @param health Здоровье корабля
     * @param speed Скорость корабля
     * @param view Отображение корабля
     * @param weapon Оружие корабля
     */
    public EnemyShip(int health, float speed, ObjectSprite view, Weapon weapon) {
        super(health, speed, view, weapon);
    }
    
}
