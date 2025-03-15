import sys
import random
import string

def generar_codigo():
    """
    Debe generar un código de 6 caracteres alfanuméricos sin caracteres ambiguos.
    Caracteres ambiguos a evitar: '0', 'O', '1', 'l'
    """
    caracteres_validos = [c for c in string.ascii_letters + string.digits if c not in {'0', 'O', '1', 'l'}]
    return ''.join(random.choices(caracteres_validos, k=6))


if __name__ == "__main__":
    if len(sys.argv) != 2 or not sys.argv[1].isdigit():
        print("Uso: python main.py <cantidad_de_codigos>")
        sys.exit(1)

    cantidad = int(sys.argv[1])
    print("Generando códigos de recuperación...")

    for i in range(cantidad):
        print(f"Código {i+1}: {generar_codigo()}")
