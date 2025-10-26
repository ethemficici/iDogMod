package com.ethem00.idogmod.entity.client.render.entity.animation;

import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.animation.Transformation;

public class iDogAnimations {
    public static final Animation TAIL_HEALTH = Animation.Builder.create(30.0F) //Corresponds to max HP of 30
            .addBoneAnimation(
                    "tail",
                    new Transformation(
                            Transformation.Targets.ROTATE,
                            new Keyframe(0.0F, AnimationHelper.createRotationalVector(0, 0, 0), Transformation.Interpolations.CUBIC),
                            new Keyframe(30.0F, AnimationHelper.createRotationalVector(-105.0F, 0.0F, 0F), Transformation.Interpolations.CUBIC))
            )
            .build();

    public static final Animation BEGGING = Animation.Builder.create(60.0F) //Corresponds with limbSwing and limbDistance .create(60) and lS(60)+lD(60) will instantly snap to the final frame.
            .addBoneAnimation(
                    "head",
                    new Transformation(
                            Transformation.Targets.ROTATE,
                            new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                            new Keyframe(30.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -125.0F), Transformation.Interpolations.CUBIC),
                            new Keyframe(60.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -250.0F), Transformation.Interpolations.CUBIC)
                    )
            )
            .addBoneAnimation(
                    "left_ear",
                    new Transformation(
                            Transformation.Targets.ROTATE,
                            new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                            new Keyframe(60.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -150.0F), Transformation.Interpolations.CUBIC))
            )
            .addBoneAnimation(
                    "right_ear",
                    new Transformation(
                            Transformation.Targets.ROTATE,
                            new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                            new Keyframe(60.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -200.0F), Transformation.Interpolations.CUBIC))
            )
            .build();

    public static final Animation SITTING = Animation.Builder.create(1.0F)
            .looping()
            .addBoneAnimation(
                    "root",
                    new Transformation(
                            Transformation.Targets.TRANSLATE,
                            new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 50.0F, 0.0F), Transformation.Interpolations.CUBIC))
            )
            .addBoneAnimation(
                    "root",
                    new Transformation(
                            Transformation.Targets.ROTATE,
                            new Keyframe(0.0F, AnimationHelper.createRotationalVector(-10.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC))
            )
            .addBoneAnimation(
                    "head",
                    new Transformation(
                            Transformation.Targets.ROTATE,
                            new Keyframe(0.0F, AnimationHelper.createRotationalVector(10.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC))
            )
            .addBoneAnimation(
                    "left_hind_leg",
                    new Transformation(
                            Transformation.Targets.ROTATE,
                            new Keyframe(0.0F, AnimationHelper.createRotationalVector(-55.0F, -15.0F, -12.0F), Transformation.Interpolations.CUBIC))
            )
            .addBoneAnimation(
                    "right_hind_leg",
                    new Transformation(
                            Transformation.Targets.ROTATE,
                            new Keyframe(0.0F, AnimationHelper.createRotationalVector(-55.0F, 15.0F, 12.0F), Transformation.Interpolations.CUBIC))
            )
            .addBoneAnimation(
                    "left_front_leg",
                    new Transformation(
                            Transformation.Targets.ROTATE,
                            new Keyframe(0.0F, AnimationHelper.createRotationalVector(2.5F, 0.5F, -5.0F), Transformation.Interpolations.CUBIC))
            )
            .addBoneAnimation(
                    "right_front_leg",
                    new Transformation(
                            Transformation.Targets.ROTATE,
                            new Keyframe(0.0F, AnimationHelper.createRotationalVector(2.5F, -0.5F, 5.0F), Transformation.Interpolations.CUBIC))
            )
            .build();

    public static final Animation WALKING_EARS_TAIL = Animation.Builder.create(1.5F)
            .looping()
            .addBoneAnimation(
                    "left_hind_leg",
                    new Transformation(
                            Transformation.Targets.ROTATE,
                            new Keyframe(0.0F, AnimationHelper.createRotationalVector(22.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                            new Keyframe(0.5F, AnimationHelper.createRotationalVector(-22.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                            new Keyframe(1.0F, AnimationHelper.createRotationalVector(22.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
                    )
            )
            .addBoneAnimation(
                    "right_hind_leg",
                    new Transformation(
                            Transformation.Targets.ROTATE,
                            new Keyframe(0.0F, AnimationHelper.createRotationalVector(-22.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                            new Keyframe(0.5F, AnimationHelper.createRotationalVector(22.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                            new Keyframe(1.0F, AnimationHelper.createRotationalVector(-22.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
                            )
            )
            .addBoneAnimation(
                    "left_front_leg",
                    new Transformation(
                            Transformation.Targets.ROTATE,
                            new Keyframe(0.0F, AnimationHelper.createRotationalVector(-22.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                            new Keyframe(0.5F, AnimationHelper.createRotationalVector(22.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                            new Keyframe(1.0F, AnimationHelper.createRotationalVector(-22.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
                            )
            )
            .addBoneAnimation(
                    "right_front_leg",
                    new Transformation(
                            Transformation.Targets.ROTATE,
                            new Keyframe(0.0F, AnimationHelper.createRotationalVector(22.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                            new Keyframe(0.5F, AnimationHelper.createRotationalVector(-22.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                            new Keyframe(1.0F, AnimationHelper.createRotationalVector(22.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
                            )
            )
    		.addBoneAnimation(
			        "left_ear",
                    new Transformation(
                            Transformation.Targets.ROTATE,
				            new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                            new Keyframe(0.375F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -15.0F), Transformation.Interpolations.CUBIC),
                            new Keyframe(0.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                            new Keyframe(1.125F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -15.0F), Transformation.Interpolations.CUBIC),
                            new Keyframe(1.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
                    )
            )
            .addBoneAnimation(
			"right_ear",
                    new Transformation(
                    Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
            new Keyframe(0.375F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 15.0F), Transformation.Interpolations.CUBIC),
            new Keyframe(0.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
            new Keyframe(1.125F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 15.0F), Transformation.Interpolations.CUBIC),
            new Keyframe(1.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
            )
            )
    		.addBoneAnimation(
			"tail",
                    new Transformation(
                    Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(15.94102F, -8.42106F, 20.94102F), Transformation.Interpolations.CUBIC),
            new Keyframe(0.75F, AnimationHelper.createRotationalVector(15.94102F, 8.42106F, -20.94102F), Transformation.Interpolations.CUBIC),
            new Keyframe(1.5F, AnimationHelper.createRotationalVector(15.94102F, -8.42106F, 20.94102F), Transformation.Interpolations.CUBIC)
            )
            )
            .build();
}
