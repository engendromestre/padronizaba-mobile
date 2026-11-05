import { describe, expect, it, vi } from "vitest"

vi.mock("@/lib/auth", () => ({ auth: vi.fn() }))

import { POST as loginHandler } from "@/app/api/mobile/auth/login/route"
import { GET as syncHandler } from "@/app/api/mobile/sync/dados-apoio/route"
import { GET as getConsultasHandler, POST as postConsultaHandler } from "@/app/api/consultas/route"
import { NextRequest } from "next/server"

describe("Integração - API Móvel do Consultor", () => {
  let authToken = ""

  it("POST /api/mobile/auth/login - deve autenticar consultor com sucesso", async () => {
    const req = new NextRequest("http://localhost:3000/api/mobile/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        email: "nara.adm@padronizaba.com.br",
        password: "123456",
      }),
    })

    const res = await loginHandler(req)
    expect(res.status).toBe(200)

    const json = await res.json()
    expect(json.success).toBe(true)
    expect(json.token).toBeDefined()
    expect(json.user.role).toBe("CONSULTOR")
    expect(json.user.empresaId).toBe("empresa-padronizaba-ba")

    authToken = json.token
  })

  it("POST /api/mobile/auth/login - deve rejeitar credenciais incorretas com 401", async () => {
    const req = new NextRequest("http://localhost:3000/api/mobile/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        email: "nara.adm@padronizaba.com.br",
        password: "senha-errada-123",
      }),
    })

    const res = await loginHandler(req)
    expect(res.status).toBe(401)

    const json = await res.json()
    expect(json.error).toContain("Credenciais inválidas")
  })

  it("GET /api/mobile/sync/dados-apoio - deve rejeitar requisição sem token com 401", async () => {
    const req = new NextRequest("http://localhost:3000/api/mobile/sync/dados-apoio", {
      method: "GET",
    })

    const res = await syncHandler(req)
    expect(res.status).toBe(401)
  })

  it("GET /api/mobile/sync/dados-apoio - deve retornar catálogo para cache offline com Bearer Token", async () => {
    const req = new NextRequest("http://localhost:3000/api/mobile/sync/dados-apoio", {
      method: "GET",
      headers: {
        Authorization: `Bearer ${authToken}`,
      },
    })

    const res = await syncHandler(req)
    expect(res.status).toBe(200)

    const json = await res.json()
    expect(json.categorias).toBeInstanceOf(Array)
    expect(json.categorias.length).toBeGreaterThan(0)
    expect(json.frigorificos).toBeInstanceOf(Array)
    expect(json.clientes).toBeInstanceOf(Array)
    expect(json.empresa).toBeDefined()
    expect(json.empresa.nome).toBe("Padronizaba BA")
  })

  it("POST /api/consultas - deve cadastrar novo lote e suportar idempotência via localId", async () => {
    const localIdUnico = `teste-idemp-${Date.now()}`
    const payload = {
      localId: localIdUnico,
      clienteId: "cliente-joao-silva",
      fazendaId: "fazenda-boa-vista",
      grupoWhatsappId: ["grupo-joao-01"],
      frigorificoId: "frigorifico-1",
      categoriaAnimalId: "cat-1",
      dataAbate: "2026-11-05",
      qtdAnimais: 40,
      sexoLote: "MACHO",
      pesoVivoKg: 520,
      pesoTotalLoteKg: 20800,
      pesoCarcacaKg: 280,
      dentes0: 10,
      dentes2: 20,
      dentes4: 10,
      dentes6: 0,
      dentes8: 0,
      gordura1: 2,
      gordura2: 8,
      gordura3: 25,
      gordura4: 5,
      gordura5: 0,
      bezerrosP: 0,
      bezerrosM: 0,
      bezerrosG: 0,
    }

    // 1º Envio
    const req1 = new NextRequest("http://localhost:3000/api/consultas", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${authToken}`,
      },
      body: JSON.stringify(payload),
    })

    const res1 = await postConsultaHandler(req1)
    expect(res1.status).toBe(201)
    const json1 = await res1.json()
    expect(json1.id).toBe(localIdUnico)

    // 2º Envio (Reenvio pelo WorkManager por timeout ou repetição)
    const req2 = new NextRequest("http://localhost:3000/api/consultas", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${authToken}`,
      },
      body: JSON.stringify(payload),
    })

    const res2 = await postConsultaHandler(req2)
    expect(res2.status).toBe(200)
    const json2 = await res2.json()
    expect(json2.id).toBe(localIdUnico)
    expect(json2.status).toBe("JA_PROCESSADA")
  })

  it("GET /api/consultas - deve listar consultas autorizadas para o consultor", async () => {
    const req = new NextRequest("http://localhost:3000/api/consultas?limit=5&page=1", {
      method: "GET",
      headers: {
        Authorization: `Bearer ${authToken}`,
      },
    })

    const res = await getConsultasHandler(req)
    expect(res.status).toBe(200)

    const json = await res.json()
    expect(json.data).toBeInstanceOf(Array)
    expect(json.pagination).toBeDefined()
    expect(json.pagination.page).toBe(1)
  })
})
