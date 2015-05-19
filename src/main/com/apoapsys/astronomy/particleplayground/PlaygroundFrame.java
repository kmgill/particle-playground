package com.apoapsys.astronomy.particleplayground;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

import com.apoapsys.astronomy.math.Vector;
import com.apoapsys.astronomy.simulations.nbody.Particle;
import com.apoapsys.astronomy.simulations.nbody.leapfrog.LeapFrogSimulator;
import com.jogamp.opengl.util.FPSAnimator;

public class PlaygroundFrame extends JFrame {
	
	private LeapFrogSimulator simulator;
	private ParticleGJPanel simPanel;
	
	private JPanel emitterPropertiesPanel;
	
	public PlaygroundFrame(final LeapFrogSimulator simulator) {
		setTitle("Particle Playground");
		setSize(500, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.simulator = simulator;
		
		setLayout(new BorderLayout());
		
		simPanel = new ParticleGJPanel(simulator);
		add(simPanel, BorderLayout.CENTER);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		JPanel forcesPanel = new JPanel();
		tabbedPane.add("Forces", forcesPanel);
		
		
		emitterPropertiesPanel = new JPanel();
		emitterPropertiesPanel.setLayout(new GridLayout(3, 1, 3, 3));
		
		JScrollPane scroll = new JScrollPane(emitterPropertiesPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		tabbedPane.add("Emitters", scroll);
		
		add(tabbedPane, BorderLayout.EAST);
		
		JToolBar toolbar = new JToolBar();
		add(toolbar, BorderLayout.NORTH);
		
		JButton btnCreateCenter = new JButton("Create Center Particle");
		toolbar.add(btnCreateCenter);
		btnCreateCenter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				createBasicCenterParticle();
			}
		});
		
		JButton btnAddEmitter = new JButton("New Emitter");
		toolbar.add(btnAddEmitter);
		btnAddEmitter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ParticleEmitter emitter = new ParticleEmitter();
				emitter.setLocation(new Vector(0, 0, 60.0));
				emitter.setMass(270000000.0);
				emitter.setVelocity(0.5);
				addParticleEmitter(emitter);
			}
		});
		
		JButton btnCreateSolarSystem = new JButton("Create Solar System");
		toolbar.add(btnCreateSolarSystem);
		btnCreateSolarSystem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createSolarSystem();
			}
		});
		
		
		final FPSAnimator animator = new FPSAnimator(simPanel, 60);
		animator.start();
		
	}
	
	protected void createSolarSystem() {
		try {
			SolarSystemCreator.create(simulator);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void createBasicCenterParticle() {
		ParticleEmitter emitter0 = new ParticleEmitter();
		emitter0.setColor(Color.YELLOW);
		emitter0.setLocation(new Vector(0, 0, 0));
		emitter0.setFacing(new Vector(0, 0, 0));
		emitter0.setMass(270000000000.0);
		emitter0.setVelocity(0.0);
		emitter0.setRadius(4.0);
		simulator.addParticle(emitter0.createParticle());
	}
	
	public void addParticleEmitter(ParticleEmitter particleEmitter) {
		particleEmitter.addParticleCreateListener(new ParticleCreateListener() {
			@Override
			public void onParticleCreated(Particle particle) {
				simulator.addParticle(particle);
			}
		});
		
		EmitterPropertiesPanel emitterPanel = new EmitterPropertiesPanel(particleEmitter);
		emitterPropertiesPanel.add(emitterPanel);
		simPanel.getEmittersList().add(particleEmitter);
	}
	
	public List<ParticleEmitter> getEmittersList() {
		return simPanel.getEmittersList();
	}
	
	
}
