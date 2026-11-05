# Padroniza.ba — Aplicativo Nativo Android de Campo

Aplicativo nativo desenvolvido em **Kotlin** com **Jetpack Compose**, **Room Database (SQLite)** e **WorkManager**, projetado especificamente para consultores de carcaça e abates em frigoríficos e fazendas com conectividade intermitente ou nula.

---

## 🏗️ Arquitetura e Decisões Técnicas

```
┌──────────────────────────────────────────────────────────────┐
│                    UI (Jetpack Compose)                      │
│   - LoginScreen: autenticação do consultor                  │
│   - DashboardScreen: listagem reativa com badges de status   │
│   - NovaConsultaScreen: formulário guiado em 5 etapas        │
└──────────────────────────────┬───────────────────────────────┘
                               │ Observa Flows reativos
┌──────────────────────────────▼───────────────────────────────┐
│              Camada de Repositórios & Negócio                │
│   - AuthRepository: JWT em EncryptedSharedPreferences        │
│   - SyncRepository: catálogo offline (clientes, fazendas)    │
│   - ConsultaRepository: cálculos de arroba, RC% e rascunhos  │
└───────────────────────┬──────────────────────┬───────────────┘
                        │                      │
┌───────────────────────▼────────┐  ┌──────────▼───────────────┐
│     Room Database (SQLite)     │  │   WorkManager Background │
│  - ConsultaEntity (Idempotente)│  │ - UploadConsultaWorker   │
│  - Catálogo de apoio local     │  │ - Conexão garantida      │
│  - Status: RASCUNHO / PENDENTE │  │ - Backoff exponencial    │
└────────────────────────────────┘  └──────────┬───────────────┘
                                               │ HTTPS (Bearer)
                                    ┌──────────▼───────────────┐
                                    │    Backend Next.js API   │
                                    │ - POST /api/consultas    │
                                    └──────────────────────────┘
```

---

## 📱 Destaques de Engenharia Offline

1. **Persistência Total Local**:
   - Cada abate finalizado recebe um `localId` (UUID) e é gravado no SQLite imediatamente com status `PENDENTE_ENVIO`.
   - Nenhuma informação é perdida caso o celular descarregue ou seja reiniciado no meio do expediente.

2. **WorkManager Resiliente**:
   - O upload só é disparado quando o Android detecta rede ativa (`NetworkType.CONNECTED`).
   - Se o sinal oscilar durante a transmissão, o WorkManager entra em retry automático com backoff exponencial.

3. **Garantia de Idempotência**:
   - O `localId` é enviado ao backend. Se a requisição for repetida, o backend detecta o registro e responde `JA_PROCESSADA`, evitando lotes duplicados no banco e mensagens duplicadas no WhatsApp.

4. **Interface Otimizada para Frigoríficos**:
   - Contadores rápidos com toques de alta precisão para maturidade (dentição 0, 2, 4, 6, 8) e acabamento (gordura 1 a 5).
   - Cálculo em tempo real de percentuais, média em arrobas e rendimento de carcaça.

---

## 🚀 Como executar no Android Studio

1. Abra a pasta `android/` no **Android Studio Ladybug (2024.2+)** ou superior.
2. Aguarde a sincronização inicial do Gradle.
3. Configure a URL base da sua API em `com/padroniza/app/data/remote/RetrofitClient.kt`:
   - Para emulador Android local apontando para o seu dev server na máquina: `"http://10.0.2.2:3000/"`
   - Para aparelho físico via Wi-Fi: `"http://SEU_IP_LOCAL:3000/"`
   - Para ambiente de produção: `"https://seu-dominio.com.br/"`
4. Conecte um dispositivo ou inicie um emulador e clique em **Run (Shift + F10)**.
