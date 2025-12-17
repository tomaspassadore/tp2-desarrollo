"use client"

import type React from "react"

import { useState } from "react"
import { UserCog, Search } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { DashboardLayout } from "@/components/dashboard-layout"
import { actualizarHuesped, buscarHuesped, Huesped } from "@/lib/api/huespedes"
import { toast } from "@/components/ui/use-toast"

export default function ModificarHuesped() {
  const [dni, setDni] = useState("")
  const [huespedEncontrado, setHuespedEncontrado] = useState(false)
  const [huesped, setHuesped] = useState<Huesped | null>(null)
  const [cargando, setCargando] = useState(false)
  const [guardando, setGuardando] = useState(false)
  const [okMsg, setOkMsg] = useState<string | null>(null)
  const [formData, setFormData] = useState({
    nombre: "",
    apellidos: "",
    dni: "",
    telefono: "",
    email: "",
    cuit: "",
    fechaDeNacimiento: "",
    nacionalidad: "",
    ocupacion: "",
    direccion: "",
    numero: "",
    departamento: "",
    piso: "",
    ciudad: "",
    provincia: "",
    pais: "",
    codigoPostal: "",
  })

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
      setFormData({
        nombre: h.nombre ?? "",
        apellidos: h.apellido ?? "",
        dni: h.nroDocumento ?? "",
        telefono: h.telefono ?? "",
        email: h.email ?? "",
        cuit: h.cuit ?? "",
        fechaDeNacimiento: h.fechaDeNacimiento ?? "",
        nacionalidad: h.nacionalidad ?? "",
        ocupacion: h.ocupacion ?? "",
        direccion: h.direccion?.calle ?? "",
        numero: h.direccion?.numero ?? "",
        departamento: h.direccion?.departamento ?? "",
        piso: h.direccion?.piso ?? "",
        ciudad: h.direccion?.localidad ?? "",
        provincia: h.direccion?.provincia ?? "",
        pais: h.direccion?.pais ?? "",
        codigoPostal: h.direccion?.codigoPostal ?? "",
      })
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

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
      ; (async () => {
        if (!huesped?.id) {
          toast({
            title: "Huésped no cargado",
            description: "Primero buscá un huésped por DNI.",
            variant: "destructive",
          })
          return
        }

        try {
          setGuardando(true)
          setOkMsg(null)

          const payload: Huesped = {
            ...huesped,
            nombre: formData.nombre,
            apellido: formData.apellidos,
            nroDocumento: formData.dni,
            telefono: formData.telefono,
            email: formData.email || undefined,
            cuit: formData.cuit || undefined,
            fechaDeNacimiento: formData.fechaDeNacimiento,
            nacionalidad: formData.nacionalidad,
            ocupacion: formData.ocupacion,
            direccion: {
              ...huesped.direccion,
              calle: formData.direccion,
              numero: formData.numero,
              departamento: formData.departamento,
              piso: formData.piso,
              localidad: formData.ciudad,
              provincia: formData.provincia,
              pais: formData.pais,
              codigoPostal: formData.codigoPostal,
            },
          }

          const actualizado = await actualizarHuesped(huesped.id, payload)
          setHuesped(actualizado)
          setOkMsg("Huésped modificado correctamente.")
        } catch (err) {
          console.error("Error al actualizar huésped:", err)
          toast({
            title: "No se pudo guardar",
            description: err instanceof Error ? err.message : "Error al guardar cambios",
            variant: "destructive",
          })
        } finally {
          setGuardando(false)
        }
      })()
  }

  return (
    <DashboardLayout>
      <div className="max-w-2xl mx-auto">
        <div className="mb-8">
          <h1 className="text-2xl font-semibold text-gray-900">Modificar Huésped</h1>
          <p className="text-gray-600 mt-1">Busque y modifique los datos de un huésped</p>
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
              <Button className="bg-blue-600 hover:bg-blue-700" onClick={handleBuscar}>
                {cargando ? "Buscando..." : "Buscar"}
              </Button>
            </div>
          </CardContent>
        </Card>

        {huespedEncontrado && (
          <Card className="border-gray-200">
            <CardHeader>
              <CardTitle className="text-lg flex items-center gap-2">
                <UserCog className="w-5 h-5 text-blue-600" />
                Editar Datos
              </CardTitle>
              <CardDescription>Modifique los campos necesarios</CardDescription>
            </CardHeader>
            <CardContent>
              <form onSubmit={handleSubmit} className="space-y-6">
                <div className="grid grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label htmlFor="nombre">Nombre</Label>
                    <Input
                      id="nombre"
                      value={formData.nombre}
                      onChange={(e) => setFormData({ ...formData, nombre: e.target.value })}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="apellidos">Apellidos</Label>
                    <Input
                      id="apellidos"
                      value={formData.apellidos}
                      onChange={(e) => setFormData({ ...formData, apellidos: e.target.value })}
                    />
                  </div>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="dniField">DNI / Pasaporte</Label>
                  <Input
                    id="dniField"
                    value={formData.dni}
                    onChange={(e) => setFormData({ ...formData, dni: e.target.value })}
                  />
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label htmlFor="telefono">Teléfono</Label>
                    <Input
                      id="telefono"
                      value={formData.telefono}
                      onChange={(e) => setFormData({ ...formData, telefono: e.target.value })}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="email">Email</Label>
                    <Input
                      id="email"
                      type="email"
                      value={formData.email}
                      onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                    />
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label htmlFor="cuit">CUIT</Label>
                    <Input
                      id="cuit"
                      value={formData.cuit}
                      onChange={(e) => setFormData({ ...formData, cuit: e.target.value })}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="fechaDeNacimiento">Fecha de Nacimiento</Label>
                    <Input
                      id="fechaDeNacimiento"
                      type="date"
                      value={formData.fechaDeNacimiento}
                      onChange={(e) => setFormData({ ...formData, fechaDeNacimiento: e.target.value })}
                    />
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label htmlFor="nacionalidad">Nacionalidad</Label>
                    <Input
                      id="nacionalidad"
                      value={formData.nacionalidad}
                      onChange={(e) => setFormData({ ...formData, nacionalidad: e.target.value })}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="ocupacion">Ocupación</Label>
                    <Input
                      id="ocupacion"
                      value={formData.ocupacion}
                      onChange={(e) => setFormData({ ...formData, ocupacion: e.target.value })}
                    />
                  </div>
                </div>

                <div className="space-y-4 border-t pt-4">
                  <h3 className="text-sm font-semibold text-gray-700">Dirección</h3>

                  <div className="space-y-2">
                    <Label htmlFor="direccion">Calle</Label>
                    <Input
                      id="direccion"
                      value={formData.direccion}
                      onChange={(e) => setFormData({ ...formData, direccion: e.target.value })}
                    />
                  </div>

                  <div className="grid grid-cols-2 gap-4">
                    <div className="space-y-2">
                      <Label htmlFor="numero">Número</Label>
                      <Input
                        id="numero"
                        value={formData.numero}
                        onChange={(e) => setFormData({ ...formData, numero: e.target.value })}
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="departamento">Departamento</Label>
                      <Input
                        id="departamento"
                        value={formData.departamento}
                        onChange={(e) => setFormData({ ...formData, departamento: e.target.value })}
                      />
                    </div>
                  </div>

                  <div className="grid grid-cols-2 gap-4">
                    <div className="space-y-2">
                      <Label htmlFor="piso">Piso</Label>
                      <Input
                        id="piso"
                        value={formData.piso}
                        onChange={(e) => setFormData({ ...formData, piso: e.target.value })}
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="ciudad">Localidad</Label>
                      <Input
                        id="ciudad"
                        value={formData.ciudad}
                        onChange={(e) => setFormData({ ...formData, ciudad: e.target.value })}
                      />
                    </div>
                  </div>

                  <div className="grid grid-cols-2 gap-4">
                    <div className="space-y-2">
                      <Label htmlFor="provincia">Provincia</Label>
                      <Input
                        id="provincia"
                        value={formData.provincia}
                        onChange={(e) => setFormData({ ...formData, provincia: e.target.value })}
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="pais">País</Label>
                      <Input
                        id="pais"
                        value={formData.pais}
                        onChange={(e) => setFormData({ ...formData, pais: e.target.value })}
                      />
                    </div>
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="codigoPostal">Código Postal</Label>
                    <Input
                      id="codigoPostal"
                      value={formData.codigoPostal}
                      onChange={(e) => setFormData({ ...formData, codigoPostal: e.target.value })}
                    />
                  </div>
                </div>

                {okMsg && <div className="text-sm text-green-700">{okMsg}</div>}

                <Button type="submit" className="w-full bg-blue-600 hover:bg-blue-700" disabled={guardando}>
                  Guardar Cambios
                </Button>
              </form>
            </CardContent>
          </Card>
        )}
      </div>
    </DashboardLayout>
  )
}
