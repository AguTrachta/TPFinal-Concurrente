# Trabajo Práctico Final - Programación Concurrente

Este repositorio contiene el código fuente y la documentación del trabajo final de la materia **Programación Concurrente**. El proyecto consiste en la implementación de una **Red de Petri** para modelar y analizar un sistema concurrente en **Java**, utilizando monitores para la sincronización y políticas de resolución de conflictos.

## 📌 Características del Proyecto
- Implementación de una **Red de Petri** con **6 segmentos**.
- **Concurrencia** con **4 hilos simultáneos** para la Red de Petri.
- Uso de **monitores** para la gestión de sincronización y control de acceso.
- Implementación de **dos políticas de ejecución**:
  - **BalancedPolicy**: Balancea la ejecución entre caminos alternativos.
  - **PriorityPolicy**: Favorece ciertos caminos según prioridad establecida.
- **Validación de propiedades** mediante la herramienta **PIPE**.
- **Registro de eventos y análisis de tiempos de ejecución**.

## 📂 Estructura del Proyecto
```
TPFinal-Concurrente/
│── src/
│   ├── monitor/      # Coordinador central del sistema
│   ├── petrinet/     # Implementación de la Red de Petri
│   ├── pool/         # Administración de hilos
│   ├── utils/        # Clases de soporte (logger, configuración, notificaciones)
│   └── Main.java     # Punto de entrada del programa
│── files/           # Diagramas y gráficos de análisis
│── README.md         # Este archivo
```

## 🚀 Cómo Ejecutar
1. **Clonar el repositorio**
   ```sh
   git clone https://github.com/usuario/TPFinal-Concurrente.git
   cd TPFinal-Concurrente
   ```
2. **Compilar el código**
   ```sh
   javac -d bin src/**/*.java
   ```
3. **Ejecutar el programa**
   ```sh
   java -cp bin Main
   ```
4. **Ver resultados en el log** (se generará un archivo `petri_net.log` con la ejecución).

## 📊 Análisis de Resultados
Los experimentos realizados con diferentes tiempos de ejecución en las transiciones muestran cómo las políticas afectan el rendimiento. Los gráficos de comparación están disponibles en el informe `TP_Final.pdf`.

## 📖 Referencias
- Stallings, W. (2018). *Operating Systems: Internals and Design Principles*.
- Contenido del curso de *Programación Concurrente* - FCEFyN, UNC.
- Algoritmos para la determinación de concurrencia con Redes de Petri.

---
**Autores:**
- Mateo Rodríguez
- Agustín Trachta

📌 *Universidad Nacional de Córdoba - Facultad de Ciencias Exactas, Físicas y Naturales*
