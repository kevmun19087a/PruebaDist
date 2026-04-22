package cliente;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClienteUDP {

    private int port;

    public ClienteUDP(int port) {
        this.port = port;
    }

    public String send(String message) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);
        InetAddress server = InetAddress.getByName("127.0.0.1");
        byte[] out = message.getBytes();
        DatagramPacket p = new DatagramPacket(out, out.length, server, port);
        socket.send(p);
        byte[] in = new byte[1024];
        DatagramPacket resp = new DatagramPacket(in, in.length);
        socket.receive(resp);
        String s = new String(resp.getData(), 0, resp.getLength());
        socket.close();
        return s;
    }
}

