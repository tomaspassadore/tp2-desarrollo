"use client"

import type React from "react"
import { useState } from "react"
import { UserPlus } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { DashboardLayout } from "@/components/dashboard-layout"
import { darAltaHuesped, Huesped } from "@/lib/api/huespedes"

export default function AltaHuesped() {
  const [formData, setFormData] = useState<Huesped>({
    nombre: "",
    apellido: "",
    cuit: "",
    nroDocumento: "",
    fechaDeNacimiento: "",
    nacionalidad: "",
    email: "",
    telefono: "",
    ocupacion: "",
    direccion: {
      calle: "",
      numero: "",
      departamento: "",
      piso: "",
      codigoPostal: "",
      localidad: "",
      provincia: "",
      pais: "",
    },
  })

  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [okMsg, setOkMsg] = useState<string | null>(null)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)
    setError(null)
    setOkMsg(null)

    try {
      await darAltaHuesped(formData)
      setOkMsg("Huésped registrado correctamente.")

      setFormData({
        nombre: "",
        apellido: "",
        cuit: "",
        nroDocumento: "",
        fechaDeNacimiento: "",
        nacionalidad: "",
        email: "",
        telefono: "",
        ocupacion: "",
        direccion: {
          calle: "",
          numero: "",
          departamento: "",
          piso: "",
          codigoPostal: "",
          localidad: "",
          provincia: "",
          pais: "",
        },
      })
    } catch (e) {
      setError(e instanceof Error ? e.message : "Error desconocido al registrar huésped")
    } finally {
      setLoading(false)
    }
  }

  return (
    <DashboardLayout>
      <div className="max-w-2xl mx-auto">
        <div className="mb-8">
          <h1 className="text-2xl font-semibold text-gray-900">Dar de Alta Huésped</h1>
          <p className="text-gray-600 mt-1">Registre un nuevo huésped en el sistema</p>
        </div>

        <Card className="border-gray-200">
          <CardHeader>
            <CardTitle className="text-lg flex items-center gap-2">
              <UserPlus className="w-5 h-5 text-green-600" />
              Nuevo Huésped
            </CardTitle>
            <CardDescription>Complete todos los campos requeridos</CardDescription>
          </CardHeader>

          <CardContent>
            <form onSubmit={handleSubmit} className="space-y-6">
              {/* Datos personales */}
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="nombre">Nombre</Label>
                  <Input
                    id="nombre"
                    value={formData.nombre}
                    onChange={(e) => setFormData({ ...formData, nombre: e.target.value })}
                    required
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="apellido">Apellido</Label>
                  <Input
                    id="apellido"
                    value={formData.apellido}
                    onChange={(e) => setFormData({ ...formData, apellido: e.target.value })}
                    required
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
                    required
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="nroDocumento">Número de documento</Label>
                  <Input
                    id="nroDocumento"
                    value={formData.nroDocumento}
                    onChange={(e) => setFormData({ ...formData, nroDocumento: e.target.value })}
                    required
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="fechaDeNacimiento">Fecha de nacimiento</Label>
                  <Input
                    id="fechaDeNacimiento"
                    type="date"
                    value={formData.fechaDeNacimiento}
                    onChange={(e) => setFormData({ ...formData, fechaDeNacimiento: e.target.value })}
                    required
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="nacionalidad">Nacionalidad</Label>
                  <Input
                    id="nacionalidad"
                    value={formData.nacionalidad}
                    onChange={(e) => setFormData({ ...formData, nacionalidad: e.target.value })}
                    required
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="email">Email</Label>
                  <Input
                    id="email"
                    type="email"
                    value={formData.email}
                    onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                    required
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="telefono">Teléfono</Label>
                  <Input
                    id="telefono"
                    value={formData.telefono}
                    onChange={(e) => setFormData({ ...formData, telefono: e.target.value })}
                    required
                  />
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="ocupacion">Ocupación</Label>
                <Input
                  id="ocupacion"
                  value={formData.ocupacion}
                  onChange={(e) => setFormData({ ...formData, ocupacion: e.target.value })}
                  required
                />
              </div>

              {/* Dirección */}
              <div className="pt-2">
                <h3 className="text-md font-semibold text-gray-900 mb-5">Dirección</h3>

                <div className="grid grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label htmlFor="calle">Calle</Label>
                    <Input
                      id="calle"
                      value={formData.direccion!.calle}
                      onChange={(e) =>
                        setFormData({ ...formData, direccion: { ...formData.direccion!, calle: e.target.value } })
                      }
                      required
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="numero">Número</Label>
                    <Input
                      id="numero"
                      value={formData.direccion!.numero}
                      onChange={(e) =>
                        setFormData({ ...formData, direccion: { ...formData.direccion!, numero: e.target.value } })
                      }
                      required
                    />
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-4 mt-4">
                  <div className="space-y-2">
                    <Label htmlFor="piso">Piso</Label>
                    <Input
                      id="piso"
                      value={formData.direccion!.piso}
                      onChange={(e) =>
                        setFormData({ ...formData, direccion: { ...formData.direccion!, piso: e.target.value } })
                      }
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="departamento">Departamento</Label>
                    <Input
                      id="departamento"
                      value={formData.direccion!.departamento}
                      onChange={(e) =>
                        setFormData({
                          ...formData,
                          direccion: { ...formData.direccion!, departamento: e.target.value },
                        })
                      }
                    />
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-4 mt-4">
                  <div className="space-y-2">
                    <Label htmlFor="codigoPostal">Código Postal</Label>
                    <Input
                      id="codigoPostal"
                      value={formData.direccion!.codigoPostal}
                      onChange={(e) =>
                        setFormData({
                          ...formData,
                          direccion: { ...formData.direccion!, codigoPostal: e.target.value },
                        })
                      }
                      required
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="localidad">Localidad</Label>
                    <Input
                      id="localidad"
                      value={formData.direccion!.localidad}
                      onChange={(e) =>
                        setFormData({
                          ...formData,
                          direccion: { ...formData.direccion!, localidad: e.target.value },
                        })
                      }
                      required
                    />
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-4 mt-4">
                  <div className="space-y-2">
                    <Label htmlFor="provincia">Provincia</Label>
                    <Input
                      id="provincia"
                      value={formData.direccion!.provincia}
                      onChange={(e) =>
                        setFormData({
                          ...formData,
                          direccion: { ...formData.direccion!, provincia: e.target.value },
                        })
                      }
                      required
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="pais">País</Label>
                    <Input
                      id="pais"
                      value={formData.direccion!.pais}
                      onChange={(e) =>
                        setFormData({
                          ...formData,
                          direccion: { ...formData.direccion!, pais: e.target.value },
                        })
                      }
                      required
                    />
                  </div>
                </div>
              </div>

              {error && <div className="text-sm text-red-600">{error}</div>}
              {okMsg && <div className="text-sm text-green-700">{okMsg}</div>}

              <Button type="submit" className="w-full bg-green-600 hover:bg-green-700" disabled={loading}>
                {loading ? "Registrando..." : "Registrar Huésped"}
              </Button>
            </form>
          </CardContent>
        </Card>
      </div>
    </DashboardLayout>
  )
}
