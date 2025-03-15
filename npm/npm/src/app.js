const fs = require('fs');

// Función para leer el archivo transactions.txt
function leerArchivo() {
    try {
        // Lee el contenido del archivo, o devuelve un objeto vacío si no existe
        if (fs.existsSync('transactions.txt')) {
            const data = fs.readFileSync('transactions.txt', 'utf8');
            return JSON.parse(data);
        } else {
            // Si el archivo no existe, devuelve un objeto vacío
            return {
                transactions: []
            };
        }
    } catch (error) {
        console.error('Error al leer el archivo:', error);
        // En caso de error, devuelve un objeto vacío
        return {
            transactions: []
        };
    }
}

// Función para escribir el archivo transactions.txt
function escribirArchivo(data) {
    try {
        // Convierte los datos a formato JSON y los guarda en el archivo
        fs.writeFileSync('transactions.txt', JSON.stringify(data, null, 2));
        return true;
    } catch (error) {
        console.error('Error al escribir el archivo:', error);
        return false;
    }
}

// Función para calcular el saldo actual de un usuario, basado en sus transacciones
function calcularSaldo(usuario) {
    // Lee el archivo de transacciones
    const data = leerArchivo();
    let saldo = 0;
    
    // Si no hay transacciones, retorna 0
    if (!data.transactions || !Array.isArray(data.transactions)) {
        return saldo;
    }
    
    // Recorre todas las transacciones
    for (const transaction of data.transactions) {
        // Si es un depósito para el usuario, suma al saldo
        if (transaction.para === usuario) {
            saldo += transaction.monto;
        }
        // Si es un retiro del usuario, resta del saldo
        if (transaction.de === usuario) {
            saldo -= transaction.monto;
        }
    }
    
    return saldo;
}

// Función para realizar la transferencia entre cuentas
function transferir(de, para, monto) {
    // Verifica que el monto sea positivo
    if (monto <= 0) {
        return {
            exito: false,
            mensaje: `El monto de la transferencia debe ser positivo.`
        };
    }
    
    // Calcula el saldo actual del usuario de origen
    const saldoOrigen = calcularSaldo(de);
    
    // Verifica que el saldo sea suficiente
    if (saldoOrigen < monto) {
        return {
            exito: false,
            mensaje: `Saldo insuficiente. El saldo actual de ${de} es ${saldoOrigen}.`
        };
    }
    
    // Lee las transacciones actuales
    const data = leerArchivo();
    
    // Si transactions no existe o no es un array, inicialízalo
    if (!data.transactions || !Array.isArray(data.transactions)) {
        data.transactions = [];
    }
    
    // Crea una nueva transacción
    const nuevaTransaccion = {
        de,
        para,
        monto,
        fecha: new Date().toISOString()
    };
    
    // Agrega la transacción al array de transacciones
    data.transactions.push(nuevaTransaccion);
    
    // Guarda las transacciones actualizadas en el archivo
    escribirArchivo(data);
    
    return {
        exito: true,
        mensaje: `Transferencia de ${monto} realizada correctamente de ${de} a ${para}.`
    };
}

const resultado = transferir('juan.jose@urosario.edu.co', 'sara.palaciosc@urosario.edu.co', 50);
console.log(resultado.mensaje);

// Exportar las funciones para pruebas
module.exports = { leerArchivo, escribirArchivo, calcularSaldo, transferir };