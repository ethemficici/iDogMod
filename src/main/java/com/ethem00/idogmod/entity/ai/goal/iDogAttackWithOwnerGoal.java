package com.ethem00.idogmod.entity.ai.goal;



import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;

public class iDogAttackWithOwnerGoal extends TargetGoal {
    private final TamableAnimal tameable;
    private LivingEntity attacking;
    private int lastAttackTime;

    public iDogAttackWithOwnerGoal(TamableAnimal tameable) {
        super(tameable, false);
        this.tameable = tameable;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (this.tameable.isTame() && !this.tameable.isOrderedToSit()) {
            LivingEntity livingEntity = this.tameable.getOwner();
            if (livingEntity == null) {
                return false;
            } else {
                this.attacking = livingEntity.getLastHurtMob();

                if(isCommonOwner()) {return false;}

                int i = livingEntity.getLastHurtMobTimestamp();
                return i != this.lastAttackTime && this.canAttack(this.attacking, TargetingConditions.DEFAULT) && this.tameable.wantsToAttack(this.attacking, livingEntity);
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
            this.lastAttackTime = livingEntity.getLastHurtMobTimestamp();
        }

        super.start();
    }

    public boolean isCommonOwner() {

        if(this.attacking instanceof TamableAnimal) {
            if(((TamableAnimal) this.attacking).getOwner() == tameable.getOwner() ) {
                return true;
            } else {return false;}
        } else {
            return false;
        }
    }
}
