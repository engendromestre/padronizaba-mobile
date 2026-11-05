import { calcularTotais, consultaSchema } from "@/lib/schemas/consulta"
import { describe, expect, it } from "vitest"

describe("Cálculos Zod e Fórmulas de Carcaça (calcularTotais)", () => {
  it("deve calcular corretamente arroba (@) e rendimento de carcaça (RC%)", () => {
    // 50 animais, peso vivo médio = 500 kg, carcaça média = 270 kg, peso total lote = 13500 kg
    const input = {
      qtdAnimais: 50,
      pesoVivoKg: 500,
      pesoTotalLoteKg: 13500,
      pesoCarcacaKg: 270,
      dentes0: 20,
      dentes2: 30,
      dentes4: 0,
      dentes6: 0,
      dentes8: 0,
      gordura1: 0,
      gordura2: 10,
      gordura3: 35,
      gordura4: 5,
      gordura5: 0,
    }

    const totais = calcularTotais(input)

    // Média @: (13500 / 50) / 15 = 270 / 15 = 18.00 @
    expect(totais.mediaArroba).toBe(18)

    // RC%: (13500 / (500 * 50)) * 100 = (13500 / 25000) * 100 = 54.00%
    expect(totais.rcPercentual).toBe(54)

    // Dentes %: 20/50 = 40%, 30/50 = 60%
    expect(totais.dentes0Pct).toBe(40)
    expect(totais.dentes2Pct).toBe(60)
    expect(totais.dentes4Pct).toBe(0)

    // Gordura %: 10/50 = 20%, 35/50 = 70%, 5/50 = 10%
    expect(totais.gordura2Pct).toBe(20)
    expect(totais.gordura3Pct).toBe(70)
    expect(totais.gordura4Pct).toBe(10)
  })

  it("deve retornar zeros com segurança e evitar divisão por zero se qtdAnimais for zero ou negativo", () => {
    const input = {
      qtdAnimais: 0,
      pesoVivoKg: 500,
      pesoTotalLoteKg: 0,
      pesoCarcacaKg: 0,
    }

    const totais = calcularTotais(input)

    expect(totais.mediaArroba).toBe(0)
    expect(totais.rcPercentual).toBe(0)
    expect(totais.dentes0Pct).toBe(0)
    expect(totais.gordura1Pct).toBe(0)
  })

  it("deve rejeitar lote onde a carcaça ultrapassa 65% do peso vivo (defesa contra digitação de peso total)", () => {
    const inputInvalido = {
      clienteId: "cliente-1",
      fazendaId: "fazenda-1",
      grupoWhatsappId: ["grupo-1"],
      frigorificoId: "frigi-1",
      categoriaAnimalId: "cat-1",
      dataAbate: "2026-11-05",
      qtdAnimais: 50,
      sexoLote: "MACHO" as const,
      pesoVivoKg: 400,
      pesoTotalLoteKg: 20000,
      // 320 / 400 = 80% (acima do teto biológico de 65%)
      pesoCarcacaKg: 320,
      dentes0: 50,
      dentes2: 0,
      dentes4: 0,
      dentes6: 0,
      dentes8: 0,
      gordura1: 0,
      gordura2: 0,
      gordura3: 50,
      gordura4: 0,
      gordura5: 0,
      bezerrosP: 0,
      bezerrosM: 0,
      bezerrosG: 0,
    }

    const resultado = consultaSchema.safeParse(inputInvalido)
    expect(resultado.success).toBe(false)
    if (!resultado.success) {
      expect(resultado.error.issues[0].message).toContain("O Peso da Carcaça está alto demais")
    }
  })
})
