"use client"

import { useState, useEffect } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { DashboardLayout } from "@/components/dashboard-layout"
import { obtenerTodasHabitaciones, Habitacion } from "@/lib/api/habitaciones"

type HabitacionDisplay = {
  numero: string
  tipo: string
  estado: string
  huesped: string | null
}

const getEstadoColor = (estado: string) => {
  switch (estado.toLowerCase()) {
    case "libre":
      return "bg-green-100 text-green-700"
    case "ocupada":
      return "bg-red-100 text-red-700"
    case "reservada":
      return "bg-yellow-100 text-yellow-700"
    case "mantenimiento":
    case "en_mantenimiento":
      return "bg-gray-100 text-gray-700"
    default:
      return "bg-gray-100 text-gray-700"
  }
}

const convertirEstado = (estado: string): string => {
  switch (estado) {
    case "LIBRE":
      return "libre"
    case "OCUPADA":
      return "ocupada"
    case "RESERVADA":
      return "reservada"
    case "EN_MANTENIMIENTO":
      return "mantenimiento"
    default:
      return estado.toLowerCase()
  }
}

export default function EstadoHabitaciones() {
  const [habitaciones, setHabitaciones] = useState<HabitacionDisplay[]>([])
  const [filtroEstado, setFiltroEstado] = useState<string | null>(null)
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const cargarHabitaciones = async () => {
      try {
        setCargando(true)
        setError(null)
        const datos = await obtenerTodasHabitaciones()

        // Convertir los datos del backend al formato que necesita el componente
        const habitacionesMapeadas: HabitacionDisplay[] = datos.map((h: Habitacion) => ({
          numero: h.numero,
          tipo: h.tipoHabitacion.nombre,
          estado: convertirEstado(h.estado),
          huesped: null, // Por ahora no tenemos esta información directamente
        }))

        setHabitaciones(habitacionesMapeadas)
      } catch (err) {
        setError(err instanceof Error ? err.message : "Error al cargar las habitaciones")
        console.error("Error al cargar habitaciones:", err)
      } finally {
        setCargando(false)
      }
    }

    cargarHabitaciones()
  }, [])

  const libres = habitaciones.filter((h) => h.estado === "libre").length
  const ocupadas = habitaciones.filter((h) => h.estado === "ocupada").length
  const reservadas = habitaciones.filter((h) => h.estado === "reservada").length
  const mantenimiento = habitaciones.filter((h) => h.estado === "mantenimiento").length

  const habitacionesFiltradas = filtroEstado
    ? habitaciones.filter((h) => h.estado === filtroEstado)
    : habitaciones

  const toggleFiltro = (estado: string) => {
    setFiltroEstado((prev) => (prev === estado ? null : estado))
  }

  const cardClase = (estado: string, colorTexto: string) =>
    `border-gray-200 cursor-pointer transition-shadow ${filtroEstado === estado ? "ring-2 ring-blue-500 shadow-md" : ""} ${colorTexto}`

  if (cargando) {
    return (
      <DashboardLayout>
        <div className="max-w-5xl mx-auto">
          <div className="mb-8">
            <h1 className="text-2xl font-semibold text-gray-900">Estado de Habitaciones</h1>
            <p className="text-gray-600 mt-1">Vista general del estado de todas las habitaciones</p>
          </div>
          <Card className="border-gray-200">
            <CardContent className="p-8 text-center">
              <div className="text-gray-600">Cargando habitaciones...</div>
            </CardContent>
          </Card>
        </div>
      </DashboardLayout>
    )
  }

  if (error) {
    return (
      <DashboardLayout>
        <div className="max-w-5xl mx-auto">
          <div className="mb-8">
            <h1 className="text-2xl font-semibold text-gray-900">Estado de Habitaciones</h1>
            <p className="text-gray-600 mt-1">Vista general del estado de todas las habitaciones</p>
          </div>
          <Card className="border-gray-200">
            <CardContent className="p-8 text-center">
              <div className="text-red-600">Error: {error}</div>
            </CardContent>
          </Card>
        </div>
      </DashboardLayout>
    )
  }

  return (
    <DashboardLayout>
      <div className="max-w-5xl mx-auto">
        <div className="mb-8">
          <h1 className="text-2xl font-semibold text-gray-900">Estado de Habitaciones</h1>
          <p className="text-gray-600 mt-1">Vista general del estado de todas las habitaciones</p>
        </div>

        {/* Resumen */}
        <div className="grid grid-cols-4 gap-4 mb-8">
          <Card className={cardClase("libre", "")} onClick={() => toggleFiltro("libre")}>
            <CardContent className="p-4 text-center select-none">
              <div className="text-2xl font-bold text-green-600">{libres}</div>
              <div className="text-sm text-gray-600">Libres</div>
            </CardContent>
          </Card>
          <Card className={cardClase("ocupada", "")} onClick={() => toggleFiltro("ocupada")}>
            <CardContent className="p-4 text-center select-none">
              <div className="text-2xl font-bold text-red-600">{ocupadas}</div>
              <div className="text-sm text-gray-600">Ocupadas</div>
            </CardContent>
          </Card>
          <Card className={cardClase("reservada", "")} onClick={() => toggleFiltro("reservada")}>
            <CardContent className="p-4 text-center select-none">
              <div className="text-2xl font-bold text-yellow-600">{reservadas}</div>
              <div className="text-sm text-gray-600">Reservadas</div>
            </CardContent>
          </Card>
          <Card className={cardClase("mantenimiento", "")} onClick={() => toggleFiltro("mantenimiento")}>
            <CardContent className="p-4 text-center select-none">
              <div className="text-2xl font-bold text-gray-600">{mantenimiento}</div>
              <div className="text-sm text-gray-600">Mantenimiento</div>
            </CardContent>
          </Card>
        </div>

        {/* Grid de habitaciones */}
        <Card className="border-gray-200">
          <CardHeader>
            <CardTitle className="text-lg">Todas las Habitaciones</CardTitle>
          </CardHeader>
          <CardContent>
            {habitacionesFiltradas.length === 0 ? (
              <div className="text-center py-8 text-gray-600">No hay habitaciones disponibles</div>
            ) : (
              <div className="grid grid-cols-5 gap-4">
                {habitacionesFiltradas.map((habitacion) => (
                  <Card
                    key={habitacion.numero}
                    className="border-gray-200 hover:shadow-md transition-shadow cursor-pointer"
                  >
                    <CardContent className="p-4">
                      <div className="text-lg font-bold text-gray-900 mb-1">{habitacion.numero}</div>
                      <div className="text-xs text-gray-500 mb-2">{habitacion.tipo}</div>
                      <Badge className={getEstadoColor(habitacion.estado)}>{habitacion.estado}</Badge>
                      {habitacion.huesped && (
                        <div className="text-xs text-gray-600 mt-2 truncate">{habitacion.huesped}</div>
                      )}
                    </CardContent>
                  </Card>
                ))}
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </DashboardLayout>
  )
}
