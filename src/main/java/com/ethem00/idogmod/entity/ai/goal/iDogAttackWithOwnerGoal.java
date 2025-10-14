package com.ethem00.idogmod.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.passive.TameableEntity;

import java.util.EnumSet;

public class iDogAttackWithOwnerGoal extends TrackTargetGoal {
    private final TameableEntity tameable;
    private LivingEntity attacking;
    private int lastAttackTime;

    public iDogAttackWithOwnerGoal(TameableEntity tameable) {
        super(tameable, false);
        this.tameable = tameable;
        this.setControls(EnumSet.of(Goal.Control.TARGET));
    }

    @Override
    public boolean canStart() {
        if (this.tameable.isTamed() && !this.tameable.isSitting()) {
            LivingEntity livingEntity = this.tameable.getOwner();
            if (livingEntity == null) {
                return false;
            } else {
                this.attacking = livingEntity.getAttacking();

                if(isCommonOwner()) {return false;}

                int i = livingEntity.getLastAttackTime();
                return i != this.lastAttackTime && this.canTrack(this.attacking, TargetPredicate.DEFAULT) && this.tameable.canAttackWithOwner(this.attacking, livingEntity);
            }
        } else {
            return false;
        }
    }

    @Override
    public void start() {
        this.mob.setTarget(this.attacking);
        LivingEntity livingEntity = this.tameable.getOwner();
        if (livingEntity != null) {
            this.lastAttackTime = livingEntity.getLastAttackTime();
        }

        super.start();
    }

    public boolean isCommonOwner() {

        if(this.attacking instanceof TameableEntity) {
            if(((TameableEntity) this.attacking).getOwner() == tameable.getOwner() ) {
                return true;
            } else {return false;}
        } else {
            return false;
        }
    }
}
