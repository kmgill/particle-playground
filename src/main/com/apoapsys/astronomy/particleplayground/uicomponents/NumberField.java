package com.apoapsys.astronomy.particleplayground.uicomponents;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JFormattedTextField;

public class NumberField extends JFormattedTextField {
	
	private static NumberFormat numberFormat;
	
	static {
		numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
		DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
		decimalFormat.setDecimalSeparatorAlwaysShown(true);
		decimalFormat.setMinimumFractionDigits(3);
		decimalFormat.setGroupingUsed(false);
	}
	
	private List<ValueChangedListener> valueChangedListeners = new ArrayList<>();
	
	public NumberField() {
		this(0.0);
	}
	
	public NumberField(double initialValue) {
		super(numberFormat);
		this.setValue(initialValue);
		
		this.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent arg0) {
				fireValueChangedListeners();
			}
			
		});
		
		this.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					fireValueChangedListeners();
				}
			}

		});
		
	}
	
	protected void fireValueChangedListeners() {
		double value = getNumberValue();
		for (ValueChangedListener l : valueChangedListeners) {
			l.onValueChanged(value);
		}
	}
	
	public void addValueChangedListener(ValueChangedListener l) {
		valueChangedListeners.add(l);
	}
	
	public void removeValueChangedListener(ValueChangedListener l) {
		valueChangedListeners.remove(l);
	}
	
	
	public double getNumberValue() {
		try {
			commitEdit();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Number value = (Number) getValue();
		return value.doubleValue();
	}
	
	
	
	public interface ValueChangedListener {
		public void onValueChanged(double newValue);
	}
}
