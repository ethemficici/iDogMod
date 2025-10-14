package com.ethem00.idogmod.entity.ai.goal;

import com.ethem00.idogmod.entity.iDogEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class iDogBegGoal extends Goal {
    private final iDogEntity iDog;
    @Nullable
    private PlayerEntity begFrom;
    private final World world;
    private final float begDistance;
    private int timer;
    private final TargetPredicate validPlayerPredicate;

    public iDogBegGoal(iDogEntity iDog, float begDistance) {
        this.iDog = iDog;
        this.world = iDog.getWorld();
        this.begDistance = begDistance;
        this.validPlayerPredicate = TargetPredicate.createNonAttackable().setBaseMaxDistance(begDistance);
        this.setControls(EnumSet.of(Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        this.begFrom = this.world.getClosestPlayer(this.validPlayerPredicate, this.iDog);
        return this.begFrom == null ? false : this.isAttractive(this.begFrom);
    }

    @Override
    public boolean shouldContinue() {
        if (!this.begFrom.isAlive()) {
            return false;
        } else {
            return this.iDog.squaredDistanceTo(this.begFrom) > this.begDistance * this.begDistance ? false : this.timer > 0 && this.isAttractive(this.begFrom);
        }
    }

    @Override
    public void start() {
        this.iDog.setBegging(true);
        this.timer = this.getTickCount(40 + this.iDog.getRandom().nextInt(40));
    }

    @Override
    public void stop() {
        this.iDog.setBegging(false);
        this.begFrom = null;
    }

    @Override
    public void tick() {
        this.iDog.getLookControl().lookAt(this.begFrom.getX(), this.begFrom.getEyeY(), this.begFrom.getZ(), 10.0F, this.iDog.getMaxLookPitchChange());
        this.timer--;
    }

    private boolean isAttractive(PlayerEntity player) {
        for (Hand hand : Hand.values()) {
            ItemStack itemStack = player.getStackInHand(hand);
            if (!this.iDog.isTamed() && itemStack.isOf(Items.BONE)) {
                return true;
            }

            if (this.iDog.isTamed()) {
                if(itemStack.isOf(Items.BONE)) {return true;}
                if(this.iDog.getHealth() != this.iDog.getMaxHealth()) {
                    if(itemStack.isOf(Items.IRON_INGOT) || itemStack.isOf(Items.IRON_NUGGET) || itemStack.isOf(Items.COPPER_INGOT)) {return true;}
                }
            }

            if (this.iDog.isBreedingItem(itemStack)) {
                return true;
            }
        }

        return false;
    }
}