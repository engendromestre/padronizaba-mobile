import { auth } from "@/lib/auth"
import { prisma } from "@/lib/prisma"
import crypto from "crypto"

export interface MobileUserPayload {
  id: string
  nome: string
  email: string
  role: "SUPER_ADMIN" | "ADMIN" | "CONSULTOR" | "CLIENTE"
  empresaId: string | null
  clienteId: string | null
  exp: number
  iat: number
}

const JWT_SECRET = process.env.AUTH_SECRET || "padronizaba-secret-key-32-chars-long!!"

function base64UrlEncode(str: string): string {
  return Buffer.from(str)
    .toString("base64")
    .replace(/=/g, "")
    .replace(/\+/g, "-")
    .replace(/\//g, "_")
}

function base64UrlDecode(str: string): string {
  let base64 = str.replace(/-/g, "+").replace(/_/g, "/")
  while (base64.length % 4) {
    base64 += "="
  }
  return Buffer.from(base64, "base64").toString("utf-8")
}

export function gerarTokenMobile(
  user: { id: string; nome: string; email: string; role: string; empresaId?: string | null; clienteId?: string | null },
  duracaoDias = 60
): string {
  const header = base64UrlEncode(JSON.stringify({ alg: "HS256", typ: "JWT" }))
  const agora = Math.floor(Date.now() / 1000)
  const exp = agora + duracaoDias * 24 * 60 * 60

  const payload = base64UrlEncode(
    JSON.stringify({
      id: user.id,
      nome: user.nome,
      email: user.email,
      role: user.role,
      empresaId: user.empresaId ?? null,
      clienteId: user.clienteId ?? null,
      iat: agora,
      exp,
    })
  )

  const signature = crypto
    .createHmac("sha256", JWT_SECRET)
    .update(`${header}.${payload}`)
    .digest("base64")
    .replace(/=/g, "")
    .replace(/\+/g, "-")
    .replace(/\//g, "_")

  return `${header}.${payload}.${signature}`
}

export function verificarTokenMobile(token: string): MobileUserPayload | null {
  try {
    const parts = token.split(".")
    if (parts.length !== 3) return null

    const [header, payload, signature] = parts

    const expectedSignature = crypto
      .createHmac("sha256", JWT_SECRET)
      .update(`${header}.${payload}`)
      .digest("base64")
      .replace(/=/g, "")
      .replace(/\+/g, "-")
      .replace(/\//g, "_")

    if (signature !== expectedSignature) {
      return null
    }

    const decoded = JSON.parse(base64UrlDecode(payload)) as MobileUserPayload
    const agora = Math.floor(Date.now() / 1000)

    if (decoded.exp && decoded.exp < agora) {
      return null
    }

    return decoded
  } catch {
    return null
  }
}

export async function obterUsuarioMobile(req: Request): Promise<MobileUserPayload | null> {
  const authHeader = req.headers.get("authorization") || req.headers.get("Authorization")
  if (!authHeader || !authHeader.startsWith("Bearer ")) {
    return null
  }

  const token = authHeader.substring(7).trim()
  const payload = verificarTokenMobile(token)
  if (!payload) return null

  // Confirma se o usuário ainda existe e está ativo no banco
  const user = await prisma.user.findUnique({
    where: { id: payload.id },
    select: { id: true, ativo: true, role: true, empresaId: true, clienteId: true, nome: true, email: true },
  })

  if (!user || !user.ativo) {
    return null
  }

  return {
    id: user.id,
    nome: user.nome,
    email: user.email,
    role: user.role as MobileUserPayload["role"],
    empresaId: user.empresaId,
    clienteId: user.clienteId,
    exp: payload.exp,
    iat: payload.iat,
  }
}

/**
 * Autentica verificando primeiro sessão Web (NextAuth cookie).
 * Se não houver, tenta cabeçalho Authorization: Bearer <token> (Android Mobile).
 */
export async function obterSessaoOuMobile(req: Request): Promise<{
  id: string
  nome: string
  role: string
  empresaId: string | null
  clienteId: string | null
} | null> {
  const session = await auth()
  if (session?.user) {
    return {
      id: session.user.id,
      nome: session.user.nome ?? "",
      role: session.user.role,
      empresaId: session.user.empresaId ?? null,
      clienteId: session.user.clienteId ?? null,
    }
  }

  const mobileUser = await obterUsuarioMobile(req)
  if (mobileUser) {
    return {
      id: mobileUser.id,
      nome: mobileUser.nome,
      role: mobileUser.role,
      empresaId: mobileUser.empresaId,
      clienteId: mobileUser.clienteId,
    }
  }

  return null
}
