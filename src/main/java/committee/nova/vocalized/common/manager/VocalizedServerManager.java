package committee.nova.vocalized.common.manager;

import com.mojang.datafixers.util.Either;
import committee.nova.vocalized.api.IVocal;
import committee.nova.vocalized.api.IVoiceMessage;
import committee.nova.vocalized.common.network.handler.NetworkHandler;
import committee.nova.vocalized.common.network.msg.S2CVocalizedMsgEntityBound;
import committee.nova.vocalized.common.network.msg.S2CVocalizedMsgPosBound;
import committee.nova.vocalized.common.phys.Vec3WithDim;
import committee.nova.vocalized.common.voice.VoiceContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

public class VocalizedServerManager {
    public static void sendVoiceMsg(ServerPlayer player, IVoiceMessage msg, VoiceContext context, Component... args) {
        sendVoiceMsgPlayerBound(
                player,
                msg.getId(), msg.getType().getId(),
                context, args
        );
    }

    public static void sendVoiceMsgWithoutText(ServerPlayer player, IVoiceMessage msg, VoiceContext context) {
        sendVoiceMsgPlayerBoundWithoutText(
                player,
                msg.getId(), msg.getType().getId(),
                context
        );
    }

    public static void sendVoiceMsg(
            Level level, Vec3 pos, Component senderName,
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            IVoiceMessage msg, VoiceContext context, Component... args
    ) {
        sendVoiceMsgPosBound(
                level, pos,
                senderName,
                voiceId, defaultVoiceId,
                msg.getId(), msg.getType().getId(),
                context, args
        );
    }

    public static void sendVoiceMsgWithoutText(
            Level level, Vec3 pos,
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            IVoiceMessage msg, VoiceContext context
    ) {
        sendVoiceMsgPosBoundWithoutText(
                level, pos,
                voiceId, defaultVoiceId,
                msg.getId(), msg.getType().getId(),
                context
        );
    }

    public static void sendVoiceMsgPlayerBound(
            ServerPlayer player,
            ResourceLocation msgId, ResourceLocation msgTypeId,
            VoiceContext context, Component... args
    ) {
        final IVocal vocal = (IVocal) player;
        sendVoiceMsgEntityBound(
                player,
                vocal.vocalized$getVoiceId(), vocal.vocalized$getDefaultVoiceId(),
                msgId, msgTypeId,
                context, args
        );
    }

    public static void sendVoiceMsgPlayerBoundWithoutText(
            ServerPlayer player,
            ResourceLocation msgId, ResourceLocation msgTypeId,
            VoiceContext context
    ) {
        final IVocal vocal = (IVocal) player;
        sendVoiceMsgEntityBoundWithoutText(
                player,
                vocal.vocalized$getVoiceId(), vocal.vocalized$getDefaultVoiceId(),
                msgId, msgTypeId,
                context
        );
    }

    public static void sendVoiceMsgEntityBound(
            Entity entity,
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation msgId, ResourceLocation msgTypeId,
            VoiceContext context, Component... args
    ) {
        final S2CVocalizedMsgEntityBound p = context.getEffect().overDimension() ?
                new S2CVocalizedMsgEntityBound(
                        voiceId, defaultVoiceId,
                        msgId, msgTypeId,
                        entity.getName(), context.getEffect(), args
                ) :
                new S2CVocalizedMsgEntityBound(
                        voiceId, defaultVoiceId,
                        msgId, msgTypeId,
                        entity.getName(), context.getEffect(), entity, args
                );
        for (final PacketDistributor.PacketTarget target : context.getTarget().determine(Either.left(entity))) {
            NetworkHandler.getInstance().channel.send(p, target);
        }
    }

    public static void sendVoiceMsgEntityBoundWithoutText(
            Entity entity,
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation msgId, ResourceLocation msgTypeId,
            VoiceContext context
    ) {
        final S2CVocalizedMsgEntityBound p = context.getEffect().overDimension() ?
                new S2CVocalizedMsgEntityBound(
                        voiceId, defaultVoiceId,
                        msgId, msgTypeId,
                        context.getEffect()
                ) :
                new S2CVocalizedMsgEntityBound(
                        voiceId, defaultVoiceId,
                        msgId, msgTypeId,
                        context.getEffect(), entity
                );
        for (final PacketDistributor.PacketTarget target : context.getTarget().determine(Either.left(entity))) {
            NetworkHandler.getInstance().channel.send(p, target);
        }
    }

    public static void sendVoiceMsgPosBound(
            Level level, Vec3 pos, Component senderName,
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation msgId, ResourceLocation msgTypeId,
            VoiceContext context, Component... args
    ) {
        final S2CVocalizedMsgPosBound p = new S2CVocalizedMsgPosBound(
                voiceId, defaultVoiceId,
                msgId, msgTypeId,
                senderName, level.dimension(), pos,
                args
        );
        for (final PacketDistributor.PacketTarget target : context.getTarget().determine(Either.right(Vec3WithDim.create(level, pos)))) {
            NetworkHandler.getInstance().channel.send(p, target);
        }
    }

    public static void sendVoiceMsgPosBoundWithoutText(
            Level level, Vec3 pos,
            ResourceLocation voiceId, ResourceLocation defaultVoiceId,
            ResourceLocation msgId, ResourceLocation msgTypeId,
            VoiceContext context
    ) {
        final S2CVocalizedMsgPosBound p = new S2CVocalizedMsgPosBound(
                voiceId, defaultVoiceId,
                msgId, msgTypeId, level.dimension(), pos
        );
        for (final PacketDistributor.PacketTarget target : context.getTarget().determine(Either.right(Vec3WithDim.create(level, pos)))) {
            NetworkHandler.getInstance().channel.send(p, target);
        }
    }
}
