import { senhaForteSchema } from "@/lib/schemas/senha"
import { describe, expect, it } from "vitest"

describe("Validação de Senha Forte (senhaForteSchema)", () => {
  it("deve aceitar uma senha que atende todos os requisitos de segurança", () => {
    const valida = senhaForteSchema.safeParse("SenhaForte@2026")
    expect(valida.success).toBe(true)
  })

  it("deve rejeitar senha com menos de 8 caracteres", () => {
    const curta = senhaForteSchema.safeParse("Ab1!xyz")
    expect(curta.success).toBe(false)
    if (!curta.success) {
      expect(curta.error.issues[0].message).toContain("Mínimo 8 caracteres")
    }
  })

  it("deve rejeitar senha sem letra maiúscula", () => {
    const semMaiuscula = senhaForteSchema.safeParse("senhaforte@123")
    expect(semMaiuscula.success).toBe(false)
    if (!semMaiuscula.success) {
      expect(semMaiuscula.error.issues[0].message).toContain("letra maiúscula")
    }
  })

  it("deve rejeitar senha sem número", () => {
    const semNumero = senhaForteSchema.safeParse("SenhaForte@SemNum")
    expect(semNumero.success).toBe(false)
    if (!semNumero.success) {
      expect(semNumero.error.issues[0].message).toContain("número")
    }
  })

  it("deve rejeitar senha sem símbolo especial", () => {
    const semSimbolo = senhaForteSchema.safeParse("SenhaForteSemSimbolo123")
    expect(semSimbolo.success).toBe(false)
    if (!semSimbolo.success) {
      expect(semSimbolo.error.issues[0].message).toContain("símbolo")
    }
  })
})
