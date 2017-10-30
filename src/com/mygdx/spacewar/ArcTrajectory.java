/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.spacewar;

import com.badlogic.gdx.math.Rectangle;

/**
 * Дуговая траектория
 * @author Katie
 */
public class ArcTrajectory extends Trajectory {

    /**
     * Конструктор дуговой траектории
     * @param speed Скорость движения
     * @param leftDirected Является ли траектория направленной влево
     */
    public ArcTrajectory(float speed, boolean leftDirected) {
        super(speed, leftDirected);
    }

    /**
     * Вычислить новую позицию согласно траектории
     * @param cuPos Текущая позиция
     * @return Новая позиция
     */
    @Override
    public Rectangle calculatePosition(Rectangle cuPos, boolean toLeft, float deltaTime) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
