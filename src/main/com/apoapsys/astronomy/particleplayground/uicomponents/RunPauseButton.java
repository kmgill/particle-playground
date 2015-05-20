package com.apoapsys.astronomy.particleplayground.uicomponents;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import com.apoapsys.astronomy.particleplayground.SimulationThread;

public class RunPauseButton extends JButton {
	
	public RunPauseButton(final SimulationThread simThread) {
		
		
		
		final ImageIcon runIcon = new ImageIcon(RunPauseButton.class.getResource("/icons/lrun_obj.gif"));
		final ImageIcon pauseIcon = new ImageIcon(RunPauseButton.class.getResource("/icons/suspend_co.gif"));
		
		setText(simThread.isPaused() ? "Start" : "Pause");
		setIcon(simThread.isPaused() ? runIcon : pauseIcon);
		
		this.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (simThread.isPaused()) {
					setText("Pause");
					simThread.setPaused(false);
					setIcon(pauseIcon);
				} else {
					setText("Resume");
					simThread.setPaused(true);
					setIcon(runIcon);
				}
			}
			
		});
		
	}
	
}
