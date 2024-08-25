
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class UI extends javax.swing.JFrame {

    private final AtomicInteger descargasPendientes; // Contador atómico para gestionar descargas pendientes
    private final AtomicInteger descargasFinalizadas; // Contador atómico para gestionar descargas finalizadas
    private final AtomicInteger duplicadosEvitados; // Contador atómico para gestionar duplicados evitados

    public UI() {
        initComponents();
        descargasPendientes = new AtomicInteger(0);
        descargasFinalizadas = new AtomicInteger(0);
        duplicadosEvitados = new AtomicInteger(0);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        pnlGen = new javax.swing.JPanel();
        txtUrl = new javax.swing.JTextField();
        txtTit = new javax.swing.JLabel();
        btnDes = new javax.swing.JButton();
        txtEst = new javax.swing.JLabel();
        txtRes = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Descargar de YT a MP3");
        setMaximumSize(new java.awt.Dimension(700, 300));
        setMinimumSize(new java.awt.Dimension(700, 300));
        setPreferredSize(new java.awt.Dimension(700, 300));
        setResizable(false);

        pnlGen.setBackground(new java.awt.Color(0, 0, 0));

        txtUrl.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtUrl.setToolTipText("");

        txtTit.setFont(new java.awt.Font("Segoe UI Black", 1, 36)); // NOI18N
        txtTit.setForeground(new java.awt.Color(255, 255, 255));
        txtTit.setText("Pegar URL ↓↓↓↓↓↓↓↓↓↓↓");

        btnDes.setBackground(new java.awt.Color(255, 255, 255));
        btnDes.setFont(new java.awt.Font("Segoe UI Black", 1, 48)); // NOI18N
        btnDes.setForeground(new java.awt.Color(0, 0, 0));
        btnDes.setText("DESCARGAR LINK A MP3");
        btnDes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDesActionPerformed(evt);
            }
        });

        txtEst.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtEst.setForeground(new java.awt.Color(255, 255, 255));
        txtEst.setText("Estado: inactivo");
        txtEst.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        txtEst.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        txtEst.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        txtRes.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtRes.setForeground(new java.awt.Color(255, 255, 255));
        txtRes.setText("Resultado:");
        txtRes.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        txtRes.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        txtRes.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout pnlGenLayout = new javax.swing.GroupLayout(pnlGen);
        pnlGen.setLayout(pnlGenLayout);
        pnlGenLayout.setHorizontalGroup(
            pnlGenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlGenLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlGenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtRes, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDes, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 688, Short.MAX_VALUE)
                    .addComponent(txtUrl, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlGenLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(txtTit)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(txtEst, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlGenLayout.setVerticalGroup(
            pnlGenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGenLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(txtTit)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEst)
                .addGap(18, 18, 18)
                .addComponent(txtRes)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDes, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlGen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlGen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>                        

    private void btnDesActionPerformed(java.awt.event.ActionEvent evt) {                                       
        String url = txtUrl.getText(); // Obtener el contenido del campo de texto
        if (url.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese una URL.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Incrementar contador de descargas pendientes
        descargasPendientes.incrementAndGet();
        actualizarEstado(); // Actualizar el estado antes de empezar la descarga

        // Deshabilitar el botón mientras hay descargas
        btnDes.setEnabled(false);

        // Ejecutar el proceso de descarga
        try {
            // Ejecutar el proceso de descarga
            String comandoDescarga = "java -jar YT_to_MP3.jar " + url;
            Process procesoDescarga = Runtime.getRuntime().exec(comandoDescarga);

            // Leer la salida del proceso
            BufferedReader lectorSalida = new BufferedReader(new InputStreamReader(procesoDescarga.getInputStream()));
            StringBuilder salida = new StringBuilder();
            String linea;

            while ((linea = lectorSalida.readLine()) != null) {
                salida.append(linea).append("\n");
            }

            int codigoSalida = procesoDescarga.waitFor();

            // Analizar la salida para detectar si el archivo ya existe
            if (salida.toString().contains("El archivo MP3 ya existe")) {
                duplicadosEvitados.incrementAndGet();
                txtEst.setText("Estado: El archivo MP3 ya existe.");
            } else if (codigoSalida == 0) {
                // Si la descarga se completó correctamente
                descargasFinalizadas.incrementAndGet();
                txtEst.setText("Estado: Descarga completada: " + url);
            } else {
                // Si hubo un error en la descarga
                txtEst.setText("Estado: Link no válido");
            }
        } catch (IOException | InterruptedException e) {
            txtEst.setText("Error crítico durante la descarga: " + url);
        } finally {
            // Decrementar descargas pendientes y actualizar el estado
            descargasPendientes.decrementAndGet();
            SwingUtilities.invokeLater(() -> {
                actualizarEstado(); // Actualizar el estado en la interfaz
                btnDes.setEnabled(true); // Habilitar el botón nuevamente
            });
        }
    }                                      
    private void actualizarEstado() {
        SwingUtilities.invokeLater(() -> {
            int pendientes = descargasPendientes.get();
            int finalizadas = descargasFinalizadas.get();
            int evitados = duplicadosEvitados.get();
            String estado = String.format("Resultado: %d finalizadas, %d pendientes, %d duplicados evitados", finalizadas, pendientes, evitados);
            txtRes.setText(estado);
        });
    }

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(UI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new UI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify                     
    private javax.swing.JButton btnDes;
    private javax.swing.JPanel pnlGen;
    private javax.swing.JLabel txtEst;
    private javax.swing.JLabel txtRes;
    private javax.swing.JLabel txtTit;
    private javax.swing.JTextField txtUrl;
    // End of variables declaration                   
}
