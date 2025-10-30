package com.ethem00.idogmod.entity.ai.goal;

import com.ethem00.idogmod.entity.iDogEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class iDogBegGoal extends Goal {
    private final iDogEntity iDog;
    @Nullable
    private Player player;
    private Level world;
    private final float begDistance;
    private int timer;
    private final TargetingConditions validPlayerPredicate;

    public iDogBegGoal(iDogEntity iDog, float begDistance) {
        this.iDog = iDog;
        this.world = iDog.level();
        this.begDistance = begDistance;
        this.validPlayerPredicate = TargetingConditions.forNonCombat().range(begDistance);
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        this.player = this.world.getNearestPlayer(this.validPlayerPredicate, this.iDog);
        return this.player == null ? false : this.isAttractive(this.player);
    }

    @Override
    public boolean shouldContinue() {
        if (!this.player.isAlive()) {
            return false;
        } else {
            return this.iDog.squaredDistanceTo(this.player) > this.begDistance * this.begDistance ? false : this.timer > 0 && this.isAttractive(this.player);
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
        this.player = null;
    }

    @Override
    public void tick() {
        this.iDog.getLookControl().lookAt(this.player.getX(), this.player.getEyeY(), this.player.getZ(), 10.0F, this.iDog.getMaxLookPitchChange());
        this.timer--;
    }

    private boolean isAttractive(Player player) {
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack itemStack = player.getItemInHand(hand);
            if (!this.iDog.isTamed() && itemStack.isOf(Items.BONE)) {
                return true;
            }

            if (this.iDog.isTame()) {
                if(itemStack.is(Items.BONE)) {return true;}
                if(this.iDog.getHealth() != this.iDog.getMaxHealth()) {
                    if(itemStack.is(Items.IRON_INGOT) || itemStack.is(Items.IRON_NUGGET) || itemStack.is(Items.COPPER_INGOT)) {return true;}
                }
            }

            if (this.iDog.isFood(itemStack)) {
                return true;
            }
        }

        return false;
    }
}