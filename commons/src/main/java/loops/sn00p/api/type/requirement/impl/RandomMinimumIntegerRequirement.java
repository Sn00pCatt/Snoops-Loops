package loops.sn00p.api.type.requirement.impl;

import loops.sn00p.api.math.UtilRandom;
import loops.sn00p.api.type.requirement.Requirement;

/**
 *
 * Random minimum integer implementation of the {@link Requirement} interface.
 * Using the {@link MinimumIntegerRequirement} but it randomly selects the minimum value based on the min
 * and max provided in the constructor
 *
 */
public class RandomMinimumIntegerRequirement extends MinimumIntegerRequirement {

    public RandomMinimumIntegerRequirement(int min, int max) {
        super(UtilRandom.randomInteger(min, max));
    }
}
