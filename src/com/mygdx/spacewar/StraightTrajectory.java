/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.spacewar;

import com.badlogic.gdx.math.Rectangle;

/**
 * Прямая траектория
 * @author Katie
 */
public class StraightTrajectory extends Trajectory{

    /**
     * Конструктор прямой траектории
     * @param speed Скорость
     * @param leftDirected Является ли направленной влево
     */
    public StraightTrajectory(float speed, boolean leftDirected) {
        super(speed, leftDirected);
    }

    /**
     * Вычислить новую позицию
     * @param cuPos Текущая позиция
     * @return Новая позиция
     */
    @Override
    public Rectangle calculatePosition(Rectangle cuPos) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
