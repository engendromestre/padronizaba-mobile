import { describe, expect, it, vi } from "vitest"

vi.mock("@/lib/auth", () => ({ auth: vi.fn() }))

import { gerarTokenMobile, verificarTokenMobile } from "@/lib/mobile-auth"
import { consultaSchema } from "@/lib/schemas/consulta"

describe("Mobile Auth (JWT)", () => {
  const usuarioMock = {
    id: "user-consultor-1",
    nome: "Consultor de Campo",
    email: "consultor@padronizaba.com.br",
    role: "CONSULTOR" as const,
    empresaId: "empresa-1",
  }

  it("deve gerar um token JWT válido e assinado", () => {
    const token = gerarTokenMobile(usuarioMock, 30)
    expect(token).toBeDefined()
    expect(token.split(".").length).toBe(3)
  })

  it("deve verificar e decodificar o token com sucesso", () => {
    const token = gerarTokenMobile(usuarioMock, 30)
    const payload = verificarTokenMobile(token)

    expect(payload).not.toBeNull()
    expect(payload?.id).toBe(usuarioMock.id)
    expect(payload?.email).toBe(usuarioMock.email)
    expect(payload?.role).toBe("CONSULTOR")
    expect(payload?.empresaId).toBe("empresa-1")
  })

  it("deve rejeitar token com assinatura adulterada", () => {
    const token = gerarTokenMobile(usuarioMock, 30)
    const [header, payload, sig] = token.split(".")
    const tokenAdulterado = `${header}.${payload}.${sig.slice(0, -4)}XXXX`

    const verificado = verificarTokenMobile(tokenAdulterado)
    expect(verificado).toBeNull()
  })

  it("deve rejeitar token expirado", () => {
    // Gerar token com -1 dia de validade (já expirado)
    const tokenExpirado = gerarTokenMobile(usuarioMock, -1)
    const verificado = verificarTokenMobile(tokenExpirado)
    expect(verificado).toBeNull()
  })
})

describe("Consulta Schema - Suporte a Mobile", () => {
  it("deve aceitar localId para idempotência do aplicativo móvel", () => {
    const dadosConsulta = {
      localId: "c3d9a1f2-uuid-mobile-1234",
      clienteId: "cliente-1",
      fazendaId: "fazenda-1",
      grupoWhatsappId: ["grupo-1"],
      frigorificoId: "frigorifico-1",
      categoriaAnimalId: "cat-1",
      dataAbate: "2026-11-05",
      qtdAnimais: 50,
      sexoLote: "MACHO",
      pesoVivoKg: 500,
      pesoTotalLoteKg: 25000,
      pesoCarcacaKg: 270,
      dentes0: 10,
      dentes2: 25,
      dentes4: 15,
      dentes6: 0,
      dentes8: 0,
      gordura1: 5,
      gordura2: 15,
      gordura3: 25,
      gordura4: 5,
      gordura5: 0,
      bezerrosP: 0,
      bezerrosM: 0,
      bezerrosG: 0,
    }

    const resultado = consultaSchema.safeParse(dadosConsulta)
    expect(resultado.success).toBe(true)
    if (resultado.success) {
      expect(resultado.data.localId).toBe("c3d9a1f2-uuid-mobile-1234")
    }
  })
})
