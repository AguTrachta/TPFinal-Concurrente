
# Relación entre Clases del Sistema

## **Relación General**

```plaintext
[Main (Simulador)]
       |
       ↓
    [Monitor] ↔ [Política]
       ↓
[Plazas] ↔ [Transiciones]
       ↕
[Pool de Hilos]
       ↕
    [Logger]
       ↕
 [Invariantes]
```

---

## **Descripción de las Relaciones**

### 1. **Main (Simulador)**:
- **Rol**: Punto de entrada del programa, inicializa y coordina todos los componentes.
- **Relación con las demás clases**:
  - Crea instancias de `Monitor`, `Política`, `Plazas`, `Transiciones`, `Pool de Hilos`, `Logger` e `Invariantes`.
  - Configura la red de Petri:
    - Define las plazas y transiciones.
    - Asocia transiciones con plazas de entrada/salida.
  - Inicia la simulación llamando al `Monitor` y coordinando la ejecución.
  - Solicita verificaciones de `Invariantes` y escribe resultados en el `Logger`.

---

### 2. **Monitor**:
- **Rol**: El controlador central que gestiona la ejecución de la red de Petri.
- **Relación con las demás clases**:
  - **Plazas**: Consulta los tokens para verificar las condiciones de disparo de transiciones.
  - **Transiciones**: Comprueba si pueden dispararse y actualiza las plazas asociadas tras el disparo.
  - **Política**: Consulta a la política activa para decidir el orden de disparo en caso de conflicto.
  - **Pool de Hilos**: Envía transiciones listas para dispararse a los hilos disponibles.
  - **Logger**: Registra eventos (disparos, estados de plazas, etc.) para análisis.
  - **Invariantes**: Verifica los invariantes después de cada disparo para garantizar la consistencia del sistema.

---

### 3. **Política**:
- **Rol**: Define las estrategias para resolver conflictos entre transiciones.
- **Relación con las demás clases**:
  - **Monitor**: El Monitor consulta la Política cuando hay varias transiciones listas para dispararse, para decidir cuál priorizar.
  - No interactúa directamente con `Plazas`, `Transiciones`, o `Pool de Hilos`.

---

### 4. **Plazas**:
- **Rol**: Representan los recursos (tokens) de la red de Petri.
- **Relación con las demás clases**:
  - **Monitor**: Verifica el estado de las plazas para determinar si una transición puede dispararse.
  - **Transiciones**: Las transiciones consumen tokens de plazas de entrada y producen tokens en plazas de salida.
  - **Logger**: Los cambios en los tokens de las plazas se registran para análisis.
  - **Invariantes**: Las plazas son evaluadas por los invariantes de plaza para garantizar la conservación de tokens.

---

### 5. **Transiciones**:
- **Rol**: Representan las reglas que mueven tokens entre plazas.
- **Relación con las demás clases**:
  - **Plazas**: Consumirán tokens de las plazas de entrada y producirán tokens en las de salida.
  - **Monitor**: Coordina el disparo de transiciones, verificando condiciones y actualizando plazas.
  - **Pool de Hilos**: Cada transición lista para dispararse es ejecutada como una tarea en la pool de hilos.
  - **Logger**: Los disparos de transiciones se registran para análisis.
  - **Invariantes**: Se verifica que los disparos cumplan con los invariantes de transición.

---

### 6. **Pool de Hilos**:
- **Rol**: Gestiona la ejecución concurrente de transiciones.
- **Relación con las demás clases**:
  - **Monitor**: Envía tareas (transiciones listas para dispararse) a la pool de hilos.
  - **Transiciones**: Cada transición se ejecuta como una tarea en la pool.
  - **Logger**: Puede registrar información sobre el uso de hilos o la ejecución concurrente.
  - No interactúa directamente con `Plazas` o `Política`.

---

### 7. **Logger**:
- **Rol**: Registra información sobre el estado del sistema y los eventos para análisis posterior.
- **Relación con las demás clases**:
  - **Monitor**: Registra eventos como disparos de transiciones, estados de plazas y resultados de políticas.
  - **Plazas** y **Transiciones**: Los cambios en los tokens de plazas y disparos de transiciones se registran.
  - **Invariantes**: Los resultados de las verificaciones de invariantes se escriben en los logs.

---

### 8. **Invariantes**:
- **Rol**: Verifica propiedades de la red de Petri para garantizar su consistencia y seguridad.
- **Relación con las demás clases**:
  - **Monitor**: El Monitor llama a los invariantes después de cada disparo o al finalizar la simulación.
  - **Plazas**: Los invariantes de plaza verifican que se conserve un balance adecuado de tokens.
  - **Transiciones**: Los invariantes de transición verifican que los disparos respeten las reglas definidas.
  - **Logger**: Los resultados de las verificaciones se registran en los logs.

---

## **Secuencia Simplificada**

1. El **Main** inicializa todas las clases y configura la red de Petri.
2. El **Monitor** ejecuta la simulación:
   - Consulta a las **Plazas** y **Transiciones** para determinar qué transiciones pueden dispararse.
   - Usa la **Política** para resolver conflictos entre transiciones listas.
   - Envía las transiciones listas a la **Pool de Hilos**.
3. Los **hilos** ejecutan las transiciones, actualizando las **Plazas**.
4. Después de cada disparo:
   - El **Monitor** verifica los **Invariantes**.
   - El **Logger** registra el estado del sistema.
5. Al finalizar, el **Logger** contiene toda la información para analizar la ejecución.

---

## **¿Faltaría algo más?**

Este diseño es bastante completo, pero podríamos agregar:

1. **Clase de Configuración**:
   - Para centralizar parámetros como:
     - Número máximo de hilos.
     - Tiempos asociados a transiciones.
     - Valores iniciales de tokens en las plazas.

2. **Manejo de errores**:
   - Asegurar que el sistema pueda manejar condiciones excepcionales (por ejemplo, un deadlock detectado).


