package com.apoapsys.astronomy.particleplayground;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.apoapsys.astronomy.math.Vector;

public class EmitterPropertiesPanel extends JPanel {
	
	private ParticleEmitter particleEmitter;
	
	private JTextField locationX;
	private JTextField locationY;
	private JTextField locationZ;
	
	private JTextField facingX;
	private JTextField facingY;
	private JTextField facingZ;
	
	private JTextField velocity;
	private JTextField mass;
	private JTextField radius;
	
	private JPanel color;
	
	public EmitterPropertiesPanel(final ParticleEmitter particleEmitter) {
		this.particleEmitter = particleEmitter;
		
		setLayout(new GridLayout(7, 1));
		
		this.setBorder(BorderFactory.createTitledBorder("Particle Emitter"));
		
		JPanel locationPanel = new JPanel();
		locationPanel.setLayout(new GridLayout(1, 4));
		locationPanel.add(new JLabel("Location:"));
		locationPanel.add(locationX = new JTextField(""+particleEmitter.getLocation().x));
		locationPanel.add(locationY = new JTextField(""+particleEmitter.getLocation().y));
		locationPanel.add(locationZ = new JTextField(""+particleEmitter.getLocation().z));
		add(locationPanel);
		
		JPanel facingPanel = new JPanel();
		facingPanel.setLayout(new GridLayout(1, 4));
		facingPanel.add(new JLabel("Facing:"));
		facingPanel.add(facingX = new JTextField(""+particleEmitter.getFacing().x));
		facingPanel.add(facingY = new JTextField(""+particleEmitter.getFacing().y));
		facingPanel.add(facingZ = new JTextField(""+particleEmitter.getFacing().z));
		add(facingPanel);
		
		JPanel velocityPanel = new JPanel();
		velocityPanel.setLayout(new GridLayout(1, 2));
		velocityPanel.add(new JLabel("Velocity:"));
		velocityPanel.add(velocity = new JTextField(""+particleEmitter.getVelocity()));
		add(velocityPanel);
		
		JPanel massPanel = new JPanel();
		massPanel.setLayout(new GridLayout(1, 2));
		massPanel.add(new JLabel("Mass:"));
		massPanel.add(mass = new JTextField(""+particleEmitter.getMass()));
		add(massPanel);
		
		JPanel radiusPanel = new JPanel();
		radiusPanel.setLayout(new GridLayout(1, 2));
		radiusPanel.add(new JLabel("Radius:"));
		radiusPanel.add(radius = new JTextField(""+particleEmitter.getRadius()));
		add(radiusPanel);
		
		JPanel colorPanel = new JPanel();
		colorPanel.setLayout(new GridLayout(1, 2));
		add(colorPanel);
		colorPanel.add(new JLabel("Color:"));
		color = new JPanel();
		color.setBackground(particleEmitter.getColor());
		colorPanel.add(color);
		color.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				Color newColor = JColorChooser.showDialog(
						EmitterPropertiesPanel.this,
	                     "Choose Particle Color",
	                     color.getBackground());
				color.setBackground(newColor);
				updateEmitterFromComponents();
			}
			
		});
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 2));
		add(buttonPanel);
		
		JButton btnCreate = new JButton("Add Particle");
		buttonPanel.add(btnCreate);
		btnCreate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				particleEmitter.createParticle();
			}
		});
		
		JButton btnUpdate = new JButton("Update Emitter");
		buttonPanel.add(btnUpdate);
		btnUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				updateEmitterFromComponents();
			}
		});
		
		particleEmitter.addPropertyListener(new EmitterPropertiesListener() {

			@Override
			public void onEmitterPropertiesModified(ParticleEmitter emitter) {
				updateComponentsFromEmitter();
			}
			
		});
	}
	
	
	public void updateComponentsFromEmitter() {
		locationX.setText(""+particleEmitter.getLocation().x);
		locationY.setText(""+particleEmitter.getLocation().y);
		locationZ.setText(""+particleEmitter.getLocation().z);
		
		facingX.setText(""+particleEmitter.getFacing().x);
		facingY.setText(""+particleEmitter.getFacing().y);
		facingZ.setText(""+particleEmitter.getFacing().z);
		
		velocity.setText(""+particleEmitter.getVelocity());
		mass.setText(""+particleEmitter.getMass());
		radius.setText(""+particleEmitter.getRadius());
		
		color.setBackground(particleEmitter.getColor());
		
		
		normalizeFacingValues();
	}
	
	private double getValueFromComponent(JTextField component) {
		return Double.parseDouble(component.getText());
	}
	
	public void normalizeFacingValues() {
		
		Vector facing = new Vector(getValueFromComponent(facingX), getValueFromComponent(facingY), getValueFromComponent(facingZ)).getNormalized();
		facingX.setText(""+facing.x);
		facingY.setText(""+facing.y);
		facingZ.setText(""+facing.z);
	}
	
	public void updateEmitterFromComponents() {
		normalizeFacingValues();
		particleEmitter.setSuppressPropertyChangeEvents(true);
		particleEmitter.setLocation(new Vector(getValueFromComponent(locationX), getValueFromComponent(locationY), getValueFromComponent(locationZ)));
		particleEmitter.setFacing(new Vector(getValueFromComponent(facingX), getValueFromComponent(facingY), getValueFromComponent(facingZ)));
		
		particleEmitter.setVelocity(getValueFromComponent(velocity));
		particleEmitter.setMass(getValueFromComponent(mass));
		particleEmitter.setRadius(getValueFromComponent(radius));
		particleEmitter.setSuppressPropertyChangeEvents(false);
		particleEmitter.setColor(color.getBackground());
		
	}
	
}
