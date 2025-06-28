package com.ninni.species;

import com.ninni.species.client.screen.ScreenShakeEvent;
import com.ninni.species.registry.SpeciesNetwork;
import com.ninni.species.registry.SpeciesPotions;
import com.ninni.species.server.data.CruncherPelletManager;
import com.ninni.species.server.data.GooberGooManager;
import com.ninni.species.server.data.LimpetOreManager;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.UUID;

public class CommonProxy {
    private final GooberGooManager gooberGooManager = new GooberGooManager();
    private final CruncherPelletManager cruncherPelletManager = new CruncherPelletManager();
    private final LimpetOreManager limpetOreManager = new LimpetOreManager();

    public void init() {
    }

    public void commonSetup() {
        SpeciesNetwork.init();
        SpeciesPotions.register();
        addSpeciesDevelopers();
    }

    public void clientSetup() {
    }

    public Level getWorld() {
        return ServerLifecycleHooks.getCurrentServer().overworld();
    }

    public void screenShake(ScreenShakeEvent event) {
    }

    public void harpoonSync(int id) {
    }

    public CruncherPelletManager getCruncherPelletManager() {
        return cruncherPelletManager;
    }

    public GooberGooManager getGooberGooManager() {
        return gooberGooManager;
    }

    public LimpetOreManager getLimpetOreManager() {
        return limpetOreManager;
    }

    public void addSpeciesDevelopers() {
        SpeciesDevelopers.developerUUIDS.put(UUID.fromString("2d173722-de6b-4bb8-b21b-b2843cfe395d"), SpeciesDevelopers.SpeciesDeveloperNames.NINNI);
        SpeciesDevelopers.developerUUIDS.put(UUID.fromString("f1fb25f4-60c4-4e21-b33c-59f0a2daf4b1"), SpeciesDevelopers.SpeciesDeveloperNames.REDA);
        SpeciesDevelopers.developerUUIDS.put(UUID.fromString("4a463319-625c-4b86-a4e7-8b700f023a60"), SpeciesDevelopers.SpeciesDeveloperNames.NOON);
        SpeciesDevelopers.developerUUIDS.put(UUID.fromString("603d30f1-77a1-4b88-b8c5-624a02feabcc"), SpeciesDevelopers.SpeciesDeveloperNames.BORNULHU);
        //SpeciesDevelopers.developerUUIDS.put(UUID.fromString(""), SpeciesDevelopers.SpeciesDeveloperNames.GLADOS); Glados does not own minecraft
        SpeciesDevelopers.developerUUIDS.put(UUID.fromString("3ff497ed-71f0-453a-a869-4fc17be298ff"), SpeciesDevelopers.SpeciesDeveloperNames.YAPETTO);
        SpeciesDevelopers.developerUUIDS.put(UUID.fromString("81499a26-ba39-430e-8009-29ee87351c20"), SpeciesDevelopers.SpeciesDeveloperNames.ORCINUS);
        SpeciesDevelopers.developerUUIDS.put(UUID.fromString("0c22615f-a189-4f4e-85ae-79fd80c353c8"), SpeciesDevelopers.SpeciesDeveloperNames.VAKY);
        SpeciesDevelopers.developerUUIDS.put(UUID.fromString("aca529a2-1166-41aa-b304-209f06831998"), SpeciesDevelopers.SpeciesDeveloperNames.TAZZ);
        SpeciesDevelopers.developerUUIDS.put(UUID.fromString("f6dffbc0-746a-41fe-b0c1-20f9a596795a"), SpeciesDevelopers.SpeciesDeveloperNames.BUNTEN);
        SpeciesDevelopers.developerUUIDS.put(UUID.fromString("4f00e7fc-b325-4f16-88cf-80cd78733646"), SpeciesDevelopers.SpeciesDeveloperNames.EXCLAIM);
    }
}
