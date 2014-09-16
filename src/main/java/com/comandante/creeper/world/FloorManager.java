package com.comandante.creeper.world;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;

public class FloorManager {

    private final Map<Integer, String> floorIdLookup = Maps.newConcurrentMap();

    public void addFloor(Integer fid, String s) {
        floorIdLookup.put(fid, s);
    }

    public String getName(Integer fid) {
        return floorIdLookup.get(fid);
    }

    public Integer getId(String name) {
        for (Map.Entry<Integer, String> next : floorIdLookup.entrySet()) {
            if (next.getValue().equals(name)) {
                return next.getKey();
            }
        }
        return 0;
    }

    public Set<Integer> getFloorIds() {
        Set<Integer> ids = Sets.newHashSet();
        for (Map.Entry<Integer, String> next : floorIdLookup.entrySet()) {
            ids.add(next.getKey());
        }
        return ids;
    }

    public boolean doesFloorIdExist(Integer floorId) {
        Set<Integer> ids = getFloorIds();
        for (Integer next : ids) {
            if (next.equals(floorId)) {
                return true;
            }
        }
        return false;
    }
}
