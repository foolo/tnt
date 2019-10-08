package tnt.editor;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import tnt.util.Settings;

public class SpecialCharacterDialog extends BaseDialog {

	public static class CharInfo {

		char character;
		String name;

		public CharInfo(char c, String name) {
			this.character = c;
			this.name = name;
		}

		@Override
		public String toString() {
			return "" + character;
		}
	}

	private static final String DEFAULT_CHARACTERS;

	static {
		StringBuilder sb = new StringBuilder();
		sb.append('\u00A0'); // NO-BREAK SPACE
		sb.append('\u2011'); // NON-BREAKING HYPHEN
		sb.append('\u2013'); // EN DASH
		sb.append('\u2014'); // EM DASH
		sb.append('\u00B0'); // DEGREE SIGN
		sb.append('\u201C'); // LEFT DOUBLE QUOTATION MARK
		sb.append('\u201D'); // RIGHT DOUBLE QUOTATION MARK
		sb.append('\u201E'); // DOUBLE LOW-9 QUOTATION MARK
		sb.append('\u201F'); // DOUBLE HIGH-REVERSED-9 QUOTATION MARK
		sb.append('\u00AB'); // LEFT-POINTING DOUBLE ANGLE QUOTATION MARK
		sb.append('\u00BB'); // RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK
		sb.append('\u2018'); // LEFT SINGLE QUOTATION MARK
		sb.append('\u2019'); // RIGHT SINGLE QUOTATION MARK
		sb.append('\u201A'); // SINGLE LOW-9 QUOTATION MARK
		sb.append('\u201B'); // SINGLE HIGH-REVERSED-9 QUOTATION MARK
		sb.append('\u2026'); // HORIZONTAL ELLIPSIS
		sb.append('\u00BF'); // INVERTED QUESTION MARK
		sb.append('\u00A1'); // INVERTED EXCLAMATION MARK
		sb.append('\u00A9'); // COPYRIGHT SIGN
		sb.append('\u00AE'); // REGISTERED SIGN
		sb.append('\u2122'); // TRADE MARK SIGN
		sb.append('\u00A5'); // YEN SIGN
		sb.append('\u00A2'); // CENT SIGN
		sb.append('\u00D7'); // MULTIPLICATION SIGN
		sb.append('\u00F7'); // DIVISION SIGN
		sb.append('\u00B1'); // PLUS-MINUS SIGN
		sb.append('\u2264'); // LESS-THAN OR EQUAL TO
		sb.append('\u2265'); // GREATER-THAN OR EQUAL TO
		sb.append('\u2260'); // NOT EQUAL TO
		sb.append('\u2248'); // ALMOST EQUAL TO
		sb.append('\u2030'); // PER MILLE SIGN
		sb.append('\u03B1'); // GREEK SMALL LETTER ALPHA
		sb.append('\u0394'); // GREEK CAPITAL LETTER DELTA
		sb.append('\u03BC'); // GREEK SMALL LETTER MU
		sb.append('\u03C0'); // GREEK SMALL LETTER PI
		sb.append('\u03A9'); // GREEK CAPITAL LETTER OMEGA
		DEFAULT_CHARACTERS = sb.toString();
	}

	class CodePointDocumentListener implements DocumentListener {

		void update() {
			try {
				int i = Integer.parseInt(jTextFieldCodePoint.getText(), 16);
				if (i > 0xffff || i < 0) {
					throw new NumberFormatException();
				}
				if (i == 0xffff) {
					// workaround for null pointer exception in java.awt.font.TextLayout.getBaselineFromGraphic, https://bugs.openjdk.java.net/browse/JDK-8037965
					throw new NumberFormatException();
				}
				char c = (char) i;
				jTextFieldChar.setText("" + c);
				jTextAreaCharName.setText(Character.getName(c));
				jButtonOk.setEnabled(true);
			}
			catch (NumberFormatException ex) {
				jTextFieldChar.setText("");
				jTextAreaCharName.setText("");
				jButtonOk.setEnabled(false);
			}
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			update();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			update();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
		}
	}

	public SpecialCharacterDialog(java.awt.Frame parent) {
		super(parent);
		initComponents();
		initButtons(jButtonOk, jButtonCancel);
		characterTableCommon.update(DEFAULT_CHARACTERS);
		characterTableRecent.update(Settings.getCustomSpecialCharacters());
		characterTableCommon.addSelectionListener((ListSelectionEvent e) -> {
			updateCodePoint(characterTableCommon);
		});
		characterTableRecent.addSelectionListener((ListSelectionEvent e) -> {
			updateCodePoint(characterTableRecent);
		});
		jTextFieldChar.setBorder(new LineBorder(Color.BLACK));
		jTextFieldCodePoint.getDocument().addDocumentListener(new CodePointDocumentListener());
		jTextFieldCodePoint.setText("");
		characterTableCommon.addEnterAction(enterAction);
		characterTableRecent.addEnterAction(enterAction);
		characterTableRecent.grabFocus();
	}

	Action enterAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent e) {
			jButtonOk.doClick();
		}
	};

	String getSelectedChar() {
		return jTextFieldChar.getText();
	}

	void updateCodePoint(CharacterTable table) {
		if (table.getSelectionModel().isSelectionEmpty()) {
			return;
		}
		Character c = table.getSelectedChar();
		if (c == null) {
			jTextFieldCodePoint.setText("");
		}
		else {
			jTextFieldCodePoint.setText(String.format("%04X", (int) c));
		}
	}

	void addCustomCharacters(String s) {
		String chars = Settings.getCustomSpecialCharacters();
		for (char c : s.toCharArray()) {
			if (chars.contains("" + c) == false) {
				chars = chars + c;
			}
		}
		Settings.setCustomSpecialCharacters(chars);
		characterTableRecent.update(chars);
	}

	void removeCustomCharacter(char c) {
		String chars = Settings.getCustomSpecialCharacters();
		chars = chars.replace("" + c, "");
		Settings.setCustomSpecialCharacters(chars);
		characterTableRecent.update(chars);
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButtonOk = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButtonAdd = new javax.swing.JButton();
        jTextFieldChar = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextAreaCharName = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        characterTableRecent = new tnt.editor.CharacterTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        characterTableCommon = new tnt.editor.CharacterTable();
        jTextFieldCodePoint = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Insert special character");
        setResizable(false);

        jButtonOk.setText("OK");
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOkActionPerformed(evt);
            }
        });

        jButtonCancel.setText("Cancel");

        jLabel1.setText("Common characters");

        jLabel2.setText("Custom/recent characters");

        jButtonAdd.setText("Add...");
        jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });

        jTextFieldChar.setEditable(false);
        jTextFieldChar.setBackground(javax.swing.UIManager.getDefaults().getColor("TextField.background"));
        jTextFieldChar.setFont(new Font(Font.SERIF, Font.PLAIN, 48));
        jTextFieldChar.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldChar.setText("A");

        jScrollPane3.setBorder(null);
        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane3.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        jTextAreaCharName.setEditable(false);
        jTextAreaCharName.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        jTextAreaCharName.setColumns(20);
        jTextAreaCharName.setLineWrap(true);
        jTextAreaCharName.setRows(5);
        jTextAreaCharName.setText("jTextAreaCharName\njTextAreaCharName");
        jTextAreaCharName.setWrapStyleWord(true);
        jScrollPane3.setViewportView(jTextAreaCharName);

        jLabel4.setText("Code point");

        characterTableRecent.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        characterTableRecent.setRowHeight(36);
        characterTableRecent.setTableHeader(null);
        characterTableRecent.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                characterTableRecentFocusGained(evt);
            }
        });
        characterTableRecent.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                characterTableRecentMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                characterTableRecentMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                characterTableRecentMouseReleased(evt);
            }
        });
        jScrollPane2.setViewportView(characterTableRecent);

        characterTableCommon.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        characterTableCommon.setRowHeight(36);
        characterTableCommon.setTableHeader(null);
        characterTableCommon.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                characterTableCommonFocusGained(evt);
            }
        });
        characterTableCommon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                characterTableCommonMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(characterTableCommon);

        jTextFieldCodePoint.setText("0000");
        jTextFieldCodePoint.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldCodePointFocusGained(evt);
            }
        });
        jTextFieldCodePoint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldCodePointActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel3.setText("U+");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 390, Short.MAX_VALUE)
                        .addComponent(jButtonOk, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jButtonAdd)
                            .addComponent(jLabel2))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jTextFieldChar)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(0, 0, 0)
                                .addComponent(jTextFieldCodePoint)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextFieldChar, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jTextFieldCodePoint, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonAdd)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonOk)
                    .addComponent(jButtonCancel))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddActionPerformed
		String s = JOptionPane.showInputDialog(this, "Paste the special character(s)");
		if (s.isEmpty() == false) {
			addCustomCharacters(s);
		}
    }//GEN-LAST:event_jButtonAddActionPerformed

    private void jTextFieldCodePointActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldCodePointActionPerformed
		jButtonOk.doClick();
    }//GEN-LAST:event_jTextFieldCodePointActionPerformed

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
		addCustomCharacters(jTextFieldChar.getText());
		result = true;
		setVisible(false);
    }//GEN-LAST:event_jButtonOkActionPerformed

	void showPopup(MouseEvent evt) {
		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem("Remove");
		menuItem.addActionListener((ActionEvent e) -> {
			Character selectedChar = characterTableRecent.getSelectedChar();
			if (selectedChar != null) {
				removeCustomCharacter(selectedChar);
				jTextFieldCodePoint.setText("");
			}
		});
		popupMenu.add(menuItem);
		popupMenu.show(characterTableRecent, evt.getX(), evt.getY());
	}

    private void characterTableRecentMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_characterTableRecentMousePressed
		if (evt.isPopupTrigger()) {
			showPopup(evt);
		}
    }//GEN-LAST:event_characterTableRecentMousePressed

    private void characterTableRecentMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_characterTableRecentMouseReleased
		if (evt.isPopupTrigger()) {
			showPopup(evt);
		}
    }//GEN-LAST:event_characterTableRecentMouseReleased

    private void characterTableCommonFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_characterTableCommonFocusGained
		characterTableRecent.clearSelection();
    }//GEN-LAST:event_characterTableCommonFocusGained

    private void characterTableRecentFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_characterTableRecentFocusGained
		characterTableCommon.clearSelection();
    }//GEN-LAST:event_characterTableRecentFocusGained

    private void jTextFieldCodePointFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldCodePointFocusGained
		characterTableRecent.clearSelection();
		characterTableCommon.clearSelection();
    }//GEN-LAST:event_jTextFieldCodePointFocusGained

    private void characterTableCommonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_characterTableCommonMouseClicked
		if (evt.getClickCount() == 2 && evt.getButton() == MouseEvent.BUTTON1) {
			jButtonOk.doClick();
		}
    }//GEN-LAST:event_characterTableCommonMouseClicked

    private void characterTableRecentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_characterTableRecentMouseClicked
		if (evt.getClickCount() == 2 && evt.getButton() == MouseEvent.BUTTON1) {
			jButtonOk.doClick();
		}
    }//GEN-LAST:event_characterTableRecentMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private tnt.editor.CharacterTable characterTableCommon;
    private tnt.editor.CharacterTable characterTableRecent;
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTextArea jTextAreaCharName;
    private javax.swing.JTextField jTextFieldChar;
    private javax.swing.JTextField jTextFieldCodePoint;
    // End of variables declaration//GEN-END:variables
}
