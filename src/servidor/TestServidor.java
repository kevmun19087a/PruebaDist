package servidor;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class TestServidor {
    public static void main(String[] args) throws Exception {
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName("127.0.0.1");
        int port = 9876;

        String[] tests = new String[] {
                "PING",
                "CREAR:12345|Juan Perez|juan@example.com|3001234|false",
                "CONSULTAR:12345",
                "ASIGNAR_TARJETA:12345",
                "CARGAR_TARJETA:12345|10.5",
                "PAGAR:12345",
                "CONSULTAR:12345"
        };

        for (String t : tests) {
            byte[] sendData = t.getBytes(StandardCharsets.UTF_8);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            clientSocket.send(sendPacket);

            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.setSoTimeout(3000);
            try {
                clientSocket.receive(receivePacket);
                String resp = new String(receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8);
                System.out.println("Req: " + t + " -> Resp: " + resp);
            } catch (Exception e) {
                System.out.println("Req: " + t + " -> sin respuesta: " + e.getMessage());
            }
        }

        clientSocket.close();
    }
}

