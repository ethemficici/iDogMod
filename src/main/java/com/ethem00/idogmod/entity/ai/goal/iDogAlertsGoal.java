package com.ethem00.idogmod.entity.ai.goal;

import com.ethem00.idogmod.entity.iDogEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

public class iDogAlertsGoal extends Goal {
    private final iDogEntity iDog;
    @Nullable
    private LivingEntity threat;
    private ChestMinecartEntity treasure;
    private final float range;
    private int timer;
    private int cooldown;
    private boolean foundTreasure;
    private final TargetPredicate detectionPredicate;

    public iDogAlertsGoal(iDogEntity iDog, float range) {
        this.iDog = iDog;
        this.range = range;
        this.detectionPredicate = TargetPredicate
                .createAttackable()
                .setBaseMaxDistance(range)
                .ignoreVisibility(); // X-Ray
        this.setControls(EnumSet.of(Control.LOOK));
        this.cooldown = 400;
    }

    @Override
    public boolean canStart() {

        if(this.iDog.isAlerting()) {return false;}
        if (!this.iDog.getAlertBool()) {
            //System.out.println("no alerts");
            return false;}
        if (!(this.iDog.age % 100 <= 5)) {
            //System.out.println(this.iDog.age % 100 + " is not modulo five seconds");
            return false;} // Scan every 5 seconds


        this.treasure = findChestMinecart();
        this.threat = findThreat();
        if(this.treasure != null) { foundTreasure = true; System.out.println("Found treasure"); return true; }
        else if (this.threat != null) { foundTreasure = false; System.out.println("Found threat"); return true; }
        else {System.out.println("Found nothing"); return false;}
    }

    @Override
    public boolean shouldContinue() {
        if(this.treasure != null && this.treasure.isAlive() && this.iDog.distanceTo(treasure) <= range) { return true; }
        else if (this.threat != null && this.threat.isAlive() && this.iDog.distanceTo(threat) <= range) { return true; }
        else {return false;}
    }

    @Override
    public void tick() {

        if(this.foundTreasure) {
            this.iDog.getLookControl().lookAt(treasure, 30.0F, 30.0F);
        } else {
            this.iDog.getLookControl().lookAt(threat, 30.0F, 30.0F);
        }

        if (this.cooldown <= 0 && !this.iDog.isAlerting() && this.iDog.getAlertBool()) {
            this.iDog.setAlerting(true);
            this.iDog.playAlertSounds(switchThreatByType());
            this.cooldown = 400; // play every minute
        } else {
            if(!this.iDog.isAlerting() && this.iDog.getAlertBool()) {
                this.cooldown--;
                System.out.println(this.cooldown);
            }
        }
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        this.threat = null;
    }

    private LivingEntity findThreat() {
        // Search by class/tag (e.g., Hostile mobs)
        List<HostileEntity> nearby = this.iDog.getWorld()
                .getEntitiesByClass(HostileEntity.class,
                        this.iDog.getBoundingBox().expand(range),
                        entity -> entity.isAlive() && !entity.isRemoved());
        if (!nearby.isEmpty()) {
            return nearby.get(0);
        }
        return null;
    }

    private ChestMinecartEntity findChestMinecart() {
        // Search for chests
        List<ChestMinecartEntity> nearby = this.iDog.getWorld()
                .getEntitiesByClass(ChestMinecartEntity.class,
                        this.iDog.getBoundingBox().expand(range),
                        entity -> entity.isAlive() && !entity.isRemoved());
        if (!nearby.isEmpty()) {
            return nearby.get(0);
        }
        return null;
    }

    private int switchThreatByType() {
        Entity entity;
        if(this.foundTreasure) {entity = this.treasure;} else {entity = this.threat;}

        System.out.println("Entity is: " + entity.getType().getUntranslatedName());

        return switch (entity.getType().getUntranslatedName()) {
            case "chest_minecart" -> -1;
            case "zombie" ->  0;
            case "husk" ->  0;
            case "drowned" ->  0;
            case "skeleton" ->  1;
            case "stray" ->  1;
            case "spider" ->  2;
            case "cave_spider" ->  2;
            case "creeper" ->  3;
            case "enderman" ->  4;
            default -> -10;
        };
    }
}