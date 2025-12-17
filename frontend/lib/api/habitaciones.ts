// Peticiones relacionadas con habitaciones
import { apiFetch } from "./fetch"

export type TipoHabitacion = {
  id?: number
  nombre: string
  costoPorNoche: number
  cantidadDisponible: number
}

export type Habitacion = {
  id?: number
  numero: string
  idHabitacion: string
  estado: "OCUPADA" | "RESERVADA" | "LIBRE" | "EN_MANTENIMIENTO"
  tipoHabitacion: TipoHabitacion
}

export function obtenerTodasHabitaciones() {
  return apiFetch<Habitacion[]>("/habitaciones/listar", {
    method: "GET",
  })
}

