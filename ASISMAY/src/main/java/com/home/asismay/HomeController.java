package com.home.asismay;

import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class HomeController {

    @FXML
    private TableView<Asistencia> table;

    @FXML
    private TableColumn<Asistencia, String> nombreColumn;

    @FXML
    private TableColumn<Asistencia, String> asistenciaColumn;

    @FXML
    private Button loadFileButton;

    private ObservableList<Asistencia> data = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Inicializa las columnas de la tabla
        nombreColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        asistenciaColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAsistencia()));

        // Configura el botón de carga de archivo
        loadFileButton.setOnAction(e -> cargarArchivoExcel());
    }

    private void cargarArchivoExcel() {
        // Crea un FileChooser para seleccionar el archivo Excel
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos Excel", "*.xlsx"));
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            try {
                FileInputStream fis = new FileInputStream(file);
                Workbook workbook = new XSSFWorkbook(fis);
                Sheet sheet = workbook.getSheetAt(0); // Lee la primera hoja del archivo Excel

                data.clear(); // Limpiar la lista antes de agregar los nuevos datos

                // Recorre las filas del Excel y agrega los datos a la tabla
                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue; // Salta la primera fila (encabezados)

                    String nombre = getCellValue(row, 0); // Nombre del empleado
                    String asistencia = getCellValue(row, 1); // Asistencia general

                    // Crear un nuevo objeto de Asistencia
                    Asistencia asistenciaEmpleado = new Asistencia(nombre, asistencia);

                    // Asignar los datos de asistencia por día (suponiendo que las columnas de días empiezan en la columna 2)
                    for (int i = 2; i < row.getPhysicalNumberOfCells(); i++) {
                        String estadoDia = getCellValue(row, i);  // Estado de asistencia para el día i
                        asistenciaEmpleado.setAsistenciaDia(i - 1, estadoDia);  // Guardamos en el mapa
                    }

                    // Agregar la asistencia del empleado a la lista
                    data.add(asistenciaEmpleado);
                }

                // Establecer los datos en la tabla
                table.setItems(data);

                // Generar las columnas para los días del mes
                generarColumnasDias(sheet);

                fis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Función para obtener el valor de las celdas y manejar diferentes tipos de datos
    private String getCellValue(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell != null) {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue();
                case NUMERIC:
                    // Si el valor es un número, devolverlo como cadena
                    return String.valueOf(cell.getNumericCellValue());
                case BOOLEAN:
                    // Si el valor es booleano, devolverlo como cadena
                    return String.valueOf(cell.getBooleanCellValue());
                default:
                    return "";
            }
        }
        return "";
    }

    private void generarColumnasDias(Sheet sheet) {
        // Obtener el número de días del mes
        int diasDelMes = java.time.LocalDate.now().lengthOfMonth();

        // Crear nuevas columnas para cada día del mes
        for (int i = 1; i <= diasDelMes; i++) {
            int dia = i;  // Necesitamos capturar el valor de i para usarlo dentro de la expresión lambda

            // Crear columna para el día
            TableColumn<Asistencia, String> dayColumn = new TableColumn<>(String.valueOf(i));

            // Definir cómo mostrar la información de asistencia
            dayColumn.setCellValueFactory(cellData -> {
                Asistencia asistencia = cellData.getValue();
                return new SimpleStringProperty(asistencia.getAsistenciaDia(dia));  // Muestra si asistió o no
            });

            // Establecer un valor predeterminado de asistencia para cada trabajador en el día
            dayColumn.setCellFactory(cell -> {
                return new TableCell<Asistencia, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(item);  // Muestra el estado de asistencia (Sí, No, Vacaciones)
                        }
                    }
                };
            });

            // Agregar la columna a la tabla
            table.getColumns().add(dayColumn);
        }
    }
}
