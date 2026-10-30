//#if FABRIC
package xyz.yourboykyle.secretroutes.dungeons.rendering;

import java.util.HashMap;
import java.util.Map;

final class BoxMeshCache {
    private final Map<Key, float[]> meshes = new HashMap<>();

    float[] get(float width, float height, float thickness) {
        return get(width, height, width, thickness);
    }

    float[] get(float width, float height, float depth, float thickness) {
        Key key = new Key(
                Float.floatToIntBits(width),
                Float.floatToIntBits(height),
                Float.floatToIntBits(depth),
                Float.floatToIntBits(thickness)
        );
        return meshes.computeIfAbsent(key, ignored -> BoxMeshGeometry.generate(width, height, depth, thickness));
    }

    int size() {
        return meshes.size();
    }

    private record Key(int widthBits, int heightBits, int depthBits, int thicknessBits) {
    }
}
//#endif
