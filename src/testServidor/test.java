package testServidor;

import entidades.UDPServer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class test {

    private static String enviarComando(String comando) throws Exception {
        try (DatagramSocket clientSocket = new DatagramSocket()) {
            clientSocket.setSoTimeout(3000);
            byte[] sendData = comando.getBytes(StandardCharsets.UTF_8);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("127.0.0.1"), 5000);
            clientSocket.send(sendPacket);

            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);

            return new String(receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8);
        }
    }

    public static void main(String[] args) throws Exception {
        String cedula = "1755871140";

        Thread serverThread = new Thread(() -> {
            try {
                new UDPServer().start();
            } catch (Exception e) {
                System.out.println("Error en servidor: " + e.getMessage());
            }
        });
        serverThread.start();

        Thread.sleep(600);

        System.out.println("CREAR = " + enviarComando("CREAR:" + cedula + "|Damaris Lopez|damaris.lopez@epn.edu.ec|0991234567|true"));
        System.out.println("CONSULTAR = " + enviarComando("CONSULTAR:" + cedula));
        System.out.println("ASIGNAR_TARJETA = " + enviarComando("ASIGNAR_TARJETA:" + cedula));
        System.out.println("CARGAR_TARJETA = " + enviarComando("CARGAR_TARJETA:" + cedula + "|5.0"));
        System.out.println("PAGAR = " + enviarComando("PAGAR:" + cedula));
        System.out.println("CONSULTAR = " + enviarComando("CONSULTAR:" + cedula));
        System.out.println("SHUTDOWN = " + enviarComando("SHUTDOWN"));

        serverThread.join(2000);
    }
}
