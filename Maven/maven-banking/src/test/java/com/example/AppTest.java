package com.example;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.time.LocalDate;
import java.util.List;
import org.json.JSONArray;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    @Test
    public void testLeerArchivo() {
        // Crear una ruta de prueba para el archivo
        String rutaArchivo = "/workspaces/BuildSolutions_ValentinaCorrea/Maven/maven-banking/src/resources/transactions.txt";
        
        // Asegurarse de que el directorio existe
        new File("src/test/resources").mkdirs();
        
        // Crear un archivo de prueba con contenido JSON
        try {
            String contenidoPrueba = "{\n" +
                    "  \"test@example.com\": [\n" +
                    "    {\"balance\": \"100\", \"type\": \"Deposit\", \"timestamp\": \"2025-02-10 14:00:00.000000\"}\n" +
                    "  ]\n" +
                    "}";
            
            java.nio.file.Files.write(java.nio.file.Paths.get(rutaArchivo), contenidoPrueba.getBytes());
            
            // Ejecutar la función a probar
            String resultado = App.leerArchivo(rutaArchivo);
            
            // Verificar que el resultado no sea nulo
            assertNotNull(resultado, "El archivo leído no debe ser nulo");
            
            // Verificar que el resultado no esté vacío
            assertFalse(resultado.isEmpty(), "El archivo leído no debe estar vacío");
            
            // Verificar que el contenido sea el esperado
            assertTrue(resultado.contains("test@example.com"), "El contenido debe incluir el correo de prueba");
            
            // Limpiar: eliminar el archivo de prueba
            new File(rutaArchivo).delete();
            
        } catch (Exception e) {
            fail("Se produjo una excepción al crear o leer el archivo de prueba: " + e.getMessage());
        }
    }

    @Test
    public void testObtenerTransacciones() {
        // Crear datos JSON de prueba
        String jsonPrueba = "{\n" +
                "  \"usuario1@example.com\": [\n" +
                "    {\"balance\": \"150\", \"type\": \"Deposit\", \"timestamp\": \"2025-03-01 12:00:00.000000\"},\n" +
                "    {\"balance\": \"-50\", \"type\": \"Withdrawal\", \"timestamp\": \"2025-03-02 13:00:00.000000\"}\n" +
                "  ],\n" +
                "  \"usuario2@example.com\": [\n" +
                "    {\"balance\": \"200\", \"type\": \"Deposit\", \"timestamp\": \"2025-03-03 14:00:00.000000\"}\n" +
                "  ]\n" +
                "}";
        
        // Ejecutar la función para usuario1
        List<JSONObject> transaccionesUsuario1 = App.obtenerTransacciones(jsonPrueba, "usuario1@example.com");
        
        // Verificar que se obtuvieron transacciones para usuario1
        assertNotNull(transaccionesUsuario1, "Las transacciones no deben ser nulas");
        assertEquals(2, transaccionesUsuario1.size(), "Debe haber 2 transacciones para usuario1");
        
        // Verificar el contenido de la primera transacción
        JSONObject primeraTransaccion = transaccionesUsuario1.get(0);
        assertEquals("150", primeraTransaccion.getString("balance"), "El balance debe ser 150");
        assertEquals("Deposit", primeraTransaccion.getString("type"), "El tipo debe ser Deposit");
        
        // Ejecutar la función para usuario2
        List<JSONObject> transaccionesUsuario2 = App.obtenerTransacciones(jsonPrueba, "usuario2@example.com");
        
        // Verificar que se obtuvo una transacción para usuario2
        assertEquals(1, transaccionesUsuario2.size(), "Debe haber 1 transacción para usuario2");
        
        // Ejecutar la función para un usuario que no existe
        List<JSONObject> transaccionesUsuarioInexistente = App.obtenerTransacciones(jsonPrueba, "noexiste@example.com");
        
        // Verificar que la lista está vacía para un usuario inexistente
        assertTrue(transaccionesUsuarioInexistente.isEmpty(), "No debe haber transacciones para un usuario inexistente");
    }

    @Test
    public void testGenerarExtracto() {
        // Crear datos de prueba
        String usuario = "test.extracto@example.com";
        List<JSONObject> transacciones = new java.util.ArrayList<>();
        
        // Crear objetos JSON para las transacciones
        JSONObject transaccion1 = new JSONObject();
        transaccion1.put("balance", "300");
        transaccion1.put("type", "Deposit");
        transaccion1.put("timestamp", "2025-03-10 10:00:00.000000");
        
        JSONObject transaccion2 = new JSONObject();
        transaccion2.put("balance", "-100");
        transaccion2.put("type", "Withdrawal");
        transaccion2.put("timestamp", "2025-03-11 11:00:00.000000");
        
        // Agregar las transacciones a la lista
        transacciones.add(transaccion1);
        transacciones.add(transaccion2);
        
        // Ejecutar la función a probar
        App.generarExtracto(usuario, transacciones);
        
        // Construir el nombre del archivo esperado
        String nombreArchivo = "test_extracto_example_com_extracto.txt";
        
        // Verificar que el archivo existe
        File archivoExtracto = new File(nombreArchivo);
        assertTrue(archivoExtracto.exists(), "El archivo extracto debe existir");
        
        try {
            // Leer el contenido del archivo
            String contenido = new String(java.nio.file.Files.readAllBytes(archivoExtracto.toPath()));
            
            // Verificar que el contenido incluye la información correcta
            assertTrue(contenido.contains("Extracto Bancario - " + usuario), "El extracto debe incluir el nombre del usuario");
            assertTrue(contenido.contains("Fecha: 2025-03-10 10:00:00.000000"), "El extracto debe incluir la fecha de la primera transacción");
            assertTrue(contenido.contains("Tipo: Deposit"), "El extracto debe incluir el tipo de la primera transacción");
            assertTrue(contenido.contains("Monto: 300"), "El extracto debe incluir el monto de la primera transacción");
            assertTrue(contenido.contains("Fecha: 2025-03-11 11:00:00.000000"), "El extracto debe incluir la fecha de la segunda transacción");
            
            // Limpiar: eliminar el archivo de prueba
            archivoExtracto.delete();
            
        } catch (Exception e) {
            fail("Se produjo una excepción al leer el archivo de extracto: " + e.getMessage());
        }
    }
}