/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.spacewar;

/**
 * Корабль
 * @author Katie
 */
public abstract class Ship {
    protected int health;       /// Здоровье корабля
    private float speed;        /// Скорость корабля
    private ObjectSprite view;  /// Отображение корабля
    private Weapon weapon;      /// Оружие корабля
    
    /**
     * Конструктор корабля
     * @param health Здоровье корабля
     * @param speed Скорость корабля
     * @param view Отображение корабля
     * @param weapon Оружие корабля
     */
    public Ship(int health, float speed, ObjectSprite view, Weapon weapon){
        if(health<=0 || speed <=0.0 || view == null || weapon == null)
            throw new Error("Can't create ship");
        this.health = health;
        this.speed = speed;
        this.view = view;
        this.weapon = weapon;
    }
    
    /**
     * Получить отображение корабля
     * @return Отображение корабля
     */
    public ObjectSprite getView(){
        // Заглушка
        return this.view;
    }
    
    /**
     * Получить снаряд корабля
     * @return Снаряд корабля
     */
    public Missile getMissile(){
        // Заглушка
        return this.weapon.getMissile();
    }
    
    /**
     * Получить здоровье корабля
     * @return Здоровье корабля
     */
    public int getHealth(){
        // Заглушка
        return this.health;
    }
    
    /**
     * Получить скорость корабля
     * @return Скорость корабля
     */
    public float getSpeed(){
        return this.speed;
    }
    
    /**
     * Получить повреждения
     * @param damage урон, нанесенный при повреждении
     */
    public void takeDamage(int damage){
        if (damage<0)
            throw new Error("Ship can't take negative damage");
        this.health-=damage;
    }
}
