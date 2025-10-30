package com.ethem00.idogmod.entity.ai.goal;

import com.ethem00.idogmod.entity.iDogEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.vehicle.MinecartChest;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

public class iDogAlertsGoal extends Goal {
    private final iDogEntity iDog;
    @Nullable
    private LivingEntity threat;
    private MinecartChest treasure;
    private final float range;
    private int timer;
    private int cooldown;
    private boolean foundTreasure;
    private final TargetingConditions detectionPredicate;

    public iDogAlertsGoal(iDogEntity iDog, float range) {
        this.iDog = iDog;
        this.range = range;
        this.detectionPredicate = TargetingConditions
                .forCombat()
                .range(range)
                .ignoreLineOfSight(); // X-Ray
        this.setFlags(EnumSet.of(Flag.LOOK));
        this.cooldown = 400;
    }

    @Override
    public boolean canUse() {

        if(this.iDog.isAlerting()) {return false;}
        if (!this.iDog.getAlertBool()) {
            //System.out.println("no alerts");
            return false;}
        if (!(this.iDog.getAge() % 100 <= 5)) {
            //System.out.println(this.iDog.age % 100 + " is not modulo five seconds");
            return false;} // Scan every 5 seconds


        this.treasure = findChestMinecart();
        this.threat = findThreat();
        if(this.treasure != null) { foundTreasure = true;  return true; }
        else if (this.threat != null) { foundTreasure = false;  return true; }
        else {; return false;}
    }

    @Override
    public boolean canContinueToUse() {
        if(this.treasure != null && this.treasure.isAlive() && this.iDog.distanceTo(treasure) <= range) { return true; }
        else if (this.threat != null && this.threat.isAlive() && this.iDog.distanceTo(threat) <= range) { return true; }
        else {return false;}
    }

    @Override
    public void tick() {

        //TODO: WHEN A MINECART IS REMOVED,
        // THE IDOG CONTINUES STARING IN THE PLACE IT WAS.
        // IT WILL ALSO ACTIVATE SOUNDS EVEN THOUGH IT NO LONGER EXISTS.
        // FIX THIS
        // .isAlive() PROBABLY DOESNT WORK WITH MINECARTS!
        if(this.foundTreasure) {
            this.iDog.getLookControl().setLookAt(treasure, 30.0F, 30.0F);
        } else {
            this.iDog.getLookControl().setLookAt(threat, 30.0F, 30.0F);
        }

        if (this.cooldown <= 0 && !this.iDog.isAlerting() && this.iDog.getAlertBool()) {
            this.iDog.setAlerting(true);
            this.iDog.playAlertSounds(switchThreatByType());

            if(this.iDog.isOrderedToSit()){
                this.cooldown = 400;
            } else {
                this.cooldown = 200;
            }
        } else {
            if(!this.iDog.isAlerting() && this.iDog.getAlertBool()) {
                this.cooldown--;
                //System.out.println(this.cooldown);
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
        List<Monster> nearby = this.iDog.level()
                .getEntitiesOfClass(Monster.class,
                        this.iDog.getBoundingBox().inflate(range),
                        entity -> entity.isAlive() && !entity.isRemoved());
        if (!nearby.isEmpty()) {
            return nearby.get(0);
        }
        return null;
    }

    private MinecartChest findChestMinecart() {
        // Search for chests
        List<MinecartChest> nearby = this.iDog.level()
                .getEntitiesOfClass(MinecartChest.class,
                        this.iDog.getBoundingBox().inflate(range),
                        entity -> entity.isAlive() && !entity.isRemoved());
        if (!nearby.isEmpty()) {
            return nearby.get(0);
        }
        return null;
    }

    private int switchThreatByType() {
        Entity entity;
        if(this.foundTreasure) {entity = this.treasure;} else {entity = this.threat;}

        System.out.println("Entity is: " + entity.getType().toShortString());

        return switch (entity.getType().toShortString()) {
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