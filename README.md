# Gestión Hotelera

#### Aplicación web para gestión hotelera: permite administrar huéspedes y hacer operaciones relacionadas (alta, modificación y búsqueda).

#### Backend: Java + Spring Boot.
#### Frontend: Next.js, Typescript y Tailwind.
#### Base de datos: Postgres (Neon).
##### En el archivo schema.sql se encuentra el script de creación de base de datos y en poblacion.sql se encuentra el script para poblar la misma.
#### Nota: al ejecutar la API, automaticamente se crean las tablas juntos con las habitaciones. Para eliminar todos los datos y volver a inicializar las tablas, cambiar la variable "spring.jpa.hibernate.ddl-auto" a create, en lugar de update. Esta se encuentra en backend/src/main/resources/application.properties.

## Instrucciones para ejecución:

### 1. Descargar o clonar el respositorio

### 2. Abrir una terminal y acceder a la carpeta backend (cd backend)
### 3. Ejecutar "mvn spring-boot:run" para correr la API REST.
### 4. Abrir otra terminal y acceder a la carpeta frontend (cd frontend)
### 5. Ejecutar "npm run build"
### 6. Luego ejecutar "npm run start" para correr de forma local la aplicacion de Next.js
### 7. Abrir el navegador en la página http://localhost:3000

## Instrucciones para ejecutar los test (todos):

### 1. cd backend
### 2. mvn test

## Ejecutar los test de un servicio en especifico:

### Tests de HabitacionServiceImpl
#### 1. mvn test -Dtest=HabitacionServiceImplTest

### Tests de PasajeroServiceImpl
#### 2. mvn test -Dtest=PasajeroServiceImplTest

### Tests de ReservaServiceImpl
#### 3. mvn test -Dtest=ReservaServiceImplTest

### Generar reporte de cobertura con JaCoCo (el reporte se genera en target/site/jacoco/index.html)
#### 1. mvn clean test jacoco:report
