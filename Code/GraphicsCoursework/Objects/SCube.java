package Objects;
public class SCube extends SObject{
	private float sideLength;

	public SCube(){
		super();
		sideLength = 1;
		update();
	}

	public SCube(float sideLength){
		super();
		this.sideLength = sideLength;
		update();
	}

	@Override
	protected void genData() {
		numVertices = 24; // 4 for each side
		vertices = new float[numVertices * 3];
		normals = new float[numVertices * 3];
		textures = new float[numVertices * 2];

		float k = sideLength / 2f;

		// Vertices and Normals
		// Vertex Order:
		//   1 → 2
		//     ↙     ↑axis3 axis2→
		//   3 → 4
		int offset = 0;
		for (int i = 0; i < 3; i++) {
			int axis1 = i % 3;
			int axis2 = (i + 1) % 3;
			int axis3 = (i + 2) % 3;
			for (int j = 0; j < 8; j++) {                           // 0 1 2 3   4 5 6 7
				vertices[offset+axis1] = (j / 4 == 0) ? k : -k;     // + + + +   - - - -
				vertices[offset+axis2] = (j % 2 == 0) ? k : -k;     // + - + -   + - + -
				vertices[offset+axis3] = (j / 2 % 2 == 0) ? k : -k; // + + - -   + + - -

				normals[offset+axis1] = (j / 4 == 0) ? k : -k;      // + + + +   - - - -
				normals[offset+axis2] = 0;
				normals[offset+axis3] = 0;
				offset += 3;
			}
		}

		// Textures
		for (int i = 0; i < 6; i++) {
			offset = i * 8;
			// Axis specific values ensure that the textures appear in the same orientations as in the brief
			textures[offset]   = 1;
			textures[offset+1] = 1;
			textures[offset+2] = i > 3 ? 0 : 1;
			textures[offset+3] = i > 3 ? 1 : 0;
			textures[offset+4] = i > 3 ? 1 : 0;
			textures[offset+5] = i > 3 ? 0 : 1;
			textures[offset+6] = 0;
			textures[offset+7] = 0;
		}

		numIndices = 36;
		indices = new int[numIndices];

		// Indices
		// For each axis
		for (int i = 0; i < 3; i++) {
			offset = i * 12;
			// Positive Face
			indices[offset]    = i * 8 + 2;
			indices[offset+1]  = i * 8 + 1;
			indices[offset+2]  = i * 8;
			indices[offset+3]  = i * 8 + 1;
			indices[offset+4]  = i * 8 + 2;
			indices[offset+5]  = i * 8 + 3;
			// Negative face
			indices[offset+6]  = i * 8 + 4;
			indices[offset+7]  = i * 8 + 5;
			indices[offset+8]  = i * 8 + 6;
			indices[offset+9]  = i * 8 + 7;
			indices[offset+10] = i * 8 + 6;
			indices[offset+11] = i * 8 + 5;
		}
	}
	
	public void setSideLength(float sideLength){
		this.sideLength = sideLength;
		updated = false;
	}

	public float getSideLength(){
		return sideLength;
	}	
}