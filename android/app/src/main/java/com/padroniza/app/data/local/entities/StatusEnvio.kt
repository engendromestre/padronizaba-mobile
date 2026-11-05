package com.padroniza.app.data.local.entities

enum class StatusEnvio {
    RASCUNHO,        // Consulta em preenchimento pelo consultor
    PENDENTE_ENVIO,  // Finalizada pelo consultor, na fila do WorkManager aguardando conexão
    ENVIADO,         // Confirmada pelo backend com sucesso
    ERRO             // Erro definitivo de validação no backend
}
