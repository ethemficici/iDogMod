package com.ethem00.idogmod.entity.client.render.entity.animation;

/*
https://easings.net/

Easing functions provided by Andrey Sitnik and Ivan Solovev
 */

public interface iDogEasing {

    default double easeInCubic(int delta) {

        return (1 - Math.pow(1-delta, 3));
    }
}
