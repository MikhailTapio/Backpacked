package com.mrcrayfish.backpacked.common.backpack;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.ClientHandler;
import com.mrcrayfish.backpacked.common.Backpack;
import com.mrcrayfish.backpacked.common.IProgressTracker;
import com.mrcrayfish.backpacked.common.tracker.UniqueCraftingProgressTracker;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class CogwheelBackpack extends Backpack
{
    public static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "cogwheel");

    public CogwheelBackpack()
    {
        super(ID);
    }

    @Override
    public Supplier<Object> getModelSupplier()
    {
        return ClientHandler.getModelInstances()::getCogwheel;
    }

    @Nullable
    @Override
    protected IProgressTracker createProgressTracker()
    {
        return new UniqueCraftingProgressTracker(30, stack -> Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(stack.getItem())).getNamespace().equals("create"));
    }
}
