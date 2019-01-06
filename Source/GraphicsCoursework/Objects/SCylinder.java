package Objects;

public class SCylinder extends SObject {

    private float height;
    private float radius;
    private int slices;
    private static final float sqrt2 = (float)Math.sqrt(2);


    public SCylinder() {
        init();
        update();
    }

    public SCylinder(float height, float radius) {
        init();
        this.height = height;
        this.radius = radius;
        update();
    }

    public SCylinder(float height, float radius, int slices) {
        this.height = height;
        this.radius = radius;
        this.slices = slices;
        update();
    }

    // Default values
    private void init(){
        this.height = 1;
        this.radius = 1;
        this.slices = 20;
    }

    @Override
    protected void genData() {
        double deltaAngle = PI * 2 / slices;

        // Generate vertices coordinates, normal values, and texture coordinates
        numVertices = slices * 4;
        vertices = new float[numVertices * 3];
        normals = new float[numVertices * 3];
        textures = new float[numVertices * 2];

        for (int i = 0; i < slices; i++){
            double angle = deltaAngle * i;
            float xComponent = (float)Math.cos(angle);
            float zComponent = (float)Math.sin(angle);

            // VERTICES FOR WALLS

            int kBot = i * 3; // Index of bottom vertex
            int kTop = (i  + slices) * 3; // Index of top vertex

            // Vertices
            vertices[kBot] = radius * xComponent;
            vertices[kBot+1] = 0;
            vertices[kBot+2] = radius * zComponent;
            vertices[kTop] = radius * xComponent;
            vertices[kTop+1] = height;
            vertices[kTop+2] = radius * zComponent;

            // Normals
            normals[kBot] = xComponent;
            normals[kBot+1] = 0;
            normals[kBot+2] = zComponent;
            normals[kTop] = xComponent;
            normals[kTop+1] = 0;
            normals[kTop+2] = zComponent;

            kBot = i * 2; // Index of texture coord for top vertex
            kTop = (i  + slices) * 2; // Index of texture coord for bottom vertex

            // Textures
            textures[kBot] = (float)i / slices;
            textures[kBot+1] = 1;
            textures[kTop] = (float)i / slices;
            textures[kTop+1] = 0;

            // VERTICES FOR CAPS (TOP AND BOTTOM FACES)
            // A different set of vertices is used for the top and bottom faces to allow for different normals.

            kBot = (i + numVertices / 2) * 3; // Index of bottom vertex
            kTop = (i  + slices + numVertices / 2) * 3 ; // Index of top vertex

            // Vertices
            vertices[kBot] = radius * xComponent;
            vertices[kBot+1] = 0;
            vertices[kBot+2] = radius * zComponent;
            vertices[kTop] = radius * xComponent;
            vertices[kTop+1] = height;
            vertices[kTop+2] = radius * zComponent;

            // Normals
            normals[kBot] = 0;
            normals[kBot+1] = -1;
            normals[kBot+2] = 0;
            normals[kTop] = 0;
            normals[kTop+1] = 1;
            normals[kTop+2] = 0;

            kBot = i * 2 + numVertices; // Index of texture coord for top vertex
            kTop = (i  + slices) * 2 + numVertices ; // Index of texture coord for bottom vertex

            // Textures
            textures[kBot] = xComponent;
            textures[kBot+1] = zComponent;
            textures[kTop] = - xComponent;
            textures[kTop+1] = zComponent;

        }

        numIndices = slices * 12 - 12;
        indices = new int[numIndices];

        // Indices for walls
        for (int i = 0; i < slices; i++) {
            indices[i*6] = i + slices;
            indices[i*6+1] = (i + 1) % slices;
            indices[i*6+2] = i;
            indices[i*6+3] = (i + 1) % slices;
            indices[i*6+4] = i + slices;
            indices[i*6+5] = (i + 1) % slices + slices;
        }

        int offset = slices * 6;

        // Indices for Caps
        for (int i = 2; i < slices; i++) {
            int kBot = (i - 2) * 3;
            int kTop = (slices + i - 4) * 3;
            indices[offset+kBot] = numVertices / 2;
            indices[offset+kBot+1] = numVertices / 2 + i - 1;
            indices[offset+kBot+2] = numVertices / 2 + i;
            indices[offset+kTop] = numVertices / 2 + slices + i;
            indices[offset+kTop+1] = numVertices / 2 + slices + i - 1;
            indices[offset+kTop+2] = numVertices / 2 + slices;
        }
    }

    public void setHeight(int slices){
        this.slices = slices;
        updated = false;
    }

    public void setRadius(float radius){
        this.radius = radius;
        updated = false;
    }

    public void setSlices(int slices){
        this.slices = slices;
        updated = false;
    }

    public float getHeight() { return height; }
    public float getRadius() { return radius; }
    public int getSlices() { return slices; }
}
