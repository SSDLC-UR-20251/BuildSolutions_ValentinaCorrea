const { transferir, leerArchivo, escribirArchivo, calcularSaldo } = require('../src/app');
const fs = require('fs');

// Mock de fs para evitar operaciones reales de archivo durante las pruebas
jest.mock('fs');

describe('Funciones bancarias', () => {
    beforeEach(() => {
        // Reset mocks before each test
        jest.clearAllMocks();
    });
    
    test('Transferencia entre cuentas', () => {
        // Configurar mock para simular que el archivo existe
        fs.existsSync.mockReturnValue(true);
        
        // Configurar mock para simular una lectura de archivo con saldo suficiente
        fs.readFileSync.mockReturnValue(JSON.stringify({
            transactions: [
                { de: 'otro@urosario.edu.co', para: 'juan.jose@urosario.edu.co', monto: 100 }
            ]
        }));
        
        // Simular escritura exitosa
        fs.writeFileSync.mockReturnValue(undefined);
        
        // Ejecutar transferencia
        const resultado = transferir('juan.jose@urosario.edu.co', 'sara.palaciosc@urosario.edu.co', 30);
        
        // Verificar que la transferencia fue exitosa
        expect(resultado.exito).toBe(true);
        expect(resultado.mensaje).toContain('realizada correctamente');
        
        // Verificar que se guardó la nueva transacción
        expect(fs.writeFileSync).toHaveBeenCalled();
    });

    test('Transferencia con saldo insuficiente', () => {
        // Configurar mock para simular que el archivo existe
        fs.existsSync.mockReturnValue(true);
        
        // Configurar mock para simular una lectura de archivo con saldo insuficiente
        fs.readFileSync.mockReturnValue(JSON.stringify({
            transactions: [
                { de: 'otro@urosario.edu.co', para: 'juan.jose@urosario.edu.co', monto: 50 }
            ]
        }));
        
        // Ejecutar transferencia con monto mayor al saldo
        const resultado = transferir('juan.jose@urosario.edu.co', 'sara.palaciosc@urosario.edu.co', 1000);
        
        // Verificar que la transferencia falló
        expect(resultado.exito).toBe(false);
        expect(resultado.mensaje).toContain('Saldo insuficiente');
        
        // Verificar que no se guardó ninguna transacción
        expect(fs.writeFileSync).not.toHaveBeenCalled();
    });
    
    test('Calcular saldo correctamente', () => {
        // Configurar mock para simular que el archivo existe
        fs.existsSync.mockReturnValue(true);
        
        // Configurar mock para simular varias transacciones
        fs.readFileSync.mockReturnValue(JSON.stringify({
            transactions: [
                { de: 'otro@urosario.edu.co', para: 'juan.jose@urosario.edu.co', monto: 100 },
                { de: 'juan.jose@urosario.edu.co', para: 'sara.palaciosc@urosario.edu.co', monto: 30 },
                { de: 'otro@urosario.edu.co', para: 'juan.jose@urosario.edu.co', monto: 50 }
            ]
        }));
        
        // Calcular saldo
        const saldo = calcularSaldo('juan.jose@urosario.edu.co');
        
        // El saldo debe ser 100 - 30 + 50 = 120
        expect(saldo).toBe(120);
    });
    
    test('Monto de transferencia debe ser positivo', () => {
        // Intentar transferir un monto negativo
        const resultado = transferir('juan.jose@urosario.edu.co', 'sara.palaciosc@urosario.edu.co', -10);
        
        // Verificar que la transferencia falló
        expect(resultado.exito).toBe(false);
        expect(resultado.mensaje).toContain('monto de la transferencia debe ser positivo');
    });
});