package entidades;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UDPServer {

    private static final int PORT = 5000;
    private final List<Usuario> usuarios = new ArrayList<>();
    private final Map<String, Tarjeta> tarjetas = new HashMap<>();

    public void start() throws Exception {
        DatagramSocket serverSocket = new DatagramSocket(PORT);
        System.out.println("Servidor UDP iniciado en puerto " + PORT);
        byte[] receiveData = new byte[1024];
        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String sentence = new String(receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8).trim();
            System.out.println("Recibido: " + sentence);
            String response = handleRequest(sentence);
            byte[] sendData = response.getBytes(StandardCharsets.UTF_8);
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            serverSocket.send(sendPacket);
            if ("SHUTDOWN".equalsIgnoreCase(sentence)) {
                serverSocket.close();
                break;
            }
        }
    }

    private String handleRequest(String req) {
        try {
            if (req == null || req.isEmpty()) return "ERROR: petición vacía";
            if (req.startsWith("CREAR:")) {
                String body = req.substring(6);
                String[] parts = body.split("\\|");
                if (parts.length != 5) return "ERROR: formato CREAR inválido";
                String ced = parts[0].trim();
                String nombre = parts[1].trim();
                String correo = parts[2].trim();
                String telefono = parts[3].trim();
                boolean pref = Boolean.parseBoolean(parts[4].trim());

                String v = validarUsuario(ced, nombre, correo, telefono);
                if (v != null) return "ERROR: " + v;
                if (buscarUsuario(ced) != null) return "ERROR: usuario ya existe";
                Usuario u = new Usuario(ced, correo, telefono, nombre, pref);
                usuarios.add(u);
                return "OK: usuario creado -> " + u;
            } else if (req.startsWith("CONSULTAR:")) {
                String ced = req.substring(10).trim();
                if (ced.isEmpty()) return "ERROR: cedula vacía";
                Usuario u = buscarUsuario(ced);
                if (u == null) return "ERROR: usuario no encontrado";
                Tarjeta t = tarjetas.get(ced);
                String tarjetaInfo = (t == null) ? "sin" : ("saldo=" + t.getSaldo());

                return "OK:" + u.getCedula() + "|" + u.getNombre() + "|" + u.getCorreo() + "|" + u.getTelefono() + "|" + u.isPreferencial() + "|" + tarjetaInfo;
            } else if (req.startsWith("ASIGNAR_TARJETA:")) {
                String ced = req.substring("ASIGNAR_TARJETA:".length()).trim();
                Usuario u = buscarUsuario(ced);
                if (u == null) return "ERROR: usuario no encontrado";
                Tarjeta t = new Tarjeta(u);
                tarjetas.put(ced, t);
                return "OK: tarjeta asignada a " + ced;
            } else if (req.startsWith("CARGAR_TARJETA:")) {
                String body = req.substring("CARGAR_TARJETA:".length());
                String[] parts = body.split("\\|");
                if (parts.length != 2) return "ERROR: formato CARGAR_TARJETA inválido";
                String ced = parts[0].trim();
                double monto;
                try { monto = Double.parseDouble(parts[1].trim()); } catch (Exception e) { return "ERROR: monto inválido"; }
                Tarjeta t = tarjetas.get(ced);
                if (t == null) return "ERROR: tarjeta no existe para ese usuario";
                if (monto <= 0) return "ERROR: monto debe ser positivo";
                t.cargarSaldo(monto);
                return "OK: saldo actual=" + t.getSaldo();
            } else if (req.startsWith("PAGAR:")) {
                String ced = req.substring(6).trim();
                Tarjeta t = tarjetas.get(ced);
                if (t == null) return "ERROR: tarjeta no existe";
                boolean ok = t.pagarPasaje();
                if (ok) return "OK: pago realizado, saldo=" + t.getSaldo();
                else return "ERROR: saldo insuficiente";
            } else if (req.equalsIgnoreCase("PING")) {
                return "OK: PONG";
            } else if (req.equalsIgnoreCase("SHUTDOWN")) {
                return "OK: servidor apagando";
            } else {
                return "ERROR: comando desconocido";
            }
        } catch (Exception e) {
            return "ERROR: Excepción en servidor - " + e.getMessage();
        }
    }

    private Usuario buscarUsuario(String cedula) {
        for (Usuario u : usuarios) if (u.getCedula().equals(cedula)) return u;
        return null;
    }

    private String validarUsuario(String ced, String nombre, String correo, String telefono) {
        if (ced == null || ced.isEmpty()) return "cedula vacía";
        if (!ced.matches("\\d+")) return "cedula debe contener solo dígitos";
        if (nombre == null || nombre.isEmpty()) return "nombre vacío";
        if (correo == null || !correo.contains("@")) return "correo inválido";
        if (telefono == null || !telefono.matches("[0-9+\\-\\s]+")) return "telefono inválido";
        return null;
    }

    public static void main(String[] args) throws Exception {
        UDPServer s = new UDPServer();
        s.start();
    }

}