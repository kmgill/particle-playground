package com.apoapsys.astronomy.particleplayground;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public abstract class AbstractOptionPanel extends JPanel {
	
	protected double getValueFromComponent(JTextField component) {
		return Double.parseDouble(component.getText());
	}
	
	protected JPanel createLabeledPanel(String label, Component ... components) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 1 + components.length));
		panel.add(new JLabel(label));
		for (Component component : components) {
			panel.add(component);
		}
		return panel;
	}
	
	protected JPanel createPanel(Component ... components) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, components.length));
		for (Component component : components) {
			panel.add(component);
		}
		return panel;
	}
}
