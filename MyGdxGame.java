package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class MyGdxGame extends ApplicationAdapter {
	
	//Just copy this code into you project preferably with eclipse change the class and package name as needed and have fun!!!
	
	Texture texture;
	ShaderProgram shader;
	Mesh mesh;
	OrthographicCamera camera;
	float[] v;
	
	//this is the shader
	public static final String VERT_SHADER =  
			"attribute vec2 a_position;\n" +	
			"attribute vec2 a_texture;\n" +
			"attribute float a_shade;\n" + 
			"uniform mat4 u_projTrans;\n" + 
			"varying float shade;" + 
			"void main() {\n" +  
			"  gl_TexCoord[0].st = a_texture;\n" + 
		    "  gl_Position = u_projTrans * vec4(a_position.xy, 0.0, 1.0);\n" + 
			"  shade = a_shade;\n" + 
			"}";
	
	public static final String FRAG_SHADER = 
            "#ifdef GL_ES\n" +
            "precision mediump int;\n" +
            "#endif\n" +
            "uniform sampler2D texture1;" + 
            "varying float shade;" + 
			"void main() {\n" +  
			"  gl_FragColor = texture2D(texture1, gl_TexCoord[0].st);\n" + 
			"  gl_FragColor.rgb *= shade;" + 
			"}";
	
	
	protected static ShaderProgram createMeshShader() {
		ShaderProgram.pedantic = false;
		ShaderProgram shader = new ShaderProgram(VERT_SHADER, FRAG_SHADER);
		String log = shader.getLog();
		if (!shader.isCompiled())
			throw new GdxRuntimeException(log);
		if (log!=null && log.length()!=0)
			System.out.println("Shader Log: "+log);
		return shader;
	}
	//end of shader stuff
	
	//this creates the block information from the inputs I provided
	public float[] createBlockGraphics(Vector2 pos, float size, float shade1, float shade2, float shade3, float shade4)
	{
		float[] v = new float[30];
		v[0] = pos.x;
		v[1] = pos.y;
		v[2] = 0f;
		v[3] = 0f;
		v[4] = shade1;
		v[5] = pos.x + size;
		v[6] = pos.y;
		v[7] = 1f;
		v[8] = 0f;
		v[9] = shade1;
		v[10] = pos.x + size;
		v[11] = pos.y - size;
		v[12] = 1f;
		v[13] = 1f;
		v[14] = shade3;
		v[15] = pos.x + size;
		v[16] = pos.y - size;
		v[17] = 1f;
		v[18] = 1f;
		v[19] = shade3;
		v[20] = pos.x;
		v[21] = pos.y - size;
		v[22] = 0f;
		v[23] = 1f;
		v[24] = shade4;
		v[25] = pos.x;
		v[26] = pos.y;
		v[27] = 0f;
		v[28] = 0f;
		v[29] = shade1;
		return v;
	}
	
	@Override
	public void create () {
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		texture = new Texture("assets/Grass.png");
		shader = createMeshShader();
		
		/*
		 * here is were I am creating the block information what this is doing is creating a bunch of blocks 
		 * in a way that is condensed, not really intended to be functional but it just gives you the basics 
		 * of block creation. What it does allow is for you to draw multiple squares on a single mesh.
		 * if you want to use multiple textures you will need to create multiple meshes.
		 */
		
		//this is the shade value; it is a value between 0 and 1. Zero being complete darkness and one being (full light)/(full texture).
		float shade = 1;
		
		int amountBlock = 48;
		List<float[]> blocks = new ArrayList<float[]>();
		float blockSize = 10;
		int xer = -amountBlock / 2;
		int yer = amountBlock / 2;
		int bufferSize = 0;
		for (int i = 0; i < amountBlock; i++)
		{
			for (int ii = 0; ii < amountBlock; ii++)
			{
				blocks.add(createBlockGraphics(new Vector2(xer * blockSize, yer * blockSize), blockSize, shade, shade, shade - ((float)1 / amountBlock), shade - ((float)1 / amountBlock)));
				bufferSize += blocks.get(i).length;
				shade -= (float)1 / amountBlock;
				yer--;
			}
			shade = 1;
			xer++;
			yer = amountBlock / 2;
		}
		v = new float[bufferSize];
		bufferSize = 0;
		for (int i = 0; i < blocks.size(); i++)
		{
			for (int ii = 0; ii < blocks.get(i).length; ii++)
			{
				v[bufferSize] = blocks.get(i)[ii];
				bufferSize++;
			}
		}
		VertexAttribute v1 = new VertexAttribute(Usage.Position, 2, "a_position");
		VertexAttribute v2 = new VertexAttribute(Usage.TextureCoordinates, 2, "a_texture");
		VertexAttribute v3 = new VertexAttribute(Usage.Position, 1, "a_shade");
		mesh = new Mesh(true, v.length / 5, 0, v1, v2, v3);
		mesh.setVertices(v);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		shader.begin();
		shader.setUniformMatrix("u_projTrans", camera.combined);
		texture.bind();
		mesh.render(shader, GL20.GL_TRIANGLES, 0, v.length / 5);
		shader.end();
	}
}
