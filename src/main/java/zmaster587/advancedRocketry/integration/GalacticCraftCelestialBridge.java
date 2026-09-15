package zmaster587.advancedRocketry.integration;

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import zmaster587.advancedRocketry.AdvancedRocketry;

public class GalacticCraftCelestialBridge {

    public static void logRegisteredBodies() {
        AdvancedRocketry.logger.info("[AR-Fork] Galacticraft celestial registry probe starting");

        for (CelestialBody body : GalaxyRegistry.getRegisteredPlanets().values()) {
            AdvancedRocketry.logger.info(
                    "[AR-Fork] GC planet: {} | dim {}",
                    body.getName(),
                    body.getDimensionID()
            );
        }

        for (CelestialBody body : GalaxyRegistry.getRegisteredMoons().values()) {
            AdvancedRocketry.logger.info(
                    "[AR-Fork] GC moon: {} | dim {}",
                    body.getName(),
                    body.getDimensionID()
            );
        }

        AdvancedRocketry.logger.info("[AR-Fork] Galacticraft celestial registry probe complete");
    }
}