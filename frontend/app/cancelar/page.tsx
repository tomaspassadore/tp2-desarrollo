"use client"

import { useEffect, useMemo, useState } from "react"
import { CalendarX, Search } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { DashboardLayout } from "@/components/dashboard-layout"
import { apiFetch } from "@/lib/api/fetch"
import { toast } from "@/components/ui/use-toast"

type ApiPasajero = {
  id?: number
  nombre?: string
  apellido?: string
  nroDocumento?: string
}

type ApiHabitacion = {
  id?: number
  numero?: string
}

type ApiResponsableReserva = {
  id?: number
  nombre?: string
  apellido?: string
  telefono?: string
}

type ApiReserva = {
  id: number
  fechaIngreso?: string | number
  fechaEgreso?: string | number
  habitacion?: ApiHabitacion
  pasajeros?: ApiPasajero[]
  responsable?: ApiPasajero
  responsableReserva?: ApiResponsableReserva // Mantener por compatibilidad
}

type ReservaDisplay = {
  id: string
  huesped: string
  habitacion: string
  entrada: string
  salida: string
}

function formatDate(value: unknown) {
  if (!value) return "-"
  const d = new Date(value as any)
  if (Number.isNaN(d.getTime())) return "-"
  return d.toISOString().slice(0, 10)
}

function getNombreCompleto(nombre?: string, apellido?: string) {
  const n = (nombre ?? "").trim()
  const a = (apellido ?? "").trim()
  const full = `${n} ${a}`.trim()
  return full.length ? full : "-"
}

export default function CancelarReserva() {
  const [reservas, setReservas] = useState<ReservaDisplay[]>([])
  const [cargando, setCargando] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [busqueda, setBusqueda] = useState("")
  const [ultimaBusquedaEjecutada, setUltimaBusquedaEjecutada] = useState("")
  const [cancelandoId, setCancelandoId] = useState<string | null>(null)

  // Función auxiliar para detectar si el input es un DNI (solo números)
  const esDni = (valor: string): boolean => {
    const soloNumeros = /^\d+$/.test(valor.trim())
    return soloNumeros && valor.trim().length >= 6 // DNI típicamente tiene al menos 6 dígitos
  }

  const buscarReservas = async (busqueda: string) => {
    try {
      setCargando(true)
      setError(null)
      const busquedaTrim = busqueda.trim()
      if (!busquedaTrim) {
        setReservas([])
        setUltimaBusquedaEjecutada("")
        setCargando(false)
        return
      }

      setUltimaBusquedaEjecutada(busquedaTrim)

      // Determinar si buscar por DNI o por nombre
      const buscarPorDni = esDni(busquedaTrim)
      const endpoint = buscarPorDni
        ? `/reservas/buscar-por-dni?dni=${encodeURIComponent(busquedaTrim)}`
        : `/reservas/buscar?nombre=${encodeURIComponent(busquedaTrim)}`

      const data = await apiFetch<ApiReserva[]>(endpoint, {
        method: "GET",
      })

      const mapeadas: ReservaDisplay[] = (data ?? []).map((r) => {
        // Priorizar responsable (campo actual), luego responsableReserva (compatibilidad), luego primer pasajero
        const responsable = r.responsable || r.responsableReserva
        const pasajero = (r.pasajeros && r.pasajeros.length > 0) ? r.pasajeros[0] : null

        // Obtener nombre del responsable o del primer pasajero
        let huesped = "-"
        if (responsable) {
          huesped = getNombreCompleto(responsable.nombre, responsable.apellido)
        } else if (pasajero) {
          huesped = getNombreCompleto(pasajero.nombre, pasajero.apellido)
        }

        return {
          id: String(r.id),
          huesped,
          habitacion: r.habitacion?.numero ?? "-",
          entrada: formatDate(r.fechaIngreso),
          salida: formatDate(r.fechaEgreso),
        }
      })

      setReservas(mapeadas)

      if (mapeadas.length === 0) {
        setError(null) // Limpiar error si no hay resultados pero la búsqueda fue exitosa
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : "Error al cargar las reservas")
      setReservas([])
      console.error("Error al cargar reservas:", err)
    } finally {
      setCargando(false)
    }
  }

  const busquedaNormalizada = busqueda.trim().toLowerCase()
  const busquedaCoincide = useMemo(
    () =>
      busquedaNormalizada.length > 0 &&
      busquedaNormalizada === ultimaBusquedaEjecutada.trim().toLowerCase(),
    [busquedaNormalizada, ultimaBusquedaEjecutada]
  )

  const reservasFiltradas = useMemo(
    () => (busquedaCoincide ? reservas : []),
    [reservas, busquedaCoincide]
  )

  const cancelarReserva = async (id: string) => {
    const idTrim = id.trim()
    if (!idTrim) {
      toast({
        title: "ID requerido",
        description: "Ingresá un ID de reserva para cancelar.",
        variant: "destructive",
      })
      return
    }

    try {
      setCancelandoId(idTrim)
      await apiFetch<void>(`/reservas/${encodeURIComponent(idTrim)}`, { method: "DELETE" })
      toast({
        title: "Reserva cancelada",
        description: `La reserva ${idTrim} fue cancelada correctamente.`,
      })
      await buscarReservas(busqueda)
    } catch (err) {
      const message = err instanceof Error ? err.message : "Error al cancelar la reserva"
      toast({
        title: "No se pudo cancelar",
        description: message,
        variant: "destructive",
      })
      console.error("Error al cancelar reserva:", err)
    } finally {
      setCancelandoId(null)
    }
  }

  return (
    <DashboardLayout>
      <div className="max-w-4xl mx-auto">
        <div className="mb-8">
          <h1 className="text-2xl font-semibold text-gray-900">Cancelar Reserva</h1>
          <p className="text-gray-600 mt-1">Busque y cancele reservas existentes</p>
        </div>

        <Card className="border-gray-200 mb-6">
          <CardContent className="p-6">
            <div className="flex gap-4 items-center">
              <div className="relative flex-1">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
                <Input
                  placeholder="Buscar por nombre o DNI del huésped..."
                  className="pl-10"
                  value={busqueda}
                  onChange={(e) => setBusqueda(e.target.value)}
                  onKeyDown={(e) => {
                    if (e.key === "Enter") {
                      buscarReservas(busqueda)
                    }
                  }}
                />
              </div>
              <Button
                className="bg-blue-600 hover:bg-blue-700"
                onClick={() => buscarReservas(busqueda)}
                disabled={cargando || cancelandoId !== null}
              >
                Buscar
              </Button>
            </div>
          </CardContent>
        </Card>

        <Card className="border-gray-200">
          <CardHeader>
            <CardTitle className="text-lg flex items-center gap-2">
              <CalendarX className="w-5 h-5 text-red-600" />
              Reservas Activas
            </CardTitle>
            <CardDescription>{reservasFiltradas.length} reserva(s) encontrada(s)</CardDescription>
          </CardHeader>
          <CardContent className="p-0">
            {cargando ? (
              <div className="p-8 text-center text-gray-600">Cargando reservas...</div>
            ) : error ? (
              <div className="p-8 text-center text-red-600">Error: {error}</div>
            ) : !busqueda.trim() ? (
              <div className="p-8 text-center text-gray-600">Ingresá un nombre o DNI y presioná Buscar</div>
            ) : !busquedaCoincide ? (
              <div className="p-8 text-center text-gray-600">
                Terminá de escribir y presioná Buscar para ver resultados
              </div>
            ) : reservasFiltradas.length === 0 ? (
              <div className="p-8 text-center text-gray-600">No se encontraron reservas</div>
            ) : (
              <Table>
                <TableHeader>
                  <TableRow className="bg-gray-50">
                    <TableHead className="font-medium text-gray-700">ID</TableHead>
                    <TableHead className="font-medium text-gray-700">Huésped</TableHead>
                    <TableHead className="font-medium text-gray-700">Habitación</TableHead>
                    <TableHead className="font-medium text-gray-700">Entrada</TableHead>
                    <TableHead className="font-medium text-gray-700">Salida</TableHead>
                    <TableHead className="font-medium text-gray-700">Acción</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {reservasFiltradas.map((reserva) => (
                    <TableRow key={reserva.id} className="hover:bg-gray-50">
                      <TableCell className="font-mono">{reserva.id}</TableCell>
                      <TableCell className="font-medium">{reserva.huesped}</TableCell>
                      <TableCell>{reserva.habitacion}</TableCell>
                      <TableCell>{reserva.entrada}</TableCell>
                      <TableCell>{reserva.salida}</TableCell>
                      <TableCell>
                        <Button
                          variant="destructive"
                          size="sm"
                          onClick={() => cancelarReserva(reserva.id)}
                          disabled={cancelandoId !== null}
                        >
                          {cancelandoId === reserva.id ? "Cancelando..." : "Cancelar"}
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            )}
          </CardContent>
        </Card>
      </div>
    </DashboardLayout>
  )
}
