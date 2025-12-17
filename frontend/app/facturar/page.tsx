"use client"

import { useMemo, useState } from "react"
import { Receipt, Search } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog"
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
  numero?: number | string
  tipoHabitacion?: {
    nombre?: string
    costoPorNoche?: number | string
  }
}

type ApiResponsableReserva = {
  id?: number
  nombre?: string
  apellido?: string
  nroDocumento?: string
}

type ApiReserva = {
  id?: number
  fechaIngreso?: string | number
  fechaEgreso?: string | number
  habitacion?: ApiHabitacion
  pasajeros?: ApiPasajero[]
  responsable?: ApiResponsableReserva
  responsableReserva?: ApiResponsableReserva // Mantener por compatibilidad
}

type ConceptoReserva = {
  reservaId: number
  habitacionNumero: string
  tipoHabitacion: string
  noches: number
  precioUnitario: number
  total: number
  fechaIngreso: string
  fechaEgreso: string
}

function formatDate(value: unknown) {
  if (!value) return "-"
  const d = new Date(value as any)
  if (Number.isNaN(d.getTime())) return "-"
  return d.toISOString().slice(0, 10)
}

function diffNoches(desde?: string | number, hasta?: string | number) {
  if (!desde || !hasta) return 0
  const d1 = new Date(desde as any)
  const d2 = new Date(hasta as any)
  if (Number.isNaN(d1.getTime()) || Number.isNaN(d2.getTime())) return 0
  const msPorDia = 1000 * 60 * 60 * 24
  const diff = Math.round((d2.getTime() - d1.getTime()) / msPorDia)
  return Math.max(diff, 0)
}

function parsePrecio(value?: number | string) {
  if (typeof value === "number") return value
  if (typeof value === "string") {
    const parsed = parseFloat(value)
    return Number.isFinite(parsed) ? parsed : 0
  }
  return 0
}

export default function Facturar() {
  const [dni, setDni] = useState("")
  const [reservas, setReservas] = useState<ApiReserva[]>([])
  const [cargando, setCargando] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const responsable = reservas[0]?.responsable || reservas[0]?.responsableReserva

  const conceptos: ConceptoReserva[] = useMemo(() => {
    return reservas.map((reserva) => {
      const noches = diffNoches(reserva.fechaIngreso, reserva.fechaEgreso)
      const precioNoche = parsePrecio(reserva.habitacion?.tipoHabitacion?.costoPorNoche)
      const totalLinea = noches * precioNoche

      return {
        reservaId: reserva.id ?? 0,
        habitacionNumero: String(reserva.habitacion?.numero ?? "-"),
        tipoHabitacion: reserva.habitacion?.tipoHabitacion?.nombre ?? "-",
        noches,
        precioUnitario: precioNoche,
        total: totalLinea,
        fechaIngreso: formatDate(reserva.fechaIngreso),
        fechaEgreso: formatDate(reserva.fechaEgreso),
      }
    })
  }, [reservas])

  const subtotal = useMemo(() => conceptos.reduce((acc, c) => acc + c.total, 0), [conceptos])
  const iva = useMemo(() => subtotal * 0.21, [subtotal])
  const total = useMemo(() => subtotal + iva, [subtotal, iva])

  const [generando, setGenerando] = useState(false)
  const [mostrarConfirmacion, setMostrarConfirmacion] = useState(false)

  const handleGenerarFacturas = async () => {
    if (reservas.length === 0) {
      toast({
        title: "Sin reservas",
        description: "No hay reservas para facturar.",
        variant: "destructive",
      })
      return
    }

    try {
      setGenerando(true)

      // Preparar las facturas a crear (una por cada reserva)
      const facturasACrear = reservas.map((reserva) => {
        const noches = diffNoches(reserva.fechaIngreso, reserva.fechaEgreso)
        const precioNoche = parsePrecio(reserva.habitacion?.tipoHabitacion?.costoPorNoche)
        const subtotalReserva = noches * precioNoche
        const ivaReserva = subtotalReserva * 0.21
        const totalReserva = subtotalReserva + ivaReserva

        return {
          idReserva: reserva.id,
          importeTotal: totalReserva,
          fechaDeEmision: new Date().getTime(), // Timestamp en milisegundos
          tipo: "A", // Por defecto tipo A
        }
      })

      const facturasCreadas = await apiFetch<any[]>(`/facturas/crear`, {
        method: "POST",
        json: facturasACrear,
      })

      setMostrarConfirmacion(false)

      toast({
        title: "✅ Facturas generadas exitosamente",
        description: `Se generaron ${facturasCreadas?.length ?? facturasACrear.length} factura(s) correctamente. Total facturado: $${total.toFixed(2)}`,
        duration: 5000,
      })

      // Opcional: limpiar o actualizar el estado
      setReservas([])
      setDni("")
    } catch (err) {
      console.error("Error al generar facturas:", err)
      const msg = err instanceof Error ? err.message : "Error al generar las facturas"
      setMostrarConfirmacion(false)
      toast({
        title: "Error",
        description: msg,
        variant: "destructive",
      })
    } finally {
      setGenerando(false)
    }
  }

  const handleBuscar = async () => {
    const dniTrim = dni.trim()
    if (!dniTrim) {
      toast({
        title: "DNI requerido",
        description: "Ingresá el DNI del huésped para buscar su reserva.",
        variant: "destructive",
      })
      return
    }

    try {
      setCargando(true)
      setError(null)
      const data = await apiFetch<ApiReserva[]>(`/reservas/buscar-por-dni?dni=${encodeURIComponent(dniTrim)}`, {
        method: "GET",
      })
      if (!data || data.length === 0) {
        setReservas([])
        toast({
          title: "Sin resultados",
          description: "No se encontraron reservas para ese DNI.",
          variant: "destructive",
        })
        return
      }
      setReservas(data)
    } catch (err) {
      console.error("Error al buscar reservas por DNI:", err)
      const msg = err instanceof Error ? err.message : "Error al buscar reservas"
      setError(msg)
      toast({
        title: "Error",
        description: msg,
        variant: "destructive",
      })
    } finally {
      setCargando(false)
    }
  }

  return (
    <DashboardLayout>
      <div className="max-w-3xl mx-auto">
        <div className="mb-8">
          <h1 className="text-2xl font-semibold text-gray-900">Facturar</h1>
          <p className="text-gray-600 mt-1">Genere facturas para los huéspedes</p>
        </div>

        <Card className="border-gray-200 mb-6">
          <CardContent className="p-6">
            <div className="flex gap-4 items-end">
              <div className="flex-1 space-y-2">
                <Label htmlFor="dni">DNI del Huésped</Label>
                <div className="relative">
                  <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
                  <Input
                    id="dni"
                    placeholder="Ingrese el DNI del huésped..."
                    className="pl-10"
                    value={dni}
                    onChange={(e) => setDni(e.target.value)}
                  />
                </div>
              </div>
              <Button className="bg-blue-600 hover:bg-blue-700" onClick={handleBuscar} disabled={cargando}>
                {cargando ? "Buscando..." : "Buscar"}
              </Button>
            </div>
          </CardContent>
        </Card>

        {error && <div className="text-red-600 mb-4">{error}</div>}

        {reservas.length > 0 && (
          <Card className="border-gray-200">
            <CardHeader>
              <CardTitle className="text-lg flex items-center gap-2">
                <Receipt className="w-5 h-5 text-blue-600" />
                Factura
              </CardTitle>
              <CardDescription>
                Huésped: {responsable?.nombre ?? "-"} {responsable?.apellido ?? "-"} - DNI: {responsable?.nroDocumento ?? "-"}
              </CardDescription>
              <p className="text-sm text-gray-600 mt-2">
                {reservas.length} reserva{reservas.length === 1 ? "" : "s"} encontrada{reservas.length === 1 ? "" : "s"}
              </p>
            </CardHeader>
            <CardContent>
              <Table>
                <TableHeader>
                  <TableRow className="bg-gray-50">
                    <TableHead className="font-medium text-gray-700">Habitación</TableHead>
                    <TableHead className="font-medium text-gray-700">Tipo</TableHead>
                    <TableHead className="font-medium text-gray-700 text-center">Noches</TableHead>
                    <TableHead className="font-medium text-gray-700 text-right">Precio Unitario</TableHead>
                    <TableHead className="font-medium text-gray-700 text-right">Total</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {conceptos.map((concepto) => (
                    <TableRow key={concepto.reservaId}>
                      <TableCell className="font-medium">{concepto.habitacionNumero}</TableCell>
                      <TableCell>{concepto.tipoHabitacion}</TableCell>
                      <TableCell className="text-center">{concepto.noches}</TableCell>
                      <TableCell className="text-right">${concepto.precioUnitario.toFixed(2)}</TableCell>
                      <TableCell className="text-right font-medium">${concepto.total.toFixed(2)}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>

              <div className="mt-6 border-t pt-4 space-y-2">
                <div className="flex justify-between text-sm">
                  <span className="text-gray-600">Subtotal</span>
                  <span>${subtotal.toFixed(2)}</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-gray-600">IVA (21%)</span>
                  <span>${iva.toFixed(2)}</span>
                </div>
                <div className="flex justify-between font-bold text-lg pt-2 border-t">
                  <span>Total</span>
                  <span>${total.toFixed(2)}</span>
                </div>
              </div>

              <Button
                className="w-full mt-6 bg-green-600 hover:bg-green-700"
                onClick={() => setMostrarConfirmacion(true)}
                disabled={generando || reservas.length === 0}
              >
                {generando ? "Generando facturas..." : "Generar Facturas"}
              </Button>
            </CardContent>
          </Card>
        )}

        <AlertDialog open={mostrarConfirmacion} onOpenChange={setMostrarConfirmacion}>
          <AlertDialogContent>
            <AlertDialogHeader>
              <AlertDialogTitle>Confirmar generación de facturas</AlertDialogTitle>
              <AlertDialogDescription>
                ¿Está seguro que desea generar {reservas.length} factura{reservas.length === 1 ? "" : "s"}?
                <br />
                <br />
                <strong>Total a facturar: ${total.toFixed(2)}</strong>
                <br />
                <span className="text-sm text-gray-500">
                  (Subtotal: ${subtotal.toFixed(2)} + IVA 21%: ${iva.toFixed(2)})
                </span>
              </AlertDialogDescription>
            </AlertDialogHeader>
            <AlertDialogFooter>
              <AlertDialogCancel disabled={generando}>Cancelar</AlertDialogCancel>
              <AlertDialogAction
                onClick={handleGenerarFacturas}
                disabled={generando}
                className="bg-green-600 hover:bg-green-700"
              >
                {generando ? "Generando..." : "Confirmar"}
              </AlertDialogAction>
            </AlertDialogFooter>
          </AlertDialogContent>
        </AlertDialog>
      </div>
    </DashboardLayout>
  )
}
