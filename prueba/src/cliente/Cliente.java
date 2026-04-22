package cliente;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class Cliente {

	@FXML private TextField cedulaField;
	@FXML private Button btnBuscar;
	@FXML private Button btnRegistrar;
	@FXML private TextArea resultArea;
	@FXML private Label statusLabel;

	@FXML
	private void initialize() {
	}

	@FXML
	private void onBuscar() {
		try {
			String ced = cedulaField.getText().trim();
			if (ced.isEmpty()) {
				resultArea.setText("Ingrese la cédula para buscar.");
				return;
			}
			String message = "CONSULTAR:" + ced;
			String resp = sendAndReceiveUDP(message);
			statusLabel.setText("Última acción: Buscar");
			if (resp != null && resp.startsWith("OK:")) {
				String payload = resp.substring(3);
				String[] parts = payload.split("\\|", 6);
				FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/cliente/detalle.fxml"));
				Parent root = loader.load();
				DetalleController ctrl = loader.getController();
				ctrl.setData(parts);
				Stage stage = new javafx.stage.Stage();
				stage.setTitle("Detalle Usuario - " + parts[1]);
				stage.setScene(new javafx.scene.Scene(root));
				stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
				stage.showAndWait();
			} else {
				resultArea.setText(resp);
			}
		} catch (Exception e) {
			resultArea.setText("Error cliente: " + e.getMessage());
		}
	}

	@FXML
	private void onRegistrar() {
		try {
			javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/cliente/register.fxml"));
			javafx.scene.Parent root = loader.load();
			javafx.stage.Stage stage = new javafx.stage.Stage();
			stage.setTitle("Registrar Usuario");
			stage.setScene(new javafx.scene.Scene(root));
			stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
			stage.showAndWait();
		} catch (Exception e) {
			resultArea.setText("Error al abrir ventana de registro: " + e.getMessage());
		}
	}

	private String sendAndReceiveUDP(String message) throws Exception {
		ClienteUDP c = new ClienteUDP(5000);
		return c.send(message);
	}

}
