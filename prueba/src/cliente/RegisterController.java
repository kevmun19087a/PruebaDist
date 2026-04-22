package cliente;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

public class RegisterController {

    @FXML private TextField cedulaField;
    @FXML private TextField nombreField;
    @FXML private TextField correoField;
    @FXML private TextField telefonoField;
    @FXML private CheckBox preferencialCheck;
    @FXML private Label messageLabel;

    @FXML
    private void onRegistrar(ActionEvent e) {
        try {
            String ced = cedulaField.getText().trim();
            String nom = nombreField.getText().trim();
            String cor = correoField.getText().trim();
            String tel = telefonoField.getText().trim();
            String pref = String.valueOf(preferencialCheck.isSelected());
            if (ced.isEmpty() || nom.isEmpty() || cor.isEmpty()) {
                messageLabel.setText("Complete los campos obligatorios.");
                return;
            }
            String message = "CREAR:" + ced + "|" + nom + "|" + cor + "|" + tel + "|" + pref;
            String resp;
            try {
                ClienteUDP client = new ClienteUDP(5000);
                resp = client.send(message);
            } catch (java.net.SocketTimeoutException ste) {
                messageLabel.setText("No hay respuesta del servidor");
                return;
            }
            if (resp == null) {
                messageLabel.setText("Sin respuesta del servidor.");
                return;
            }
            messageLabel.setText(resp);
            if (resp.startsWith("OK:")) {
                Stage stage = (javafx.stage.Stage) cedulaField.getScene().getWindow();
                stage.close();
            }
        } catch (Exception ex) {
            messageLabel.setText("Error: " + ex.getMessage());
        }
    }

    @FXML
    private void onCancelar(ActionEvent e) {
        Stage stage = (javafx.stage.Stage) cedulaField.getScene().getWindow();
        stage.close();
    }
}

