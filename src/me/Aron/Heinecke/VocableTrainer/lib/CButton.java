package me.Aron.Heinecke.VocableTrainer.lib;

import java.awt.Font;

import javax.swing.JButton;

/**
 * Cusom button with font handler
 * @author aron
 *
 */
public class CButton extends JButton {
	
	private static final long serialVersionUID = 7603166773217229165L;

	public CButton(String text, Font font){
		super(text);
		this.setFont(font);
	}

}
