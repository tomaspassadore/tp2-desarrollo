"use client"

import type React from "react"

import Link from "next/link"
import { usePathname } from "next/navigation"
import { Search, CalendarPlus, LayoutGrid, CalendarX, Receipt, UserPlus, UserCog, UserMinus, Hotel } from "lucide-react"

const navigation = [
  { name: "Buscar huésped", href: "/", icon: Search },
  { name: "Reservar habitación", href: "/reservar", icon: CalendarPlus },
  { name: "Estado habitaciones", href: "/habitaciones", icon: LayoutGrid },
  { name: "Cancelar reserva", href: "/cancelar", icon: CalendarX },
  { name: "Facturar", href: "/facturar", icon: Receipt },
  { name: "Dar de alta huésped", href: "/alta-huesped", icon: UserPlus },
  { name: "Modificar huésped", href: "/modificar-huesped", icon: UserCog },
  { name: "Dar de baja huésped", href: "/baja-huesped", icon: UserMinus },
]

interface DashboardLayoutProps {
  children: React.ReactNode
}

export function DashboardLayout({ children }: DashboardLayoutProps) {
  const pathname = usePathname()

  return (
    <div className="min-h-screen bg-white pt-16">
      <header className="h-16 border-b border-gray-200 bg-white px-6 flex items-center fixed top-0 left-0 right-0 z-20">
        <div className="flex items-center gap-4">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 bg-gradient-to-br from-blue-600 to-cyan-600 rounded-lg flex items-center justify-center">
              <Hotel className="w-4 h-4 text-white" />
            </div>
            <span className="font-semibold text-gray-900">Gestión Hotelera</span>
          </div>
        </div>
      </header>

      <div className="flex">
        <aside className="w-60 border-r border-gray-200 bg-white h-[calc(100vh-4rem)] overflow-y-auto sticky top-16">
          <div className="p-4">
            <nav className="space-y-1">
              {navigation.map((item) => {
                const isActive = pathname === item.href
                return (
                  <Link
                    key={item.name}
                    href={item.href}
                    className={`flex items-center w-full justify-start px-3 py-2 rounded-md text-sm font-medium transition-colors ${
                      isActive ? "bg-blue-50 text-blue-700 hover:bg-blue-100" : "text-gray-600 hover:bg-gray-50"
                    }`}
                  >
                    <item.icon className="w-4 h-4 mr-3" />
                    {item.name}
                  </Link>
                )
              })}
            </nav>
          </div>
        </aside>

        {/* Main Content */}
        <main className="flex-1 p-8 bg-gray-50">{children}</main>
      </div>
    </div>
  )
}
