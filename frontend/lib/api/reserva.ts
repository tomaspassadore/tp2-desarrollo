import { apiFetch } from "./fetch"

export type Reserva = {
  id: number

  // Fechas (en JSON suelen venir como string ISO)
  fechaIngreso: string
  fechaEgreso: string

  // Relaciones mínimas que usamos desde el front
  habitacion: {
    id: number
    numero: string
  }
}

export type CrearReservaRequest = {
  fechaIngreso: string
  fechaEgreso: string
  habitacion: {
    numero: string
  }
  dniPasajero?: string
}

export function crearReserva(payload: CrearReservaRequest) {
  return apiFetch<Reserva>("/reservas/crear", {
    method: "POST",
    json: payload,
  })
}
