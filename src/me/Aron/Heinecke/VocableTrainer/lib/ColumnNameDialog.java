/* Copyright 2016 Aron Heinecke. See the COPYRIGHT
 * file at the top-level directory of this distribution.
 * 
 * Licensed under the MIT license
 * <LICENSE-MIT or http://opensource.org/licenses/MIT>.
 * This file may not be copied, modified, or distributed
 * except according to those terms.
 */
package me.Aron.Heinecke.VocableTrainer.lib;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

/**
 * Custom dialog for column name changes
 * @author Aron Heinecke
 */
public class ColumnNameDialog extends JDialog {

	private static final long serialVersionUID = 6140699368341282787L;
	private final JPanel contentPanel = new JPanel();
	private JTextField colA;
	private JTextField colB;
	/**
	 * Create the dialog.
	 */
	public ColumnNameDialog(JFrame parent,TDTableInfoElement table) {
		super(parent);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		setBounds(100, 100, 450, 178);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblPleaseSetThe = new JLabel("Please set the new column names");
		lblPleaseSetThe.setHorizontalAlignment(SwingConstants.CENTER);
		lblPleaseSetThe.setAlignmentY(5.0f);
		lblPleaseSetThe.setAlignmentX(5.0f);
		contentPanel.add(lblPleaseSetThe, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		contentPanel.add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("", "[][grow]", "[][][]"));
		
		JLabel lblColumn = new JLabel("Column");
		panel.add(lblColumn, "cell 0 0");
		
		JLabel lblA = new JLabel("A:");
		panel.add(lblA, "cell 0 1,alignx trailing");
		
		colA = new JTextField(table.getColumn_a());
		panel.add(colA, "cell 1 1,growx");
		colA.setColumns(10);
		
		JLabel lblB = new JLabel("B:");
		panel.add(lblB, "cell 0 2,alignx trailing");
		colB = new JTextField(table.getColumn_b());
		panel.add(colB, "cell 1 2,growx");
		colB.setColumns(10);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(colA.getText().equals("") || colB.getText().equals("")){
							return;
						}
						table.setColumn_a(colA.getText());
						table.setColumn_b(colB.getText());
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(parent);
		setVisible(true);
	}
}
