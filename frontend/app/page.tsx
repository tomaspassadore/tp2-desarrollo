"use client"

import { useMemo, useState } from "react"
import { Search } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { DashboardLayout } from "@/components/dashboard-layout"
import { buscarHuesped, type Huesped } from "@/lib/api/huespedes"

type TipoBusqueda = "dni" | "nombre" | "apellido"

export default function BuscarHuesped() {
  const [busqueda, setBusqueda] = useState("")
  const [tipo, setTipo] = useState<TipoBusqueda>("nombre")

  const [resultados, setResultados] = useState<Huesped[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [yaBuscado, setYaBuscado] = useState(false)

  async function onBuscar() {
    const query = busqueda.trim()
    if (!query) return

    setLoading(true)
    setError(null)
    setYaBuscado(true)

    try {
      const data = await buscarHuesped({
        criterio: tipo,
        valor: query,
      })
      setResultados(data)
    } catch (e) {
      setResultados([])
      setError(e instanceof Error ? e.message : "Ocurrió un error")
    } finally {
      setLoading(false)
    }
  }

  return (
    <DashboardLayout>
      <div className="max-w-7xl mx-auto">
        <div className="mb-8">
          <h1 className="text-2xl font-semibold text-gray-900">Buscar Huésped</h1>
          <p className="text-gray-600 mt-1">Busque huéspedes por nombre, apellido o DNI</p>
        </div>

        <Card className="border-gray-200 mb-6">
          <CardContent className="p-6">
            <form
              className="flex gap-4"
              onSubmit={(e) => {
                e.preventDefault()
                onBuscar()
              }}
            >
              <select
                className="h-10 rounded-md border border-input bg-background px-3 text-sm"
                value={tipo}
                onChange={(e) => setTipo(e.target.value as TipoBusqueda)}
                aria-label="Tipo de búsqueda"
              >
                <option value="nombre">Nombre</option>
                <option value="apellido">Apellido</option>
                <option value="dni">DNI</option>
              </select>
              <div className="relative flex-1">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
                <Input
                  placeholder="Buscar..."
                  className="pl-10"
                  value={busqueda}
                  onChange={(e) => setBusqueda(e.target.value)}
                />
              </div>


              <Button className="bg-blue-600 hover:bg-blue-700" type="submit" disabled={loading || !busqueda.trim()}>
                {loading ? "Buscando..." : "Buscar"}
              </Button>
            </form>

            {error && <div className="mt-3 text-sm text-red-600">{error}</div>}
          </CardContent>
        </Card>

        <Card className="border-gray-200">
          <CardHeader>
            <CardTitle className="text-lg">Resultados</CardTitle>
            <CardDescription>
              {yaBuscado ? `${resultados.length} huésped(es) encontrado(s)` : "Aún no se realizó ninguna búsqueda"}
            </CardDescription>
          </CardHeader>

          <CardContent className="p-0 overflow-x-auto">
            <Table>
              <TableHeader>
                <TableRow className="bg-gray-50">
                  <TableHead className="font-medium text-gray-700">Nombre</TableHead>
                  <TableHead className="font-medium text-gray-700">Apellido</TableHead>
                  <TableHead className="font-medium text-gray-700">DNI</TableHead>
                  <TableHead className="font-medium text-gray-700">CUIT</TableHead>
                  <TableHead className="font-medium text-gray-700">Email</TableHead>
                  <TableHead className="font-medium text-gray-700">Fecha de nacimiento</TableHead>
                  <TableHead className="font-medium text-gray-700">Teléfono</TableHead>
                  <TableHead className="font-medium text-gray-700">Nacionalidad</TableHead>
                  <TableHead className="font-medium text-gray-700">Ocupación</TableHead>
                </TableRow>
              </TableHeader>

              <TableBody>
                {resultados.map((huesped) => {
                  // Formatear fecha de nacimiento
                  const formatearFecha = (fecha: string | undefined) => {
                    if (!fecha) return "-"
                    try {
                      const date = new Date(fecha)
                      return date.toLocaleDateString("es-AR", {
                        year: "numeric",
                        month: "2-digit",
                        day: "2-digit",
                      })
                    } catch {
                      return fecha
                    }
                  }

                  return (
                    <TableRow key={huesped.id || Math.random()} className="hover:bg-gray-50">
                      <TableCell className="font-medium">{huesped.nombre || "-"}</TableCell>
                      <TableCell>{huesped.apellido || "-"}</TableCell>
                      <TableCell>{huesped.nroDocumento || huesped.dni || "-"}</TableCell>
                      <TableCell>{huesped.cuit || "-"}</TableCell>
                      <TableCell>{huesped.email || "-"}</TableCell>
                      <TableCell>{formatearFecha(huesped.fechaDeNacimiento || huesped.fechaNacimiento)}</TableCell>
                      <TableCell>{huesped.telefono || "-"}</TableCell>
                      <TableCell>{huesped.nacionalidad || "-"}</TableCell>
                      <TableCell>{huesped.ocupacion || "-"}</TableCell>
                    </TableRow>
                  )
                })}

                {yaBuscado && !loading && resultados.length === 0 && (
                  <TableRow>
                    <TableCell colSpan={9} className="text-center py-8 text-gray-500">
                      No se encontraron huéspedes
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </CardContent>
        </Card>
      </div>
    </DashboardLayout>
  )
}
