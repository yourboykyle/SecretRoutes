package xyz.yourboykyle.secretroutes.dungeons.rendering;

public final class BoxMeshGeometryTest {
    private BoxMeshGeometryTest() {
    }

    public static void main(String[] args) {
        verifyGeometry();
        verifyCacheReuse();
        System.out.println("Unified box-mesh geometry tests passed");
    }

    private static void verifyGeometry() {
        float width = 1.0f;
        float height = 1.0f;
        float thickness = 0.07f;
        float[] mesh = BoxMeshGeometry.generate(width, height, thickness);
        int expectedFloats = BoxMeshGeometry.VERTEX_COUNT * 3;

        if (mesh.length != expectedFloats) {
            throw new AssertionError("Generated " + mesh.length + " floats, expected " + expectedFloats);
        }
        for (float coordinate : mesh) {
            if (!Float.isFinite(coordinate)) {
                throw new AssertionError("Generated a non-finite coordinate");
            }
        }
        for (int offset = 0; offset < mesh.length; offset += 12) {
            if (quadIsDegenerate(mesh, offset)) {
                throw new AssertionError("Generated a degenerate quad at float index " + offset);
            }
        }
        verifyWinding(mesh, width, height, thickness);
    }

    private static void verifyCacheReuse() {
        BoxMeshCache cache = new BoxMeshCache();
        float[] first = cache.get(1.0f, 1.0f, 0.07f);
        float[] second = cache.get(1.0f, 1.0f, 0.07f);
        if (first != second || cache.size() != 1) {
            throw new AssertionError("Box mesh cache did not reuse the generated mesh");
        }
    }

    private static boolean quadIsDegenerate(float[] mesh, int offset) {
        float ax = mesh[offset + 3] - mesh[offset];
        float ay = mesh[offset + 4] - mesh[offset + 1];
        float az = mesh[offset + 5] - mesh[offset + 2];
        float bx = mesh[offset + 6] - mesh[offset];
        float by = mesh[offset + 7] - mesh[offset + 1];
        float bz = mesh[offset + 8] - mesh[offset + 2];

        float crossX = ay * bz - az * by;
        float crossY = az * bx - ax * bz;
        float crossZ = ax * by - ay * bx;
        return crossX * crossX + crossY * crossY + crossZ * crossZ < 1.0e-12f;
    }

    private static void verifyWinding(float[] mesh, float width, float height, float thickness) {
        float halfThickness = thickness / 2.0f;
        for (int offset = 0; offset < mesh.length; offset += 12) {
            float ax = mesh[offset + 3] - mesh[offset];
            float ay = mesh[offset + 4] - mesh[offset + 1];
            float az = mesh[offset + 5] - mesh[offset + 2];
            float bx = mesh[offset + 6] - mesh[offset];
            float by = mesh[offset + 7] - mesh[offset + 1];
            float bz = mesh[offset + 8] - mesh[offset + 2];
            float normalX = ay * bz - az * by;
            float normalY = az * bx - ax * bz;
            float normalZ = ax * by - ay * bx;

            if (sameCoordinate(mesh, offset, 0)) {
                assertNormalDirection("X", mesh[offset], normalX, width, halfThickness, offset);
            } else if (sameCoordinate(mesh, offset, 1)) {
                assertNormalDirection("Y", mesh[offset + 1], normalY, height, halfThickness, offset);
            } else if (sameCoordinate(mesh, offset, 2)) {
                assertNormalDirection("Z", mesh[offset + 2], normalZ, width, halfThickness, offset);
            } else {
                throw new AssertionError("Mesh quad is not axis-aligned at float index " + offset);
            }
        }
    }

    private static boolean sameCoordinate(float[] mesh, int offset, int axis) {
        float coordinate = mesh[offset + axis];
        return Float.compare(mesh[offset + 3 + axis], coordinate) == 0
                && Float.compare(mesh[offset + 6 + axis], coordinate) == 0
                && Float.compare(mesh[offset + 9 + axis], coordinate) == 0;
    }

    private static void assertNormalDirection(String axis, float plane, float normal, float dimension, float halfThickness, int offset) {
        float expectedSign;
        if (Float.compare(plane, -halfThickness) == 0 || Float.compare(plane, dimension - halfThickness) == 0) {
            expectedSign = -1.0f;
        } else if (Float.compare(plane, halfThickness) == 0 || Float.compare(plane, dimension + halfThickness) == 0) {
            expectedSign = 1.0f;
        } else {
            throw new AssertionError("Unexpected " + axis + " plane " + plane + " at float index " + offset);
        }
        if (normal * expectedSign <= 0.0f) {
            throw new AssertionError(axis + " face has inward winding at float index " + offset);
        }
    }
}
