/* INSERCIÓN DE DATOS DE PRUEBA */

-- Insertar Tipos de Habitación
INSERT INTO tipo_habitacion (id, nombre, costo_por_noche, cantidad_disponible) VALUES 
(1, 'Individual Estándar', 50800.00, 10),
(2, 'Doble Estándar', 70230.00, 18),
(3, 'Doble Superior', 90560.00, 8),
(4, 'Superior Family Plan', 110500.00, 10),
(5, 'Suite Doble', 128600.00, 2);

-- Insertar las 48 Habitaciones

-- Individual Estándar (10 habitaciones): 101-110
INSERT INTO habitacion (numero, tipo_habitacion_id, estado) VALUES 
(101, 1, 'RESERVADA'),  -- Reservada por Juan
(102, 1, 'LIBRE'),
(103, 1, 'LIBRE'),
(104, 1, 'LIBRE'),
(105, 1, 'LIBRE'),
(106, 1, 'LIBRE'),
(107, 1, 'LIBRE'),
(108, 1, 'LIBRE'),
(109, 1, 'LIBRE'),
(110, 1, 'LIBRE');

-- Doble Estándar (18 habitaciones): 201-218
INSERT INTO habitacion (numero, tipo_habitacion_id, estado) VALUES 
(201, 2, 'RESERVADA'),  -- Reservada por María (primera reserva)
(202, 2, 'LIBRE'),
(203, 2, 'LIBRE'),
(204, 2, 'LIBRE'),
(205, 2, 'LIBRE'),
(206, 2, 'LIBRE'),
(207, 2, 'LIBRE'),
(208, 2, 'LIBRE'),
(209, 2, 'LIBRE'),
(210, 2, 'LIBRE'),
(211, 2, 'LIBRE'),
(212, 2, 'LIBRE'),
(213, 2, 'LIBRE'),
(214, 2, 'LIBRE'),
(215, 2, 'LIBRE'),
(216, 2, 'LIBRE'),
(217, 2, 'LIBRE'),
(218, 2, 'LIBRE');

-- Doble Superior (8 habitaciones): 301-308
INSERT INTO habitacion (numero, tipo_habitacion_id, estado) VALUES 
(301, 3, 'LIBRE'),
(302, 3, 'LIBRE'),
(303, 3, 'LIBRE'),
(304, 3, 'LIBRE'),
(305, 3, 'LIBRE'),
(306, 3, 'LIBRE'),
(307, 3, 'LIBRE'),
(308, 3, 'LIBRE');

-- Superior Family Plan (10 habitaciones): 401-410
INSERT INTO habitacion (numero, tipo_habitacion_id, estado) VALUES 
(401, 4, 'RESERVADA'),  -- Reservada por María (segunda reserva)
(402, 4, 'LIBRE'),
(403, 4, 'LIBRE'),
(404, 4, 'LIBRE'),
(405, 4, 'LIBRE'),
(406, 4, 'LIBRE'),
(407, 4, 'LIBRE'),
(408, 4, 'LIBRE'),
(409, 4, 'LIBRE'),
(410, 4, 'LIBRE');

-- Suite Doble (2 habitaciones): 501-502
INSERT INTO habitacion (numero, tipo_habitacion_id, estado) VALUES 
(501, 5, 'LIBRE'),
(502, 5, 'LIBRE');

-- Insertar Direcciones (5 direcciones para 5 pasajeros)
INSERT INTO direccion (id, calle, numero, piso, departamento, codigo_postal, localidad, provincia, pais) VALUES 
(1, 'Av. Corrientes', '1234', '5', 'A', '1043', 'CABA', 'Buenos Aires', 'Argentina'),
(2, 'San Martín', '567', NULL, NULL, '5000', 'Córdoba', 'Córdoba', 'Argentina'),
(3, 'Mitre', '890', '2', 'B', '2000', 'Rosario', 'Santa Fe', 'Argentina'),
(4, 'Belgrano', '456', '10', 'C', '4000', 'San Miguel de Tucumán', 'Tucumán', 'Argentina'),
(5, 'Rivadavia', '2345', NULL, NULL, '3300', 'Posadas', 'Misiones', 'Argentina');

-- Insertar Pasajeros (5 pasajeros)
INSERT INTO pasajero (id, nombre, apellido, nro_documento, cuit, fecha_de_nacimiento, nacionalidad, email, telefono, ocupacion, estado, direccion_id) VALUES 
(1, 'Juan', 'Pérez', '12345678', '20-12345678-9', '1985-03-15 00:00:00', 'Argentina', 'juan.perez@email.com', '11-2345-6789', 'Ingeniero', 'ACTIVO', 1),
(2, 'María', 'González', '23456789', '27-23456789-0', '1990-07-22 00:00:00', 'Argentina', 'maria.gonzalez@email.com', '351-456-7890', 'Médica', 'ACTIVO', 2),
(3, 'Carlos', 'Rodríguez', '34567890', '20-34567890-1', '1978-11-30 00:00:00', 'Argentina', 'carlos.rodriguez@email.com', '341-567-8901', 'Abogado', 'ACTIVO', 3),
(4, 'Ana', 'Martínez', '45678901', '27-45678901-2', '1995-05-18 00:00:00', 'Argentina', 'ana.martinez@email.com', '381-678-9012', 'Arquitecta', 'ACTIVO', 4),
(5, 'Luis', 'Fernández', '56789012', '20-56789012-3', '1982-09-25 00:00:00', 'Argentina', 'luis.fernandez@email.com', '376-789-0123', 'Contador', 'ACTIVO', 5);

-- Insertar Reservas
-- Reserva 1: Para Juan Pérez (pasajero_id = 1) - 1 reserva en habitación 101
INSERT INTO reserva (id, fecha_ingreso, fecha_egreso, habitacion_id, responsable_id) VALUES 
(1, '2024-12-20 14:00:00', '2024-12-25 11:00:00', 101, 1);

-- Reserva 2 y 3: Para María González (pasajero_id = 2) - 2 reservas
INSERT INTO reserva (id, fecha_ingreso, fecha_egreso, habitacion_id, responsable_id) VALUES 
(2, '2024-12-18 15:00:00', '2024-12-22 10:00:00', 201, 2),
(3, '2025-01-10 14:00:00', '2025-01-15 11:00:00', 401, 2);

-- Los pasajeros Carlos (3), Ana (4) y Luis (5) no tienen reservas