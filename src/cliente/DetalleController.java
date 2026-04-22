package cliente;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class DetalleController {

    @FXML private Label cedulaLabel;
    @FXML private Label nombreLabel;
    @FXML private Label correoLabel;
    @FXML private Label telefonoLabel;
    @FXML private Label preferencialLabel;
    @FXML private Label tarjetaLabel;

    public void setData(String[] parts) {
        if (parts == null || parts.length < 6) return;
        cedulaLabel.setText(parts[0]);
        nombreLabel.setText(parts[1]);
        correoLabel.setText(parts[2]);
        telefonoLabel.setText(parts[3]);
        preferencialLabel.setText(parts[4]);
        tarjetaLabel.setText(parts[5]);
    }

    @FXML
    private void handleClose(ActionEvent e) {
        Stage stage = (Stage) cedulaLabel.getScene().getWindow();
        stage.close();
    }
}

