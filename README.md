# VeterinariaApp - Evaluación Final Transversal (Semana 9)

## Objetivo General

Este proyecto corresponde a la **Evaluación Final Transversal (Semana 9)**. El objetivo principal ha sido consolidar una aplicación Android funcional y robusta, integrando componentes
 nativos como **Services**, **Broadcast Receivers** e **Intents**, manteniendo la arquitectura **MVVM** y aplicando principios de **Material Design 3**. Además, se han incorporado **pruebas unitarias** para garantizar la calidad y el correcto funcionamiento de la lógica de negocio.

---

## 1. Características Implementadas (Consolidado)

### a. Componentes Nativos
- **Activity**: Separación lógica en `MainActivity` y `ConsultasActivity`.
- **Service**: `NotificacionService` implementado como *Foreground Service* para recordatorios.
- **Content Provider**: `VeterinariaProvider` para exponer datos de mascotas de forma segura.
- **Broadcast Receiver**: `ConnectivityReceiver` para monitorear el estado de la red.

### b. Navegación e Intents
- **Intents Explícitos**: Para la navegación entre actividades.
- **Intents Implícitos**: Para compartir resúmenes de consultas.
- **Intent Filters**: Configuración de Deep Link.

### c. Interfaz de Usuario (UI) Moderna
- Implementación 100% con **Jetpack Compose** y **Material Theme 3**.
- Uso de `Scaffold`, `LazyColumn`, `Cards`, y `Navigation Drawer`.
- Validaciones de formularios en tiempo real.

---

## 2. Pruebas Unitarias (Semana 9)

Se han añadido pruebas unitarias utilizando **JUnit** y **Mockito** para validar la lógica de negocio en los ViewModels, asegurando que los casos de uso principales funcionen como se espera.

-   **`RegistroViewModelTest`**:
    -   Verifica que el registro de un dueño sea exitoso cuando los datos son válidos.
    -   Comprueba que el estado de la UI se actualice correctamente a `Success` tras un registro válido.
-   **Objetivo**: Garantizar la fiabilidad del flujo de registro y la correcta gestión del estado.

---

## 3. Arquitectura MVVM

El proyecto mantiene la estructura **Model-View-ViewModel** para asegurar modularidad y escalabilidad:

-   **Model**: Entidades (`Mascota`, `Consulta`) y Lógica de Negocio.
-   **View**: Pantallas en Compose, sin lógica de negocio.
-   **ViewModel**: Gestión del estado de la UI y comunicación con la capa de datos.

---

## 4. Estructura del Proyecto

```
app/
└── src/
    ├── main/
    │   └── java/cl/duoc/veterinaria/
    │       ├── MainActivity.kt
    │       ├── ConsultasActivity.kt
    │       ├── data/
    │       ├── model/
    │       ├── provider/
    │       ├── receiver/
    │       ├── service/
    │       └── ui/
    │           ├── navigation/
    │           ├── registro/
    │           └── viewmodel/
    └── test/
        └── java/cl/duoc/veterinaria/
            └── ui/viewmodel/
                └── RegistroViewModelTest.kt  (Pruebas Unitarias)
```

---

## 5. Instrucciones de Ejecución

1.  **Requisitos**: Android Studio Koala o superior.
2.  **Sincronización**: Permitir la sincronización de Gradle al abrir.
3.  **Ejecución**:
    - Ejecutar en un emulador (Recomendado API 34).
    - Para probar el **Broadcast Receiver**, alternar el modo avión o WiFi.
    - Para probar el **Service**, finalizar un registro para ver la notificación.
4.  **Ejecutar Pruebas**:
    - Clic derecho en `RegistroViewModelTest.kt` y seleccionar "Run '''RegistroViewModelTest'''".

---

**Autor:**
Liliana Tapia
**Asignatura:** Desarrollo de Apps Móviles I - DUOC UC
