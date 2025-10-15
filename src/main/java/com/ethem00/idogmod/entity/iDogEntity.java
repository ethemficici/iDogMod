package com.ethem00.idogmod.entity;

import com.ethem00.idogmod.entity.ai.goal.iDogAttackWithOwnerGoal;
import com.ethem00.idogmod.entity.ai.goal.iDogBegGoal;
import com.ethem00.idogmod.entity.client.render.entity.animation.iDogEasing;
import com.ethem00.idogmod.iDogMod;
import com.google.common.annotations.VisibleForTesting;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.telemetry.WorldUnloadedEvent;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.EntityView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

public class iDogEntity extends TameableEntity implements Angerable, SingleStackInventory, iDogEasing {
    private static final TrackedData<Boolean> BEGGING = DataTracker.registerData(iDogEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_PLAYING = DataTracker.registerData(iDogEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<String> CURRENT_DISC = DataTracker.registerData(iDogEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Long> RECORD_END_TICK =DataTracker.registerData(iDogEntity.class, TrackedDataHandlerRegistry.LONG);
    private static final UniformIntProvider ANGER_TIME_RANGE = TimeHelper.betweenSeconds(20, 120);
    private boolean wasBegging = false;
    private int cumulativeTick = 0;
    private SoundEvent currentSong;
    private int angerTime;
    @Nullable
    private UUID angryAt;
    public static final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;
    private float begAnimationProgress;
    private float lastBegAnimationProgress;
    public static final Predicate<LivingEntity> FOLLOW_TAMED_PREDICATE = entity -> {
        EntityType<?> entityType = entity.getType();
        return entityType == EntityType.SHEEP || entityType == EntityType.RABBIT || entityType == EntityType.FOX;
    };

    //Jukebox
    private static final int SECOND_PER_TICK = 20;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private long tickCount;
    private long recordStartTick;
    private boolean loopSongs;
    private boolean alertMe;
    private boolean hasDisc;
    private float songVolume;

    // Eye Effects & Eye Covers
    private int eyeCover;
    private int currentBPM;
    private int startTime;
    private int elapsedTime;
    //
    private float eyeRedValue;
    private float eyeGreenValue;
    private float eyeBlueValue;
    private float eyeAlphaValue;
    private Color eyeRGBA = new Color (1F, 1F, 1F, 1F);

    public iDogEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(1, new iDogEntity.iDogEscapeDangerGoal(1.5));
        this.goalSelector.add(2, new SitGoal(this));
        this.goalSelector.add(4, new PounceAtTargetGoal(this, 0.4F));
        this.goalSelector.add(5, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(6, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F, false));
        this.goalSelector.add(7, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(8, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(9, new iDogBegGoal(this, 8.0F));
        this.goalSelector.add(10, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(10, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new iDogAttackWithOwnerGoal(this));
        this.targetSelector.add(3, new RevengeGoal(this).setGroupRevenge());
        this.targetSelector.add(4, new ActiveTargetGoal<>(
                this,
                PlayerEntity.class,
                10,
                true,
                false,
                entity -> this.shouldAngerAtTarget(entity)
        ));
        this.targetSelector.add(5, new UntamedActiveTargetGoal(this, AnimalEntity.class, false, FOLLOW_TAMED_PREDICATE));
        this.targetSelector.add(6, new UntamedActiveTargetGoal(this, TurtleEntity.class, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
        this.targetSelector.add(7, new ActiveTargetGoal(this, AbstractSkeletonEntity.class, false));
        this.targetSelector.add(8, new UniversalAngerGoal<>(this, true));

    }

    public static DefaultAttributeContainer.Builder createiDogAttributes(){
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35F)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 12.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0)
                .add(EntityAttributes.GENERIC_ARMOR, 1.0)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 1.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5);
    }

    //TODO: Fix issue where loading world doesn't display the correct eye type. Even if iDog has a disc in it's inventory.
    @Override
    protected void initDataTracker() {
        super.initDataTracker();

        this.dataTracker.startTracking(IS_PLAYING, false);
        this.dataTracker.startTracking(BEGGING, false);
        this.dataTracker.startTracking(CURRENT_DISC, "none");
        this.dataTracker.startTracking(RECORD_END_TICK, 0L);
    }

    @Override
    public void onRemoved() {
        super.onRemoved();
        this.dataTracker.set(IS_PLAYING, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isAlive()) {

            if(this.isBegging()) {
                this.cumulativeTick += 1;
                this.wasBegging = true;
            } else if(!this.isBegging() && this.wasBegging()) {
                this.cumulativeTick -= 1;
                if(this.cumulativeTick <= 0) {this.wasBegging = false; this.cumulativeTick = 0;}
            }

            /*
            if(this.isPlayingRecord() && this.songVolume != 0) {
                songDisplayLogic();
            } else {
                setEyeCover(0);
            }

             */
        }
    }

    public void songDisplayLogic() {
        //TODO:
        // Based on song BPM (if available), each beat corresponds with a pulse/ease.
        // At the end of every beat, choose a random pulse/ease.
        // Inside the pulse, interpolate RGB and Alpha. And also choose eye covers for when Alpha is 1F (maximum opacity).
        // .....................................................
        // If muted, don't use songDisplay(), instead use EyeRenderLayer without any covers.
        // .....................................................
        // First, test different interpolations
        // Primary colors will need to be mapped.
        // Music_Disc_Mellohi, Red 1F, Green 0F, Blue 1F; Music_Disc_13, Red 1F, Green 0.75F, Blue 0.25F
        // If disc isn't mapped, resort to default.
        // Using setEyeRGBA() method
        // .....................................................
        // 0 none covered, 1 center covered, 2 center uncovered, -1 inverted center cover
        // 3 -> 8 clockwise covering. 3,4,5 top; 6,7,8 bottom.
        // -
        // 9 top left & middle, 10, top right and middle.
        // 11 bottom right & middle, 12, bottom left and middle.
        // 13 top, 14, bottom.  15 top center, 16 bottom center


        //TODO: If music disc is 11 or (or 5 if there is time), do unique logic
        // Alternate from Pure Red to Interpolated Effects based on song sections.


        if(this.isSongFinished(this.getDisc())) {
            stopPlayingRecord(); //Ends any moving sound instances.
        }
    }

    public Color getEyeRGBA() {
        return this.eyeRGBA;
    }

    private void setEyeRGBA(String disc) {

        //TODO: Get RGB values for subsequent discs.
        this.eyeRGBA = switch (disc) {
            case "none" -> new Color (1F, 1F, 1F, 1F);
            case "music_disc_5" -> new Color (1F, 1F, 1F, 1F);
            case "music_disc_11" -> new Color (1F, 1F, 1F, 1F);
            case "music_disc_13" -> new Color (1F, 0.75F, 0.25F, 1F);
            case "music_disc_blocks" -> new Color (1F, 1F, 1F, 1F);
            case "music_disc_cat" -> new Color (1F, 1F, 1F, 1F);
            case "music_disc_chirp" -> new Color (1F, 1F, 1F, 1F);
            case "music_disc_far" -> new Color (1F, 1F, 1F, 1F);
            case "music_disc_mall" -> new Color (1F, 1F, 1F, 1F);
            case "music_disc_mellohi" -> new Color (1F, 0F, 1F, 1F);
            case "music_disc_otherside" -> new Color (1F, 1F, 1F, 1F);
            case "music_disc_pigstep" -> new Color (1F, 1F, 1F, 1F);
            case "music_disc_relic" -> new Color (1F, 1F, 1F, 1F);
            case "music_disc_stal" -> new Color (1F, 1F, 1F, 1F);
            case "music_disc_strad" -> new Color (1F, 1F, 1F, 1F);
            case "music_disc_wait" -> new Color (1F, 1F, 1F, 1F);
            case "music_disc_ward" -> new Color (1F, 1F, 1F, 1F);
            default -> new Color (1F, 1F, 1F, 1F);
        };
    }

    public int getEyeCover() {
        return this.eyeCover;
    }

    private void setEyeCover(int passedInt) {
        this.eyeCover = passedInt;
    }

    public int getCurrentBPM() {
        return this.currentBPM;
    }

    private void setCurrentBPM(String disc) {

        this.currentBPM = switch (disc) {
            case "none" -> 130;
            case "music_disc_5" -> 74;
            case "music_disc_11" -> 79;
            case "music_disc_13" -> 71;
            case "music_disc_blocks" -> 85;
            case "music_disc_cat" -> 112;
            case "music_disc_chirp" -> 110;
            case "music_disc_far" -> 130;
            case "music_disc_mall" -> 115;
            case "music_disc_mellohi" -> 91;
            case "music_disc_otherside" -> 92;
            case "music_disc_pigstep" -> 113;
            case "music_disc_relic" -> 136;
            case "music_disc_stal" -> 105;
            case "music_disc_strad" -> 188;
            case "music_disc_wait" -> 114;
            case "music_disc_ward" -> 107;
            default -> 130;
        };
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            Entity entity = source.getAttacker();
            if (!this.getWorld().isClient) {
                super.setSitting(false);
            }

            if (entity != null && !(entity instanceof PlayerEntity) && !(entity instanceof PersistentProjectileEntity)) {
                amount = (amount + 1.0F) / 2.0F;
            }

            return super.damage(source, amount);
        }
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean bl = target.damage(this.getDamageSources().mobAttack(this), (int)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE));
        if (bl) {
            this.applyDamageEffects(this, target);
        }

        return bl;
    }

    @Override
    public void setTamed(boolean tamed) {
        super.setTamed(tamed);
        if (tamed) {
            this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(30.0);
            this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(5.0);
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(5.0);
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).setBaseValue(2.0);
            this.setHealth(30.0F);
        } else {
            this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(12.0);
        }

        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(5.0);
    }

    public float getTailAngle() {
        if (this.hasAngerTime()) {
            return 1.5393804F;
        } else {
            return this.isTamed() ? (0.55F - (this.getMaxHealth() - this.getHealth()) * 0.02F) * (float) Math.PI : (float) (Math.PI / 5);
        }
    }

    @Override
    public void chooseRandomAngerTime() {
        this.setAngerTime(ANGER_TIME_RANGE.get(this.random));
    }

    public int getBegDelta() {
        return MathHelper.clamp(this.cumulativeTick, 0, 60);
    }

    public int getInverseBegDelta() {
        this.cumulativeTick -= 1;
        return MathHelper.clamp(this.cumulativeTick, 0, 60);
    }

    public boolean wasBegging() {
        return this.wasBegging;
    }

    public void setBegging(boolean begging) {
        this.dataTracker.set(BEGGING, begging);
    }

    public boolean isBegging() {
        return this.dataTracker.get(BEGGING);
    }

    @Override
    public void setAngerTime(int angerTime) {
        this.angerTime = angerTime;
    }

    @Override
    public int getAngerTime() {
        return this.angerTime;
    }

    @Override
    public void setAngryAt(@Nullable UUID angryAt) {
        this.angryAt = angryAt;
    }

    @Nullable
    @Override
    public UUID getAngryAt() {
        return this.angryAt;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();

        if (!this.getWorld().isClient) {
            this.tickAngerLogic((ServerWorld)this.getWorld(), true);
        }
    }

    //**!* Start of Jukebox *!**//
    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("RecordItem", NbtElement.COMPOUND_TYPE)) {
            this.inventory.set(0, ItemStack.fromNbt(nbt.getCompound("RecordItem")));
        }

        this.loopSongs = nbt.getBoolean("LoopSongs");
        this.alertMe = nbt.getBoolean("AlertMe");
        this.songVolume = nbt.getFloat("SongVolume");
        this.recordStartTick = nbt.getLong("RecordStartTick");
        this.tickCount = nbt.getLong("TickCount");

        if (nbt.contains("CurrentDisc")) {
            String disc = nbt.getString("CurrentDisc");
            this.setCurrentDisc(disc); // <---- force sync to clients
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (!this.getStack().isEmpty()) {
            nbt.put("RecordItem", this.getStack().writeNbt(new NbtCompound()));
        }

        //TODO: NBT based volume control, Music Looping, and Alerts for danger and treasure minecarts
        // On alert, volume for sound instance is lerped to 0 until the alert finishes, and then it is lerped back to its original volume.
        nbt.putBoolean("LoopSongs", this.loopSongs);
        nbt.putBoolean("AlertMe", this.alertMe);
        nbt.putFloat("SongVolume", this.songVolume);
        nbt.putLong("RecordStartTick", this.recordStartTick);
        nbt.putLong("TickCount", this.tickCount);
        nbt.putString("CurrentDisc", this.getCurrentDisc());
    }

    public String getCurrentDisc() {
        return this.dataTracker.get(CURRENT_DISC);
    }

    public void setCurrentDisc(String discName) {
        this.dataTracker.set(CURRENT_DISC, discName);
    }

    public void setLoopBool(boolean passedBool) {
        this.loopSongs = passedBool;
    }

    public boolean getLoopBool() {
        return this.loopSongs;
    }

    public void setSongVolume(int passedVolume) {
        this.songVolume = passedVolume/100F;
    }

    public float getSongVolume() {
        return this.songVolume;
    }

    public boolean isPlayingRecord() {
        return this.dataTracker.get(IS_PLAYING); //Previously: return !this.getStack().isEmpty() && this.isPlaying;
    }

    public void stopPlayingRecord() {
        this.dataTracker.set(IS_PLAYING, false);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return stack.isIn(ItemTags.MUSIC_DISCS) && this.getStack(slot).isEmpty();
    }

    @VisibleForTesting
    public void startPlaying(MusicDiscItem musicDisc) {
        this.dataTracker.set(IS_PLAYING, true);
        this.recordStartTick = this.tickCount;
        setSongEndTick(musicDisc); // Tells dataTracker the end tick.

        playSound(SoundEvents.ENTITY_ITEM_FRAME_ROTATE_ITEM, 2.0F, 0.25F);
        playSound(SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, 2.0F, 0.5F);

        setSongVolume(100);
        setLoopBool(true);

        if(!this.getWorld().isClient)
        {

            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(this.getId());
            buf.writeIdentifier(Registries.ITEM.getId(musicDisc));
            ServerWorld serverWorld = (ServerWorld) this.getWorld();
            serverWorld.getPlayers().forEach(player -> {
                ServerPlayNetworking.send(player, iDogMod.PLAY_IDOG_MUSIC, buf);
                System.out.println("Sound packet sent to Player: " + player + " from entity: " + this.getId() + " with disc ID of: " + Registries.ITEM.getId(musicDisc));
            });
        }
        currentSong = musicDisc.getSound();
        this.markDirty();
    }

    private void stopPlaying() {
        this.dataTracker.set(IS_PLAYING, false);
        playSound(SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, 2.0F, 2.0F);
        this.markDirty();
    }

    private boolean isSongFinished(MusicDiscItem musicDisc) {
        return this.tickCount >= this.recordStartTick + musicDisc.getSongLengthInTicks() + 20L;
    }

    private void setSongEndTick(MusicDiscItem musicDisc) {
        this.dataTracker.set(RECORD_END_TICK, this.recordStartTick + musicDisc.getSongLengthInTicks() + 20L);
    }

    public long getSongEndTick(MusicDiscItem musicDisc) {
        return this.dataTracker.get(RECORD_END_TICK);
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    public MusicDiscItem getDisc() {
        return ((MusicDiscItem) this.getStack().getItem()); //TODO: This is crashing! Can't cast AIR to disc!
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (stack.isIn(ItemTags.MUSIC_DISCS) && this.getWorld() != null) {
            this.inventory.set(slot, stack);
            setCurrentDisc(stack.getItem().toString());
            //System.out.println("iDog is now playing: " + getCurrentDisc());
            this.startPlaying((MusicDiscItem) stack.getItem());
        }
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        dropRecord();
        ItemStack itemStack = (ItemStack) Objects.requireNonNullElse(this.inventory.get(slot), ItemStack.EMPTY);
        this.inventory.set(slot, ItemStack.EMPTY);
        if (!itemStack.isEmpty()) {
            this.setCurrentDisc("none");
            this.stopPlaying();
        }

        return itemStack;
    }

    public void dropRecord() {
        ItemStack itemStack = this.getStack();
        if (this.hasCustomName()) {
            itemStack.setCustomName(this.getCustomName());
        }

        this.dropStack(itemStack);
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }
    //**!* End of Jukebox *!**//

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {

        //TODO tree
        // If not crouched:
        // If food and hurt, feed. else sit
        // If Music Disc and no disc, insert disc. If music disc and has disc, remove current disc
        // If random or empty, sit
        // If crouched, stop playing current disc and remove current disc.

        ItemStack itemStack = player.getStackInHand(hand);

            //TODO isTamed() seems to be lost after reconnect
            if (!this.isOwner(player) && !this.isTamed()) { // if player is not owner, it is not tamed,

                if (itemStack.isOf(Items.BONE) && !this.hasAngerTime()) {
                    if (this.random.nextInt(3) == 0) { // 25% chance to tame
                        this.setOwner(player);
                        this.navigation.stop();
                        if (!this.getWorld().isClient) {
                            super.setSitting(true);
                        }
                        this.setTarget(null);
                        this.getWorld().sendEntityStatus(this, (byte) 7); // heart particles
                        return ActionResult.success(this.getWorld().isClient);
                    } else {
                        this.getWorld().sendEntityStatus(this, (byte) 6); // smoke particles
                    }

                    if (!player.getAbilities().creativeMode) {
                        itemStack.decrement(1);
                    }

                    return ActionResult.CONSUME;
                }
            }

            if (this.isOwner(player) && this.isTamed()) {

                Boolean canHeal = (itemStack.isOf(Items.IRON_NUGGET) || itemStack.isOf(Items.IRON_INGOT) || itemStack.isOf(Items.COPPER_INGOT) && (this.getHealth() != this.getMaxHealth()));
                if (!canHeal && !itemStack.isIn(ItemTags.MUSIC_DISCS)) {

                    if (isPlayingRecord() && player.isSneaking()) //If the iDog is playing, and player is sneaking.
                    {
                        removeStack();
                        return ActionResult.success(this.getWorld().isClient);
                    } else {

                        ActionResult actionResult = super.interactMob(player, hand);
                        if ((!actionResult.isAccepted() || this.isBaby()) && this.isOwner(player)) {
                            if (!this.getWorld().isClient) {
                                super.setSitting(!isSitting());
                            }
                            this.jumping = false;
                            this.navigation.stop();
                            this.setTarget(null);
                            return ActionResult.SUCCESS;
                        }
                    }
                }

                if (!itemStack.isIn(ItemTags.MUSIC_DISCS)) {
                    float f = this.getHealth();
                    if (itemStack.isOf(Items.IRON_NUGGET) || itemStack.isOf(Items.COPPER_INGOT)) {
                        this.heal(2.5F);
                    }
                    if (itemStack.isOf(Items.IRON_INGOT)) {
                        this.heal(25.0F);
                    }
                    if (this.getHealth() == f) {
                        return ActionResult.PASS;
                    } else {
                        float g = 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;
                        this.playSound(SoundEvents.ENTITY_PLAYER_BURP, 1.0F, g);
                        this.playSound(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 0.5F, g);
                        if (!player.getAbilities().creativeMode) {
                            itemStack.decrement(1);
                        }

                        return ActionResult.success(this.getWorld().isClient);
                    }
                }

                if (itemStack.isIn(ItemTags.MUSIC_DISCS)) {

                    if (getCurrentDisc() != "none") {
                        removeStack();
                    } else {
                        setStack(0, itemStack);
                        if (!player.getAbilities().creativeMode) {
                            itemStack.decrement(1);
                        }
                    }
                    return ActionResult.success(this.getWorld().isClient);
                }
            }
            return ActionResult.PASS;
    }

    @Override
    public void setSitting(boolean sitting) {
        super.setSitting(sitting);
        this.setInSittingPose(sitting);
    }

    @Override
    public EntityView method_48926() {
        return this.getWorld();
    }

    class iDogEscapeDangerGoal extends EscapeDangerGoal {
        public iDogEscapeDangerGoal(double speed) {
            super(iDogEntity.this, speed);
        }
    }

    boolean shouldAngerAtTarget(LivingEntity entity) {
        if (!this.canTarget(entity)) {
            return false;
        } else {

            if(entity instanceof TameableEntity)
            {
                if(((TameableEntity) entity).getOwner() == this.getOwner()) {
                    return false;
                } else {
                    return entity.getType() == EntityType.PLAYER && this.isUniversallyAngry(entity.getWorld()) ? true : entity.getUuid().equals(this.getAngryAt());

                }
            } else {
                return entity.getType() == EntityType.PLAYER && this.isUniversallyAngry(entity.getWorld()) ? true : entity.getUuid().equals(this.getAngryAt());
            }
        }
    }

    @Override
    public @Nullable PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    @Override
    protected Entity.MoveEffect getMoveEffect() {
        return MoveEffect.SOUNDS;
    }

    @Override
    protected int getNextAirUnderwater(int air) {
        return air;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.hasAngerTime()) {
            return SoundEvents.ENTITY_WOLF_GROWL;
        } else if (this.random.nextInt(3) == 0) {
            return this.isTamed() && this.getHealth() < 10.0F ? SoundEvents.ENTITY_WOLF_WHINE : SoundEvents.ENTITY_WOLF_PANT;
        } else {
            return SoundEvents.ENTITY_WOLF_AMBIENT;
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_GUARDIAN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_GUARDIAN_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }


    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_IRON_GOLEM_STEP, 0.5F, 2.0F);
    }

    //Mob Alert Sounds
    //Replace with custom sound events
    protected void playAlertSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ITEM_FIRECHARGE_USE, 1F, 1.0F);
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.65F;
    }

    @Override
    public int getMaxLookPitchChange() {
        return this.isInSittingPose() ? 20 : super.getMaxLookPitchChange();
    }

    @Override
    public Vec3d getLeashOffset() {
        return new Vec3d(0.0, 0.675F * this.getStandingEyeHeight(), this.getWidth() * 0.4F);
    }

    //Animations

    @Override
    protected void updateLimbs(float posDelta) {
        float f;
        if (this.getPose() == EntityPose.STANDING) {
            f = Math.min(posDelta * 6.0F, 1.0F);
        } else {
            f = 0.0F;
        }

        this.limbAnimator.updateLimbs(f, 0.2F);
    }

    public float getBegAnimationProgress(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.lastBegAnimationProgress, this.begAnimationProgress) * 0.15F * (float) Math.PI;
    }
}
