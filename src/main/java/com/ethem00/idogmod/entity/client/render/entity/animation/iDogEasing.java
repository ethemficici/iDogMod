package com.ethem00.idogmod.entity.client.render.entity.animation;

/*
https://easings.net/

Easing functions provided by Andrey Sitnik and Ivan Solovev
 */

import net.minecraft.util.math.MathHelper;

public interface iDogEasing {

    // 0 means no easing, 1 = easeInCubic, 2 = easeOutCubic
    public final int methodAmount = 2; //Used in selecting random in iDogEntity.

    // 1
    default float easeInCubic(float delta) {


        return MathHelper.clamp(delta * delta * delta, 0, 1);
    }
    // 2
    default float easeOutCubic(float delta) {

        float d = MathHelper.clamp(delta, 0.0F, 1.0F);
        return 1 - (float) StrictMath.pow(1 - d, 3);
    }

    // 3
    default float easeInOutCubic(float delta) {
        return delta < 0.5 ? (4 * delta * delta * delta) : (1 - (float) StrictMath.pow(-2 * delta + 2, 3) / 2);
    }

}
