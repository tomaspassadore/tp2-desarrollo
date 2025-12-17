"use client"

import type React from "react"

import { useState, useMemo } from "react"
import { CalendarPlus } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { DashboardLayout } from "@/components/dashboard-layout"
import { crearReserva } from "@/lib/api/reserva"

export default function ReservarHabitacion() {
  const [formData, setFormData] = useState({
    huesped: "",
    habitacion: "",
    fechaEntrada: "",
    fechaSalida: "",
    numPersonas: "",
  })

  const [isSubmitting, setIsSubmitting] = useState(false)

  // Obtener la fecha actual en formato YYYY-MM-DD
  const today = useMemo(() => {
    const date = new Date()
    return date.toISOString().split("T")[0]
  }, [])

  // Calcular la fecha mínima para fecha de salida (día siguiente a fecha de entrada)
  const minFechaSalida = useMemo(() => {
    if (!formData.fechaEntrada) return today

    const fechaEntrada = new Date(formData.fechaEntrada + "T00:00:00")
    fechaEntrada.setDate(fechaEntrada.getDate() + 1)
    return fechaEntrada.toISOString().split("T")[0]
  }, [formData.fechaEntrada, today])

  const handleSubmit: React.MouseEventHandler<HTMLButtonElement> = async (e) => {
    e.preventDefault()

    if (!formData.habitacion || !formData.fechaEntrada || !formData.fechaSalida || !formData.huesped) {
      window.alert("Complete habitación, fecha de entrada, fecha de salida y DNI del huésped")
      return
    }

    try {
      setIsSubmitting(true)

      const payload = {
        fechaIngreso: formData.fechaEntrada,
        fechaEgreso: formData.fechaSalida,
        habitacion: {
          numero: formData.habitacion,
        },
        responsable: {
          nroDocumento: formData.huesped,
        },
      }

      await crearReserva(payload)

      window.alert("Reserva creada correctamente")

      // Opcional: limpiar formulario
      setFormData({
        huesped: "",
        habitacion: "",
        fechaEntrada: "",
        fechaSalida: "",
        numPersonas: "",
      })
    } catch (error: any) {
      const message = error?.message ?? "Ocurrió un error al crear la reserva"
      window.alert(message)
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <DashboardLayout>
      <div className="max-w-2xl mx-auto">
        <div className="mb-8">
          <h1 className="text-2xl font-semibold text-gray-900">Reservar Habitación</h1>
          <p className="text-gray-600 mt-1">Complete los datos para realizar una nueva reserva</p>
        </div>

        <Card className="border-gray-200">
          <CardHeader>
            <CardTitle className="text-lg flex items-center gap-2">
              <CalendarPlus className="w-5 h-5 text-blue-600" />
              Nueva Reserva
            </CardTitle>
            <CardDescription>Ingrese los datos de la reserva</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-6">
              <div className="space-y-2">
                <Label htmlFor="huesped">DNI del Huésped</Label>
                <Input
                  id="huesped"
                  placeholder="Ingrese el DNI del huésped"
                  value={formData.huesped}
                  onChange={(e) => setFormData({ ...formData, huesped: e.target.value })}
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="habitacion">Habitación</Label>
                <Select
                  value={formData.habitacion}
                  onValueChange={(value) => setFormData({ ...formData, habitacion: value })}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Seleccione una habitación" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="101">101 - Individual</SelectItem>
                    <SelectItem value="102">102 - Individual</SelectItem>
                    <SelectItem value="201">201 - Doble</SelectItem>
                    <SelectItem value="202">202 - Doble</SelectItem>
                    <SelectItem value="301">301 - Suite</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="fechaEntrada">Fecha de Entrada</Label>
                  <Input
                    id="fechaEntrada"
                    type="date"
                    min={today}
                    value={formData.fechaEntrada}
                    onChange={(e) => setFormData({ ...formData, fechaEntrada: e.target.value })}
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="fechaSalida">Fecha de Salida</Label>
                  <Input
                    id="fechaSalida"
                    type="date"
                    min={minFechaSalida}
                    value={formData.fechaSalida}
                    onChange={(e) => setFormData({ ...formData, fechaSalida: e.target.value })}
                  />
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="numPersonas">Número de Personas</Label>
                <Input
                  id="numPersonas"
                  type="number"
                  min="1"
                  max="4"
                  placeholder="1"
                  value={formData.numPersonas}
                  onChange={(e) => setFormData({ ...formData, numPersonas: e.target.value })}
                />
              </div>

              <Button
                onClick={handleSubmit}
                className="w-full bg-blue-600 hover:bg-blue-700"
                disabled={isSubmitting}
              >
                {isSubmitting ? "Creando reserva..." : "Confirmar Reserva"}
              </Button>
            </div>
          </CardContent>
        </Card>
      </div>
    </DashboardLayout>
  )
}
