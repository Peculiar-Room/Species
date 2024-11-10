package com.ninni.species;

import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.UUID;

import static com.ninni.species.Species.MOD_ID;

public class SpeciesDevelopers {
    public static HashMap<UUID, SpeciesDeveloperNames> developerUUIDS = new HashMap<>();
    public static final ResourceLocation NINNI_TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/capes/ninni.png");
    public static final ResourceLocation REDA_TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/capes/floofhips.png");
    public static final ResourceLocation NOON_TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/capes/noonyeyz.png");
    public static final ResourceLocation BORNULHU_TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/capes/bornulhu.png");
    public static final ResourceLocation GLADOS_TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/capes/glados_edition.png");
    public static final ResourceLocation CONTRIBUTOR_TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/capes/contributor.png");
    public static final ResourceLocation GUEST_ARTIST_TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/capes/guest_artist.png");


    public enum SpeciesDeveloperNames {
        NINNI("Ninni", ContributionLevel.DEVELOPER, ChatFormatting.DARK_AQUA, NINNI_TEXTURE),
        REDA("Floofhips", ContributionLevel.DEVELOPER, ChatFormatting.RED, REDA_TEXTURE),
        NOON("Noonyeyz", ContributionLevel.DEVELOPER, ChatFormatting.AQUA, NOON_TEXTURE),
        BORNULHU("Bornulhu", ContributionLevel.DEVELOPER, ChatFormatting.GREEN, BORNULHU_TEXTURE),
        GLADOS("GLaDOS edition", ContributionLevel.DEVELOPER, ChatFormatting.LIGHT_PURPLE, GLADOS_TEXTURE),
        ORCINUS("Orcinus", ContributionLevel.CONTRIBUTOR, ChatFormatting.DARK_GRAY, CONTRIBUTOR_TEXTURE),
        VAKY("VakyPanda", ContributionLevel.CONTRIBUTOR, ChatFormatting.DARK_GRAY, CONTRIBUTOR_TEXTURE),
        TAZZ("Tazz", ContributionLevel.CONTRIBUTOR, ChatFormatting.DARK_GRAY, CONTRIBUTOR_TEXTURE),
        BUNTEN("lunarbunten", ContributionLevel.COMPOSER, ChatFormatting.DARK_GRAY, CONTRIBUTOR_TEXTURE),
        EXCLAIM("Exclaim!", ContributionLevel.COMPOSER, ChatFormatting.DARK_GRAY, CONTRIBUTOR_TEXTURE);

        private final String name;
        private final ContributionLevel contributionLevel;
        private final ChatFormatting formatting;
        private final ResourceLocation capeTexture;

        SpeciesDeveloperNames(String name, ContributionLevel contributionLevel, ChatFormatting formatting, ResourceLocation capeTexture) {
            this.name = name;
            this.contributionLevel = contributionLevel;
            this.formatting = formatting;
            this.capeTexture = capeTexture;
        }

        public String getName() {
            return name;
        }
        public ContributionLevel getContributionLevel() {
            return contributionLevel;
        }
        public ChatFormatting getFormatting() {
            return formatting;
        }
        public ResourceLocation getCapeTexture() {
            return capeTexture;
        }

        public enum ContributionLevel {
            DEVELOPER(" "),
            GUEST_ARTIST("(Guest Artist) "),
            COMPOSER("(Composer) "),
            CONTRIBUTOR("(Contributor) ");

            private final String name;

            ContributionLevel(String name) {
                this.name = name;
            }

            public String getContributionLevelName() {
                return name;
            }
        }
    }
}
