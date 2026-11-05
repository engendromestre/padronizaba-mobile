import { gerarTokenMobile } from "@/lib/mobile-auth"
import { prisma } from "@/lib/prisma"
import bcrypt from "bcryptjs"
import { NextRequest, NextResponse } from "next/server"
import { z } from "zod"

const loginMobileSchema = z.object({
  email: z.string().email("E-mail com formato inválido"),
  password: z.string().min(1, "A senha é obrigatória"),
})

export async function POST(req: NextRequest) {
  try {
    const body = await req.json()
    const parsed = loginMobileSchema.safeParse(body)

    if (!parsed.success) {
      return NextResponse.json(
        { error: "Dados inválidos", details: parsed.error.flatten().fieldErrors },
        { status: 400 }
      )
    }

    const { email, password } = parsed.data

    const user = await prisma.user.findUnique({
      where: { email: email.toLowerCase().trim() },
    })

    if (!user) {
      return NextResponse.json(
        { error: "Credenciais inválidas. Verifique seu e-mail e senha." },
        { status: 401 }
      )
    }

    if (!user.ativo) {
      return NextResponse.json(
        { error: "Conta suspensa ou inativa. Entre em contato com a administração." },
        { status: 403 }
      )
    }

    // Apenas consultores e administradores acessam o app de campo
    if (!["SUPER_ADMIN", "ADMIN", "CONSULTOR"].includes(user.role)) {
      return NextResponse.json(
        { error: "Seu perfil não possui acesso ao aplicativo móvel de consultas." },
        { status: 403 }
      )
    }

    const senhaValida = await bcrypt.compare(password, user.senhaHash)
    if (!senhaValida) {
      return NextResponse.json(
        { error: "Credenciais inválidas. Verifique seu e-mail e senha." },
        { status: 401 }
      )
    }

    const token = gerarTokenMobile(user, 60) // Válido por 60 dias para uso contínuo em campo

    return NextResponse.json({
      success: true,
      token,
      user: {
        id: user.id,
        nome: user.nome,
        email: user.email,
        role: user.role,
        empresaId: user.empresaId,
      },
    })
  } catch (error) {
    console.error("Erro no login mobile:", error)
    return NextResponse.json(
      { error: "Falha interna ao processar autenticação móvel." },
      { status: 500 }
    )
  }
}
