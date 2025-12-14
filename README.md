# VeterinariaApp - Sumativa 3: Aplicando Android UI (Semana 8)

## Objetivo General

Este proyecto corresponde a la **Sumativa N°3 de la Semana 8**, titulada "Aplicando Android UI". El objetivo principal ha sido diseñar e implementar elementos de interfaz de usuario (UI) simples y funcionales, integrando utilidades gráficas nativas de Android, como **Services**, **Content Providers**, **Broadcast Receivers** e **Intents**, manteniendo la arquitectura **MVVM** desarrollada previamente y aplicando principios de **Material Design 3**.

---

## 1. Características Implementadas (Semana 8)

Se han integrado componentes fundamentales de Android para extender la funcionalidad de la aplicación:

### a. Componentes Nativos
- **Activity**: Separación lógica en dos actividades:
    - `MainActivity`: Contenedor principal de la aplicación.
    - `ConsultasActivity`: Actividad secundaria dedicada exclusivamente a la gestión y listado de consultas.
- **Service**: `NotificacionService` implementado como un *Foreground Service* para enviar recordatorios de salud y alertas en segundo plano.
- **Content Provider**: `VeterinariaProvider` configurado para exponer datos básicos de mascotas (con permisos de lectura seguros).
- **Broadcast Receiver**: `ConnectivityReceiver` que monitorea el estado de la red y notifica al usuario sobre la conexión a internet.

### b. Navegación e Intents
- **Intents Explícitos**: Navegación directa entre la pantalla principal y la pantalla de listado.
- **Intents Implícitos**: Funcionalidad para **compartir el resumen de la consulta** como texto plano a través de otras aplicaciones (WhatsApp, Gmail, etc.).
- **Intent Filters**: Configuración de Deep Link para abrir la aplicación desde una URL externa.

### c. Interfaz de Usuario (UI) Moderna
- Implementación completa con **Jetpack Compose** y **Material Theme 3**.
- Uso de componentes como `Scaffold`, `LazyColumn`, `Cards`, `OutlinedTextField` y `Navigation Drawer`.
- Validaciones en tiempo real para mejorar la experiencia de usuario en los formularios.

---

## 2. Arquitectura MVVM (Consolidada desde Semana 7)

El proyecto mantiene y refuerza la estructura **Model-View-ViewModel** establecida anteriormente para asegurar modularidad y escalabilidad:

-   **Model**: Entidades (`Mascota`, `Consulta`) y Servicios de Negocio.
-   **View**: Pantallas (`Screens`) desarrolladas en Compose, libres de lógica de negocio.
-   **ViewModel**: Gestión del estado de la UI y comunicación con el Repositorio.

---

## 3. Estructura del Proyecto

El código fuente está organizado para reflejar esta arquitectura limpia:

```
app/
└── src/
    └── main/
        └── java/
            └── cl/
                └── duoc/
                    └── veterinaria/
                        ├── MainActivity.kt        (Punto de entrada)
                        ├── ConsultasActivity.kt   (Activity secundaria)
                        ├── data/                  (Repositorio de datos)
                        ├── service/               (NotificacionService y lógica de negocio)
                        ├── provider/              (VeterinariaProvider)
                        ├── receiver/              (ConnectivityReceiver)
                        ├── model/                 (Clases de datos)
                        └── ui/                    (Capa de Presentación)
                            ├── navigation/
                            ├── screens/           (Bienvenida, Listado, Pedidos)
                            ├── registro/          (Flujo de registro)
                            └── viewmodel/         (ViewModels)
```

---

## 4. Instrucciones de Ejecución

1.  **Requisitos**: Android Studio Koala o superior.
2.  **Sincronización**: Al abrir el proyecto, permitir la sincronización de Gradle.
3.  **Ejecución**:
    - Ejecutar en un emulador con API 26 o superior (Recomendado API 34).
    - Para probar el **Broadcast Receiver**, alternar el modo avión o WiFi en el emulador.
    - Para probar el **Service**, finalizar un registro y observar la notificación en la barra de estado.

---

**Autor:**
Liliana Tapia
**Asignatura:** Desarrollo de Apps Móviles I - DUOC UC