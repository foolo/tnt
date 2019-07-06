package editor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import util.Log;
import util.Settings;

public final class CreatePackageDialog extends javax.swing.JDialog {

	class ValueChangedDocumentListener implements DocumentListener {

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
			update();
		}
	}

	private boolean result = false;

	boolean getResult() {
		return result;
	}

	public CreatePackageDialog(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		initComponents();

		jTextFieldInputFile.getDocument().addDocumentListener(new ValueChangedDocumentListener());
		jTextFieldCommonDir.getDocument().addDocumentListener(new ValueChangedDocumentListener());
		jTextFieldPackageName.getDocument().addDocumentListener(new ValueChangedDocumentListener());

		jTextFieldCommonDir.setText(Settings.getPackageDirectory().getAbsolutePath());

		String timestamp = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss"));
		String suggestedPackageName = "tnt_" + timestamp;
		jTextFieldPackageName.setText(suggestedPackageName);

		sourceLanguageComboBox.setLanguages(languages);
		targetLanguageComboBox.setLanguages(languages);

		update();
	}

	File getCommonDirectory() {
		return new File(jTextFieldCommonDir.getText()).getAbsoluteFile();
	}

	File getPackagePath() {
		return new File(getCommonDirectory(), jTextFieldPackageName.getText());
	}

	String getPackageName() {
		return jTextFieldPackageName.getText();
	}

	String getInputFile() {
		return jTextFieldInputFile.getText();
	}

	String getSourceLanguage() {
		return sourceLanguageComboBox.getSelectedLanguageCode();
	}

	String getTargetLanguage() {
		return targetLanguageComboBox.getSelectedLanguageCode();
	}

	String preValidateInput() {
		if (jTextFieldInputFile.getText().isEmpty()) {
			return "";
		}
		if (sourceLanguageComboBox.getSelectedLanguageCode().isEmpty()) {
			return "Please select a source language.";
		}
		if (targetLanguageComboBox.getSelectedLanguageCode().isEmpty()) {
			return "Please select a target language.";
		}
		if (new File(jTextFieldInputFile.getText()).exists() == false) {
			return "Input file does not exist.";
		}
		if (jTextFieldCommonDir.getText().isEmpty()) {
			return "Common package directory must not be empty.";
		}
		if (jTextFieldPackageName.getText().isEmpty()) {
			return "Package name must not be empty.";
		}
		File packagePath = getPackagePath();
		if (Files.exists(packagePath.toPath())) {
			return "Package path already exists: " + packagePath.getAbsolutePath();
		}
		return null;
	}

	void update() {
		File packagePath = getPackagePath();
		jLabelPackageDir.setText(packagePath.getAbsolutePath());
		String error = preValidateInput();
		jButtonOk.setEnabled(error == null);
		if (error != null) {
			jLabelError.setText(error);
		}
		else {
			jLabelError.setText("");
		}
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jButtonChooseInputFiles = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldCommonDir = new javax.swing.JTextField();
        jButtonChoosePackageDirectory = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldPackageName = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabelPackageDir = new javax.swing.JLabel();
        jButtonOk = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jLabelError = new javax.swing.JLabel();
        jTextFieldInputFile = new javax.swing.JTextField();
        sourceLanguageComboBox = new editor.LanguageComboBox();
        targetLanguageComboBox = new editor.LanguageComboBox();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Input file");

        jButtonChooseInputFiles.setText("Choose...");
        jButtonChooseInputFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonChooseInputFilesActionPerformed(evt);
            }
        });

        jLabel2.setText("Common package directory");

        jButtonChoosePackageDirectory.setText("Choose...");
        jButtonChoosePackageDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonChoosePackageDirectoryActionPerformed(evt);
            }
        });

        jLabel3.setText("Package name");

        jLabel4.setText("Package path");

        jLabelPackageDir.setText("jLabelPackageDir");
        jLabelPackageDir.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButtonOk.setText("OK");
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOkActionPerformed(evt);
            }
        });

        jButtonCancel.setText("Cancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        jLabelError.setForeground(new java.awt.Color(255, 0, 0));
        jLabelError.setText("jLabelError");

        sourceLanguageComboBox.setMaximumRowCount(30);
        sourceLanguageComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sourceLanguageComboBoxActionPerformed(evt);
            }
        });

        targetLanguageComboBox.setMaximumRowCount(30);
        targetLanguageComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                targetLanguageComboBoxActionPerformed(evt);
            }
        });

        jLabel5.setText("Source language:");

        jLabel6.setText("Target language:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldInputFile)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(sourceLanguageComboBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                            .addComponent(targetLanguageComboBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)))
                    .addComponent(jLabelError, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextFieldCommonDir)
                    .addComponent(jLabelPackageDir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(137, 280, Short.MAX_VALUE)
                        .addComponent(jButtonOk, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonChooseInputFiles))
                            .addComponent(jLabel3)
                            .addComponent(jTextFieldPackageName, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonChoosePackageDirectory))
                            .addComponent(jLabel4))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jButtonChooseInputFiles))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldInputFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sourceLanguageComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(targetLanguageComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jButtonChoosePackageDirectory))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldCommonDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldPackageName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelPackageDir)
                .addGap(18, 18, 18)
                .addComponent(jLabelError)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonOk)
                    .addComponent(jButtonCancel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonChooseInputFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonChooseInputFilesActionPerformed
		JFileChooser fc = new JFileChooser(Settings.getInputFileDirectory());
		int returnVal = fc.showOpenDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}
		Settings.setInputFileDirectory(fc.getSelectedFile());
		jTextFieldInputFile.setText(fc.getSelectedFile().getAbsolutePath());
		update();
    }//GEN-LAST:event_jButtonChooseInputFilesActionPerformed

    private void jButtonChoosePackageDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonChoosePackageDirectoryActionPerformed
		JFileChooser fc = new JFileChooser(Settings.getPackageDirectory());
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}
		jTextFieldCommonDir.setText(fc.getSelectedFile().getAbsolutePath());
		update();
    }//GEN-LAST:event_jButtonChoosePackageDirectoryActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
		setVisible(false);
    }//GEN-LAST:event_jButtonCancelActionPerformed

	void showError(String msg) {
		JOptionPane.showMessageDialog(this, msg, "", JOptionPane.ERROR_MESSAGE);
	}

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
		String preValidateResult = preValidateInput();
		if (preValidateResult != null) {
			JOptionPane.showMessageDialog(this, preValidateResult, "", JOptionPane.ERROR_MESSAGE);
			return;
		}

		Path commonDir = getCommonDirectory().toPath();
		if (Files.exists(commonDir)) {
			if (Files.isWritable(commonDir) == false) {
				showError("Directory is not writable: " + commonDir);
				return;
			}
		}
		else {
			try {
				Files.createDirectories(commonDir);
			}
			catch (IOException ex) {
				showError("Common package directory can not be created: " + commonDir);
				return;
			}
		}

		// create the package directory and delete it again to make sure it can be created
		Path packageDirectory = getPackagePath().toPath();
		try {
			Files.createDirectories(packageDirectory);
		}
		catch (IOException ex) {
			showError("Package directory can not be created: " + packageDirectory);
			return;
		}
		try {
			Files.deleteIfExists(packageDirectory);
		}
		catch (IOException ex) {
			Log.warn("Could not delete directory: " + packageDirectory + " (" + ex.toString() + ")");
		}

		Settings.setPackageDirectory(commonDir.toFile());
		result = true;
		setVisible(false);
    }//GEN-LAST:event_jButtonOkActionPerformed

    private void sourceLanguageComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sourceLanguageComboBoxActionPerformed
		update();
    }//GEN-LAST:event_sourceLanguageComboBoxActionPerformed

    private void targetLanguageComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_targetLanguageComboBoxActionPerformed
		update();
    }//GEN-LAST:event_targetLanguageComboBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonChooseInputFiles;
    private javax.swing.JButton jButtonChoosePackageDirectory;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabelError;
    private javax.swing.JLabel jLabelPackageDir;
    private javax.swing.JTextField jTextFieldCommonDir;
    private javax.swing.JTextField jTextFieldInputFile;
    private javax.swing.JTextField jTextFieldPackageName;
    private editor.LanguageComboBox sourceLanguageComboBox;
    private editor.LanguageComboBox targetLanguageComboBox;
    // End of variables declaration//GEN-END:variables

	static final ArrayList<LanguageComboBox.Language> languages = new ArrayList<>();

	static void addLanguage(String name, String code) {
		languages.add(new LanguageComboBox.Language(name, code));
	}

	static {
		addLanguage("Abkhazian", "ab");
		addLanguage("Afar", "aa");
		addLanguage("Afrikaans", "af");
		addLanguage("Akan", "ak");
		addLanguage("Albanian", "sq");
		addLanguage("Amharic", "am");
		addLanguage("Arabic", "ar");
		addLanguage("Arabic (Algeria)", "ar-DZ");
		addLanguage("Arabic (Bahrain)", "ar-BH");
		addLanguage("Arabic (Egypt)", "ar-EG");
		addLanguage("Arabic (Iraq)", "ar-IQ");
		addLanguage("Arabic (Jordan)", "ar-JO");
		addLanguage("Arabic (Kuwait)", "ar-KW");
		addLanguage("Arabic (Lebanon)", "ar-LB");
		addLanguage("Arabic (Libya)", "ar-LY");
		addLanguage("Arabic (Morocco)", "ar-MA");
		addLanguage("Arabic (Oman)", "ar-OM");
		addLanguage("Arabic (Qatar)", "ar-QA");
		addLanguage("Arabic (Saudi Arabia)", "ar-SA");
		addLanguage("Arabic (Syrian Arab Republic)", "ar-SY");
		addLanguage("Arabic (Tunisia)", "ar-TN");
		addLanguage("Arabic (United Arab Emirates)", "ar-AE");
		addLanguage("Arabic (Yemen)", "ar-YE");
		addLanguage("Aragonese", "an");
		addLanguage("Armenian", "hy");
		addLanguage("Assamese", "as");
		addLanguage("Avaric", "av");
		addLanguage("Avestan", "ae");
		addLanguage("Aymara", "ay");
		addLanguage("Azerbaijani", "az");
		addLanguage("Bambara", "bm");
		addLanguage("Bashkir", "ba");
		addLanguage("Basque", "eu");
		addLanguage("Belarusian", "be");
		addLanguage("Bengali", "bn");
		addLanguage("Bihari languages", "bh");
		addLanguage("Bislama", "bi");
		addLanguage("Bosnian", "bs");
		addLanguage("Breton", "br");
		addLanguage("Bulgarian", "bg");
		addLanguage("Burmese", "my");
		addLanguage("Catalan", "ca");
		addLanguage("Chamorro", "ch");
		addLanguage("Chechen", "ce");
		addLanguage("Chinese", "zh");
		addLanguage("Chinese (China)", "zh-CN");
		addLanguage("Chinese (Han [Simplified variant])", "zh-Hans");
		addLanguage("Chinese (Han [Traditional variant])", "zh-Hant");
		addLanguage("Chinese (Hong Kong)", "zh-HK");
		addLanguage("Chinese (Singapore)", "zh-SG");
		addLanguage("Chinese (Taiwan, Province of China)", "zh-TW");
		addLanguage("Church Slavic", "cu");
		addLanguage("Chuvash", "cv");
		addLanguage("Cornish", "kw");
		addLanguage("Corsican", "co");
		addLanguage("Cree", "cr");
		addLanguage("Croatian", "hr");
		addLanguage("Czech", "cs");
		addLanguage("Danish", "da");
		addLanguage("Dhivehi", "dv");
		addLanguage("Dutch", "nl");
		addLanguage("Dutch (Belgium)", "nl-BE");
		addLanguage("Dzongkha", "dz");
		addLanguage("English", "en");
		addLanguage("English (Australia)", "en-AU");
		addLanguage("English (Belize)", "en-BZ");
		addLanguage("English (Canada)", "en-CA");
		addLanguage("English (Caribbean)", "en-029");
		addLanguage("English (Ireland)", "en-IE");
		addLanguage("English (Jamaica)", "en-JM");
		addLanguage("English (New Zealand)", "en-NZ");
		addLanguage("English (South Africa)", "en-ZA");
		addLanguage("English (Trinidad and Tobago)", "en-TT");
		addLanguage("English (United Kingdom)", "en-GB");
		addLanguage("English (United States)", "en-US");
		addLanguage("Esperanto", "eo");
		addLanguage("Estonian", "et");
		addLanguage("Ewe", "ee");
		addLanguage("Faroese", "fo");
		addLanguage("Fijian", "fj");
		addLanguage("Filipino", "fil");
		addLanguage("Finnish", "fi");
		addLanguage("French", "fr");
		addLanguage("French (Belgium)", "fr-BE");
		addLanguage("French (Canada)", "fr-CA");
		addLanguage("French (France)", "fr-FR");
		addLanguage("French (Luxembourg)", "fr-LU");
		addLanguage("French (Switzerland)", "fr-CH");
		addLanguage("Fulah", "ff");
		addLanguage("Galician", "gl");
		addLanguage("Ganda", "lg");
		addLanguage("Georgian", "ka");
		addLanguage("German", "de");
		addLanguage("German (Austria)", "de-AT");
		addLanguage("German (Germany)", "de-DE");
		addLanguage("German (Liechtenstein)", "de-LI");
		addLanguage("German (Luxembourg)", "de-LU");
		addLanguage("German (Switzerland)", "de-CH");
		addLanguage("Greek", "el");
		addLanguage("Guarani", "gn");
		addLanguage("Gujarati", "gu");
		addLanguage("Haitian", "ht");
		addLanguage("Hausa", "ha");
		addLanguage("Hebrew", "he");
		addLanguage("Herero", "hz");
		addLanguage("Hindi", "hi");
		addLanguage("Hiri Motu", "ho");
		addLanguage("Hungarian", "hu");
		addLanguage("Icelandic", "is");
		addLanguage("Ido", "io");
		addLanguage("Igbo", "ig");
		addLanguage("Indonesian", "id");
		addLanguage("Indonesian", "in");
		addLanguage("Interlingua ", "ia");
		addLanguage("Interlingue", "ie");
		addLanguage("Inuktitut", "iu");
		addLanguage("Inupiaq", "ik");
		addLanguage("Irish", "ga");
		addLanguage("Italian", "it");
		addLanguage("Italian (Italy)", "it-IT");
		addLanguage("Italian (Switzerland)", "it-CH");
		addLanguage("Japanese", "ja");
		addLanguage("Japanese (Japan)", "ja-JP");
		addLanguage("Javanese", "jv");
		addLanguage("Kalaallisut", "kl");
		addLanguage("Kannada", "kn");
		addLanguage("Kanuri", "kr");
		addLanguage("Kashmiri", "ks");
		addLanguage("Kazakh", "kk");
		addLanguage("Khmer", "km");
		addLanguage("Kikuyu", "ki");
		addLanguage("Kinyarwanda", "rw");
		addLanguage("Kirghiz", "ky");
		addLanguage("Komi", "kv");
		addLanguage("Kongo", "kg");
		addLanguage("Korean", "ko");
		addLanguage("Kuanyama", "kj");
		addLanguage("Kurdish", "ku");
		addLanguage("Lao", "lo");
		addLanguage("Latin", "la");
		addLanguage("Latvian", "lv");
		addLanguage("Limburgan", "li");
		addLanguage("Lingala", "ln");
		addLanguage("Lithuanian", "lt");
		addLanguage("Luba-Katanga", "lu");
		addLanguage("Luxembourgish", "lb");
		addLanguage("Macedonian", "mk");
		addLanguage("Malagasy", "mg");
		addLanguage("Malay ", "ms");
		addLanguage("Malayalam", "ml");
		addLanguage("Maltese", "mt");
		addLanguage("Manx", "gv");
		addLanguage("Maori", "mi");
		addLanguage("Marathi", "mr");
		addLanguage("Marshallese", "mh");
		addLanguage("Moldavian", "mo");
		addLanguage("Mongolian", "mn");
		addLanguage("Nauru", "na");
		addLanguage("Navajo", "nv");
		addLanguage("Ndonga", "ng");
		addLanguage("Nepali ", "ne");
		addLanguage("North Ndebele", "nd");
		addLanguage("Northern Sami", "se");
		addLanguage("Norwegian", "no");
		addLanguage("Norwegian Bokmål", "nb");
		addLanguage("Norwegian Nynorsk", "nn");
		addLanguage("Nyanja", "ny");
		addLanguage("Occitan ", "oc");
		addLanguage("Ojibwa", "oj");
		addLanguage("Oriya ", "or");
		addLanguage("Oromo", "om");
		addLanguage("Ossetian", "os");
		addLanguage("Pali", "pi");
		addLanguage("Panjabi", "pa");
		addLanguage("Persian", "fa");
		addLanguage("Polish", "pl");
		addLanguage("Portuguese", "pt");
		addLanguage("Portuguese (Brazil)", "pt-BR");
		addLanguage("Pushto", "ps");
		addLanguage("Quechua", "qu");
		addLanguage("Romanian", "ro");
		addLanguage("Romansh", "rm");
		addLanguage("Rundi", "rn");
		addLanguage("Russian", "ru");
		addLanguage("Samoan", "sm");
		addLanguage("Sango", "sg");
		addLanguage("Sanskrit", "sa");
		addLanguage("Sardinian", "sc");
		addLanguage("Scottish Gaelic", "gd");
		addLanguage("Serbian", "sr");
		addLanguage("Shona", "sn");
		addLanguage("Sichuan Yi", "ii");
		addLanguage("Sindhi", "sd");
		addLanguage("Sinhala", "si");
		addLanguage("Slovak", "sk");
		addLanguage("Slovenian", "sl");
		addLanguage("Somali", "so");
		addLanguage("South Ndebele", "nr");
		addLanguage("Southern Sotho", "st");
		addLanguage("Spanish", "es");
		addLanguage("Spanish (Argentina)", "es-AR");
		addLanguage("Spanish (Bolivia)", "es-BO");
		addLanguage("Spanish (Chile)", "es-CL");
		addLanguage("Spanish (Colombia)", "es-CO");
		addLanguage("Spanish (Costa Rica)", "es-CR");
		addLanguage("Spanish (Dominican Republic)", "es-DO");
		addLanguage("Spanish (Ecuador)", "es-EC");
		addLanguage("Spanish (El Salvador)", "es-SV");
		addLanguage("Spanish (Guatemala)", "es-GT");
		addLanguage("Spanish (Honduras)", "es-HN");
		addLanguage("Spanish (Latin America and the Caribbean)", "es-419");
		addLanguage("Spanish (Mexico)", "es-MX");
		addLanguage("Spanish (Nicaragua)", "es-NI");
		addLanguage("Spanish (Panama)", "es-PA");
		addLanguage("Spanish (Paraguay)", "es-PY");
		addLanguage("Spanish (Peru)", "es-PE");
		addLanguage("Spanish (Puerto Rico)", "es-PR");
		addLanguage("Spanish (Spain)", "es-ES");
		addLanguage("Spanish (Uruguay)", "es-UY");
		addLanguage("Spanish (Venezuela)", "es-VE");
		addLanguage("Sundanese", "su");
		addLanguage("Swahili ", "sw");
		addLanguage("Swati", "ss");
		addLanguage("Swedish", "sv");
		addLanguage("Swedish (Finland)", "sv-FI");
		addLanguage("Tagalog", "tl");
		addLanguage("Tahitian", "ty");
		addLanguage("Tajik", "tg");
		addLanguage("Tamil", "ta");
		addLanguage("Tatar", "tt");
		addLanguage("Telugu", "te");
		addLanguage("Thai", "th");
		addLanguage("Tibetan", "bo");
		addLanguage("Tigrinya", "ti");
		addLanguage("Tonga ", "to");
		addLanguage("Tsonga", "ts");
		addLanguage("Tswana", "tn");
		addLanguage("Turkish", "tr");
		addLanguage("Turkmen", "tk");
		addLanguage("Twi", "tw");
		addLanguage("Uighur", "ug");
		addLanguage("Ukrainian", "uk");
		addLanguage("Urdu", "ur");
		addLanguage("Uzbek", "uz");
		addLanguage("Venda", "ve");
		addLanguage("Vietnamese", "vi");
		addLanguage("Volapük", "vo");
		addLanguage("Walloon", "wa");
		addLanguage("Welsh", "cy");
		addLanguage("Western Frisian", "fy");
		addLanguage("Wolof", "wo");
		addLanguage("Xhosa", "xh");
		addLanguage("Yiddish", "yi");
		addLanguage("Yoruba", "yo");
		addLanguage("Zhuang", "za");
		addLanguage("Zulu", "zu");
	}
}
