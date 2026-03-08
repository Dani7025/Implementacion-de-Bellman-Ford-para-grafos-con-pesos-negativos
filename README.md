# Implementacion-de-Bellman-Ford-para-grafos-con-pesos-negativos
## ✈ AeroRuta — Sistema Inteligente de Navegación Aérea

Este proyecto fue desarrollado como parte de la evaluación de la asignatura
**Técnicas de Programación 3** de la Universidad Nacional Experimental de
Guayana (UNEG).

El objetivo principal es aplicar los conceptos de **Análisis y Diseño
Orientado a Objetos (OO)**, patrones de diseño y la **Teoría de Grafos**
mediante el algoritmo **Bellman-Ford** en una aplicación funcional con
interfaz gráfica.

---

## Descripción del Programa

El software modela una red aérea como un **grafo dirigido con pesos** y
permite al usuario encontrar la ruta de menor costo entre dos aeropuertos,
considerando descuentos, impuestos y tasas de aeropuerto.

- **Gestión de Aeropuertos:** Registro, actualización y eliminación de
  aeropuertos con código IATA, nombre, ciudad y país. La eliminación aplica
  cascada automática sobre todas las rutas asociadas.
- **Gestión de Rutas:** Soporte para dos tipos de vuelo:
  - *Vuelo Doméstico:* Costo final con impuesto nacional.
  - *Vuelo Internacional:* Costo final con impuesto internacional y tasa
    fija de aeropuerto.
- **Cálculo de Ruta Óptima:** Algoritmo Bellman-Ford que maneja pesos
  negativos (descuentos) y detecta ciclos negativos (tarifas fraudulentas
  o bonificaciones infinitas).
- **Visualización Gráfica:** Mapa interactivo de la red aérea con
  resaltado de la ruta óptima calculada.
- **Persistencia:** Base de datos SQLite embebida. Los datos se conservan
  entre sesiones sin configuración adicional.
- **Tema Visual:** Cambio dinámico entre tema oscuro y claro (FlatLaf)
  sin reiniciar la aplicación.

---

## Arquitectura

El proyecto aplica el patrón **MVC** y cuatro patrones de diseño:

| Patrón | Clases |
|---|---|
| Singleton | `ConexionBD`, `GestorTema` |
| DAO | `AeropuertoDAO`, `RutaDAO` |
| Template Method | `Ruta` → `VueloDomestico` / `VueloInternacional` |
| Strategy | `IAlgoritmoGrafo` ← `AlgoritmoBellmanFord` |

### Estructura de Paquetes
```
src/
├── app.aeroruta/
│   └── Main.java
├── com.aeroruta.algo/
│   ├── IAlgoritmoGrafo.java
│   └── AlgoritmoBellmanFord.java
├── com.aeroruta.dao/
│   ├── ConexionBD.java
│   ├── AeropuertoDAO.java
│   └── RutaDAO.java
├── com.aeroruta.model/
│   ├── Aeropuerto.java
│   ├── Ruta.java
│   ├── VueloDomestico.java
│   ├── VueloInternacional.java
│   ├── GrafoAereo.java
│   └── ResultadoRuta.java
├── com.aeroruta.utils/
│   └── GestorTema.java
└── com.aeroruta.view/
    ├── FrmPrincipal.java
    ├── PanelMapa.java
    ├── PanelAeropuertos.java
    └── PanelRutas.java
```

---

## Herramientas Utilizadas

- **Lenguaje:** Java.
- **Entorno de Desarrollo (IDE):** NetBeans 8.0.2 (o superior).
- **Interfaz:** Java Swing.
- **Base de datos:** SQLite via JDBC (`sqlite-jdbc`).
- **Diseño:** Librería externa **FlatLaf** para tema oscuro/claro.

---

## Instrucciones de Instalación y Ejecución

Para ejecutar este proyecto en su computadora, siga estos pasos:

1. **Descargar:** Haga clic en el botón verde **Code** y seleccione
   **Download ZIP**, o clone el repositorio:
```bash
   git clone https://github.com/usuario/aeroruta.git
```

2. **Descomprimir:** Extraiga los archivos en una carpeta de su preferencia.

3. **Abrir en NetBeans:**
   - Inicie NetBeans.
   - Vaya a `File` → `Open Project`.
   - Busque la carpeta del proyecto y selecciónela.

4. **Verificar Librerías:**
   - El proyecto incluye una carpeta `lib` con los archivos
     `flatlaf.jar` y `sqlite-jdbc.jar`.
   - NetBeans debería reconocerlas automáticamente. Si marca error,
     haga clic derecho en el proyecto → `Properties` → `Libraries`
     y agregue los archivos JAR que están dentro de la carpeta `lib`.

5. **Correr el programa:**
   - Ubique el archivo `Main.java` dentro del paquete `app.aeroruta`.
   - Haga clic derecho y seleccione **Run File**.

> **Nota:** La base de datos `aeroruta.db` se crea automáticamente en
> el directorio de ejecución la primera vez que se corre el programa.
> No es necesario instalar SQLite ni configurar nada externamente.

---

## Autora

- **Angie Urrieta** — C.I: 31.538.385

**Sección:** 3 &nbsp;|&nbsp; **Profesora:** Ing. Dubraska Roca
&nbsp;|&nbsp; **Período:** 2025-2
