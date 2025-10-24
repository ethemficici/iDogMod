package com.ethem00.idogmod.entity;

import com.ethem00.idogmod.entity.ai.goal.iDogAlertsGoal;
import com.ethem00.idogmod.entity.ai.goal.iDogAttackWithOwnerGoal;
import com.ethem00.idogmod.entity.ai.goal.iDogBegGoal;
import com.ethem00.idogmod.entity.client.render.entity.animation.iDogEasing;
import com.ethem00.idogmod.entity.client.render.entity.animation.iDogEyeVariants;
import com.ethem00.idogmod.iDogMod;
import com.ethem00.idogmod.screen.iDogScreenHandler;
import com.ethem00.idogmod.sound.ModSounds;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
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
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
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

public class iDogEntity extends TameableEntity implements Angerable, SingleStackInventory, iDogEasing, iDogEyeVariants, ExtendedScreenHandlerFactory {
    private static final TrackedData<Boolean> BEGGING = DataTracker.registerData(iDogEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> ALERTING = DataTracker.registerData(iDogEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_PLAYING = DataTracker.registerData(iDogEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<String> CURRENT_DISC = DataTracker.registerData(iDogEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<ItemStack> DISC_ITEMSTACK = DataTracker.registerData(iDogEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<Integer> EYE_VARIANT = DataTracker.registerData(iDogEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Long> SONG_END_TICK = DataTracker.registerData(iDogEntity.class, TrackedDataHandlerRegistry.LONG);
    private static final UniformIntProvider ANGER_TIME_RANGE = TimeHelper.betweenSeconds(20, 120);
    private boolean wasBegging = false;
    private int cumulativeBegTick = 0;
    private SoundEvent currentSong;
    private int angerTime;
    @Nullable
    private UUID angryAt;
    //public static final AnimationState idleAnimationState = new AnimationState();
    public static final Predicate<LivingEntity> FOLLOW_TAMED_PREDICATE = entity -> {
        EntityType<?> entityType = entity.getType();
        return entityType == EntityType.SHEEP || entityType == EntityType.RABBIT || entityType == EntityType.FOX;
    };

    //Jukebox + Song Logic
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private int ticksThisSecond;
    private long tickCount; //Cumulative for iDog lifespan.
    private static final TrackedData<Long> START_TICK = DataTracker.registerData(iDogEntity.class, TrackedDataHandlerRegistry.LONG);
    private static final TrackedData<Boolean> LOOP_BOOLEAN = DataTracker.registerData(iDogEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> ALERT_BOOLEAN = DataTracker.registerData(iDogEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Float> SONG_VOLUME = DataTracker.registerData(iDogEntity.class, TrackedDataHandlerRegistry.FLOAT);


    // Eye Effects & Eye Covers
    private static final TrackedData<Integer> EYE_COVER = DataTracker.registerData(iDogEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> CURRENT_BPM = DataTracker.registerData(iDogEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> TICKS_PER_BEAT_CUMULATIVE = DataTracker.registerData(iDogEntity.class, TrackedDataHandlerRegistry.INTEGER); // Ticks inside a beat
    private static final TrackedData<Float> TICKS_PER_BEAT = DataTracker.registerData(iDogEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Integer> ANIMATION_SET = DataTracker.registerData(iDogEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> ANIMATION_STEP = DataTracker.registerData(iDogEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> ANIMATION_STEP_COUNT = DataTracker.registerData(iDogEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> BEAT_CUMULATIVE = DataTracker.registerData(iDogEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> EASE_METHOD = DataTracker.registerData(iDogEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> SPEED_MOD = DataTracker.registerData(iDogEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private boolean forceFreshTick = false;
    //
    private float eyeRedValue;
    private float eyeGreenValue;
    private float eyeBlueValue;
    private float eyeAlphaValue;
    private Color baseEyeRGBA = new Color (1F, 1F, 1F, 1F);

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
        this.goalSelector.add(7, new iDogAlertsGoal(this, 16));
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

    @Override
    protected void initDataTracker() {
        super.initDataTracker();

        this.dataTracker.startTracking(BEGGING, false);
        this.dataTracker.startTracking(ALERTING, false);

        this.dataTracker.startTracking(START_TICK, 0L);
        this.dataTracker.startTracking(SONG_VOLUME, 1F);
        this.dataTracker.startTracking(LOOP_BOOLEAN, false);
        this.dataTracker.startTracking(ALERT_BOOLEAN, true);


        this.dataTracker.startTracking(DISC_ITEMSTACK, ItemStack.EMPTY);


        this.dataTracker.startTracking(IS_PLAYING, false);
        this.dataTracker.startTracking(CURRENT_DISC, "none");
        this.dataTracker.startTracking(EYE_VARIANT, 0);
        this.dataTracker.startTracking(SONG_END_TICK, 0L);
        this.dataTracker.startTracking(EYE_COVER, 0);
        this.dataTracker.startTracking(CURRENT_BPM, 130);
        this.dataTracker.startTracking(TICKS_PER_BEAT_CUMULATIVE, 0);
        this.dataTracker.startTracking(TICKS_PER_BEAT, 0F);
        this.dataTracker.startTracking(ANIMATION_SET, 0);
        this.dataTracker.startTracking(ANIMATION_STEP, -1);
        this.dataTracker.startTracking(ANIMATION_STEP_COUNT, 0);
        this.dataTracker.startTracking(BEAT_CUMULATIVE, 0);
        this.dataTracker.startTracking(EASE_METHOD, 0);
        this.dataTracker.startTracking(SPEED_MOD, 1F); // NEVER SET TO 0! BREAKS EVERYTHING!
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

            if (this.isPlayingRecord()) {
                this.ticksThisSecond++; //Ticks this second, for visual effects.

                this.songDisplayLogic();

                if (this.hasSecondPassed()) {
                    this.ticksThisSecond = 0;
                }
            } else {
                this.setEyeCover(0);
                this.setEyeVariant();
                this.eyeAlphaValue = 1F;
            }

            if(this.isBegging()) {
                this.cumulativeBegTick += 4;
                this.wasBegging = true;
            } else if(!this.isBegging() && this.wasBegging()) {
                this.cumulativeBegTick -= 2;
                if(this.cumulativeBegTick <= 0) {this.wasBegging = false; this.cumulativeBegTick = 0;}
            }

            this.tickCount++; //Cumulative ticks to compare with song length.
        }
    }

    public void songDisplayLogic() {

        /** Based on song BPM (if available), each beat corresponds with a pulse/ease.
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
        // .....................................................
        // occupiedAnimTick used for queue.
        // Each animation needs to chain covers together. Clockwise chain would be 6 occupiedAnimTicks
        // Subtract one animTick for every beat.*/

        if(this.isSongFinished()) {

            this.stopPlaying(); //Ends any moving sound instances.
            //System.out.println("LOOP IS " + this.getLoopBool());

            if(this.getLoopBool()) {
                //System.out.println("Freshness is " + this.forceFreshTick);
                this.startPlaying(getDiscAsItem());
            }
        } else {
            int tickBeatCumulative = this.dataTracker.get(TICKS_PER_BEAT_CUMULATIVE) + 1;
            float ticksPerBeat = this.dataTracker.get(TICKS_PER_BEAT);
            int animationStep = this.dataTracker.get(ANIMATION_STEP);
            float speedMod = this.dataTracker.get(SPEED_MOD);

            // Eye alpha logic,
            // Boolean for easing out, or flashing.
            // If easing out double the rate of delta, and then reverse it.
            this.dataTracker.set(TICKS_PER_BEAT_CUMULATIVE, tickBeatCumulative);

            float fadeDelta = switch(this.dataTracker.get(EASE_METHOD)) {
                case 0 -> tickBeatCumulative * speedMod / ticksPerBeat;
                //case 1 -> easeInCubic(tickBeatCumulative * speedMod / ticksPerBeat);
                //case 2 -> easeOutCubic(tickBeatCumulative * speedMod / ticksPerBeat);
                //case 3 -> easeInOutCubic(tickBeatCumulative * speedMod / ticksPerBeat); I don't know if I like these.
                default -> tickBeatCumulative * speedMod / ticksPerBeat;
            };

            this.eyeAlphaValue = fadeDelta;

            // Eye cover logic
            if (tickBeatCumulative * speedMod >= ticksPerBeat) { //An entire beat is finished!
                this.dataTracker.set(BEAT_CUMULATIVE, this.dataTracker.get(BEAT_CUMULATIVE) + 1); // Beat Cumulative

                if (animationStep <= 0) {
                    this.setEyeCover(getFromAnimSet(this.dataTracker.get(ANIMATION_SET), this.dataTracker.get(ANIMATION_STEP)));

                    // Animation finished. Get next

                    if(!getWorld().isClient) {
                        this.setNextAnimSetNumber(); //Chooses the next animation set
                        this.dataTracker.set(ANIMATION_STEP, this.getFromAnimSet(this.dataTracker.get(ANIMATION_SET), -1)); //Gets the steps
                        this.dataTracker.set(ANIMATION_STEP_COUNT, this.dataTracker.get(ANIMATION_STEP) + 1);//Sets the step count

                        this.setEyeVariant();

                        this.setEaseMethod();
                    }
                } else {
                    this.setEyeCover(getFromAnimSet(this.dataTracker.get(ANIMATION_SET), this.dataTracker.get(ANIMATION_STEP)));

                    if(!getWorld().isClient) {
                        this.dataTracker.set(ANIMATION_STEP, animationStep -1);
                        //Debug
                        //System.out.println("Anim set is: " + this.dataTracker.get(ANIMATION_SET) + " SpeedMod: " + dataTracker.get(SPEED_MOD));
                        //System.out.println("Anim step is: " + this.dataTracker.get(ANIMATION_STEP) + " of " + this.dataTracker.get(ANIMATION_STEP_COUNT));
                        //System.out.println("Eye cover is: " + this.dataTracker.get(EYE_COVER) + " Variant: " + this.getEyeVariant());
                    }
                }
                this.dataTracker.set(TICKS_PER_BEAT_CUMULATIVE, 0); //SET THIS TO ZERO EVERY BEAT YOU IDIOT!
            }
        }
    }

    private void setEaseMethod() {
        this.dataTracker.set(EASE_METHOD, iDogEasing.methodAmount);

        float speedMod = switch(this.random.nextInt(5)) {
          case 0 -> 1F;
          case 1 -> 1F;
          case 2 -> 0.5F;
          case 3 -> 0.5F;
          case 4 -> {
              if(this.random.nextInt(3) == 0) {yield 0.25F;} else {yield 0.5F;}}
            default -> 0.5F;
        };
        this.dataTracker.set(SPEED_MOD, speedMod);
    }

    //Bounds are listed below. Denotes the animation set. Zero included
    private void setNextAnimSetNumber() {
        this.dataTracker.set(ANIMATION_SET, this.random.nextInt(24));
    }

    //Returns the EyeCover for iDogLidFeatureRenderer if not initializing.
    // Else, returns the amount of steps.
    private int getFromAnimSet(int set, int step) {

        /** Arrays of animation sets.
         *  Takes the current set and step, and gives the EyeCover from the array set.
         *  Ergo:
         *  Clockwork pulse would be:   3 > 4 > 5 > 6 > 7 > 8| Eyecover textures
         *  Invert Clockwork would be: -3 >-4 >-5 >-6 >-7 >-8| [Corresponding Negatives]
         *  From steps:                 5 > 4 > 3 > 2 > 1 > 0| currentAnimStep
         *  If animStep less than 0, #songDisplayLogic() knows to select next animSet.
         */

        //Animation sets.

        //0 No Eyecover [1 step]
        int noneAnim = 0;
        //1 Animset Clockwise [6 step]
        int[] clockwiseAnim = {3, 4, 5, 6, 7, 8};
        //2 Animset Anti-Clockwise [6 step]
        int[] antiClockwiseAnim = {8, 7, 6, 5, 4, 3};
        //3 Animset Inverted Clockwise [6 step]
        int[] invertClockwiseAnim = {-3, -4, -5, -6, -7, -8};
        //4 Animset Inverted Anti-Clockwise [6 step]
        int[] invertAntiClockwiseAnim = {-8, -7, -6, -5, -4, -3};
        //5 Animset Stripe Down to Up [6 step]
        int[] stripeDownUpAnim = {-4, 40, 407, -7, 70, 407};
        //6 Animset Stripe Up to Down [6 step]
        int[] stripeUpDownAnim = {-7, 70, 407, -4, 40, 407};
        //7 Animset Stripe Down to Up Full [6 step]
        int[] stripeDownUpFullAnim = {407, 40, -4, -7, 70, 407};
        //8 Animset Stripe Up to Down Full [6 step]
        int[] stripeUpDownFullAnim = {407, 70, -7, -4, 40, 407};
        //9 Clockwise Inverted Triangles [6 step]
        int[] invertClockwiseTrianglesAnim = {338, 337, 336, 335, 334, 333};
        //10 Clockwise Inverted Triangles [6 step]
        int[] invertAntiClockwiseTrianglesAnim = {333, 334, 335, 336, 337, 338};
        //11 Clockwise Triangles [6 step]
        int[] clockwiseTrianglesAnim = {-338, -337, -336, -335, -334, -333};
        //12 Clockwise Triangles [6 step]
        int[] antiClockwiseTrianglesAnim = {-333, -334, -335, -336, -337, -338};
        //13 Alternating Triangles Clockwise [6]
        int[] alternateTrianglesAnim = {-334, -337, -333, -336, -338, -335};
        //14 Alternating Triangles Clockwise [6]
        int[] alternateTrianglesReverseAnim = {-335, -338, -336, -333, -337, -334};
        //15 Side Snakes [11]
        int[] sideSnakesAnim = {-3, -8, -1, -5, -6, 0, -6, -5, -1, -8, -3};
        //16 Side Snakes [11]
        int[] sideSnakesInvertedAnim = {3, 8, 1, 5, 6, 0, 6, 5, 1, 8, 3};
        //17 In and Out [8]
        int[] inAndOutAnim = {1, -1, 1, -1, 1, -1, 1, -1};
        //18 Stripes In and Out [12]
        int[] stripesInAndOutAnim = {407, -407, 407, -407, 407, 1, 407, 1, -407, -2, -407, -2};
        //19 Stripes In and Out [12]
        int[] stripesInAndOutReversedAnim = {-2, -407, -2, -407, 1, 407, 1, 407, -407, 407, -407, 407};
        //20 Twist Clockwise [7]
        int[] twistClockwiseAnim = {47, 58, 63, 74, 85, 36, 47};
        //21 Twist Clockwise Inverted [7]
        int[] twistClockwiseInvertedAnim = {-47, -58, -63, -74, -85, -36, -47};
        //22 Alternating Crosses [6]
        int[] alternatingCrossesAnim = {47, -47, 47, -47, 47, -47};
        //23 Washing Over [5]
        int[] washingOverAnim = {-4, -345, 678, 7, 0};
        //24 Washing Out [5]
        int[] washingOutAnim = {0, 4, 345, -678, -7};

        if(step <= -1) {
            return switch(set) { // Initialize currentAnimStep
                case 0 -> 3;
                case 1 -> clockwiseAnim.length-1;
                case 2 -> antiClockwiseAnim.length-1;
                case 3 -> invertClockwiseAnim.length-1;
                case 4 -> invertAntiClockwiseAnim.length-1;
                case 5 -> stripeDownUpAnim.length-1;
                case 6 -> stripeUpDownAnim.length-1;
                case 7 -> stripeDownUpFullAnim.length-1;
                case 8 -> stripeUpDownFullAnim.length-1;
                case 9 -> invertClockwiseTrianglesAnim.length-1;
                case 10 -> invertAntiClockwiseTrianglesAnim.length-1;
                case 11 -> clockwiseTrianglesAnim.length-1;
                case 12 -> antiClockwiseTrianglesAnim.length-1;
                case 13 -> alternateTrianglesAnim.length-1;
                case 14 -> alternateTrianglesReverseAnim.length-1;
                case 15 -> sideSnakesAnim.length-1;
                case 16 -> sideSnakesInvertedAnim.length-1;
                case 17 -> inAndOutAnim.length-1;
                case 18 -> stripesInAndOutAnim.length-1;
                case 19 -> stripesInAndOutReversedAnim.length-1;
                case 20 -> twistClockwiseAnim.length-1;
                case 21 -> twistClockwiseInvertedAnim.length-1;
                case 22 -> alternatingCrossesAnim.length-1;
                case 23 -> washingOverAnim.length-1;
                case 24 -> washingOutAnim.length-1;
                default -> 1;
            };
        } else { // Return EyeCover
            return switch (set) {
                case 0 -> noneAnim;
                case 1 -> clockwiseAnim[step];
                case 2 -> antiClockwiseAnim[step];
                case 3 -> invertClockwiseAnim[step];
                case 4 -> invertAntiClockwiseAnim[step];
                case 5 -> stripeDownUpAnim[step];
                case 6 -> stripeUpDownAnim[step];
                case 7 -> stripeDownUpFullAnim[step];
                case 8 -> stripeUpDownFullAnim[step];
                case 9 -> invertClockwiseTrianglesAnim[step];
                case 10 -> invertAntiClockwiseTrianglesAnim[step];
                case 11 -> clockwiseTrianglesAnim[step];
                case 12 -> antiClockwiseTrianglesAnim[step];
                case 13 -> alternateTrianglesAnim[step];
                case 14 -> alternateTrianglesReverseAnim[step];
                case 15 -> sideSnakesAnim[step];
                case 16 -> sideSnakesInvertedAnim[step];
                case 17 -> inAndOutAnim[step];
                case 18 -> stripesInAndOutAnim[step];
                case 19 -> stripesInAndOutReversedAnim[step];
                case 20 -> twistClockwiseAnim[step];
                case 21 -> twistClockwiseInvertedAnim[step];
                case 22 -> alternatingCrossesAnim[step];
                case 23 -> washingOverAnim[step];
                case 24 -> washingOutAnim[step];
                default -> noneAnim;
            };
        }
    }

    private void setEyeVariant() {
        this.dataTracker.set(EYE_VARIANT, getEyeVariantFromDisc(getCurrentDisc()));
    }

    public int getEyeVariant() {
        return this.dataTracker.get(EYE_VARIANT);
    }

    //Takes a string starting with "music_disc_" and returns an integer denoting the eye variant to display
    private int getEyeVariantFromDisc(String disc) {

        if(!this.isPlayingRecord()) {
            return 0; //Every array will always have an entry at 0.
        }

        return switch (disc) {
            case "none" -> this.random.nextInt(music_disc_default_variants.length-1);
            case "music_disc_5" -> this.random.nextInt(music_disc_5_variants.length-1);
            case "music_disc_11" -> this.random.nextInt(music_disc_11_variants.length-1);
            case "music_disc_13" -> this.random.nextInt(music_disc_13_variants.length-1);
            case "music_disc_blocks" -> this.random.nextInt(music_disc_blocks_variants.length-1);
            case "music_disc_cat" -> this.random.nextInt(music_disc_cat_variants.length-1);
            case "music_disc_chirp" -> this.random.nextInt(music_disc_chirp_variants.length-1);
            case "music_disc_far" -> this.random.nextInt(music_disc_far_variants.length-1);
            case "music_disc_mall" -> this.random.nextInt(music_disc_mall_variants.length-1);
            case "music_disc_mellohi" -> this.random.nextInt(music_disc_mellohi_variants.length-1);
            case "music_disc_otherside" -> this.random.nextInt(music_disc_otherside_variants.length-1);
            case "music_disc_pigstep" -> this.random.nextInt(music_disc_pigstep_variants.length-1);
            case "music_disc_relic" -> this.random.nextInt(music_disc_relic_variants.length-1);
            case "music_disc_stal" -> this.random.nextInt(music_disc_stal_variants.length-1);
            case "music_disc_strad" -> this.random.nextInt(music_disc_strad_variants.length-1);
            case "music_disc_wait" -> this.random.nextInt(music_disc_wait_variants.length-1);
            case "music_disc_ward" -> this.random.nextInt(music_disc_ward_variants.length-1);
            //----------------------------------------------------------------------------------------
            //Modded
            case "music_disc_red" -> this.random.nextInt(music_disc_randomRed_variants.length-1);
            case "music_disc_green" -> this.random.nextInt(music_disc_randomGreen_variants.length-1);
            case "music_disc_blue" -> this.random.nextInt(music_disc_randomBlue_variants.length-1);
            case "music_disc_rainbow" -> this.random.nextInt(music_disc_randomRainbow_variants.length-1);
            case "music_disc_creeper" -> this.random.nextInt(music_disc_randomCreeper_variants.length-1);
            default -> { int rand = this.random.nextInt(4);
                switch (rand) {
                    case 0 -> setCurrentDisc("music_disc_red");
                    case 1 -> setCurrentDisc("music_disc_green");
                    case 2 -> setCurrentDisc("music_disc_blue");
                    case 3 -> setCurrentDisc("music_disc_rainbow");
                    case 4 -> setCurrentDisc("music_disc_creeper"); //If you add more later, don't forget the bounds!
                }
                yield getEyeVariantFromDisc(getCurrentDisc());
            }
        };
    }

    public float getEyeRedValue() {
        return this.eyeRedValue;
    }
    public float getEyeGreenValue() {
        return this.eyeGreenValue;
    }
    public float getEyeBlueValue() {
        return this.eyeBlueValue;
    }
    public float getEyeAlpha() {
        return this.eyeAlphaValue;
    }
    private void setEyeRGBAFromDisc(String disc) {

        //TODO: Get RGB values for subsequent discs.
        this.baseEyeRGBA = switch (disc) {
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

        this.eyeRedValue = this.baseEyeRGBA.getRed();
        this.eyeGreenValue = this.baseEyeRGBA.getGreen();
        this.eyeBlueValue = this.baseEyeRGBA.getBlue();
        this.eyeAlphaValue = this.baseEyeRGBA.getAlpha();
    }

    public int getEyeCover() {
        return this.dataTracker.get(EYE_COVER);
    }

    private void setEyeCover(int passedInt) {
        this.dataTracker.set(EYE_COVER, passedInt);
    }

    private boolean hasSecondPassed() {
        return this.ticksThisSecond >= 20;
    }

    public int getCurrentBPM() {
        return this.dataTracker.get(CURRENT_BPM);
    }

    private void setCurrentBPM(String disc) {

        int currentBPM = switch (disc) {
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
            case "music_disc_calm4" -> 140;
            default -> 130;
        };

        this.dataTracker.set(CURRENT_BPM, currentBPM);
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

    @Override
    public void chooseRandomAngerTime() {
        this.setAngerTime(ANGER_TIME_RANGE.get(this.random));
    }

    public int getBegDelta() {
        return MathHelper.clamp(this.cumulativeBegTick, 0, 60);
    }

    public int getInverseBegDelta() {
        this.cumulativeBegTick -= 1;
        return MathHelper.clamp(this.cumulativeBegTick, 0, 60);
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
            this.dataTracker.set(DISC_ITEMSTACK, ItemStack.fromNbt(nbt.getCompound("RecordItem")));
        }

        this.dataTracker.set(LOOP_BOOLEAN, nbt.getBoolean("LoopSongs"));
        this.dataTracker.set(ALERT_BOOLEAN, nbt.getBoolean("AlertMe"));
        this.dataTracker.set(SONG_VOLUME, nbt.getFloat("SongVolume"));
        this.dataTracker.set(START_TICK, nbt.getLong("RecordStartTick")); //TODO: REPLACE VARIABLE WITH DATATRACKER
        this.tickCount = nbt.getLong("TickCount"); //TODO: REPLACE VARIABLE WITH DATATRACKER

        if (nbt.contains("CurrentDisc")) {
            String disc = nbt.getString("CurrentDisc");
            this.setCurrentDisc(disc); // <- Should force dataTracker sync to all clients
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (!this.getStack().isEmpty()) {
            nbt.put("RecordItem", this.getStack().writeNbt(new NbtCompound()));
        }

        //TODO: Alerts for danger and treasure minecarts
        // On alert, volume for sound instance is lerped to 0 until the alert finishes, and then it is lerped back to its original volume.
        if(!isTamed()) {
            nbt.putFloat("SongVolume", 1F);
            nbt.putBoolean("LoopSongs", false);
            nbt.putBoolean("AlertMe", true);
        } else {
            nbt.putFloat("SongVolume", this.dataTracker.get(SONG_VOLUME));
            nbt.putBoolean("LoopSongs", this.dataTracker.get(LOOP_BOOLEAN));
            nbt.putBoolean("AlertMe", this.dataTracker.get(ALERT_BOOLEAN));
        }
        nbt.putLong("RecordStartTick", this.dataTracker.get(START_TICK));
        nbt.putLong("TickCount", this.tickCount);
        nbt.putString("CurrentDisc", this.getCurrentDisc());
    }

    public ItemStack getCurrentDiscItemStack() {
        return this.inventory.get(0);
    }

    public String getCurrentDisc() {
        return this.dataTracker.get(CURRENT_DISC);
    }

    private void setCurrentDisc(String discName) {
        this.dataTracker.set(CURRENT_DISC, discName);
    }

    private void setLoopBool(boolean passedBool) {
        this.dataTracker.set(LOOP_BOOLEAN, passedBool);
    }

    public boolean getLoopBool() {
        return this.dataTracker.get(LOOP_BOOLEAN);
    }

    private void setAlertBool(boolean passedBool) {

        if(!passedBool) { //If not alerting, set alerting to false.
            this.setAlerting(false);
        }

        this.dataTracker.set(ALERT_BOOLEAN, passedBool);
    }

    public boolean getAlertBool() {
        return this.dataTracker.get(ALERT_BOOLEAN);
    }

    public void setSongVolume(int passedVolume) {
        this.dataTracker.set(SONG_VOLUME, (MathHelper.clamp(passedVolume, 0, 100))/100F);
        //Debug
        //System.out.println("Volume was set to: " + this.getSongVolume());
    }

    public float getSongVolume(boolean override) {
        if(override) {
            return this.dataTracker.get(SONG_VOLUME);
        }
        if(this.isAlerting()) {
            return 0.01F;
        } else {
            return this.dataTracker.get(SONG_VOLUME);
        }
    }

    public boolean isPlayingRecord() {
        return this.dataTracker.get(IS_PLAYING); //Previously: return !this.getStack().isEmpty() && this.isPlaying;
    }

    public boolean isAlerting() {
        return this.dataTracker.get(ALERTING);
    }

    public void setAlerting(boolean bool) {
        this.dataTracker.set(ALERTING, bool);
        System.out.println("Alerting is now: " + bool);
    }

    public void stopPlayingRecord() {
        this.dataTracker.set(IS_PLAYING, false);
    }

    //TODO SOUND INSTANCE FINISHED WHEN LOOPING. START PLAYING LOGIC IN HERE
    public void soundInstanceFinishedAlert() {

        this.stopPlaying();
        this.startPlaying(getDiscAsItem());
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return stack.isIn(ItemTags.MUSIC_DISCS) && this.getStack(slot).isEmpty();
    }

    /**
     * An important lesson was learned from the function below. \|/
     * This code will be called on the server and client.
     * That means everything is done twice.
     *-----------------------------------------------------
     * DataTracker is important because it forces client and server synchronization.
     * If you have weird crashes, don't forget to check what the DataTracker is doing!
     * ... And more importantly, NOT doing!
     */

    public void startPlaying(MusicDiscItem musicDisc) {

        if(!this.forceFreshTick) {
            this.forceFreshTick = true;
            this.dataTracker.set(START_TICK, this.tickCount);

            //System.out.println("Am I fresh? " + forceFreshTick);

            if(!this.getWorld().isClient) {
                this.dataTracker.set(IS_PLAYING, true);
                this.setSongEndTick(musicDisc); // Tells dataTracker the end tick.
                this.setCurrentBPM(musicDisc.toString());
                this.setTicksPerBeat();
                this.setEaseMethod();

                //Is stopPlaying() running on client? Maybe send a packet to the client immediately.

                //Debug
                //System.out.println("Song End Tick is: " + this.dataTracker.get(SONG_END_TICK));

                //Set first animation
                this.setNextAnimSetNumber();
                //Set animationStep
                this.dataTracker.set(ANIMATION_STEP, getFromAnimSet(this.dataTracker.get(ANIMATION_SET), -1));
                this.dataTracker.set(ANIMATION_STEP_COUNT, this.dataTracker.get(ANIMATION_STEP) + 1); //Set Step Count
                this.setEyeVariant();

                //Debug
                //System.out.println("Anim set is: " + this.dataTracker.get(ANIMATION_SET));
                //System.out.println("Anim step is: " + this.dataTracker.get(ANIMATION_STEP) + " of " + this.dataTracker.get(ANIMATION_STEP_COUNT));
                //System.out.println("Eye cover is: " + this.dataTracker.get(EYE_COVER));
            }

            /* TODO: THIS MIGHT BE A SOLUTION TO IS_PLAYING MISMATCH
            boolean serverBool;
            boolean clientBool;

            if(!this.getWorld().isClient) {
                serverBool = this.dataTracker.get(IS_PLAYING);
            } else {
                clientBool = this.dataTracker.get(IS_PLAYING);
            }

            if(serverBool != clientBool) {
                this.dataTracker.set(IS_PLAYING, true);
            }
             */


            this.dataTracker.set(IS_PLAYING, true);
            this.debugPrintDataTrackedValues();

            //Sending {@link iDogMovingSoundInstance} Packet information to server
            if (!this.getWorld().isClient) {

                //System.out.println("Look mom! I'm gonna send it!");

                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeInt(this.getId());
                buf.writeIdentifier(Registries.ITEM.getId(musicDisc));
                ServerWorld serverWorld = (ServerWorld) this.getWorld();
                serverWorld.getPlayers().forEach(player -> {
                    ServerPlayNetworking.send(player, iDogMod.PLAY_IDOG_MUSIC, buf);
                    //System.out.println("Sound packet sent to Player: " + player + " from entity: " + this.getId() + " with disc ID of: " + Registries.ITEM.getId(musicDisc));
                });
            }
            this.currentSong = musicDisc.getSound();
            this.markDirty();
        } else {
            //Debug
            //System.out.println("Tried to run song twice! Not on my watch!");
        }
    }

    private void stopPlaying() {

        if (!getWorld().isClient && this.isPlayingRecord()) {
            this.dataTracker.set(IS_PLAYING, false);
            this.dataTracker.set(SONG_END_TICK, 0L);

            this.dataTracker.set(IS_PLAYING, false);
            this.dataTracker.set(SONG_END_TICK, 0L);
            this.dataTracker.set(EYE_COVER, 0);
            this.dataTracker.set(CURRENT_BPM, 130);
            this.dataTracker.set(TICKS_PER_BEAT_CUMULATIVE, 0);
            this.dataTracker.set(TICKS_PER_BEAT, 0F);
            this.dataTracker.set(ANIMATION_SET, 0);
            this.dataTracker.set(ANIMATION_STEP, -1);
            this.dataTracker.set(ANIMATION_STEP_COUNT, 0);
            this.dataTracker.set(BEAT_CUMULATIVE, 0);
            this.dataTracker.set(SPEED_MOD, 1F);
        }
        this.forceFreshTick = false; // If false, that means fresh tick is not enforced.
        this.markDirty();
    }

    private boolean isSongFinished() {
        if(this.tickCount >= this.dataTracker.get(SONG_END_TICK)) {
            return true;
        } else {
            return false;
        }
    }

    private void setSongEndTick(MusicDiscItem musicDisc) {
        this.dataTracker.set(SONG_END_TICK, this.dataTracker.get(START_TICK) + musicDisc.getSongLengthInTicks() + 20L);
    }

    public long getSongEndTick() {
        return this.dataTracker.get(SONG_END_TICK);
    }

    private void setTicksPerBeat() {
        this.dataTracker.set(TICKS_PER_BEAT, 60F/getCurrentBPM() * 20F);
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(0);
    }

    public MusicDiscItem getDiscAsItem() {
        //Debug
        //System.out.println(dataTracker.get(DISC_ITEMSTACK).getItem().toString());

        if(dataTracker.get(DISC_ITEMSTACK).getItem() instanceof MusicDiscItem) {
            return (MusicDiscItem) dataTracker.get(DISC_ITEMSTACK).getItem();
        } else {
            return null;
        }
    }

    @Override
    public void setStack(int slot, ItemStack stack) {

        if (stack.isIn(ItemTags.MUSIC_DISCS)) {
            this.inventory.set(0, stack);
            this.dataTracker.set(DISC_ITEMSTACK, stack);
            this.setCurrentDisc(stack.getItem().toString());
            //Debug
            //System.out.println("iDog is now playing: " + getCurrentDisc());
            this.startPlaying((MusicDiscItem) stack.getItem());
            this.setSongEndTick((MusicDiscItem) stack.getItem());

            playSound(SoundEvents.ENTITY_ITEM_FRAME_ROTATE_ITEM, 2.0F, 0.25F);
            playSound(SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, 2.0F, 0.5F);
        }
    }

    //TODO: CHECK IF IDOG HAS THE MUSIC DISC EVERY BEAT. IF NOT, STOP PLAYING.
    @Override //Todo: removeStack not being called in mob interaction with disc
    public ItemStack removeStack(int slot, int amount) {
        this.dropRecord();
        ItemStack itemStack = (ItemStack) Objects.requireNonNullElse(this.inventory.get(0), ItemStack.EMPTY);
        playSound(SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, 2.0F, 2.0F);
        this.inventory.set(0, ItemStack.EMPTY);
        if (!itemStack.isEmpty()) {
            this.setCurrentDisc("none"); //SOMETIMES IT'S JUST RANDOMLY EMPTY!
            this.dataTracker.set(DISC_ITEMSTACK, ItemStack.EMPTY);
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

    public void handleReceivedPacket(int type) {
        //Debug
        System.out.println("iDog handling packet of: " + type);

        switch(type) {
            case -100 -> this.setAlerting(false);
            case -10 -> this.setSongVolume((int) ((this.getSongVolume(true) * 100) - 10));   //Vol -10 Packet
            case -5 -> this.setSongVolume((int) ((this.getSongVolume(true) * 100) - 5));     //Vol -5 Packet
            case 5 -> this.setSongVolume((int) ((this.getSongVolume(true) * 100) + 5));      //Vol +5 Packet
            case 10 -> this.setSongVolume((int) ((this.getSongVolume(true) * 100) + 10));    //Vol +10 Packet
            //------------------------------------------------
            case 0 -> this.stopPlaying(); //TODO once i figure out why isPlaying seems to not refresh properly
            case 1 -> this.setSongVolume(100);      //Vol MAX Packet
            case -1 -> this.setSongVolume(0);       //Vol ZERO Packet
            case 2 -> this.setLoopBool(true);       //Loop ON Packet
            case -2 -> this.setLoopBool(false);     //Loop OFF Packet
            case 3 -> this.setAlertBool(true);      //Alerts ON Packet
            case -3 -> this.setAlertBool(false);    //Alerts OFF Packet
            //------------------------------------------------
            case 4 -> this.removeStack();           //EJECT Disc Packet
            //Warning
            default -> System.out.println("Non-compliant type attempt of: " + type);
        }
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {


        ItemStack itemStack = player.getStackInHand(hand);

            if (!this.isOwner(player) && !this.isTamed()) { // if player is not owner, it is not tamed,

                if (itemStack.isOf(Items.BONE) && !this.hasAngerTime()) {
                    if (this.random.nextInt(3) == 0) { // 25% chance to tame
                        this.setOwner(player);
                        this.navigation.stop();
                        itemStack.decrement(1);
                        if (!this.getWorld().isClient) {
                            super.setSitting(true);
                            this.dataTracker.set(SONG_VOLUME, 1F);
                            this.dataTracker.set(LOOP_BOOLEAN, false);
                            this.dataTracker.set(ALERT_BOOLEAN, true);
                        }
                        this.setTarget(null);
                        this.getWorld().sendEntityStatus(this, (byte) 7); // heart particles
                        return ActionResult.SUCCESS;
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

                    if (player.isSneaking())
                    {
                        if (!player.getWorld().isClient) {
                            player.openHandledScreen(this); // this triggers createMenu()
                            return ActionResult.SUCCESS;
                        }

                        //TODO: REPLACE THIS AND THE CROUCH DISC REMOVAL.
                        // GUI SHOULD BE OPENED WHEN CROUCH RIGHT CLICKED, LIKE A HORSE
                        // IMPLEMENT EJECT INSIDE OF GUI
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
                    ItemStack discCopy = itemStack.copy(); //Prevents itemStack.decrement from turning the disc into air!

                    if (getCurrentDiscItemStack().getItem() instanceof MusicDiscItem) {
                        this.removeStack();
                    } else {
                        this.setStack(0, discCopy);
                        if(!player.getAbilities().creativeMode) {
                            itemStack.decrement(1);
                        }
                    }
                    return ActionResult.SUCCESS;
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
            return ModSounds.ENTITY_IDOG_GROWL;
        } else if (this.random.nextInt(3) == 0) {
            return this.isTamed() && this.getHealth() < 10.0F ? ModSounds.ENTITY_IDOG_WHINE : ModSounds.ENTITY_IDOG_PANT;
        } else {
            return ModSounds.ENTITY_IDOG_AMBIENT;
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.ENTITY_IDOG_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.ENTITY_IDOG_DEATH;
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
    //TODO: Implement modified goal similar to iDogBegGoal.
    // Detect entities in a radius, ignore line of sight.
    // Inside class, if entity is detected and alerts are true, send playAlertSounds(type) to iDog.
    public void playAlertSounds(int alertType) {

        //TODO ADD CUSTOM ALERT SOUNDS AND IDOG CONTROLLER
        //-1 Treasure Minecart, 0 Zombie, 1 Skeleton, 2 Spider, 3 Creeper, 4 Enderman,

        //Sending {@link iDogMovingSoundInstance} Packet information to server
        if (!this.getWorld().isClient) {

            //System.out.println("Look mom! I'm gonna send it!");

            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(this.getId());
            buf.writeInt(alertType);
            ServerWorld serverWorld = (ServerWorld) this.getWorld();
            serverWorld.getPlayers().forEach(player -> {
                ServerPlayNetworking.send(player, iDogMod.PLAY_IDOG_ALERT, buf);
                //System.out.println("Sound packet sent to Player: " + player + " from entity: " + this.getId() + " with disc ID of: " + Registries.ITEM.getId(musicDisc));
            });

            System.out.println("Played " + alertType);
        }
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

    //Screens
    public Text getDisplayName() {
        if(this.hasCustomName()) {
          return this.getCustomName();
        }
        return Text.literal("iDog");
    }

    public Inventory getInventory() {
        return this;
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(this.getId()); //iDog entity ID
        //We provide this to the screenHandler as our class Implements Inventory
        //Only the Server has the Inventory at the start, this will be synced to the client in the ScreenHandler
        return new iDogScreenHandler(syncId, playerInventory, this, this);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeInt(this.getId()); //iDog entity ID

        /* Redundant? Can get from entity.
        buf.writeFloat(getSongVolume()); //Volume
        buf.writeBoolean(getLoopBool()); //Loop
        buf.writeBoolean(getAlertBool()); //Alerts
         */
    }

    public void debugPrintDataTrackedValues() {
        System.out.println("--DEBUG VALUES START HERE--");
        if(this.getWorld().isClient)
        {
            System.out.println("On client:");
        } else {
            System.out.println("On server:");
        }

        //System.out.println(this.dataTracker.get(BEGGING));
        //System.out.println(this.dataTracker.get(ALERTING));

        System.out.println("Start tick: "+this.dataTracker.get(START_TICK));
        System.out.println("End tick: "+this.dataTracker.get(SONG_END_TICK));
        //System.out.println(this.dataTracker.get(SONG_VOLUME));
        //System.out.println(this.dataTracker.get(LOOP_BOOLEAN));
        //System.out.println(this.dataTracker.get(ALERT_BOOLEAN));

        System.out.println("Current disc: "+this.dataTracker.get(CURRENT_DISC));
        System.out.println("Disc itemstack: "+this.dataTracker.get(DISC_ITEMSTACK));


        System.out.println("Is playing?: "+this.dataTracker.get(IS_PLAYING));
        //System.out.println(this.dataTracker.get(EYE_VARIANT));
        //System.out.println(this.dataTracker.get(EYE_COVER));
        //System.out.println(this.dataTracker.get(CURRENT_BPM));
        //System.out.println(this.dataTracker.get(TICKS_PER_BEAT_CUMULATIVE));
        //System.out.println(this.dataTracker.get(TICKS_PER_BEAT));
        //System.out.println(this.dataTracker.get(ANIMATION_SET));
        //System.out.println(this.dataTracker.get(ANIMATION_STEP));
        //System.out.println(this.dataTracker.get(ANIMATION_STEP_COUNT));
        //System.out.println("Beat cumulative: "+this.dataTracker.get(BEAT_CUMULATIVE));
        //System.out.println(this.dataTracker.get(EASE_METHOD));
        //System.out.println(this.dataTracker.get(SPEED_MOD));
        if(this.getWorld().isClient)
        {
            System.out.println("On client.");
        } else {
            System.out.println("On server.");
        }
        System.out.println("--DEBUG VALUES END HERE--");
    }

    /*
    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        //The pos field is a public field from BlockEntity
        packetByteBuf.writeBlockPos(this.getBlockPos());
    }

     */
}
