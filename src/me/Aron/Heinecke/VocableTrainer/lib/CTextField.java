package me.Aron.Heinecke.VocableTrainer.lib;

import java.awt.Font;

import javax.swing.JTextField;

/**
 * Custom JTextField providing an font initializer
 * @author Aron Heinecke
 */
public class CTextField extends JTextField {
	private static final long serialVersionUID = 4228275676842837120L;

	public CTextField(String text, Font font){
		super(text);
		this.setFont(font);
	}
}
