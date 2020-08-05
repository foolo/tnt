package com.example;

import javax.swing.WindowConstants;

public class MainForm extends javax.swing.JFrame {

	public MainForm() {
		init();
		editableMarkupView1.addDocumentListener(); // done after setEditorKit which resets the internal document
		editableMarkupView1.setText("sample text sample text");
	}

	private void init() {
		editableMarkupView1 = new com.example.EditableMarkupView();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		add(editableMarkupView1);
		setMinimumSize(new java.awt.Dimension(400, 300));
		editableMarkupView1.addCaretListener((javax.swing.event.CaretEvent evt) -> {
			editableMarkupView1.caretUpdate();
		});
		pack();
	}

	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(() -> {
			MainForm mainForm = new MainForm();
			mainForm.setLocationRelativeTo(null);
			mainForm.setVisible(true);
		});
	}

	private EditableMarkupView editableMarkupView1;
}
