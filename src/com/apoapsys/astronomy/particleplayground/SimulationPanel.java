package com.apoapsys.astronomy.particleplayground;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

import com.apoapsys.astronomy.Constants;
import com.apoapsys.astronomy.math.MathExt;
import com.apoapsys.astronomy.math.Vector;
import com.apoapsys.astronomy.orbits.EllipticalOrbit;
import com.apoapsys.astronomy.orbits.Ephemeris;
import com.apoapsys.astronomy.orbits.OrbitPosition;
import com.apoapsys.astronomy.simulations.nbody.Particle;
import com.apoapsys.astronomy.simulations.nbody.leapfrog.LeapFrogSimulator;
import com.apoapsys.astronomy.time.DateTime;
import com.apoapsys.astronomy.time.JulianUtil;

public class SimulationPanel extends JPanel {

	private LeapFrogSimulator simulator;
	
	private List<ParticleEmitter> emitters = new ArrayList<>();
	
	private Particle activeParticle = null;
	private ParticleEmitter activeEmitter = null;
	
	private Particle centerOnParticle = null;
	
	
	private ParticleEmitter draggingEmitter;
	
	private double viewDistance = 2350.0;
	private int panX = 0;
	private int panY = 0;
	private int mouseX = -1;
	private int mouseY = -1;
	
	public SimulationPanel(LeapFrogSimulator simulator) {
		this.simulator = simulator;
		
		Timer timer = new Timer(10, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		});
		timer.start();
		
		
		
		class MouseActionListener implements MouseMotionListener, MouseListener, MouseWheelListener {
			int lastX = -1;
			int lastY = -1;
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				double delta = e.getPreciseWheelRotation();
				viewDistance += viewDistance * (delta * 0.1);
				
				panX += panX * (delta * 0.1);
				panY += panY * (delta * 0.1);
			}
			@Override
			public void mouseDragged(MouseEvent e) {
				if (draggingEmitter == null && lastX != -1 && lastY != -1) {
					int dX = e.getX() - lastX;
					int dY = e.getY() - lastY;
					panX += dX;
					panY += dY;
				}
				lastX = e.getX();
				lastY = e.getY();
				
				Vector map = screenCoordinatesToMap((int)lastX, (int)lastY);
				if (draggingEmitter != null) {
					draggingEmitter.setLocation(map);
				}
				
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();
				
			}
			

			@Override
			public void mouseExited(MouseEvent e) {
				mouseX = -1;
				mouseY = -1;
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
					centerOnParticle = getParticleAtScreenLocation((int)mouseX, (int)mouseY);
				} else if (e.getButton() == MouseEvent.BUTTON3) { // Right mouse button
				
					Vector map = screenCoordinatesToMap((int)mouseX, (int)mouseY);

				} else if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 1) {
					activeParticle = getParticleAtScreenLocation((int)mouseX, (int)mouseY);
					activeEmitter = getParticleEmitterAtScreenLocation((int)mouseX, (int) mouseY);
				}
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				draggingEmitter = getParticleEmitterAtScreenLocation(e.getX(), e.getY());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				draggingEmitter = null;
				lastX = -1;
				lastY = -1;
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		};
		
		MouseActionListener mouseListener = new MouseActionListener();
		this.addMouseMotionListener(mouseListener);
		this.addMouseListener(mouseListener);
		this.addMouseWheelListener(mouseListener);
	}
	
	private Particle getParticleAtScreenLocation(int x, int y) {
		Vector location = new Vector(x, y, 0);
		
		for (Particle particle : simulator.getParticles()) {
			
			Vector screen = this.mapCoordinatesToScreen(particle.position);
			
			double bodyRadius = (particle.body.getRadius() / viewDistance) * (getHeight() / 2.0);
			
			bodyRadius = (bodyRadius < 10) ? 10 : bodyRadius;

			if (location.getDistanceTo(screen) <= bodyRadius) {
				return particle;
			}
		}
		
		return null;
	}
	
	
	private ParticleEmitter getParticleEmitterAtScreenLocation(int x, int y) {
		Vector location = new Vector(x, y, 0);
		
		for (ParticleEmitter emitter : emitters) {
			Vector screen = this.mapCoordinatesToScreen(emitter.getLocation());
			double bodyRadius = 10;
			
			if (location.getDistanceTo(screen) <= bodyRadius) {
				return emitter;
			}
		}
		
		return null;
	}
	
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		
		for (Particle particle : simulator.getParticles()) {
			if (centerOnParticle != null && particle != centerOnParticle) {
				drawEphemerisLine(particle, g2d);
			}
		}
		
		for (Particle particle : simulator.getParticles()) {
			drawParticle(particle, g2d);
		}
		
		for (ParticleEmitter emitter : emitters) {
			drawEmitter(emitter, g2d);
		}
	}
	
	private void drawParticle(Particle particle, Graphics2D g2d) {
		Vector screen = this.mapCoordinatesToScreen(particle.position);
		
		double bodyRadius = (particle.body.getRadius() / viewDistance) * (getHeight() / 2.0);
		bodyRadius = MathExt.max(bodyRadius, 2);
		
		Color c = Color.BLACK;
		if (particle.extendedProperties.get("color") != null) {
			c = (Color) particle.extendedProperties.get("color");
		}
		
		g2d.setColor((particle == activeParticle) ? Color.RED : c);
		g2d.fillOval((int)(screen.x-bodyRadius), (int)(screen.y-bodyRadius), (int)bodyRadius*2, (int)bodyRadius*2);
		
		if (particle == centerOnParticle) {
			bodyRadius += 2;
			g2d.drawOval((int)(screen.x-bodyRadius), (int)(screen.y-bodyRadius), (int)bodyRadius*2, (int)bodyRadius*2);
		} 
	}
	
	private void drawEphemerisLine(Particle particle, Graphics2D g2d) {
		
		Ephemeris eph = calculateParticleEphemeris(particle, centerOnParticle);
		if (eph == null) {
			return;
		}
		
		g2d.setColor(new Color(0, 255, 0, 100));
		
		if (eph.eccentricity >= 0.0 && eph.eccentricity <= 1.0) {
			eph.epoch = JulianUtil.julianNow();
			
			EllipticalOrbit orbit = new EllipticalOrbit(eph);
			double step = eph.period / 360.0 / 4.0;
			
			int k = 0;
			Path2D futurePath = new Path2D.Double();
			for (double jd = eph.epoch; jd < eph.epoch + eph.period + step; jd+=step) {
				DateTime dt = new DateTime(jd);
				OrbitPosition pos = orbit.positionAtTime(dt);
				Vector posVector = pos.getPosition();
				posVector.multiplyScalar(Constants.AU_TO_KM * 1000);
				
				posVector.add(centerOnParticle.position);
				
				Vector screenXY = this.mapCoordinatesToScreen(posVector);
				
				
				
				if (k == 0) {
					futurePath.moveTo(screenXY.x, screenXY.y);
				} else {
					futurePath.lineTo(screenXY.x, screenXY.y);
				}
				k++;
				
			}
			if (k > 0) {
				futurePath.closePath();

				g2d.draw(futurePath);
			}
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
	
	
	private void drawEmitter(ParticleEmitter emitter, Graphics2D g2d) {
		Vector screen = this.mapCoordinatesToScreen(emitter.getLocation());
		
		g2d.setColor((emitter == activeEmitter) ? Color.BLUE : Color.BLACK);
		g2d.drawOval((int)(screen.x-2), (int)(screen.y-2), (int)4, (int)4);
		
		Vector line = new Vector(screen.x, 0, screen.y);
		line.add(emitter.getFacing().clone().multiplyScalar(5));
		g2d.drawLine((int)screen.x, (int)screen.y, (int)line.x, (int)line.z);
	}
	
	
	protected Vector screenCoordinatesToMap(int x, int y) {
		return screenCoordinatesToMap(x, y, getWidth(), getHeight());
	}
	
	protected Vector screenCoordinatesToMap(int x, int y, int width, int height) {
		
		double _x = (x - panX - (width / 2.0)) / (width / 2.0) * viewDistance;
		double _z = (y - panY - (height / 2.0)) / (height / 2.0) * viewDistance;
		
		Vector center = centerOnParticle != null ? centerOnParticle.position : new Vector(0, 0, 0);
		_x += center.x;
		_z += center.y;
		
		return new Vector(_x, 0.0, _z);
	}
	
	protected Vector mapCoordinatesToScreen(Vector mapCoords) {
		return mapCoordinatesToScreen(mapCoords, false, getWidth(), getHeight());
	}
	
	protected Vector mapCoordinatesToScreen(Vector mapCoords, boolean adjustToCenter, double width, double height) {

		double x = mapCoords.x;
		double y = mapCoords.z;
		
		Vector center = centerOnParticle != null ? centerOnParticle.position : new Vector(0, 0, 0);
		x -= center.x;
		y -= center.z;
		
		x = (x / viewDistance) * (width / 2.0) + (width / 2.0) + panX;
		y = (y / viewDistance) * (height / 2.0) + (height / 2.0) + panY;
		
		return new Vector(x, y, 0.0);
	}

	
	public List<ParticleEmitter> getEmittersList() {
		return emitters;
	}
	
	public Particle getActiveParticle() {
		return activeParticle;
	}
	
	public ParticleEmitter getActiveParticleEmitter() {
		return activeEmitter;
	}
	
	public Particle getCenterOnParticle() {
		return centerOnParticle;
	}
}
