import { obterSessaoOuMobile } from "@/lib/mobile-auth"
import { prisma } from "@/lib/prisma"
import { NextRequest, NextResponse } from "next/server"

export const dynamic = "force-dynamic"

export async function GET(req: NextRequest) {
  try {
    const user = await obterSessaoOuMobile(req)
    if (!user) {
      return NextResponse.json({ error: "Não autorizado" }, { status: 401 })
    }

    const whereCliente =
      user.role === "SUPER_ADMIN"
        ? { deletedAt: null, ativo: true }
        : { empresaId: user.empresaId ?? "", deletedAt: null, ativo: true }

    const [categorias, frigorificos, clientes, empresa] = await Promise.all([
      prisma.categoriaAnimal.findMany({
        orderBy: { nome: "asc" },
        select: {
          id: true,
          nome: true,
        },
      }),

      prisma.frigorifico.findMany({
        where: { deletedAt: null, ativo: true },
        orderBy: { nome: "asc" },
        select: {
          id: true,
          nome: true,
          cnpj: true,
          cidade: {
            select: {
              id: true,
              nome: true,
              estado: {
                select: {
                  id: true,
                  uf: true,
                  nome: true,
                },
              },
            },
          },
        },
      }),

      prisma.cliente.findMany({
        where: whereCliente,
        orderBy: { nomeProdutor: "asc" },
        select: {
          id: true,
          nomeProdutor: true,
          documento: true,
          empresaId: true,
          fazendas: {
            where: { deletedAt: null, ativo: true },
            select: {
              id: true,
              nome: true,
              cidade: {
                select: {
                  id: true,
                  nome: true,
                  estado: {
                    select: {
                      id: true,
                      uf: true,
                    },
                  },
                },
              },
            },
          },
          grupos: {
            where: { deletedAt: null, ativo: true },
            select: {
              id: true,
              nomeGrupo: true,
              groupIdWhatsapp: true,
            },
          },
        },
      }),

      user.empresaId
        ? prisma.empresa.findUnique({
            where: { id: user.empresaId },
            select: { id: true, nome: true },
          })
        : null,
    ])

    return NextResponse.json({
      timestamp: new Date().toISOString(),
      versao: "1.0",
      usuario: {
        id: user.id,
        nome: user.nome,
        role: user.role,
      },
      empresa,
      categorias,
      frigorificos,
      clientes,
    })
  } catch (error) {
    console.error("Erro ao sincronizar dados de apoio para mobile:", error)
    return NextResponse.json(
      { error: "Erro interno ao carregar dados de sincronização." },
      { status: 500 }
    )
  }
}
