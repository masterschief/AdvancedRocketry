package zmaster587.advancedRocketry.integration;

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;

public class GalacticCraftCelestialBridge {

    private static final String PRIMARY_MOON_NAME = "moon";

    /**
     * Points Advanced Rocketry's built-in Moon slot at Galacticraft's real Moon
     * dimension before AR builds its celestial model.  The dimension remains
     * externally owned: AR only registers DimensionProperties for navigation
     * and never registers or generates the Forge dimension itself.
     *
     * @return true when the Galacticraft Moon was found and bound
     */
    public static boolean bindPrimaryMoon() {
        if (ARConfiguration.getCurrentConfig().MoonId != Constants.INVALID_PLANET) {
            AdvancedRocketry.logger.info(
                    "[Voidspan] Moon bridge skipped; AR MoonId is already {}",
                    ARConfiguration.getCurrentConfig().MoonId
            );
            return false;
        }

        CelestialBody moon = findPrimaryMoon();
        if (moon == null) {
            AdvancedRocketry.logger.warn("[Voidspan] Galacticraft is loaded, but its primary Moon was not found in GalaxyRegistry");
            return false;
        }

        int dimensionId = moon.getDimensionID();

        // Galacticraft registers its Forge dimensions later in the integrated-server
        // startup sequence than AR constructs its celestial model.  GalaxyRegistry is
        // already authoritative here, so bind the ID now and let GC retain Forge
        // ownership when it registers the dimension a few moments later.
        ARConfiguration.getCurrentConfig().MoonId = dimensionId;
        AdvancedRocketry.logger.info(
                "[Voidspan] Bound AR Moon destination to Galacticraft body '{}' | dim {} | externally owned",
                moon.getName(),
                dimensionId
        );
        return true;
    }

    /**
     * Emits a concise runtime assertion after AR has created/loaded its galaxy.
     * This is intentionally diagnostic for the 0.1b bridge milestone.
     */
    public static void verifyPrimaryMoonBinding() {
        int moonId = ARConfiguration.getCurrentConfig().MoonId;
        DimensionManager manager = DimensionManager.getInstance();

        if (moonId == Constants.INVALID_PLANET || !manager.isDimensionCreated(moonId)) {
            AdvancedRocketry.logger.warn(
                    "[Voidspan] Moon bridge verification failed; AR has no celestial properties registered for MoonId {}",
                    moonId
            );
            return;
        }

        DimensionProperties properties = manager.getDimensionProperties(moonId);
        boolean forgeRegistered = net.minecraftforge.common.DimensionManager.isDimensionRegistered(properties.getId());

        if (!forgeRegistered) {
            AdvancedRocketry.logger.warn(
                    "[Voidspan] Moon bridge properties exist for '{}' | dim {} | parent {} | nativeToAR={}, but Forge still has not registered the destination",
                    properties.getName(),
                    properties.getId(),
                    properties.getParentPlanet(),
                    properties.isNativeDimension
            );
            return;
        }

        AdvancedRocketry.logger.info(
                "[Voidspan] Moon bridge active: '{}' | dim {} | parent {} | nativeToAR={} | forgeRegistered=true",
                properties.getName(),
                properties.getId(),
                properties.getParentPlanet(),
                properties.isNativeDimension
        );
    }

    private static CelestialBody findPrimaryMoon() {
        for (CelestialBody body : GalaxyRegistry.getRegisteredMoons().values()) {
            if (PRIMARY_MOON_NAME.equalsIgnoreCase(body.getName())) {
                return body;
            }
        }
        return null;
    }

    public static void logRegisteredBodies() {
        AdvancedRocketry.logger.info("[Voidspan] Galacticraft celestial registry probe starting");

        for (CelestialBody body : GalaxyRegistry.getRegisteredPlanets().values()) {
            AdvancedRocketry.logger.info(
                    "[Voidspan] GC planet: {} | dim {}",
                    body.getName(),
                    body.getDimensionID()
            );
        }

        for (CelestialBody body : GalaxyRegistry.getRegisteredMoons().values()) {
            AdvancedRocketry.logger.info(
                    "[Voidspan] GC moon: {} | dim {}",
                    body.getName(),
                    body.getDimensionID()
            );
        }

        AdvancedRocketry.logger.info("[Voidspan] Galacticraft celestial registry probe complete");
    }
}
