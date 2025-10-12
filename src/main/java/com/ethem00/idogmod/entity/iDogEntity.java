package com.ethem00.idogmod.entity;

import com.ethem00.idogmod.entity.ai.goal.iDogBegGoal;
import com.google.common.annotations.VisibleForTesting;
import net.minecraft.block.BlockState;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
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
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

public class iDogEntity extends TameableEntity implements Angerable, SingleStackInventory {
    private static final TrackedData<Boolean> BEGGING = DataTracker.registerData(iDogEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<String> CURRENT_DISC = DataTracker.registerData(iDogEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final UniformIntProvider ANGER_TIME_RANGE = TimeHelper.betweenSeconds(20, 120);
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
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
    private int ticksThisSecond;
    private long tickCount;
    private long recordStartTick;
    private boolean isPlaying;

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
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, new RevengeGoal(this).setGroupRevenge());
        this.targetSelector.add(4, new ActiveTargetGoal<>(
                this,
                PlayerEntity.class,
                10,
                true,
                false,
                entity -> this.shouldAngerAt(entity)
        ));
        this.targetSelector.add(5, new UntamedActiveTargetGoal(this, AnimalEntity.class, false, FOLLOW_TAMED_PREDICATE));
        this.targetSelector.add(6, new UntamedActiveTargetGoal(this, TurtleEntity.class, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
        this.targetSelector.add(7, new ActiveTargetGoal(this, AbstractSkeletonEntity.class, false));
        this.targetSelector.add(8, new UniversalAngerGoal<>(this, true));

    }

    public static DefaultAttributeContainer.Builder createiDogAttributes(){
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35F)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 25.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0)
                .add(EntityAttributes.GENERIC_ARMOR, 5.0)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 1.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(BEGGING, false);
        this.dataTracker.startTracking(CURRENT_DISC, "none");
    }

    public String getCurrentDisc() {
        return this.dataTracker.get(CURRENT_DISC);
    }

    public void setCurrentDisc(String discName) {
        this.dataTracker.set(CURRENT_DISC, discName);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isAlive()) {
            this.lastBegAnimationProgress = this.begAnimationProgress;
            if (this.isBegging()) {
                this.begAnimationProgress = this.begAnimationProgress + (1.0F - this.begAnimationProgress) * 0.4F;
            } else {
                this.begAnimationProgress = this.begAnimationProgress + (0.0F - this.begAnimationProgress) * 0.4F;
            }
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            Entity entity = source.getAttacker();
            if (!this.getWorld().isClient) {
                this.setSitting(false);
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
            this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(20.0);
            this.setHealth(20.0F);
        } else {
            this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(8.0);
        }

        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(4.0);
    }

    public float getTailAngle() {
        if (this.hasAngerTime()) {
            return 1.5393804F;
        } else {
            return this.isTamed() ? (0.55F - (this.getMaxHealth() - this.getHealth()) * 0.02F) * (float) Math.PI : (float) (Math.PI / 5);
        }
    }

    @Override
    public EntityView method_48926() {
        return null;
    }

    @Override
    public void chooseRandomAngerTime() {
        this.setAngerTime(ANGER_TIME_RANGE.get(this.random));
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


    //**!* Start of Jukebox *!**//
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("RecordItem", NbtElement.COMPOUND_TYPE)) {
            this.inventory.set(0, ItemStack.fromNbt(nbt.getCompound("RecordItem")));
        }

        this.isPlaying = nbt.getBoolean("IsPlaying");
        this.recordStartTick = nbt.getLong("RecordStartTick");
        this.tickCount = nbt.getLong("TickCount");
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (!this.getStack().isEmpty()) {
            nbt.put("RecordItem", this.getStack().writeNbt(new NbtCompound()));
        }

        nbt.putBoolean("IsPlaying", this.isPlaying);
        nbt.putLong("RecordStartTick", this.recordStartTick);
        nbt.putLong("TickCount", this.tickCount);
        return nbt;
    }

    public boolean isPlayingRecord() {
        return !this.getStack().isEmpty() && this.isPlaying;
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return stack.isIn(ItemTags.MUSIC_DISCS) && this.getStack(slot).isEmpty();
    }

    @VisibleForTesting
    public void startPlaying() {
        this.recordStartTick = this.tickCount;
        this.isPlaying = true;
        this.getWorld().syncWorldEvent(null, WorldEvents.JUKEBOX_STARTS_PLAYING, BlockPos.ofFloored(this.getPos()), Item.getRawId(this.getStack().getItem()));
        this.markDirty();
    }

    private void stopPlaying() {
        this.isPlaying = false;
        this.getWorld().emitGameEvent(GameEvent.JUKEBOX_STOP_PLAY, this.getPos(), null);
        this.getWorld().syncWorldEvent(WorldEvents.JUKEBOX_STOPS_PLAYING, BlockPos.ofFloored(this.getPos()), 0);
        this.markDirty();
    }


    private boolean isSongFinished(MusicDiscItem musicDisc) {
        return this.tickCount >= this.recordStartTick + musicDisc.getSongLengthInTicks() + 20L;
    }

    private boolean hasSecondPassed() {
        return this.ticksThisSecond >= 20;
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (stack.isIn(ItemTags.MUSIC_DISCS) && this.getWorld() != null) {
            this.inventory.set(slot, stack);
            setCurrentDisc(stack.getName().toString());
            this.startPlaying();
        }
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack itemStack = (ItemStack) Objects.requireNonNullElse(this.inventory.get(slot), ItemStack.EMPTY);
        this.inventory.set(slot, ItemStack.EMPTY);
        if (!itemStack.isEmpty()) {
            this.stopPlaying();
        }

        return itemStack;
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }
    //**!* End of Jukebox *!**//

    public ActionResult interactMob(PlayerEntity player, Hand hand) {

        //TODO tree
        // If not crouched:
        // If food and hurt, feed. else sit
        // If Music Disc and no disc, insert disc. If music disc and has disc, remove current disc
        // If random or empty, sit
        // If crouched, stop playing current disc and remove current disc.

        ItemStack itemStack = player.getStackInHand(hand);
        if (!itemStack.isOf(Items.IRON_NUGGET) || !itemStack.isOf(Items.IRON_INGOT) || !itemStack.isOf(Items.COPPER_INGOT) || !itemStack.isIn(ItemTags.MUSIC_DISCS)) {

            if(isPlayingRecord() && player.isSneaking())
            {
                removeStack();
            } else {
                setSitting(true);
            }

            return ActionResult.PASS;
        } else {

            if(!itemStack.isIn(ItemTags.MUSIC_DISCS))
            {
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
                    this.playSound(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 1.0F, g);
                    if (!player.getAbilities().creativeMode) {
                        itemStack.decrement(1);
                    }

                    return ActionResult.success(this.getWorld().isClient);
                }
            } else {

                if(isPlayingRecord())
                {
                    removeStack();
                } else {
                    setStack(1, itemStack);
                }

                return ActionResult.success(this.getWorld().isClient);
            }
        }
    }

    class iDogEscapeDangerGoal extends EscapeDangerGoal {
        public iDogEscapeDangerGoal(double speed) {
            super(iDogEntity.this, speed);
        }
    }

    @Override
    public @Nullable PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
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
