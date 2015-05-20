package com.apoapsys.astronomy.particleplayground.uicomponents;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.event.ListDataListener;

public class SimulationSpeedSelect extends JComboBox {
	
	private SpeedComboBoxModel model = new SpeedComboBoxModel();
	
	public SimulationSpeedSelect() {
		setModel(model);
		
		
	}
	
	class SpeedComboBoxModel implements ComboBoxModel<Integer> {
		private int[] speeds = {1, 10, 100, 1000, 10000, 100000, 1000000};
		private int selectedItem = 1;
		
		private List<ListDataListener> listeners = new ArrayList<>();
		
		@Override
		public int getSize() {
			return speeds.length;
		}

		@Override
		public Integer getElementAt(int index) {
			return speeds[index];
		}

		@Override
		public void addListDataListener(ListDataListener l) {
			listeners.add(l);
		}

		@Override
		public void removeListDataListener(ListDataListener l) {
			listeners.remove(l);
		}

		@Override
		public void setSelectedItem(Object anItem) {
			selectedItem = (Integer) anItem;
		}

		@Override
		public Object getSelectedItem() {
			return selectedItem;
		}
		
	}
	
}
