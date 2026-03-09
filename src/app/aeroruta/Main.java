package app.aeroruta;

import com.aeroruta.utils.GestorTema;
import com.aeroruta.view.FrmPrincipal;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        GestorTema.getInstancia().aplicarTemaOscuro();
        SwingUtilities.invokeLater(() -> {
            FrmPrincipal vista = new FrmPrincipal();
            vista.setVisible(true);
        });
    }
}