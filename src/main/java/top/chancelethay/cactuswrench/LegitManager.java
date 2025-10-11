package top.chancelethay.cactuswrench;

import com.google.common.collect.ImmutableSet;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.FaceAttachable;
import org.bukkit.block.data.type.*;

import java.util.Set;

public class LegitManager {
    private static final Set<Class<? extends BlockData>> DISALLOWED_BLOCKDATAS = ImmutableSet.of(
            // multi-block
            Bed.class,
            PistonHead.class,

            // attached to wall
            AmethystCluster.class,
            RedstoneWallTorch.class,
            Cocoa.class,
            CoralWallFan.class,
            TripwireHook.class,
            WallSign.class,
            Ladder.class,

            // misalignment
            BigDripleaf.class,

            // non-survival
            EndPortalFrame.class,
            Vault.class,
            CommandBlock.class
    );


    public static boolean legitCheck(Block block) {

        BlockData blockData = block.getBlockData();

        for (Class<? extends BlockData> disallowedClass : DISALLOWED_BLOCKDATAS) {
            if (disallowedClass.isInstance(blockData)) {
                return false;
            }
        }

        if (blockData instanceof Piston) {
            Piston piston = (Piston) blockData;
            if (piston.isExtended()) {
                return false;
            }
        }

        if (blockData instanceof Switch) {
            if (((Switch) blockData).getAttachedFace() == FaceAttachable.AttachedFace.WALL) {
                return false;
            }
        }

        if (blockData instanceof Bell) {
            Bell.Attachment attachment = ((Bell) blockData).getAttachment();
            if (attachment == Bell.Attachment.DOUBLE_WALL || attachment == Bell.Attachment.SINGLE_WALL) {
                return false;
            }
        }

        return true;
    }
}
