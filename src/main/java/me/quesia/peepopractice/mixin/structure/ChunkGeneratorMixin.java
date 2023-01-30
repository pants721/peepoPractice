package me.quesia.peepopractice.mixin.structure;

import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.properties.StructureProperties;
import net.minecraft.client.MinecraftClient;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ChunkGenerator.class)
public abstract class ChunkGeneratorMixin {
    @Shadow @Final protected BiomeSource biomeSource;
    @Shadow protected abstract void method_28508(ConfiguredStructureFeature<?, ?> configuredStructureFeature, StructureAccessor structureAccessor, Chunk chunk, StructureManager structureManager, long l, ChunkPos chunkPos, Biome biome);
    @Shadow @Final private List<ChunkPos> field_24749;

    private StructureProperties getUniqueStronghold() {
        for (StructureProperties properties : PeepoPractice.CATEGORY.getStructureProperties()) {
            if (properties.isSameStructure(StructureFeature.STRONGHOLD) && properties.isUnique()) {
                return properties;
            }
        }
        return null;
    }

    @Inject(method = "setStructureStarts", at = @At("HEAD"))
    private void customStructureStarts(StructureAccessor structureAccessor, Chunk chunk, StructureManager structureManager, long l, CallbackInfo ci) {
        for (StructureProperties properties : PeepoPractice.CATEGORY.getStructureProperties()) {
            if (!properties.isSameStructure(StructureFeature.STRONGHOLD)) {
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.world != null) {
                    ChunkPos chunkPos = client.world.getChunk(properties.getChunkPos().x, properties.getChunkPos().z, ChunkStatus.BIOMES).getPos();
                    Biome biome = this.biomeSource.getBiomeForNoiseGen((chunkPos.x << 2) + 2, 0, (chunkPos.z << 2) + 2);
                    this.method_28508(properties.getStructure(), structureAccessor, chunk, structureManager, l, chunkPos, biome);
                }
            }
        }
    }

    @Inject(method = "method_28509", at = @At("HEAD"), cancellable = true)
    private void cancelStrongholdLocations(CallbackInfo ci) {
        StructureProperties properties = this.getUniqueStronghold();
        if (properties != null) {
            if (properties.hasChunkPos()) {
                this.field_24749.add(properties.getChunkPos());
            }
            ci.cancel();
        }
    }
}
