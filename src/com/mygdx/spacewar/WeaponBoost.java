/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.spacewar;

/**
 *
 * @author Katie
 */
public class WeaponBoost extends Bonus{

    private final int activeWeapons;  /// Количество орудий для установления активными
    private int oldShipWeaponsCount;  /// Начальное количество активных орудий на корабле

    public WeaponBoost(BonusType type, ObjectSprite sprite, StraightTrajectory traj) {
        super(type, sprite, traj);
        this.activeWeapons = 3;
    }
    
    @Override
    public void activate(Ship shipToTakeBonus) {
        oldShipWeaponsCount = shipToTakeBonus.getDefaultWeaponsCount();
        shipToTakeBonus.setActiveWeaponsCount(activeWeapons);
    }
    
    public void deactivate(Ship shipToTakeBonus) {
        shipToTakeBonus.setActiveWeaponsCount(oldShipWeaponsCount);
    }
}
