package com.apoapsys.astronomy.particleplayground;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

import com.apoapsys.astronomy.Constants;
import com.apoapsys.astronomy.math.Matrix;
import com.apoapsys.astronomy.math.Vector;
import com.apoapsys.astronomy.orbits.EllipticalOrbit;
import com.apoapsys.astronomy.orbits.Ephemeris;
import com.apoapsys.astronomy.orbits.OrbitPosition;
import com.apoapsys.astronomy.particleplayground.ExamineView.ModelViewChangeListener;
import com.apoapsys.astronomy.simulations.nbody.Particle;
import com.apoapsys.astronomy.simulations.nbody.leapfrog.LeapFrogSimulator;
import com.apoapsys.astronomy.time.DateTime;
import com.apoapsys.astronomy.time.JulianUtil;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;

public class ParticleGJPanel extends GLJPanel implements GLEventListener, KeyListener, MouseMotionListener, MouseListener, MouseWheelListener  {
	private static GLCapabilities caps;
	
	private LeapFrogSimulator simulator;
	private List<ParticleEmitter> emitters = new ArrayList<>();
	
	private ExamineView examineView;
	
	private boolean showOrbits = true;
	private boolean showGrid = true;
	
	private double radius = .5;
	private double rotateSpeed = 0.2;
	private double zoomSpeed = 2.0;
	
	private boolean hideMouse = false;
	private boolean captureMouse = false;

	private int lastX = -1;
	private int lastY = -1;
	
	private boolean mouse1Down = false;
	private boolean mouse2Down = false;
	private boolean mouse3Down = false;
	
	static {
		caps = new GLCapabilities(GLProfile.get(GLProfile.GL2));
		caps.setAlphaBits(8);
		caps.setDoubleBuffered(true);
		caps.setSampleBuffers(true);
		caps.setNumSamples(4);
	}
	
	public ParticleGJPanel(LeapFrogSimulator simulator) {
		super(caps, null);
		this.simulator = simulator;
		addGLEventListener(this);
		
		addGLEventListener(this);
		addKeyListener(this);
		addMouseMotionListener(this);
		addMouseListener(this);
		addMouseWheelListener(this);
		
		examineView = new ExamineView();
		examineView.setMinDistance(1);
		examineView.setMaxDistance(20000);
		examineView.setDistance(500);

		examineView.setModelRadius(.5);
		examineView.setMaxScale((examineView.getDistance() - radius) / radius);
		//examineView.rotate(-90, 0);
		//examineView.rotate(0, 90);
		//examineView.rotate(-30, 0);
		
		examineView.addModelViewChangeListener(new ModelViewChangeListener()
		{
			public void onModelViewChanged(Matrix modelView)
			{
				repaint();
			}
		});
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		
	}
	
	public List<ParticleEmitter> getEmittersList() {
		return emitters;
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		GLU glu = new GLU();
		GLUT glut = new GLUT();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
	    gl.glLoadIdentity();
	    
	    gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	    gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
	    glu.gluLookAt(0, 0, examineView.getDistance(), 0, 0, 0, 0, 1, 0);
	    gl.glMultMatrixd(examineView.getModelView().matrix, 0);
	    
	    Particle centerOn = (simulator.getParticles().size() > 0) ? simulator.getParticles().get(0) : null;

	    if (centerOn != null) {
	    	//gl.glTranslated(-centerOn.position.x, -centerOn.position.y, -centerOn.position.z);
	    }
	    
	    
	    if (showGrid) {
	    	drawGrid(gl);
	    }
	    
	    if (showOrbits) {
		    gl.glPushMatrix();
		    gl.glDisable(GL2.GL_LIGHTING);
		    for (Particle particle : simulator.getParticles()) {
				if (centerOn != null && particle != centerOn) {
					
					
					drawEphemerisLine(particle, centerOn, gl);
				}
			}
		    gl.glEnable(GL2.GL_LIGHTING);
		    gl.glPopMatrix();
	    }
	    
	    gl.glPushMatrix();
	    for (Particle particle : simulator.getParticles()) {
	    	drawParticle(particle, gl);
	    }
	    gl.glPopMatrix();
	    
	    gl.glPushMatrix();
	    for (ParticleEmitter emitter : emitters) {
	    	drawEmitter(emitter, gl);
	    }
	    
	    
	    gl.glFlush();
	    
	}
	
	
	private void drawGrid(GL2 gl) {
		gl.glDisable(GL2.GL_LIGHTING);
		
		gl.glColor4f(1.0f, 1.0f, 0.0f, 0.3f);
		
		// Arbitraty values for now...
		gl.glBegin(GL2.GL_LINES);
		for (float v = -10000; v < 10000; v+= 100) {
			gl.glVertex3f(v, 0.0f, 10000f);
			gl.glVertex3f(v, 0.0f, -10000f);
			
			gl.glVertex3f(-10000f, 0.0f, v);
			gl.glVertex3f(10000f, 0.0f, v);
		}
		gl.glEnd();
		
		
		
		
		
		gl.glEnable(GL2.GL_LIGHTING);
	}
	
	private void drawEphemerisLine(Particle particle, Particle centerOnParticle, GL2 gl) {
		Ephemeris eph = calculateParticleEphemeris(particle, centerOnParticle);
		if (eph == null) {
			return;
		}
		
		if (eph.eccentricity >= 0.0 && eph.eccentricity <= 1.0) {
			eph.epoch = JulianUtil.julianNow();
			
			EllipticalOrbit orbit = new EllipticalOrbit(eph);
			double step = eph.period / 360.0 / 4.0;
			
			gl.glBegin(GL2.GL_LINE_STRIP);
			gl.glColor3f(1.0f, 0.0f, 0.0f);
			int k = 0;
			for (double jd = eph.epoch; jd < eph.epoch + eph.period + step; jd+=step) {
				DateTime dt = new DateTime(jd);
				OrbitPosition pos = orbit.positionAtTime(dt);
				Vector posVector = pos.getPosition();
				posVector.multiplyScalar(Constants.AU_TO_KM * 1000);
				Vector position = posVector.clone().divideScalar(10E8);
				//position.add(centerOnParticle.position);
				
				gl.glVertex3f((float)position.x, (float)position.y, (float)position.z);
				
				
			}
			
			gl.glEnd();
		}
	}
	
	private Ephemeris calculateParticleEphemeris(Particle particle, Particle centeredOn) {
		
		if (centeredOn == null) {
			return null;
		}
		
		Vector statePos = particle.position.clone();
		Vector stateVel = particle.velocity.clone();
		
		statePos.subtract(centeredOn.position);
		stateVel.subtract(centeredOn.velocity);
		
		double y = statePos.y;
		statePos.y = -statePos.z;
		statePos.z = y;
		
		y = stateVel.y;
		stateVel.y = -stateVel.z;
		stateVel.z = y;
		
		Ephemeris eph = Ephemeris.fromStateVectors(statePos,
													stateVel, 
													particle.body.getMass(), 
													centeredOn.body.getMass());
		return eph;
	}
	
	private void drawParticle(Particle particle, GL2 gl) {
		
		GLUT glut = new GLUT();
		gl.glPushMatrix();
		

		gl.glColor3f(1, 0, 0);
		
		//System.err.println(particle.position.length() / Constants.AU_TO_KM / 10.0);
		
		Vector position = particle.position.clone().divideScalar(10E8);//.divideScalar(Constants.AU_TO_KM).divideScalar(10.0);
		
		if (particle.body.getName().equals("Mercury")) {
		//	System.err.println(position.length());
		}
		
		gl.glTranslatef((float)position.x, (float)position.y, (float)position.z);
		
		Color c = (Color) particle.extendedProperties.get("color");
		c = (c != null) ? c : Color.gray;
		
		float radius = 1.0f;//(float) ((particle.body.getRadius() >= 1.0) ? particle.body.getRadius() : 10.0);
		
		float color[] = { c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, 0.7f };
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, color, 0);
		glut.glutSolidSphere(radius, 64, 32);

		
		gl.glPopMatrix();
	}
	
	private void drawEmitter(ParticleEmitter emitter, GL2 gl) {
		GLUT glut = new GLUT();
		gl.glPushMatrix();
		
		gl.glDisable(GL2.GL_LIGHTING);
		gl.glColor3f(1.0f, 0.0f, 0.0f);

		glut.glutWireSphere(1.0, 24, 24);
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glPopMatrix();
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		
		
		float pos[] = { 5.0f, 5.0f, 10.0f, 0.0f };
	    float red[] = { 0.8f, 0.1f, 0.0f, 0.7f };
	    float green[] = { 0.0f, 0.8f, 0.2f, 0.7f };
	    float blue[] = { 0.2f, 0.2f, 1.0f, 0.7f };

	    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, pos, 0);
	    gl.glEnable(GL2.GL_CULL_FACE);
	    gl.glEnable(GL2.GL_LIGHTING);
	    gl.glEnable(GL2.GL_LIGHT0);
	    gl.glEnable(GL2.GL_DEPTH_TEST);
	    gl.glEnable(GL.GL_MULTISAMPLE);
	    gl.glShadeModel(GL2.GL_SMOOTH);
	    gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_FASTEST);
	    gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL.GL_FASTEST);
	    gl.glHint(GL2.GL_POINT_SMOOTH_HINT, GL.GL_FASTEST);

	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
		GL2 gl = drawable.getGL().getGL2();
		GLU glu = new GLU();
		
		
		gl.glViewport(0,0,w,h);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		
		
		double aspect = (double) w / (double) h;
		
		glu.gluPerspective(45,
				aspect,
			 	0.01,
			 	100000);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		
	}
	
	
	@Override
	public void keyTyped(KeyEvent arg0)
	{
	}

	@Override
	public void keyPressed(KeyEvent key)
	{

		Vector moveVector = null;
		switch (key.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
			new Thread()
			{
				@Override
				public void run()
				{
					//animator.stop();
				}
			}.start();
			System.exit(0);
			break;
		case KeyEvent.VK_N:

			break;
		case 38: // Forward
			moveVector = new Vector(0, 0, -0.1);
			break;
		case 40: // Backward
			moveVector = new Vector(0, 0, 0.1);
			break;
		case 37: // Left
			moveVector = new Vector(-0.1, 0, 0);
			break;
		case 39: // Right
			moveVector = new Vector(0.1, 0, 0);
			break;
		default:
			break;
		}

		if (moveVector != null) {
			// Vectors.rotateY(viewPoint.getYaw(), moveVector);
			// viewPoint.setZ(viewPoint.getZ() + (float) moveVector.z);
			// viewPoint.setX(viewPoint.getX() + (float) moveVector.x);

		}
	}

	@Override
	public void keyReleased(KeyEvent key)
	{

	}


	@Override
	public void mouseDragged(MouseEvent e)
	{
		int x = e.getX();
		int y = e.getY();

		if (lastX != -1 && lastY != -1) {

			int deltaX = x - lastX;
			int deltaY = y - lastY;

			if (e.isShiftDown() || mouse3Down) {
				examineView.setPitch(examineView.getPitch() + deltaY * rotateSpeed);
				examineView.setRoll(examineView.getRoll() + deltaX * rotateSpeed);
			} else {
				examineView.rotate(-deltaY * rotateSpeed, -deltaX * rotateSpeed);
			}

			repaint();
		}

		lastX = x;
		lastY = y;
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{

	}

	@Override
	public void mouseClicked(MouseEvent e)
	{

	}

	@Override
	public void mouseEntered(MouseEvent e)
	{

	}

	@Override
	public void mouseExited(MouseEvent e)
	{

		Point locOnScreen = this.getLocationOnScreen();
		int middleX = locOnScreen.x + (this.getWidth() / 2);
		int middleY = locOnScreen.y + (this.getHeight() / 2);

		if (captureMouse) {
			try {
				Robot rob = new Robot();
				rob.mouseMove(middleX, middleY);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		lastX = -1;
		lastY = -1;
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		lastX = e.getX();
		lastY = e.getY();

		if (e.getButton() == MouseEvent.BUTTON1) {
			mouse1Down = true;
		} else if (e.getButton() == MouseEvent.BUTTON2) {
			mouse2Down = true;
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			mouse3Down = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		lastX = -1;
		lastY = -1;

		if (e.getButton() == MouseEvent.BUTTON1) {
			mouse1Down = false;
		} else if (e.getButton() == MouseEvent.BUTTON2) {
			mouse2Down = false;
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			mouse3Down = false;
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		double c = e.getWheelRotation();
		examineView.setDistance(examineView.getDistance() + (c * zoomSpeed));
		examineView.setMaxScale((examineView.getDistance() - radius) / radius);
	}

	public boolean getShowOrbits() {
		return showOrbits;
	}

	public void setShowOrbits(boolean showOrbits) {
		this.showOrbits = showOrbits;
	}

	public boolean getShowGrid() {
		return showGrid;
	}

	public void setShowGrid(boolean showGrid) {
		this.showGrid = showGrid;
	}
	

}
