package tnt.editor;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.event.HyperlinkEvent;
import tnt.util.Log;

public class AboutPanel extends javax.swing.JPanel {

	public AboutPanel() {
		initComponents();
		jEditorPane1.setContentType("text/html");
		StringBuilder sb = new StringBuilder();
		sb.append("Application version: ").append(Application.APPLICATION_NAME).append(" ").append(Application.APPLICATION_VERSION).append("<br>");
		sb.append("Website: <a href=\"https://foolo.github.io/tnt-home/\">https://foolo.github.io/tnt-home/</a><br>");
		sb.append("<br>");
		sb.append("Environment information:<br>");
		sb.append("Java: ").append(System.getProperty("java.version")).append(", ").append(System.getProperty("java.vm.name")).append(" ").append(System.getProperty("java.vm.version")).append("<br>");
		sb.append("OS: ").append(System.getProperty("os.name")).append(" ").append(System.getProperty("os.arch")).append(" ").append(System.getProperty("os.version")).append("<br>");
		jEditorPane1.setText(sb.toString());
		jEditorPane1.addHyperlinkListener((HyperlinkEvent e) -> {
			if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
				Desktop desktop = Desktop.getDesktop();
				try {
					desktop.browse(e.getURL().toURI());
				}
				catch (IOException | URISyntaxException ex) {
					Log.err(ex);
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();

        jEditorPane1.setEditable(false);
        jScrollPane2.setViewportView(jEditorPane1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
