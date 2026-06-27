# ArriendApp 🚚

<p align="center">
  <img src="docs/screenshots/screenshot_1.png" width="220" alt="Login"/>
  &nbsp;&nbsp;
  <img src="docs/screenshots/screenshot_2.png" width="220" alt="Catálogo"/>
  &nbsp;&nbsp;
  <img src="docs/screenshots/screenshot_3.png" width="220" alt="Nuevo arriendo"/>
  &nbsp;&nbsp;
  <img src="docs/screenshots/screenshot_4.png" width="220" alt="Asistente ia"/>
</p>

<p align="center">
  <strong>Gestión profesional de arriendos de equipos, logística y clientes en tiempo real.</strong>
</p>

---

## ¿Qué es ArriendApp?

**ArriendApp** es una aplicación Android nativa diseñada para empresas y emprendedores que gestionan arriendos de equipos. Permite llevar el control total del catálogo, registrar reservas con fechas y precios en CLP, y contactar a los clientes con un solo toque directamente desde la agenda.

> Sin papeles. Sin confusión. Solo gestión que funciona.

---

## ✨ Características

- 🔐 **Login con Email / Google** — Autenticación segura con Firebase Auth
- 📦 **Catálogo CRUD** — Añade, edita y elimina equipos con sincronización en la nube (Firestore)
- 📅 **Calendario de Reservas** — Visualiza el estado de cada arriendo en tiempo real
- 💰 **Precios en CLP** — Formatos de moneda localizada para pesos chilenos
- 📞 **Contacto Integrado** — Llama al cliente o escríbele por WhatsApp con un toque, sin añadirlo a contactos
- 🤖 **Asistente IA (LuxeAssist)** — FAB arrastrable con chat de inteligencia artificial integrado
- 🎨 **UI/UX Premium** — Diseño dark mode moderno con Jetpack Compose y Material Design 3

---

## 📱 Pantallas

| Login | Catálogo | Nuevo arriendo | Asistente ia |
|-------|----------|----------------|--------------|
| <img src="docs/screenshots/screenshot_1.png" width="180"/> | <img src="docs/screenshots/screenshot_2.png" width="180"/> | <img src="docs/screenshots/screenshot_3.png" width="180"/> | <img src="docs/screenshots/screenshot_4.png" width="180"/> |

| Calendario | Contacto Cliente | Estado | Detalle de entrega |
|------------|------------------|--------|--------------------|
| <img src="docs/screenshots/screenshot_5.png" width="180"/> | <img src="docs/screenshots/screenshot_6.png" width="180"/> | <img src="docs/screenshots/screenshot_7.png" width="180"/> | <img src="docs/screenshots/screenshot_8.png" width="180"/> |

---

## 🛠️ Stack Tecnológico

| Tecnología | Uso |
|---|---|
| **Kotlin** | Lenguaje principal |
| **Jetpack Compose** | UI declarativa 100% nativa |
| **Material Design 3** | Sistema de diseño y componentes |
| **Firebase Auth** | Autenticación con Email y Google |
| **Cloud Firestore** | Base de datos en tiempo real en la nube |
| **Room (SQLite)** | Persistencia local de reservas |
| **Kotlin Coroutines & Flows** | Programación asíncrona reactiva |
| **MVVM** | Arquitectura Model-View-ViewModel |
| **Intents Nativos** | Llamadas y WhatsApp directos desde la app |

---

## 🎨 Diseño

La UI está construida alrededor de una identidad de marca profesional y oscura:
- **Fondo:** Degradado oscuro entre `#1A2530` y `#0F171E`
- **Acento primario:** Cyan eléctrico `#00B4D8`
- **Estados de equipo:** Verde `#81C784` (Disponible), Rojo `#E57373` (Ocupado), Amarillo `#FFD54F` (Mantenimiento)
- **Tipografía:** Material Design 3 (sans-serif moderna)
- **Logo:** Camioneta tipo furgón con identidad corporativa

---

## 📂 Estructura del Proyecto

```
app/
├── java/com/kleber/arriendapp/
│   ├── data/
│   │   ├── Entities.kt              # Modelos Room: ReservaEntity, etc.
│   │   ├── FirebaseRepository.kt    # Lógica centralizada de Firestore/Auth
│   │   └── AppDatabase.kt           # Configuración de Room DB
│   └── ui/
│       ├── LuxeRentalApp.kt         # NavHost, FAB arrastrable, navegación
│       ├── screens/
│       │   ├── auth/
│       │   │   ├── LoginScreen.kt   # Pantalla de inicio de sesión
│       │   │   └── AuthViewModel.kt
│       │   ├── catalog/
│       │   │   ├── CatalogScreen.kt # Catálogo + formulario de arriendo
│       │   │   └── CatalogViewModel.kt
│       │   ├── calendar/
│       │   │   └── CalendarScreen.kt # Agenda + detalles + intents de contacto
│       │   └── ai/
│       │       └── AiChatScreen.kt  # Chat con asistente IA
│       └── theme/
│           ├── Color.kt             # Paleta de colores corporativos
│           └── Theme.kt             # Configuración del tema Material 3
└── res/
    ├── drawable/                    # Logo, íconos y assets
    ├── mipmap-*/                    # Íconos de la app en todas las densidades
    └── values/                      # Colors, themes, strings
```

---

## 🔒 Nota sobre el Proyecto

Este repositorio funciona como portafolio y exhibición de la arquitectura, diseño UI/UX y capacidades técnicas de **ArriendApp**.

Al ser una aplicación comercial privada, las credenciales de Firebase (`google-services.json`) y el acceso al backend se mantienen restringidos.

---

## 📜 Licencia

Proyecto privado — **ArriendApp** © 2026. Todos los derechos reservados.

---

<p align="center">
  Hecho con 🚚 y mucho trabajo en Chile.
</p>
