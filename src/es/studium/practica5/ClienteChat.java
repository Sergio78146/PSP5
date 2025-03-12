package es.studium.practica5; 
import java.awt.event.ActionEvent; 
import java.awt.event.ActionListener; 
import java.io.DataInputStream; 
import java.io.DataOutputStream; 
import java.io.IOException; 
import javax.swing.JButton; 
import javax.swing.JFrame; 
import javax.swing.JOptionPane;
import javax.swing.JScrollPane; 
import javax.swing.JTextArea; 
import javax.swing.JTextField; 

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class ClienteChat extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    SSLSocket socket;
    DataInputStream fentrada;
    DataOutputStream fsalida;
    String nombre;
    static JTextField mensaje = new JTextField();
    private JScrollPane scrollpane;
    static JTextArea textarea;
    JButton boton = new JButton("Enviar");
    JButton desconectar = new JButton("Salir");
    boolean repetir = true;

    public ClienteChat(SSLSocket socket, String nombre) {
        super(" Conexión del cliente chat: " + nombre);
        setLayout(null);
        mensaje.setBounds(10, 10, 400, 30);
        add(mensaje);
        textarea = new JTextArea();
        scrollpane = new JScrollPane(textarea);
        scrollpane.setBounds(10, 50, 400, 300);
        add(scrollpane);
        boton.setBounds(420, 10, 100, 30);
        add(boton);
        desconectar.setBounds(420, 50, 100, 30);
        add(desconectar);
        textarea.setEditable(false);
        boton.addActionListener(this);
        this.getRootPane().setDefaultButton(boton);
        desconectar.addActionListener(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.socket = socket;
        this.nombre = nombre;

        try {
            fentrada = new DataInputStream(socket.getInputStream());
            fsalida = new DataOutputStream(socket.getOutputStream());
            String texto = "SERVIDOR> Entra en el chat... " + nombre;
            fsalida.writeUTF(texto);
        } catch (IOException ex) {
            System.out.println("Error de E/S");
            ex.printStackTrace();
            System.exit(0);
        }
    }

    public static void main(String[] args) throws Exception {
        int puerto = 44444;
        String nombre = JOptionPane.showInputDialog("Introduce tu nombre o nick:");
        SSLSocket socket = null;
        try {
            System.setProperty("javax.net.ssl.trustStore", "ServerKeyStore.jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "123456");
            System.setProperty("javax.net.debug", "SSL, handshake"); 


            SSLSocketFactory ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) ssf.createSocket("127.0.0.1", puerto);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Imposible conectar con el servidor \n" + ex.getMessage(), "<<Mensaje de Error:1>>", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        if (!nombre.trim().equals("")) {
            ClienteChat cliente = new ClienteChat(socket, nombre);
            cliente.setBounds(0, 0, 540, 400);
            cliente.setVisible(true);
            cliente.ejecutar();
        } else {
            System.out.println("El nombre está vacío...");
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == boton) {
            String texto = nombre + "> " + mensaje.getText();
            try {
                mensaje.setText("");
                fsalida.writeUTF(texto);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == desconectar) {
            String texto = "SERVIDOR> Abandona el chat... " + nombre;
            try {
                fsalida.writeUTF(texto);
                fsalida.writeUTF("*");
                repetir = false;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void ejecutar() {
        String texto = "";
        while (repetir) {
            try {
                texto = fentrada.readUTF();
                textarea.setText(texto);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Imposible conectar con el servidor \n" + ex.getMessage(), "<<Mensaje de Error:2>>", JOptionPane.ERROR_MESSAGE);
                repetir = false;
            }
        }
        try {
            socket.close();
            System.exit(0);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
