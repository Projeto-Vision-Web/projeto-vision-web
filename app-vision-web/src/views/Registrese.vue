<template lang="pug">
section.registro-multi
  .imagem-container
    img(
      src="@/assets/logocjl.png"
      alt="Imagem de registro"
      loading="lazy"
    )

  .form-container
    small.etapa-titulo(v-if="etapaAtual === 1") 
    small.etapa-titulo(v-else-if="etapaAtual === 2") 
    small.etapa-titulo(v-else-if="etapaAtual === 3") 
    small.etapa-titulo(v-else-if="etapaAtual === 4") Informe sua data de Nascimento:
    small.etapa-titulo(v-else-if="etapaAtual === 5") 
    small.etapa-titulo(v-else-if="etapaAtual === 6") 

    form(@submit.prevent="proximaEtapa")

      // Etapa 1 - Nome
      template(v-if="etapaAtual === 1")
        p.msg-digite-cpf.text-branco.text-left Digite o Nome Completo
        .input-group
          input(
            type="text"
            v-model="form.nome"
            :class="{ 'input-erro': erros.nome }"
            placeholder=" "
            id="nome"
          )
          label(for="nome") Nome próprio
        span.mensagem-erro(v-if="erros.nome") Introduza o nome próprio.

        .input-group
          input(
            type="text"
            v-model="form.sobrenome"
            :class="{ 'input-erro': erros.sobrenome }"
            placeholder=" "
            id="sobrenome"
          )
          label(for="sobrenome") Sobrenome
        span.mensagem-erro(v-if="erros.sobrenome") Introduza o sobrenome.

        .input-group
          input(
            type="text"
            v-model="form.apelido"
            :class="{ 'input-erro': false }"
            placeholder=" "
            id="apelido"
          )
          label(for="apelido") Apelido (Opcional)

        section.botoes
          button(type="submit") Seguinte

      // Etapa 2 - CPF (obrigatório para todos)
      template(v-if="etapaAtual === 2")
        p.msg-digite-cpf.text-branco.text-left Digite o CPF

        .input-group
          input(
            type="text"
            v-model="form.cpf"
            inputmode="numeric"
            maxlength="14"
            placeholder=" "
            id="cpf"
            @input="formatarCPF"
            :class="{ 'input-erro': erros.cpfVazio || erros.cpfInvalido }"
          )
          label(for="cpf") CPF
        span.mensagem-erro(v-if="tentativas.etapa2 && erros.cpfVazio") CPF é obrigatório.
        span.mensagem-erro(v-else-if="tentativas.etapa2 && erros.cpfInvalido") CPF inválido.

        // Campo Telefone
        p.msg-digite-telefone.text-branco.text-left Digite o Telefone
        .input-group
          input(
            type="text"
            v-model="form.telefone"
            inputmode="numeric"
            maxlength="15"
            placeholder=" "
            id="telefone"
            @input="formatarTelefone"
            :class="{ 'input-erro': erros.telefoneVazio || erros.telefoneInvalido }"
          )
          label(for="telefone") Telefone
        span.mensagem-erro(v-if="tentativas.etapa2 && erros.telefoneVazio") Telefone é obrigatório.
        span.mensagem-erro(v-else-if="tentativas.etapa2 && erros.telefoneInvalido") Telefone inválido.

        section.botoes
          button(type="button", @click="etapaAtual--") Voltar
          button(type="submit") Seguinte

      // Etapa 3 - Pessoa Jurídica (opcional)
      template(v-if="etapaAtual === 3")
        p.msg-digite-cpf.text-branco.text-left Você é uma pessoa Juridica?
        

        .radio-group
          .radio-option
            input#pj-sim(type="radio" name="pessoaJuridica" :value="true" v-model="form.ehPessoaJuridica")
            label(for="pj-sim") Sim, sou Pessoa Jurídica

          .radio-option
            input#pj-nao(type="radio" name="pessoaJuridica" :value="false" v-model="form.ehPessoaJuridica")
            label(for="pj-nao") Não, não sou Pessoa Jurídica

        // Campos aparecem somente se for PJ
        template(v-if="form.ehPessoaJuridica === true")
          
          .input-group
            input(
              type="text"
              v-model="form.cnpj"
              inputmode="numeric"
              maxlength="18"
              placeholder=" "
              id="cnpj"
              @input="formatarCNPJ"
              :class="{ 'input-erro': erros.cnpjInvalido }"
            )
            label(for="cnpj") CNPJ
          span.mensagem-erro(v-if="erros.cnpjInvalido") CNPJ inválido.

          .input-group
            input(
              type="text"
              v-model="form.nomeEmpresa"
              id="nomeEmpresa"
              placeholder=" "
              :class="{ 'input-erro': erros.nomeEmpresaInvalido }"
            )
            label(for="nomeEmpresa") Nome da empresa
          span.mensagem-erro(v-if="erros.nomeEmpresaInvalido") Nome da empresa é obrigatório.

          

        section.botoes
          button(type="button", @click="etapaAtual--") Voltar
          button(type="submit") Seguinte

      // Etapa 4 - Data de nascimento e gênero
      template(v-if="etapaAtual === 4")
        section.data-nascimento
          section.data-nascimento-selects
            section.input-group
              select(
                v-model="form.dia"
                :class="{ 'input-erro': erros.diaInvalido }"
                id="dia"
              )
                option(value="") Dia
                option(v-for="d in dias" :key="d" :value="d") {{ d }}

            section.input-group
              select(
                v-model="form.mes"
                :class="{ 'input-erro': erros.mesInvalido }"
                id="mes"
              )
                option(value="") Mês
                option(v-for="(m, index) in meses" :key="index" :value="index + 1") {{ m }}

            section.input-group
              select(
                v-model="form.ano"
                :class="{ 'input-erro': erros.anoInvalido }"
                id="ano"
              )
                option(value="") Ano
                option(v-for="a in anos" :key="a" :value="a") {{ a }}

          span.mensagem-erro-etapa2(v-if="erros.nascimentoIncompleto") Introduza uma data de nascimento completa.

          section.input-group.genero
            select(
              v-model="form.genero"
              :class="{ 'input-erro': erros.generoInvalido }"
              id="genero"
            )
              option(value="") Gênero
              option(value="Feminino") Feminino
              option(value="Masculino") Masculino
              option(value="Outro") Outro

          section
            span.mensagem-erro-etapa2(v-if="erros.generoInvalido") Selecione o seu gênero.
            span.mensagem-erro-etapa2(v-if="erros.idadeInvalida") É necessário ter pelo menos 18 anos para se registrar.

        section.botoes
          button(type="button", @click="etapaAtual--") Voltar
          button(type="submit") Seguinte

      // Etapa 5 - Endereço
      template(v-if="etapaAtual === 5")
        p.msg-digite-cpf.text-branco.text-left Digite o Endereço
        section.endereco
          .input-group
            input(
              type="text"
              v-model="form.cep"
              :class="{ 'input-erro-etapa3': erros.cepInvalido }"
              id="cep"
              placeholder=" "
              @blur="buscarEndereco"
              maxlength="9"
            )
            label(for="cep") CEP

          .input-group
            input(
              type="text"
              v-model="form.rua"
              :class="{ 'input-erro-etapa3': erros.ruaInvalida }"
              id="rua"
              placeholder=" "
            )
            label(for="rua") Logradouro

          .input-group
            input(
              type="text"
              v-model="form.numero"
              :class="{ 'input-erro-etapa3': erros.numeroInvalido }"
              id="numero"
              placeholder=" "
            )
            label(for="numero") Número

          .input-group
            input(
              type="text"
              v-model="form.complemento"
              id="complemento"
              placeholder=" "
            )
            label(for="complemento") Complemento (opcional)

          .input-group
            input(
              type="text"
              v-model="form.bairro"
              :class="{ 'input-erro-etapa3': erros.bairroInvalido }"
              id="bairro"
              placeholder=" "
            )
            label(for="bairro") Bairro

          .input-group
            input(
              type="text"
              v-model="form.cidade"
              :class="{ 'input-erro-etapa3': erros.cidadeInvalida }"
              id="cidade"
              placeholder=" "
            )
            label(for="cidade") Cidade

          .input-group
            input(
              type="text"
              v-model="form.estado"
              :class="{ 'input-erro-etapa3': erros.estadoInvalido }"
              id="estado"
              placeholder=" "
              maxlength="2"
            )
            label(for="estado") Estado

          span.mensagem-erro-etapa3(v-if="erros.enderecoIncompleto") Preencha todos os campos do endereço corretamente.

        section.botoes
          button(type="button", @click="etapaAtual--") Voltar
          button(type="submit") Seguinte

      // Etapa 6 - Credenciais
      template(v-if="etapaAtual === 6")
        p.msg-digite-cpf.text-branco.text-left Digite as Credenciais
        section.credenciais
          .input-group
            input(
              type="email"
              v-model="form.email"
              :class="{ 'input-erro-etapa4': erros.emailInvalido || erros.emailVazio }"
              id="email"
              placeholder=" "
            )
            label(for="email") Email
          span.mensagem-erro-etapa4(v-if="erros.emailVazio") O email é obrigatório.
          span.mensagem-erro-etapa4(v-if="erros.emailInvalido") Por favor, insira um email válido.

          .input-group
            input(
              :type="mostrarSenha ? 'text' : 'password'"
              v-model="form.senha"
              :class="{ 'input-erro-etapa4': erros.senhaInvalida || erros.senhaVazia }"
              id="senha"
              placeholder=" "
            )
            label(for="senha") Senha
          span.mensagem-erro-etapa4(v-if="erros.senhaVazia") A senha é obrigatória.
          span.mensagem-erro-etapa4(v-if="erros.senhaInvalida") A senha deve conter pelo menos uma letra maiúscula e um número.

          .input-group
            input(
              :type="mostrarSenha ? 'text' : 'password'"
              v-model="form.confirmaSenha"
              :class="{ 'input-erro-etapa4': erros.senhasDiferentes || erros.confirmaSenhaVazia }"
              id="confirmaSenha"
              placeholder=" "
            )
            label(for="confirmaSenha") Confirmar Senha
          span.mensagem-erro-etapa4(v-if="erros.confirmaSenhaVazia") Por favor, confirme a senha.
          span.mensagem-erro-etapa4(v-if="erros.senhasDiferentes") As senhas não coincidem.

          .input-group
            input(
              type="checkbox"
              id="mostrarSenha"
              v-model="mostrarSenha"
            )
            label(for="mostrarSenha") Mostrar senha

          section.botoes
            button(type="button", @click="etapaAtual--") Voltar
            button(type="submit") Finalizar
</template>

<script setup>
import { reactive, ref } from 'vue'
import router from '@/router'

// Etapa atual do formulário
const etapaAtual = ref(1)

// Flags para controle de tentativas por etapa
const tentativas = reactive({
  etapa1: false,
  etapa2: false,
  etapa3: false,
  etapa4: false,
  etapa5: false,
  etapa6: false,
})

function formatarTelefone(e) {
  let valor = e.target.value.replace(/\D/g, "")
  valor = valor.replace(/^(\d{2})(\d)/g, "($1) $2") // adiciona DDD
  valor = valor.replace(/(\d{5})(\d{4})$/, "$1-$2") // adiciona traço
  form.telefone = valor
}

// Datas para selects de nascimento
const dias = Array.from({ length: 31 }, (_, i) => i + 1)
const meses = [
  'Janeiro', 'Fevereiro', 'Março', 'Abril',
  'Maio', 'Junho', 'Julho', 'Agosto',
  'Setembro', 'Outubro', 'Novembro', 'Dezembro'
]
const anos = []
for (let a = 2025; a >= 1900; a--) anos.push(a)

// Dados do formulário
const form = reactive({
  nome: '',
  sobrenome: '',
  apelido: '',
  ehPessoaJuridica: null,
  cpf: '',
  cnpj: '',
  nomeEmpresa: '',
  dia: '',
  mes: '',
  ano: '',
  genero: '',
  cep: '',
  rua: '',
  complemento: '',
  numero: '',
  bairro: '',
  cidade: '',
  estado: '',
  email: '',
  senha: '',
  confirmaSenha: '',
  telefone: ''
})

// Objeto de erros
const erros = reactive({
  nome: false,
  sobrenome: false,
  apelido: false,
  cpfVazio: false,
  cpfInvalido: false,
  cnpjInvalido: false,
  cpfDuplicado: false,
  nomeEmpresaInvalido: false,
  diaInvalido: false,
  mesInvalido: false,
  anoInvalido: false,
  nascimentoIncompleto: false,
  generoInvalido: false,
  idadeInvalida: false,
  cepInvalido: false,
  ruaInvalida: false,
  complementoInvalido: false,
  numeroInvalido: false,
  bairroInvalido: false,
  cidadeInvalida: false,
  estadoInvalido: false,
  enderecoIncompleto: false,
  emailVazio: false,
  emailInvalido: false,
  senhaVazia: false,
  senhaInvalida: false,
  confirmaSenhaVazia: false,
  senhasDiferentes: false,
  ehPessoaJuridicaVazio: false,
  telefoneVazio: false,
  telefoneInvalido: false
})

// Validação de CPF
const validarCPF = (cpf) => {
  cpf = cpf.replace(/[^\d]+/g, '')
  if (cpf.length !== 11 || /^(\d)\1+$/.test(cpf)) return false

  let soma = 0
  for (let i = 0; i < 9; i++) soma += parseInt(cpf.charAt(i)) * (10 - i)
  let resto = (soma * 10) % 11
  if (resto === 10 || resto === 11) resto = 0
  if (resto !== parseInt(cpf.charAt(9))) return false

  soma = 0
  for (let i = 0; i < 10; i++) soma += parseInt(cpf.charAt(i)) * (11 - i)
  resto = (soma * 10) % 11
  if (resto === 10 || resto === 11) resto = 0
  if (resto !== parseInt(cpf.charAt(10))) return false

  return true
}

// Verifica se o CPF já existe via API
const verificarCPFJaExiste = async (cpf) => {
  const cpfNumeros = cpf.replace(/\D/g, '')
  try {
    const response = await fetch(`http://localhost:8080/api/auth/verificar-cpf/${cpfNumeros}`)
    if (!response.ok) return false
    const data = await response.json()
    return data.existe
  } catch (error) {
    console.error('Erro ao verificar CPF:', error)
    return false
  }
}

// Etapas de validação
const validarEtapa1 = () => {
  erros.nome = form.nome.trim() === ''
  erros.sobrenome = form.sobrenome.trim() === ''
  return !erros.nome && !erros.sobrenome
}

const validarEtapa2 = () => {
  // --- CPF ---
  const cpfLimpo = form.cpf.replace(/\D/g, "")
  erros.cpfVazio = !cpfLimpo
  erros.cpfInvalido = cpfLimpo.length !== 11 || !validarCPF(cpfLimpo)

  // --- Telefone ---
  const telLimpo = form.telefone.replace(/\D/g, "")
  erros.telefoneVazio = !telLimpo
  // aceitando 10 dígitos (fixo) ou 11 dígitos (celular)
  erros.telefoneInvalido = !(telLimpo.length === 10 || telLimpo.length === 11)

  return !erros.cpfVazio &&
         !erros.cpfInvalido &&
         !erros.telefoneVazio &&
         !erros.telefoneInvalido
}

const validarEtapa3 = () => {
  erros.ehPessoaJuridicaVazio = typeof form.ehPessoaJuridica !== 'boolean'
  erros.cnpjInvalido = false
  erros.nomeEmpresaInvalido = false

  if (form.ehPessoaJuridica === true) {
    erros.cnpjInvalido = !/^\d{14}$/.test(form.cnpj.replace(/\D/g, ''))
    erros.nomeEmpresaInvalido = form.nomeEmpresa.trim() === ''

  }

  return !(
    erros.ehPessoaJuridicaVazio ||
    (form.ehPessoaJuridica === true && (erros.cnpjInvalido || erros.nomeEmpresaInvalido))
  )
}

const validarEtapa4 = () => {
  const dia = +form.dia
  const mes = +form.mes
  const ano = +form.ano

  erros.diaInvalido = !(dia >= 1 && dia <= 31)
  erros.mesInvalido = !(mes >= 1 && mes <= 12)
  erros.anoInvalido = !(ano >= 1900 && ano <= 2025)
  erros.nascimentoIncompleto = erros.diaInvalido || erros.mesInvalido || erros.anoInvalido
  erros.idadeInvalida = false

  if (!erros.nascimentoIncompleto) {
    erros.idadeInvalida = !temMaisDe18Anos(dia, mes, ano)
  }

  erros.generoInvalido = form.genero === ''
  return !(erros.nascimentoIncompleto || erros.idadeInvalida || erros.generoInvalido)
}

function temMaisDe18Anos(dia, mes, ano) {
  const hoje = new Date()
  let idade = hoje.getFullYear() - ano
  if ((hoje.getMonth() + 1 < mes) || (hoje.getMonth() + 1 === mes && hoje.getDate() < dia)) {
    idade--
  }
  return idade >= 18
}

const validarEtapa5 = () => {
  erros.cepInvalido = !form.cep.match(/^\d{5}-?\d{3}$/)
  erros.ruaInvalida = form.rua.trim() === ''
  erros.numeroInvalido = form.numero.trim() === ''
  erros.bairroInvalido = form.bairro.trim() === ''
  erros.cidadeInvalida = form.cidade.trim() === ''
  erros.estadoInvalido = !form.estado.match(/^[A-Za-z]{2}$/)

  erros.enderecoIncompleto =
    erros.cepInvalido || erros.ruaInvalida || erros.numeroInvalido ||
    erros.bairroInvalido || erros.cidadeInvalida || erros.estadoInvalido

  return !erros.enderecoIncompleto
}

const validarEtapa6 = () => {
  erros.emailVazio = form.email.trim() === ''
  erros.emailInvalido = !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)
  erros.senhaVazia = form.senha.trim() === ''
  erros.senhaInvalida = !/(?=.*[A-Z])(?=.*\d)/.test(form.senha)
  erros.confirmaSenhaVazia = form.confirmaSenha.trim() === ''
  erros.senhasDiferentes = form.senha !== form.confirmaSenha

  return !(
    erros.emailVazio || erros.emailInvalido ||
    erros.senhaVazia || erros.senhaInvalida ||
    erros.confirmaSenhaVazia || erros.senhasDiferentes
  )
}

// Busca automática do endereço via CEP
const buscarEndereco = async () => {
  const cepLimpo = form.cep.replace(/\D/g, '')
  const cepValido = /^\d{8}$/.test(cepLimpo)
  erros.cepInvalido = !cepValido

  if (!cepValido) {
    form.rua = form.bairro = form.cidade = form.estado = ''
    return
  }

  try {
    const resposta = await fetch(`https://viacep.com.br/ws/${cepLimpo}/json/`)
    const dados = await resposta.json()

    if (dados.erro) {
      erros.cepInvalido = true
      form.rua = form.bairro = form.cidade = form.estado = ''
    } else {
      erros.cepInvalido = false
      form.rua = dados.logradouro || ''
      form.bairro = dados.bairro || ''
      form.cidade = dados.localidade || ''
      form.estado = dados.uf || ''
    }
  } catch (error) {
    erros.cepInvalido = true
    form.rua = form.bairro = form.cidade = form.estado = ''
    console.error('Erro ao buscar CEP:', error)
  }
}

// Avança para próxima etapa
const proximaEtapa = () => {
  const etapa = etapaAtual.value
  tentativas[`etapa${etapa}`] = true

  const validadores = [
    validarEtapa1,
    validarEtapa2,
    validarEtapa3,
    validarEtapa4,
    validarEtapa5,
    validarEtapa6,
  ]

  if (validadores[etapa - 1]()) {
    if (etapa === 6) {
      enviarCadastro()
      alert('Cadastro concluído com sucesso!')
    } else {
      etapaAtual.value++
    }
    tentativas[`etapa${etapa}`] = false
  } else {
    console.log(`Erro etapa ${etapa}:`, JSON.stringify(erros))
  }
}

// Envio final para backend
const enviarCadastro = async () => {
  try {
    const ehPJ = form.ehPessoaJuridica === true;


    const user = {
      nome: form.nome.trim(),
      sobrenome: form.sobrenome.trim(),
      apelido: form.apelido.trim(),
      telefone: form.telefone.replace(/\D/g, ''),
      pj: form.ehPessoaJuridica,
      cpf: form.cpf.replace(/\D/g, ''),
      diaNascimento: Number(form.dia),
      mesNascimento: Number(form.mes),
      anoNascimento: Number(form.ano),
      genero: form.genero,
      cep: form.cep.replace(/\D/g, ''),
      rua: form.rua,
      bairro: form.bairro,
      cidade: form.cidade,
      estado: form.estado,
      numeroResidencia: form.numero,
      complemento: form.complemento,
      email: form.email.trim(),
      senha: form.senha
    };

    const empresa = ehPJ
  ? {
      cnpj: form.cnpj.replace(/\D/g, ''),
      nome: form.nomeEmpresa.trim()
    }
  : null;
    const dadosParaEnviar = { user, empresa };

    const response = await fetch('http://localhost:8080/api/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(dadosParaEnviar)
    })
    const contentType = response.headers.get('content-type')
    let data

    if (contentType && contentType.includes('application/json')) {
      try {
        data = await response.json()
      } catch {
        data = { message: 'Resposta JSON inválida' }
      }
    } else {
      const text = await response.text()
      data = text.trim().length === 0
        ? { message: 'Resposta vazia do servidor' }
        : { message: text }
    }

    if (!response.ok) {
      alert('Erro ao cadastrar: ' + (data.message || 'Erro desconhecido'))
      return
    }

    router.push('/login')
  } catch (error) {
    alert('Erro inesperado: ' + error.message)
  }
}

// Formatação visual de campos
const mostrarSenha = ref(false)

const formatarSomenteNumeros = (campo, maxLength) => {
  let valor = form[campo]
  valor = valor.replace(/\D/g, '')
  if (valor.length > maxLength) valor = valor.slice(0, maxLength)
  form[campo] = valor
}

const formatarCPF = () => {
  let valor = form.cpf || ''
  valor = valor.replace(/\D/g, '')
  if (valor.length > 3) valor = valor.replace(/^(\d{3})(\d)/, '$1.$2')
  if (valor.length > 6) valor = valor.replace(/^(\d{3})\.(\d{3})(\d)/, '$1.$2.$3')
  if (valor.length > 9) valor = valor.replace(/^(\d{3})\.(\d{3})\.(\d{3})(\d)/, '$1.$2.$3-$4')
  form.cpf = valor.slice(0, 14)
}

const formatarCNPJ = () => {
  let valor = form.cnpj || ''
  valor = valor.replace(/\D/g, '')
  if (valor.length > 2) valor = valor.replace(/^(\d{2})(\d)/, '$1.$2')
  if (valor.length > 5) valor = valor.replace(/^(\d{2})\.(\d{3})(\d)/, '$1.$2.$3')
  if (valor.length > 8) valor = valor.replace(/^(\d{2})\.(\d{3})\.(\d{3})(\d)/, '$1.$2.$3/$4')
  if (valor.length > 12) valor = valor.replace(/^(\d{2})\.(\d{3})\.(\d{3})\/(\d{4})(\d)/, '$1.$2.$3/$4-$5')
  form.cnpj = valor.slice(0, 18)
}
</script>

<style scoped>

.msg-digite-telefone {
  margin-top: -3.5rem !important; /* margem menor */
}
.msg-digite-telefone {
  position: relative;
  top: 30px; /* desce só o texto */
  text-align: left !important;
}

.radio-group {
  display: flex;
  flex-direction: column; /* coloca em coluna (um em cima do outro) */
  align-items: flex-start;
  gap: 0.75rem;           /* espaçamento entre os dois */
  margin-bottom: 1.5rem;
}
* {
  font-family: Arial, sans-serif !important;
}
.radio-option {
  display: flex;
  align-items: center;
  cursor: pointer;
}
.radio-option input[type="radio"] {
  appearance: none;       /* esconde o padrão */
  -webkit-appearance: none;
  width: 15px;
  height: 15px;
  border: 2px solid #ffffff; /* borda cinza clara */
  border-radius: 4px;     /* leve arredondado */
  cursor: pointer;
  position: relative;
  margin: 0;
}

/* Quadrado preenchido branco ao marcar */
.radio-option input[type="radio"]:checked {
  background-color: white;

}

/* Quadradinho escuro dentro quando marcado */


/* Texto afastado */

/* Estilo básico do “radio” que será quadrado */

/* Mantém o estilo padrão do navegador (não usa appearance:none) */

.radio-option label {
  color: #ffffff; /* texto vermelho */
  cursor: pointer;
  user-select: none;
  font-size: 1rem;
  margin-left: 0.6rem; /* ajuste conforme quiser */
}

/* Quadradinho interno para indicar seleção */





.msg-digite-cpf {
  text-align: left !important;
  margin-left: 0 !important;
  padding-left: 0 !important;
}

.etapa-tipo-pessoa .input-group {
  display: flex;
  flex-direction: column; /* empilha verticalmente input e mensagem */
  align-items: flex-start; /* alinhamento à esquerda */
  margin-bottom: 1rem; /* espaçamento abaixo do grupo */
}

.etapa-tipo-pessoa .mensagem-erro,
.etapa-tipo-pessoa .erro-campo,
.etapa-tipo-pessoa .erro-tipo,
.etapa-tipo-pessoa .mensagem-erro-etapa2 {
margin-bottom: 0px;
margin-top: 0px;
}

.etapa-tipo-pessoa input[type="text"] {
  width: 350px; /* ou 100%, ou o que desejar */
  max-width: 100%;
  box-sizing: border-box;
}

/* ---------- BOTÃO SUBMIT E VOLTAR ---------- */
section.botoes {
  display: flex;
  justify-content: flex-end; /* Alinha à direita */
  margin-top: 16px;
  gap: 12px; /* Espaço entre os botões */
}

section.botoes button[type="submit"],
section.botoes button[type="button"] {
  background-color: transparent;
  border-radius: 14px;
  color: rgb(255, 255, 255);
  font-weight: 600;
  font-size: 14px;
  line-height: 16px;
  padding: 10px 24px;
  border: none;
  cursor: pointer;
  box-shadow: 0 1px 1px rgb(0 0 0 / 0.1);
  transition: background-color 0.15s ease-in-out;
  user-select: none;
  border: solid 1px #4e00e0;
}

section.botoes button[type="submit"]:hover,
section.botoes button[type="button"]:hover {
  background-color: #0c0c0c27;
}

button[type="button"] {
  margin-right: 1rem;
  background-color: #f0f0f0;
  color: #333;
  border: 1px solid #ccc;
  padding: 0.5rem 1.2rem;
  border-radius: 6px;
  cursor: pointer;
}
button[type="button"]:hover {
  background-color: #e0e0e0;
}
section.botoes button[type="submit"],
section.botoes button[type="button"] {
  padding: 0px 35px; /* Aumenta a largura do botão */
  /* restante do código */
}

.erro-campo {
  display: flex;
  align-items: center;
  color: #1eff00;
  font-weight: 500;
  font-size: 0.7rem;
  text-align: left;
  margin-top: 0.5rem;
  gap: 0.5rem;
  width: 100%;
  justify-content: flex-start;
}

.icone-erro {
  background-color: #7bff00;
  color: white;
  font-weight: bold;
  border-radius: 50%;
  width: 15px;
  height: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
  line-height: 1;
  margin-top: -2px;
}

.text-branco {
  color: white !important;
  margin-bottom: 0rem;
  margin-top: 2rem;
  text-align: right;  /* aqui o alinhamento */
}

.etapa-tipo-pessoa input {
  background-color: transparent !important;
  box-shadow: none !important;
  color: #ffffff;
}

.etapa-tipo-pessoa {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  margin-top: 1.5rem;
}

.titulo-etapa2 {
  font-size: 1.3rem;
  font-weight: 500;
  color: #ffffff;
  text-align: center;
  margin-bottom: 20px;
}

.grupo-radio {
  display: flex;
  justify-content: center;
  gap: 2rem;
}

.radio-item {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  color: #fff;
  font-weight: 500;
}


.erro-tipo {
  display: flex;
  align-items: center;
  color: #d93025;
  font-weight: 500;
  font-size: 0.8rem;
  text-align: left;
  margin-top: 0.5rem;
  gap: 0.5rem;
}

.icone-erro {
  background-color: #d93025;
  color: white;
  font-weight: bold;
  border-radius: 50%;
  width: 15px;
  height: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
  line-height: 1; /* garante centralização vertical da exclamação */
  margin-top: -2px;
}

.grupo-input-cpf,
.grupo-input-cnpj {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.3rem;
}

.grupo-input-cpf input,
.grupo-input-cnpj input {
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  width: 280px;
}

.input-erro2 {
  border-color: red;
}

/* Espaçamento extra somente nas mensagens de erro da Etapa 2 */

.mensagem-erro-etapa2 + .mensagem-erro-etapa2 {
  margin-top: 8px;
}
.mensagens-erros-etapa2 {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: -32px;
  margin-bottom: 16px;
}

.input-group {
  display: flex;
  align-items: center;
  gap: 6px; /* espaçamento pequeno entre checkbox e texto */
  margin-top: -12px; /* se quiser tirar o espaço, ajuste aqui */
}

.input-group input[type="checkbox"] {
  width: 12px;
  height: 12px;
  cursor: pointer;
  margin: 0;
  padding: 0;
  border: 2px solid white;
  border-radius: 2px;
  background-color: transparent;
  position: relative;
  appearance: none;
  -webkit-appearance: none;
}

.input-group input[type="checkbox"]:checked {
  background-color: #ffffff;
  border-color: #ffffff;
}

.input-group input[type="checkbox"]:checked::after {
  content: '';
  position: absolute;
  top: 0;      /* Ajuste vertical */
  left: 2px;     /* Ajuste horizontal */
  width: 4px;
  height: 7px;
  border: solid black;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
  transform-origin: center;
}


.input-group label[for="mostrarSenha"] {
  color: white;
  font-size: 14px;
  cursor: pointer;
  user-select: none;
  margin-left: 5px;
  margin-top: -2px;
}


/* ===== AJUSTES ESPECÍFICOS DA ETAPA 3 ===== */
.endereco {
  display: flex;
  flex-direction: column;
  gap:14px; /* reduz o espaçamento entre os campos */
  margin-bottom: 8px;
}

.endereco .input-group {
  margin-bottom: 8px; /* diminui o espaço entre os inputs */
}

.endereco .botoes {
  margin-top: 12px; /* sobe o botão */
}

.etapa-titulo {
  font-size: 14px;
  font-weight: 500;
  color: #ffffff;
  margin-bottom: 16px;
  display: block;
  text-align: left;
}

.input-erro-etapa4 {
  border-color: #d93025 !important;
  box-shadow: none !important;
}

.mensagem-erro-etapa4 {
  font-size: 12px;
  color: #d93025;
  font-family: Roboto, Arial, sans-serif;
  user-select: text;
  display: block;
  margin-top: 0;

  transform: translateY(-25px); /* sobe 12px visualmente */
}


.mensagem-erro-etapa4::before {
  content: '!';
  background-color: #d93025;
  color: white;
  border-radius: 50%;
  font-weight: 700;
  width: 15px;
  height: 15px;
  display: inline-flex;
  justify-content: center;
  align-items: center;
  font-size: 10px;
  line-height: 18px;
  margin-right: 6px;
  vertical-align: middle;

}


.mensagem-erro-etapa3 {
  font-size: 12px;
  color: #d93025;
  display: flex;
  align-items: center;
  gap: 6px;
  font-family: Roboto, Arial, sans-serif;
  user-select: text;
  margin-top: -10px;

}

.mensagem-erro-etapa3::before {
  content: '!';
  background-color: #d93025;
  color: white;
  border-radius: 50%;
  font-weight: 700;
  width: 15px;
  height: 15px;
  display: inline-flex;
  justify-content: center;
  align-items: center;
  font-size: 10px;
  line-height: 18px;
  margin-top: -5px;
}
.input-group input.input-erro-etapa3 {
  border: 2px solid #d93025 !important;
  box-shadow: none !important;
  outline: none !important;
  background-color: transparent !important; /* impede qualquer preenchimento branco */
}
/* ---------- REGISTRO CONTAINER ---------- */
.registro-multi {
  max-width: 900px;
  margin: 6rem auto 2rem;
  padding: 48px;
  background-color: #222222;
  border-radius: 16px;
  box-shadow: 0 0 24px rgb(0 0 0 / 0.2);
  font-family: 'Google Sans', Roboto, Arial, sans-serif;
  color: #202124;
  user-select: none;
  height: auto;
  position: relative;
  margin-top: 100px;
  display: flex;
  gap: 40px;
  align-items: center;
  max-height: 600px;
}

/* ---------- IMAGEM ---------- */
.imagem-container {
  flex: 1;
}
.imagem-container img {
  width: 80%;
  height: auto;
  border-radius: 12px;
  object-fit: cover;
  user-select: none;
  pointer-events: none;
  margin-top: -10px;
  margin-left: 30px;
}

/* ---------- FORMULÁRIO ---------- */
.form-container {
  flex: 1;
  display: flex;
  flex-direction: column;
}

h1 {
  font-weight: 400;
  font-size: 24px;
  line-height: 32px;
  margin-bottom: 32px;
  letter-spacing: 0.3px;
}

form {
  display: grid;
  grid-template-columns: 1fr;
  gap: 16px 12px;
}

/* ---------- INPUT FLOATING LABEL ---------- */
.input-group {
  position: relative;
  margin-bottom: 32px;
}

.input-group input {
  width: 100%;
  padding: 24px 12px 8px;
  font-size: 16px;
  border: 1px solid #dadce0;
  border-radius: 4px;
  outline-offset: -2px;
  background-color: transparent;
  color: white;
  transition: border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
}

.input-group input:focus {
  border-color: #ffffff;
}

.input-group input.input-erro:focus {
  outline: none !important;
  box-shadow: none !important;
  border-color: #d93025 !important;
}

.input-group label {
  position: absolute;
  top: 16px;
  left: 12px;
  font-size: 14px;
  color: #6b6b6b;
  pointer-events: none;

  padding: 0 6px;
  transition: 0.2s ease all;
}

.input-group input:focus + label,
.input-group input:not(:placeholder-shown) + label {
  top: 0;
  font-size: 12px;
  color: #ffffff;
  font-weight: 600;
  box-shadow: -6px 0 0 #222222, 6px 0 0 #222222;
}
.input-group input:focus + label,
.input-group input:not(:placeholder-shown) + label {
  top: 0;
  font-size: 12px;
  color: #ffffff;
  font-weight: 600;
  background-color: #222222;  /* COR do fundo da tela */
  padding: 0 6px;             /* espaço lateral para o fundo não cortar o texto */
}
/* ---------- ERRO INPUT ETAPA 1 ---------- */
.mensagem-erro {
  font-size: 12px;
  color: #d93025;
  display: flex;
  align-items: center;
  gap: 6px;
  font-family: Roboto, Arial, sans-serif;
  user-select: text;
  margin-top: -50px;
}

.mensagem-erro-etapa2 {
  min-height: 20px; /* ajuste conforme altura real das mensagens */
  display: block;
}
.mensagem-erro::before {
  content: '!';
  background-color: #d93025;
  color: white;
  border-radius: 50%;
  font-weight: 700;
  width: 15px;
  height: 15px;
  display: inline-flex;
  justify-content: center;
  align-items: center;
  font-size: 10px;
  line-height: 18px;
  margin-top: -5px;
}

/* ---------- ERRO INPUT ETAPA 2 ---------- */
.mensagem-erro-etapa2 {
  font-size: 12px;
  color: #d93025;
  display: block;
  margin-top: 8px;
  font-family: Roboto, Arial, sans-serif;
  text-align: left;
  user-select: text;
  margin-top: -40px;
}
.mensagem-erro-etapa2::before {
  content: '!';
  background-color: #d93025;
  color: white;
  border-radius: 50%;
  font-weight: 700;
  width: 15px;
  height: 15px;
  display: inline-flex;
  justify-content: center;
  align-items: center;
  font-size: 10px;
  line-height: 18px;
  margin-right: 6px; /* espaçamento do ícone para o texto */
  vertical-align: middle;
}
/* ---------- SELECTS ETAPA 2 ---------- */
.data-nascimento {
  display: flex;
  flex-direction: column;
  gap: 16px;
  width: 100%;
}

.data-nascimento-selects {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  width: 100%;
}

.data-nascimento-selects .input-group {
  flex: 1 1 30%;
  min-width: 100px;
}

/* SELECT PADRÃO */
.input-group select {
  width: 100%;
  padding: 12px 14px;
  font-size: 16px;
  border: 1px solid #dadce0;
  border-radius: 6px;
  background-color: transparent !important;
  color: #ffffff !important; /* letras pretas quando não focado */
  appearance: none;
  box-sizing: border-box;
  font-family: 'Google Sans', Roboto, Arial, sans-serif;
  background-image:
    linear-gradient(45deg, transparent 50%, #5f6368 50%),
    linear-gradient(135deg, #5f6368 50%, transparent 50%);
  background-position:
    calc(100% - 20px) 50%,
    calc(100% - 15px) 50%;
  background-size: 5px 5px;
  background-repeat: no-repeat;
  cursor: pointer;
  outline-offset: -2px;
  transition: border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out, background-color 0.15s ease-in-out, color 0.15s ease-in-out;
}

/* Quando o select estiver focado */

.input-group select:focus {
  outline: none;
  background-color: #ffffff !important;
  color: #000000 !important;
  border-color: #000000;

}

.input-group select {
  color: transparent; /* esconde o texto visível */
  text-shadow: 0 0 0 black; /* faz o texto parecer preto dentro do select */
}



/* ---------- LABELS ESPECÍFICOS ---------- */
section.data-nascimento .input-group label {
  display: none;
}
form .input-group label {
  display: block;
}

/* ---------- CAMPO DE GÊNERO ---------- */
.genero select {
  width: 100%;
  padding: 12px 14px;
  font-size: 16px;
  border: 1px solid #dadce0;
  border-radius: 6px;
  background-color: white;
  color: #202124;
  appearance: none;
}

/* ---------- TEXTO INFORMATIVO ---------- */
.texto-explicativo {
  font-size: 14px;
  color: #1a73e8;
  font-family: Roboto, Arial, sans-serif;
  margin-top: -4px;
  cursor: pointer;
}

/* ---------- BOTÃO SUBMIT ---------- */
section.botoes {
  display: flex;
  justify-content: flex-end; /* Alinha à direita */
  margin-top: 16px;
}

section.botoes button[type="submit"] {
  background-color: #4e00e0;
  border-radius: 14px;
  color: rgb(255, 255, 255);
  font-weight: 600;
  font-size: 14px;
  line-height: 16px;
  padding: 10px 24px;
  border: none;
  cursor: pointer;
  box-shadow: 0 1px 1px rgb(0 0 0 / 0.1);
  transition: background-color 0.15s ease-in-out;
  user-select: none;
}

section.botoes button[type="submit"]:hover {
  background-color: #5900ff;
}


/* ---------- BORDAS VERMELHAS EM INPUTS COM ERRO ---------- */
.input-group input.input-erro {
  border-color: #d93025 !important;
  box-shadow: none !important;
}

/* ---------- BORDAS VERMELHAS EM SELECTS COM ERRO ---------- */
.input-group select.input-erro {
  border-color: #d93025 !important;
  box-shadow: none !important;
}

/* ---------- Seta vermelha nos selects com erro ---------- */
.input-group select.input-erro {
  background-image:
    linear-gradient(45deg, transparent 50%, #d93025 50%),
    linear-gradient(135deg, #d93025 50%, transparent 50%);
}

/* ---------- AUTOFILL ---------- */
input:-webkit-autofill,
input:-webkit-autofill:hover,
input:-webkit-autofill:focus,
input:-webkit-autofill:active {
  transition: background-color 5000s ease-in-out 0s;
  -webkit-text-fill-color: #ffffff !important;
  box-shadow: 0 0 0 1000px #222222 inset !important;
  caret-color: white;
}

/* ---------- RESPONSIVO ---------- */
@media (max-width: 768px) {
  .registro-multi {
    flex-direction: column;
    padding: 32px 24px;
    max-width: 100%;
    height: auto;
  }

  .imagem-container {
    display: none;
  }

  .data-nascimento-selects .input-group {
    flex: 1 1 100%;
  }
}
</style>
