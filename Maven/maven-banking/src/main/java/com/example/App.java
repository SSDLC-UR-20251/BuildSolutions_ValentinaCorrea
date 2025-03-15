package com.example;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import org.json.*;

public class App {

    // 🔹 1. Leer el archivo JSON desde un .txt
    public static String leerArchivo(String rutaArchivo) {
        try {
            // Leer todo el contenido del archivo como una cadena
            String contenido = new String(Files.readAllBytes(Paths.get(rutaArchivo)));
            return contenido;
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            return null;
        }
    }

    // 🔹 2. Obtener transacciones de un usuario específico
    public static List<JSONObject> obtenerTransacciones(String jsonData, String usuario) {
        List<JSONObject> transacciones = new ArrayList<>();
        
        try {
            // Convertir el string JSON a un objeto JSONObject
            JSONObject datos = new JSONObject(jsonData);
            
            // Verificar si el usuario existe en el JSON
            if (datos.has(usuario)) {
                // Obtener el array de transacciones del usuario
                JSONArray transaccionesArray = datos.getJSONArray(usuario);
                
                // Recorrer el array y agregar cada transacción a la lista
                for (int i = 0; i < transaccionesArray.length(); i++) {
                    transacciones.add(transaccionesArray.getJSONObject(i));
                }
            }
        } catch (JSONException e) {
            System.err.println("Error al procesar el JSON: " + e.getMessage());
        }
        
        return transacciones;
    }

    // 🔹 3. Generar extracto bancario en un archivo .txt
    public static void generarExtracto(String usuario, List<JSONObject> transacciones) {
        // Crear nombre de archivo reemplazando caracteres especiales
        String nombreArchivo = usuario.replaceAll("[.@]", "_") + "_extracto.txt";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(nombreArchivo))) {
            // Escribir encabezado
            writer.println("Extracto Bancario - " + usuario);
            writer.println("====================================");
            
            // Escribir cada transacción
            for (JSONObject transaccion : transacciones) {
                writer.println("Fecha: " + transaccion.getString("timestamp"));
                writer.println("Tipo: " + transaccion.getString("type"));
                writer.println("Monto: " + transaccion.getString("balance"));
                writer.println("------------------------------------");
            }
            
            System.out.println("Extracto generado correctamente en el archivo: " + nombreArchivo);
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // Solicitar el correo del usuario
        System.out.print("Ingrese el correo del usuario: ");
        String usuario = scanner.nextLine();
        
        // Leer el archivo de transacciones
        String rutaArchivo = "src/resources/transactions.txt";
        String jsonData = leerArchivo(rutaArchivo);
        
        if (jsonData != null) {
            // Obtener las transacciones del usuario
            List<JSONObject> transacciones = obtenerTransacciones(jsonData, usuario);
            
            if (!transacciones.isEmpty()) {
                // Generar el extracto bancario
                generarExtracto(usuario, transacciones);
            } else {
                System.out.println("No se encontraron transacciones para el usuario: " + usuario);
            }
        } else {
            System.out.println("No se pudo leer el archivo de transacciones.");
        }
        
        scanner.close();
    }
}