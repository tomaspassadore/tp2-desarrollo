export const NEXT_PUBLIC_API_BASE_URL = "http://localhost:8080/api"

// Estructura general de los fetch
export class ApiError extends Error {
  status: number
  body?: unknown
  constructor(status: number, message: string, body?: unknown) {
    super(message)
    this.status = status
    this.body = body
  }
}

type Json = Record<string, any>

export async function apiFetch<T>(
  path: string,
  options: RequestInit & { json?: Json } = {}
): Promise<T> {
  const { json, headers, ...rest } = options

  const res = await fetch(`${NEXT_PUBLIC_API_BASE_URL}${path}`, {
    ...rest,
    headers: {
      "Content-Type": "application/json",
      ...(headers ?? {}),
    },
    body: json ? JSON.stringify(json) : rest.body,
  })

  const contentType = res.headers.get("content-type") || ""
  const isJson = contentType.includes("application/json")

  const data = isJson ? await res.json().catch(() => null) : await res.text().catch(() => null)

  if (!res.ok) {
    const message = (isJson && data && (data.message || data.error)) ? (data.message || data.error) : `HTTP ${res.status}`
    throw new ApiError(res.status, message, data)
  }

  return data as T
}
