package es.studium.practica5; 

import java.awt.Color; 
import java.awt.event.ActionEvent; 
import java.awt.event.ActionListener; 
import java.io.IOException; 
import java.net.Socket; 
import java.net.SocketException; 

import javax.swing.JButton; 
import javax.swing.JFrame; 
import javax.swing.JScrollPane; 
import javax.swing.JTextArea; 
import javax.swing.JTextField; 

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

public class ServidorChat extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    static SSLServerSocket servidor;
    static final int PUERTO = 44444;
    static int CONEXIONES = 0;
    static int ACTUALES = 0;
    static int MAXIMO = 15;
    static JTextField mensaje = new JTextField("");
    static JTextField mensaje2 = new JTextField("");
    private JScrollPane scrollpane1;
    static JTextArea textarea;
    JButton salir = new JButton("Salir");
    static Socket[] tabla = new Socket[MAXIMO];

    public ServidorChat() {
        super(" VENTANA DEL SERVIDOR DE CHAT ");
        setLayout(null);
        mensaje.setBounds(10, 10, 400, 30);
        add(mensaje);
        mensaje.setEditable(false);
        mensaje2.setBounds(10, 348, 400, 30);
        add(mensaje2);
        mensaje2.setEditable(false);
        textarea = new JTextArea();
        scrollpane1 = new JScrollPane(textarea);
        scrollpane1.setBounds(10, 50, 400, 300);
        add(scrollpane1);
        salir.setBounds(420, 10, 100, 30);
        add(salir);
        textarea.setEditable(false);
        salir.addActionListener(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String args[]) throws Exception {
        System.setProperty("javax.net.ssl.keyStore", "ServerKeyStore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "123456");
        System.setProperty("javax.net.debug", "SSL, handshake"); 


        SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        servidor = (SSLServerSocket) ssf.createServerSocket(PUERTO);
        System.out.println("Servidor iniciado...");
        ServidorChat pantalla = new ServidorChat();
        pantalla.setBounds(0, 0, 540, 450);
        pantalla.setVisible(true);
        mensaje.setText("Número de conexiones actuales: " + 0);

        while (CONEXIONES < MAXIMO) {
            Socket socket;
            try {
                socket = servidor.accept();
            } catch (SocketException sex) {
                break;
            }
            tabla[CONEXIONES] = socket;
            CONEXIONES++;
            ACTUALES++;
            HiloServidor hilo = new HiloServidor(socket);
            hilo.start();
        }

        if (!servidor.isClosed()) {
            try {
                mensaje2.setForeground(Color.red);
                mensaje2.setText("Máximo Nº de conexiones establecidas: " + CONEXIONES);
                servidor.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("Servidor finalizado...");
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == salir) {
            try {
                servidor.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.exit(0);
        }
    }
}
