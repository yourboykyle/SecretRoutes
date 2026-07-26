//#if FABRIC
package xyz.yourboykyle.secretroutes.dungeons.rendering;

final class BoxMeshGeometry {
    static final int VERTEX_COUNT = 192;

    private BoxMeshGeometry() {
    }

    static float[] generate(float width, float height, float thickness) {
        FloatMeshSink sink = new FloatMeshSink(VERTEX_COUNT * 3);
        emitUnified(width, height, thickness, sink);
        return sink.finish();
    }

    private static void emitUnified(float width, float height, float thickness, VertexSink sink) {
        float h = thickness / 2.0f;
        float x0 = -h;
        float x1 = h;
        float x2 = width - h;
        float x3 = width + h;
        float y0 = -h;
        float y1 = h;
        float y2 = height - h;
        float y3 = height + h;
        float z0 = -h;
        float z1 = h;
        float z2 = width - h;
        float z3 = width + h;

        addFrameX(sink, x0, false, y0, y1, y2, y3, z0, z1, z2, z3);
        addFrameX(sink, x3, true, y0, y1, y2, y3, z0, z1, z2, z3);
        addFrameY(sink, y0, false, x0, x1, x2, x3, z0, z1, z2, z3);
        addFrameY(sink, y3, true, x0, x1, x2, x3, z0, z1, z2, z3);
        addFrameZ(sink, z0, false, x0, x1, x2, x3, y0, y1, y2, y3);
        addFrameZ(sink, z3, true, x0, x1, x2, x3, y0, y1, y2, y3);

        addInnerX(sink, x1, true, y0, y1, y2, y3, z0, z1, z2, z3);
        addInnerX(sink, x2, false, y0, y1, y2, y3, z0, z1, z2, z3);
        addInnerY(sink, y1, true, x0, x1, x2, x3, z0, z1, z2, z3);
        addInnerY(sink, y2, false, x0, x1, x2, x3, z0, z1, z2, z3);
        addInnerZ(sink, z1, true, x0, x1, x2, x3, y0, y1, y2, y3);
        addInnerZ(sink, z2, false, x0, x1, x2, x3, y0, y1, y2, y3);
    }

    private static void addFrameX(VertexSink sink, float x, boolean positiveNormal, float y0, float y1, float y2, float y3, float z0, float z1, float z2, float z3) {
        quadX(sink, x, y0, z0, y1, z3, positiveNormal);
        quadX(sink, x, y2, z0, y3, z3, positiveNormal);
        quadX(sink, x, y1, z0, y2, z1, positiveNormal);
        quadX(sink, x, y1, z2, y2, z3, positiveNormal);
    }

    private static void addInnerX(VertexSink sink, float x, boolean positiveNormal, float y0, float y1, float y2, float y3, float z0, float z1, float z2, float z3) {
        quadX(sink, x, y0, z1, y1, z2, positiveNormal);
        quadX(sink, x, y2, z1, y3, z2, positiveNormal);
        quadX(sink, x, y1, z0, y2, z1, positiveNormal);
        quadX(sink, x, y1, z2, y2, z3, positiveNormal);
    }

    private static void addFrameY(VertexSink sink, float y, boolean positiveNormal, float x0, float x1, float x2, float x3, float z0, float z1, float z2, float z3) {
        quadY(sink, y, x0, z0, x1, z3, positiveNormal);
        quadY(sink, y, x2, z0, x3, z3, positiveNormal);
        quadY(sink, y, x1, z0, x2, z1, positiveNormal);
        quadY(sink, y, x1, z2, x2, z3, positiveNormal);
    }

    private static void addInnerY(VertexSink sink, float y, boolean positiveNormal, float x0, float x1, float x2, float x3, float z0, float z1, float z2, float z3) {
        quadY(sink, y, x0, z1, x1, z2, positiveNormal);
        quadY(sink, y, x2, z1, x3, z2, positiveNormal);
        quadY(sink, y, x1, z0, x2, z1, positiveNormal);
        quadY(sink, y, x1, z2, x2, z3, positiveNormal);
    }

    private static void addFrameZ(VertexSink sink, float z, boolean positiveNormal, float x0, float x1, float x2, float x3, float y0, float y1, float y2, float y3) {
        quadZ(sink, z, x0, y0, x1, y3, positiveNormal);
        quadZ(sink, z, x2, y0, x3, y3, positiveNormal);
        quadZ(sink, z, x1, y0, x2, y1, positiveNormal);
        quadZ(sink, z, x1, y2, x2, y3, positiveNormal);
    }

    private static void addInnerZ(VertexSink sink, float z, boolean positiveNormal, float x0, float x1, float x2, float x3, float y0, float y1, float y2, float y3) {
        quadZ(sink, z, x0, y1, x1, y2, positiveNormal);
        quadZ(sink, z, x2, y1, x3, y2, positiveNormal);
        quadZ(sink, z, x1, y0, x2, y1, positiveNormal);
        quadZ(sink, z, x1, y2, x2, y3, positiveNormal);
    }

    private static void quadX(VertexSink sink, float x, float y1, float z1, float y2, float z2, boolean positiveNormal) {
        if (positiveNormal) {
            sink.vertex(x, y1, z2);
            sink.vertex(x, y1, z1);
            sink.vertex(x, y2, z1);
            sink.vertex(x, y2, z2);
        } else {
            sink.vertex(x, y1, z1);
            sink.vertex(x, y1, z2);
            sink.vertex(x, y2, z2);
            sink.vertex(x, y2, z1);
        }
    }

    private static void quadY(VertexSink sink, float y, float x1, float z1, float x2, float z2, boolean positiveNormal) {
        if (positiveNormal) {
            sink.vertex(x1, y, z1);
            sink.vertex(x1, y, z2);
            sink.vertex(x2, y, z2);
            sink.vertex(x2, y, z1);
        } else {
            sink.vertex(x1, y, z2);
            sink.vertex(x1, y, z1);
            sink.vertex(x2, y, z1);
            sink.vertex(x2, y, z2);
        }
    }

    private static void quadZ(VertexSink sink, float z, float x1, float y1, float x2, float y2, boolean positiveNormal) {
        if (positiveNormal) {
            sink.vertex(x2, y1, z);
            sink.vertex(x2, y2, z);
            sink.vertex(x1, y2, z);
            sink.vertex(x1, y1, z);
        } else {
            sink.vertex(x1, y1, z);
            sink.vertex(x1, y2, z);
            sink.vertex(x2, y2, z);
            sink.vertex(x2, y1, z);
        }
    }

    @FunctionalInterface
    private interface VertexSink {
        void vertex(float x, float y, float z);
    }

    private static final class FloatMeshSink implements VertexSink {
        private final float[] vertices;
        private int index;

        private FloatMeshSink(int floatCount) {
            this.vertices = new float[floatCount];
        }

        @Override
        public void vertex(float x, float y, float z) {
            vertices[index++] = x;
            vertices[index++] = y;
            vertices[index++] = z;
        }

        private float[] finish() {
            if (index != vertices.length) {
                throw new IllegalStateException("Expected " + vertices.length + " box-mesh floats but wrote " + index);
            }
            return vertices;
        }
    }
}
//#endif
