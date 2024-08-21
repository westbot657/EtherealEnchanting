package com.westbot.ethereal_enchanting.entity;

import com.westbot.ethereal_enchanting.networking.SyncTrailPayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CelestialTrailEntity extends Entity {

    public static class LineSegment {
        public Vec3d start;
        public Vec3d end;
        public Quaternionf rotation;
        public double length;

        public LineSegment(Vec3d start, Vec3d end) {
            this.start = start;
            this.end = end;
            this.length = start.distanceTo(end);
            this.rotation = new Quaternionf().rotationTo(new Vector3f(0, 0, 0), end.subtract(start).toVector3f());

        }
        public String toString() {
            return start + " -> " + end + " (dist: " + length + ")";
        }
    }

    public final List<Vec3d> corners;
    private UUID itemEntityUuid;
    public boolean inactive;
    private Vec3d expectedItemPosition;
    private int lifetime;
    public boolean synced;
    public int syncDelay;

    public List<LineSegment> cachedLineSegments;

    public CelestialTrailEntity(EntityType<? extends CelestialTrailEntity> entityType, World world) {
        super(entityType, world);
        corners = new ArrayList<>();
        inactive = world.isClient;
        cachedLineSegments = new ArrayList<>();
        expectedItemPosition = this.getPos();
        synced = false;
        syncDelay = 5;
    }

    public void trackEntity(Entity entity) {
        itemEntityUuid = entity.getUuid();
        expectedItemPosition = entity.getPos();
    }

    public void addCorner(Vec3d corner) {
        corners.add(corner);
        if (corners.size() > 6) {
            corners.removeFirst();
        }
    }

    public void sync(NbtCompound corners) {
        this.readNbt(corners);
        this.getLineSegments();
    }

    public void clearCorners() {
        corners.clear();
    }

    public Vec3d getLastCorner() {
        if (corners.isEmpty()) {
            return this.getPos();
        }
        return corners.getLast();
    }

    public void getLineSegments() {
        List<Vec3d> corners_temp = new ArrayList<>(List.of(this.getPos()));
        corners_temp.addAll(this.corners);


        List<LineSegment> segments = new ArrayList<>();
        for (int i = 0; i < corners_temp.size()-1; i++) {
            Vec3d start = corners_temp.get(i);
            Vec3d end = corners_temp.get(i+1);
            LineSegment lineSegment = new LineSegment(start, end);
            segments.add(lineSegment);
        }
        cachedLineSegments = segments;

    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        corners.clear();
        for (NbtElement compound : nbt.getList("corners", NbtElement.COMPOUND_TYPE)) {
            if (compound instanceof NbtCompound) {
                corners.add(new Vec3d(((NbtCompound) compound).getDouble("x"), ((NbtCompound) compound).getDouble("y"), ((NbtCompound) compound).getDouble("z")));
            }
        }

        itemEntityUuid = nbt.getUuid("itemEntityUuid");
        NbtCompound compound = nbt.getCompound("expectedItemPosition");
        expectedItemPosition = new Vec3d(compound.getDouble("x"), compound.getDouble("y"), compound.getDouble("z"));
        inactive = nbt.getBoolean("inactive");
        getLineSegments();
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        NbtList list = new NbtList();

        for (Vec3d corner : corners) {
            NbtCompound compound = new NbtCompound();
            compound.putDouble("x", corner.getX());
            compound.putDouble("y", corner.getY());
            compound.putDouble("z", corner.getZ());
            list.add(compound);
        }

        nbt.put("corners", list);
        nbt.putUuid("itemEntityUuid", itemEntityUuid);
        NbtCompound compound = new NbtCompound();
        compound.putDouble("x", expectedItemPosition.x);
        compound.putDouble("y", expectedItemPosition.y);
        compound.putDouble("z", expectedItemPosition.z);
        nbt.put("expectedItemPosition", compound);
        nbt.putBoolean("inactive", inactive);
    }

    @Override
    public void tick() {

        if (getWorld().isClient) {
            if (corners.isEmpty()) {
                if (syncDelay > 0) {
                    syncDelay--;
                } else {
                    synced = false;
                }
            }

        } else {
            if (lifetime < 20) {
                lifetime++;
            }

            Entity tracking = ((ServerWorld) getWorld()).getEntity(itemEntityUuid);
            if ((tracking == null && getWorld().isChunkLoaded((int) (expectedItemPosition.x/16), (int) (expectedItemPosition.z/16)) && lifetime == 20) || getPos().distanceTo(expectedItemPosition) > 128) {
                discard();
                return;
            }
            if (tracking == null) {
                return;
            }



            if (inactive) return;

            Vec3d pos = tracking.getPos();

            expectedItemPosition = pos;

            if (corners.size() == 5 && pos.distanceTo(getLastCorner()) > 0.25) {
                CelestialTrailEntity newEntity = new CelestialTrailEntity(ModEntities.CELESTIAL_TRAIL_TYPE, getWorld());
                newEntity.trackEntity(tracking);

                newEntity.setPos(pos.x, pos.y, pos.z);
                getWorld().spawnEntity(newEntity);
                addCorner(pos);
                inactive = true;
                getLineSegments();
                for (ServerPlayerEntity player : PlayerLookup.tracking(tracking)) {
                    ServerPlayNetworking.send(player, new SyncTrailPayload(this.getUuid(), this.writeNbt(new NbtCompound())));
                }

            } else if (pos.distanceTo(getLastCorner()) > 0.25) {
                addCorner(pos);
                getLineSegments();
                for (ServerPlayerEntity player : PlayerLookup.tracking(tracking)) {
                    ServerPlayNetworking.send(player, new SyncTrailPayload(this.getUuid(), this.writeNbt(new NbtCompound())));
                }
            }
        }
    }


}
