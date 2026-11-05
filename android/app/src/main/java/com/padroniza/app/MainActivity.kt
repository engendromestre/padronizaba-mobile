package com.padroniza.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.padroniza.app.data.local.entities.*
import com.padroniza.app.data.repository.AuthRepository
import com.padroniza.app.data.repository.ConsultaRepository
import com.padroniza.app.data.repository.SyncRepository
import com.padroniza.app.ui.consulta.NovaConsultaScreen
import com.padroniza.app.ui.dashboard.DashboardScreen
import com.padroniza.app.ui.login.LoginScreen
import com.padroniza.app.ui.theme.PadronizaTheme
import kotlinx.coroutines.launch

enum class TelaAtual {
    LOGIN,
    DASHBOARD,
    NOVA_CONSULTA
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authRepository = AuthRepository(this)
        val syncRepository = SyncRepository(this)
        val consultaRepository = ConsultaRepository(this)

        setContent {
            PadronizaTheme {
                val scope = rememberCoroutineScope()
                var telaAtual by remember {
                    mutableStateOf(if (authRepository.estaAutenticado()) TelaAtual.DASHBOARD else TelaAtual.LOGIN)
                }

                var isLoginLoading by remember { mutableStateOf(false) }
                var loginErrorMessage by remember { mutableStateOf<String?>(null) }

                // Listas observadas do banco Room local
                val consultas by consultaRepository.listarConsultas().collectAsState(initial = emptyList())
                val frigorificos by syncRepository.listarFrigorificos().collectAsState(initial = emptyList())
                val categorias by syncRepository.listarCategorias().collectAsState(initial = emptyList())
                val clientes by syncRepository.listarClientes().collectAsState(initial = emptyList())

                var selectedClienteIdParaFazendas by remember { mutableStateOf<String?>(null) }
                val fazendas by (if (selectedClienteIdParaFazendas != null) {
                    syncRepository.listarFazendasDoCliente(selectedClienteIdParaFazendas!)
                } else {
                    remember { kotlinx.coroutines.flow.flowOf(emptyList<FazendaEntity>()) }
                }).collectAsState(initial = emptyList())

                val grupos by (if (selectedClienteIdParaFazendas != null) {
                    syncRepository.listarGruposDoCliente(selectedClienteIdParaFazendas!)
                } else {
                    remember { kotlinx.coroutines.flow.flowOf(emptyList<GrupoWhatsappEntity>()) }
                }).collectAsState(initial = emptyList())

                when (telaAtual) {
                    TelaAtual.LOGIN -> {
                        LoginScreen(
                            isLoading = isLoginLoading,
                            errorMessage = loginErrorMessage,
                            onLoginClick = { email, password ->
                                isLoginLoading = true
                                loginErrorMessage = null
                                scope.launch {
                                    val result = authRepository.login(email, password)
                                    isLoginLoading = false
                                    result.onSuccess {
                                        // Baixa catálogo inicial para o Room
                                        syncRepository.sincronizarCatalogo()
                                        telaAtual = TelaAtual.DASHBOARD
                                    }.onFailure { erro ->
                                        loginErrorMessage = erro.message ?: "Falha ao realizar login."
                                    }
                                }
                            }
                        )
                    }

                    TelaAtual.DASHBOARD -> {
                        DashboardScreen(
                            consultorNome = authRepository.obterNomeUsuario(),
                            consultas = consultas,
                            onNovaConsulta = { telaAtual = TelaAtual.NOVA_CONSULTA },
                            onSincronizarManual = {
                                scope.launch {
                                    Toast.makeText(this@MainActivity, "Sincronizando...", Toast.LENGTH_SHORT).show()
                                    val res = syncRepository.sincronizarCatalogo()
                                    res.onSuccess {
                                        Toast.makeText(this@MainActivity, "Catálogo atualizado!", Toast.LENGTH_SHORT).show()
                                    }.onFailure {
                                        Toast.makeText(this@MainActivity, "Falha na sincronização: ${it.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            onLogout = {
                                authRepository.logout()
                                telaAtual = TelaAtual.LOGIN
                            },
                            onSelecionarConsulta = { localId ->
                                Toast.makeText(this@MainActivity, "Lote: $localId", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    TelaAtual.NOVA_CONSULTA -> {
                        NovaConsultaScreen(
                            frigorificos = frigorificos,
                            categorias = categorias,
                            clientes = clientes,
                            fazendasDoCliente = fazendas,
                            gruposDoCliente = grupos,
                            onCarregarFazendasEGrupos = { clienteId ->
                                selectedClienteIdParaFazendas = clienteId
                            },
                            onSalvarConsulta = { novaConsulta ->
                                scope.launch {
                                    consultaRepository.finalizarEEnviar(novaConsulta)
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Abate gravado localmente! Sincronizando com a central...",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    telaAtual = TelaAtual.DASHBOARD
                                }
                            },
                            onVoltar = { telaAtual = TelaAtual.DASHBOARD }
                        )
                    }
                }
            }
        }
    }
}
