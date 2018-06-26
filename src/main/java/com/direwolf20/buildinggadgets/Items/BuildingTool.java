package com.direwolf20.buildinggadgets.Items;

import com.direwolf20.buildinggadgets.BuildingGadgets;
import com.direwolf20.buildinggadgets.Entities.BlockBuildEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;


public class BuildingTool extends Item {
    public BuildingTool() {
        setRegistryName("buildingtool");        // The unique name (within your mod) that identifies this item
        setUnlocalizedName(BuildingGadgets.MODID + ".buildingtool");     // Used for localization (en_US.lang)
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        player.setActiveHand(hand);
        RayTraceResult lookingAt = player.rayTrace(20, 1.0F);
        //System.out.println(lookingAt.sideHit);
        if (!world.isRemote) {
            if (world.getBlockState(lookingAt.getBlockPos()) != Blocks.AIR.getDefaultState()) {
                buildToMe(world, player, lookingAt.getBlockPos(),lookingAt.sideHit);
                //world.spawnEntity(new BlockBuildEntity(world, lookingAt.getBlockPos().up(), player,Blocks.COBBLESTONE.getDefaultState()));
            }
        }
        else {

        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
    }

    public boolean buildToMe(World world, EntityPlayer player, BlockPos startBlock, EnumFacing sideHit) {
        BlockPos playerPos = player.getPosition();
        IBlockState cobbleBlock = Blocks.COBBLESTONE.getDefaultState();
        BlockPos changePos;
        if (sideHit == EnumFacing.SOUTH) {
            for (int i = startBlock.getZ(); i <= playerPos.getZ(); i++) {
                changePos = new BlockPos(startBlock.getX(), startBlock.getY(), i);
                placeBlock(world, player, changePos, cobbleBlock);
            }
        }
        else if (sideHit == EnumFacing.NORTH) {
            for (int i = startBlock.getZ(); i >= playerPos.getZ(); i--) {
                changePos = new BlockPos(startBlock.getX(), startBlock.getY(), i);
                placeBlock(world, player, changePos, cobbleBlock);
            }
        }
        else if (sideHit == EnumFacing.EAST) {
            for (int i = startBlock.getX(); i <= playerPos.getX(); i++) {
                changePos = new BlockPos(i, startBlock.getY(), startBlock.getZ());
                placeBlock(world, player, changePos, cobbleBlock);
            }
        }
        else if (sideHit == EnumFacing.WEST) {
            for (int i = startBlock.getX(); i >= playerPos.getX(); i--) {
                changePos = new BlockPos(i, startBlock.getY(), startBlock.getZ());
                placeBlock(world, player, changePos, cobbleBlock);
            }
        }
        else if (sideHit == EnumFacing.UP) {
            for (int i = startBlock.getY(); i <= playerPos.getY(); i++) {
                changePos = new BlockPos(startBlock.getX(), i, startBlock.getZ());
                placeBlock(world, player, changePos, cobbleBlock);
            }
        }
        else if (sideHit == EnumFacing.DOWN) {
            for (int i = startBlock.getY(); i >= playerPos.getY(); i--) {
                changePos = new BlockPos(startBlock.getX(), i, startBlock.getZ());
                placeBlock(world, player, changePos, cobbleBlock);
            }
        }
        return true;
    }

    public boolean placeBlock(World world, EntityPlayer player, BlockPos pos, IBlockState setBlock) {
        if (world.getBlockState(pos).getBlock().isReplaceable(world,pos)) {
            world.spawnEntity(new BlockBuildEntity(world, pos, player, setBlock,false));
        }
        return true;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 20;
    }


    @SideOnly(Side.CLIENT)
    public void renderOverlay(RenderWorldLastEvent evt, EntityPlayer player, ItemStack buildingTool) {
        RayTraceResult lookingAt = player.rayTrace(20, 1.0F);
        if (lookingAt != null) {
            World world = player.world;
            int x = lookingAt.getBlockPos().getX();
            int y = lookingAt.getBlockPos().getY();
            int z = lookingAt.getBlockPos().getZ();
            IBlockState block = world.getBlockState(lookingAt.getBlockPos());
            if (block != null && block != Blocks.AIR.getDefaultState()) {
                renderOutlines(evt, player, lookingAt.getBlockPos().up(), 200, 230, 180);
            }
        }
    }

    protected static void renderOutlines(RenderWorldLastEvent evt, EntityPlayer p, BlockPos pos, int r, int g, int b) {
        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();

        double doubleX = p.lastTickPosX + (p.posX - p.lastTickPosX) * evt.getPartialTicks();
        double doubleY = p.lastTickPosY + (p.posY - p.lastTickPosY) * evt.getPartialTicks();
        double doubleZ = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * evt.getPartialTicks();

        double minX = pos.getX();
        double minY = pos.getY();
        double minZ = pos.getZ();
        double maxX = pos.getX()+1;
        double maxY = pos.getY()+1;
        double maxZ = pos.getZ()+1;
        float red = 0f;
        float green = 0f;
        float blue = 0f;
        float alpha = 0.25f;

        IBlockState renderBlockState = Blocks.COBBLESTONE.getDefaultState();
        if (renderBlockState == null) {
            renderBlockState = Blocks.COBBLESTONE.getDefaultState();
        }

        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);


        GlStateManager.translate(-doubleX,-doubleY,-doubleZ);


        Tessellator t = Tessellator.getInstance();
        BufferBuilder bufferBuilder = t.getBuffer();
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);

        //down
        bufferBuilder.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();

        //up
        bufferBuilder.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();

        //north
        bufferBuilder.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();

        //south
        bufferBuilder.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();

        //east
        bufferBuilder.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();

        //west
        bufferBuilder.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        t.draw();


        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();


        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.translate(-doubleX,-doubleY,-doubleZ);
        GlStateManager.translate(pos.getX(),pos.getY(),pos.getZ());

        GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.scale(1.0f,1.0f,1.0f);
        GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
        blockrendererdispatcher.renderBlockBrightness(renderBlockState, 0.75f);


        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();

//-------------------------------------------------------------------------------------------------------



    }

    private static void renderBlockOutline(Tessellator tessellator, float mx, float my, float mz, float o) {

    }

}