import static com.jogamp.opengl.GL3.*;

import java.io.IOException;
import java.io.File;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.swing.JFrame;

import Basic.ShaderProg;
import Basic.Transform;
import Basic.Vec4;
import Objects.SCube;
import Objects.SObject;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class CGCW03 extends JFrame{

	final GLCanvas canvas;
	final FPSAnimator animator=new FPSAnimator(60, true);
	final Renderer renderer = new Renderer();

	// Copied from CGCW02.java
	public CGCW03() {
		GLProfile glp = GLProfile.get(GLProfile.GL3);
		GLCapabilities caps = new GLCapabilities(glp);
		canvas = new GLCanvas(caps);

		add(canvas, java.awt.BorderLayout.CENTER);
		canvas.addGLEventListener(renderer);

		animator.add(canvas);

		setTitle("Coursework 1");
		setSize(500, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

		animator.start();
		canvas.requestFocus();
	}

	public static void main(String[] args) {
		new CGCW03();
	}

	class Renderer implements GLEventListener{

		private Transform T = new Transform();

		// VAOs and VBOs parameters
		private int idPoint   = 0, numVAOs = 1;
		private int idBuffer  = 0, numVBOs = 1;
		private int idElement = 0, numEBOs = 1;
		private int[] VAOs = new int[numVAOs];
		private int[] VBOs = new int[numVBOs];
		private int[] EBOs = new int[numEBOs];

		// Model parameters
		private int numElements;
		private int vPosition;
		private int vNormal;
		private int vTextureCoord;

		// Transformation parameters
		private int ModelView;
		private int Projection;
		private int NormalTransform;

		// Lighting Parameters
		private int LightPosition;
		private int AmbientProduct;
		private int DiffuseProduct;
		private int SpecularProduct;
		private int Shininess;

		@Override
		public void display(GLAutoDrawable drawable) {
			GL3 gl = drawable.getGL().getGL3(); // Get the GL pipeline object this

			gl.glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);

			// Rotate the cube to a similar orientation to the image in the brief
			T.initialize();
			T.rotateY(-20);
			T.rotateZ(15);
			T.rotateX(-20);

			gl.glUniformMatrix4fv( ModelView,       1, true, T.getTransformv(),     0 );
			gl.glUniformMatrix4fv( NormalTransform, 1, true, T.getInvTransformTv(), 0 );

			gl.glPolygonMode(GL_FRONT_FACE, GL_FILL);
			gl.glDrawElements(GL_TRIANGLES, numElements, GL_UNSIGNED_INT, 0);
		}

		@Override
		public void dispose(GLAutoDrawable drawable) {
			// TODO Auto-generated method stub
		}

		@Override
		public void init(GLAutoDrawable drawable) {
			GL3 gl = drawable.getGL().getGL3();

			try {
				// Load the texture
				TextureIO.newTexture(new File("WelshDragon.jpg"), false);
			} catch (IOException e) {
				e.printStackTrace();
			}

			gl.glEnable(GL_CULL_FACE);

			// Create the cube
			SObject cube = new SCube(1);
			float [] vertexArray  = cube.getVertices();
			float [] normalArray  = cube.getNormals();
			float [] textureArray = cube.getTextures();
			int [] vertexIndices  = cube.getIndices();
			numElements = cube.getNumIndices();

			// Generate vertex array, vertex buffer, and element buffer
			gl.glGenVertexArrays(numVAOs,VAOs,0);
			gl.glGenBuffers(numVBOs, VBOs,0);
			gl.glGenBuffers(numEBOs, EBOs,0);
			// Bind vertex array, vertex buffer, and element buffer
			gl.glBindVertexArray(VAOs[idPoint]);
			gl.glBindBuffer(GL_ARRAY_BUFFER,         VBOs[idBuffer]);
			gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBOs[idElement]);

			// Convert standard arrays to OpenGL Buffers
			FloatBuffer vertices = FloatBuffer.wrap(vertexArray);
			FloatBuffer normals = FloatBuffer.wrap(normalArray);
			FloatBuffer textures = FloatBuffer.wrap(textureArray);
			IntBuffer elements = IntBuffer.wrap(vertexIndices);

			// Calculate sizes of vertex attribute arrays and element array
			long vertexSize  =  vertexArray.length  * Float.SIZE / 8;
			long normalSize  =  normalArray.length  * Float.SIZE / 8;
			long textureSize = textureArray.length  * Float.SIZE / 8;
			long indexSize   = vertexIndices.length * Integer.SIZE / 8;

			// Initialise array buffer to fit vertexes, normals and texture coords
			// and initialise element array buffer to fit all indices.
			gl.glBufferData(GL_ARRAY_BUFFER,         vertexSize + normalSize + textureSize, null,     GL_STATIC_DRAW);
			gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexSize,                             elements, GL_STATIC_DRAW);

			// Add vertex attribute data to array buffer
			gl.glBufferSubData( GL_ARRAY_BUFFER, 0,                       vertexSize,  vertices );
			gl.glBufferSubData( GL_ARRAY_BUFFER, vertexSize,              normalSize,  normals );
			gl.glBufferSubData( GL_ARRAY_BUFFER, vertexSize + normalSize, textureSize, textures );


			// Load shaders and initialise shader program
			ShaderProg shaderproc = new ShaderProg(gl, "Texture.vert", "Texture.frag");
			int program = shaderproc.getProgram();
			gl.glUseProgram(program);

			// Get handles for sending vertex attributes to shaders
			vPosition     = gl.glGetAttribLocation( program, "vPosition" );
			vNormal       = gl.glGetAttribLocation( program, "vNormal" );
			vTextureCoord = gl.glGetAttribLocation( program, "vTextureCoord" );
			// Get handles for sending global attributes to shaders
			ModelView       = gl.glGetUniformLocation(program, "ModelView");
			NormalTransform = gl.glGetUniformLocation(program, "NormalTransform");
			Projection      = gl.glGetUniformLocation(program, "Projection");

			// Enable vertex attribute arrays
			gl.glEnableVertexAttribArray(vPosition);
			gl.glEnableVertexAttribArray(vNormal);
			gl.glEnableVertexAttribArray(vTextureCoord);
			// Define locations in array buffer for each vertex attribute array
			gl.glVertexAttribPointer(vPosition,     3, GL_FLOAT, false, 0, 0L);
			gl.glVertexAttribPointer(vNormal,       3, GL_FLOAT, false, 0, vertexSize);
			gl.glVertexAttribPointer(vTextureCoord, 2, GL_FLOAT, false, 0, vertexSize + normalSize);


			// Define lighting parameters
			float[] lightPosition = {10.0f, 3.0f, -10.0f, 0.0f};
			float materialShininess = 64.0f;
			Vec4 materialAmbient  = new Vec4(1.0f, 1.0f, 1.0f, 1.0f);
			Vec4 materialDiffuse  = new Vec4(1.0f, 1.0f, 1.0f, 1.0f);
			Vec4 materialSpecular = new Vec4(1.0f, 1.0f, 1.0f, 1.0f);

			Vec4 lightAmbient  = new Vec4(0.2f, 0.2f, 0.2f, 1.0f);
			Vec4 lightDiffuse  = new Vec4(0.8f, 0.8f, 0.8f, 1.0f);
			Vec4 lightSpecular = new Vec4(1.0f, 1.0f, 1.0f, 1.0f);
			Vec4 ambientProduct  = lightAmbient.times(materialAmbient);
			Vec4 diffuseProduct  = lightDiffuse.times(materialDiffuse);
			Vec4 specularProduct = lightSpecular.times(materialSpecular);
			float[] ambient  = ambientProduct.getVector();
			float[] diffuse  = diffuseProduct.getVector();
			float[] specular = specularProduct.getVector();

			// Get locations to send global lighting attributes to shaders
			LightPosition   = gl.glGetUniformLocation(program, "LightPosition");
			AmbientProduct  = gl.glGetUniformLocation(program, "AmbientProduct");
			DiffuseProduct  = gl.glGetUniformLocation(program, "DiffuseProduct");
			SpecularProduct = gl.glGetUniformLocation(program, "SpecularProduct");
			Shininess       = gl.glGetUniformLocation(program, "Shininess");

			// Send values of global lighting attributes
			gl.glUniform4fv(LightPosition,   1, lightPosition, 0 );
			gl.glUniform4fv(AmbientProduct,  1, ambient,       0 );
			gl.glUniform4fv(DiffuseProduct,  1, diffuse,       0 );
			gl.glUniform4fv(SpecularProduct, 1, specular,      0 );
			gl.glUniform1f(Shininess, materialShininess );

		    //gl.glDepthFunc(GL_LESS);
			gl.glEnable(GL_DEPTH_TEST);
		}

		// Copied from CGCW02.java
		@Override
		public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
			GL3 gl = drawable.getGL().getGL3();
			gl.glViewport(x, y, w, h);

			T.initialize();

			if (h<1) { h=1; }
			if (w<1) { w=1; }
			float a = (float) w / h;
			if (w < h) { T.ortho(-1,   1,   -1/a, 1/a, -1, 1); }
			else       { T.ortho(-1*a, 1*a, -1,   1,   -1, 1); }

			T.reverseZ();
			gl.glUniformMatrix4fv( Projection, 1, true, T.getTransformv(), 0 );
		}
	}
}