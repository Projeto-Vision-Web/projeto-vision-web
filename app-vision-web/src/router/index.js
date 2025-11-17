import { createRouter, createWebHistory } from 'vue-router'

import Home from '../views/Home.vue'
import HelloWorldPage from '../views/HelloWorldPage.vue'
import Recursos from '../views/Recursos.vue'
import Solucoes from '../views/Solucoes.vue'
import Login from '../views/Login.vue'
import RegistreSe from '../views/Registrese.vue'
import Marketing from '../views/Marketing.vue'
import PerfilUsuario from '../views/PerfilUsuario.vue' // ✅ Importação adicionada
import FormAdmin from '../views/FormAdmin.vue'
import Relatorio from '../views/Relatorio.vue'
import Medalhas from '../views/Medalhas.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/home',
    name: 'HomePage',
    component: Home
  },
  {
    path: '/hello',
    name: 'HelloWorld',
    component: HelloWorldPage
  },
  {
    path: '/recursos',
    name: 'Recursos',
    component: Recursos
  },
  {
    path: '/solucoes',
    name: 'Solucoes',
    component: Solucoes
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/registre-se',
    name: 'RegistreSe',
    component: RegistreSe
  },
  {
    path: '/marketing',
    name: 'Marketing',
    component: Marketing
  },
  {
    path: '/perfil',
    name: 'Perfil',
    component: PerfilUsuario // ✅ Nova rota para a página de perfil
  },
  {
    path: '/form-admin',
    name: 'Admin-Colaborador',
    component: FormAdmin
  },
  {
    path: '/relatorio',
    name: 'Relatorio',
    component: Relatorio
  },
  {
    path: '/medalhas',
    name: 'Medalhas',
    component: Medalhas
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
