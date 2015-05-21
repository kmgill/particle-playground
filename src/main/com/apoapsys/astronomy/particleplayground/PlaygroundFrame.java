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
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

import com.apoapsys.astronomy.math.Vector;
import com.apoapsys.astronomy.particleplayground.uicomponents.RunPauseButton;
import com.apoapsys.astronomy.simulations.nbody.Particle;
import com.apoapsys.astronomy.simulations.nbody.leapfrog.LeapFrogSimulator;
import com.apoapsys.astronomy.simulations.nbody.leapfrog.NewtonianGravityForceProviderImpl;
import com.apoapsys.astronomy.simulations.nbody.leapfrog.ParticlePropulsionForceProviderImpl;
import com.apoapsys.astronomy.simulations.nbody.leapfrog.SimulationForceProvider;
import com.jogamp.opengl.util.FPSAnimator;

public class PlaygroundFrame extends JFrame {
	
	private LeapFrogSimulator simulator;
	private SimulationThread simThread;
	private ParticleGJPanel simPanel;
	
	private JPanel emitterPropertiesPanel;
	
	public PlaygroundFrame(final LeapFrogSimulator simulator, final SimulationThread simThread) {
		setTitle("Particle Playground");
		setSize(1300, 700);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.simulator = simulator;
		this.simThread = simThread;
		
		setLayout(new BorderLayout());
		
		simPanel = new ParticleGJPanel(simulator);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		//JPanel generalPanel = new JPanel();
		//generalPanel.setLayout(new GridLayout(2, 1));
		tabbedPane.add("Simulation", new SimulationOptionsPanel(simulator, simThread, simPanel));

		
		JPanel forcesPanel = new JPanel();
		forcesPanel.setLayout(new GridLayout(3, 1, 3, 3));
		tabbedPane.add("Forces", forcesPanel);
		
		// Create initial force panels with any pre-configured forces
		for (SimulationForceProvider provider : simulator.getSimulationForceProviders()) {
			forcesPanel.add(createForcePanel(provider));
		}
		for (Particle particle : simulator.getParticles()) {
			for (SimulationForceProvider provider : particle.ownForces) {
				forcesPanel.add(createForcePanel(provider));
			}
		}
		
		
		emitterPropertiesPanel = new JPanel();
		emitterPropertiesPanel.setLayout(new GridLayout(3, 1, 3, 3));
		
		JScrollPane scroll = new JScrollPane(emitterPropertiesPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		tabbedPane.add("Emitters", scroll);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, simPanel, tabbedPane);
		splitPane.setResizeWeight(1.0);
		add(splitPane, BorderLayout.CENTER);
		
		
		JToolBar toolbar = new JToolBar();
		add(toolbar, BorderLayout.NORTH);
	
		toolbar.add(new RunPauseButton(simThread));
		
		
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
				emitter.setLocation(new Vector(0, 0, 1.5E11));
				emitter.setMass(270000000.0);
				emitter.setVelocity(23000.0);
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
		
		JButton btnReset = new JButton("Reset");
		toolbar.add(btnReset);
		btnReset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				resetSimulation();
			}
			
		});
		
		
		final FPSAnimator animator = new FPSAnimator(simPanel, 60);
		animator.start();
		
	}
	
	protected void resetSimulation() {
		simThread.setPaused(true);
		// Delete all particles
		simThread.setPaused(false);
	}
	
	protected JPanel createForcePanel(SimulationForceProvider provider) {
		if (provider instanceof NewtonianGravityForceProviderImpl) {
			return new NewtonianForcePanel((NewtonianGravityForceProviderImpl) provider);
		} else if (provider instanceof ParticlePropulsionForceProviderImpl) {
			return new PropulsionForcePanel((ParticlePropulsionForceProviderImpl)provider);
		} else {
			return null;
		}
	}
	
	protected void createSolarSystem() {
		try {
			simThread.setPaused(true);
			SolarSystemCreator.create(simulator, false);
			simThread.setPaused(false);
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
		
		simThread.setPaused(true);
		simulator.addParticle(emitter0.createParticle());
		simThread.setPaused(false);
	}
	
	public void addParticleEmitter(ParticleEmitter particleEmitter) {
		particleEmitter.addParticleCreateListener(new ParticleCreateListener() {
			@Override
			public void onParticleCreated(Particle particle) {
				simThread.setPaused(true);
				simulator.addParticle(particle);
				simThread.setPaused(false);
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
