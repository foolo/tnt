package tnt.editor;

import javax.swing.SwingUtilities;

public class PleaseWaitDialog extends javax.swing.JDialog {

	String message;
	final long MINIMUM_DISPLAY_TIME_MS = 300;

	public PleaseWaitDialog(java.awt.Frame parent, String message) {
		super(parent, false);
		this.message = message;
		initComponents();
		init(parent);
	}

	private void init(java.awt.Frame parent) {
		jLabel1.setText(message);
		pack();
		setLocationRelativeTo(parent);
		setVisible(true);
	}

	void run(Runnable r) {
		SwingUtilities.invokeLater(() -> {
			long startTime = System.currentTimeMillis();
			r.run();
			long execTime = System.currentTimeMillis() - startTime;
			if (execTime < MINIMUM_DISPLAY_TIME_MS) {
				try {
					Thread.sleep(MINIMUM_DISPLAY_TIME_MS - execTime);
				}
				catch (InterruptedException ex) {
				}
			}
			setVisible(false);
		});
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("jLabel1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
