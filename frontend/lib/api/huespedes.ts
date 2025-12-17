// Peticiones relacionadas con huéspedes
import { apiFetch } from "./fetch"

export type Criterio = "dni" | "nombre" | "apellido"

export type BuscarHuespedRequest = {
  criterio: Criterio
  valor: string
}

export type Direccion = {
  id?: number
  calle: string
  numero: string
  departamento: string
  piso: string
  codigoPostal: string
  localidad: string
  provincia: string
  pais: string
}

export type Huesped = {
  id?: number
  nombre: string
  apellido: string
  nroDocumento: string
  telefono: string
  email?: string // Opcional
  cuit?: string // Opcional
  fechaDeNacimiento: string
  nacionalidad: string
  ocupacion: string
  direccion: Direccion
}

export type BuscarHuespedResponse = {
  resultados: Huesped[]
}

export function buscarHuesped(payload: BuscarHuespedRequest) {
  return apiFetch<Huesped[] | BuscarHuespedResponse>("/pasajeros/buscar", {
    method: "POST",
    json: payload,
  }).then((data) => {
    if (Array.isArray(data)) return data
    return data?.resultados ?? []
  })
}

export function darAltaHuesped(payload: Huesped) {
  return apiFetch("/pasajeros/dar-alta", {
    method: "POST",
    json: payload,
  })
}

export function actualizarHuesped(id: number, payload: Huesped) {
  return apiFetch<Huesped>(`/pasajeros/${id}`, {
    method: "PUT",
    json: payload,
  })
}

export function eliminarHuesped(id: number) {
  return apiFetch<void>(`/pasajeros/${id}`, {
    method: "DELETE",
  })
}