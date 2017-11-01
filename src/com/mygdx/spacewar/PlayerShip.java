/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.spacewar;

/**
 * Пользовательский корабль
 * @author Katie
 */
public class PlayerShip extends Ship {

    private int points;
    /**
     * Конструктор пользовательского корабля
     * @param health Здоровье корабля
     * @param speed Скорость корабля
     * @param view Отображение корабля
     * @param weapon Оружие корабля
     */
    public PlayerShip(int health, float speed, ObjectSprite view, Weapon weapon) {
        super(health, speed, view, weapon);
        points = 0;
    }

    /**
     * Совершить выстрел
     * @return Снаряд, которым выстрелили
     */
    public Missile shoot(){
        return this.getMissile();
    }
    
    public int getPoints(){
        return this.points;
    }
    
    public void increasePoints(int earnedPoints) {
        this.points+=earnedPoints;
    }
}
