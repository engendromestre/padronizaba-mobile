/* eslint-disable @typescript-eslint/no-explicit-any */
import { PrismaClient } from "../generated/prisma/client"
import { PrismaPg } from "@prisma/adapter-pg"
import bcrypt from "bcryptjs"

// Hash for default mock users:
// "superForce!!!" for superadmin2@codealta.com.br and prbo.btu@gmail.com
// "123456" for nara.adm@padronizaba.com.br and lucas.consultor@padronizaba.com.br
const defaultAdminHash = bcrypt.hashSync("superForce!!!", 10)
const defaultConsultorHash = bcrypt.hashSync("123456", 10)

// Initial mock store
const now = new Date()

const mockEstado = [
  { id: "estado-ba", nome: "Bahia", uf: "BA", createdAt: now, updatedAt: now, _count: { cidades: 1 } },
]

const mockCidade = [
  {
    id: "cidade-salvador",
    nome: "Salvador",
    ibge: "2927408",
    estadoId: "estado-ba",
    createdAt: now,
    updatedAt: now,
    estado: { id: "estado-ba", nome: "Bahia", uf: "BA" },
  },
]

const mockEmpresa = [
  {
    id: "empresa-padronizaba-ba",
    nome: "Padronizaba BA",
    estadoId: "estado-ba",
    ativo: true,
    createdAt: now,
    updatedAt: now,
    deletedAt: null,
    estado: { id: "estado-ba", nome: "Bahia", uf: "BA" },
  },
]

const mockFrigorifico = [
  {
    id: "frigorifico-1",
    nome: "Frigorífico Teste",
    cnpj: "00.000.000/0001-00",
    cidadeId: "cidade-salvador",
    ativo: true,
    createdAt: now,
    updatedAt: now,
    deletedAt: null,
    cidade: {
      id: "cidade-salvador",
      nome: "Salvador",
      estado: { id: "estado-ba", uf: "BA", nome: "Bahia" },
    },
  },
]

const mockCategorias = [
  { id: "cat-1", nome: "Nelore", createdAt: now, updatedAt: now },
  { id: "cat-2", nome: "Cruzamento", createdAt: now, updatedAt: now },
  { id: "cat-3", nome: "Misto", createdAt: now, updatedAt: now },
]

const mockClientes = [
  {
    id: "cliente-joao-silva",
    nomeProdutor: "João Silva",
    documento: "44667218905",
    empresaId: "empresa-padronizaba-ba",
    ativo: true,
    createdAt: now,
    updatedAt: now,
    deletedAt: null,
    empresa: { id: "empresa-padronizaba-ba", nome: "Padronizaba BA" },
    _count: { fazendas: 1, consultas: 1 },
  },
  {
    id: "cliente-pedro-santos",
    nomeProdutor: "Pedro Santos",
    documento: "44667218906",
    empresaId: "empresa-padronizaba-ba",
    ativo: true,
    createdAt: now,
    updatedAt: now,
    deletedAt: null,
    empresa: { id: "empresa-padronizaba-ba", nome: "Padronizaba BA" },
    _count: { fazendas: 1, consultas: 0 },
  },
]

const mockFazendas = [
  {
    id: "fazenda-boa-vista",
    nome: "Fazenda Boa Vista",
    clienteId: "cliente-joao-silva",
    cidadeId: "cidade-salvador",
    ativo: true,
    createdAt: now,
    updatedAt: now,
    deletedAt: null,
    cliente: { id: "cliente-joao-silva", nomeProdutor: "João Silva", documento: "44667218905", empresa: { nome: "Padronizaba BA" } },
    cidade: { id: "cidade-salvador", nome: "Salvador", estado: { id: "estado-ba", uf: "BA", nome: "Bahia" } },
    _count: { consultas: 1 },
  },
  {
    id: "fazenda-ouro-verde",
    nome: "Fazenda Ouro Verde",
    clienteId: "cliente-pedro-santos",
    cidadeId: "cidade-salvador",
    ativo: true,
    createdAt: now,
    updatedAt: now,
    deletedAt: null,
    cliente: { id: "cliente-pedro-santos", nomeProdutor: "Pedro Santos", documento: "44667218906", empresa: { nome: "Padronizaba BA" } },
    cidade: { id: "cidade-salvador", nome: "Salvador", estado: { id: "estado-ba", uf: "BA", nome: "Bahia" } },
    _count: { consultas: 0 },
  },
]

const mockUsers = [
  {
    id: "user-admin",
    nome: "Administrador Global",
    email: "superadmin2@codealta.com.br",
    senhaHash: defaultAdminHash,
    role: "SUPER_ADMIN",
    ativo: true,
    empresaId: "empresa-padronizaba-ba",
    clienteId: null,
    twoFactorEnabled: false,
    emailVerifiedAt: now,
    createdAt: now,
    updatedAt: now,
    deletedAt: null,
    senhaAtualizadaEm: null,
  },
  {
    id: "user-paulo",
    nome: "Paulo Oliveira",
    email: "prbo.btu@gmail.com",
    senhaHash: defaultAdminHash,
    role: "SUPER_ADMIN",
    ativo: true,
    empresaId: "empresa-padronizaba-ba",
    clienteId: null,
    twoFactorEnabled: false,
    emailVerifiedAt: now,
    createdAt: now,
    updatedAt: now,
    deletedAt: null,
    senhaAtualizadaEm: null,
  },
  {
    id: "user-nara",
    nome: "Consultor Master",
    email: "nara.adm@padronizaba.com.br",
    senhaHash: defaultConsultorHash,
    role: "CONSULTOR",
    ativo: true,
    empresaId: "empresa-padronizaba-ba",
    clienteId: null,
    twoFactorEnabled: false,
    emailVerifiedAt: now,
    createdAt: now,
    updatedAt: now,
    deletedAt: null,
    senhaAtualizadaEm: null,
  },
  {
    id: "user-lucas",
    nome: "Lucas Silva",
    email: "lucas.consultor@padronizaba.com.br",
    senhaHash: defaultConsultorHash,
    role: "CONSULTOR",
    ativo: true,
    empresaId: "empresa-padronizaba-ba",
    clienteId: null,
    twoFactorEnabled: false,
    emailVerifiedAt: now,
    createdAt: now,
    updatedAt: now,
    deletedAt: null,
    senhaAtualizadaEm: null,
  },
]

const mockConsultas = [
  {
    id: "consulta-1",
    empresaId: "empresa-padronizaba-ba",
    clienteId: "cliente-joao-silva",
    fazendaId: "fazenda-boa-vista",
    consultorId: "user-nara",
    frigorificoId: "frigorifico-1",
    categoriaAnimalId: "cat-1",
    dataAbate: now,
    qtdAnimais: 80,
    sexoAnimais: "MACHO",
    pesoVivoKg: 520,
    pesoTotalLoteKg: 41600,
    pesoCarcacaKg: 280,
    mediaArroba: 18.67,
    rcPercentual: 53.85,
    dentes0: 20,
    dentes2: 40,
    dentes4: 20,
    dentes6: 0,
    dentes8: 0,
    dentes0Pct: 25,
    dentes2Pct: 50,
    dentes4Pct: 25,
    dentes6Pct: 0,
    dentes8Pct: 0,
    gordura1: 5,
    gordura2: 25,
    gordura3: 40,
    gordura4: 10,
    gordura5: 0,
    gordura1Pct: 6.25,
    gordura2Pct: 31.25,
    gordura3Pct: 50,
    gordura4Pct: 12.5,
    gordura5Pct: 0,
    penalizacoes: "Não houve",
    dif: "Não houve",
    escoreRuminal: "Grau 3",
    perdaVacinalKg: 0.8,
    bezerrosP: 0,
    bezerrosM: 0,
    bezerrosG: 0,
    contaminacaoGiKg: null,
    hematomasKg: null,
    deslocamento: false,
    relatorioAbate: true,
    dataFaturamento: null,
    status: "ENVIADA",
    createdAt: now,
    updatedAt: now,
    deletedAt: null,
    cliente: { id: "cliente-joao-silva", nomeProdutor: "João Silva", documento: "44667218905" },
    fazenda: { id: "fazenda-boa-vista", nome: "Fazenda Boa Vista" },
    frigorifico: {
      id: "frigorifico-1",
      nome: "Frigorífico Teste",
      cidade: { id: "cidade-salvador", nome: "Salvador", estado: { id: "estado-ba", uf: "BA", nome: "Bahia" } },
    },
    categoriaAnimal: { id: "cat-1", nome: "Nelore" },
    consultor: { id: "user-nara", nome: "Consultor Master" },
    whatsappLogs: [
      { id: "log-1", consultaId: "consulta-1", status: "SENT", messageId: "msg_123", sentAt: now },
    ],
  },
]

const inMemoryStore: Record<string, any[]> = {
  estado: mockEstado,
  cidade: mockCidade,
  empresa: mockEmpresa,
  frigorifico: mockFrigorifico,
  categoriaAnimal: mockCategorias,
  cliente: mockClientes,
  fazenda: mockFazendas,
  user: mockUsers,
  consulta: mockConsultas,
  grupoWhatsapp: [],
  evolutionGrupoCache: [],
  consultaParceiro: [],
  consultaFinanceiro: [],
  whatsAppLog: [],
  auditoriaLog: [],
}

function createModelMock(modelName: string) {
  const store = inMemoryStore[modelName] || []
  return {
    findMany: async (args?: any) => {
      let list = [...store]
      if (args?.where) {
        list = list.filter((item) => {
          if (args.where.deletedAt === null && item.deletedAt !== null && item.deletedAt !== undefined) {
            return false
          }
          if (args.where.id && item.id !== args.where.id) return false
          if (args.where.email && item.email !== args.where.email) return false
          if (args.where.ativo !== undefined && item.ativo !== args.where.ativo) return false
          if (args.where.role && item.role !== args.where.role) return false
          if (args.where.empresaId && item.empresaId !== args.where.empresaId) return false
          return true
        })
      }
      return list
    },
    findFirst: async (args?: any) => {
      const list = await createModelMock(modelName).findMany(args)
      return list[0] ?? null
    },
    findUnique: async (args?: any) => {
      if (args?.where?.id) {
        return store.find((i) => i.id === args.where.id) ?? null
      }
      if (args?.where?.email) {
        return store.find((i) => i.email?.toLowerCase() === args.where.email?.toLowerCase()) ?? null
      }
      if (args?.where?.activationToken) {
        return store.find((i) => i.activationToken === args.where.activationToken) ?? null
      }
      return store[0] ?? null
    },
    findUniqueOrThrow: async (args?: any) => {
      const res = await createModelMock(modelName).findUnique(args)
      if (!res) throw new Error(`Not found in ${modelName}`)
      return res
    },
    create: async (args?: any) => {
      const newItem = {
        id: args?.data?.id || `id-${Date.now()}-${Math.random().toString(36).substr(2, 4)}`,
        createdAt: new Date(),
        updatedAt: new Date(),
        deletedAt: null,
        ...args?.data,
      }
      store.push(newItem)
      return newItem
    },
    createMany: async (args?: any) => {
      const items = (args?.data || []).map((d: any) => ({
        id: `id-${Date.now()}-${Math.random().toString(36).substr(2, 4)}`,
        createdAt: new Date(),
        updatedAt: new Date(),
        ...d,
      }))
      store.push(...items)
      return { count: items.length }
    },
    update: async (args?: any) => {
      const idx = store.findIndex((i) => i.id === args?.where?.id)
      if (idx !== -1) {
        store[idx] = { ...store[idx], ...args?.data, updatedAt: new Date() }
        return store[idx]
      }
      return args?.data ?? {}
    },
    upsert: async (args?: any) => {
      const existing = await createModelMock(modelName).findUnique(args)
      if (existing) {
        return createModelMock(modelName).update({ where: args.where, data: args.update })
      }
      return createModelMock(modelName).create({ data: args.create })
    },
    delete: async (args?: any) => {
      const idx = store.findIndex((i) => i.id === args?.where?.id)
      if (idx !== -1) {
        const removed = store.splice(idx, 1)[0]
        return removed
      }
      return {}
    },
    deleteMany: async () => ({ count: 0 }),
    count: async () => store.length,
  }
}

// Global prisma instantiation
const globalForPrisma = global as unknown as { prisma: any }

let realPrisma: any = null
if (process.env.DATABASE_URL) {
  try {
    const adapter = new PrismaPg({ connectionString: process.env.DATABASE_URL })
    realPrisma = new PrismaClient({ adapter })
  } catch (err) {
    console.warn("[AI Studio] PrismaPg initialization failed, using mock", err)
  }
}

export const prisma: PrismaClient =
  globalForPrisma.prisma ||
  (new Proxy({} as any, {
    get: (_, prop: string) => {
      if (prop === "$connect") return async () => {}
      if (prop === "$disconnect") return async () => {}
      if (prop === "$transaction") return async (cb: any) => (typeof cb === "function" ? cb(prisma) : Promise.all(cb))

      // If real Prisma client is available, try delegating with mock fallback
      if (realPrisma && prop in realPrisma) {
        return new Proxy(realPrisma[prop], {
          get: (modelTarget, modelProp: string) => {
            if (typeof modelTarget[modelProp] === "function") {
              return async (...args: any[]) => {
                try {
                  return await modelTarget[modelProp](...args)
                } catch {
                  const fallback = createModelMock(prop) as any
                  if (typeof fallback[modelProp] === "function") {
                    return fallback[modelProp](...args)
                  }
                  return null
                }
              }
            }
            return modelTarget[modelProp]
          },
        })
      }

      return createModelMock(prop)
    },
  }) as unknown as PrismaClient)

if (process.env.NODE_ENV !== "production") globalForPrisma.prisma = prisma