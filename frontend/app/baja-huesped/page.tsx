"use client"

import { useState } from "react"
import { UserMinus, Search, AlertTriangle } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { DashboardLayout } from "@/components/dashboard-layout"
import { buscarHuesped, eliminarHuesped, Huesped } from "@/lib/api/huespedes"
import { toast } from "@/components/ui/use-toast"

export default function BajaHuesped() {
  const [dni, setDni] = useState("")
  const [huespedEncontrado, setHuespedEncontrado] = useState(false)
  const [huesped, setHuesped] = useState<Huesped | null>(null)
  const [cargando, setCargando] = useState(false)
  const [eliminando, setEliminando] = useState(false)

  const handleBuscar = async () => {
    const dniTrim = dni.trim()
    if (!dniTrim) {
      toast({
        title: "DNI requerido",
        description: "Ingresá el DNI del huésped para buscarlo.",
        variant: "destructive",
      })
      return
    }

    try {
      setCargando(true)
      const encontrados = await buscarHuesped({ criterio: "dni", valor: dniTrim })

      if (encontrados.length === 0) {
        setHuesped(null)
        setHuespedEncontrado(false)
        toast({
          title: "Sin resultados",
          description: "No se encontró ningún huésped con ese DNI.",
          variant: "destructive",
        })
        return
      }

      const h = encontrados[0]
      setHuesped(h)
      setHuespedEncontrado(true)
    } catch (err) {
      console.error("Error al buscar huésped:", err)
      toast({
        title: "Error",
        description: err instanceof Error ? err.message : "Error al buscar el huésped",
        variant: "destructive",
      })
      setHuesped(null)
      setHuespedEncontrado(false)
    } finally {
      setCargando(false)
    }
  }

  const handleEliminar = async () => {
    if (!huesped?.id) {
      toast({
        title: "Huésped no cargado",
        description: "Buscá un huésped por DNI antes de eliminar.",
        variant: "destructive",
      })
      return
    }

    try {
      setEliminando(true)
      await eliminarHuesped(huesped.id)
      toast({
        title: "Huésped eliminado",
        description: `Se dio de baja al huésped con DNI ${huesped.nroDocumento}.`,
      })
      setHuesped(null)
      setHuespedEncontrado(false)
      setDni("")
    } catch (err) {
      console.error("Error al eliminar huésped:", err)
      toast({
        title: "No se pudo eliminar",
        description: err instanceof Error ? err.message : "Error al eliminar al huésped",
        variant: "destructive",
      })
    } finally {
      setEliminando(false)
    }
  }

  return (
    <DashboardLayout>
      <div className="max-w-2xl mx-auto">
        <div className="mb-8">
          <h1 className="text-2xl font-semibold text-gray-900">Dar de Baja Huésped</h1>
          <p className="text-gray-600 mt-1">Elimine un huésped del sistema</p>
        </div>

        <Card className="border-gray-200 mb-6">
          <CardContent className="p-6">
            <div className="flex gap-4 items-end">
              <div className="flex-1 space-y-2">
                <Label htmlFor="buscarDni">DNI del Huésped</Label>
                <div className="relative">
                  <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
                  <Input
                    id="buscarDni"
                    placeholder="Ingrese el DNI del huésped..."
                    className="pl-10"
                    value={dni}
                    onChange={(e) => setDni(e.target.value)}
                  />
                </div>
              </div>
              <Button className="bg-blue-600 hover:bg-blue-700" onClick={handleBuscar} disabled={cargando || eliminando}>
                {cargando ? "Buscando..." : "Buscar"}
              </Button>
            </div>
          </CardContent>
        </Card>

        {huespedEncontrado && huesped && (
          <Card className="border-gray-200">
            <CardHeader>
              <CardTitle className="text-lg flex items-center gap-2">
                <UserMinus className="w-5 h-5 text-red-600" />
                Confirmar Baja
              </CardTitle>
              <CardDescription>Revise los datos antes de confirmar</CardDescription>
            </CardHeader>
            <CardContent className="space-y-6">
              <div className="bg-gray-50 rounded-lg p-4 space-y-3">
                <div className="flex justify-between">
                  <span className="text-gray-600">Nombre:</span>
                  <span className="font-medium">{huesped.nombre ?? "-"}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">DNI:</span>
                  <span className="font-medium">{huesped.nroDocumento ?? "-"}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">Teléfono:</span>
                  <span className="font-medium">{huesped.telefono ?? "-"}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">Email:</span>
                  <span className="font-medium">{huesped.email ?? "-"}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">Nacionalidad:</span>
                  <span className="font-medium">{huesped.nacionalidad ?? "-"}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">Ocupación:</span>
                  <span className="font-medium">{huesped.ocupacion ?? "-"}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">Dirección:</span>
                  <span className="font-medium">
                    {[
                      huesped.direccion?.calle,
                      huesped.direccion?.numero,
                      huesped.direccion?.departamento,
                      huesped.direccion?.piso,
                      huesped.direccion?.localidad,
                      huesped.direccion?.provincia,
                      huesped.direccion?.pais,
                    ]
                      .filter(Boolean)
                      .join(" ") || "-"}
                  </span>
                </div>
              </div>

              <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4 flex gap-3">
                <AlertTriangle className="w-5 h-5 text-yellow-600 shrink-0 mt-0.5" />
                <div>
                  <p className="font-medium text-yellow-800">Atención</p>
                  <p className="text-sm text-yellow-700">
                    Esta acción eliminará permanentemente al huésped del sistema. Esta operación no se puede deshacer.
                  </p>
                </div>
              </div>

              <div className="flex gap-4">
                <Button variant="outline" className="flex-1 bg-transparent" onClick={() => setHuespedEncontrado(false)}>
                  Cancelar
                </Button>
                <Button variant="destructive" className="flex-1" onClick={handleEliminar} disabled={eliminando}>
                  {eliminando ? "Eliminando..." : "Confirmar Baja"}
                </Button>
              </div>
            </CardContent>
          </Card>
        )}
      </div>
    </DashboardLayout>
  )
}
