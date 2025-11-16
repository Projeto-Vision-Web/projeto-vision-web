<template lang="pug">
  // INÍCIO DA MUDANÇA: Wrapper de Centralização
  .login-page-wrapper 
    // Fundo full-screen
    .fundo-completo

    // Conteúdo do login
    section.registro-multi
      // Botão de voltar no canto superior esquerdo (somente seta)
      button.btn-voltar(@click="router.push('/')")
        svg(width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round")
          polyline(points="15 18 9 12 15 6")

      .imagem-container
        img(
          src="@/assets/logocjl.png",
          alt="Logo CJL",
          loading="lazy"
        )

      .form-container
        small.etapa-titulo Faça login na sua conta

        form(@submit.prevent="logarUsuario")
          .input-group
            input(
              type="email",
              v-model="email",
              :class="{ 'input-erro': erroEmail }",
              placeholder=" ",
              id="email"
            )
            label(for="email") Email

          span.mensagem-erro(v-if="erroEmail") O email é obrigatório.

          .input-group
            input(
              :type="mostrarSenha ? 'text' : 'password'",
              v-model="senha",
              :class="{ 'input-erro': erroSenha }",
              placeholder=" ",
              id="senha"
            )
            label(for="senha") Senha

          span.mensagem-erro(v-if="erroSenha") A senha é obrigatória.

          .input-group
            input(type="checkbox", id="mostrarSenha", v-model="mostrarSenha")
            label(for="mostrarSenha") Mostrar senha

          section.botoes(style="display: flex; align-items: center; gap: 1rem; width: 100%;")
            button(type="submit") Entrar
            span.mensagem-erro(v-if="mensagemErro")= mensagemErro

    // Modal Esqueci a senha
    .modal-esqueci(v-if="mostrarModalEsqueci")
      .modal-conteudo
        button.close(@click="fecharModalEsqueci") ×
        .icone-lock
          .circulo-icone
            svg(width="48" height="48" fill="none" stroke="white" stroke-width="2" viewBox="0 0 24 24" stroke-linecap="round" stroke-linejoin="round")
              rect(x="3" y="11" width="18" height="11" rx="2" ry="2")
              path(d="M7 11V7a5 5 0 0 1 10 0v4")

        p.titulo Problemas ao iniciar sessão?
        p.descricao Insira o seu CPF ou E-mail.
        input.input-recuperar(type="text", placeholder="CPF ou E-mail", v-model="emailRecuperar")
        button.btn-enviar(@click="enviarLigacao") Enviar
        hr
  // FIM DA MUDANÇA: Wrapper de Centralização
</template>



<script setup>
import { ref } from 'vue'

import { useRouter } from 'vue-router'

const router = useRouter()
const mensagemErro = ref('')
const email = ref('')
const senha = ref('')
const mostrarSenha = ref(false)

const erroEmail = ref(false)
const erroSenha = ref(false)

const mostrarModalEsqueci = ref(false)
const emailRecuperar = ref('')

const logarUsuario = async () => {
  erroEmail.value = email.value.trim() === ''
  erroSenha.value = senha.value.trim() === ''
  mensagemErro.value = ''

  if (erroEmail.value || erroSenha.value) return

  try {
    localStorage.removeItem('token')

    // Lembrete: Esta é uma simulação.
    // A equipe de backend substituirá esta chamada 'login()'
    // const response = await login(email.value, senha.value)
    
    // Simulação de uma resposta de sucesso para teste
    const response = { token: 'simulated-token-12345' }


    if (!response || !response.token) {
      mensagemErro.value = 'E-mail ou senha incorretos.'
      return
    }

    localStorage.setItem('token', response.token)
    window.dispatchEvent(new Event('atualizarUsuario'))
    
    router.push('/form-admin') // Redirecionamento corrigido

  } catch (error) {
    // Tratamento de erro (se a chamada real falhar)
    // if (error.response?.status === 401) {
    //   mensagemErro.value = 'E-mail ou senha incorretos.'
    // } else {
    //   mensagemErro.value = 'Erro no login. Tente novamente mais tarde.'
    // }
    console.error("Erro na simulação de login:", error)
    mensagemErro.value = 'Erro na simulação de login.'
  }
}

function abrirModalEsqueci() {
  mostrarModalEsqueci.value = true
}

function fecharModalEsqueci() {
  mostrarModalEsqueci.value = false
  emailRecuperar.value = ''
}

function enviarLigacao() {
  // Aqui você pode implementar a lógica para enviar o e-mail/telefone para recuperação.
  alert(`Enviado link de recuperação para: ${emailRecuperar.value}`)
  fecharModalEsqueci()
}
</script>
<style>
/* INÍCIO DA MUDANÇA: Estilos de Centralização */
.login-page-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100vh; /* Ocupa a altura inteira da tela */
}
/* FIM DA MUDANÇA */

.btn-voltar {
  position: absolute;
  top: 16px;
  left: 16px;
  background: transparent;   
  border: none;              
  cursor: pointer;
  display: flex;
  align-items: center;
  z-index: 3;
  padding: 0;

  box-shadow: none !important;
  -webkit-box-shadow: none !important;
  -moz-box-shadow: none !important;
  outline: none !important;
  -webkit-appearance: none !important;
  -moz-appearance: none !important;
  appearance: none !important;
  -webkit-tap-highlight-color: transparent; 
}

.btn-voltar:focus {
  outline: none !important;
  box-shadow: none !important;
}

.btn-voltar:active {
  outline: none !important;
  box-shadow: none !important;
}

.btn-voltar:hover {
  box-shadow: none !important;
}

.btn-voltar:focus-visible {
  outline: none !important;
  box-shadow: none !important;
}

.btn-voltar svg {
  stroke: white;
  width: 16px;
  height: 16px;
  transition: stroke 0.2s ease;
}

.btn-voltar:hover svg {
  stroke: #a8a8a8;
}

.btn-voltar::-moz-focus-inner {
  border: 0;
}



@media (max-width: 768px) {
  .registro-multi {
    flex-direction: column;
    padding: 32px 24px;
    max-width: 100%;
    height: auto;
    min-height: 430px;
    margin-top: 50px !important; 
  }

  .imagem-container {
    display: none;
  }
}

.fundo-completo {
  position: fixed;        
  top: 0;
  left: 0;
  width: 100vw;           
  height: 100vh;          
  background-color: rgba(0, 0, 0, 0.7); 
  z-index: 1;           
}

.circulo-icone {
  border: 2px solid white;     
  border-radius: 50%;          
  padding: 1rem;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 80px;
  height: 80px;
  margin: 0 auto 1rem auto;
  background-color: transparent; 
}

.circulo-icone svg {
  stroke: white;
  width: 48px;
  height: 48px;
}

/* Estilos do modal */
.modal-esqueci {
  position: fixed;
  inset: 0;
  background: rgba(36, 36, 36, 0.849);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  color: white;
  font-family: Arial, sans-serif;
}

.modal-conteudo {
  background: #000;
  padding: 2rem 3rem;
  width: 400px;
  border-radius: 6px;
  position: relative;
  text-align: center;
  box-shadow: 0 0 15px rgba(20,20,20,0.9);
}

button.close {
  position: absolute;
  top: 10px;
  right: 15px;
  background: none;
  border: none;
  font-size: 1.8rem;
  color: #ccc;
  cursor: pointer;
}

.icone-lock {
  margin-bottom: 1rem;
}

.titulo {
  font-weight: 700;
  font-size: 1.1rem;
  margin-bottom: 0.3rem;
}

.descricao {
  font-size: 0.85rem;
  margin-bottom: 1rem;
  color: #ddd;
  line-height: 1.3;
}

.input-recuperar {
  width: 100%;
  padding: 0.65rem 1rem;
  border-radius: 6px;
  border: none;
  background: #222;
  color: #ccc;
  margin-bottom: 1rem;
  font-size: 0.9rem;
}

.input-recuperar::placeholder {
  color: #666;
}

.btn-enviar {
  width: 100%;
  background-color: #4e00e0; /* azul forte */
  border: none;
  padding: 0.7rem;
  color: rgb(0, 0, 0);
  font-weight: 700;
  border-radius: 6px;
  cursor: pointer;
  margin-bottom: 0.8rem;
  transition: background-color 0.3s ease;
}

.btn-enviar:hover {
  background-color: #ffffff;
}
* {
  font-family: Arial, sans-serif !important;
}
.link-aux {
  font-size: 0.8rem;
  color: #1a56db;
  cursor: pointer;
  text-decoration: underline;
  display: inline-block;
  margin-bottom: 1rem;
}

hr {
  border: 0;
  height: 1px;
  background: #222;
  margin: 1rem 0;
}

.ou {
  font-size: 0.75rem;
  font-weight: 600;
  margin-bottom: 1rem;
}

.criar-conta {
  font-weight: 700;
  margin-bottom: 1rem;
}


a.esqueci-senha {
  color: rgb(218, 218, 218);
  text-decoration: none;
  transition: color 0.3s ease;
  text-shadow: none !important;
  outline: none !important;
  box-shadow: none !important;
  border: none !important;
  background: none !important;
}

a.esqueci-senha:hover,
a.esqueci-senha:focus {
  color: #a8a8a8;
  cursor: pointer;
  text-shadow: none !important;
  outline: none !important;
  box-shadow: none !important;
  border: none !important;
  background: none !important;
}


input:-webkit-autofill,
input:-webkit-autofill:hover,
input:-webkit-autofill:focus,
input:-webkit-autofill:active {
  -webkit-box-shadow: 0 0 0px 1000px #222222 inset !important;
  box-shadow: 0 0 0px 1000px #222222 inset !important;
  -webkit-text-fill-color: white !important;
  caret-color: white !important;
  transition: background-color 9999s ease-in-out 0s;
}
</style>

<style scoped>
/* IDÊNTICO AO REGISTRO */
body, * {
  font-family: 'SuaFonteEscolhida', sans-serif !important;
}

.registro-multi {
  max-width: 900px;
  margin: 0 auto 2rem; /* Removido margin-top */
  padding: 48px;
  background-color: #222222;
  border-radius: 16px;
  box-shadow: 0 0 24px rgb(0 0 0 / 0.2);
  font-family: 'Google Sans', Roboto, Arial, sans-serif;
  color: #202124;
  user-select: none;
  height: auto;
  position: relative;
  display: flex;
  gap: 40px;
  align-items: center;
  max-height: 600px;
  position: relative; 
  z-index: 2; 
}

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

.form-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding-top: 20px;
}

.etapa-titulo {
  font-size: 14px;
  font-weight: 500;
  color: #ffffff;
  margin-bottom: 2px;
  display: block;
  text-align: left;
}

form {
  display: grid;
  grid-template-columns: 1fr;
  gap: 16px 12px;
}

.input-group {
  position: relative;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 6px;
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
  margin-bottom: 12px;
  margin-top: -17px;


}

.input-group input[type="checkbox"]:checked {
  background-color: #ffffff;
  border-color: #ffffff;
}

.input-group input[type="checkbox"]:checked::after {
  content: '';
  position: absolute;
  top: 0;
  left: 2px;
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
  margin-top: -17px;
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
  background-color: #222222;
  padding: 0 6px;
  box-shadow: -6px 0 0 #222222, 6px 0 0 #222222;
}

.mensagem-erro {
  font-size: 12px;
  color: #d93025;
  display: flex;
  align-items: center;
  gap: 6px;
  font-family: Roboto, Arial, sans-serif;
  user-select: text;
  margin-top: -30px;
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

section.botoes {
  display: flex;
  justify-content: flex-end;
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

.input-group input.input-erro {
  border-color: #d93025 !important;
  box-shadow: none !important;
}

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
}
</style>