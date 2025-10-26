# 🐠 ToolsFish - Calculadora de Volumen de Acuario

## Descripción
ToolsFish es una aplicación Android desarrollada en Kotlin con Jetpack Compose que proporciona herramientas útiles para acuaristas. El primer módulo implementado es una **calculadora de volumen de acuario** que permite calcular el volumen de diferentes tipos de acuarios.

## Características del Módulo de Calculadora de Volumen

### 🎯 Funcionalidades Principales

#### 1. Tipos de Acuario Soportados
- **Rectangular**: Acuarios tradicionales de forma rectangular
- **Cilíndrico**: Acuarios cilíndricos (como bowls o columnas)
- **Esquina (Triangular)**: Acuarios de esquina con forma triangular
- **Hexagonal**: Acuarios hexagonales

#### 2. Cálculos Automáticos
- **Fórmulas implementadas**:
  - Rectangular: `V = largo × ancho × alto`
  - Cilíndrico: `V = π × (radio²) × altura`
  - Esquina: `V = (base × profundidad × altura) / 2`
  - Hexagonal: `V = 2.598 × lado² × altura`

#### 3. Conversiones de Unidades
- **Centímetros cúbicos** → **Litros** (÷ 1000)
- **Litros** → **Galones** (× 0.264172)
- Resultado mostrado en ambas unidades

#### 4. Validaciones
- No permite campos vacíos
- No permite valores negativos
- Mensajes de error claros y específicos
- Validación en tiempo real

### 🎨 Diseño y UI

#### Tema Acuático
- **Colores principales**: Azules y verdes acuáticos
- **Tema claro**: Fondo azul claro con texto azul marino
- **Tema oscuro**: Fondo azul marino con texto azul claro
- **Componentes**: Cards, RadioButtons, TextFields, Buttons

#### Componentes de UI
- **Selector de tipo**: RadioButtons para elegir el tipo de acuario
- **Formulario dinámico**: Campos que cambian según el tipo seleccionado
- **Validación visual**: Mensajes de error en cards rojas
- **Resultado detallado**: Card con volumen en litros y galones + descripción

### 🛠️ Arquitectura Técnica

#### Estructura del Código
```
app/src/main/java/com/example/toolsfish/
├── models/
│   └── AquariumModels.kt          # Enums, data classes y lógica de cálculo
├── ui/
│   ├── AquariumCalculatorScreen.kt # Pantalla principal con Compose
│   └── theme/
│       ├── Color.kt               # Colores acuáticos
│       └── Theme.kt               # Tema personalizado
└── MainActivity.kt               # Activity principal
```

#### Tecnologías Utilizadas
- **Kotlin**: Lenguaje principal
- **Jetpack Compose**: UI moderna y declarativa
- **Material Design 3**: Componentes y tema
- **Arquitectura simple**: Una sola pantalla para el primer módulo

### 📱 Flujo de Uso

1. **Abrir la aplicación**
2. **Seleccionar tipo de acuario** (Rectangular, Cilíndrico, Esquina, Hexagonal)
3. **Introducir medidas** (los campos cambian según el tipo)
4. **Presionar "Calcular Volumen"**
5. **Ver resultado** con volumen en litros y galones + descripción detallada

### 🔮 Funcionalidades Futuras (Comentadas en el Código)

- **Conversor de unidades**: cm ↔ pulgadas
- **Estimador de peces**: Según volumen del acuario
- **Historial de cálculos**: Guardar cálculos recientes
- **Módulos adicionales**: pH, iluminación, filtración

### 📊 Ejemplo de Uso

**Entrada**:
- Tipo: Rectangular
- Largo: 100 cm
- Ancho: 40 cm
- Alto: 50 cm

**Resultado**:
- Volumen: 200.0 L
- Equivalente: 52.8 galones
- Descripción: "Tu acuario rectangular de 100 cm de largo, 40 cm de ancho y 50 cm de alto tiene un volumen aproximado de 200.0 L (52.8 galones)."

### 🚀 Cómo Ejecutar

1. Abrir el proyecto en Android Studio
2. Sincronizar Gradle
3. Ejecutar en dispositivo o emulador
4. ¡Disfrutar calculando volúmenes de acuarios!

---

**Desarrollado con ❤️ para la comunidad acuarista**
