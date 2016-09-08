package me.Aron.Heinecke.VocableTrainer.lib;

import java.awt.Font;

import javax.swing.JLabel;

/**
 * Custom JLabel class, adding font initalization
 * @author Aron Heinecke
 *
 */
public class CLabel extends JLabel {
	private static final long serialVersionUID = 4542068845774566896L;

	public CLabel(String text, Font font){
		super(text);
		this.setFont(font);
	}
}
