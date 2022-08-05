package view;

import util.JDBCUtilities;
import java.sql.ResultSet;
import java.sql.Statement;

public class ReportesView {

    private String repitaCaracter(Character caracter, Integer veces) {
        String respuesta = "";
        for (int i = 0; i < veces; i++) {
            respuesta += caracter;
        }
        return respuesta;
    }

    public void proyectosFinanciadosPorBanco(String banco) {
        System.out.println(repitaCaracter('=', 36) + " LISTADO DE PROYECTOS POR BANCO "
                + repitaCaracter('=', 37));
        if (banco != null && !banco.isBlank()) {
            System.out.println(String.format("%3s %-25s %-20s %-15s %-7s %-30s",
                    "ID", "CONSTRUCTORA", "CIUDAD", "CLASIFICACION", "ESTRATO", "LIDER"));
            System.out.println(repitaCaracter('-', 105));
            // TODO Imprimir en pantalla la información del proyecto

            try {
                var conexionDB= JDBCUtilities.getConnection();
                Statement statement = null;
                ResultSet resultset = null;
                String consultaSQL = "SELECT p.ID_Proyecto AS ID, p.Constructora, p.Ciudad, p.Clasificacion, t.Estrato, l.Nombre || ' ' || l.Primer_Apellido || ' ' || l.Segundo_Apellido AS LIDER FROM Proyecto p JOIN Tipo t ON p.ID_Tipo = t.ID_Tipo JOIN Lider l ON p.ID_Lider = l.ID_Lider WHERE p.Banco_Vinculado = '"+banco+"' ORDER BY p.Fecha_Inicio DESC, p.Ciudad, p.Constructora";

                statement = conexionDB.createStatement();
                resultset = statement.executeQuery(consultaSQL);

                while (resultset.next()) {
                    System.out.println(String.format("%3d %-25s %-20s %-15s %7d %-30s",
                            resultset.getInt("ID"),
                            resultset.getString("Constructora"),
                            resultset.getString("Ciudad"),
                            resultset.getString("Clasificacion"),
                            resultset.getInt("Estrato"),
                            resultset.getString("LIDER")));
                }
                resultset.close();
                statement.close();
                conexionDB.close();

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    public void totalAdeudadoPorProyectosSuperioresALimite(Double limiteInferior) {
        System.out.println(repitaCaracter('=', 1) + " TOTAL DEUDAS POR PROYECTO "
                + repitaCaracter('=', 1));
        if (limiteInferior != null) {
            System.out.println(String.format("%3s %14s", "ID", "VALOR "));
            System.out.println(repitaCaracter('-', 29));
            // TODO Imprimir en pantalla la información del total adeudado
            try {
                var conexionDB= JDBCUtilities.getConnection();
                Statement statement = null;
                ResultSet resultset = null;
                String consultaSQL = "SELECT p.ID_Proyecto, SUM(c.Cantidad * mc.Precio_Unidad) AS VALOR FROM Proyecto p JOIN Compra c ON p.ID_Proyecto = c.ID_Proyecto JOIN MaterialConstruccion mc ON c.ID_MaterialConstruccion  = mc.ID_MaterialConstruccion WHERE c.Pagado = 'No' GROUP BY p.ID_Proyecto HAVING VALOR > "+limiteInferior+" ORDER BY VALOR DESC";

                statement = conexionDB.createStatement();
                resultset = statement.executeQuery(consultaSQL);

                while (resultset.next()) {
                    System.out.println(String.format("%3d %,15.1f",
                            resultset.getInt("ID_Proyecto"),
                            resultset.getDouble("VALOR")));
                }
                resultset.close();
                statement.close();
                conexionDB.close();

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }


    public void lideresQueMasGastan() {

        System.out.println(repitaCaracter('=', 6) + " 10 LIDERES MAS COMPRADORES "
                + repitaCaracter('=', 7));
        System.out.println(String.format("%-25s %14s", "LIDER", "VALOR "));
        System.out.println(repitaCaracter('-', 41));
        // TODO Imprimir en pantalla la información de los líderes

        try {
            var conexionDB= JDBCUtilities.getConnection();
            Statement statement = null;
            ResultSet resultset = null;
            String consultaSQL = "SELECT l.Nombre || ' ' || l.Primer_Apellido || ' ' || l.Segundo_Apellido AS LIDER, SUM(c.Cantidad * mc.Precio_Unidad) AS VALOR FROM Lider l JOIN Proyecto p ON p.ID_Lider = l.ID_Lider JOIN Compra c ON c.ID_Proyecto = p.ID_Proyecto JOIN MaterialConstruccion mc ON c.ID_MaterialConstruccion  = mc.ID_MaterialConstruccion GROUP BY LIDER ORDER BY VALOR DESC LIMIT 10";

            statement = conexionDB.createStatement();
            resultset = statement.executeQuery(consultaSQL);

            while (resultset.next()) {
                System.out.println(String.format("%-25s %,15.1f",
                        resultset.getString("LIDER"),
                        resultset.getDouble("VALOR")));
            }
            resultset.close();
            statement.close();
            conexionDB.close();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

}