package com.apoapsys.astronomy.particleplayground;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;

import com.apoapsys.astronomy.particleplayground.uicomponents.SimulationSpeedSelect;
import com.apoapsys.astronomy.simulations.nbody.leapfrog.LeapFrogSimulator;

public class SimulationOptionsPanel extends AbstractOptionPanel {
	
	public SimulationOptionsPanel(final LeapFrogSimulator simulator, final SimulationThread simThread, final ParticleGJPanel simPanel) {
		setLayout(new GridLayout(10, 1));
		
		final JCheckBox chkShowOrbits = new JCheckBox("Show Orbits");
		chkShowOrbits.setSelected(simPanel.getShowOrbits());
		chkShowOrbits.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				simPanel.setShowOrbits(chkShowOrbits.isSelected());
			}
			
		});
		add(chkShowOrbits);
		
		final JCheckBox chkShowGrid = new JCheckBox("Show Grid");
		chkShowGrid.setSelected(simPanel.getShowGrid());
		chkShowGrid.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				simPanel.setShowGrid(chkShowGrid.isSelected());
			}
		});
		add(chkShowGrid);
		
		SimulationSpeedSelect speedSelect = new SimulationSpeedSelect();
		add(createLabeledPanel("Speed:", speedSelect));
		speedSelect.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				int speed = (Integer) e.getItem();
				simThread.setSpeed(speed);
			}
			
		});
		
	}
	
}	
